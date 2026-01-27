package com.team10.instagram

import com.fasterxml.jackson.databind.ObjectMapper
import com.team10.instagram.domain.album.dto.AlbumCreateRequest
import com.team10.instagram.domain.album.repository.AlbumRepository
import com.team10.instagram.domain.post.repository.PostRepository
import com.team10.instagram.domain.user.model.User
import com.team10.instagram.helper.DataGenerator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AlbumIntegrationTest
    @Autowired
    constructor(
        private val mvc: MockMvc,
        private val dataGenerator: DataGenerator,
        private val albumRepository: AlbumRepository,
        private val postRepository: PostRepository,
    ) {
        private val objectMapper = ObjectMapper()
        private lateinit var myUser: User
        private lateinit var myToken: String

        @BeforeEach
        fun setup() {
            myUser = dataGenerator.generateUser(email = "me@example.com", nickname = "me")
            myToken = "Bearer ${dataGenerator.generateToken(myUser)}"
        }

        @Test
        fun `앨범을 생성할 수 있다`() {
            // given
            val request = AlbumCreateRequest(title = "제주도 여행")

            // when & then
            mvc
                .perform(
                    post("/api/v1/albums")
                        .header("Authorization", myToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)),
                ).andDo(print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data").isNumber)
        }

        @Test
        fun `내 앨범 목록을 조회할 수 있다`() {
            // given
            val albumId1 = dataGenerator.generateAlbum(myUser, "맛집 모음")
            val albumId2 = dataGenerator.generateAlbum(myUser, "풍경 사진")

            // 앨범1에 게시글 추가 (썸네일 검증용)
            val post1 = dataGenerator.generatePost(myUser, images = listOf("http://thumb1.com"))
            val post2 = dataGenerator.generatePost(myUser, images = listOf("http://thumb2.com"))

            albumRepository.updatePostAlbum(post1.id!!, albumId1)
            albumRepository.updatePostAlbum(post2.id!!, albumId1)

            // when & then
            mvc
                .perform(
                    get("/api/v1/albums/my")
                        .header("Authorization", myToken),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.length()").value(2))
                // 최신순 정렬 확인 (풍경 사진이 나중에 생성됨)
                .andExpect(jsonPath("$.data[0].title").value("풍경 사진"))
                .andExpect(jsonPath("$.data[1].title").value("맛집 모음"))
                .andExpect(jsonPath("$.data[1].postCount").value(2))
        }

        @Test
        fun `앨범 상세(게시글 목록)를 조회할 수 있다`() {
            // given
            val albumId = dataGenerator.generateAlbum(myUser, "추억")
            val post = dataGenerator.generatePost(myUser)
            albumRepository.updatePostAlbum(post.id!!, albumId)

            // when & then
            mvc
                .perform(
                    get("/api/v1/albums/$albumId")
                        .header("Authorization", myToken),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.title").value("추억"))
                .andExpect(jsonPath("$.data.posts.length()").value(1))
                .andExpect(jsonPath("$.data.posts[0].postId").value(post.id))
        }

        @Test
        fun `앨범에 게시글을 추가할 수 있다`() {
            // given
            val albumId = dataGenerator.generateAlbum(myUser)
            val post = dataGenerator.generatePost(myUser)

            // when & then
            mvc
                .perform(
                    post("/api/v1/albums/$albumId/posts/${post.id}")
                        .header("Authorization", myToken),
                ).andExpect(status().isOk)

            // DB 검증
            val savedPost = postRepository.findByIdOrNull(post.id!!)
            assertEquals(albumId, savedPost?.albumId)
        }

        @Test
        fun `타인의 게시글을 내 앨범에 담으려 하면 실패한다`() {
            // given
            val otherUser = dataGenerator.generateUser(nickname = "other")
            val otherPost = dataGenerator.generatePost(otherUser)
            val myAlbumId = dataGenerator.generateAlbum(myUser)

            // when & then
            mvc
                .perform(
                    post("/api/v1/albums/$myAlbumId/posts/${otherPost.id}")
                        .header("Authorization", myToken),
                ).andExpect(status().isForbidden)
        }

        @Test
        fun `타인의 앨범에 내 게시글을 담으려 하면 실패한다`() {
            // given
            val otherUser = dataGenerator.generateUser(nickname = "other")
            val otherAlbumId = dataGenerator.generateAlbum(otherUser)
            val myPost = dataGenerator.generatePost(myUser)

            // when & then
            mvc
                .perform(
                    post("/api/v1/albums/$otherAlbumId/posts/${myPost.id}")
                        .header("Authorization", myToken),
                ).andExpect(status().isForbidden)
        }

        @Test
        fun `앨범 제목을 수정할 수 있다`() {
            // given
            val albumId = dataGenerator.generateAlbum(myUser, "수정 전 제목")
            val request = AlbumCreateRequest(title = "수정 후 제목")

            // when & then
            mvc
                .perform(
                    patch("/api/v1/albums/$albumId")
                        .header("Authorization", myToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isOk)

            // DB 검증
            val updatedAlbum = albumRepository.findById(albumId)
            assertEquals("수정 후 제목", updatedAlbum?.title)
        }

        @Test
        fun `앨범에서 게시글을 제외할 수 있다`() {
            // given
            val albumId = dataGenerator.generateAlbum(myUser)
            val post = dataGenerator.generatePost(myUser)
            albumRepository.updatePostAlbum(post.id!!, albumId)

            // when & then
            mvc
                .perform(
                    delete("/api/v1/albums/$albumId/posts/${post.id}")
                        .header("Authorization", myToken),
                ).andExpect(status().isOk)

            // DB 검증
            val savedPost = postRepository.findByIdOrNull(post.id!!)
            assertNull(savedPost?.albumId)
        }

        @Test
        fun `앨범 삭제 시 게시글은 유지되고 연결만 끊긴다`() {
            // given
            val albumId = dataGenerator.generateAlbum(myUser)
            val post = dataGenerator.generatePost(myUser)
            albumRepository.updatePostAlbum(post.id!!, albumId)

            // when & then
            mvc
                .perform(
                    delete("/api/v1/albums/$albumId")
                        .header("Authorization", myToken),
                ).andExpect(status().isOk)

            // DB 검증
            assertNull(albumRepository.findById(albumId))
            val savedPost = postRepository.findByIdOrNull(post.id!!)
            assertNotNull(savedPost)
            assertNull(savedPost?.albumId)
        }

        @Test
        fun `이미 존재하는 앨범 이름으로 생성하면 실패한다`() {
            // given
            val duplicateTitle = "제주도 여행"

            dataGenerator.generateAlbum(myUser, duplicateTitle)

            // 똑같은 이름으로 생성 요청 객체 만들기
            val request = AlbumCreateRequest(title = duplicateTitle)

            // when & then
            mvc
                .perform(
                    post("/api/v1/albums")
                        .header("Authorization", myToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)),
                ).andDo(print())
                .andExpect(status().isConflict) // 409 Conflict 확인
                .andExpect(jsonPath("$.code").value("ALBUM_ALREADY_EXISTS"))
        }

        @Test
        fun `다른 앨범에 있는 게시글을 제외하려고 하면 실패한다`() {
            // given
            val albumAId = dataGenerator.generateAlbum(myUser, "앨범 A")
            val albumBId = dataGenerator.generateAlbum(myUser, "앨범 B")

            val post = dataGenerator.generatePost(myUser)

            albumRepository.updatePostAlbum(post.id!!, albumBId)

            // when & then
            // 내 앨범은 맞지만 잘못된 요청
            mvc
                .perform(
                    delete("/api/v1/albums/$albumAId/posts/${post.id}")
                        .header("Authorization", myToken),
                ).andDo(print())
                .andExpect(status().isBadRequest) // 400 Bad Request 확인
                .andExpect(jsonPath("$.code").value("POST_NOT_IN_ALBUM"))
        }

        @Test
        fun `존재하지 않는 게시글을 앨범에 추가하면 실패한다`() {
            // given
            val albumId = dataGenerator.generateAlbum(myUser)

            val wrongPostId = 99999L // 존재하지 않는 ID

            // when & then
            mvc
                .perform(
                    post("/api/v1/albums/$albumId/posts/$wrongPostId")
                        .header("Authorization", myToken),
                ).andDo(print())
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("$.code").value("POST_NOT_FOUND"))
        }
    }
