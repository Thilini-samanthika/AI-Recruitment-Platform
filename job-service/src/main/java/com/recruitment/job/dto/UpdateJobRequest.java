package com.recruitment.job.dto;

import com.recruitment.job.entity.JobStatus;
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
@Schema(description = "Request payload to update an existing job posting")
public class UpdateJobRequest {

    @Size(min = 3, max = 255, message = "Job title must be between 3 and 255 characters")
    @Schema(description = "Job title", example = "Lead Backend Systems Architect")
    private String title;

    @Schema(description = "Full job description", example = "Updated job details and expanded team responsibilities...")
    private String description;

    @Schema(description = "Comma-separated required skills", example = "Java 21, Spring Boot 3, Kubernetes, Kafka, AWS")
    private String requiredSkills;

    @Schema(description = "Job location", example = "San Francisco, CA / Remote")
    private String location;

    @Schema(description = "Salary range", example = "$160,000 - $210,000 / yr")
    private String salaryRange;

    @Schema(description = "Job engagement type", example = "FULL_TIME")
    private String jobType;

    @Schema(description = "Job listing lifecycle status (OPEN or CLOSED)", example = "OPEN")
    private JobStatus status;
}
