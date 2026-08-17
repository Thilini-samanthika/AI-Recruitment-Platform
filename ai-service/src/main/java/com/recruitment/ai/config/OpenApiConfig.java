package com.recruitment.ai.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8085}")
    private String serverPort;

    @Bean
    public OpenAPI aiServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Resume & Recommendation Service API")
                        .description("Member 5 Microservice: Handles resume upload, PDF/DOCX text parsing, AI skill extraction, semantic job matching, and intelligent candidate recommendations.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Member 5 - AI & Frontend Lead")
                                .email("member5@recruitment-platform.internal"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort).description("Direct AI Service Instance"),
                        new Server().url("http://localhost:8080").description("API Gateway Route")
                ))
                .addSecurityItem(new SecurityRequirement().addList("ApiKeyAuth").addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("ApiKeyAuth", new SecurityScheme()
                                .name("X-API-KEY")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .description("Internal Microservice API Key (e.g. AI_SERVICE_KEY)"))
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Bearer token forwarded by Gateway")));
    }
}
