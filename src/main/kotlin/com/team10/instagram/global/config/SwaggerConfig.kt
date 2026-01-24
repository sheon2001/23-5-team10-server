package com.team10.instagram.global.config

import com.team10.instagram.global.error.ErrorCode
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Value("\${jwt.test-token}")
    private lateinit var testJwt: String

    @Bean
    fun openAPI(): OpenAPI {
        val securitySchemeName = "bearerAuth"

        return OpenAPI()
            .addServersItem(Server().url("/")) // https 등으로 변경되어도 유연하게 동작
            .info(
                Info()
                    .title("Team 10 Instagram API")
                    .description("인스타그램 클론 코딩 API 명세서입니다.\n\n" + generateErrorCodeTable())
                    .version("v1.0.0"),
            ).addSecurityItem(SecurityRequirement().addList(securitySchemeName))
            .components(
                Components().addSecuritySchemes(
                    securitySchemeName,
                    SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"),
                ),
            )
    }

    private fun generateErrorCodeTable(): String {
        val sb = StringBuilder()

        sb.append("\n\n### [ 에러 코드 모음 ]\n")
        sb.append("FE 개발 시, 아래 `Code` 값을 기준으로 분기 처리 부탁드립니다.\n\n")
        sb.append("| Status | Code (FE 식별용) | Message (설명) |\n")
        sb.append("| :--- | :--- | :--- |\n")

        // ErrorCode의 모든 값을 순회하며 행 추가
        ErrorCode.entries.forEach { error ->
            sb.append("| ${error.status.value()} | **${error.code}** | ${error.message} |\n")
        }

        return sb.toString()
    }
}
