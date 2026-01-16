package com.team10.instagram.global.s3

import io.awspring.cloud.s3.ObjectMetadata
import io.awspring.cloud.s3.S3Template
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class ImageService(
    private val s3Template: S3Template,
    @Value("\${spring.cloud.aws.s3.bucket}")
    private val bucket: String,
) {
    fun upload(files: List<MultipartFile>): List<String> {

        // 들어온 파일들을 하나씩 꺼내서 S3에 올리고, URL들을 모아서 반환
        return files.map { file ->
            // 1. 파일 이름 중복 방지 (UUID 사용)
            // 예: originalFilename = "my_cat.jpg"
            //     savedFileName = "550e8400-e29b-41d4..._my_cat.jpg"
            val originalFileName = file.originalFilename ?: "image.jpg"
            val uuid = UUID.randomUUID().toString()
            val savedFileName = "${uuid}_$originalFileName"

            // 2. S3에 파일 업로드
            // upload(버킷이름, 저장할이름, 파일의_InputStream, 메타데이터)
            val resource = s3Template.upload(
                bucket,
                savedFileName,
                file.inputStream,
                ObjectMetadata.builder().contentType(file.contentType).build()
            )

            resource.url.toString()
        }
    }
}
