package com.team10.instagram.domain.search.repository

import com.team10.instagram.domain.search.model.Search
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface SearchHistoryRepository : CrudRepository<Search, Long> {
    fun findAllByFromUserId(fromUserId: Long): List<Search>?

    @Query(
        """
            SELECT * FROM search_history sh
            WHERE sh.deleted_at IS NULL
            AND sh.from_user_id = :fromUserId
            AND sh.created_at = (
                SELECT MAX(sub.created_at)
                FROM search_history sub
                WHERE sub.from_user_id = :fromUserId
                AND sub.to_user_id = sh.to_user_id
                AND sub.deleted_at IS NULL
            )
            ORDER BY sh.created_at DESC
        """,
    )
    fun findRecentByFromUserId(
        @Param("fromUserId") fromUserId: Long,
    ): List<Search>?

    @Modifying
    @Query(
        """
            UPDATE search_history
            SET deleted_at = :deletedAt
            WHERE from_user_id = :fromUserId
            AND to_user_id = :toUserId
            AND deleted_at IS NULL
        """,
    )
    fun markDeleted(
        @Param("fromUserId") fromUserId: Long,
        @Param("toUserId") toUserId: Long,
        @Param("deletedAt") deletedAt: LocalDateTime = LocalDateTime.now(),
    )
}
