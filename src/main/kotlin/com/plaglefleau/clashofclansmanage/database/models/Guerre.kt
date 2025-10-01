package com.plaglefleau.clashofclansmanage.database.models

data class Guerre(
    val idGuerre: Int,
    val nombreEtoileClan: Int = 0,
    val nombreEtoileOppose: Int = 0,
    val statDeGuerre: List<StatDeGuerre>,
)