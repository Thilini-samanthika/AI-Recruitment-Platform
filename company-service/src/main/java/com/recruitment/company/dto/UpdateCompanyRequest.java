package com.recruitment.company.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload to update company details")
public class UpdateCompanyRequest {

    @Size(min = 2, max = 255, message = "Company name must be between 2 and 255 characters")
    @Schema(description = "Company trade or legal name", example = "Acme Technologies Global")
    private String companyName;

    @Schema(description = "Corporate phone number", example = "+1 (555) 987-6543")
    private String phone;

    @Schema(description = "Corporate address / headquarters", example = "200 Silicon Blvd, 10th Floor, San Jose, CA 95113")
    private String address;
}
