package com.plaglefleau.clashofclansmanage.api.model

data class WarClan(
    val attacks: Int,
    val badgeUrls: BadgeUrls,
    val clanLevel: Int,
    val destructionPercentage: Double,
    val expEarned: Int,
    val members: List<ClanWarMember>,
    val name: String,
    val stars: Int,
    val tag: String
)