package com.team10.instagram.domain.follow.controller

import com.team10.instagram.domain.follow.dto.FollowResponse
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
class FollowController {
    // 1. 팔로우 / 언팔로우
    @PostMapping("/{toUserId}")
    @Operation(summary = "팔로우/언팔로우 요청", description = "팔로우 <-> 언팔로우 전환 요청입니다.")
    fun toggleFollow(
        @PathVariable toUserId: Long,
    ): ApiResponse<String> {
        // TODO : 이미 팔로우 중이면 언팔로우, 아니면 팔로우
        // "팔로우됨" 또는 "취소됨"
        return ApiResponse.onSuccess("팔로우/언팔로우 되었습니다.")
    }

    // 2. 팔로워 목록 조회 (나를 팔로우 하는 사람들)
    @GetMapping("/{userId}/follower")
    @Operation(summary = "팔로워 목록 조회", description = "나를 팔로우 하는 사람들의 목록을 조회합니다.")
    fun getFollowers(
        @PathVariable userId: Long,
    ): ApiResponse<List<FollowResponse>> {
        val mockFollowers =
            listOf(
                FollowResponse(2, "user2", "https://dummyimage.com/100", true),
                FollowResponse(3, "user3", null, false),
            )
        return ApiResponse.onSuccess(mockFollowers)
    }

    // 3. 팔로잉 목록 조회 (내가 팔로우 하는 사람들)
    @GetMapping("/{userId}/following")
    @Operation(summary = "팔로잉 목록 조회", description = "내가 팔로우 하는 사람들의 목록을 조회합니다.")
    fun getFollowings(
        @PathVariable userId: Long,
    ): ApiResponse<List<FollowResponse>> {
        val mockFollowings =
            listOf(
                FollowResponse(5, "celebrity", "https://dummyimage.com/100", true),
            )
        return ApiResponse.onSuccess(mockFollowings)
    }

    // 4. 팔로워 삭제
    @DeleteMapping("/followers/{followerId}")
    @Operation(summary = "팔로워 삭제 (강제 언팔)", description = "내 팔로워 목록에서 특정 유저를 삭제합니다. (상대방의 팔로잉 목록에서 내가 사라짐)")
    fun deleteFollower(
        @PathVariable followerId: Long,
    ): ApiResponse<Unit> {
        // DELETE FROM follow WHERE from_user_id = {followerId} AND to_user_id = {나}
        println("유저($followerId)를 내 팔로워 목록에서 삭제했습니다.")
        return ApiResponse.onSuccess(Unit)
    }
}
