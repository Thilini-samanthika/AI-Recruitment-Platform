package com.recruitment.company.dto;

import com.recruitment.company.entity.VerificationDetails;
import com.recruitment.company.entity.VerificationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Company verification status response")
public class VerificationResponse {

    @Schema(description = "Verification status", example = "VERIFIED")
    private VerificationStatus status;

    @Schema(description = "Corporate tax identification number", example = "TAX-99887766")
    private String taxId;

    @Schema(description = "State/country business registration ID", example = "REG-CA-2024-5544")
    private String businessRegistrationNumber;

    @Schema(description = "Document URL for official verification credentials", example = "https://documents.recruitment.com/docs/acme-cert.pdf")
    private String documentUrl;

    @Schema(description = "Admin review comments or reason for rejection", example = "Verified with government corporate registrar")
    private String reviewNotes;

    @Schema(description = "Submission timestamp")
    private LocalDateTime submittedAt;

    @Schema(description = "Review timestamp")
    private LocalDateTime reviewedAt;

    @Schema(description = "Admin user who reviewed the application")
    private String reviewedBy;

    public static VerificationResponse fromEntity(VerificationDetails details) {
        if (details == null) {
            return VerificationResponse.builder()
                    .status(VerificationStatus.UNVERIFIED)
                    .build();
        }
        return VerificationResponse.builder()
                .status(details.getStatus())
                .taxId(details.getTaxId())
                .businessRegistrationNumber(details.getBusinessRegistrationNumber())
                .documentUrl(details.getDocumentUrl())
                .reviewNotes(details.getReviewNotes())
                .submittedAt(details.getSubmittedAt())
                .reviewedAt(details.getReviewedAt())
                .reviewedBy(details.getReviewedBy())
                .build();
    }
}
