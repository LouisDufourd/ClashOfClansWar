package com.plaglefleau.clashofclansmanage.api.model

data class ClanMembers(
    val items: List<ClanMember>,
    val paging: Paging
)