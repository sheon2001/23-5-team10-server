package com.team10.instagram.domain.auth.dto

import io.swagger.v3.oas.annotations.media.Schema

data class LoginRequest(
    @Schema(description = "로그인 화면에서 입력한 이메일 또는 nickname", example = "test@gmail.com")
    val loginId: String,
    @Schema(description = "로그인 화면에서 입력한 PW", example = "password123")
    val password: String,
)