package com.team10.instagram.domain.comment.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("comment")
data class Comment(
    @Id
    @Column("comment_id")
    val id: Long? = null,
    @Column("post_id")
    val postId: Long,
    @Column("user_id")
    val userId: Long,
    val content: String,
    @CreatedDate
    val createdAt: LocalDateTime? = null,
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,
)
