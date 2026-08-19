package com.recruitment.company.dto;

import com.recruitment.company.entity.VerificationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for Administrator to verify or reject a company")
public class ReviewVerificationRequest {

    @NotNull(message = "Verification status is required")
    @Schema(description = "New verification status (VERIFIED or REJECTED)", example = "VERIFIED", requiredMode = Schema.RequiredMode.REQUIRED)
    private VerificationStatus status;

    @Size(max = 1000, message = "Review notes cannot exceed 1000 characters")
    @Schema(description = "Admin review comments or reason for decision", example = "Incorporation filings verified against state database.")
    private String reviewNotes;
}
