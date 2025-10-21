package com.plaglefleau.clashofclansmanage.api.model.clan

import com.plaglefleau.clashofclansmanage.api.model.other.BadgeUrls
import com.plaglefleau.clashofclansmanage.api.model.league.CapitalLeague
import com.plaglefleau.clashofclansmanage.api.model.other.Label
import com.plaglefleau.clashofclansmanage.api.model.other.Location
import com.plaglefleau.clashofclansmanage.api.model.league.WarLeague

data class Clan(
    val badgeUrls: BadgeUrls,
    val capitalLeague: CapitalLeague,
    val chatLanguage: ChatLanguage,
    val clanBuilderBasePoints: Int,
    val clanCapital: ClanCapital,
    val clanCapitalPoints: Int,
    val clanLevel: Int,
    val clanPoints: Int,
    val description: String,
    val isFamilyFriendly: Boolean,
    val isWarLogPublic: Boolean,
    val labels: List<Label>,
    val location: Location,
    val memberList: List<ClanMember>,
    val members: Int,
    val name: String,
    val requiredBuilderBaseTrophies: Int,
    val requiredTownhallLevel: Int,
    val requiredTrophies: Int,
    val tag: String,
    val type: String,
    val warFrequency: String,
    val warLeague: WarLeague,
    val warLosses: Int,
    val warTies: Int,
    val warWinStreak: Int,
    val warWins: Int
)