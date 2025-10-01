package com.plaglefleau.clashofclansmanage.api

import com.plaglefleau.clashofclansmanage.api.model.ClanWar
import com.plaglefleau.clashofclansmanage.api.model.Token
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ClashOfClansApiClient {
    @GET("clans/{clanTag}/currentwar")
    suspend fun getClanCurrentWar(@Path("clanTag") clanTag: String): ClanWar

    @POST("players/{playerTag}/verifytoken")
    suspend fun verifyToken(@Path("playerTag") playerTag: String, @Body token: Token): String
}