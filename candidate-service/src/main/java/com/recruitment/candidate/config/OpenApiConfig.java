package com.recruitment.candidate.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI candidateServiceOpenAPI() {
        final String apiKeyScheme = "apiKeyAuth";
        final String bearerScheme = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("AI Recruitment Platform - Candidate Service API")
                        .version("1.0.0")
                        .description("Microservice responsible for candidate profile management, skills, education, and work experience.")
                        .contact(new Contact()
                                .name("Member 2 (Candidate Service Lead)")
                                .url("https://github.com/Thilini-samanthika/AI-Recruitment-Platform"))
                        .license(new License().name("Apache 2.0")))
                .addSecurityItem(new SecurityRequirement().addList(apiKeyScheme).addList(bearerScheme))
                .components(new Components()
                        .addSecuritySchemes(apiKeyScheme,
                                new SecurityScheme()
                                        .name("X-API-KEY")
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .description("Internal Microservice API Key"))
                        .addSecuritySchemes(bearerScheme,
                                new SecurityScheme()
                                        .name(bearerScheme)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT Authorization Header forwarded by Gateway")));
    }
}
