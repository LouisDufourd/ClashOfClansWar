package com.plaglefleau.clashofclansmanage.api.model.war

data class ClanWarAttack(
    val order: Int,
    val attackerTag: String,
    val defenderTag: String,
    val stars: Int,
    val destructionPercentage: Int,
    val duration: Int
)