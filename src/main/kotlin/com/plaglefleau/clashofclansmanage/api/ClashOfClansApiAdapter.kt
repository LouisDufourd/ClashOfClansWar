package com.plaglefleau.clashofclansmanage.api

import com.plaglefleau.clashofclansmanage.Credential
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


object ClashOfClansApiAdapter {

    private val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(Interceptor { chain ->
        val newRequest: Request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer " + Credential.CLASH_API_TOKEN)
            .build()
        chain.proceed(newRequest)
    }).build()

    val apiClient: ClashOfClansApiClient = Retrofit.Builder()
        .baseUrl(Credential.CLASH_API_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ClashOfClansApiClient::class.java)
}