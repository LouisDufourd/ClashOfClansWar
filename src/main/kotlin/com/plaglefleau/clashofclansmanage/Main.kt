package com.plaglefleau.clashofclansmanage

import com.plaglefleau.clashofclansmanage.discord.buttons.ButtonListener
import com.plaglefleau.clashofclansmanage.discord.commands.CommandListener
import com.plaglefleau.clashofclansmanage.discord.modal.ModalListener
import io.github.oshai.kotlinlogging.KotlinLogging
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.slf4j.LoggerFactory
import java.util.concurrent.CountDownLatch
import kotlin.system.exitProcess

class Main {
    companion object {
        const val DEV_MODE = true
        private val timer = UpdateData()
        private val logger = KotlinLogging.logger {  }

        @JvmStatic
        fun main(args: Array<String>) {
            println("println works?")
            logger.info { "kotlin-logging works?" }
            println(">>> SLF4J impl: " + LoggerFactory::class.java.protectionDomain.codeSource.location)
            LoggerFactory.getLogger("probe").info(">>> slf4j info works?")
            val discordBot = DiscordBot.Builder()
                .setupCommands(isDev = DEV_MODE, commands = getCommands())
                .setupListeners(ButtonListener(), CommandListener(), ModalListener())
                .build()

            timer.startUpdates(discordBot.jda)

            val latch = CountDownLatch(1)
            Runtime.getRuntime().addShutdownHook(Thread {
                logger.info { "Shutting down..." }
                stop()
                latch.countDown()
            })

            if (System.console() != null) {
                logger.info { "Press ENTER to stop" }
                runCatching { readln() }.onFailure {
                    logger.warn(it) { "Console read failed; waiting for SIGINT instead." }
                    logger.info { "Press Ctrl+C to stop" }
                    latch.await()
                    return
                }
                stop()
            } else {
                logger.info { "No console detected; press Ctrl+C to stop" }
                latch.await()
            }
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

            val optionalWarId = OptionData(
                OptionType.INTEGER,
                "war_id",
                "L'identifiant de la guerre",
                false
            )

            val requiredWarId = OptionData(
                OptionType.INTEGER,
                "war_id",
                "L'identifiant de la guerre",
                true
            )

            return listOf(
                createCommand("ping", "Envoie une commande ping"),
                createCommand("link", "Relie un compte discord avec un compte clash of clans", playerTag, apiToken),
                createCommand("show_next_war", "Affiche la prochaine guerre à venir"),
                createCommand("plan_next_war", "Planifie la prochaine guerre", startTime),
                createCommand("assign_role", "Assigne les rôles du clan au comptes discord"),
                createCommand("rule", "Affiche les règles du clans"),
                createCommand("consequence", "Affiche les conséquence et récompense du clans à partir de la guerre en cours / précédente", optionalWarId),
                createCommand("confirm-consequence", "Confirme que les conséquence ont été faites", requiredWarId),
            )
        }
    }
}