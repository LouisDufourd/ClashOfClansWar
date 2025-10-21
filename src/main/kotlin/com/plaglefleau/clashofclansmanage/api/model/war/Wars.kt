package com.plaglefleau.clashofclansmanage.api.model.war

import com.plaglefleau.clashofclansmanage.api.model.other.Paging

data class Wars(
    val items: List<ClanWar>,
    val paging: Paging
)