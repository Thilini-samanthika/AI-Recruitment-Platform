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

    @Schema(description = "Authenticated user ID", example = "66c3abc1234567890abcdef1")
    private String userId;

    @Schema(description = "Authenticated user email", example = "candidate@example.com")
    private String email;

    @Schema(description = "Authenticated user role", example = "ROLE_CANDIDATE")
    private String role;

    @Schema(description = "Status message", example = "Token is valid")
    private String message;

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
