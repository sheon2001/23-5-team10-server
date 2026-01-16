package com.team10.instagram.domain.post.repository

import com.team10.instagram.domain.post.model.Post
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository

interface PostRepository : CrudRepository<Post, Long> {
    @Query("SELECT * FROM post ORDER BY created_at DESC")
    fun findAllByOrderByCreatedAtDesc(): List<Post>
}
