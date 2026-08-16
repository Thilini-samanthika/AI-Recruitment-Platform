package com.recruitment.auth.service.impl;

import com.recruitment.auth.dto.*;
import com.recruitment.auth.entity.ApiKey;
import com.recruitment.auth.entity.Role;
import com.recruitment.auth.entity.RoleEnum;
import com.recruitment.auth.entity.User;
import com.recruitment.auth.exception.InvalidCredentialsException;
import com.recruitment.auth.exception.ResourceNotFoundException;
import com.recruitment.auth.exception.UserAlreadyExistsException;
import com.recruitment.auth.repository.ApiKeyRepository;
import com.recruitment.auth.repository.RoleRepository;
import com.recruitment.auth.repository.UserRepository;
import com.recruitment.auth.security.JwtUtil;
import com.recruitment.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private static final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
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

        // Create and save User with hashed password
        User user = User.builder()
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Registered new user with id: {} and email: {}", savedUser.getId(), savedUser.getEmail());

        // Generate JWT
        String token = jwtUtil.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRole().getRoleName());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationTime())
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().getRoleName())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().getRoleName());
        log.info("User logged in successfully: {}", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationTime())
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().getRoleName())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ValidateTokenResponse validateToken(String token) {
        if (token == null || token.isBlank()) {
            return ValidateTokenResponse.builder()
                    .valid(false)
                    .message("Token is null or empty")
                    .build();
        }

        String actualToken = token.startsWith("Bearer ") ? token.substring(7).trim() : token.trim();

        if (!jwtUtil.validateToken(actualToken)) {
            return ValidateTokenResponse.builder()
                    .valid(false)
                    .message("Token is invalid or expired")
                    .build();
        }

        Long userId = jwtUtil.extractUserId(actualToken);
        String email = jwtUtil.extractEmail(actualToken);
        String role = jwtUtil.extractRole(actualToken);

        return ValidateTokenResponse.builder()
                .valid(true)
                .userId(userId)
                .email(email)
                .role(role)
                .message("Token is valid")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().getRoleName())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public ApiKeyResponse generateApiKey(CreateApiKeyRequest request) {
        String serviceName = request.getServiceName().trim().toUpperCase();

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String generatedKey = "sec_" + serviceName.toLowerCase() + "_" + Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        ApiKey apiKey = ApiKey.builder()
                .serviceName(serviceName)
                .keyValue(generatedKey)
                .active(true)
                .build();

        ApiKey saved = apiKeyRepository.save(apiKey);
        log.info("Generated new API key for service: {}", serviceName);

        return ApiKeyResponse.builder()
                .id(saved.getId())
                .serviceName(saved.getServiceName())
                .keyValue(saved.getKeyValue())
                .active(saved.getActive())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
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
}
