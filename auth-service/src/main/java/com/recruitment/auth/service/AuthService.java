package com.recruitment.auth.service;

import com.recruitment.auth.dto.*;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    ValidateTokenResponse validateToken(String token);
    UserResponse getUserById(Long id);
    ApiKeyResponse generateApiKey(CreateApiKeyRequest request);
    boolean validateApiKey(String keyValue, String serviceName);
}
