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

    @Schema(description = "API Key ID", example = "66c3abc1234567890abcdef2")
    private String id;

    @Schema(description = "Service name", example = "CANDIDATE_SERVICE")
    private String serviceName;

    @Schema(description = "Generated API Key value", example = "sec_cand_xyz123...")
    private String keyValue;

    @Schema(description = "Status of the API key", example = "true")
    private Boolean active;

    @Schema(description = "Timestamp when key was generated")
    private Instant createdAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getKeyValue() { return keyValue; }
    public void setKeyValue(String keyValue) { this.keyValue = keyValue; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
