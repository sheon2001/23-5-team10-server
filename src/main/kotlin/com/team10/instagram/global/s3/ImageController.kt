package com.team10.instagram.global.s3

import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/images")
class ImageController(
    private val imageService: ImageService,
) {
    // POST /api/images/upload
    // form-data의 key 이름은 "image"
    @Operation(summary = "이미지 업로드", description = "이미지 파일을 업로드하고, S3 URL을 반환받습니다.")
    @PostMapping("/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadImage(
        @RequestPart("image") images: List<MultipartFile>,
    ): ResponseEntity<List<String>> {
        val imageUrls = imageService.upload(images)
        return ResponseEntity.ok(imageUrls)
    }
}
