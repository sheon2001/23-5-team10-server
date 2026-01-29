package com.team10.instagram.domain.search.model
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("search_history")
data class Search(
    @Id
    @Column("search_id")
    val searchId: Long? = null,
    @Column("from_user_id")
    val fromUserId: Long,
    @Column("to_user_id")
    val toUserId: Long,
    @Column("created_at")
    val createdAt: LocalDateTime? = null,
    @Column("deleted_at")
    val deletedAt: LocalDateTime? = null,
)
