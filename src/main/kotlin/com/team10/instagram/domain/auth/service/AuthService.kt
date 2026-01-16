package com.team10.instagram.domain.auth.service

import com.team10.instagram.domain.auth.jwt.JwtTokenProvider
import com.team10.instagram.domain.user.Role
import com.team10.instagram.domain.user.dto.UserDto
import com.team10.instagram.domain.user.model.User
import com.team10.instagram.domain.user.repository.UserRepository
import com.team10.instagram.global.error.CustomException
import com.team10.instagram.global.error.ErrorCode
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
) {
    fun register(
        email: String,
        password: String,
        nickname: String,
    ): UserDto {
        if (userRepository.existsByEmail(email)) {
            throw CustomException(ErrorCode.EMAIL_ALREADY_EXISTS)
        }
        if (userRepository.existsByNickname(nickname)) {
            throw CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS)
        }

        val encodedPassword = passwordEncoder.encode(password)

        val user =
            userRepository.save(
                User(
                    email = email,
                    password = encodedPassword,
                    nickname = nickname,
                    role = Role.USER,
                    provider = null,
                    providerId = null,
                ),
            )

        return UserDto(user)
    }

    fun login(
        loginId: String,
        password: String,
    ): String {
        val user =
            findUserByLoginId(loginId)
                ?: throw CustomException(ErrorCode.USER_NOT_FOUND)

        if (!passwordEncoder.matches(password, user.password)) {
            throw CustomException(ErrorCode.INVALID_PASSWORD)
        }

        return jwtTokenProvider.createToken(user.userId!!)
    }

    private fun findUserByLoginId(loginId: String): User? =
        if (loginId.contains("@")) {
            userRepository.findByEmail(loginId)
        } else {
            userRepository.findByNickname(loginId)
        }

    fun loginOAuth(
        email: String,
        nickname: String?,
        provider: String,
        providerId: String,
    ): String {
        val user =
            userRepository.findByEmail(email)
                ?: userRepository.save(
                    User(
                        email = email,
                        password = null,
                        nickname = if (nickname.isNullOrBlank()) "nickname" else nickname,
                        role = Role.USER,
                        provider = provider,
                        providerId = providerId,
                    ),
                )

        return jwtTokenProvider.createToken(user.userId!!)
    }
}
