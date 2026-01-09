package com.team10.instagram.domain.album.controller

import com.team10.instagram.domain.album.dto.AlbumCreateRequest
import com.team10.instagram.domain.album.dto.AlbumDetailResponse
import com.team10.instagram.domain.album.dto.AlbumPostDto
import com.team10.instagram.domain.album.dto.AlbumResponse
import com.team10.instagram.global.common.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/albums")
class AlbumController {
    // 1. 앨범 생성
    @PostMapping
    fun createAlbum(
        @RequestBody request: AlbumCreateRequest,
    ): ApiResponse<Long> = ApiResponse.onSuccess(100L)

    // 2. 내 앨범 목록 조회
    @GetMapping("/my")
    fun getMyAlbums(): ApiResponse<List<AlbumResponse>> {
        val mockAlbums =
            listOf(
                AlbumResponse(1, "제주도 여행", "https://dummyimage.com/600x400/000/fff", 5),
                AlbumResponse(2, "맛집 리스트", "https://dummyimage.com/600x400/ff0000/fff", 12),
            )
        return ApiResponse.onSuccess(mockAlbums)
    }

    // 3. 앨범 상세 조회 (게시글 목록)
    @GetMapping("/{albumId}")
    fun getAlbumDetail(
        @PathVariable albumId: Long,
    ): ApiResponse<AlbumDetailResponse> {
        val mockPosts =
            listOf(
                AlbumPostDto(101, "https://dummyimage.com/600x400/000/fff"),
                AlbumPostDto(102, "https://dummyimage.com/600x400/111/fff"),
            )
        return ApiResponse.onSuccess(
            AlbumDetailResponse(albumId, "제주도 여행", mockPosts),
        )
    }

    // 4. 앨범에 게시글 추가 또는 이동
    @PostMapping("/{albumId}/posts/{postId}")
    @Operation(
        summary = "앨범에 게시글 추가 (이동)",
        description = "게시글을 특정 앨범에 담습니다. 만약 이미 다른 앨범에 있던 글이라면, 새로운 앨범으로 이동됩니다.",
    )
    fun addPostToAlbum(
        @PathVariable albumId: Long,
        @PathVariable postId: Long,
    ): ApiResponse<Unit> = ApiResponse.onSuccess(Unit)

    // 5. 앨범 수정 (제목 변경 등)
    @PatchMapping("/{albumId}")
    fun updateAlbum(
        @PathVariable albumId: Long,
        @RequestBody request: AlbumCreateRequest,
    ): ApiResponse<Unit> {
        // 실제로는 DB에서 albumId로 찾아서 title을 update 하는 쿼리 실행
        println("앨범($albumId) 제목 수정됨: ${request.title}")
        return ApiResponse.onSuccess(Unit)
    }

    // 6. 앨범 삭제
    @DeleteMapping("/{albumId}")
    fun deleteAlbum(
        @PathVariable albumId: Long,
    ): ApiResponse<Unit> {
        // 1. Post 테이블에서 해당 album_id를 가진 게시글들을 찾아서 album_id = NULL로 변경 (게시글은 살려둠)
        // 2. 그 후에 Album 테이블에서 DELETE 실행
        println("앨범($albumId) 삭제됨")
        return ApiResponse.onSuccess(Unit)
    }

    // 7. 앨범에서 게시글 제외
    @DeleteMapping("/{albumId}/posts/{postId}")
    @Operation(
        summary = "앨범에서 게시글 제외",
        description = "게시글을 앨범에서 뺍니다. 게시글 자체가 삭제되지는 않고, '앨범 없음' 상태가 됩니다.",
    )
    fun removePostFromAlbum(
        @PathVariable albumId: Long,
        @PathVariable postId: Long,
    ): ApiResponse<Unit> {
        // UPDATE post SET album_id = NULL WHERE post_id = {postId} AND album_id = {albumId}
        println("게시글($postId)이 앨범($albumId)에서 제외되었습니다.")
        return ApiResponse.onSuccess(Unit)
    }
}
