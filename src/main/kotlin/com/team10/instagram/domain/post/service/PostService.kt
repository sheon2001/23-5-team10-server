package com.team10.instagram.domain.post.service

import com.team10.instagram.domain.comment.repository.CommentRepository
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
) {
    @Transactional
    fun create(
        user: User,
        request: PostCreateRequest,
    ): PostResponse {
        val images = request.imageUrls.map { PostImage(imageUrl = it) }

        val post =
            Post(
                userId = user.id!!,
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

    @Transactional(readOnly = true)
    fun getBookmarkedPosts(user: User): List<PostResponse> {
        val bookmarks = bookmarkRepository.findAllByUserId(user.id!!)
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

        if (post.userId != user.id) throw CustomException(ErrorCode.INVALID_INPUT_VALUE)

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

        if (post.userId != user.id) throw CustomException(ErrorCode.INVALID_INPUT_VALUE)

        postRepository.delete(post)
    }

    @Transactional
    fun likePost(
        user: User,
        postId: Long,
    ) {
        if (!postRepository.existsById(postId)) throw CustomException(ErrorCode.POST_NOT_FOUND)

        if (!postLikeRepository.existsByPostIdAndUserId(postId, user.id!!)) {
            postLikeRepository.save(PostLike(postId = postId, userId = user.id))
        }
    }

    @Transactional
    fun unlikePost(
        user: User,
        postId: Long,
    ) {
        if (!postRepository.existsById(postId)) throw CustomException(ErrorCode.POST_NOT_FOUND)

        val like = postLikeRepository.findByPostIdAndUserId(postId, user.id!!)
        if (like != null) postLikeRepository.delete(like)
    }

    @Transactional
    fun bookmarkPost(
        user: User,
        postId: Long,
    ) {
        if (!postRepository.existsById(postId)) throw CustomException(ErrorCode.POST_NOT_FOUND)

        if (!bookmarkRepository.existsByPostIdAndUserId(postId, user.id!!)) {
            bookmarkRepository.save(Bookmark(postId = postId, userId = user.id))
        }
    }

    @Transactional
    fun unBookmarkPost(
        user: User,
        postId: Long,
    ) {
        if (!postRepository.existsById(postId)) throw CustomException(ErrorCode.POST_NOT_FOUND)

        val bookmark = bookmarkRepository.findByPostIdAndUserId(postId, user.id!!)
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

        val isLiked = currentUser?.let { postLikeRepository.existsByPostIdAndUserId(post.id, it.id!!) } ?: false
        val isBookmarked = currentUser?.let { bookmarkRepository.existsByPostIdAndUserId(post.id, it.id!!) } ?: false

        return PostResponse(
            id = post.id,
            userId = author.id!!,
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
