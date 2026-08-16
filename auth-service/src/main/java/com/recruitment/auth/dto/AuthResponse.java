package com.recruitment.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication response with JWT token")
public class AuthResponse {

    @Schema(description = "JWT Bearer access token")
    private String token;

    @Schema(description = "Token type", example = "Bearer")
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(description = "Token expiration in milliseconds", example = "86400000")
    private Long expiresIn;

    @Schema(description = "User ID", example = "1")
    private Long userId;

    @Schema(description = "User email", example = "candidate@example.com")
    private String email;

    @Schema(description = "Assigned role", example = "ROLE_CANDIDATE")
    private String role;

    @Schema(description = "Creation timestamp")
    private Instant createdAt;
}
