package com.plaglefleau.clashofclansmanage.discord.commands.execution

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class PingCommand: DiscordCommand {
    override fun execute(event: SlashCommandInteractionEvent) {
        val startTime = System.currentTimeMillis()
        event.reply("Ping ...").setEphemeral(true).queue {
            val endTime = System.currentTimeMillis()
            it.editOriginalFormat("Pong: %d ms", endTime - startTime).queue()
        }
    }
}