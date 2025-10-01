package com.plaglefleau.clashofclansmanage.api.model

data class ClanWar(
    val attacksPerMember: Int,
    val battleModifier: String,
    val clan: WarClan,
    val endTime: String,
    val opponent: WarClan,
    val preparationStartTime: String,
    val result: String,
    val startTime: String,
    val state: String,
    val teamSize: Int
)