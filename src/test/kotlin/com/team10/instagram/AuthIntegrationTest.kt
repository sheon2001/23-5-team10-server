package com.team10.instagram.domain.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.team10.instagram.domain.auth.dto.LoginRequest
import com.team10.instagram.domain.auth.service.JwtTokenBlacklistService
import com.team10.instagram.domain.user.Role
import com.team10.instagram.domain.user.dto.UserDto
import com.team10.instagram.domain.user.model.User
import com.team10.instagram.domain.user.repository.UserRepository
import com.team10.instagram.global.common.ApiResponse
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertNotNull


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val userRepository: UserRepository,
    private val jwtTokenBlacklistService: JwtTokenBlacklistService,
) {

    private val objectMapper = ObjectMapper()
    private val passwordEncoder = BCryptPasswordEncoder()
    private lateinit var testUser: User

    @BeforeEach
    fun setup() {
        val encodedPassword = passwordEncoder.encode("password123")
        println("User Repository")
        println(userRepository.findAll())
        testUser = userRepository.save(
            User(
                email = "test@example.com",
                password = encodedPassword,
                nickname = "tester",
                role = Role.USER
            )
        )
        println("User Repository")
        println(userRepository.findAll())
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
        val token = response.get("data").get("accessToken").asText()
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
        val token = response.get("data").get("accessToken").asText()
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
        val errorCode = response.get("code").asText()
        assert(errorCode == "401")
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
        val errorCode = response.get("code").asText()
        assert(errorCode == "404")
    }

    @Test
    fun `GET me returns current logged in user with correct fields`() {
        val loginRequest = LoginRequest(loginId = testUser.email, password = "password123")
        val loginResult = mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginRequest)
        }.andReturn()

        val token = objectMapper.readTree(loginResult.response.contentAsString).get("data")
            .get("accessToken").asText()


        val mvcResult = mockMvc.get("/api/v1/users/me") {
            header("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }.andReturn()

        val response = objectMapper.readTree(mvcResult.response.contentAsString)

        val userDto = response.get("data")
        assertNotNull(userDto)

        assert(testUser.email == userDto.get("email").asText())
        assert(testUser.nickname == userDto.get("nickname").asText())
    }

    @Test
    fun `logout invalidates JWT token`() {
        val loginRequest = LoginRequest(loginId = "test@example.com", password = "password123")
        val loginResult = mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginRequest)
        }.andReturn()

        val token = objectMapper.readTree(loginResult.response.contentAsString).get("data")
            .get("accessToken").asText()

        mockMvc.post("/api/v1/auth/logout") {
            header("Authorization", "Bearer $token")
        }.andExpect {
            status { isOk() }
        }

        assert(jwtTokenBlacklistService.contains(token))
    }

    @Test
    fun `delete account removes users and invalidates JWT token`() {
        val loginRequest = LoginRequest(loginId = "test@example.com", password = "password123")
        val loginResult = mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginRequest)
        }.andReturn()

        val token = objectMapper.readTree(loginResult.response.contentAsString).get("data")
            .get("accessToken").asText()

        mockMvc.delete("/api/v1/users/me") {
            header("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }

        assert(!userRepository.existsByUserId(testUser.userId))
        assert(jwtTokenBlacklistService.contains(token))
    }
}
