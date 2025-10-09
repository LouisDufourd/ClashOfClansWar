package com.plaglefleau.clashofclansmanage.api.model

data class ClanMember(
    val builderBaseLeague: BuilderBaseLeague,
    val builderBaseTrophies: Int,
    val clanRank: Int,
    val donations: Int,
    val donationsReceived: Int,
    val expLevel: Int,
    val league: League,
    val name: String,
    val playerHouse: PlayerHouse,
    val previousClanRank: Int,
    val role: String,
    val tag: String,
    val townHallLevel: Int,
    val trophies: Int
)