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
@Schema(description = "Request payload for creating a new candidate profile")
public class CreateCandidateRequest {

    @Schema(description = "Auth Service User ID", example = "1")
    private Long userId;

    @NotBlank(message = "Full name is required")
    @Schema(description = "Full name of the candidate", example = "Alice Johnson", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fullName;

    @Schema(description = "Contact phone number", example = "+1-555-0199")
    private String phone;

    @Schema(description = "Physical or mailing address", example = "123 Innovation Way, San Francisco, CA")
    private String address;

    @Schema(description = "Professional headline", example = "Senior Full Stack Software Engineer")
    private String headline;

    @Schema(description = "Professional summary / bio", example = "Passionate engineer with 6+ years experience in distributed systems and React.")
    private String summary;
}
