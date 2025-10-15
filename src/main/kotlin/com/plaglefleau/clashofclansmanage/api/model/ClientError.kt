package com.plaglefleau.clashofclansmanage.api.model

data class ClientError(val reason: String, val message: String, val type: String?, val detail: Any?)
