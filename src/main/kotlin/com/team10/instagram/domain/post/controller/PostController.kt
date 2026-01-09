package com.team10.instagram.domain.post.controller

import com.team10.instagram.domain.post.dto.BookMarkedSearchResponse
import com.team10.instagram.domain.post.dto.PostCreateRequest
import com.team10.instagram.domain.post.dto.PostImageResponse
import com.team10.instagram.domain.post.dto.PostResponse
import com.team10.instagram.domain.post.dto.PostUpdateRequest
import com.team10.instagram.domain.post.dto.SearchResponse
import com.team10.instagram.global.common.ApiResponse
import io.swagger.v3.oas.annotations.Operation
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
import java.time.LocalDateTime // for mocking

// removed for dummy output
// import com.team10.instagram.domain.post.service.PostService
@RestController
@RequestMapping("/api/v1/posts")
@Tag(name = "Post API", description = "게시글 관련 API")
class PostController(
    // private val postService: PostService,
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
    @Operation(summary = "게시글 생성", description = "게시글을 작성합니다.") // [추가]
    fun createPost(
        @Valid @RequestBody request: PostCreateRequest,
    ): ApiResponse<PostResponse> {
        val response =
            PostResponse(
                id = 11L,
                userId = 111L,
                nickname = "Dummy Nickname1",
                profileImageUrl = "https://example.com/profile1.jpg",
                content = "첫 번째 게시글입니다!",
                albumId = 1111L,
                images = getDummyImages(),
                likeCount = 0,
                commentCount = 0,
                isLiked = false,
                isBookmarked = false,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
            )
        return ApiResponse.onSuccess(response)
    }

    // Retrieve single post
    @GetMapping("/{postId}")
    @Operation(summary = "게시글 상세 조회", description = "게시글 ID로 상세 정보를 조회합니다.")
    fun getPost(
        @PathVariable postId: Long,
    ): ApiResponse<PostResponse> {
        val response =
            PostResponse(
                id = 22L,
                userId = 222L,
                nickname = "Dummy Nickname2",
                profileImageUrl = "https://example.com/profile2.jpg",
                content = "두 번째 게시글입니다!",
                albumId = 2222L,
                images = getDummyImages(),
                likeCount = 0,
                commentCount = 0,
                isLiked = false,
                isBookmarked = false,
                createdAt = LocalDateTime.now().minusDays(1),
                updatedAt = LocalDateTime.now().minusDays(1),
            )
        return ApiResponse.onSuccess(response)
    }

    // Retrieve multiple posts
    @GetMapping("/search")
    @Operation(summary = "게시글 탐색", description = "추천 게시글 목록을 조회합니다.")
    fun searchPosts(): ApiResponse<SearchResponse> {
        val posts =
            listOf(
                PostResponse(
                    id = 33L,
                    userId = 333L,
                    content = "유저 333L의 첫 번째 게시글입니다.",
                    nickname = "Dummy Nickname3",
                    profileImageUrl = "https://example.com/profile3.jpg",
                    albumId = 3333L,
                    images = getDummyImages(),
                    likeCount = 120,
                    commentCount = 15,
                    isLiked = false,
                    isBookmarked = false,
                    createdAt = LocalDateTime.now().minusDays(5),
                    updatedAt = LocalDateTime.now().minusDays(5),
                ),
                PostResponse(
                    id = 44L,
                    userId = 444L,
                    nickname = "Dummy Nickname4",
                    profileImageUrl = null,
                    content = "유저 444L의 두 번째 게시글입니다.",
                    albumId = null,
                    images = getDummyImages(),
                    likeCount = 230,
                    commentCount = 42,
                    isLiked = true,
                    isBookmarked = true,
                    createdAt = LocalDateTime.now().minusDays(2),
                    updatedAt = LocalDateTime.now().minusDays(2),
                ),
            )
        return ApiResponse.onSuccess(posts)
    }

    // Retrieve bookmarked posts
    @GetMapping("/bookmarks")
    @Operation(summary = "북마크 게시글 조회", description = "내가 북마크한 게시글 목록을 조회합니다.")
    fun searchBookmarkedPosts(): ApiResponse<BookMarkedSearchResponse> {
        val bookmarkedPosts =
            listOf(
                PostResponse(
                    id = 33L,
                    userId = 333L,
                    content = "유저 333L의 첫 번째 게시글입니다.",
                    nickname = "Dummy Nickname3",
                    profileImageUrl = "https://example.com/profile3.jpg",
                    albumId = 3333L,
                    images = getDummyImages(),
                    likeCount = 120,
                    commentCount = 15,
                    isLiked = false,
                    isBookmarked = false,
                    createdAt = LocalDateTime.now().minusDays(5),
                    updatedAt = LocalDateTime.now().minusDays(5),
                ),
                PostResponse(
                    id = 44L,
                    userId = 444L,
                    nickname = "Dummy Nickname4",
                    profileImageUrl = null,
                    content = "유저 444L의 두 번째 게시글입니다.",
                    albumId = null,
                    images = getDummyImages(),
                    likeCount = 230,
                    commentCount = 42,
                    isLiked = true,
                    isBookmarked = true,
                    createdAt = LocalDateTime.now().minusDays(2),
                    updatedAt = LocalDateTime.now().minusDays(2),
                ),
            )
        return ApiResponse.onSuccess(bookmarkedPosts)
    }

    // Update single posts
    @PutMapping("/{postId}")
    @Operation(summary = "게시글 수정", description = "게시글 내용을 수정합니다.")
    fun updatePost(
        @PathVariable postId: Long,
        @Valid @RequestBody request: PostUpdateRequest,
    ): ApiResponse<PostResponse> {
        val response =
            PostResponse(
                id = 11L,
                userId = 111L,
                nickname = "Dummy Nickname1",
                profileImageUrl = "https://example.com/profile1.jpg",
                content = "첫 번째 게시글 업데이트 내용입니다",
                albumId = 1L,
                images = getDummyImages(),
                likeCount = 10,
                commentCount = 5,
                isLiked = true,
                isBookmarked = false,
                createdAt = LocalDateTime.now().minusDays(1),
                updatedAt = LocalDateTime.now(),
            )
        return ApiResponse.onSuccess(response)
    }

    // Delete single posts
    @DeleteMapping("/{postId}")
    @Operation(summary = "게시글 삭제", description = "게시글 내용을 삭제합니다.")
    fun deletePost(
        @PathVariable postId: Long,
    ): ApiResponse<Unit> = ApiResponse.onSuccess(Unit)

    // Like single posts
    @PostMapping("/{postId}/like")
    @Operation(summary = "게시글 좋아요", description = "게시글에 좋아요를 남깁니다.")
    fun likePost(
        @PathVariable postId: Long,
    ): ApiResponse<Unit> = ApiResponse.onSuccess(Unit)

    // Unlike single posts
    @DeleteMapping("/{postId}/like")
    @Operation(summary = "게시글 좋아요 취소", description = "게시글에 좋아요를 취소합니다.")
    fun unlikePost(
        @PathVariable postId: Long,
    ): ApiResponse<Unit> = ApiResponse.onSuccess(Unit)

    // Bookmark single posts
    @PostMapping("/{postId}/bookmark")
    @Operation(summary = "게시글 북마크", description = "게시글을 북마크합니다.")
    fun bookMarkPost(
        @PathVariable postId: Long,
    ): ApiResponse<Unit> = ApiResponse.onSuccess(Unit)

    // Unbookmark single posts
    @DeleteMapping("/{postId}/bookmark")
    @Operation(summary = "게시글 북마크 취소", description = "게시글 북마크를 취소합니다.")
    fun unBookMarkPost(
        @PathVariable postId: Long,
    ): ApiResponse<Unit> = ApiResponse.onSuccess(Unit)
}
