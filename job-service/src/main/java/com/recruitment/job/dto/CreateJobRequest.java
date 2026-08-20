package com.recruitment.job.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload to post a new job opening")
public class CreateJobRequest {

    @NotNull(message = "Company ID is required")
    @Schema(description = "ID of the company posting the vacancy", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long companyId;

    @NotBlank(message = "Job title is required")
    @Size(min = 3, max = 255, message = "Job title must be between 3 and 255 characters")
    @Schema(description = "Position title", example = "Senior Backend Engineer", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank(message = "Job description is required")
    @Size(min = 10, max = 10000, message = "Job description must be between 10 and 10,000 characters")
    @Schema(description = "Comprehensive job description, responsibilities, and qualifications", example = "We are seeking a senior backend engineer to design scalable microservices using Spring Boot and MongoDB...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @NotBlank(message = "Required skills must not be blank")
    @Size(max = 2000, message = "Required skills must not exceed 2000 characters")
    @Schema(description = "Key technical competencies and skills required", example = "Java 17, Spring Boot, MongoDB, Docker, Microservices", requiredMode = Schema.RequiredMode.REQUIRED)
    private String requiredSkills;

    @NotBlank(message = "Location is required")
    @Size(max = 255, message = "Location must not exceed 255 characters")
    @Schema(description = "Work location or remote policy", example = "San Francisco, CA / Hybrid", requiredMode = Schema.RequiredMode.REQUIRED)
    private String location;

    @NotBlank(message = "Salary range is required")
    @Size(max = 100, message = "Salary range must not exceed 100 characters")
    @Schema(description = "Compensation bracket or annual salary range", example = "$140,000 - $180,000 / yr", requiredMode = Schema.RequiredMode.REQUIRED)
    private String salaryRange;

    @NotBlank(message = "Job type is required")
    @Size(max = 50, message = "Job type must not exceed 50 characters")
    @Schema(description = "Employment engagement type (FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, REMOTE)", example = "FULL_TIME", requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobType;

    @Future(message = "Application deadline must be a future date and time")
    @Schema(description = "Application closing deadline timestamp", example = "2026-12-31T23:59:59")
    private LocalDateTime deadline;
}
