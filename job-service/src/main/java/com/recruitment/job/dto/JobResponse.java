package com.recruitment.job.dto;

import com.recruitment.job.entity.Job;
import com.recruitment.job.entity.JobStatus;
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
@Schema(description = "Job posting details response")
public class JobResponse {

    @Schema(description = "Job ID (MongoDB ObjectId)", example = "66c3abc1234567890abcdef1")
    private String id;

    @Schema(description = "Company ID posting the vacancy", example = "10")
    private Long companyId;

    @Schema(description = "Job title", example = "Senior Backend Engineer")
    private String title;

    @Schema(description = "Full description and requirements")
    private String description;

    @Schema(description = "Required skills", example = "Java, Spring Boot, MongoDB, Docker")
    private String requiredSkills;

    @Schema(description = "Location", example = "San Francisco, CA / Hybrid")
    private String location;

    @Schema(description = "Salary range", example = "$140,000 - $180,000 / yr")
    private String salaryRange;

    @Schema(description = "Job type", example = "FULL_TIME")
    private String jobType;

    @Schema(description = "Job status", example = "OPEN")
    private JobStatus status;

    @Schema(description = "Application deadline timestamp", example = "2026-12-31T23:59:59")
    private LocalDateTime deadline;

    @Schema(description = "Total number of candidates applied", example = "12")
    private Long applicationCount;

    @Schema(description = "Posting creation timestamp")
    private LocalDateTime postedAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    public static JobResponse fromEntity(Job job) {
        return fromEntity(job, 0L);
    }

    public static JobResponse fromEntity(Job job, long applicationCount) {
        if (job == null) return null;
        return JobResponse.builder()
                .id(job.getId())
                .companyId(job.getCompanyId())
                .title(job.getTitle())
                .description(job.getDescription())
                .requiredSkills(job.getRequiredSkills())
                .location(job.getLocation())
                .salaryRange(job.getSalaryRange())
                .jobType(job.getJobType())
                .status(job.getStatus())
                .deadline(job.getDeadline())
                .applicationCount(applicationCount)
                .postedAt(job.getPostedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}
