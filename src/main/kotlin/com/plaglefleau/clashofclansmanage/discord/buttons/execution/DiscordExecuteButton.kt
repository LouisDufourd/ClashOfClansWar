package com.plaglefleau.clashofclansmanage.discord.buttons.execution

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

interface DiscordExecuteButton {
    fun execute(event: ButtonInteractionEvent)
}