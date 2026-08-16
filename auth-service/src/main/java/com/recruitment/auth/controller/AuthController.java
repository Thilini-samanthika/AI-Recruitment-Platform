package com.recruitment.auth.controller;

import com.recruitment.auth.dto.*;
import com.recruitment.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication & Authorization API", description = "Endpoints for user registration, login, JWT validation, and service API key generation")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with role (ROLE_CANDIDATE, ROLE_COMPANY, or ROLE_ADMIN) and returns a JWT access token.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload or validation failure"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email already registered")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Received registration request for email: {}", request.getEmail());
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Validates user credentials (email and password) and issues a JWT token.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful, token returned"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Received login attempt for email: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate JWT Token", description = "Validates a JWT token and returns user details. Used internally by the API Gateway.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token validated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token is invalid or expired")
    })
    public ResponseEntity<ApiResponse<ValidateTokenResponse>> validateToken(
            @Parameter(description = "Bearer token string or header", example = "Bearer eyJhbGciOi...")
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "token", required = false) String tokenParam) {

        String token = (authHeader != null && !authHeader.isBlank()) ? authHeader : tokenParam;
        ValidateTokenResponse response = authService.validateToken(token);

        if (!response.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<ValidateTokenResponse>builder()
                            .success(false)
                            .message(response.getMessage())
                            .data(response)
                            .build());
        }

        return ResponseEntity.ok(ApiResponse.success("Token is valid", response));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user details by ID", description = "Retrieves user email, role, and metadata by User ID.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse response = authService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", response));
    }

    @PostMapping("/api-keys")
    @Operation(summary = "Generate service API key", description = "Generates a secure API key for internal microservice-to-microservice communication.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "API Key created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid service name")
    })
    public ResponseEntity<ApiResponse<ApiKeyResponse>> generateApiKey(@Valid @RequestBody CreateApiKeyRequest request) {
        ApiKeyResponse response = authService.generateApiKey(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("API key generated successfully", response));
    }
}
