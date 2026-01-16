package com.team10.instagram.domain.user.service

import com.team10.instagram.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun deleteUser(userId: Long) {
        userRepository.deleteByUserId(userId)
    }
}
