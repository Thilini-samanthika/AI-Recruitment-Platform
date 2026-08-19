package com.recruitment.candidate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    @Schema(description = "Full name of the candidate", example = "Alice Johnson", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fullName;

    @Size(max = 25, message = "Phone number must not exceed 25 characters")
    @Schema(description = "Contact phone number", example = "+1-555-0199")
    private String phone;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    @Schema(description = "Physical or mailing address", example = "123 Innovation Way, San Francisco, CA")
    private String address;

    @Size(max = 150, message = "Headline must not exceed 150 characters")
    @Schema(description = "Professional headline", example = "Lead Distributed Systems Architect")
    private String headline;

    @Size(max = 2000, message = "Summary must not exceed 2000 characters")
    @Schema(description = "Professional summary / bio", example = "Updated summary describing new leadership and cloud experience.")
    private String summary;
}
