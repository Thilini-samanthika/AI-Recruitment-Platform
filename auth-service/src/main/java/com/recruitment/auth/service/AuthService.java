package com.recruitment.auth.service;

import com.recruitment.auth.dto.*;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    ValidateTokenResponse validateToken(String token);
    UserResponse getUserById(String id);
    ApiKeyResponse generateApiKey(CreateApiKeyRequest request);
    boolean validateApiKey(String keyValue, String serviceName);

    // OAuth 2.0 and Refresh Token workflows
    OAuth2TokenResponse processOAuth2Token(OAuth2TokenRequest request);
    OAuth2TokenResponse refreshAccessToken(String refreshToken);
    void revokeToken(String refreshToken);
}
