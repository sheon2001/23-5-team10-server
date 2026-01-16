package com.team10.instagram.domain.post.repository

import com.team10.instagram.domain.post.model.PostLike
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface PostLikeRepository : CrudRepository<PostLike, Long> {
    @Query("SELECT COUNT(*) > 0 FROM post_like WHERE post_id = :postId AND user_id = :userId")
    fun existsByPostIdAndUserId(
        @Param("postId") postId: Long,
        @Param("userId") userId: Long,
    ): Boolean

    @Query("SELECT COUNT(*) FROM post_like WHERE post_id = :postId")
    fun countByPostId(
        @Param("postId") postId: Long,
    ): Long

    @Query("SELECT * FROM post_like WHERE post_id = :postId AND user_id = :userId")
    fun findByPostIdAndUserId(
        @Param("postId") postId: Long,
        @Param("userId") userId: Long,
    ): PostLike?
}
