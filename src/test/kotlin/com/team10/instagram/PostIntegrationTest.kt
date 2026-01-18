package com.team10.instagram

import com.fasterxml.jackson.databind.ObjectMapper
import com.team10.instagram.domain.post.dto.PostCreateRequest
import com.team10.instagram.domain.post.dto.PostUpdateRequest
import com.team10.instagram.domain.post.repository.BookmarkRepository
import com.team10.instagram.domain.post.repository.PostLikeRepository
import com.team10.instagram.domain.post.repository.PostRepository
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
        private val bookmarkRepository: BookmarkRepository,
    ) {
        lateinit var myToken: String
        private val objectMapper = ObjectMapper()

        @BeforeEach
        fun setup() {
            val me = dataGenerator.generateUser(email = "me@example.com", nickname = "me")
            myToken = "Bearer ${dataGenerator.generateToken(me)}"
        }

        @Test
        fun `로그인한 유저는 게시글을 생성할 수 있다`() {
            // given
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
                        .header("Authorization", myToken) // 토큰 포함
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.content").value("새로운 게시글입니다."))
                .andExpect(jsonPath("$.data.images.length()").value(2))
        }

        @Test
        fun `게시글을 상세 조회할 수 있다`() {
            // given
            val otherUser = dataGenerator.generateUser()
            val post = dataGenerator.generatePost(user = otherUser, content = "조회용 게시글")

            // when & then
            mvc
                .perform(
                    get("/api/v1/posts/${post.id}")
                        .header("Authorization", myToken),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.id").value(post.id))
                .andExpect(jsonPath("$.data.content").value("조회용 게시글"))
                .andExpect(jsonPath("$.data.userId").value(otherUser.userId))
        }

        @Test
        fun `작성자는 본인의 게시글을 수정할 수 있다`() {
            // given
            val me = dataGenerator.generateUser(email = "writer@test.com")
            val writerToken = "Bearer ${dataGenerator.generateToken(me)}"

            val post = dataGenerator.generatePost(user = me, content = "수정 전 내용")

            val request =
                PostUpdateRequest(
                    content = "수정 후 내용",
                    albumId = null,
                )

            // when & then
            mvc
                .perform(
                    put("/api/v1/posts/${post.id}")
                        .header("Authorization", writerToken) // 작성자 토큰
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.content").value("수정 후 내용"))

            // DB 확인
            val updatedPost = postRepository.findByIdOrNull(post.id!!)
            assertEquals("수정 후 내용", updatedPost!!.content)
        }

        @Test
        fun `작성자는 본인의 게시글을 삭제할 수 있다`() {
            // given
            val me = dataGenerator.generateUser(email = "deleter@test.com")
            val writerToken = "Bearer ${dataGenerator.generateToken(me)}"
            val post = dataGenerator.generatePost(user = me)

            // when & then
            mvc
                .perform(
                    delete("/api/v1/posts/${post.id}")
                        .header("Authorization", writerToken),
                ).andExpect(status().isOk)

            // check DB status
            assertNull(postRepository.findByIdOrNull(post.id!!))
        }

        @Test
        fun `게시글 좋아요 및 취소를 할 수 있다`() {
            // given
            val otherUser = dataGenerator.generateUser()
            val post = dataGenerator.generatePost(user = otherUser)

            // Like
            mvc
                .perform(
                    post("/api/v1/posts/${post.id}/like")
                        .header("Authorization", myToken),
                ).andExpect(status().isOk)

            assertTrue(postLikeRepository.countByPostId(post.id!!) == 1L)

            // Unlike
            mvc
                .perform(
                    delete("/api/v1/posts/${post.id}/like")
                        .header("Authorization", myToken),
                ).andExpect(status().isOk)

            assertTrue(postLikeRepository.countByPostId(post.id!!) == 0L)
        }

        @Test
        fun `팔로우한 유저의 피드를 조회할 수 있다`() {
            // given
            val starUser = dataGenerator.generateUser(email = "star@test.com")

            val post1 = dataGenerator.generatePost(user = starUser, content = "Star Post 1")
            val post2 = dataGenerator.generatePost(user = starUser, content = "Star Post 2")

            val follower = dataGenerator.generateUser(email = "follower@test.com")
            val followerToken = "Bearer ${dataGenerator.generateToken(follower)}"

            dataGenerator.generateFollow(fromUser = follower, toUser = starUser)

            // when & then
            mvc
                .perform(
                    get("/api/v1/feed")
                        .header("Authorization", followerToken)
                        .param("page", "1")
                        .param("size", "10"),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.items.length()").value(2))
                // check if sorted properly
                .andExpect(jsonPath("$.data.items[0].postId").value(post2.id))
                .andExpect(jsonPath("$.data.items[1].postId").value(post1.id))
        }
    }
