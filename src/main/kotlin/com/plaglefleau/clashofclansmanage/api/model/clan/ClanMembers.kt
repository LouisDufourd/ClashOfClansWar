package com.plaglefleau.clashofclansmanage.api.model.clan

import com.plaglefleau.clashofclansmanage.api.model.other.Paging

data class ClanMembers(
    val items: List<ClanMember>,
    val paging: Paging
)