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

    @Schema(description = "Experience record ID", example = "1")
    private Long id;

    @Schema(description = "Candidate ID", example = "10")
    private Long candidateId;

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
