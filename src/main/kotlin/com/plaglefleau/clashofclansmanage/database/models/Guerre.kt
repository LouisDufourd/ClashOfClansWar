package com.plaglefleau.clashofclansmanage.database.models

import java.util.Calendar

data class Guerre(
    val idGuerre: Int,
    val nombreEtoileClan: Int = 0,
    val nombreEtoileOppose: Int = 0,
    val dateDebut: Calendar = Calendar.getInstance(),
    val dateFin: Calendar,
    val statDeGuerre: List<StatDeGuerre> = emptyList(),
    val consequence: Boolean
)