package com.team10.instagram.domain.auth.dto

data class RegisterRequest(
    val email: String,
    val password: String,
    val nickname: String,
)
