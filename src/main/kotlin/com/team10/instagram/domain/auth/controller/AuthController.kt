package com.team10.instagram.domain.auth.controller

import com.team10.instagram.domain.auth.dto.LoginRequest
import com.team10.instagram.domain.auth.dto.LoginResponse
import com.team10.instagram.domain.auth.dto.RegisterRequest
import com.team10.instagram.domain.auth.dto.RegisterResponse
import com.team10.instagram.domain.auth.dto.OAuthLoginRequest
import com.team10.instagram.domain.auth.service.AuthService
import com.team10.instagram.domain.auth.service.JwtTokenBlacklistService
import com.team10.instagram.global.common.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
    private val jwtTokenBlacklistService: JwtTokenBlacklistService
) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ApiResponse<LoginResponse> {
        val token = authService.login(request.loginId, request.password)
        return ApiResponse.onSuccess(LoginResponse(token))
    }


    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ApiResponse<RegisterResponse> {
        val user = authService.register(request.email, request.password, request.nickname)
        val token = authService.login(request.email, request.password)

        return ApiResponse.onSuccess(RegisterResponse(accessToken = token, user = user,))
    }

    @PostMapping("/logout")
    fun logout(@RequestHeader("Authorization") authorizationHeader: String): ApiResponse<String> {
        val token = authorizationHeader.replace("Bearer","").trim()

        jwtTokenBlacklistService.add(token)

        return ApiResponse.onSuccess("Logged out successfully")
    }


    @PostMapping("/oauth")
    fun oauthLogin(@RequestBody request: OAuthLoginRequest): ApiResponse<LoginResponse> {
        val token = authService.loginOAuth(
            email = request.email,
            nickname = request.nickname,
            provider = request.provider,
            providerId = request.providerId,
        )
        return ApiResponse.onSuccess(LoginResponse(token))
    }
}
