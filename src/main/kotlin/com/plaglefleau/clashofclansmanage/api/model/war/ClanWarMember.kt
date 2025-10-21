package com.plaglefleau.clashofclansmanage.api.model.war

data class ClanWarMember(
    val tag: String,
    val name: String,
    val townhallLevel: Int,
    val mapPosition: Int,
    val attacks: List<ClanWarAttack>,
    val opponentAttacks: Int,
    val bestOpponentAttack: ClanWarAttack
)