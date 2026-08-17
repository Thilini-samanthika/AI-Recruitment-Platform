package com.recruitment.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload to match a resume against a job")
public class MatchRequest {

    @Schema(description = "Resume ID (optional if candidateId is provided)", example = "1")
    private Long resumeId;

    @Schema(description = "Candidate ID (optional if resumeId is provided)", example = "101")
    private Long candidateId;

    @NotNull(message = "Job ID is required")
    @Schema(description = "Job ID to match against", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long jobId;

    @Schema(description = "Job Title", example = "Senior Full Stack Java Engineer")
    private String jobTitle;

    @Schema(description = "Job Description", example = "We are seeking an experienced Java/React developer...")
    private String jobDescription;

    @Schema(description = "Required Skills list or keywords", example = "[\"Java\", \"Spring Boot\", \"React\", \"Docker\", \"MySQL\"]")
    private List<String> requiredSkills;
}
