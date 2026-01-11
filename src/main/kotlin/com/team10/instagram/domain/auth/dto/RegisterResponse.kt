package com.team10.instagram.domain.auth.dto

import com.team10.instagram.domain.user.dto.UserDto

data class RegisterResponse(
    val accessToken: String,
    val user: UserDto,
)
