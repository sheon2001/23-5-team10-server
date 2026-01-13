package com.team10.instagram.domain.auth.dto

data class LoginRequest(
    val loginId: String,  // email OR nickname
    val password: String,
)