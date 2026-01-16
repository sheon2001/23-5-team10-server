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

    // JPA와 달리 객체(User)가 아닌 ID(Long)를 저장합니다.
    @Column("user_id")
    val userId: Long,

    @Column("album_id")
    val albumId: Long? = null,

    val content: String,

    // 1:N 관계 매핑. post_id 컬럼을 기준으로 매핑하며, List의 인덱스를 sort_order 컬럼에 매핑합니다.
    @MappedCollection(idColumn = "post_id", keyColumn = "sort_order")
    val images: List<PostImage> = emptyList(),

    @CreatedDate
    val createdAt: LocalDateTime? = null,

    @LastModifiedDate
    val updatedAt: LocalDateTime? = null
)