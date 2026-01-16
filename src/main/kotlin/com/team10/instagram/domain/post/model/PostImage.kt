package com.team10.instagram.domain.post.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("post_image")
data class PostImage(
    @Id
    val id: Long? = null,
    val imageUrl: String,
)
