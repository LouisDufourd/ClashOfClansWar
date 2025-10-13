package com.plaglefleau.clashofclansmanage.utils

import com.plaglefleau.clashofclansmanage.Credential
import com.plaglefleau.clashofclansmanage.api.ClashOfClansApiAdapter
import com.plaglefleau.clashofclansmanage.database.AccountManager
import com.plaglefleau.clashofclansmanage.database.WarManager
import com.plaglefleau.clashofclansmanage.database.models.Rang
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

    private val FLEXIBLE_OFFSET: DateTimeFormatter = DateTimeFormatterBuilder()
        .appendPattern("yyyyMMdd'T'HHmmss")
        .optionalStart()
        .appendLiteral('.')
        .appendFraction(ChronoField.MILLI_OF_SECOND, 1, 9, false) // .SSS ... .nanosec
        .optionalEnd()
        // Try a region ID like [Europe/Paris] OR an offset like Z / +02:00 / +0200 / +02
        .optionalStart().appendZoneOrOffsetId().optionalEnd()          // [Europe/Paris] or Z or +HH:MM
        .optionalStart().appendOffset("+HH:MM", "Z").optionalEnd()     // +02:00 or Z
        .optionalStart().appendOffset("+HHMM",  "Z").optionalEnd()     // +0200  or Z
        .optionalStart().appendOffset("+HH",    "Z").optionalEnd()     // +02    or Z
        .toFormatter()

    suspend fun updateClashData() {
        val warManager = WarManager()
        val apiClient = ClashOfClansApiAdapter.apiClient
        val clanMembers = apiClient.getClanMembers(Credential.CLAN_TAG).items
        clanMembers.forEach { member ->
            AccountManager().updateClashAccount(member.tag, member.name, stringToRank(member.role))
        }

        val playerTags = clanMembers.map { it.tag }
        val clashUsers = AccountManager().getClashUsers()

        clashUsers.filterNot { it.idCompte in playerTags.toSet() }.forEach {
            AccountManager().updateClashAccount(it.idCompte, it.pseudo, Rang.NOT_MEMBER)
        }

        val war = apiClient.getClanCurrentWar(Credential.CLAN_TAG)

        if(war.clan.members.isEmpty()) return

        val calendar = toCalendar(war.startTime)

        var warId = WarManager().getWarIdByDate(calendar)


        if(warId == null) {
            println(warId)
            warId = WarManager().getNextWarId(calendar)
        }

        warManager.updateWar(warId, calendar, war.clan.stars, war.opponent.stars)

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
}