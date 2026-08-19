package com.recruitment.auth.service.impl;

import com.recruitment.auth.dto.*;
import com.recruitment.auth.entity.ApiKey;
import com.recruitment.auth.entity.RefreshToken;
import com.recruitment.auth.entity.Role;
import com.recruitment.auth.entity.RoleEnum;
import com.recruitment.auth.entity.User;
import com.recruitment.auth.exception.InvalidCredentialsException;
import com.recruitment.auth.exception.ResourceNotFoundException;
import com.recruitment.auth.exception.UserAlreadyExistsException;
import com.recruitment.auth.repository.ApiKeyRepository;
import com.recruitment.auth.repository.RefreshTokenRepository;
import com.recruitment.auth.repository.RoleRepository;
import com.recruitment.auth.repository.UserRepository;
import com.recruitment.auth.security.JwtUtil;
import com.recruitment.auth.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final long REFRESH_TOKEN_VALIDITY_DAYS = 7;

    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           ApiKeyRepository apiKeyRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.apiKeyRepository = apiKeyRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new UserAlreadyExistsException("User with email already exists: " + normalizedEmail);
        }

        // Determine Role
        String requestedRoleName = request.getRole() != null ? request.getRole() : "ROLE_CANDIDATE";
        RoleEnum roleEnum = RoleEnum.fromString(requestedRoleName);
        String finalRoleName = roleEnum.name();

        Role role = roleRepository.findByRoleName(finalRoleName)
                .orElseGet(() -> roleRepository.save(new Role(finalRoleName)));

        // Create and save User in MongoDB
        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setCreatedAt(Instant.now());

        User savedUser = userRepository.save(user);
        log.info("Registered new user with MongoDB id: {} and email: {}", savedUser.getId(), savedUser.getEmail());

        // Generate JWT Access Token and Refresh Token
        String token = jwtUtil.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRole().getRoleName());
        String refreshToken = createAndPersistRefreshToken(savedUser.getId());

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(jwtUtil.getExpirationTime());
        response.setRefreshToken(refreshToken);
        response.setUserId(savedUser.getId());
        response.setEmail(savedUser.getEmail());
        response.setRole(savedUser.getRole().getRoleName());
        response.setCreatedAt(savedUser.getCreatedAt());
        return response;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().getRoleName());
        String refreshToken = createAndPersistRefreshToken(user.getId());
        log.info("User logged in successfully: {}", user.getEmail());

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(jwtUtil.getExpirationTime());
        response.setRefreshToken(refreshToken);
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().getRoleName());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    @Override
    public ValidateTokenResponse validateToken(String token) {
        if (token == null || token.isBlank()) {
            ValidateTokenResponse resp = new ValidateTokenResponse();
            resp.setValid(false);
            resp.setMessage("Token is null or empty");
            return resp;
        }

        String actualToken = token.startsWith("Bearer ") ? token.substring(7).trim() : token.trim();

        if (!jwtUtil.validateToken(actualToken)) {
            ValidateTokenResponse resp = new ValidateTokenResponse();
            resp.setValid(false);
            resp.setMessage("Token is invalid or expired");
            return resp;
        }

        String userId = jwtUtil.extractUserId(actualToken);
        String email = jwtUtil.extractEmail(actualToken);
        String role = jwtUtil.extractRole(actualToken);

        ValidateTokenResponse resp = new ValidateTokenResponse();
        resp.setValid(true);
        resp.setUserId(userId);
        resp.setEmail(email);
        resp.setRole(role);
        resp.setMessage("Token is valid");
        return resp;
    }

    @Override
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        UserResponse resp = new UserResponse();
        resp.setId(user.getId());
        resp.setEmail(user.getEmail());
        resp.setRole(user.getRole().getRoleName());
        resp.setCreatedAt(user.getCreatedAt());
        return resp;
    }

    @Override
    public ApiKeyResponse generateApiKey(CreateApiKeyRequest request) {
        String serviceName = request.getServiceName().trim().toUpperCase();

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String generatedKey = "sec_" + serviceName.toLowerCase() + "_" + Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        ApiKey apiKey = new ApiKey();
        apiKey.setServiceName(serviceName);
        apiKey.setKeyValue(generatedKey);
        apiKey.setActive(true);
        apiKey.setCreatedAt(Instant.now());

        ApiKey saved = apiKeyRepository.save(apiKey);
        log.info("Generated new API key for service: {}", serviceName);

        ApiKeyResponse resp = new ApiKeyResponse();
        resp.setId(saved.getId());
        resp.setServiceName(saved.getServiceName());
        resp.setKeyValue(saved.getKeyValue());
        resp.setActive(saved.getActive());
        resp.setCreatedAt(saved.getCreatedAt());
        return resp;
    }

    @Override
    public boolean validateApiKey(String keyValue, String serviceName) {
        if (keyValue == null || keyValue.isBlank()) {
            return false;
        }
        Optional<ApiKey> apiKeyOpt = apiKeyRepository.findByKeyValueAndActiveTrue(keyValue);
        if (apiKeyOpt.isEmpty()) {
            return false;
        }
        if (serviceName != null && !serviceName.isBlank()) {
            return apiKeyOpt.get().getServiceName().equalsIgnoreCase(serviceName);
        }
        return true;
    }

    @Override
    public OAuth2TokenResponse processOAuth2Token(OAuth2TokenRequest request) {
        String grantType = request.getGrantType();
        if (grantType == null || grantType.isBlank()) {
            throw new InvalidCredentialsException("Missing grant_type parameter");
        }

        if ("password".equalsIgnoreCase(grantType)) {
            if (request.getUsername() == null || request.getPassword() == null) {
                throw new InvalidCredentialsException("Username and password are required for password grant");
            }
            LoginRequest loginReq = new LoginRequest();
            loginReq.setEmail(request.getUsername());
            loginReq.setPassword(request.getPassword());
            AuthResponse authResponse = login(loginReq);

            OAuth2TokenResponse resp = new OAuth2TokenResponse();
            resp.setAccessToken(authResponse.getToken());
            resp.setTokenType("Bearer");
            resp.setExpiresIn(authResponse.getExpiresIn() / 1000);
            resp.setRefreshToken(authResponse.getRefreshToken());
            resp.setScope(request.getScope() != null ? request.getScope() : "read write");
            resp.setUserId(authResponse.getUserId());
            resp.setEmail(authResponse.getEmail());
            resp.setRole(authResponse.getRole());
            return resp;

        } else if ("refresh_token".equalsIgnoreCase(grantType)) {
            if (request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
                throw new InvalidCredentialsException("Refresh token is required for refresh_token grant");
            }
            return refreshAccessToken(request.getRefreshToken());

        } else if ("client_credentials".equalsIgnoreCase(grantType)) {
            String clientId = request.getClientId();
            String clientSecret = request.getClientSecret();

            if (clientId == null || clientSecret == null || !validateApiKey(clientSecret, clientId)) {
                throw new InvalidCredentialsException("Invalid client_id or client_secret");
            }

            // Generate Service Access Token
            String serviceToken = jwtUtil.generateToken("SVC_" + clientId, clientId.toLowerCase() + "@system.internal", "ROLE_SERVICE");
            OAuth2TokenResponse resp = new OAuth2TokenResponse();
            resp.setAccessToken(serviceToken);
            resp.setTokenType("Bearer");
            resp.setExpiresIn(jwtUtil.getExpirationTime() / 1000);
            resp.setScope("service-internal");
            resp.setRole("ROLE_SERVICE");
            return resp;

        } else {
            throw new InvalidCredentialsException("Unsupported grant_type: " + grantType);
        }
    }

    @Override
    public OAuth2TokenResponse refreshAccessToken(String tokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid or expired refresh token"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidCredentialsException("Refresh token has expired. Please login again.");
        }

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User associated with refresh token not found"));

        String newAccessToken = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().getRoleName());

        OAuth2TokenResponse resp = new OAuth2TokenResponse();
        resp.setAccessToken(newAccessToken);
        resp.setTokenType("Bearer");
        resp.setExpiresIn(jwtUtil.getExpirationTime() / 1000);
        resp.setRefreshToken(refreshToken.getToken());
        resp.setUserId(user.getId());
        resp.setEmail(user.getEmail());
        resp.setRole(user.getRole().getRoleName());
        resp.setScope("read write");
        return resp;
    }

    @Override
    public void revokeToken(String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenRepository.deleteByToken(refreshToken);
            log.info("Revoked refresh token");
        }
    }

    private String createAndPersistRefreshToken(String userId) {
        // Clean up existing tokens for this user
        refreshTokenRepository.deleteByUserId(userId);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", ""));
        refreshToken.setUserId(userId);
        refreshToken.setExpiryDate(Instant.now().plus(REFRESH_TOKEN_VALIDITY_DAYS, ChronoUnit.DAYS));
        refreshToken.setCreatedAt(Instant.now());

        RefreshToken saved = refreshTokenRepository.save(refreshToken);
        return saved.getToken();
    }
}
