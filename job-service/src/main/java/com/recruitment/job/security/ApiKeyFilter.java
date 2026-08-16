package com.recruitment.job.security;

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

    @Value("${security.api-key:job-service-secret-key-12345}")
    private String expectedApiKey;

    private static final String API_KEY_HEADER = "X-API-KEY";
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final List<String> WHITELISTED_PATHS = List.of(
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/actuator/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Whitelisted documentation endpoints bypass API key requirement
        if (isWhitelisted(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader(API_KEY_HEADER);
        String userIdHeader = request.getHeader("X-User-Id");

        // 1. If internal API Key is provided, validate it
        if (apiKey != null && !apiKey.isBlank()) {
            if (expectedApiKey.equals(apiKey.trim())) {
                log.debug("Valid X-API-KEY supplied for path: {}", path);
                filterChain.doFilter(request, response);
                return;
            } else {
                log.warn("Invalid X-API-KEY supplied for path: {}", path);
                sendUnauthorized(response, "Invalid X-API-KEY");
                return;
            }
        }

        // 2. If forwarded from Gateway with authenticated user context
        if (userIdHeader != null && !userIdHeader.isBlank()) {
            log.debug("Forwarded gateway user context detected (X-User-Id: {}) for path: {}", userIdHeader, path);
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Fallback: If neither valid API key nor user context is present
        log.warn("Missing X-API-KEY or user authentication headers for path: {}", path);
        sendUnauthorized(response, "Missing or invalid X-API-KEY or user authentication");
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
