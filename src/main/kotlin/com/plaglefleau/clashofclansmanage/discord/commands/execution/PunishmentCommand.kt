package com.plaglefleau.clashofclansmanage.discord.commands.execution

import com.plaglefleau.clashofclansmanage.database.WarManager
import com.plaglefleau.clashofclansmanage.database.models.Rang
import com.plaglefleau.clashofclansmanage.utils.DiscordPermission
import com.plaglefleau.clashofclansmanage.utils.DiscordUser
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class PunishmentCommand: DiscordCommand {
    override fun execute(event: SlashCommandInteractionEvent) {
        val guild = event.guild
        if (guild == null) {
            event.reply("This command can only be used in a server").setEphemeral(true).queue()
            return
        }

        val roles = DiscordPermission.getRolesByNames(
            guild,
            DiscordUser.MEMBER,
            DiscordUser.ADMIN,
            DiscordUser.COLEADER,
            DiscordUser.LEADER
        )

        if(DiscordPermission.hasPermission(event.member!!, roles))

        event.deferReply(true).queue()
        val warManager = WarManager()
        val warId = warManager.getPreviousWarId()
        val punishedPlayers = warManager.getPlayerWithLessThanTwoAttack(warId)
        val rewardedPlayers = warManager.getWarStat(warId).filter { warStat ->
            warStat.nbAttaques == 2 && warStat.compteClash.rang == Rang.MEMBER
        }.map { warStat -> warStat.compteClash }
        val message = StringBuilder()

        punishedPlayers.forEach { punishedPlayer ->
            when(punishedPlayer.rang) {
                Rang.NOT_MEMBER -> {
                    message.append("Le joueur ${punishedPlayer.pseudo} n'est plus membre du clan.\n")
                }
                Rang.MEMBER -> {
                    message.append("Le joueur ${punishedPlayer.pseudo} doit être viré du clan.\n")
                }
                Rang.ADMIN -> {
                    message.append("Le joueur ${punishedPlayer.pseudo} doit être rétrograder.\n")
                }
                else -> {}
            }
        }

        rewardedPlayers.forEach { rewardedPlayer ->
            message.append("Le joueur ${rewardedPlayer.pseudo} doit être promu au rang de ainé.\n")
        }

        if(message.isEmpty()) {
            message.append("No punishment found.")
        }

        event.hook.sendMessage(message.toString()).setEphemeral(true).queue()
    }
}
