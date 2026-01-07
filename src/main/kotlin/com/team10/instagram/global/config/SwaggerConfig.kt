package com.team10.instagram.global.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .addServersItem(Server().url("/")) // https 등으로 변경되어도 유연하게 동작
            .info(Info()
                .title("Team 10 Instagram API")
                .description("인스타그램 클론 코딩 API 명세서입니다.")
                .version("v1.0.0")
            )
    }
}