package com.plaglefleau.clashofclansmanage

import com.plaglefleau.clashofclansmanage.utils.DiscordUser
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import org.slf4j.LoggerFactory

class DiscordBot {
    val jda: JDA;

    private constructor(jda: JDA) {
        this.jda = jda
        this.jda.presence.activity = Activity.playing("I'm Ready")
    }

    class Builder {
        val logger = LoggerFactory.getLogger(Builder::class.java)
        val jda = JDABuilder.createLight(Credential.DISCORD_TOKEN, GatewayIntent.GUILD_MEMBERS)
            .setMemberCachePolicy(MemberCachePolicy.ALL)
            .setChunkingFilter(ChunkingFilter.ALL)
            .build()

        init {
            this.jda.awaitReady()
        }

        fun build(): DiscordBot {
            createRoles()
            return DiscordBot(jda)
        }

        fun setupCommands(isDev: Boolean, vararg commands: CommandData) : Builder {
            if (isDev) {
                val guild = jda.getGuildById(Credential.TEST_GUILD_ID);
                if(guild == null) {
                    logger.error("No guild found for id: ${Credential.TEST_GUILD_ID}")
                    return this
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

            return this
        }

        fun setupCommands(isDev: Boolean, commands: List<CommandData>) : Builder {
            if (isDev) {
                val guild = jda.getGuildById(Credential.TEST_GUILD_ID);
                if(guild == null) {
                    logger.error("No guild found for id: ${Credential.TEST_GUILD_ID}")
                    return this
                }

                guild
                    .updateCommands()
                    .addCommands(commands)
                    .queue()
            } else {
                jda.updateCommands()
                    .addCommands(commands)
                    .queue()
            }

            return this
        }

        fun setupListeners(vararg listeners: ListenerAdapter): Builder {
            jda.addEventListener(
                *listeners
            )

            return this
        }

        private fun createRoles() {
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

                logger.info("${guild.name} : ${guild.members.size} members")
            }
        }
    }
}