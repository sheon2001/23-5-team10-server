package com.team10.instagram.domain.follow.dto

data class FollowResponse(
    val userId: Long,
    val nickname: String,
    val profileImageUrl: String?,
    // 내가 이 사람을 팔로우 중인지 여부
    val isFollowing: Boolean,
)
