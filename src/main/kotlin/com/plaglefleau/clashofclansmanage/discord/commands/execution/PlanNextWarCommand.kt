package com.plaglefleau.clashofclansmanage.discord.commands.execution

import com.plaglefleau.clashofclansmanage.database.WarManager
import com.plaglefleau.clashofclansmanage.utils.DiscordEventReply
import com.plaglefleau.clashofclansmanage.utils.DiscordPermission
import com.plaglefleau.clashofclansmanage.utils.DiscordUser
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar

class PlanNextWarCommand: DiscordCommand {
    /**
     * Handles the execution of the plan next war command triggered by a slash command interaction event.
     * The command verifies user permissions, validates the provided start time, and schedules the next war.
     *
     * @param event The event triggered by the slash command interaction. This contains the command details
     *              and the interacting user's context, such as member and guild information.
     */
    override fun execute(event: SlashCommandInteractionEvent) {
        event.deferReply(true).queue()

        val member = event.member ?: return DiscordEventReply.hookEphemeralMessage(
            event,
            "Erreur: Impossible de trouver l'utilisateur de la commande"
        )

        val guild = event.guild ?: return DiscordEventReply.hookEphemeralMessage(
            event,
            "Erreur: impossible de trouver le serveur"
        )

        val roles = DiscordPermission.getRolesByNames(guild, DiscordUser.LEADER, DiscordUser.COLEADER)

        if(!DiscordPermission.hasPermission(member, roles))
            return DiscordEventReply.hookEphemeralMessage(event, "Erreur: Tu n'as pas les permissions pour utiliser cette commande")

        val manager = WarManager()
        val nextWar = manager.getWar(manager.getNextWarId())

        if(nextWar.dateDebut.time > Calendar.getInstance().time)
            return DiscordEventReply.hookEphemeralMessage(event, "Erreur la prochaine guerre n'as pas encore commencé")

        val date = event.getOption("start_time")?.asString ?: return DiscordEventReply.hookEphemeralMessage(
            event,
            "Erreur: Veuillez fournir une date au format 'aaaa-MM-jj hh:mm:ss'"
        )

        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        val startTime = Calendar.getInstance()

        try {
            startTime.time = format.parse(date)
        } catch (_: ParseException) {
            return DiscordEventReply.hookEphemeralMessage(event, "Erreur: Vous ne respecter pas le format de la date")
        }

        WarManager().createNewWar(startTime)

        DiscordEventReply.hookEphemeralMessage(event, "Prochaine guerre prévu pour $date")
    }
}
