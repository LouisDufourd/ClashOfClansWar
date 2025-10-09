package com.plaglefleau.clashofclansmanage.discord.commands.execution

import com.plaglefleau.clashofclansmanage.database.WarManager
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.buttons.ButtonStyle
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class ShowNextWarEvent: DiscordCommand {
    override fun execute(event: SlashCommandInteractionEvent) {
        event.deferReply(true)
            .queue()
        val manager = WarManager()
        val war = manager.getWar(manager.getNextWarId())
        val date = war.dateDebut
        event.hook.sendMessage("La prochaine guerre se feras le ${date.time}")
            .setComponents(
                ActionRow.of(
                    Button.of(ButtonStyle.SUCCESS, "join-war", "S'inscrire")
                )
            )
            .queue()
    }
}