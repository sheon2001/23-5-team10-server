package com.team10.instagram.domain.user.repository

import com.team10.instagram.domain.user.model.User
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Long> {
    fun findByUserId(userId: Long): User?

    fun findByEmail(email: String): User?

    fun findByNickname(nickname: String): User?

    fun existsByUserId(userId: Long): Boolean

    fun existsByEmail(email: String): Boolean

    fun existsByNickname(nickname: String): Boolean

    fun findByProviderAndProviderId(
        provider: String,
        providerId: String,
    ): User?

    fun deleteByUserId(userId: Long)
}
