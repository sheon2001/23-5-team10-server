package com.team10.instagram.domain.user.dto

import io.swagger.v3.oas.annotations.media.Schema
import com.team10.instagram.domain.user.model.User

data class UserDto(
    @Schema(description = "사용자 식별을 위한 아이디", example = "1")
    val userId: Long,
    @Schema(description = "사용자 이메일", example = "test@gmail.com")
    val email: String,
    @Schema(description = "사용자 닉네임", example = "tester")
    val nickname: String,
    @Schema(description = "사용자 프로필 이미지 URL", example = "https://example.com/profile.png", nullable = true)
    val profileImageUrl: String?,
    @Schema(description = "사용자 소개", example = "안녕하세요, 저는 테스터입니다.", nullable = true)
    val bio: String?,
    @Schema(description = "사용자 역할", example = "USER")
    val role: String,
) {
    constructor(user: User) : this(
        userId = user.userId,
        email = user.email,
        nickname = user.nickname,
        profileImageUrl = user.profileImageUrl,
        bio = user.bio,
        role = user.role.name
    )
}