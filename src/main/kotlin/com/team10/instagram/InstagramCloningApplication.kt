package com.team10.instagram

import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.TimeZone

@SpringBootApplication
class InstagramCloningApplication {
    @PostConstruct
    fun started() {
        // 시간대 'Asia/Seoul'
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
        println("현재 시간: ${java.time.LocalDateTime.now()}") // 로그 확인용
    }
}

fun main(args: Array<String>) {
    runApplication<InstagramCloningApplication>(*args)
}
