package com.plaglefleau.clashofclansmanage.utils

import com.plaglefleau.clashofclansmanage.api.ClashOfClansApiAdapter
import com.plaglefleau.clashofclansmanage.database.AccountManager
import com.plaglefleau.clashofclansmanage.database.models.Rang

object ClashUser {
    suspend fun updateClashData() {
        val clanMembers = ClashOfClansApiAdapter.apiClient.getClanMembers("#2g8lplur8").items
        clanMembers.forEach { member ->
            AccountManager().updateClashAccount(member.tag, member.name, stringToRank(member.role))
        }

        val playerTags = clanMembers.map { it.tag }
        val clashUsers = AccountManager().getClashUsers()

        clashUsers.filterNot { it.idCompte in playerTags.toSet() }.forEach {
            AccountManager().updateClashAccount(it.idCompte, it.pseudo, Rang.NOT_MEMBER)
        }
    }

    private fun stringToRank(role: String): Rang {
        return when (role.uppercase()) {
            "ADMIN" -> Rang.ADMIN
            "COLEADER" -> Rang.COLEADER
            "MEMBER" -> Rang.MEMBER
            "LEADER" -> Rang.LEADER
            else -> Rang.NOT_MEMBER
        }
    }
}