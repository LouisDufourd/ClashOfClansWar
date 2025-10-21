package com.plaglefleau.clashofclansmanage.discord.commands.execution

import com.plaglefleau.clashofclansmanage.database.WarManager
import com.plaglefleau.clashofclansmanage.utils.DiscordPermission
import com.plaglefleau.clashofclansmanage.utils.DiscordUser
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class ConfirmPunishment: DiscordCommand {
    override fun execute(event: SlashCommandInteractionEvent) {
        event.deferReply(true).queue()

        if(!hasPermission(event.guild, event.member))
            return event.hook.sendMessage("You don't have the permission to use this command").setEphemeral(true).queue()

        val warManager = WarManager()

        val warId = event.getOption("war_id")?.asInt ?: return event.hook.sendMessage("Id de guerre invalide").setEphemeral(true).queue()

        val war = try {
            warManager.getWar(warId)
        } catch (e: Exception) {
            return event.hook.sendMessage("Error: ${e.message}").setEphemeral(true).queue()
        }

        WarManager().updateWar(
            war.idGuerre,
            war.dateDebut,
            war.dateFin,
            war.nombreEtoileClan,
            war.nombreEtoileOppose,
            true
        )

        event.hook.sendMessage("Confirmed punishment").setEphemeral(true).queue()
    }

    private fun hasPermission(guild: Guild?, member: Member?) : Boolean {
        if(guild == null || member == null) return false

        val roles = DiscordPermission.getRolesByNames(
            guild,
            DiscordUser.COLEADER,
            DiscordUser.LEADER
        )
        return DiscordPermission.hasPermission(member, roles)
    }
}