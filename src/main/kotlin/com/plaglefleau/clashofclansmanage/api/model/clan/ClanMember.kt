package com.plaglefleau.clashofclansmanage.api.model.clan

import com.plaglefleau.clashofclansmanage.api.model.league.BuilderBaseLeague
import com.plaglefleau.clashofclansmanage.api.model.league.League
import com.plaglefleau.clashofclansmanage.api.model.league.LeagueTier
import com.plaglefleau.clashofclansmanage.api.model.other.PlayerHouse

data class ClanMember(
    val builderBaseLeague: BuilderBaseLeague,
    val builderBaseTrophies: Int,
    val clanRank: Int,
    val donations: Int,
    val donationsReceived: Int,
    val expLevel: Int,
    val league: League,
    val leagueTier: LeagueTier,
    val name: String,
    val playerHouse: PlayerHouse,
    val previousClanRank: Int,
    val role: String,
    val tag: String,
    val townHallLevel: Int,
    val trophies: Int
)