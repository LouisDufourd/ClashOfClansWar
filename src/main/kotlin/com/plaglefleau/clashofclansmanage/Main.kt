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

suspend fun main() {
    val jda = JDABuilder.createLight(Credential.DISCORD_TOKEN,
        GatewayIntent.GUILD_MEMBERS
        )
        .setMemberCachePolicy(MemberCachePolicy.ALL)           // cache all members (optional)
        .setChunkingFilter(ChunkingFilter.ALL)                 // request member chunks at startup (optional)
        .addEventListeners(
            CommandListener(),
            ButtonListener(),
            ModalListener()
        )
        .build()

    jda.awaitReady()

    val pingCommand = Commands.slash("ping", "Send a ping command")
    val linkCommand = Commands.slash("link", "Relie un compte discord avec un compte clash of clans")
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
        )
    val showNextWarCommand = Commands.slash("show_next_war", "Affiche la prochaine guerre à venir")
    val planNextWarCommand = Commands.slash("plan_next_war", "Planifie la prochaine guerre")
        .addOptions(
            OptionData(
                OptionType.STRING,
                "start_time",
                "La date où la recherche de la prochaine guerre commence (aaaa-MM-jj hh:mm:ss)",
                true
            )
        )
    val assignRole = Commands.slash("assign_role", "Assigne les rôles du clan au comptes discord")

    registerCommands(
        jda, true, "470957170721161227",
        pingCommand,
        linkCommand,
        showNextWarCommand,
        planNextWarCommand,
        assignRole
    )

    jda.guilds.forEach { guild ->
        if(guild.getRolesByName(DiscordUser.NOT_MEMBER, true).isEmpty())
            guild.createRole().setName(DiscordUser.NOT_MEMBER).setColor(0x383838).queue()
        if(guild.getRolesByName(DiscordUser.MEMBER, true).isEmpty())
            guild.createRole().setName(DiscordUser.MEMBER).setColor(0xFFFFFF).queue()
        if(guild.getRolesByName(DiscordUser.ADMIN, true).isEmpty())
            guild.createRole().setName(DiscordUser.ADMIN).setColor(0x00b4d8).queue()
        if(guild.getRolesByName(DiscordUser.COLEADER, true).isEmpty())
            guild.createRole().setName(DiscordUser.COLEADER).setColor(0x950606).queue()
        if(guild.getRolesByName(DiscordUser.LEADER, true).isEmpty())
            guild.createRole().setName(DiscordUser.LEADER).setColor(0xD4AF37).queue()

        println("${guild.name} : ${guild.members.size} members")
    }

    jda.presence.activity = Activity.playing("I'm Ready")
}

private fun registerCommands(@NotNull jda: JDA, isDev : Boolean, guildId: String, @NotNull vararg commands: CommandData) {
    if(commands.isEmpty()) return

    if (isDev) {
        val guild = jda.getGuildById(guildId);
        if(guild == null) {
            println("No guild found for id: $guildId")
            return
        }

        guild
            .updateCommands()
            .addCommands(*commands)
            .queue()
    } else {
        jda.updateCommands()
            .addCommands(*commands)
            .queue()
    }
}

private fun setWarStats(json: String) {
    try {
        val war = Gson().fromJson(json, ClanWar::class.java)
        for (member in war.clan.members) {
            try {
                WarManager().updateWarStat(1, member.tag, member.attacks.size)
            }
            catch (e: Exception) {
                println("Error creating account for member: ${member.name}")
                System.err.println(e.message)
                println()
            }
        }
    } catch (e: HttpException) {
        e.response()?.errorBody()?.string()?.let { println(it) }
    }
}