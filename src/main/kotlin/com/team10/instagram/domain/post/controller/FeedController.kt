package com.team10.instagram.domain.post.controller

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
    ): ApiResponse<FeedResponse> = ApiResponse.onSuccess(feedService.getFeed(user, page, size))
}
