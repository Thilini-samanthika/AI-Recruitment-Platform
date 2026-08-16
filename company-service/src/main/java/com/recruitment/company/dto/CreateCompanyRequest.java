package com.recruitment.company.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Request payload to register a new company")
public class CreateCompanyRequest {

    @NotNull(message = "User ID is required")
    @Schema(description = "User ID from Auth Service", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 255, message = "Company name must be between 2 and 255 characters")
    @Schema(description = "Company trade or legal name", example = "Acme Technologies Inc.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Corporate contact email", example = "contact@acme.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "Corporate phone number", example = "+1 (555) 234-5678")
    private String phone;

    @Schema(description = "Corporate address / headquarters", example = "100 Innovation Way, Suite 400, San Francisco, CA 94105")
    private String address;
}
