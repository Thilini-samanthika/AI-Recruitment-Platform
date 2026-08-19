package com.recruitment.auth.service;

import com.recruitment.auth.dto.*;
import com.recruitment.auth.entity.ApiKey;
import com.recruitment.auth.entity.RefreshToken;
import com.recruitment.auth.entity.Role;
import com.recruitment.auth.entity.User;
import com.recruitment.auth.exception.InvalidCredentialsException;
import com.recruitment.auth.exception.UserAlreadyExistsException;
import com.recruitment.auth.repository.ApiKeyRepository;
import com.recruitment.auth.repository.RefreshTokenRepository;
import com.recruitment.auth.repository.RoleRepository;
import com.recruitment.auth.repository.UserRepository;
import com.recruitment.auth.security.JwtUtil;
import com.recruitment.auth.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ApiKeyRepository apiKeyRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private Role candidateRole;

    @BeforeEach
    void setUp() {
        candidateRole = new Role("role-id-1", "ROLE_CANDIDATE");
    }

    @Test
    void shouldRegisterNewUserSuccessfully() {
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .password("password123")
                .role("ROLE_CANDIDATE")
                .build();

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(roleRepository.findByRoleName("ROLE_CANDIDATE")).thenReturn(Optional.of(candidateRole));
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");

        User savedUser = User.builder()
                .id("user-id-1")
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .role(candidateRole)
                .createdAt(Instant.now())
                .build();

        RefreshToken mockRefreshToken = RefreshToken.builder()
                .id("rt-id-1")
                .token("mock-refresh-token")
                .userId("user-id-1")
                .expiryDate(Instant.now().plusSeconds(604800))
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(mockRefreshToken);
        when(jwtUtil.generateToken("user-id-1", "test@example.com", "ROLE_CANDIDATE")).thenReturn("mock-jwt-token");
        when(jwtUtil.getExpirationTime()).thenReturn(86400000L);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("ROLE_CANDIDATE", response.getRole());
    }

    @Test
    void shouldThrowExceptionWhenRegisteringExistingEmail() {
        RegisterRequest request = RegisterRequest.builder()
                .email("existing@example.com")
                .password("password123")
                .build();

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
    }

    @Test
    void shouldLoginSuccessfullyWithValidCredentials() {
        LoginRequest request = LoginRequest.builder()
                .email("user@example.com")
                .password("password123")
                .build();

        User user = User.builder()
                .id("user-id-2")
                .email("user@example.com")
                .passwordHash("hashedPassword")
                .role(candidateRole)
                .createdAt(Instant.now())
                .build();

        RefreshToken mockRefreshToken = RefreshToken.builder()
                .id("rt-id-2")
                .token("mock-refresh-token")
                .userId("user-id-2")
                .expiryDate(Instant.now().plusSeconds(604800))
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(mockRefreshToken);
        when(jwtUtil.generateToken("user-id-2", "user@example.com", "ROLE_CANDIDATE")).thenReturn("mock-jwt-token");
        when(jwtUtil.getExpirationTime()).thenReturn(86400000L);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals("user@example.com", response.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenLoginWithWrongPassword() {
        LoginRequest request = LoginRequest.builder()
                .email("user@example.com")
                .password("wrongpassword")
                .build();

        User user = User.builder()
                .id("user-id-2")
                .email("user@example.com")
                .passwordHash("hashedPassword")
                .role(candidateRole)
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "hashedPassword")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void shouldGenerateApiKeySuccessfully() {
        CreateApiKeyRequest request = new CreateApiKeyRequest();
        request.setServiceName("JOB_SERVICE");

        when(apiKeyRepository.save(any(ApiKey.class))).thenAnswer(invocation -> {
            ApiKey key = invocation.getArgument(0);
            key.setId("api-key-id-10");
            return key;
        });

        ApiKeyResponse response = authService.generateApiKey(request);

        assertNotNull(response);
        assertEquals("JOB_SERVICE", response.getServiceName());
        assertTrue(response.getKeyValue().startsWith("sec_job_service_"));
        assertTrue(response.getActive());
    }
}
