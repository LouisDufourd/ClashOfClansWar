package com.plaglefleau.clashofclansmanage.api.model.league

import com.plaglefleau.clashofclansmanage.api.model.other.IconUrlsXX

data class LeagueTier(
    val iconUrls: IconUrlsXX,
    val id: Int,
    val name: String
)