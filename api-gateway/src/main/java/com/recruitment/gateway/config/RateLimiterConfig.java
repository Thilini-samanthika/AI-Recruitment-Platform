package com.recruitment.gateway.config;

import com.recruitment.gateway.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

@Configuration
public class RateLimiterConfig {

    private static final Logger log = LoggerFactory.getLogger(RateLimiterConfig.class);

    private final JwtUtil jwtUtil;

    public RateLimiterConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * IP-based rate limiter key resolver.
     * Extracts client IP address from X-Forwarded-For header or remote socket address.
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            ServerHttpRequest request = exchange.getRequest();
            String forwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
            if (forwardedFor != null && !forwardedFor.isBlank()) {
                String clientIp = forwardedFor.split(",")[0].trim();
                return Mono.just("ip_" + clientIp);
            }
            InetSocketAddress remoteAddress = request.getRemoteAddress();
            String ip = (remoteAddress != null && remoteAddress.getAddress() != null)
                    ? remoteAddress.getAddress().getHostAddress()
                    : "anonymous";
            return Mono.just("ip_" + ip);
        };
    }

    /**
     * User-based rate limiter key resolver.
     * If user is authenticated via Bearer JWT, keys by user ID; otherwise falls back to client IP.
     */
    @Bean
    @Primary
    public KeyResolver userKeyResolver() {
        return exchange -> {
            ServerHttpRequest request = exchange.getRequest();
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7).trim();
                try {
                    if (jwtUtil.validateToken(token)) {
                        String userId = jwtUtil.extractUserId(token);
                        if (userId != null && !userId.isBlank()) {
                            return Mono.just("user_" + userId);
                        }
                    }
                } catch (Exception e) {
                    log.debug("Failed to extract userId from JWT for rate limiting: {}", e.getMessage());
                }
            }

            // Fallback to IP address for unauthenticated requests
            String forwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
            if (forwardedFor != null && !forwardedFor.isBlank()) {
                return Mono.just("ip_" + forwardedFor.split(",")[0].trim());
            }
            InetSocketAddress remoteAddress = request.getRemoteAddress();
            String ip = (remoteAddress != null && remoteAddress.getAddress() != null)
                    ? remoteAddress.getAddress().getHostAddress()
                    : "anonymous";
            return Mono.just("ip_" + ip);
        };
    }
}
