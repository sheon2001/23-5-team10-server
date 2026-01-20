package com.team10.instagram

import com.fasterxml.jackson.databind.ObjectMapper
import com.team10.instagram.domain.post.dto.PostCreateRequest
import com.team10.instagram.domain.post.dto.PostUpdateRequest
import com.team10.instagram.domain.post.repository.BookmarkRepository
import com.team10.instagram.domain.post.repository.PostLikeRepository
import com.team10.instagram.domain.post.repository.PostRepository
import com.team10.instagram.domain.user.model.User
import com.team10.instagram.helper.DataGenerator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostIntegrationTest
    @Autowired
    constructor(
        private val mvc: MockMvc,
        private val dataGenerator: DataGenerator,
        private val postRepository: PostRepository,
        private val postLikeRepository: PostLikeRepository,
        @Autowired private val bookmarkRepository: BookmarkRepository,
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
        fun `로그인한 유저는 게시글을 생성할 수 있다`() {
            // given
            val user = myUser
            val token = myToken

            val request =
                PostCreateRequest(
                    content = "새로운 게시글입니다.",
                    albumId = null,
                    imageUrls = listOf("https://s3.aws.com/img1.jpg", "https://s3.aws.com/img2.jpg"),
                )

            // when & then
            mvc
                .perform(
                    post("/api/v1/posts")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.content").value("새로운 게시글입니다."))
                .andExpect(jsonPath("$.data.images.length()").value(2))
        }

        @Test
        fun `빈 내용으로 게시글을 생성하면 실패한다`() {
            // given
            val user = myUser
            val token = myToken
            val request =
                PostCreateRequest(
                    content = "",
                    albumId = null,
                )

            // when & then
            mvc
                .perform(
                    post("/api/v1/posts")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isBadRequest) // 400 Bad Request
        }

        @Test
        fun `게시글을 상세 조회할 수 있다`() {
            // given
            val user = myUser
            val token = myToken

            val otherUser = dataGenerator.generateUser()
            val post = dataGenerator.generatePost(user = otherUser, content = "조회용 게시글")

            // when & then
            mvc
                .perform(
                    get("/api/v1/posts/${post.id}")
                        .header("Authorization", token),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.id").value(post.id))
                .andExpect(jsonPath("$.data.content").value("조회용 게시글"))
                .andExpect(jsonPath("$.data.userId").value(otherUser.userId))
        }

        @Test
        fun `존재하지 않는 게시글을 조회하면 실패한다`() {
            // given
            val user = myUser
            val token = myToken
            val notExistPostId = 99999L

            // when & then
            mvc
                .perform(
                    get("/api/v1/posts/$notExistPostId")
                        .header("Authorization", token),
                ).andExpect(status().isNotFound) // 404 Not Found
        }

        @Test
        fun `작성자는 본인의 게시글을 수정할 수 있다`() {
            // given
            val user = myUser
            val token = myToken

            val post = dataGenerator.generatePost(user = user, content = "수정 전 내용")

            val request =
                PostUpdateRequest(
                    content = "수정 후 내용",
                    albumId = null,
                )

            // when & then
            mvc
                .perform(
                    put("/api/v1/posts/${post.id}")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.content").value("수정 후 내용"))

            // DB 확인
            val updatedPost = postRepository.findByIdOrNull(post.id!!)
            assertEquals("수정 후 내용", updatedPost!!.content)
        }

        @Test
        fun `빈 내용으로 게시글을 수정하면 실패한다`() {
            // given
            val user = myUser
            val token = myToken
            val post = dataGenerator.generatePost(user = user, content = "원본 글")

            val request =
                PostUpdateRequest(
                    content = "",
                    albumId = null,
                )

            // when & then
            mvc
                .perform(
                    put("/api/v1/posts/${post.id}")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isBadRequest) // 400 Bad Request
        }

        @Test
        fun `다른 유저의 게시글을 수정하려 하면 실패한다`() {
            // given
            val user = myUser
            val token = myToken
            val otherUser = dataGenerator.generateUser(nickname = "other")
            val post = dataGenerator.generatePost(user = otherUser, content = "원본 글")

            val request = PostUpdateRequest(content = "해킹 시도", albumId = null)

            // when & then
            mvc
                .perform(
                    put("/api/v1/posts/${post.id}")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isForbidden) // 403 Forbidden
        }

        @Test
        fun `작성자는 본인의 게시글을 삭제할 수 있다`() {
            // given
            val user = myUser
            val token = myToken
            val post = dataGenerator.generatePost(user = user)

            // when & then
            mvc
                .perform(
                    delete("/api/v1/posts/${post.id}")
                        .header("Authorization", token),
                ).andExpect(status().isOk)

            // check DB status
            assertNull(postRepository.findByIdOrNull(post.id!!))
        }

        @Test
        fun `다른 유저의 게시글을 삭제하려 하면 실패한다`() {
            // given
            val user = myUser
            val token = myToken
            val otherUser = dataGenerator.generateUser(nickname = "other")
            val post = dataGenerator.generatePost(user = otherUser)

            // when & then
            mvc
                .perform(
                    delete("/api/v1/posts/${post.id}")
                        .header("Authorization", token),
                ).andExpect(status().isForbidden) // 403 Forbidden
        }

        @Test
        fun `게시글 좋아요 및 취소를 할 수 있다`() {
            // given
            val user = myUser
            val token = myToken
            val otherUser = dataGenerator.generateUser()
            val post = dataGenerator.generatePost(user = otherUser)

            // Like
            mvc
                .perform(
                    post("/api/v1/posts/${post.id}/like")
                        .header("Authorization", token),
                ).andExpect(status().isOk)

            assertTrue(postLikeRepository.countByPostId(post.id!!) == 1L)

            // Unlike
            mvc
                .perform(
                    delete("/api/v1/posts/${post.id}/like")
                        .header("Authorization", token),
                ).andExpect(status().isOk)

            assertTrue(postLikeRepository.countByPostId(post.id!!) == 0L)
        }

        @Test
        fun `게시글 북마크 및 취소를 할 수 있다`() {
            // given
            val user = myUser
            val token = myToken
            val otherUser = dataGenerator.generateUser()
            val post = dataGenerator.generatePost(user = otherUser)

            // Bookmark
            mvc
                .perform(
                    post("/api/v1/posts/${post.id}/bookmark")
                        .header("Authorization", token),
                ).andExpect(status().isOk)

            assertTrue(bookmarkRepository.existsByPostIdAndUserId(post.id!!, user.userId!!))

            // Unbookmark
            mvc
                .perform(
                    delete("/api/v1/posts/${post.id}/bookmark")
                        .header("Authorization", token),
                ).andExpect(status().isOk)

            val exists = bookmarkRepository.existsByPostIdAndUserId(post.id!!, user.userId!!)
            assertTrue(!exists)
        }
    }
