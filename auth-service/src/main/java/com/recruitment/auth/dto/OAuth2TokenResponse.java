package com.recruitment.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard OAuth 2.0 Token Response (RFC 6749)")
public class OAuth2TokenResponse {

    @Schema(description = "JWT Bearer access token")
    @JsonProperty("access_token")
    private String accessToken;

    @Schema(description = "Token type", example = "Bearer")
    @JsonProperty("token_type")
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(description = "Lifetime in seconds", example = "86400")
    @JsonProperty("expires_in")
    private Long expiresIn;

    @Schema(description = "Refresh token for issuing new access tokens")
    @JsonProperty("refresh_token")
    private String refreshToken;

    @Schema(description = "Authorized scope", example = "read write")
    private String scope;

    @Schema(description = "User ID", example = "66c3abc1234567890abcdef1")
    @JsonProperty("user_id")
    private String userId;

    @Schema(description = "User email", example = "candidate@example.com")
    @JsonProperty("email")
    private String email;

    @Schema(description = "Assigned user role", example = "ROLE_CANDIDATE")
    @JsonProperty("role")
    private String role;

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
