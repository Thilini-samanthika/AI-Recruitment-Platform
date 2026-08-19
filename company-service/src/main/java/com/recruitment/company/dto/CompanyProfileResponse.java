package com.recruitment.company.dto;

import com.recruitment.company.entity.CompanyProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Extended company profile response")
public class CompanyProfileResponse {

    @Schema(description = "Industry domain", example = "Information Technology")
    private String industry;

    @Schema(description = "Company headcount size", example = "51-200")
    private String companySize;

    @Schema(description = "Company website URL", example = "https://acmecorp.com")
    private String website;

    @Schema(description = "Company overview and mission description", example = "Leading innovator in enterprise software and AI solutions.")
    private String description;

    @Schema(description = "Company logo URL", example = "https://images.unsplash.com/photo-1549719386-74dfcbf7dbed")
    private String logoUrl;

    public static CompanyProfileResponse fromEntity(CompanyProfile profile) {
        if (profile == null) {
            return null;
        }
        return CompanyProfileResponse.builder()
                .industry(profile.getIndustry())
                .companySize(profile.getCompanySize())
                .website(profile.getWebsite())
                .description(profile.getDescription())
                .logoUrl(profile.getLogoUrl())
                .build();
    }
}
