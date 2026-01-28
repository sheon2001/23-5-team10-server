package com.team10.instagram.domain.user.service

import com.team10.instagram.domain.user.dto.UserSearchResponse
import com.team10.instagram.domain.user.dto.UserSearchResponseDtoUnit
import com.team10.instagram.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun deleteUser(userId: Long) {
        userRepository.deleteByUserId(userId)
    }

    fun search(q: String): UserSearchResponse {
        val users = userRepository.findByNicknameContainingIgnoreCaseOrNameContainingIgnoreCase(q, q)
        val userDtos: List<UserSearchResponseDtoUnit> =
            users.map { user ->
                UserSearchResponseDtoUnit(
                    userId = user.userId!!,
                    nickname = user.nickname,
                    profileImageUrl = user.profileImageUrl,
                )
            }
        return UserSearchResponse(userDtos)
    }
}
