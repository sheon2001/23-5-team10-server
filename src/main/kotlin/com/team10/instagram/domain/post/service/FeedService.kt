package com.team10.instagram.domain.post.service

import com.team10.instagram.domain.comment.repository.CommentRepository
import com.team10.instagram.domain.follow.repository.FollowRepository
import com.team10.instagram.domain.post.dto.FeedAuthorDto
import com.team10.instagram.domain.post.dto.FeedPostDto
import com.team10.instagram.domain.post.dto.FeedResponse
import com.team10.instagram.domain.post.repository.BookmarkRepository
import com.team10.instagram.domain.post.repository.PostLikeRepository
import com.team10.instagram.domain.post.repository.PostRepository
import com.team10.instagram.domain.user.model.User
import com.team10.instagram.domain.user.repository.UserRepository
import com.team10.instagram.global.error.CustomException
import com.team10.instagram.global.error.ErrorCode
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.math.ceil

@Service
class FeedService(
    private val postRepository: PostRepository,
    private val followRepository: FollowRepository,
    private val userRepository: UserRepository,
    private val postLikeRepository: PostLikeRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val commentRepository: CommentRepository,
) {
    @Transactional(readOnly = true)
    fun getFeed(
        user: User,
        page: Int,
        size: Int,
    ): FeedResponse {
        val pageIndex = if (page < 1) 1 else page
        val offset = (pageIndex - 1) * size.toLong()

        // 1. 내가 팔로우하는 유저 ID 목록 조회
        val followingIds = followRepository.findAllFollowingIds(user.userId!!)

        // 2. 팔로우한 사람이 없으면 빈 결과 반환
        if (followingIds.isEmpty()) {
            return FeedResponse(
                items = emptyList(),
                page = pageIndex,
                size = size,
                totalPages = 0,
                totalElements = 0,
                hasNext = false,
                hasPrev = false,
            )
        }

        // 3. 게시글 조회
        val posts = postRepository.findAllByUserIdsIn(followingIds, size, offset)
        val totalElements = postRepository.countByUserIdsIn(followingIds)
        val totalPages = ceil(totalElements.toDouble() / size).toInt()

        // 4. DTO 변환
        val items =
            posts.map { post ->
                val author =
                    userRepository.findByIdOrNull(post.userId)
                        ?: throw CustomException(ErrorCode.USER_NOT_FOUND)

                val likeCount = postLikeRepository.countByPostId(post.id!!)
                val commentCount = commentRepository.countByPostId(post.id)
                val isLiked = postLikeRepository.existsByPostIdAndUserId(post.id, user.userId)
                val isBookmarked = bookmarkRepository.existsByPostIdAndUserId(post.id, user.userId)

                val thumbnail = post.images.firstOrNull()?.imageUrl

                FeedPostDto(
                    postId = post.id,
                    author =
                        FeedAuthorDto(
                            userId = author.userId!!,
                            nickname = author.nickname,
                            profileImageUrl = author.profileImageUrl,
                        ),
                    thumbnailImageUrl = thumbnail,
                    likeCount = likeCount,
                    commentCount = commentCount,
                    createdAt = post.createdAt ?: LocalDateTime.now(),
                    liked = isLiked,
                    bookmarked = isBookmarked,
                )
            }

        return FeedResponse(
            items = items,
            page = pageIndex,
            size = size,
            totalPages = totalPages,
            totalElements = totalElements,
            hasNext = pageIndex < totalPages,
            hasPrev = pageIndex > 1,
        )
    }
}
