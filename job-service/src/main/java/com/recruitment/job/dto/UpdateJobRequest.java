package com.recruitment.job.dto;

import com.recruitment.job.entity.JobStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
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
@Schema(description = "Request payload to update an existing job posting")
public class UpdateJobRequest {

    @Size(min = 3, max = 255, message = "Job title must be between 3 and 255 characters")
    @Schema(description = "Updated position title", example = "Lead Backend Systems Architect")
    private String title;

    @Size(min = 10, max = 10000, message = "Job description must be between 10 and 10,000 characters")
    @Schema(description = "Updated job description", example = "Updated job details and expanded team responsibilities...")
    private String description;

    @Size(max = 2000, message = "Required skills must not exceed 2000 characters")
    @Schema(description = "Updated comma-separated required skills", example = "Java 17, Spring Boot, MongoDB, Docker, Kafka, AWS")
    private String requiredSkills;

    @Size(max = 255, message = "Location must not exceed 255 characters")
    @Schema(description = "Updated job location or remote policy", example = "San Francisco, CA / Remote")
    private String location;

    @Size(max = 100, message = "Salary range must not exceed 100 characters")
    @Schema(description = "Updated salary range", example = "$160,000 - $210,000 / yr")
    private String salaryRange;

    @Size(max = 50, message = "Job type must not exceed 50 characters")
    @Schema(description = "Updated job engagement type", example = "FULL_TIME")
    private String jobType;

    @Schema(description = "Job listing lifecycle status (OPEN or CLOSED)", example = "OPEN")
    private JobStatus status;

    @Future(message = "Application deadline must be a future date and time")
    @Schema(description = "Updated application deadline timestamp", example = "2026-12-31T23:59:59")
    private LocalDateTime deadline;
}
