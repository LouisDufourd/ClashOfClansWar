package com.plaglefleau.clashofclansmanage.discord.commands

import com.plaglefleau.clashofclansmanage.discord.commands.execution.*
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class CommandListener: ListenerAdapter() {
    /**
     * Handles slash command interactions by dispatching the execution to specific command implementations
     * based on the command name provided in the interaction event. Replies with an error message if the
     * command is not recognized.
     *
     * @param event The event triggered by the slash command interaction. Contains information about the
     *              command being executed and the user interaction context, such as the user and guild details.
     */
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        when (event.name) {
            "ping" -> PingCommand().execute(event)
            "link" -> LinkAccountCommand().execute(event)
            "show_next_war" -> ShowNextWarEvent().execute(event)
            "plan_next_war" -> PlanNextWarCommand().execute(event)
            "assign_role" -> AssignRoleCommand().execute(event)
            "rule" -> RuleCommand().execute(event)
            "punishment" -> PunishmentCommand().execute(event)
            else -> event.reply("Error: Command not found").setEphemeral(true).queue()
        }
    }
}