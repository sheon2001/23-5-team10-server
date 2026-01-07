package com.team10.instagram.global.common

// 모든 응답은 이 껍데기로 감싸서 나갑니다.
data class ApiResponse<T>(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val data: T? // 실제 데이터 (DTO 등)
) {
    companion object {
        // 성공했을 때: ApiResponse.onSuccess(데이터)
        fun <T> onSuccess(data: T): ApiResponse<T> {
            return ApiResponse(
                isSuccess = true,   //성공 여부 (Boolean)
                code = "200",   // 응답 코드 (String)
                message = "요청에 성공하였습니다.",   // 메시지 (String)
                data = data     // 실제 들어갈 데이터 (Generic)
            )
        }

        // 실패했을 때: ApiResponse.onFailure("400", "잘못된 요청")
        fun onFailure(code: String, message: String): ApiResponse<Nothing> {
            return ApiResponse(
                isSuccess = false,
                code = code,
                message = message,
                data = null
            )
        }
    }
}