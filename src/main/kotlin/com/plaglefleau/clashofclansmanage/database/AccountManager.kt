package com.plaglefleau.clashofclansmanage.database

import com.plaglefleau.clashofclansmanage.database.models.CompteClash
import com.plaglefleau.clashofclansmanage.database.models.Rang

class AccountManager {
    private val connector = Connector()

    /**
     * Retrieves a Clash of Clans account from the database using the provided player tag.
     *
     * @param playerTag The tag of the player whose account should be retrieved.
     * @return A CompteClash object containing the account details of the specified player.
     * @throws Exception if no account is found for the provided playerTag.
     */
    fun getClashAccount(playerTag: String) : CompteClash {
        val preparedStatement = connector.getPreparedStatement("SELECT pk_account_id, nom, rang FROM compte_clash WHERE pk_account_id = ?")
        preparedStatement.setString(1, playerTag)
        val resultSet = preparedStatement.executeQuery()
        if(resultSet.next()) {
            val compteClash = CompteClash(resultSet.getString("pk_account_id"), resultSet.getString("nom"), Rang.valueOf(resultSet.getString("rang")))
            connector.disconnect()
            return compteClash
        }
        else {
            throw Exception("No account found for tag $playerTag")
        }
    }

    fun updateClashAccount(playerTag: String, pseudo: String, rang: Rang, discordAccount: String? = null) {
        if(discordAccount != null && !doesAccountExist(discordAccount)) {
            createDiscordAccount(discordAccount)
        }

        val preparedStatement = connector.getPreparedStatement("select updateClashUser(?, ?, ?::erang, ?)")
        preparedStatement.setString(1, playerTag)
        preparedStatement.setString(2, pseudo)
        preparedStatement.setString(3, rang.name)
        preparedStatement.setString(4, discordAccount)
        preparedStatement.execute()
        connector.disconnect()
    }

    private fun doesAccountExist(discordAccount: String): Boolean {
        val preparedStatement = connector.getPreparedStatement("select * from compte_discord where pk_discord_id = ?")
        preparedStatement.setString(1, discordAccount)
        val resultSet = preparedStatement.executeQuery()
        val result = resultSet.next()
        connector.disconnect()
        return result
    }

    fun createDiscordAccount(discordId: String) {
        val preparedStatement = connector.getPreparedStatement("insert into compte_discord (pk_discord_id) values (?)")
        preparedStatement.setString(1, discordId)
        preparedStatement.executeUpdate()
        connector.disconnect()
    }

    fun getClashUsers() : List<CompteClash> {
        val preparedStatement = connector.getPreparedStatement(
            """
                select * from compte_clash c;
            """.trimIndent()
        )
        val resultSet = preparedStatement.executeQuery()
        val clashUsers = mutableListOf<CompteClash>()
        while (resultSet.next()) {
            clashUsers.add(
                CompteClash(
                    idCompte = resultSet.getString("pk_account_id"),
                    pseudo = resultSet.getString("nom")
                )
            )
        }

        connector.disconnect()
        return clashUsers.toList()
    }

    /**
     * Links a Discord account to a Clash of Clans account in the database.
     *
     * @param discordAccount The identifier of the Discord accounts to be linked.
     * @param playerTag The tag of the Clash of Clans player whose account will be linked.
     */
    fun linkAccount(discordAccount: String, playerTag: String) {
        val preparedStatement = connector.getPreparedStatement("select linkAccount(?, ?)")
        preparedStatement.setString(1, playerTag)
        preparedStatement.setString(2, discordAccount)
        preparedStatement.execute()
        connector.disconnect()
    }

    fun updateMemberStatus(playerTag: String, rang: Rang) {
        val preparedStatement = connector.getPreparedStatement("update compteclash set rang = ? where pk_account_id = ?")
        preparedStatement.setString(1, rang.name)
        preparedStatement.setString(2, playerTag)
        preparedStatement.executeUpdate()
    }

    fun getPlayerTags(discordId: String): List<String> {
        val preparedStatement = connector.getPreparedStatement("""
            select c.pk_account_id from utilisateur u 
            join compte_clash c on u.pk_discord_id = c.fk_discord_account 
            where pk_discord_id = ?;
        """.trimIndent())
        preparedStatement.setString(1, discordId)
        val resultSet = preparedStatement.executeQuery()
        val playerTags = mutableListOf<String>()
        while (resultSet.next()) {
            playerTags.add(resultSet.getString("pk_account_id"))
        }
        connector.disconnect()
        return playerTags
    }

    fun getUserHighestRank(discordId: String) : Rang {
        val preparedStatement = connector.getPreparedStatement(
            """
                select distinct rang from compte_clash 
                where fk_discord_account = ?;
            """.trimIndent()
        )

        preparedStatement.setString(1, discordId)

        val resultSet = preparedStatement.executeQuery()

        val rangs = mutableListOf<Rang>()

        while(resultSet.next()) {
            rangs.add(Rang.valueOf(resultSet.getString("rang")))
        }

        return if(rangs.contains(Rang.LEADER))
            Rang.LEADER
        else if (rangs.contains(Rang.COLEADER))
            Rang.COLEADER
        else if (rangs.contains(Rang.ADMIN))
            Rang.ADMIN
        else if (rangs.contains(Rang.MEMBER))
            Rang.MEMBER
        else Rang.NOT_MEMBER
    }
}
