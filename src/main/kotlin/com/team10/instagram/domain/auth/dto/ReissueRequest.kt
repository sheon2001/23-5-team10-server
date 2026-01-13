package com.team10.instagram.domain.auth.dto

import io.swagger.v3.oas.annotations.media.Schema

data class ReissueRequest(
    //TODO
    val refreshToken: String,
)