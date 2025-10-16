package com.plaglefleau.clashofclansmanage

import com.plaglefleau.clashofclansmanage.discord.buttons.ButtonListener
import com.plaglefleau.clashofclansmanage.discord.commands.CommandListener
import com.plaglefleau.clashofclansmanage.discord.modal.ModalListener
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

class Main {
    companion object {
        private val timer = UpdateData()
        private val logger = LoggerFactory.getLogger(Main::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            val discordBot = DiscordBot.Builder()
                .setupCommands(isDev = true, commands = getCommands())
                .setupListeners(ButtonListener(), CommandListener(), ModalListener())
                .build()

            timer.startUpdates(discordBot.jda)

            logger.info("press enter to stop")
            readln()

            stop()
        }

        fun stop() {
            timer.stopUpdates()
            exitProcess(0)
        }

        fun createCommand(name:String, description: String, vararg options: OptionData) : CommandData{
            return Commands.slash(name, description)
                .addOptions(*options)
        }

        private fun getCommands(): List<CommandData> {
            val playerTag = OptionData(
                OptionType.STRING,
                "player_tag",
                "Le tag du joueur (peut être trouver dans le profil)",
                true
            )

            val apiToken = OptionData(
                OptionType.STRING,
                "api_token",
                "Votre jeton api (peut être trouver dans les paramètre supplémentaires)"
            )

            val startTime = OptionData(
                OptionType.STRING,
                "start_time",
                "La date où la recherche de la prochaine guerre commence (jj/MM/aaaa HH:mm:ss)",
                true
            )

            return listOf(
                createCommand("ping", "Envoie une commande ping"),
                createCommand("link", "Relie un compte discord avec un compte clash of clans", playerTag, apiToken),
                createCommand("show_next_war", "Affiche la prochaine guerre à venir"),
                createCommand("plan_next_war", "Planifie la prochaine guerre", startTime),
                createCommand("assign_role", "Assigne les rôles du clan au comptes discord"),
                createCommand("rule", "Affiche les règles du clans"),
                createCommand("punishment", "Affiche les punitions et récompense du clans à partir de la guerre en cours / précédente")
            )
        }
    }
}