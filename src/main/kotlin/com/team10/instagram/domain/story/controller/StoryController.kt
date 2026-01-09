package com.team10.instagram.domain.story.controller

import com.team10.instagram.domain.story.dto.StoryCreateRequest
import com.team10.instagram.domain.story.dto.StoryDetailResponse
import com.team10.instagram.domain.story.dto.StoryFeedResponse
import com.team10.instagram.global.common.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/stories")
@Tag(name = "Story API", description = "스토리 관련 API")
class StoryController {
    // 1. 스토리 업로드
    @PostMapping
    @Operation(summary = "스토리 업로드", description = "이미지를 업로드하여 스토리를 생성합니다. (24시간 뒤 만료 로직은 조회 시 처리)")
    fun createStory(
        @RequestBody request: StoryCreateRequest,
    ): ApiResponse<Long> {
        // Story 테이블에 INSERT.
        return ApiResponse.onSuccess(100L)
    }

    // 2. 스토리 피드 조회 (메인화면 상단)
    @GetMapping("/feed")
    @Operation(summary = "스토리 피드 조회 (상단 바)", description = "내가 팔로우한 사람 중 24시간 이내에 스토리를 올린 유저 목록을 조회합니다.")
    fun getStoryFeed(): ApiResponse<List<StoryFeedResponse>> {
        // 1. 내가 팔로우한 사람(Follow Table)을 다 찾는다.
        // 2. 그 사람들이 올린 Story 중 created_at이 24시간 이내인 것이 존재하는지 확인한다.
        // 3. 존재하면 리스트에 담아서 리턴.
        val mockFeed =
            listOf(
                StoryFeedResponse(2, "friend_user", "https://dummyimage.com/100", true),
                StoryFeedResponse(3, "best_friend", "https://dummyimage.com/100", false),
            )
        return ApiResponse.onSuccess(mockFeed)
    }

    // 3. 스토리 조회
    @GetMapping("/user/{userId}")
    @Operation(summary = "스토리 상세 조회", description = "스토리를 조회합니다. (내 스토리면 조회수 포함, 아니면 null)")
    fun getUserStories(
        @PathVariable userId: Long,
    ): ApiResponse<List<StoryDetailResponse>> {
        // 현재 로그인한 내 ID가 1(me)이라고 가정
        val currentUserId = 1L
        // 1. 남의 스토리를 보는 경우 -> viewCount = null
        if (userId != currentUserId) {
            val otherStories =
                listOf(
                    StoryDetailResponse(10, "https://dummy.com/friend1.jpg", LocalDateTime.now().minusHours(1), null), // null 입력
                    StoryDetailResponse(11, "https://dummy.com/friend2.jpg", LocalDateTime.now().minusMinutes(30), null),
                )
            return ApiResponse.onSuccess(otherStories)
        }

        // 2. 내 스토리를 보는 경우 viewCount != null
        else {
            val myStories =
                listOf(
                    StoryDetailResponse(20, "https://dummy.com/my1.jpg", LocalDateTime.now().minusHours(2), 52),
                    StoryDetailResponse(21, "https://dummy.com/my2.jpg", LocalDateTime.now().minusMinutes(10), 3),
                )
            return ApiResponse.onSuccess(myStories)
        }
    }

    // 4. 스토리 삭제
    @DeleteMapping("/{storyId}")
    @Operation(summary = "스토리 삭제", description = "내 스토리를 삭제합니다.")
    fun deleteStory(
        @PathVariable storyId: Long,
    ): ApiResponse<Unit> = ApiResponse.onSuccess(Unit)
}
