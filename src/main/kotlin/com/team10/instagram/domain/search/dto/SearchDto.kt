package com.team10.instagram.domain.search.dto

data class SaveSearchRequest(
    val toUserId: Long,
)

data class DeleteSearchRequest(
    val toUserId: Long,
)

data class SaveSearchResponse(
    val searchId: Long,
)

data class GetSearchHistoryResponseUnit(
    val searchId: Long,
    val userId: Long,
    val nickname: String,
    val profileImageUrl: String?,
)

data class GetSearchHistoryResponse(
    val items: List<GetSearchHistoryResponseUnit>?,
)
