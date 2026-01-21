package com.team10.instagram

import com.fasterxml.jackson.databind.ObjectMapper
import com.team10.instagram.domain.comment.dto.CommentCreateRequest
import com.team10.instagram.domain.comment.dto.CommentUpdateRequest
import com.team10.instagram.domain.comment.repository.CommentRepository
import com.team10.instagram.domain.user.model.User
import com.team10.instagram.helper.DataGenerator
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CommentIntegrationTest
    @Autowired
    constructor(
        private val mvc: MockMvc,
        private val dataGenerator: DataGenerator,
        private val commentRepository: CommentRepository,
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
        fun `댓글을 작성할 수 있다`() {
            // given
            val user = myUser
            val token = myToken
            val post = dataGenerator.generatePost(user = user)

            val request = CommentCreateRequest(content = "좋은 사진이네요!")

            // when & then
            mvc
                .perform(
                    post("/api/v1/posts/${post.id}/comments")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.content").value("좋은 사진이네요!"))
        }

        @Test
        fun `빈 내용으로 댓글을 작성하면 실패한다`() {
            // given
            val user = myUser
            val token = myToken
            val post = dataGenerator.generatePost(user = user)
            val request = CommentCreateRequest(content = "")

            // when & then
            mvc
                .perform(
                    post("/api/v1/posts/${post.id}/comments")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isBadRequest) // 400 Bad Request
        }

        @Test
        fun `게시글의 댓글 목록을 조회할 수 있다`() {
            // given
            val user = myUser
            val token = myToken
            val post = dataGenerator.generatePost(user = user)

            dataGenerator.generateComment(post = post, user = user, content = "댓글1")
            dataGenerator.generateComment(post = post, user = user, content = "댓글2")

            // when & then
            mvc
                .perform(
                    get("/api/v1/posts/${post.id}/comments")
                        .header("Authorization", token),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.length()").value(2))
                // Check sort by time (Lastest comment is first)
                .andExpect(jsonPath("$.data[0].content").value("댓글2"))
                .andExpect(jsonPath("$.data[1].content").value("댓글1"))
        }

        @Test
        fun `댓글을 수정할 수 있다`() {
            // given
            val user = myUser
            val token = myToken
            val post = dataGenerator.generatePost(user = user)
            val comment = dataGenerator.generateComment(post = post, user = user, content = "수정 전 댓글")

            val request = CommentUpdateRequest(content = "수정 후 댓글")

            // when & then
            mvc
                .perform(
                    put("/api/v1/posts/${post.id}/comments/${comment.id}")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.content").value("수정 후 댓글"))
        }

        @Test
        fun `빈 내용으로 댓글을 수정하면 실패한다`() {
            // given
            val user = myUser
            val token = myToken
            val post = dataGenerator.generatePost(user = user)
            val comment = dataGenerator.generateComment(post = post, user = user, content = "수정 전 댓글")

            val request = CommentUpdateRequest(content = "")

            // when & then
            mvc
                .perform(
                    put("/api/v1/posts/${post.id}/comments/${comment.id}")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isBadRequest) // 400 Bad Request
        }

        @Test
        fun `다른 유저의 댓글을 수정하려 하면 실패한다`() {
            // given
            val user = myUser
            val token = myToken
            val otherUser = dataGenerator.generateUser(nickname = "other")
            val post = dataGenerator.generatePost(user = otherUser)
            val comment = dataGenerator.generateComment(post = post, user = otherUser, content = "다른 사람의 댓글")

            val request = CommentUpdateRequest(content = "다른 유저의 댓글에 대한 수정 요청")

            // when & then
            mvc
                .perform(
                    put("/api/v1/posts/${post.id}/comments/${comment.id}")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isForbidden) // 403 Forbidden
        }

        @Test
        fun `댓글을 삭제할 수 있다`() {
            // given
            val user = myUser
            val token = myToken
            val post = dataGenerator.generatePost(user = user)
            val comment = dataGenerator.generateComment(post = post, user = user)

            // when & then
            mvc
                .perform(
                    delete("/api/v1/posts/${post.id}/comments/${comment.id}")
                        .header("Authorization", token),
                ).andExpect(status().isOk)

            assertNull(commentRepository.findByIdOrNull(comment.id!!))
        }

        @Test
        fun `다른 유저의 댓글을 삭제하려 하면 실패한다`() {
            // given
            val user = myUser
            val token = myToken
            val otherUser = dataGenerator.generateUser(nickname = "other")
            val post = dataGenerator.generatePost(user = otherUser)
            val comment = dataGenerator.generateComment(post = post, user = otherUser)

            // when & then
            mvc
                .perform(
                    delete("/api/v1/posts/${post.id}/comments/${comment.id}")
                        .header("Authorization", myToken),
                ).andExpect(status().isForbidden) // 403 Forbidden
        }
    }
