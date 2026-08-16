package com.recruitment.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to generate an internal service API key")
public class CreateApiKeyRequest {

    @NotBlank(message = "Service name is required")
    @Schema(description = "Service identifier (e.g. CANDIDATE_SERVICE, COMPANY_SERVICE, JOB_SERVICE, AI_SERVICE)", example = "CANDIDATE_SERVICE")
    private String serviceName;
}
