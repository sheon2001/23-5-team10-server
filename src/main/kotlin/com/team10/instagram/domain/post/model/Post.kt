package com.team10.instagram.domain.post.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("post")
data class Post(
    @Id
    @Column("post_id")
    val id: Long? = null,
    @Column("user_id")
    val userId: Long,
    @Column("album_id")
    val albumId: Long? = null,
    val content: String,
    // 1:N Mapping with images (PostImage)
    @MappedCollection(idColumn = "post_id", keyColumn = "sort_order")
    val images: List<PostImage> = emptyList(),
    @CreatedDate
    val createdAt: LocalDateTime? = null,
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,
)
