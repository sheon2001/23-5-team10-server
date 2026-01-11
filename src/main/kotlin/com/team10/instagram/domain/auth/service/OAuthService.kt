package com.instagram.clone.domain.auth.service

import com.instagram.clone.domain.user.domain.Provider
import com.instagram.clone.domain.user.domain.User
import com.instagram.clone.domain.user.repository.UserRepository
import com.instagram.clone.global.error.CustomException
import com.instagram.clone.global.error.ErrorCode
import com.instagram.clone.global.security.JwtTokenProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OAuthService(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {

    /**
     * Facebook OAuth 로그인 또는 회원가입
     *
     * @param email OAuth 제공자가 전달한 이메일
     * @param name OAuth 제공자가 전달한 이름
     * @param providerId OAuth 제공자가 발급한 고유 ID
     * @return JWT Access Token
     */
    @Transactional
    fun loginOrRegister(
        email: String,
        name: String,
        providerId: String
    ): String {

        // 이미 가입된 사용자 확인
        val existingUser = userRepository.findByProviderAndProviderId(Provider.FACEBOOK, providerId)

        val user = if (existingUser != null) {
            existingUser
        } else {
            // 신규 회원 생성
            val newUser = User(
                email = email,
                password = "", // OAuth는 패스워드 없음
                name = name,
                provider = Provider.FACEBOOK,
                providerId = providerId
            )
            userRepository.save(newUser)
        }

        // JWT Access Token 발급
        return jwtTokenProvider.createToken(user.id, user.role.name)
    }
}
