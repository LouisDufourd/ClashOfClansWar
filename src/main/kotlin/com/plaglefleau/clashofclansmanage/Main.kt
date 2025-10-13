package com.plaglefleau.clashofclansmanage

import com.google.gson.Gson
import com.plaglefleau.clashofclansmanage.api.ClashOfClansApiAdapter
import com.plaglefleau.clashofclansmanage.api.model.ClanWar
import com.plaglefleau.clashofclansmanage.database.AccountManager
import com.plaglefleau.clashofclansmanage.database.WarManager
import com.plaglefleau.clashofclansmanage.database.models.Rang
import com.plaglefleau.clashofclansmanage.discord.buttons.ButtonListener
import com.plaglefleau.clashofclansmanage.discord.commands.CommandListener
import com.plaglefleau.clashofclansmanage.discord.modal.ModalListener
import com.plaglefleau.clashofclansmanage.utils.ClashUser
import com.plaglefleau.clashofclansmanage.utils.DiscordUser
import jdk.jfr.internal.OldObjectSample.emit
import kotlinx.coroutines.flow.flow
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.jetbrains.annotations.NotNull
import retrofit2.HttpException
import kotlin.system.exitProcess

class Main {
    companion object {
        private val timer = UpdateData()

        @JvmStatic
        fun main(args: Array<String>) {
            val discordBot = DiscordBot.Builder()
                .setupCommands(true, createCommands())
                .setupListeners(ButtonListener(), CommandListener(), ModalListener())
                .build()

            timer.startUpdates(discordBot.jda)

            print("press enter to stop")
            readln()

            stop()
        }

        fun stop() {
            timer.stopUpdates()
            exitProcess(0)
        }

        private fun createCommands(): List<CommandData> {
            return listOf(
                Commands.slash("ping", "Send a ping command"),
                Commands.slash("link", "Relie un compte discord avec un compte clash of clans")
                    .addOptions(
                        OptionData(
                            OptionType.STRING,
                            "player_tag",
                            "Le tag du joueur (peut être trouver dans le profil)",
                            true
                        ),
                        OptionData(
                            OptionType.STRING,
                            "api_token",
                            "Votre jeton api (peut être trouver dans les paramètre supplémentaires)"
                        )
                    ),
                Commands.slash("show_next_war", "Affiche la prochaine guerre à venir"),
                Commands.slash("plan_next_war", "Planifie la prochaine guerre")
                    .addOptions(
                        OptionData(
                            OptionType.STRING,
                            "start_time",
                            "La date où la recherche de la prochaine guerre commence (aaaa-MM-jj hh:mm:ss)",
                            true
                        )
                    ),
                Commands.slash("assign_role", "Assigne les rôles du clan au comptes discord"),
                Commands.slash("stop", "Stoppe le bot")
            )
        }
    }
}