package com.team10.instagram

import com.fasterxml.jackson.databind.ObjectMapper
import com.team10.instagram.domain.user.model.User
import com.team10.instagram.helper.DataGenerator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FeedIntegrationTest
    @Autowired
    constructor(
        private val mvc: MockMvc,
        private val dataGenerator: DataGenerator,
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
        fun `팔로우한 유저의 피드를 조회할 수 있다`() {
            // given
            val user = myUser
            val token = myToken

            // Create follow relationship
            val starUser = dataGenerator.generateUser(nickname = "star")
            val post1 = dataGenerator.generatePost(user = starUser, content = "Old Post")
            val post2 = dataGenerator.generatePost(user = starUser, content = "New Post")
            dataGenerator.generateFollow(fromUser = user, toUser = starUser)

            // Create non-follow relationship
            val stranger = dataGenerator.generateUser(nickname = "stranger")
            dataGenerator.generatePost(user = stranger, content = "Stranger Post")

            // when & then
            mvc
                .perform(
                    get("/api/v1/feed")
                        .header("Authorization", token)
                        .param("page", "1")
                        .param("size", "6"),
                ).andExpect(status().isOk)
                // We have to return only 2 posts
                .andExpect(jsonPath("$.data.items.length()").value(2))
                // Check sort by time (Lastest comment is first)
                .andExpect(jsonPath("$.data.items[0].postId").value(post2.id))
                .andExpect(jsonPath("$.data.items[1].postId").value(post1.id))
        }

        @Test
        fun `팔로우한 유저가 없으면 빈 피드가 반환된다`() {
            // given
            val user = myUser
            val token = myToken

            // when & then
            mvc
                .perform(
                    get("/api/v1/feed")
                        .header("Authorization", token),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.items").isEmpty())
                .andExpect(jsonPath("$.data.totalElements").value(0))
        }
    }
