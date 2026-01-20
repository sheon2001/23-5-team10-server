package com.team10.instagram.domain.follow.controller

import com.team10.instagram.domain.follow.dto.FollowResponse
import com.team10.instagram.domain.follow.service.FollowService
import com.team10.instagram.domain.user.LoggedInUser
import com.team10.instagram.global.common.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/follows")
@Tag(name = "Follow API", description = "팔로우/팔로워 관련 API")
class FollowController(
    private val followService: FollowService,
) {
    // 1. 팔로우 / 언팔로우
    @PostMapping("/{toUserId}")
    @Operation(summary = "팔로우/언팔로우 요청", description = "팔로우 <-> 언팔로우 전환 요청입니다.")
    fun toggleFollow(
        @PathVariable toUserId: Long,
        @LoggedInUser loggedInUser: Long
    ): ApiResponse<String> {
        val message = followService.toggleFollow(loggedInUser, toUserId)

        return ApiResponse.onSuccess(message)
    }

    // 2. 팔로워 목록 조회
    @GetMapping("/{userId}/follower")
    @Operation(summary = "팔로워 목록 조회", description = "특정 유저를 팔로우 하는 사람들의 목록을 조회합니다.")
    fun getFollowers(
        @PathVariable userId: Long,
        @LoggedInUser loggedInUser: Long
    ): ApiResponse<List<FollowResponse>> {
        val followers = followService.getFollowers(userId, loggedInUser)

        return ApiResponse.onSuccess(followers)
    }

    // 3. 팔로잉 목록 조회
    @GetMapping("/{userId}/following")
    @Operation(summary = "팔로잉 목록 조회", description = "특정 유저가 팔로우 하는 사람들의 목록을 조회합니다.")
    fun getFollowings(
        @PathVariable userId: Long,
        @LoggedInUser loggedInUser: Long
    ): ApiResponse<List<FollowResponse>> {
        val followings = followService.getFollowings(userId, loggedInUser)

        return ApiResponse.onSuccess(followings)
    }

    // 4. 팔로워 삭제
    @DeleteMapping("/followers/{followerId}")
    @Operation(summary = "팔로워 삭제 (강제 언팔)", description = "내 팔로워 목록에서 특정 유저를 삭제합니다. (상대방의 팔로잉 목록에서 내가 사라짐)")
    fun deleteFollower(
        @PathVariable followerId: Long,
        @LoggedInUser loggedInUser: Long
    ): ApiResponse<Unit> {
        followService.deleteFollower(loggedInUser, followerId)

        return ApiResponse.onSuccess(Unit)
    }
}
