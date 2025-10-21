package com.plaglefleau.clashofclansmanage.api

import com.plaglefleau.clashofclansmanage.api.model.clan.Clan
import com.plaglefleau.clashofclansmanage.api.model.clan.ClanMembers
import com.plaglefleau.clashofclansmanage.api.model.war.ClanWar
import com.plaglefleau.clashofclansmanage.api.model.other.VerifyTokenRequest
import com.plaglefleau.clashofclansmanage.api.model.other.VerifyTokenResponse
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

    @GET("clans/{clanTag}")
    suspend fun getClan(@Path("clanTag") clanTag: String): Response<Clan>
    @POST("players/{playerTag}/verifytoken")
    suspend fun verifyToken(@Path("playerTag") playerTag: String, @Body token: VerifyTokenRequest): Response<VerifyTokenResponse>
}