package com.innocv.smartdoc.api.dto

data class LlmResponse(
    val rawResponse: String,
    val totalTokens: Int
)