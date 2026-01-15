package com.team10.instagram.domain.follow.service

import com.team10.instagram.domain.follow.dto.FollowResponse
import com.team10.instagram.domain.follow.repository.FollowRepository
import com.team10.instagram.global.error.CustomException
import com.team10.instagram.global.error.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FollowService(
    private val followRepository: FollowRepository,
) {
    // 1. 팔로우 <-> 언팔로우 전환
    @Transactional
    fun toggleFollow(
        fromUserId: Long,
        toUserId: Long,
    ): String {
        if (fromUserId == toUserId) throw CustomException(ErrorCode.SELF_FOLLOW_NOT_ALLOWED)

        val isFollowing = followRepository.exists(fromUserId, toUserId)

        return if (isFollowing) {
            followRepository.delete(fromUserId, toUserId)
            "언팔로우 되었습니다."
        } else {
            followRepository.save(fromUserId, toUserId)
            "팔로우 되었습니다."
        }
    }

    // 2. 팔로워 목록 조회
    fun getFollowers(
        targetUserId: Long,
        loginUserId: Long,
    ): List<FollowResponse> = followRepository.findAllFollowers(targetUserId, loginUserId)

    // 3. 팔로잉 목록 조회
    fun getFollowings(
        targetUserId: Long,
        loginUserId: Long,
    ): List<FollowResponse> = followRepository.findAllFollowings(targetUserId, loginUserId)

    // 4. 팔로워 삭제 (강제 언팔)
    @Transactional
    fun deleteFollower(
        myUserId: Long,
        followerId: Long,
    ) {
        // DELETE FROM follow WHERE from = follower AND to = me
        followRepository.delete(followerId, myUserId)
    }
}
