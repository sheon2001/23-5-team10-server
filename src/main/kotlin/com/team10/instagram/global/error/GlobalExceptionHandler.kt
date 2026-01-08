package com.team10.instagram.global.error

import com.team10.instagram.global.common.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    // 1. 우리가 직접 정의한 에러 (CustomException) 처리
    @ExceptionHandler(CustomException::class)
    fun handleCustomException(e: CustomException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(e.errorCode.status)
            .body(ApiResponse.onFailure(e.errorCode.code, e.errorCode.message))

    // 2. 예상치 못한 모든 시스템 에러 (Exception) 처리
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        e.printStackTrace() // 서버 로그에 에러 스택트레이스 출력 (디버깅용)
        return ResponseEntity
            .status(ErrorCode.INTERNAL_SERVER_ERROR.status)
            .body(ApiResponse.onFailure(ErrorCode.INTERNAL_SERVER_ERROR.code, ErrorCode.INTERNAL_SERVER_ERROR.message))
    }
}

