package com.plaglefleau.clashofclansmanage.database

import com.plaglefleau.clashofclansmanage.database.models.CompteClash
import com.plaglefleau.clashofclansmanage.database.models.CompteDiscord
import com.plaglefleau.clashofclansmanage.database.models.Guerre
import com.plaglefleau.clashofclansmanage.database.models.InscriptionGuerre
import com.plaglefleau.clashofclansmanage.database.models.Rang
import com.plaglefleau.clashofclansmanage.database.models.StatDeGuerre

class WarManager {
    private val connector = Connector()

    /**
     * Registers a player for the next war, creating a new war if none exists.
     *
     * @param playerTag The tag of the player to be registered.
     * @param doesJoin Indicates whether the player is opting to participate in the next war. Defaults to true.
     */
    fun joinNextWar(playerTag: String, doesJoin: Boolean = true) {
        var nextWarId = getNextWarId()

        if(nextWarId == -1) {
            createNewWar()
            nextWarId = getNextWarId()
        }

        val preparedStatement = connector.getPreparedStatement("insert into inscription_guerre (pk_iguerre_id, pk_iaccount_id, participation) values (?, ?, ?)")
        preparedStatement.setInt(1, nextWarId)
        preparedStatement.setString(2, playerTag)
        preparedStatement.setBoolean(3, doesJoin)

        preparedStatement.executeUpdate()

        connector.disconnect()
    }

    /**
     * Retrieves the list of player inscriptions for a specific war.
     * Each inscription includes the associated war, the player's account information, and their participation status.
     *
     * @param warId The unique ID of the war for which the inscriptions are to be fetched.
     * @return A list of `InscriptionGuerre` containing the war details, player accounts, and participation status.
     */
    fun getInscriptionsForWar(warId: Int): List<InscriptionGuerre> {
        val statement = connector
            .getPreparedStatement(
                sql="select c.*, ? as guerre_id, case when ig.pk_iguerre_id = ? then ig.participation else false end as participation from compte_clash c " +
                    "left join inscription_guerre ig on ig.pk_iaccount_id = c.pk_account_id " +
                    "left join guerre g on g.pk_guerre_id = ig.pk_iguerre_id;"
            )
        statement.setInt(1, warId)
        statement.setInt(2, warId)
        val resultSet = statement.executeQuery()
        val inscriptions = mutableListOf<InscriptionGuerre>()
        while (resultSet.next()) {
            inscriptions.add(
                InscriptionGuerre(
                    guerre = getWar(warId),
                    compte = CompteClash(
                        idCompte = resultSet.getString("pk_iaccount_id"),
                        pseudo = resultSet.getString("pseudo"),
                        rang = Rang.valueOf(resultSet.getString("rang")),
                        compteDiscord = resultSet.getString("compte_discord")?.let {
                            CompteDiscord(it)
                        }
                    ),
                    participation = resultSet.getBoolean("participation")
                )
            )
        }

        connector.disconnect()
        return inscriptions.toList()
    }

    /**
     * Retrieves the ID of the next war that has not yet started.
     * If no such war exists, returns -1.
     *
     * @return The ID of the next war or -1 if no war is available.
     */
    fun getNextWarId(): Int {
        val statement = connector.getStatement()
        val resultSet = statement
            .executeQuery("select pk_guerre_id from guerre where debut_guerre is null")

        if (!resultSet.next()) {
            return -1
        }

        val id = resultSet.getInt(1)

        connector.disconnect()

        return id
    }

    /**
     * Retrieves a war by its unique ID.
     *
     * @param warId The unique identifier of the war to be retrieved.
     * @return The `Guerre` object representing the war with its associated details.
     * @throws Exception If no war is found for the given ID.
     */
    fun getWar(warId: Int): Guerre {
        val statement = connector.getPreparedStatement("select * from guerre where pk_guerre_id = ?")
        statement.setInt(1, warId)
        val resultSet = statement.executeQuery()
        if(resultSet.next()) {
            val guerre = Guerre(
                idGuerre = resultSet.getInt("pk_guerre_id"),
                nombreEtoileClan = resultSet.getInt("nombre_etoile_clan"),
                nombreEtoileOppose = resultSet.getInt("nombre_etoile_oppose"),
                statDeGuerre = getWarStat(warId)
            )
            connector.disconnect()
            return guerre
        }
        else {
            connector.disconnect()
            throw Exception("No war found for id $warId")
        }
    }

    /**
     * Retrieves the statistics associated with a specific war, such as player accounts and their attack counts.
     *
     * @param warId The unique ID of the war for which the statistics are to be fetched.
     * @return A list of `StatDeGuerre`, each containing player account details and their number of attacks.
     */
    fun getWarStat(warId: Int): List<StatDeGuerre> {
        val statement = connector.getPreparedStatement("select * from stat_de_guerre where pk_iguerre_id = ?")
        statement.setInt(1, warId)
        val resultSet = statement.executeQuery()
        val stats = mutableListOf<StatDeGuerre>()
        while (resultSet.next()) {
            stats.add(
                StatDeGuerre(
                    compteClash = AccountManager().getClashAccount(resultSet.getString("pk_iaccount_id")),
                    nbAttaques = resultSet.getInt("nb_attaques")
                )
            )
        }

        connector.disconnect()
        return stats.toList()
    }

    /**
     * Retrieves the war statistics for a specific player in a given war.
     *
     * @param warId The unique identifier of the war to get statistics for.
     * @param playerTag The unique tag of the player whose statistics are being retrieved.
     * @return A `StatDeGuerre` object containing the player's account and the number of attacks performed in the specified war.
     * @throws Exception If no statistics are found for the given war and player tag.
     */
    fun getPlayerWarStat(warId: Int, playerTag: String) : StatDeGuerre {
        val statement = connector.getPreparedStatement("select * from stat_de_guerre where pk_iguerre_id = ? and pk_iaccount_id = ?")
        statement.setInt(1, warId)
        statement.setString(2, playerTag)
        val resultSet = statement.executeQuery()
        if(resultSet.next()) {
            val stat = StatDeGuerre(
                compteClash = AccountManager().getClashAccount(resultSet.getString("pk_iaccount_id")),
                nbAttaques = resultSet.getInt("nb_attaques")
            )
            connector.disconnect()
            return stat
        }
        else {
            connector.disconnect()
            throw Exception("No stat found for war $warId and player $playerTag")
        }
    }

    /**
     * Retrieves a list of players participating in a specific war who have made fewer than two attacks.
     *
     * @param warId The unique identifier of the war for which the player data is being retrieved.
     * @return A list of `CompteClash` objects representing players with fewer than two attacks in the specified war.
     */
    fun getPlayerWithLessThanTwoAttack(warId: Int): List<CompteClash> {
        val statement = connector.getPreparedStatement("select * from stat_de_guerre where pk_iguerre_id = ? and nb_attaques < 2")
        statement.setInt(1, warId)
        val resultSet = statement.executeQuery()
        val players = mutableListOf<CompteClash>()
        while (resultSet.next()) {
            players.add(AccountManager().getClashAccount(resultSet.getString("pk_iaccount_id")))
        }

        connector.disconnect()
        return players.toList()
    }

    /**
     * Creates a new war entry in the database.
     *
     * This method inserts a default record into the `guerre` table, effectively
     * creating a new war instance. It ensures the database connection is handled
     * correctly by obtaining a statement for execution and disconnecting the
     * connector once the operation is complete.
     *
     * Note: No parameters are required for creating a new war record, as it relies
     * on default database values for initialization.
     *
     * Throws:
     * - `SQLException` if there is an issue executing the update.
     */
    fun createNewWar() {
        val statement = connector.getStatement()
        statement.executeUpdate("insert into guerre default values")

        connector.disconnect()
    }
}