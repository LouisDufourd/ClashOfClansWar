package com.plaglefleau.clashofclansmanage.discord.commands.execution

import com.plaglefleau.clashofclansmanage.api.ClashOfClansApiAdapter
import com.plaglefleau.clashofclansmanage.api.model.VerifyTokenRequest
import com.plaglefleau.clashofclansmanage.database.AccountManager
import com.plaglefleau.clashofclansmanage.database.WarManager
import com.plaglefleau.clashofclansmanage.database.models.Rang
import com.plaglefleau.clashofclansmanage.utils.ClashUser
import com.plaglefleau.clashofclansmanage.utils.DiscordUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.sql.SQLException

class LinkAccountCommand: DiscordCommand {
    override fun execute(event: SlashCommandInteractionEvent) {
        val playerTag = event.options[0].asString
        val apiToken = event.options[1].asString

        event
            .deferReply(true)
            .queue()

        CoroutineScope(Dispatchers.IO).launch{
            val result = verifyToken(playerTag, apiToken)

            if (!result) {
                event.hook.sendMessage("Impossible de lié le compte $playerTag (Mauvais jeton)")
                    .setEphemeral(true)
                    .queue()
                return@launch
            }

            updateDatabase(playerTag, apiToken, event)

            DiscordUser.assignRole(event.guild!!, event.member!!)
        }
    }

    private suspend fun verifyToken(playerTag: String, apiToken: String): Boolean {
        val verifyTokenResponse = ClashOfClansApiAdapter.apiClient.verifyToken(playerTag, VerifyTokenRequest(apiToken))
        return verifyTokenResponse.status.equals("ok", ignoreCase = true)
    }

    private suspend fun updateDatabase(playerTag: String, apiToken: String, event: SlashCommandInteractionEvent) {
        ClashUser.updateClashData()

        try {
            AccountManager().linkAccount(event.user.id, playerTag)
        } catch (e: SQLException) {
            println(e.message)
            event.hook.sendMessage("Impossible de lié le compte $playerTag à ce compte discord (${e.message})")
                .setEphemeral(true)
                .queue()
            return
        }

        event.hook.sendMessage("Successfully linked account $playerTag")
            .setEphemeral(true)
            .queue()
    }
}
