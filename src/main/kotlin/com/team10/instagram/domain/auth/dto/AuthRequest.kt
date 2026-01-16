package com.team10.instagram.domain.auth.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

class AuthRequest {
    data class LoginRequest(
        @field:NotBlank
        @Schema(description = "로그인 화면에서 입력한 이메일 또는 nickname", example = "test@gmail.com")
        val loginId: String,
        @field:NotBlank
        @Schema(description = "로그인 화면에서 입력한 PW", example = "password123")
        val password: String,
    )

    data class OAuthLoginRequest(
        // TODO
        val email: String,
        val nickname: String?,
        val provider: String,
        val providerId: String,
    )

    data class RegisterRequest(
        @field:NotBlank
        @Email
        @Schema(description = "회원가입 요청 시 작성한 이메일", example = "test@gmail.com")
        val email: String,
        @field:NotBlank
        @Schema(description = "회원가입 요청 시 작성한 PW", example = "password123")
        val password: String,
        @field:NotBlank
        @Schema(description = "회원가입 요청 시 작성한 nickname", example = "test_user_123")
        val nickname: String,
    )

    data class RefreshRequest(
        // TODO
        val refreshToken: String,
    )
}
