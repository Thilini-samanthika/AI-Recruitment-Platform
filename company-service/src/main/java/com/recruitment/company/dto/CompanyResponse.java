package com.recruitment.company.dto;

import com.recruitment.company.entity.Company;
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
@Schema(description = "Company details response")
public class CompanyResponse {

    @Schema(description = "Company unique ID (MongoDB ObjectId)", example = "66c25a1f2b3e8c0012345678")
    private String id;

    @Schema(description = "User ID in Auth Service", example = "10")
    private Long userId;

    @Schema(description = "Company name", example = "Acme Technologies Inc.")
    private String companyName;

    @Schema(description = "Corporate contact email", example = "contact@acme.com")
    private String email;

    @Schema(description = "Contact phone number", example = "+1 (555) 234-5678")
    private String phone;

    @Schema(description = "Headquarters address", example = "100 Innovation Way, Suite 400, San Francisco, CA 94105")
    private String address;

    @Schema(description = "Extended company profile")
    private CompanyProfileResponse profile;

    @Schema(description = "Corporate verification details")
    private VerificationResponse verification;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    public static CompanyResponse fromEntity(Company company) {
        if (company == null) {
            return null;
        }
        return CompanyResponse.builder()
                .id(company.getId())
                .userId(company.getUserId())
                .companyName(company.getCompanyName())
                .email(company.getEmail())
                .phone(company.getPhone())
                .address(company.getAddress())
                .profile(company.getProfile() != null ? CompanyProfileResponse.fromEntity(company.getProfile()) : null)
                .verification(company.getVerification() != null ? VerificationResponse.fromEntity(company.getVerification()) : null)
                .createdAt(company.getCreatedAt())
                .updatedAt(company.getUpdatedAt())
                .build();
    }
}
