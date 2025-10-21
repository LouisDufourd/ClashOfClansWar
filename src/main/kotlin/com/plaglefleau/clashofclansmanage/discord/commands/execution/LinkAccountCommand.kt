package com.plaglefleau.clashofclansmanage.discord.commands.execution

import com.plaglefleau.clashofclansmanage.api.ClashOfClansApiAdapter
import com.plaglefleau.clashofclansmanage.api.model.other.VerifyTokenRequest
import com.plaglefleau.clashofclansmanage.database.AccountManager
import com.plaglefleau.clashofclansmanage.utils.ClashUser
import com.plaglefleau.clashofclansmanage.utils.DiscordUser
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.sql.SQLException

class LinkAccountCommand: DiscordCommand {
    private val logger = KotlinLogging.logger {}

    override fun execute(event: SlashCommandInteractionEvent) {
        if(event.options.size < 2) {
            event.reply("Veuillez fournir à la fois le tag du joueur et le jeton API").setEphemeral(true).queue()
            return
        }

        val playerTag = event.options[0].asString
        val apiToken = event.options[1].asString

        event
            .deferReply(true)
            .queue()

        CoroutineScope(Dispatchers.IO).launch{
            val result = verifyToken(playerTag, apiToken)

            if (!result) {
                event.hook.sendMessage("Impossible de lié le compte $playerTag (mauvais jeton ou mauvais tag)")
                    .setEphemeral(true)
                    .queue()
                return@launch
            }

            updateDatabase(playerTag, event)

            DiscordUser.assignRole(event.guild!!, event.member!!)
        }
    }

    private suspend fun verifyToken(playerTag: String, apiToken: String): Boolean {
        val verifyTokenResponse = ClashOfClansApiAdapter.apiClient.verifyToken(playerTag, VerifyTokenRequest(apiToken))

        if (verifyTokenResponse.isSuccessful.not()) {
            return false
        }

        return verifyTokenResponse.body()?.status.equals("ok", ignoreCase = true)
    }

    private suspend fun updateDatabase(playerTag: String, event: SlashCommandInteractionEvent) {
        ClashUser.updateClanMembers(event to true)

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
