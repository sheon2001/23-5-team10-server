package com.team10.instagram.domain.album.dto

data class AlbumCreateRequest(
    val title: String,
)

data class AlbumResponse(
    val albumId: Long,
    val title: String,
    val thumbnailImageUrl: String?, // 앨범 대표 이미지 (없으면 null)
    val postCount: Int,
)

data class AlbumDetailResponse(
    val albumId: Long,
    val title: String,
    val posts: List<AlbumPostDto>,
)

data class AlbumPostDto(
    val postId: Long,
    val imageUrl: String,
)
