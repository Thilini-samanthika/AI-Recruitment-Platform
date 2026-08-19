package com.recruitment.company.dto;

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
@Schema(description = "Request payload to submit verification documents")
public class SubmitVerificationRequest {

    @NotBlank(message = "Tax ID is required")
    @Size(min = 3, max = 50, message = "Tax ID must be between 3 and 50 characters")
    @Schema(description = "Corporate tax identification number", example = "TAX-99887766", requiredMode = Schema.RequiredMode.REQUIRED)
    private String taxId;

    @NotBlank(message = "Business registration number is required")
    @Size(min = 3, max = 50, message = "Business registration number must be between 3 and 50 characters")
    @Schema(description = "State/country business registration ID", example = "REG-CA-2024-5544", requiredMode = Schema.RequiredMode.REQUIRED)
    private String businessRegistrationNumber;

    @NotBlank(message = "Document URL is required")
    @Size(max = 1000, message = "Document URL cannot exceed 1000 characters")
    @Schema(description = "URL pointing to incorporation document or tax certificate", example = "https://documents.recruitment.com/docs/acme-cert.pdf", requiredMode = Schema.RequiredMode.REQUIRED)
    private String documentUrl;
}
