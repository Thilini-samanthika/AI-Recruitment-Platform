package com.recruitment.candidate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Experience record details response")
public class ExperienceResponse {

    @Schema(description = "Experience unique ID", example = "exp-4b8c2d1e-9012")
    private String id;

    @Schema(description = "Candidate Profile ID", example = "66c3abc1234567890abcdef1")
    private String candidateId;

    @Schema(description = "Name of the company", example = "Google LLC")
    private String companyName;

    @Schema(description = "Job title", example = "Software Engineer II")
    private String jobTitle;

    @Schema(description = "Start date", example = "2022-07-01")
    private LocalDate startDate;

    @Schema(description = "End date", example = "2024-05-01")
    private LocalDate endDate;

    @Schema(description = "Description of responsibilities and achievements", example = "Designed and maintained scalable microservices.")
    private String description;
}
