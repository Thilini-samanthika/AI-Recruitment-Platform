package com.recruitment.job.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for candidate applying to a job")
public class ApplyJobRequest {

    @NotNull(message = "Candidate ID is required")
    @Schema(description = "Candidate ID applying for the position", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long candidateId;

    @Schema(description = "Cover letter, candidate pitch, or notes", example = "I am excited to apply for this backend position. I have 6 years of Spring Boot microservices experience.")
    private String notes;

    @Schema(description = "Candidate resume URL or uploaded asset link", example = "https://storage.googleapis.com/recruitment-resumes/cand_1_resume.pdf")
    private String resumeUrl;
}
