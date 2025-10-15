package com.plaglefleau.clashofclansmanage.api

import com.plaglefleau.clashofclansmanage.Credential
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object PastebinApiAdapter {

    private val client = OkHttpClient.Builder().build()

    val apiClient: PastebinApiClient = Retrofit.Builder()
        .baseUrl(Credential.PASTEBIN_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .client(client)
        .build()
        .create(PastebinApiClient::class.java)
}