package com.plaglefleau.clashofclansmanage.database.models

data class InscriptionGuerre(val guerre: Guerre, val compte: CompteClash, val participation: Boolean) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as InscriptionGuerre
        if (guerre.idGuerre != other.guerre.idGuerre) return false
        if (compte.idCompte != other.compte.idCompte) return false
        return true
    }
}
