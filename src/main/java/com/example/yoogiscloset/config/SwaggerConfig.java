package com.example.yoogiscloset.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger (SpringDoc OpenAPI) 설정 클래스
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Yoogiscloset Automation API")
                .description("고이비토(Koibito) 상품 스크래핑 및 유기스클로젯(Yoogi's Closet) 판매 등록 자동화 프로젝트의 API 명세서입니다.")
                .version("1.0.0");
    }
}