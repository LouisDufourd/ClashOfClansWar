package com.plaglefleau.clashofclansmanage.database

import com.plaglefleau.clashofclansmanage.database.models.CompteClash
import com.plaglefleau.clashofclansmanage.database.models.CompteDiscord
import com.plaglefleau.clashofclansmanage.database.models.Guerre
import com.plaglefleau.clashofclansmanage.database.models.InscriptionGuerre
import com.plaglefleau.clashofclansmanage.database.models.Rang
import com.plaglefleau.clashofclansmanage.database.models.StatDeGuerre
import java.util.Calendar

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
            val calendar = Calendar.getInstance()
            createNewWar(calendar)
            nextWarId = getNextWarId()
        }

        val preparedStatement = connector.getPreparedStatement("update inscription_guerre set participation = ? where pk_iguerre_id = ? and pk_iaccount_id = ?;")
        preparedStatement.setBoolean(1, doesJoin)
        preparedStatement.setInt(2, nextWarId)
        preparedStatement.setString(3, playerTag)

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
                sql="""
                    select c.*, ? as guerre_id, case when ig.pk_iguerre_id = ? then ig.participation else false end as participation 
                    from compte_clash c
                    left join inscription_guerre ig on ig.pk_iaccount_id = c.pk_account_id
                    left join guerre g on g.pk_guerre_id = ig.pk_iguerre_id where g.pk_guerre_id = ? and ig.participation;
                """.trimIndent()
            )
        statement.setInt(1, warId)
        statement.setInt(2, warId)
        statement.setInt(3, warId)
        val resultSet = statement.executeQuery()
        val inscriptions = mutableListOf<InscriptionGuerre>()
        while (resultSet.next()) {
            inscriptions.add(
                InscriptionGuerre(
                    guerre = getWar(warId),
                    compte = CompteClash(
                        idCompte = resultSet.getString("pk_account_id"),
                        pseudo = resultSet.getString("nom"),
                        rang = Rang.valueOf(resultSet.getString("rang")),
                        compteDiscord = resultSet.getString("fk_discord_account")?.let {
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

    fun getPlayerInscriptionForWar(warId: Int, playerTag: String) : InscriptionGuerre? {
        val statement = connector.getPreparedStatement("select * from inscription_guerre where pk_iguerre_id = ? and pk_iaccount_id = ?;")
        statement.setInt(1, warId)
        statement.setString(2, playerTag)
        val resultSet = statement.executeQuery()
        val guerre = getWar(warId)
        val compte = AccountManager().getClashAccount(playerTag)
        if(resultSet.next()) {
            return InscriptionGuerre(guerre, compte, resultSet.getBoolean("participation"))
        }
        return null
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
            .executeQuery("select pk_guerre_id from guerre order by datedebut desc limit 1;")

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
            val start = Calendar.getInstance();
            start.timeInMillis = resultSet.getTimestamp("datedebut").time;
            val guerre = Guerre(
                idGuerre = resultSet.getInt("pk_guerre_id"),
                nombreEtoileClan = resultSet.getInt("nb_etoile_clan"),
                nombreEtoileOppose = resultSet.getInt("nb_etoile_clan_adverse"),
                dateDebut = start,
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
        val statement = connector.getPreparedStatement("select * from stat_de_guerre where pk_sguerre_id = ?")
        statement.setInt(1, warId)
        val resultSet = statement.executeQuery()
        val stats = mutableListOf<StatDeGuerre>()
        while (resultSet.next()) {
            stats.add(
                StatDeGuerre(
                    compteClash = AccountManager().getClashAccount(resultSet.getString("pk_saccount_id")),
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
        val statement = connector.getPreparedStatement(
            """
                select c.* from stat_de_guerre s
                join compte_clash c on s.pk_saccount_id = c.pk_account_id 
                join inscription_guerre i on i.pk_iguerre_id = s.pk_sguerre_id and i.pk_iaccount_id = s.pk_saccount_id
                where pk_sguerre_id = 6 and s.nb_attaque < 2 and i.participation;
            """.trimIndent()
        )
        statement.setInt(1, warId)
        val resultSet = statement.executeQuery()
        val players = mutableListOf<CompteClash>()
        while (resultSet.next()) {
            players.add(
                CompteClash(
                    idCompte = resultSet.getString("pk_account_id"),
                    pseudo = resultSet.getString("nom"),
                    rang = Rang.valueOf(resultSet.getString("rang")),
                    compteDiscord = resultSet.getString("fk_discord_account")?.let {
                        CompteDiscord(it)
                    }
                )
            )
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
    fun createNewWar(startTime : Calendar) {
        val statement = connector.getPreparedStatement("select createWarInscription(?)")
        statement.setTimestamp(1, java.sql.Timestamp(startTime.timeInMillis))
        statement.execute()
        connector.disconnect()
    }

    /**
     * Adds a player's statistic record for a specific war to the database.
     *
     * @param warId The unique identifier of the war for which the statistic is being added.
     * @param playerTag The unique tag of the player whose statistic is being recorded.
     */
    fun addWarStat(warId: Int?, playerTag: String) {
        val preparedStatement = connector.getPreparedStatement("select addWarStat(?::varchar, ?::int2);")
        preparedStatement.setString(1, playerTag)
        if(warId != null) preparedStatement.setInt(2, warId)
        else preparedStatement.setNull(2, java.sql.Types.INTEGER)
        preparedStatement.execute()
        connector.disconnect()
    }

    /**
     * Updates the war statistics for a given player in a specific war by setting the number of attacks.
     *
     * @param warId The unique identifier of the war to be updated.
     * @param playerTag The unique tag of the player whose statistics are being updated.
     * @param nbAttaques The number of attacks performed by the player in the specified war.
     */
    fun updateWarStat(warId: Int, playerTag: String, nbAttaques: Int) {
        val preparedStatement = connector.getPreparedStatement("select updateWarStat(?, ?::int2, ?::int2);")
        preparedStatement.setString(1, playerTag)
        preparedStatement.setInt(2, warId)
        preparedStatement.setInt(3, nbAttaques)
        preparedStatement.execute()
        connector.disconnect()
    }
}