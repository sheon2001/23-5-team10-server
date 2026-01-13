package com.team10.instagram.domain.auth.dto

import io.swagger.v3.oas.annotations.media.Schema

typealias LoginResponse = TokenResponse

data class TokenResponse(
    @Schema(description = "액세스 토큰", example = "")
    val accessToken: String,

    //TODO
    @Schema(description = "재발급 토큰", example = "")
    val reissueToken: String = ""
)
