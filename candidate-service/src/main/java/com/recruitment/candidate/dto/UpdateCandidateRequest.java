package com.recruitment.candidate.dto;

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
@Schema(description = "Request payload for updating an existing candidate profile")
public class UpdateCandidateRequest {

    @NotBlank(message = "Full name is required")
    @Schema(description = "Full name of the candidate", example = "Alice Johnson", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fullName;

    @Schema(description = "Contact phone number", example = "+1-555-0199")
    private String phone;

    @Schema(description = "Physical or mailing address", example = "123 Innovation Way, San Francisco, CA")
    private String address;

    @Schema(description = "Professional headline", example = "Lead Distributed Systems Architect")
    private String headline;

    @Schema(description = "Professional summary / bio", example = "Updated summary describing new leadership and cloud experience.")
    private String summary;
}
