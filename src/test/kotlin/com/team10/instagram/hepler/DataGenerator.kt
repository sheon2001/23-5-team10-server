package com.team10.instagram.helper

import com.team10.instagram.domain.auth.jwt.JwtTokenProvider
import com.team10.instagram.domain.comment.model.Comment
import com.team10.instagram.domain.comment.repository.CommentRepository
import com.team10.instagram.domain.follow.repository.FollowRepository
import com.team10.instagram.domain.post.model.Post
import com.team10.instagram.domain.post.model.PostImage
import com.team10.instagram.domain.post.repository.PostRepository
import com.team10.instagram.domain.user.model.User
import com.team10.instagram.domain.user.repository.UserRepository
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class DataGenerator(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val followRepository: FollowRepository,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    // Generate User
    fun generateUser(
        email: String? = null,
        nickname: String? = null,
    ): User {
        val randomStr = Random.nextInt(1000000).toString()
        return userRepository.save(
            User(
                email = email ?: "user$randomStr@example.com",
                nickname = nickname ?: "nick$randomStr",
                password = "password",
                profileImageUrl = "https://dummyimage.com/100",
            ),
        )
    }

    // Generate a Jwt token for user
    fun generateToken(user: User): String = jwtTokenProvider.createAccessToken(user.userId!!)

    // Generate single post
    fun generatePost(
        user: User,
        content: String? = null,
        images: List<String> = listOf("http://img1.com"),
    ): Post {
        val postImages = images.map { PostImage(imageUrl = it) }
        return postRepository.save(
            Post(
                userId = user.userId!!,
                content = content ?: "테스트 게시글입니다.",
                images = postImages,
            ),
        )
    }

    // Generate single comment
    fun generateComment(
        post: Post,
        user: User,
        content: String? = null,
    ): Comment =
        commentRepository.save(
            Comment(
                postId = post.id!!,
                userId = user.userId!!,
                content = content ?: "테스트 댓글입니다.",
            ),
        )

    // Generate follow relation
    fun generateFollow(
        fromUser: User,
        toUser: User,
    ) {
        followRepository.save(
            fromUserId = fromUser.userId!!,
            toUserId = toUser.userId!!,
        )
    }
}
