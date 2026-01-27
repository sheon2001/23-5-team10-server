package com.team10.instagram.domain.story.controller

import com.team10.instagram.domain.story.dto.StoryCreateRequest
import com.team10.instagram.domain.story.dto.StoryDetailResponse
import com.team10.instagram.domain.story.dto.StoryFeedResponse
import com.team10.instagram.domain.story.service.StoryService
import com.team10.instagram.domain.user.LoggedInUser
import com.team10.instagram.global.common.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/stories")
@Tag(name = "Story API", description = "스토리 관련 API")
class StoryController(
    private val storyService: StoryService,
) {
    // 1. 스토리 업로드
    @PostMapping
    @Operation(summary = "스토리 업로드", description = "이미지를 업로드하여 스토리를 생성합니다. (24시간 뒤 만료 로직은 조회 시 처리)")
    fun createStory(
        @LoggedInUser loggedInUser: Long,
        @Valid @RequestBody request: StoryCreateRequest,
    ): ApiResponse<Unit> {
        storyService.createStory(loggedInUser, request)
        return ApiResponse.onSuccess(Unit)
    }

    // 2. 스토리 피드 조회 (메인화면 상단)
    @GetMapping("/feed")
    @Operation(summary = "스토리 피드 조회 (상단 바)", description = "내가 팔로우한 사람 중 24시간 이내에 스토리를 올린 유저 목록을 조회합니다.")
    fun getStoryFeed(
        @LoggedInUser loggedInUser: Long,
    ): ApiResponse<List<StoryFeedResponse>> {
        val result = storyService.getStoryFeed(loggedInUser)
        return ApiResponse.onSuccess(result)
    }

    // 3. 스토리 조회
    @GetMapping("/user/{userId}")
    @Operation(summary = "스토리 상세 조회", description = "스토리를 조회합니다. (내 스토리면 조회수 포함, 아니면 null)")
    fun getUserStories(
        @LoggedInUser loggedInUser: Long,
        @PathVariable userId: Long,
    ): ApiResponse<List<StoryDetailResponse>> {
        val result = storyService.getUserStories(loggedInUser, userId)
        return ApiResponse.onSuccess(result)
    }

    // 4. 스토리 삭제
    @DeleteMapping("/{storyId}")
    @Operation(summary = "스토리 삭제", description = "내 스토리를 삭제합니다.")
    fun deleteStory(
        @LoggedInUser loggedInUser: Long,
        @PathVariable storyId: Long,
    ): ApiResponse<Unit> {
        storyService.deleteStory(loggedInUser, storyId)
        return ApiResponse.onSuccess(Unit)
    }
}
