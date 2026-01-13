package com.team10.instagram.domain.auth.service

import org.springframework.stereotype.Service

@Service
class JwtTokenBlacklistService {
    private val blacklist = mutableSetOf<String>()

    fun add(token: String) {
        blacklist.add(token)
    }

    fun contains(token: String) : Boolean {
        return blacklist.contains(token)
    }
}