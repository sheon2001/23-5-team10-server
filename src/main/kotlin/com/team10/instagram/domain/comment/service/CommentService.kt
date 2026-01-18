package com.team10.instagram.domain.comment.service

import com.team10.instagram.domain.comment.dto.CommentCreateRequest
import com.team10.instagram.domain.comment.dto.CommentResponse
import com.team10.instagram.domain.comment.dto.CommentUpdateRequest
import com.team10.instagram.domain.comment.model.Comment
import com.team10.instagram.domain.comment.repository.CommentRepository
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
class CommentService(
    private val commentRepository: CommentRepository,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
) {
    @Transactional
    fun create(
        user: User,
        postId: Long,
        request: CommentCreateRequest,
    ): CommentResponse {
        if (!postRepository.existsById(postId)) {
            throw CustomException(ErrorCode.POST_NOT_FOUND)
        }

        if (request.content.isBlank()) {
            throw CustomException(ErrorCode.EMPTY_CONTENT)
        }

        val comment =
            Comment(
                postId = postId,
                userId = user.userId!!,
                content = request.content,
            )

        val savedComment = commentRepository.save(comment)
        return convertToDto(savedComment, user)
    }

    @Transactional(readOnly = true)
    fun getCommentsByPostId(postId: Long): List<CommentResponse> {
        if (!postRepository.existsById(postId)) {
            throw CustomException(ErrorCode.POST_NOT_FOUND)
        }

        val comments = commentRepository.findAllByPostIdOrderByCreatedAtDesc(postId)
        return comments.map { comment ->
            val writer =
                userRepository.findByIdOrNull(comment.userId)
                    ?: throw CustomException(ErrorCode.USER_NOT_FOUND)
            convertToDto(comment, writer)
        }
    }

    @Transactional
    fun update(
        user: User,
        commentId: Long,
        request: CommentUpdateRequest,
    ): CommentResponse {
        val comment =
            commentRepository.findByIdOrNull(commentId)
                ?: throw CustomException(ErrorCode.INVALID_INPUT_VALUE)

        if (comment.userId != user.userId) throw CustomException(ErrorCode.INVALID_INPUT_VALUE)

        if (request.content.isBlank()) {
            throw CustomException(ErrorCode.EMPTY_CONTENT)
        }

        val updatedComment = comment.copy(content = request.content)
        val saved = commentRepository.save(updatedComment)

        return convertToDto(saved, user)
    }

    @Transactional
    fun delete(
        user: User,
        commentId: Long,
    ) {
        val comment =
            commentRepository.findByIdOrNull(commentId)
                ?: throw CustomException(ErrorCode.INVALID_INPUT_VALUE)

        if (comment.userId != user.userId) throw CustomException(ErrorCode.INVALID_INPUT_VALUE)

        commentRepository.delete(comment)
    }

    private fun convertToDto(
        comment: Comment,
        writer: User,
    ): CommentResponse =
        CommentResponse(
            id = comment.id!!,
            postId = comment.postId,
            userId = writer.userId!!,
            nickname = writer.nickname,
            profileImageUrl = writer.profileImageUrl,
            content = comment.content,
            createdAt = comment.createdAt ?: LocalDateTime.now(),
            updatedAt = comment.updatedAt ?: LocalDateTime.now(),
        )
}
