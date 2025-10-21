package com.plaglefleau.clashofclansmanage.api.model.error

data class ClientError(val reason: String, val message: String, val type: String?, val detail: Any?)