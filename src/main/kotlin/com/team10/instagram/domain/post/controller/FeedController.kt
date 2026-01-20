package com.team10.instagram.domain.post.controller

import com.team10.instagram.domain.post.dto.FeedAuthorDto
import com.team10.instagram.domain.post.dto.FeedPostDto
import com.team10.instagram.domain.post.dto.FeedResponse
import com.team10.instagram.domain.post.service.FeedService
import com.team10.instagram.domain.user.LoggedInUser
import com.team10.instagram.domain.user.model.User
import com.team10.instagram.global.common.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime // for mocking

@RestController
@RequestMapping("/api/v1/feed")
@Tag(name = "Feed API", description = "피드 관련 API")
class FeedController(
    private val feedService: FeedService,
) {
    @GetMapping
    @Operation(summary = "피드 조회", description = "팔로우한 유저들의 게시글을 최신순으로 조회합니다.")
    fun getFeed(
        @Parameter(hidden = true) @LoggedInUser user: User,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "6") size: Int,
    ): ApiResponse<FeedResponse> {
        val dummyItems =
            listOf(
                FeedPostDto(
                    postId = 123L,
                    author =
                        FeedAuthorDto(
                            userId = 5L,
                            nickname = "celebrity_kim",
                            profileImageUrl = "https://dummyimage.com/100x100/ff0/000&text=Celeb",
                        ),
                    thumbnailImageUrl = "https://dummyimage.com/600x400/000/fff&text=Feed+Image+1",
                    likeCount = 32,
                    commentCount = 4,
                    createdAt = LocalDateTime.now().minusHours(2),
                    liked = true,
                    bookmarked = false,
                ),
                FeedPostDto(
                    postId = 124L,
                    author =
                        FeedAuthorDto(
                            userId = 8L,
                            nickname = "travel_lover",
                            profileImageUrl = "https://dummyimage.com/100x100/00f/fff&text=Travel",
                        ),
                    thumbnailImageUrl = "https://dummyimage.com/600x400/f00/fff&text=Feed+Image+2",
                    likeCount = 120,
                    commentCount = 15,
                    createdAt = LocalDateTime.now().minusDays(1),
                    liked = false,
                    bookmarked = true,
                ),
            )

        val dummyResponse =
            FeedResponse(
                items = dummyItems,
                page = page,
                size = size,
                totalPages = 12,
                totalElements = 72,
                hasNext = true,
                hasPrev = page > 1,
            )

        return ApiResponse.onSuccess(dummyResponse)
    }
}
