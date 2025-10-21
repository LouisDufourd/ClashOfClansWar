package com.plaglefleau.clashofclansmanage.api.model.league

import com.plaglefleau.clashofclansmanage.api.model.other.IconUrlsX

data class League(
    val iconUrls: IconUrlsX,
    val id: Int,
    val name: String
)