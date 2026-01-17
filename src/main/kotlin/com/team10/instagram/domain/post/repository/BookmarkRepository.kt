package com.team10.instagram.domain.post.repository

import com.team10.instagram.domain.post.model.Bookmark
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface BookmarkRepository : CrudRepository<Bookmark, Long> {
    @Query("SELECT COUNT(*) > 0 FROM bookmark WHERE post_id = :postId AND user_id = :userId")
    fun existsByPostIdAndUserId(
        @Param("postId") postId: Long,
        @Param("userId") userId: Long,
    ): Boolean

    @Query("SELECT * FROM bookmark WHERE post_id = :postId AND user_id = :userId")
    fun findByPostIdAndUserId(
        @Param("postId") postId: Long,
        @Param("userId") userId: Long,
    ): Bookmark?

    @Query("SELECT * FROM bookmark WHERE user_id = :userId ORDER BY created_at DESC")
    fun findAllByUserId(
        @Param("userId") userId: Long,
    ): List<Bookmark>
}
