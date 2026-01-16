package com.team10.instagram.domain.comment.repository

import com.team10.instagram.domain.comment.model.Comment
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface CommentRepository : CrudRepository<Comment, Long> {
    @Query("SELECT * FROM comment WHERE post_id = :postId ORDER BY created_at DESC")
    fun findAllByPostIdOrderByCreatedAtDesc(@Param("postId") postId: Long): List<Comment>

    @Query("SELECT COUNT(*) FROM comment WHERE post_id = :postId")
    fun countByPostId(@Param("postId") postId: Long): Long
}