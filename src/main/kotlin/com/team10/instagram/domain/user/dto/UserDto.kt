package com.team10.instagram.domain.user.dto

import com.team10.instagram.domain.user.model.User

data class UserDto(
    val userId: Long,
    val email: String,
    val nickname: String,
    val profileImageUrl: String?,
    val bio: String?,
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