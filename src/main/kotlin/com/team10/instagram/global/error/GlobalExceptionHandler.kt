package com.team10.instagram.global.error

import com.team10.instagram.global.common.ApiResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {

    // 1. 커스텀 에러 처리
    @ExceptionHandler(CustomException::class)
    fun handleCustomException(e: CustomException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(e.errorCode.status)
            .body(ApiResponse.onFailure(e.errorCode.code, e.errorCode.message))

    // 2. @Valid 유효성 검사 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Map<String, String>>> {

        // 에러가 난 필드와 메시지를 Map으로 변환 (예: "email" to "이메일 형식이 아닙니다")
        val errors = e.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "알 수 없는 에러")
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.onFailure(
                ErrorCode.INVALID_INPUT_VALUE.code,
                ErrorCode.INVALID_INPUT_VALUE.message,
                errors // 에러 Map
            ))
    }

    // 3. 기타 에러 처리
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception, request: HttpServletRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error(e) { "예상치 못한 에러 발생 !! url: ${request.method} ${request.requestURI}" }

        return ResponseEntity
            .status(ErrorCode.INTERNAL_SERVER_ERROR.status)
            .body(ApiResponse.onFailure(ErrorCode.INTERNAL_SERVER_ERROR.code, ErrorCode.INTERNAL_SERVER_ERROR.message))
    }
}
