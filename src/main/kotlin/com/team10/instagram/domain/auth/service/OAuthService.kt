package com.team10.instagram.domain.auth.service

import com.team10.instagram.domain.auth.jwt.JwtTokenProvider
import com.team10.instagram.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class OAuthService(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
) {
/*
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
    }*/
}
