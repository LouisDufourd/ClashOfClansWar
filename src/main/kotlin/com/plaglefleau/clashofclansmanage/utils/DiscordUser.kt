package com.plaglefleau.clashofclansmanage.utils

import com.plaglefleau.clashofclansmanage.database.AccountManager
import com.plaglefleau.clashofclansmanage.database.models.Rang
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role

object DiscordUser {
    const val NOT_MEMBER = "Clan Not Member"
    const val MEMBER = "Clan Member"
    const val ADMIN = "Clan Admin"
    const val COLEADER = "Clan Co-Leader"
    const val LEADER = "Clan Leader"

    fun assignRole(guild: Guild, member: Member) {
        if (member.user.isBot) return

        val rang: Rang = AccountManager().getUserHighestRank(member.id)
        val targetRoleName = rangToString(rang)

        val targetRole = guild.getRolesByName(targetRoleName, true).firstOrNull()
            ?: //warn("Role '$targetRoleName' not found in '${guild.name}'. Create it and try again.")
            return

        if(!checkBotPermission(guild, member, targetRole)) return

        val toRemove = getToRemoveRoles(getClanRoles(guild), targetRole, member)
        val toAdd = getToAddRoles(targetRole, member)

        if (toAdd.isEmpty() && toRemove.isEmpty()) {
            //println("No change for ${member.user.asTag}")
            return
        }

        changeRoles(guild, member, toAdd, toRemove)
    }

    fun getClanRoles(guild: Guild): List<Role> {
        val clanRoleNames = listOf(NOT_MEMBER, MEMBER, ADMIN, COLEADER, LEADER)
        return clanRoleNames.flatMap { guild.getRolesByName(it, true) }.toSet().toList()
    }

    private fun getToAddRoles(targetRole: Role, member: Member) : List<Role> {
        return if (member.roles.none { it.idLong == targetRole.idLong }) listOf(targetRole) else emptyList()
    }

    private fun getToRemoveRoles(clanRoles: List<Role>, targetRole: Role, member: Member) : List<Role> {
        return member.roles.filter { role -> role in clanRoles && role.idLong != targetRole.idLong }
    }

    private fun checkBotPermission(guild: Guild, member: Member, targetRole: Role): Boolean {
        val self = guild.selfMember
        if (!self.hasPermission(Permission.MANAGE_ROLES)) {
            return false // warn("Bot lacks MANAGE_ROLES in '${guild.name}'.")
        }
        val selfTop   = self.roles.maxOfOrNull { it.position } ?: -1
        val targetPos = targetRole.position
        val memberTop = member.roles.maxOfOrNull { it.position } ?: -1
        if (selfTop <= targetPos) return false // warn("Bot top role must be ABOVE '${targetRole.name}' (pos=$targetPos, botTop=$selfTop).")
        if (selfTop <= memberTop) return false // warn("Bot top role must be ABOVE member’s top role (memberTop=$memberTop, botTop=$selfTop).")

        return true
    }

    /*private fun warn(msg: String): Boolean {
        System.err.println("[RoleAssign] $msg")
        return false
    }*/

    private fun changeRoles(guild: Guild, member: Member, toAdd: List<Role>, toRemove: List<Role>) {
        guild.modifyMemberRoles(member, toAdd, toRemove).queue(
            {
                println(
                    """
                    Updated ${member.user.asTag}:
                        +${toAdd.joinToString { it.name }}
                        -${toRemove.joinToString { it.name }}
                    """.trimIndent()
                )
            },
            { err -> System.err.println("Failed to update ${member.user.asTag}: ${err.message}") }
        )
    }

    fun rangToString(rang: Rang): String {
        return when (rang) {
            Rang.MEMBER     -> MEMBER
            Rang.ADMIN      -> ADMIN
            Rang.COLEADER   -> COLEADER
            Rang.LEADER     -> LEADER
            else            -> NOT_MEMBER
        }
    }
}