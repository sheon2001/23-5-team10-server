package com.team10.instagram.domain.follow.controller

import com.team10.instagram.domain.follow.dto.FollowResponse
import com.team10.instagram.domain.follow.service.FollowService
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
    ): ApiResponse<String> {
        // TODO() : 현재 로그인 한 유저 ID 가져오기
        val currentUserId = 1L
        val message = followService.toggleFollow(currentUserId, toUserId)

        return ApiResponse.onSuccess(message)
    }

    // 2. 팔로워 목록 조회
    @GetMapping("/{userId}/follower")
    @Operation(summary = "팔로워 목록 조회", description = "특정 유저를 팔로우 하는 사람들의 목록을 조회합니다.")
    fun getFollowers(
        @PathVariable userId: Long,
    ): ApiResponse<List<FollowResponse>> {
        // TODO() : 현재 로그인 한 유저 ID 가져오기
        val currentUserId = 1L
        val followers = followService.getFollowers(userId, currentUserId)

        return ApiResponse.onSuccess(followers)
    }

    // 3. 팔로잉 목록 조회
    @GetMapping("/{userId}/following")
    @Operation(summary = "팔로잉 목록 조회", description = "특정 유저가 팔로우 하는 사람들의 목록을 조회합니다.")
    fun getFollowings(
        @PathVariable userId: Long,
    ): ApiResponse<List<FollowResponse>> {
        // TODO() : 현재 로그인 한 유저 ID 가져오기
        val currentUserId = 1L
        val followings = followService.getFollowings(userId, currentUserId)

        return ApiResponse.onSuccess(followings)
    }

    // 4. 팔로워 삭제
    @DeleteMapping("/followers/{followerId}")
    @Operation(summary = "팔로워 삭제 (강제 언팔)", description = "내 팔로워 목록에서 특정 유저를 삭제합니다. (상대방의 팔로잉 목록에서 내가 사라짐)")
    fun deleteFollower(
        @PathVariable followerId: Long,
    ): ApiResponse<Unit> {
        // TODO() : 현재 로그인 한 유저 ID 가져오기
        val currentUserId = 1L
        followService.deleteFollower(currentUserId, followerId)

        return ApiResponse.onSuccess(Unit)
    }
}
