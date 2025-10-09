package com.plaglefleau.clashofclansmanage.discord.commands.execution

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

interface DiscordCommand {
    fun execute(event: SlashCommandInteractionEvent)
}