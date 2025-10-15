package com.plaglefleau.clashofclansmanage.discord.commands.execution

import com.plaglefleau.clashofclansmanage.database.WarManager
import com.plaglefleau.clashofclansmanage.utils.DiscordEventReply
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.buttons.ButtonStyle
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.text.SimpleDateFormat
import java.util.Calendar

class ShowNextWarEvent: DiscordCommand {
    override fun execute(event: SlashCommandInteractionEvent) {
        event.deferReply(true)
            .queue()

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val manager = WarManager()
        val war = manager.getWar(manager.getNextWarId())
        val date = war.dateDebut

        if(date.time < Calendar.getInstance().time) {
            DiscordEventReply.hookEphemeralMessage(event, "Il n'y a aucune guerre de prévu actuellement")
            return
        }

        event.hook.sendMessage("La prochaine guerre se feras le ${dateFormat.format(date.time)}")
            .setComponents(
                ActionRow.of(
                    Button.of(ButtonStyle.SUCCESS, "join-war", "S'inscrire"),
                    Button.of(ButtonStyle.DANGER, "leave-war", "Se désinscrire")
                )
            )
            .queue()
    }
}