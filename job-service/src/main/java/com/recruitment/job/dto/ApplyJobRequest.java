package com.recruitment.job.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request payload for candidate applying to a job posting")
public class ApplyJobRequest {

    @NotNull(message = "Candidate ID is required")
    @Schema(description = "Candidate ID applying for the position (must match authenticated candidate user ID)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long candidateId;

    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    @Schema(description = "Candidate cover letter, pitch, or application notes", example = "I am excited to apply for this backend position. I have 6 years of experience building Spring Boot microservices with MongoDB.")
    private String notes;

    @Size(max = 500, message = "Resume URL must not exceed 500 characters")
    @Schema(description = "URL to candidate resume PDF or profile asset", example = "https://storage.googleapis.com/recruitment-resumes/cand_1_resume.pdf")
    private String resumeUrl;
}
