package com.plaglefleau.clashofclansmanage

import com.google.gson.Gson
import com.plaglefleau.clashofclansmanage.api.ClashOfClansApiAdapter
import com.plaglefleau.clashofclansmanage.api.ClashOfClansApiClient
import retrofit2.HttpException

suspend fun main() {
    try {
        val war = ClashOfClansApiAdapter.apiClient.getClanCurrentWar("#2G8LPLUR8")
        print(war.opponent.stars)
    } catch (e: HttpException) {
        e.response()?.errorBody()?.string()?.let { println(it) }
    }

}