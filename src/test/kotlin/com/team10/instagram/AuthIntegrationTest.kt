package com.team10.instagram.domain.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.team10.instagram.domain.auth.dto.LoginRequest
import com.team10.instagram.domain.user.Role
import com.team10.instagram.domain.user.model.User
import com.team10.instagram.domain.user.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertNotNull

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthIntegrationTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val userRepository: UserRepository
) {

    private val objectMapper = ObjectMapper()
    private val passwordEncoder = BCryptPasswordEncoder()
    private lateinit var testUser: User

    @BeforeEach
    fun setup() {
        val encodedPassword = passwordEncoder.encode("password123")
        testUser = userRepository.save(
            User(
                email = "test@example.com",
                password = encodedPassword,
                nickname = "tester",
                role = Role.USER
            )
        )
    }

    @Test
    fun `login with email returns JWT token`() {
        val request = LoginRequest(loginId = "test@example.com", password = "password123")

        val result = mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
        }.andReturn()

        val response = objectMapper.readTree(result.response.contentAsString)
        val token = response.get("accessToken").asText()
        assertNotNull(token)
    }

    @Test
    fun `login with nickname returns JWT token`() {
        val request = LoginRequest(loginId = "tester", password = "password123")

        val result = mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
        }.andReturn()

        val response = objectMapper.readTree(result.response.contentAsString)
        val token = response.get("accessToken").asText()
        assertNotNull(token)
    }

    @Test
    fun `login with wrong password returns INVALID_PASSWORD error`() {
        val request = LoginRequest(loginId = "test@example.com", password = "wrongpass")

        val result = mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { is4xxClientError() }
        }.andReturn()

        val response = objectMapper.readTree(result.response.contentAsString)
        val errorCode = response.get("errorCode").asText()
        assert(errorCode == "INVALID_PASSWORD")
    }

    @Test
    fun `login with non-existent user returns USER_NOT_FOUND error`() {
        val request = LoginRequest(loginId = "notfound@example.com", password = "password123")

        val result = mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { is4xxClientError() }
        }.andReturn()

        val response = objectMapper.readTree(result.response.contentAsString)
        val errorCode = response.get("errorCode").asText()
        assert(errorCode == "USER_NOT_FOUND")
    }
}
