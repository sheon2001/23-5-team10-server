package com.team10.instagram.domain.auth.dto

data class OAuthLoginRequest(
    val email: String,
    val nickname: String,
    val provider: String,
    val providerId: String,
)
