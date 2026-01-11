package com.team10.instagram.domain.health.controller

import com.team10.instagram.global.common.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {

    @GetMapping("/api/v1/health")
    fun healthCheck(): ApiResponse<String> {
        return ApiResponse.onSuccess("I'm alive!")
    }
}
