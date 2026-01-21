package com.team10.instagram.domain.post.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class PostCreateRequest(
    @Schema(description = "게시글 내용", example = "오늘 날씨 좋다!")
    @field:Size(max = 1000, message = "게시글은 1000자 이하여야 합니다.")
    val content: String,
    val albumId: Long?,
    val imageUrls: List<String> = emptyList(),
)

data class PostUpdateRequest(
    @Schema(description = "수정할 게시글 내용", example = "오늘 날씨 좋다!")
    @field:Size(max = 1000, message = "게시글은 1000자 이하여야 합니다.")
    val content: String,
    val albumId: Long?,
    val imageUrls: List<String> = emptyList(),
)

data class PostResponse(
    val id: Long,
    val userId: Long,
    val nickname: String,
    val profileImageUrl: String?,
    val content: String,
    val albumId: Long?,
    val images: List<PostImageResponse>,
    val likeCount: Long,
    val commentCount: Long,
    val isLiked: Boolean,
    val isBookmarked: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

typealias SearchResponse = List<PostResponse>
typealias BookMarkedSearchResponse = List<PostResponse>

data class PostImageResponse(
    val id: Long,
    val url: String,
    val orderIndex: Int,
)
