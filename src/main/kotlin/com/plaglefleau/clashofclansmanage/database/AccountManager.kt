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
        val preparedStatement = connector.getPreparedStatement("SELECT * FROM compteclash WHERE tag = ?")
        preparedStatement.setString(1, playerTag)
        val resultSet = preparedStatement.executeQuery()
        if(resultSet.next()) {
            val compteClash = CompteClash(resultSet.getString("pk_iaccount_id"), resultSet.getString("tag"))
            connector.disconnect()
            return compteClash
        }
        else {
            throw Exception("No account found for tag $playerTag")
        }
    }

    fun createClashAccount(playerTag: String, pseudo: String, rang: Rang, discordAccount: String? = null) {
        if(discordAccount != null && !doesAccountExist(discordAccount)) {
            createDiscordAccount(discordAccount)
        }

        val preparedStatement = connector.getPreparedStatement("insert into compte_clash (pk_account_id, pseudo, rang, fk_discord_account) values (?, ?, ?, ?, ?)")
        preparedStatement.setString(1, playerTag)
        preparedStatement.setString(2, pseudo)
        preparedStatement.setString(3, rang.name)
        preparedStatement.setString(4, discordAccount)
        preparedStatement.executeUpdate()
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

    /**
     * Links a Discord account to a Clash of Clans account in the database.
     *
     * @param discordAccount The identifier of the Discord accounts to be linked.
     * @param playerTag The tag of the Clash of Clans player whose account will be linked.
     */
    fun linkAccount(discordAccount: String, playerTag: String) {
        val preparedStatement = connector.getPreparedStatement("update compteclash set fk_discord_account = ? where pk_account_id = ?")
        preparedStatement.setString(1, discordAccount)
        preparedStatement.setString(2, playerTag)
        preparedStatement.executeUpdate()
        connector.disconnect()
    }
}
