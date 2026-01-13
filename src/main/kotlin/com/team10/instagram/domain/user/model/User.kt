package com.team10.instagram.domain.user.model


import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import com.team10.instagram.domain.user.Role


@Table("users")
class User(

    @Id val userId: Long = 0L,
    val email: String,
    var password: String? = null,
    var nickname: String,
    var profileImageUrl: String? = null,
    var bio: String? = null,
    var role: Role = Role.USER,
    var provider: String? = null,
    var providerId: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)
