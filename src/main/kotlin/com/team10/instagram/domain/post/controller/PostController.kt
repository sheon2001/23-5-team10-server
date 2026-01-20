package com.team10.instagram.domain.post.controller

import com.team10.instagram.domain.post.dto.BookMarkedSearchResponse
import com.team10.instagram.domain.post.dto.PostCreateRequest
import com.team10.instagram.domain.post.dto.PostImageResponse
import com.team10.instagram.domain.post.dto.PostResponse
import com.team10.instagram.domain.post.dto.PostUpdateRequest
import com.team10.instagram.domain.post.dto.SearchResponse
import com.team10.instagram.domain.post.service.PostService
import com.team10.instagram.domain.user.LoggedInUser
import com.team10.instagram.domain.user.model.User
import com.team10.instagram.global.common.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/posts")
@Tag(name = "Post API", description = "게시글 관련 API")
class PostController(
    private val postService: PostService,
) {
    private fun getDummyImages(): List<PostImageResponse> =
        listOf(
            PostImageResponse(id = 1L, url = "https://example.com/image1.jpg", orderIndex = 0),
            PostImageResponse(id = 2L, url = "https://example.com/image2.jpg", orderIndex = 1),
        )

    // Create single post
    // Assume image urls (for S3 buckets) are received
    // Actual image upload logic will implement at seperated controller.
    @PostMapping
    @Operation(summary = "게시글 생성", description = "게시글을 작성합니다.")
    fun createPost(
        @Parameter(hidden = true) @LoggedInUser user: User,
        @Valid @RequestBody request: PostCreateRequest,
    ): ApiResponse<PostResponse> = ApiResponse.onSuccess(postService.create(user, request))

    // Retrieve single post
    @GetMapping("/{postId}")
    @Operation(summary = "게시글 상세 조회", description = "게시글 ID로 상세 정보를 조회합니다.")
    fun getPost(
        @Parameter(hidden = true) @LoggedInUser user: User,
        @PathVariable postId: Long,
    ): ApiResponse<PostResponse> = ApiResponse.onSuccess(postService.get(user, postId))

    // Retrieve multiple posts
    @GetMapping("/search")
    @Operation(summary = "게시글 탐색", description = "추천 게시글 목록을 조회합니다.")
    fun searchPosts(
        @Parameter(hidden = true) @LoggedInUser user: User,
    ): ApiResponse<SearchResponse> = ApiResponse.onSuccess(postService.search(user))

    // Retrieve bookmarked posts
    @GetMapping("/bookmarks")
    @Operation(summary = "북마크 게시글 조회", description = "내가 북마크한 게시글 목록을 조회합니다.")
    fun searchBookmarkedPosts(
        @Parameter(hidden = true) @LoggedInUser user: User,
    ): ApiResponse<BookMarkedSearchResponse> = ApiResponse.onSuccess(postService.getBookmarkedPosts(user))

    // Update single posts
    @PutMapping("/{postId}")
    @Operation(summary = "게시글 수정", description = "게시글 내용을 수정합니다.")
    fun updatePost(
        @Parameter(hidden = true) @LoggedInUser user: User,
        @PathVariable postId: Long,
        @Valid @RequestBody request: PostUpdateRequest,
    ): ApiResponse<PostResponse> = ApiResponse.onSuccess(postService.update(user, postId, request))

    // Delete single posts
    @DeleteMapping("/{postId}")
    @Operation(summary = "게시글 삭제", description = "게시글 내용을 삭제합니다.")
    fun deletePost(
        @Parameter(hidden = true) @LoggedInUser user: User,
        @PathVariable postId: Long,
    ): ApiResponse<Unit> {
        postService.delete(user, postId)
        return ApiResponse.onSuccess(Unit)
    }

    // Like single posts
    @PostMapping("/{postId}/like")
    @Operation(summary = "게시글 좋아요", description = "게시글에 좋아요를 남깁니다.")
    fun likePost(
        @Parameter(hidden = true) @LoggedInUser user: User,
        @PathVariable postId: Long,
    ): ApiResponse<Unit> {
        postService.likePost(user, postId)
        return ApiResponse.onSuccess(Unit)
    }

    // Unlike single posts
    @DeleteMapping("/{postId}/like")
    @Operation(summary = "게시글 좋아요 취소", description = "게시글에 좋아요를 취소합니다.")
    fun unlikePost(
        @Parameter(hidden = true) @LoggedInUser user: User,
        @PathVariable postId: Long,
    ): ApiResponse<Unit> {
        postService.unlikePost(user, postId)
        return ApiResponse.onSuccess(Unit)
    }

    // Bookmark single posts
    @PostMapping("/{postId}/bookmark")
    @Operation(summary = "게시글 북마크", description = "게시글을 북마크합니다.")
    fun bookMarkPost(
        @Parameter(hidden = true) @LoggedInUser user: User,
        @PathVariable postId: Long,
    ): ApiResponse<Unit> {
        postService.bookmarkPost(user, postId)
        return ApiResponse.onSuccess(Unit)
    }

    // Unbookmark single posts
    @DeleteMapping("/{postId}/bookmark")
    @Operation(summary = "게시글 북마크 취소", description = "게시글 북마크를 취소합니다.")
    fun unBookMarkPost(
        @Parameter(hidden = true) @LoggedInUser user: User,
        @PathVariable postId: Long,
    ): ApiResponse<Unit> {
        postService.unBookmarkPost(user, postId)
        return ApiResponse.onSuccess(Unit)
    }
}
