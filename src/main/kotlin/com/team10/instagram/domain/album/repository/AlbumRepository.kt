package com.team10.instagram.domain.album.repository

import com.team10.instagram.domain.album.dto.AlbumPostDto
import com.team10.instagram.domain.album.dto.AlbumResponse
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class AlbumRepository(
    private val jdbcTemplate: JdbcTemplate,
) {
    // 1. 앨범 생성
    fun save(
        userId: Long,
        title: String,
    ): Long {
        val sql = "INSERT INTO album (user_id, title) VALUES (?, ?)"
        jdbcTemplate.update(sql, userId, title)
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long::class.java)!!
    }

    // 2. 내 앨범 목록 조회 (썸네일 포함)
    fun findAllByUserId(userId: Long): List<AlbumResponse> {
        val sql = """
            SELECT a.album_id, a.title,
                   -- 해당 앨범에 속한 게시글 개수
                   (SELECT COUNT(*) FROM post p WHERE p.album_id = a.album_id) as post_count,
                   -- 앨범 썸네일 (가장 최근 게시글의 첫 번째 이미지)
                   (SELECT pi.image_url 
                    FROM post p 
                    JOIN post_image pi ON p.post_id = pi.post_id 
                    WHERE p.album_id = a.album_id 
                    ORDER BY p.created_at DESC LIMIT 1) as thumbnail_image_url
            FROM album a
            WHERE a.user_id = ?
            ORDER BY a.created_at DESC, a.album_id DESC
        """

        return jdbcTemplate.query(sql, albumResponseMapper, userId)
    }

    // 3. 앨범 단건 조회 (제목 등 기본 정보) -> 검증용
    fun findById(albumId: Long): AlbumResponse? {
        val sql = "SELECT album_id, title, user_id FROM album WHERE album_id = ?"
        return try {
            jdbcTemplate.queryForObject(
                sql,
                { rs, _ ->
                    AlbumResponse(
                        albumId = rs.getLong("album_id"),
                        title = rs.getString("title"),
                        thumbnailImageUrl = null,
                        postCount = 0,
                    )
                },
                albumId,
            )
        } catch (e: Exception) {
            null
        }
    }

    // 3-1. 앨범 소유자 확인용 (User ID 조회)
    fun findOwnerId(albumId: Long): Long? {
        val sql = "SELECT user_id FROM album WHERE album_id = ?"
        return try {
            jdbcTemplate.queryForObject(sql, Long::class.java, albumId)
        } catch (e: Exception) {
            null
        }
    }

    // 4. 앨범 내부 게시글 목록 조회
    fun findPostsByAlbumId(albumId: Long): List<AlbumPostDto> {
        val sql = """
            SELECT p.post_id,
                   (SELECT pi.image_url 
                    FROM post_image pi 
                    WHERE pi.post_id = p.post_id 
                    ORDER BY pi.sort_order ASC
                    LIMIT 1) as image_url
            FROM post p
            WHERE p.album_id = ?
            ORDER BY p.created_at DESC
        """

        return jdbcTemplate.query(
            sql,
            { rs, _ ->
                AlbumPostDto(
                    postId = rs.getLong("post_id"),
                    imageUrl = rs.getString("image_url"),
                )
            },
            albumId,
        )
    }

    // 5. 앨범 제목 수정
    fun updateTitle(
        albumId: Long,
        title: String,
    ) {
        val sql = "UPDATE album SET title = ? WHERE album_id = ?"
        jdbcTemplate.update(sql, title, albumId)
    }

    // 6. 게시글을 앨범에 추가/이동 (UPDATE)
    fun updatePostAlbum(
        postId: Long,
        albumId: Long?,
    ) {
        val sql = "UPDATE post SET album_id = ? WHERE post_id = ?"
        jdbcTemplate.update(sql, albumId, postId)
    }

    // 7. 앨범 삭제 전, 게시글 연결 끊기 (album_id = NULL)
    fun detachPostsFromAlbum(albumId: Long) {
        val sql = "UPDATE post SET album_id = NULL WHERE album_id = ?"
        jdbcTemplate.update(sql, albumId)
    }

    // 8. 앨범 삭제
    fun delete(albumId: Long) {
        val sql = "DELETE FROM album WHERE album_id = ?"
        jdbcTemplate.update(sql, albumId)
    }

    // 9.
    fun existsByUserIdAndTitle(
        userId: Long,
        title: String,
    ): Boolean {
        val sql = "SELECT COUNT(*) FROM album WHERE user_id = ? AND title = ?"
        val count = jdbcTemplate.queryForObject(sql, Int::class.java, userId, title) ?: 0
        return count > 0
    }

    private val albumResponseMapper =
        RowMapper { rs: ResultSet, _: Int ->
            AlbumResponse(
                albumId = rs.getLong("album_id"),
                title = rs.getString("title"),
                postCount = rs.getInt("post_count"),
                thumbnailImageUrl = rs.getString("thumbnail_image_url"),
            )
        }
}
