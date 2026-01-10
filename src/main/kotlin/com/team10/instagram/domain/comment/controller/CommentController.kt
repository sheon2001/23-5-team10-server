package com.team10.instagram.domain.comment.controller

import com.team10.instagram.domain.comment.dto.CommentCreateRequest
import com.team10.instagram.domain.comment.dto.CommentResponse
import com.team10.instagram.domain.comment.dto.CommentUpdateRequest
import com.team10.instagram.global.common.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime // for mocking
// removed for dummy output
// import com.team10.instagram.domain.comment.service.CommentService

@RestController
@RequestMapping("/api/v1/posts")
class CommentController(
    // private val commentService: CommentService,
) {
    @PostMapping("/{postId}/comments")
    @Operation(summary = "댓글 생성", description = "특정 게시글에 댓글을 작성합니다.")
    fun createComment(
        @PathVariable postId: Long,
        @Valid @RequestBody request: CommentCreateRequest,
    ): ApiResponse<CommentResponse> {
        val response =
            CommentResponse(
                id = 1L,
                postId = 11L,
                userId = 111L,
                nickname = "Dummy Nickname1",
                profileImageUrl = "https://example.com/profile1.jpg",
                content = "첫 번째 더미 댓글입니다.",
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
            )
        return ApiResponse.onSuccess(response)
    }

    @GetMapping("/{postId}/comments")
    @Operation(summary = "댓글 목록 조회", description = "게시글의 댓글 목록을 조회합니다.")
    fun getComments(
        @PathVariable postId: Long,
    ): ApiResponse<List<CommentResponse>> {
        val comments =
            listOf(
                CommentResponse(
                    id = 2L,
                    postId = 22L,
                    userId = 222L,
                    nickname = "Dummy Nickname2",
                    profileImageUrl = "https://example.com/profile2.jpg",
                    content = "두 번째 더미 댓글입니다.",
                    createdAt = LocalDateTime.now().minusHours(2),
                    updatedAt = LocalDateTime.now().minusHours(2),
                ),
                CommentResponse(
                    id = 3L,
                    postId = 33L,
                    userId = 333L,
                    nickname = "Dummy Nickname3",
                    profileImageUrl = "https://example.com/profile3.jpg",
                    content = "세 번째 더미 댓글입니다.",
                    createdAt = LocalDateTime.now().minusHours(1),
                    updatedAt = LocalDateTime.now().minusHours(1),
                ),
            )
        return ApiResponse.onSuccess(comments)
    }

    @PutMapping("/{postId}/comments/{commentId}")
    @Operation(summary = "댓글 수정", description = "댓글 내용을 수정합니다.")
    fun updateComment(
        @PathVariable postId: Long,
        @PathVariable commentId: Long,
        @Valid @RequestBody request: CommentUpdateRequest,
    ): ApiResponse<CommentResponse> {
        val response =
            CommentResponse(
                id = 4L,
                postId = 44L,
                userId = 444L,
                nickname = "Dummy Nickname4",
                profileImageUrl = "https://example.com/profile4.jpg",
                content = "네 번째 더미 댓글입니다.",
                createdAt = LocalDateTime.now().minusHours(2),
                updatedAt = LocalDateTime.now().minusHours(2),
            )
        return ApiResponse.onSuccess(response)
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    fun deleteComment(
        @PathVariable postId: Long,
        @PathVariable commentId: Long,
    ): ApiResponse<Unit> = ApiResponse.onSuccess(Unit)
}
