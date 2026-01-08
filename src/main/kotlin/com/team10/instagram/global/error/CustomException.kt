package com.team10.instagram.global.error

class CustomException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.message)
