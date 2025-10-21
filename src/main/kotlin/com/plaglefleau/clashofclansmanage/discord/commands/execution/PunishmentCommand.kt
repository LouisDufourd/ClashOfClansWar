package com.plaglefleau.clashofclansmanage.discord.commands.execution

import com.plaglefleau.clashofclansmanage.database.WarManager
import com.plaglefleau.clashofclansmanage.database.models.CompteClash
import com.plaglefleau.clashofclansmanage.database.models.Guerre
import com.plaglefleau.clashofclansmanage.database.models.Rang
import com.plaglefleau.clashofclansmanage.utils.DiscordPermission
import com.plaglefleau.clashofclansmanage.utils.DiscordUser
import io.github.oshai.kotlinlogging.KotlinLogging
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class PunishmentCommand: DiscordCommand {
    private val logger = KotlinLogging.logger {}

    override fun execute(event: SlashCommandInteractionEvent) {
        event.deferReply(true).queue()

        val guild =
            event.guild ?: return event.hook.sendMessage("This command can only be used in a server").setEphemeral(true)
                .queue()

        if(!hasPermission(guild, event.member))
            return event.hook.sendMessage("You don't have the permission to use this command").setEphemeral(true).queue()

        val warManager = WarManager()
        val warId = event.getOption("war_id")?.asInt ?: warManager.getPreviousWarId()

        logger.debug { "War id: $warId" }
        val war = warManager.getWar(warId)

        val punishedPlayers = warManager.getPlayerWithLessThanTwoAttack(warId)
        val rewardedPlayers = warManager.getWarStat(warId).filter { warStat ->
            logger.debug { "\nPseudo: ${warStat.compteClash.pseudo}\nRank: ${warStat.compteClash.rang}" }
            warStat.nbAttaques == 2 && warStat.compteClash.rang == Rang.MEMBER
        }.map { warStat -> warStat.compteClash }

        val message = if (war.consequence) {
            "Aucune punition disponible."
        } else {
            generateMessage(punishedPlayers, rewardedPlayers)
        }

        event.hook.sendMessage(message).setEphemeral(true).queue()
    }

    private fun generateMessage(punishedPlayers: List<CompteClash>, rewardedPlayers: List<CompteClash>): String {
        val message = StringBuilder()

        punishedPlayers.forEach { punishedPlayer ->
            when(punishedPlayer.rang) {
                Rang.NOT_MEMBER -> message.append("Le joueur ${punishedPlayer.pseudo} n'est plus membre du clan.\n")
                Rang.MEMBER -> message.append("Le joueur ${punishedPlayer.pseudo} doit être viré du clan.\n")
                Rang.ADMIN -> message.append("Le joueur ${punishedPlayer.pseudo} doit être rétrograder.\n")
                else -> {}
            }
        }

        rewardedPlayers.forEach { rewardedPlayer ->
            message.append("Le joueur ${rewardedPlayer.pseudo} doit être promu au rang de ainé.\n")
        }

        if(message.isEmpty()) {
            return "Aucune punition disponible."
        }

        return message.toString()
    }

    private fun hasPermission(guild: Guild?, member: Member?) : Boolean {
        if(guild == null || member == null) return false

        val roles = DiscordPermission.getRolesByNames(
            guild,
            DiscordUser.MEMBER,
            DiscordUser.ADMIN,
            DiscordUser.COLEADER,
            DiscordUser.LEADER
        )
        return DiscordPermission.hasPermission(member, roles)
    }
}
