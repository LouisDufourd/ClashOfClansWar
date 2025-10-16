package com.plaglefleau.clashofclansmanage.database.models

data class CompteClash(val idCompte: String, val pseudo: String = "", val rang: Rang = Rang.MEMBER, val compteDiscord: CompteDiscord? = null)
