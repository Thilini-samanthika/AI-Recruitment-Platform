package com.recruitment.company.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfile {

    private String industry;

    private String companySize;

    private String website;

    private String description;

    private String logoUrl;
}
