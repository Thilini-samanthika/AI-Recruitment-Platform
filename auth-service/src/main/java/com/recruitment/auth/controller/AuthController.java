package com.recruitment.auth.controller;

import com.recruitment.auth.dto.*;
import com.recruitment.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication & Authorization API", description = "Endpoints for OAuth 2.0 token issuance, user registration, login, JWT validation, and service API keys")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

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
    @Operation(summary = "Authenticate user", description = "Validates user credentials (email and password) and issues a JWT token with a refresh token.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful, token returned"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Received login attempt for email: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/oauth/token")
    @Operation(summary = "OAuth 2.0 Token Endpoint (RFC 6749)", description = "Standard OAuth 2.0 token issuance endpoint supporting grant_type=password, grant_type=refresh_token, and grant_type=client_credentials.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OAuth 2.0 token issued successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid grant_type or missing parameters"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials or expired refresh token")
    })
    public ResponseEntity<OAuth2TokenResponse> oauthToken(@RequestBody OAuth2TokenRequest request) {
        log.info("Received OAuth 2.0 token request with grant_type: {}", request.getGrantType());
        OAuth2TokenResponse response = authService.processOAuth2Token(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh JWT Access Token", description = "Exchanges a valid refresh token for a newly generated JWT access token.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "New access token generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    public ResponseEntity<ApiResponse<OAuth2TokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        OAuth2TokenResponse response = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Access token refreshed successfully", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "User Logout / Token Revocation", description = "Revokes the active refresh token, terminating the session.")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.revokeToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
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
            ApiResponse<ValidateTokenResponse> errorResp = new ApiResponse<>();
            errorResp.setSuccess(false);
            errorResp.setMessage(response.getMessage());
            errorResp.setData(response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResp);
        }

        return ResponseEntity.ok(ApiResponse.success("Token is valid", response));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user details by ID", description = "Retrieves user email, role, and metadata by MongoDB User ID.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String id) {
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
