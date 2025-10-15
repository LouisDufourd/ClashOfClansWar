package com.plaglefleau.clashofclansmanage.discord.commands.execution

import com.plaglefleau.clashofclansmanage.api.PastebinApiAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class RuleCommand: DiscordCommand {
    override fun execute(event: SlashCommandInteractionEvent) {
        event.deferReply().setEphemeral(true).queue()

        CoroutineScope(Dispatchers.IO).launch {
            val response = PastebinApiAdapter.apiClient.getPaste("yPVKsd0w")
            if(response.isSuccessful) {
                event.hook.sendMessage(response.body()?: "").setEphemeral(true).queue()
            }
            else {
                event.hook.sendMessage("Error: Impossible d'obtenir les règles depuis pastebin").setEphemeral(true).queue()
            }
        }
    }
}