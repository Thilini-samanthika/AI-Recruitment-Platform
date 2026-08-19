package com.recruitment.auth.dto;

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
@Schema(description = "OAuth 2.0 Token Request payload (RFC 6749)")
public class OAuth2TokenRequest {

    @Schema(description = "Grant type: 'password', 'refresh_token', or 'client_credentials'", example = "password", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("grant_type")
    private String grantType;

    @Schema(description = "Username or email (used with grant_type=password)", example = "candidate@example.com")
    private String username;

    @Schema(description = "Password (used with grant_type=password)", example = "Password@123")
    private String password;

    @Schema(description = "Refresh token string (used with grant_type=refresh_token)")
    @JsonProperty("refresh_token")
    private String refreshToken;

    @Schema(description = "Client ID / Service identifier (used with grant_type=client_credentials)", example = "CANDIDATE_SERVICE")
    @JsonProperty("client_id")
    private String clientId;

    @Schema(description = "Client secret / API key (used with grant_type=client_credentials)")
    @JsonProperty("client_secret")
    private String clientSecret;

    @Schema(description = "Requested scope", example = "read write")
    private String scope;

    public String getGrantType() { return grantType; }
    public void setGrantType(String grantType) { this.grantType = grantType; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
}
