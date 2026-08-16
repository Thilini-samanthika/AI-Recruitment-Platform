package com.recruitment.job.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request payload to post a new job opening")
public class CreateJobRequest {

    @NotNull(message = "Company ID is required")
    @Schema(description = "Company ID posting the job", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long companyId;

    @NotBlank(message = "Job title is required")
    @Size(min = 3, max = 255, message = "Job title must be between 3 and 255 characters")
    @Schema(description = "Job title", example = "Senior Backend Engineer", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank(message = "Job description is required")
    @Schema(description = "Full job description, responsibilities, and requirements", example = "We are seeking a seasoned backend engineer with Spring Boot expertise...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @Schema(description = "Comma-separated or JSON skills required", example = "Java, Spring Boot, Docker, MySQL, Redis")
    private String requiredSkills;

    @Schema(description = "Job work location or remote policy", example = "San Francisco, CA / Hybrid")
    private String location;

    @Schema(description = "Estimated salary range", example = "$140,000 - $180,000 / yr")
    private String salaryRange;

    @Schema(description = "Job engagement type (e.g. FULL_TIME, PART_TIME, INTERNSHIP, CONTRACT, REMOTE)", example = "FULL_TIME")
    private String jobType;
}
