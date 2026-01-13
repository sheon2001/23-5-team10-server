package com.team10.instagram.global.error

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val code: String,
    val message: String,
) {
    // 공통 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500", "서버 내부 에러가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "400", "입력값이 올바르지 않습니다."),

    // 유저 관련 에러 (예시)
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "존재하지 않는 회원입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "401", "비밀번호가 일치하지 않습니다."),

    // 게시글 관련 에러 (예시)
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "게시글을 찾을 수 없습니다."),
}
