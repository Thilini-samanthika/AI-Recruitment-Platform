package com.recruitment.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API Key details response")
public class ApiKeyResponse {

    @Schema(description = "API Key ID", example = "1")
    private Long id;

    @Schema(description = "Service name", example = "CANDIDATE_SERVICE")
    private String serviceName;

    @Schema(description = "Generated API Key value")
    private String keyValue;

    @Schema(description = "Whether the key is active", example = "true")
    private Boolean active;

    @Schema(description = "Creation timestamp")
    private Instant createdAt;
}
