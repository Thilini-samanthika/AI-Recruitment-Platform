package com.recruitment.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User registration request payload")
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "User email address", example = "candidate@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Schema(description = "User password", example = "StrongPassword123!")
    private String password;

    @Schema(description = "User role (ROLE_CANDIDATE, ROLE_COMPANY, ROLE_ADMIN)", example = "ROLE_CANDIDATE", defaultValue = "ROLE_CANDIDATE")
    private String role;
}
