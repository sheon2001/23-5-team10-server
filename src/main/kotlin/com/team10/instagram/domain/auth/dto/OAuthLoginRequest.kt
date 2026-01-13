package com.team10.instagram.domain.auth.dto

import io.swagger.v3.oas.annotations.media.Schema

data class OAuthLoginRequest(
    //TODO
    val email: String,
    val nickname: String,
    val provider: String,
    val providerId: String,
)
