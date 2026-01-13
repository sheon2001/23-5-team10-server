package com.team10.instagram.domain.auth.jwt

import com.team10.instagram.domain.auth.service.JwtTokenBlacklistService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}")
    private val secretKey: String,
    @Value("\${jwt.access-token-expiration-in-ms}")
    private val expirationInMs: Long,
) {
    private val key = Keys.hmacShaKeyFor(secretKey.toByteArray())

    fun createToken(userId: Long): String {
        val now = Date()
        val validity = Date(now.time + expirationInMs)

        return Jwts
            .builder()
            .setSubject(userId.toString())
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun getUserId(token: String): Long =
        Jwts
            .parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
            .subject
            .toLong()

    fun validateToken(token: String, jwtTokenBlacklistService: JwtTokenBlacklistService): Boolean {
        if(jwtTokenBlacklistService.contains(token)) return false

        return try {
            Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }
}