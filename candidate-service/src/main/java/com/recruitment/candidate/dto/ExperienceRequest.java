package com.recruitment.candidate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for adding an experience record")
public class ExperienceRequest {

    @NotBlank(message = "Company name is required")
    @Schema(description = "Name of the employing company", example = "Google LLC", requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyName;

    @NotBlank(message = "Job title is required")
    @Schema(description = "Job title or position held", example = "Software Engineer II", requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobTitle;

    @Schema(description = "Start date", example = "2022-07-01")
    private LocalDate startDate;

    @Schema(description = "End date (leave null if current role)", example = "2024-05-01")
    private LocalDate endDate;

    @Schema(description = "Description of responsibilities and achievements", example = "Designed and maintained scalable microservices handling 50k RPS.")
    private String description;
}
