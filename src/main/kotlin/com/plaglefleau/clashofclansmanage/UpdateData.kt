package com.plaglefleau.clashofclansmanage

import com.plaglefleau.clashofclansmanage.api.ClashOfClansApiAdapter
import com.plaglefleau.clashofclansmanage.database.AccountManager
import com.plaglefleau.clashofclansmanage.utils.ClashUser
import com.plaglefleau.clashofclansmanage.utils.DiscordUser
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDA
import kotlin.reflect.typeOf

class UpdateData {
    val scope = CoroutineScope(Dispatchers.IO)
    var job: Job? = null

    private val logger = KotlinLogging.logger {  }

    fun startUpdates(jda: JDA) {
        stopUpdates()
        job = scope.launch {
            while (true) {
                val guild = jda.getGuildById(Credential.TEST_GUILD_ID)

                if(guild == null) logger.warn { "No guild found for id: ${Credential.TEST_GUILD_ID}" }
                else ClashUser.updateDiscordServer(guild)

                ClashUser.updateClanMembers()
                ClashUser.updateClanWars()

                jda.guilds.forEach { guild ->
                    guild.members.forEach { member ->
                        DiscordUser.assignRole(guild, member)
                    }
                }
                delay(300000)
            }
        }
    }

    fun stopUpdates() {
        job?.cancel()
        job = null
    }
}