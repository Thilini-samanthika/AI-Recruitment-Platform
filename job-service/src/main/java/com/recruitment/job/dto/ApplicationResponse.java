package com.recruitment.job.dto;

import com.recruitment.job.entity.Application;
import com.recruitment.job.entity.ApplicationStatus;
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
@Schema(description = "Job application details response")
public class ApplicationResponse {

    @Schema(description = "Application ID (MongoDB ObjectId)", example = "66c3abc1234567890abcdef2")
    private String id;

    @Schema(description = "Job ID (MongoDB ObjectId)", example = "66c3abc1234567890abcdef1")
    private String jobId;

    @Schema(description = "Job Title", example = "Senior Backend Engineer")
    private String jobTitle;

    @Schema(description = "Company ID", example = "10")
    private Long companyId;

    @Schema(description = "Candidate ID", example = "1")
    private Long candidateId;

    @Schema(description = "Application status (APPLIED, REVIEWED, SHORTLISTED, INTERVIEW, OFFERED, ACCEPTED, REJECTED, WITHDRAWN)", example = "APPLIED")
    private ApplicationStatus status;

    @Schema(description = "Candidate notes / cover pitch", example = "Strong background in Spring Boot microservices")
    private String notes;

    @Schema(description = "Resume URL", example = "https://storage.googleapis.com/recruitment-resumes/cand_1_resume.pdf")
    private String resumeUrl;

    @Schema(description = "Application submission timestamp")
    private LocalDateTime appliedAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    public static ApplicationResponse fromEntity(Application application) {
        if (application == null) return null;
        return ApplicationResponse.builder()
                .id(application.getId())
                .jobId(application.getJobId())
                .jobTitle(application.getJobTitle())
                .companyId(application.getCompanyId())
                .candidateId(application.getCandidateId())
                .status(application.getStatus())
                .notes(application.getNotes())
                .resumeUrl(application.getResumeUrl())
                .appliedAt(application.getAppliedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }
}
