package com.plaglefleau.clashofclansmanage.api

import com.plaglefleau.clashofclansmanage.api.model.ClanMembers
import com.plaglefleau.clashofclansmanage.api.model.ClanWar
import com.plaglefleau.clashofclansmanage.api.model.VerifyTokenRequest
import com.plaglefleau.clashofclansmanage.api.model.VerifyTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ClashOfClansApiClient {
    @GET("clans/{clanTag}/currentwar")
    suspend fun getClanCurrentWar(@Path("clanTag") clanTag: String): Response<ClanWar>

    @GET("clans/{clanTag}/members")
    suspend fun getClanMembers(@Path("clanTag") clanTag: String): Response<ClanMembers>

    @POST("players/{playerTag}/verifytoken")
    suspend fun verifyToken(@Path("playerTag") playerTag: String, @Body token: VerifyTokenRequest): Response<VerifyTokenResponse>
}