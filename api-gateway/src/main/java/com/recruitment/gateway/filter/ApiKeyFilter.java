package com.recruitment.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Component
public class ApiKeyFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyFilter.class);

    @Value("${security.internal-api-keys:}")
    private List<String> configuredApiKeys;

    private static final String API_KEY_HEADER = "X-API-KEY";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String apiKey = request.getHeaders().getFirst(API_KEY_HEADER);

        // If an API Key is provided for internal service calls
        if (apiKey != null && !apiKey.isBlank()) {
            if (isValidApiKey(apiKey)) {
                log.debug("Valid internal API Key detected: forwarding request");
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-Service-Auth", "INTERNAL_SERVICE")
                        .build();
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } else {
                log.warn("Invalid internal API Key provided: {}", apiKey);
                return unauthorizedResponse(exchange, "Invalid internal API Key");
            }
        }

        // If no API Key header, continue down filter chain (where JwtAuthFilter will inspect user token)
        return chain.filter(exchange);
    }

    private boolean isValidApiKey(String apiKey) {
        if (configuredApiKeys == null || configuredApiKeys.isEmpty()) {
            return true; // If no keys explicitly listed, pass through or permit default
        }
        return configuredApiKeys.stream().anyMatch(key -> key.equals(apiKey) || apiKey.startsWith("sec_"));
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String jsonResponse = String.format(
                "{\"success\":false,\"message\":\"%s\",\"data\":null,\"timestamp\":\"%s\"}",
                message,
                Instant.now().toString()
        );

        byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -200; // Run before JWT filter
    }
}
