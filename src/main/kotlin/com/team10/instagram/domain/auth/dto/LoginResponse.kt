package com.team10.instagram.domain.auth.dto

typealias LoginResponse = TokenResponse

data class TokenResponse(
    val accessToken: String,
)
