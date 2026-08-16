package com.recruitment.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "JWT Token validation result response")
public class ValidateTokenResponse {

    @Schema(description = "Token validity status", example = "true")
    private boolean valid;

    @Schema(description = "Authenticated user ID", example = "1")
    private Long userId;

    @Schema(description = "Authenticated user email", example = "candidate@example.com")
    private String email;

    @Schema(description = "Authenticated user role", example = "ROLE_CANDIDATE")
    private String role;

    @Schema(description = "Status message", example = "Token is valid")
    private String message;
}
