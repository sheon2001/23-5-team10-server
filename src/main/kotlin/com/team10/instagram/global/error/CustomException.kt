package com.team10.instagram.global.error

class CustomException(
    val errorCode: ErrorCode,
    val data: Any? = null,
) : RuntimeException(errorCode.message)
