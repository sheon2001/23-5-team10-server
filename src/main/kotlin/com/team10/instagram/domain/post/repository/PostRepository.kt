package com.team10.instagram.domain.post.repository

import com.team10.instagram.domain.post.model.Post
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface PostRepository : CrudRepository<Post, Long> {
    @Query("SELECT * FROM post ORDER BY created_at DESC")
    fun findAllByOrderByCreatedAtDesc(): List<Post>

    @Query(
        """
        SELECT * FROM post 
        WHERE user_id IN (:userIds) 
        ORDER BY created_at DESC 
        LIMIT :limit OFFSET :offset
    """,
    )
    fun findAllByUserIdsIn(
        @Param("userIds") userIds: List<Long>,
        @Param("limit") limit: Int,
        @Param("offset") offset: Long,
    ): List<Post>

    @Query("SELECT COUNT(*) FROM post WHERE user_id IN (:userIds)")
    fun countByUserIdsIn(
        @Param("userIds") userIds: List<Long>,
    ): Long
}
