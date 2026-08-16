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
@Schema(description = "User profile details response")
public class UserResponse {

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "User email", example = "candidate@example.com")
    private String email;

    @Schema(description = "Role name", example = "ROLE_CANDIDATE")
    private String role;

    @Schema(description = "Account creation timestamp")
    private Instant createdAt;
}
