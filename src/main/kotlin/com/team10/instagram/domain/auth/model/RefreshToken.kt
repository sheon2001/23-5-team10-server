package com.team10.instagram.domain.auth.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("refresh_tokens")
class RefreshToken(
    @Id val tokenId: Long? = null,
    val userId: Long,
    val token: String,
    val expiresAt: LocalDateTime,
    var usedAt: LocalDateTime? = null,
)
