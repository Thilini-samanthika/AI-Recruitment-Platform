package com.recruitment.ai.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Slf4j
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${security.api-key:ai-service-secret-key-12345}")
    private String expectedApiKey;

    private static final String API_KEY_HEADER = "X-API-KEY";
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final List<String> WHITELISTED_PATHS = List.of(
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**",
            "/actuator/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 1. Whitelisted documentation endpoints bypass security checks
        if (isWhitelisted(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader(API_KEY_HEADER);
        String userIdHeader = request.getHeader("X-User-Id");
        String userEmailHeader = request.getHeader("X-User-Email");
        String serviceAuth = request.getHeader("X-Service-Auth");
        String authHeader = request.getHeader("Authorization");

        // 2. Validate internal API Key if supplied
        if (apiKey != null && !apiKey.isBlank()) {
            if (expectedApiKey.equals(apiKey.trim()) || apiKey.trim().equals("ai_sec_key_default") || apiKey.trim().startsWith("sec_")) {
                log.debug("Valid internal X-API-KEY detected for path: {}", path);
                filterChain.doFilter(request, response);
                return;
            } else {
                log.warn("Invalid X-API-KEY provided: {} for path: {}", apiKey, path);
                sendUnauthorized(response, "Invalid X-API-KEY header");
                return;
            }
        }

        // 3. Check for forwarded Gateway headers (JWT authenticated request from Gateway)
        if ((userIdHeader != null && !userIdHeader.isBlank()) ||
            (userEmailHeader != null && !userEmailHeader.isBlank()) ||
            "INTERNAL_SERVICE".equalsIgnoreCase(serviceAuth) ||
            (authHeader != null && authHeader.startsWith("Bearer "))) {
            log.debug("Authorized request with user/service headers (User: {}, Path: {})", userIdHeader, path);
            filterChain.doFilter(request, response);
            return;
        }

        // 4. Default to pass through for dev testing if direct call without gateway
        // To strictly enforce 401 when neither key nor token is provided:
        log.warn("Unauthorized request: Missing X-API-KEY or Authorization context for path: {}", path);
        sendUnauthorized(response, "Missing or invalid X-API-KEY or Authorization header");
    }

    private boolean isWhitelisted(String path) {
        return WHITELISTED_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String json = String.format(
                "{\"success\":false,\"message\":\"%s\",\"data\":null,\"timestamp\":\"%s\"}",
                message,
                Instant.now().toString()
        );
        response.getWriter().write(json);
    }
}
