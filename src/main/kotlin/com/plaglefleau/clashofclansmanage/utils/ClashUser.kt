package com.plaglefleau.clashofclansmanage.utils

import com.google.gson.Gson
import com.plaglefleau.clashofclansmanage.Credential
import com.plaglefleau.clashofclansmanage.api.ClashOfClansApiAdapter
import com.plaglefleau.clashofclansmanage.api.model.ClanWar
import com.plaglefleau.clashofclansmanage.api.model.ClientError
import com.plaglefleau.clashofclansmanage.api.model.WarClan
import com.plaglefleau.clashofclansmanage.database.AccountManager
import com.plaglefleau.clashofclansmanage.database.WarManager
import com.plaglefleau.clashofclansmanage.database.models.Rang
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.Calendar
import java.util.GregorianCalendar

object ClashUser {

    private val logger = LoggerFactory.getLogger(ClashUser::class.java)

    private val FLEXIBLE_OFFSET: DateTimeFormatter = DateTimeFormatterBuilder()
        .appendPattern("yyyyMMdd'T'HHmmss")
        .optionalStart()
        .appendLiteral('.')
        .appendFraction(ChronoField.MILLI_OF_SECOND, 1, 9, false) // .SSS ... .nanosec
        .optionalEnd()
        // Try a region ID like [Europe/Paris] OR an offset like Z / +02:00 / +0200 / +02
        .optionalStart().appendZoneOrOffsetId().optionalEnd() // [Europe/Paris] or Z or +HH:MM
        .optionalStart().appendOffset("+HH:MM", "Z").optionalEnd()     // +02:00    or Z
        .optionalStart().appendOffset("+HHMM",  "Z").optionalEnd()     // +0200     or Z
        .optionalStart().appendOffset("+HH",    "Z").optionalEnd()     // +02       or Z
        .toFormatter()

    suspend fun updateClashData(event: Pair<IReplyCallback, Boolean>? = null) {
        val warManager = WarManager()
        val apiClient = ClashOfClansApiAdapter.apiClient
        val response = apiClient.getClanMembers(Credential.CLAN_TAG)

        if(!response.isSuccessful) {
            val error = Gson().fromJson(response.errorBody()?.string(), ClientError::class.java)
            if(error == null) {
                sendError(event, "Erreur: ${response.message()}")
            }
        }

        val clanMembers = response.body()!!.items

        clanMembers.forEach { member ->
            AccountManager().updateClashAccount(member.tag, member.name, stringToRank(member.role))
        }

        val playerTags = clanMembers.map { it.tag }
        val clashUsers = AccountManager().getClashUsers()

        clashUsers.filterNot { it.idCompte in playerTags.toSet() }.forEach {
            AccountManager().updateClashAccount(it.idCompte, it.pseudo, Rang.NOT_MEMBER)
        }

        val warResponse = apiClient.getClanCurrentWar(Credential.CLAN_TAG)

        if(!warResponse.isSuccessful) {
            val error = Gson().fromJson(warResponse.errorBody()?.string(), ClientError::class.java)
            error?.let {
                sendError(event, "Error: ${it.message}")
            }
        }

        val war = warResponse.body()!!

        val startTime = toCalendar(war.preparationStartTime)
        val endTime = toCalendar(war.endTime)

        var warId = warManager.getWarIdByDate(startTime)

        if(warId == null) {
            warId = warManager.getNextWarId(startTime)
        }

        if(warId == -1) {
            warId = warManager.getNextVal()
        }

        warManager.updateWar(warId, startTime, endTime, war.clan.stars, war.opponent.stars)
    }

    private fun updateWarParticipant(war: ClanWar, warId: Int, warManager: WarManager) {
        if(war.clan.members.isEmpty()) {
            logger.info("No clan members found for warId: ${warId}")
            return
        }

        war.clan.members.forEach { member ->
            warManager.updateWarStat(warId, member.tag, member.attacks?.size ?: 0)
        }
    }

    private fun stringToRank(role: String): Rang {
        return when (role.uppercase()) {
            "ADMIN" -> Rang.ADMIN
            "COLEADER" -> Rang.COLEADER
            "MEMBER" -> Rang.MEMBER
            "LEADER" -> Rang.LEADER
            else -> Rang.NOT_MEMBER
        }
    }

    private fun toCalendar(ts: String, zoneId: String? = null): Calendar {
        // Parse into the most specific temporal we can
        val temporal = FLEXIBLE_OFFSET.parseBest(
            ts,
            ZonedDateTime::from,
            OffsetDateTime::from,
            Instant::from
        )

        val zdt = when (temporal) {
            is ZonedDateTime -> temporal
            is OffsetDateTime -> temporal.toZonedDateTime()
            is Instant -> (zoneId?.let(ZoneId::of) ?: ZoneOffset.UTC).let { ZonedDateTime.ofInstant(temporal, it) }
            else -> throw IllegalArgumentException("Unrecognized datetime: $ts")
        }

        val finalZdt = zoneId?.let { zdt.withZoneSameInstant(ZoneId.of(it)) } ?: zdt
        return GregorianCalendar.from(finalZdt)
    }

    private fun sendError(event: Pair<IReplyCallback, Boolean>?, message: String) {
        logger.error("Error: ${message}")
        event?.let {
            if(it.second)
                it.first.hook.sendMessage(message).setEphemeral(true).queue()
            else
                it.first.reply(message).setEphemeral(true).queue()
        }
    }
}