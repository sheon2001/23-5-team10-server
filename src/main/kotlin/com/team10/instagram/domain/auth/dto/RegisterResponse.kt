package com.team10.instagram.domain.auth.dto

import io.swagger.v3.oas.annotations.media.Schema
import com.team10.instagram.domain.user.dto.UserDto

data class RegisterResponse(
    @Schema(description = "회원가입 직후 로그인 상태 유지하기 위한 액세스 토큰", example = "")
    val accessToken: String,
    @Schema(description = "유저 정보")
    val user: UserDto,
)
