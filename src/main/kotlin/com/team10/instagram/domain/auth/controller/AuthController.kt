package com.team10.instagram.domain.auth.controller

import com.team10.instagram.domain.auth.dto.LoginRequest
import com.team10.instagram.domain.auth.dto.LoginResponse
import com.team10.instagram.domain.auth.dto.RegisterRequest
import com.team10.instagram.domain.auth.dto.RegisterResponse
import com.team10.instagram.domain.auth.dto.OAuthLoginRequest
import com.team10.instagram.domain.auth.service.AuthService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): LoginResponse {
        val token = authService.login(request.loginId, request.password)
        return LoginResponse(token)
    }


    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): RegisterResponse {
        val user = authService.register(request.email, request.password, request.nickname)
        val token = authService.login(request.email, request.password)

        return RegisterResponse(
            accessToken = token,
            user = user,
        )
    }


    @PostMapping("/oauth")
    fun oauthLogin(@RequestBody request: OAuthLoginRequest): LoginResponse {
        val token = authService.loginOAuth(
            email = request.email,
            nickname = request.nickname,
            provider = request.provider,
            providerId = request.providerId,
        )
        return LoginResponse(token)
    }
}
