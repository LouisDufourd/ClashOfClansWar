package com.plaglefleau.clashofclansmanage

import com.plaglefleau.clashofclansmanage.api.ClashOfClansApiAdapter
import com.plaglefleau.clashofclansmanage.database.AccountManager
import com.plaglefleau.clashofclansmanage.utils.ClashUser
import com.plaglefleau.clashofclansmanage.utils.DiscordUser
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

    fun startUpdates(jda: JDA) {
        stopUpdates()
        job = scope.launch {
            while (true) {
                ClashUser.updateClashData()
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