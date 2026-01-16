package com.team10.instagram.domain.post.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("bookmark")
data class Bookmark(
    @Id
    val id: Long? = null,
    @Column("post_id")
    val postId: Long,
    @Column("user_id")
    val userId: Long,
    @CreatedDate
    val createdAt: LocalDateTime? = null
)