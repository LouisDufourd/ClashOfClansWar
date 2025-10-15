package com.plaglefleau.clashofclansmanage.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PastebinApiClient {
    @GET("raw/{id}")
    suspend fun getPaste(@Path("id") id: String): Response<String>
}