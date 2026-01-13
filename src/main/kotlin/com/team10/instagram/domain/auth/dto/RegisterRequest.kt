package com.team10.instagram.domain.auth.dto

import io.swagger.v3.oas.annotations.media.Schema

data class RegisterRequest(
    @Schema(description = "회원가입 요청 시 작성한 이메일", example = "test@gmail.com")
    val email: String,
    @Schema(description = "회원가입 요청 시 작성한 PW", example = "password123")
    val password: String,
    @Schema(description = "회원가입 요청 시 작성한 nickname", example = "test_user_123")
    val nickname: String,
)
