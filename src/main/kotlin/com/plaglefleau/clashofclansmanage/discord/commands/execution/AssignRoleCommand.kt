package com.plaglefleau.clashofclansmanage.discord.commands.execution

import com.plaglefleau.clashofclansmanage.utils.ClashUser
import com.plaglefleau.clashofclansmanage.utils.DiscordEventReply
import com.plaglefleau.clashofclansmanage.utils.DiscordPermission
import com.plaglefleau.clashofclansmanage.utils.DiscordUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class AssignRoleCommand : DiscordCommand {
    /**
     * Handles the execution of the slash command for assigning roles to members of the Discord guild.
     * This command checks permissions and assigns roles based on predefined criteria.
     *
     * @param event The `SlashCommandInteractionEvent` that triggers the execution of the command.
     * It contains information about the command's context, including the issuing member and guild.
     */
    override fun execute(event: SlashCommandInteractionEvent) {
        event.deferReply(true).queue()
        val guild = event.guild
        val member = event.member

        if (member == null || guild == null) {
            DiscordEventReply.hookEphemeralMessage(event, "Error: Could not find the issuer")
            return
        }

        val roles = DiscordPermission.getRolesByNames(guild, DiscordUser.ADMIN, DiscordUser.COLEADER, DiscordUser.LEADER)

        if(!DiscordPermission.hasPermission(member, roles)) {
            DiscordEventReply.hookEphemeralMessage(event, "Erreur Vous n'avez pas la permission d'utiliser la commande")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            ClashUser.updateClashData()
            guild.members.forEach { DiscordUser.assignRole(guild, it) }
            DiscordEventReply.hookEphemeralMessage(event, "All members have been assigned the role")
        }
    }
}