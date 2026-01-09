package com.team10.instagram.domain.follow.dto

data class FollowResponse(
    val userId: Long,
    val nickname: String,
    val profileImageUrl: String?,
    // 내가 이 사람을 팔로우 중인지 여부
    val isFollowing: Boolean,
)

// class 하나일 경우 파일 이름과 class 이름이 같아야 해서, 임시로 추가
data class FollowRequest(
    val toUserId: Long,
)
