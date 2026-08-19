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
@Schema(description = "Request payload for generating a new service API key")
public class CreateApiKeyRequest {

    @NotBlank(message = "Service name is required")
    @Schema(description = "Target microservice identifier", example = "CANDIDATE_SERVICE", requiredMode = Schema.RequiredMode.REQUIRED)
    private String serviceName;

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
}
