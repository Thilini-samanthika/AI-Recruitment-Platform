package com.recruitment.company.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload to create or update company extended profile")
public class CompanyProfileRequest {

    @Size(max = 100, message = "Industry cannot exceed 100 characters")
    @Schema(description = "Industry category", example = "Information Technology")
    private String industry;

    @Pattern(regexp = "^$|^(1-10|11-50|51-200|201-500|500\\+|1-10 employees|11-50 employees|51-200 employees|201-500 employees|500\\+ employees)$",
            message = "Company size must be a valid range (e.g., '1-10', '11-50', '51-200', '201-500', '500+')")
    @Schema(description = "Company size band (e.g. 1-10, 11-50, 51-200, 201-500, 500+)", example = "51-200")
    private String companySize;

    @Pattern(regexp = "^$|^(https?://)?[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(/.*)?$",
            message = "Invalid website URL format")
    @Schema(description = "Company website URL", example = "https://acmecorp.com")
    private String website;

    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    @Schema(description = "Detailed company overview, culture, and achievements", example = "Acme is a global enterprise software and AI tech leader.")
    private String description;

    @Size(max = 1000, message = "Logo URL cannot exceed 1000 characters")
    @Schema(description = "Direct URL to company logo image", example = "https://images.unsplash.com/photo-1549719386-74dfcbf7dbed")
    private String logoUrl;
}
