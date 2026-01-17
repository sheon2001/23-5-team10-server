package com.team10.instagram.domain.post.service

import com.team10.instagram.domain.comment.repository.CommentRepository
import com.team10.instagram.domain.follow.repository.FollowRepository
import com.team10.instagram.domain.post.dto.PostCreateRequest
import com.team10.instagram.domain.post.dto.PostImageResponse
import com.team10.instagram.domain.post.dto.PostResponse
import com.team10.instagram.domain.post.dto.PostUpdateRequest
import com.team10.instagram.domain.post.model.Bookmark
import com.team10.instagram.domain.post.model.Post
import com.team10.instagram.domain.post.model.PostImage
import com.team10.instagram.domain.post.model.PostLike
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

@Service
class PostService(
    private val postRepository: PostRepository,
    private val postLikeRepository: PostLikeRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
    private val followRepository: FollowRepository,
) {
    @Transactional
    fun create(
        user: User,
        request: PostCreateRequest,
    ): PostResponse {
        val images = request.imageUrls.map { PostImage(imageUrl = it) }

        val post =
            Post(
                userId = user.userId,
                content = request.content,
                albumId = request.albumId,
                images = images,
            )

        val savedPost = postRepository.save(post)
        return convertToDto(savedPost, user)
    }

    @Transactional(readOnly = true)
    fun get(
        user: User,
        postId: Long,
    ): PostResponse {
        val post =
            postRepository.findByIdOrNull(postId)
                ?: throw CustomException(ErrorCode.POST_NOT_FOUND)
        return convertToDto(post, user)
    }

    @Transactional(readOnly = true)
    fun search(user: User?): List<PostResponse> {
        val posts = postRepository.findAllByOrderByCreatedAtDesc()
        return posts.map { convertToDto(it, user) }
    }

    /*
    @Transactional(readOnly = true)
    fun getFeed(user: User, page: Int, size: Int): FeedResponse {
        val pageIndex = if (page < 1) 1 else page
        val offset = (pageIndex - 1) * size.toLong()

        // Need to be implemented
        val followingIds = followRepository.findAllFollowingIds(user.userId)

        if (followingIds.isEmpty()) {
            return FeedResponse(
                items = emptyList(),
                page = pageIndex,
                size = size,
                totalPages = 0,
                totalElements = 0,
                hasNext = false,
                hasPrev = false
            )
        }

        // 3. Fetch posts
        val posts = postRepository.findAllByUserIdsIn(followingIds, size, offset)
        val totalElements = postRepository.countByUserIdsIn(followingIds)
        val totalPages = ceil(totalElements.toDouble() / size).toInt()

        // 4. Map to DTO
        val items = posts.map { post ->
            val author = userRepository.findByIdOrNull(post.userId)
                ?: throw CustomException(ErrorCode.USER_NOT_FOUND)

            val likeCount = postLikeRepository.countByPostId(post.id!!)
            val commentCount = commentRepository.countByPostId(post.id)
            val isLiked = postLikeRepository.existsByPostIdAndUserId(post.id, user.userId)
            val isBookmarked = bookmarkRepository.existsByPostIdAndUserId(post.id, user.userId)

            // Thumbnail is the first image, or null if no images
            val thumbnail = post.images.firstOrNull()?.imageUrl

            FeedPostDto(
                postId = post.id,
                author = FeedAuthorDto(
                    userId = author.userId,
                    nickname = author.nickname,
                    profileImageUrl = author.profileImageUrl
                ),
                thumbnailImageUrl = thumbnail,
                likeCount = likeCount,
                commentCount = commentCount,
                createdAt = post.createdAt ?: LocalDateTime.now(),
                liked = isLiked,
                bookmarked = isBookmarked
            )
        }

        return FeedResponse(
            items = items,
            page = pageIndex,
            size = size,
            totalPages = totalPages,
            totalElements = totalElements,
            hasNext = pageIndex < totalPages,
            hasPrev = pageIndex > 1
        )
    }
     */

    @Transactional(readOnly = true)
    fun getBookmarkedPosts(user: User): List<PostResponse> {
        val bookmarks = bookmarkRepository.findAllByUserId(user.userId)
        val posts =
            bookmarks.mapNotNull { bookmark ->
                postRepository.findByIdOrNull(bookmark.postId)
            }
        return posts.map { convertToDto(it, user) }
    }

    @Transactional
    fun update(
        user: User,
        postId: Long,
        request: PostUpdateRequest,
    ): PostResponse {
        val post =
            postRepository.findByIdOrNull(postId)
                ?: throw CustomException(ErrorCode.POST_NOT_FOUND)

        if (post.userId != user.userId) throw CustomException(ErrorCode.INVALID_INPUT_VALUE)

        val updatedPost =
            post.copy(
                content = request.content,
                albumId = request.albumId,
            )

        val saved = postRepository.save(updatedPost)
        return convertToDto(saved, user)
    }

    @Transactional
    fun delete(
        user: User,
        postId: Long,
    ) {
        val post =
            postRepository.findByIdOrNull(postId)
                ?: throw CustomException(ErrorCode.POST_NOT_FOUND)

        if (post.userId != user.userId) throw CustomException(ErrorCode.INVALID_INPUT_VALUE)

        postRepository.delete(post)
    }

    @Transactional
    fun likePost(
        user: User,
        postId: Long,
    ) {
        if (!postRepository.existsById(postId)) throw CustomException(ErrorCode.POST_NOT_FOUND)

        if (!postLikeRepository.existsByPostIdAndUserId(postId, user.userId)) {
            postLikeRepository.save(PostLike(postId = postId, userId = user.userId))
        }
    }

    @Transactional
    fun unlikePost(
        user: User,
        postId: Long,
    ) {
        if (!postRepository.existsById(postId)) throw CustomException(ErrorCode.POST_NOT_FOUND)

        val like = postLikeRepository.findByPostIdAndUserId(postId, user.userId)
        if (like != null) postLikeRepository.delete(like)
    }

    @Transactional
    fun bookmarkPost(
        user: User,
        postId: Long,
    ) {
        if (!postRepository.existsById(postId)) throw CustomException(ErrorCode.POST_NOT_FOUND)

        if (!bookmarkRepository.existsByPostIdAndUserId(postId, user.userId)) {
            bookmarkRepository.save(Bookmark(postId = postId, userId = user.userId))
        }
    }

    @Transactional
    fun unBookmarkPost(
        user: User,
        postId: Long,
    ) {
        if (!postRepository.existsById(postId)) throw CustomException(ErrorCode.POST_NOT_FOUND)

        val bookmark = bookmarkRepository.findByPostIdAndUserId(postId, user.userId)
        if (bookmark != null) bookmarkRepository.delete(bookmark)
    }

    private fun convertToDto(
        post: Post,
        currentUser: User?,
    ): PostResponse {
        val author =
            userRepository.findByIdOrNull(post.userId)
                ?: throw CustomException(ErrorCode.USER_NOT_FOUND)

        val likeCount = postLikeRepository.countByPostId(post.id!!)
        val commentCount = commentRepository.countByPostId(post.id)

        val isLiked = currentUser?.let { postLikeRepository.existsByPostIdAndUserId(post.id, it.userId) } ?: false
        val isBookmarked = currentUser?.let { bookmarkRepository.existsByPostIdAndUserId(post.id, it.userId) } ?: false

        return PostResponse(
            id = post.id,
            userId = author.userId,
            nickname = author.nickname,
            profileImageUrl = author.profileImageUrl,
            content = post.content,
            albumId = post.albumId,
            images =
                post.images.mapIndexed { index, img ->
                    PostImageResponse(img.id ?: 0L, img.imageUrl, index)
                },
            likeCount = likeCount,
            commentCount = commentCount,
            isLiked = isLiked,
            isBookmarked = isBookmarked,
            createdAt = post.createdAt ?: LocalDateTime.now(),
            updatedAt = post.updatedAt ?: LocalDateTime.now(),
        )
    }
}
