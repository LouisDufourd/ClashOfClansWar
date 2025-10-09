package com.plaglefleau.clashofclansmanage.utils

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role

object DiscordPermission {
    /**
     * Checks if a given member has at least one of the required roles in the specified guild.
     *
     * @param member The member whose roles are to be checked.
     * @param guild The guild to which the member and roles belong.
     * @param requiredRoles The roles that the member needs to have at least one of.
     * @return True if the member has at least one of the required roles, false otherwise.
     */
    fun hasPermission(member: Member, vararg requiredRoles: Role): Boolean {
        if (member.user.isBot) return false
        if (member.roles.isEmpty()) return false

        for (role in requiredRoles) {
            if (member.roles.contains(role)) return true
        }

        return false;
    }

    /**
     * Checks whether a given member has at least one of the required roles.
     *
     * @param member The Member whose roles are to be checked.
     * @param requiredRoles A list of roles that are required to grant permission.
     * @return Returns true if the member has any of the required roles; otherwise, false.
     */
    fun hasPermission(member: Member, requiredRoles: List<Role>): Boolean {
        if (member.user.isBot) return false
        if (member.roles.isEmpty()) return false

        for (role in requiredRoles) {
            if (member.roles.contains(role)) return true
        }

        return false;
    }

    fun getRolesByNames(guild: Guild, vararg roleNames: String): List<Role> {
        val roles = listOf<Role>()
        roleNames.forEach { roleName ->
            if (roleName.isBlank()) return emptyList()
            val role = guild.getRolesByName(roleName, true).firstOrNull()

            if(role != null) roles.plus(role)
        }
        return roles
    }
}