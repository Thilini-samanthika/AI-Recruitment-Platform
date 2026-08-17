package com.recruitment.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI Job Matching Result")
public class MatchResponse {

    @Schema(description = "Match Result ID", example = "1")
    private Long id;

    @Schema(description = "Resume ID", example = "1")
    private Long resumeId;

    @Schema(description = "Candidate ID", example = "101")
    private Long candidateId;

    @Schema(description = "Job ID", example = "5")
    private Long jobId;

    @Schema(description = "Overall Match Percentage (0 to 100)", example = "87.5")
    private Double matchPercentage;

    @Schema(description = "List of candidate skills that matched the job requirements", example = "[\"Java\", \"Spring Boot\", \"MySQL\"]")
    private List<String> matchedSkills;

    @Schema(description = "List of required job skills that are missing in candidate resume", example = "[\"Kubernetes\", \"Kafka\"]")
    private List<String> missingSkills;

    @Schema(description = "All skills identified from candidate resume", example = "[\"Java\", \"Spring Boot\", \"MySQL\", \"React\", \"REST API\"]")
    private List<String> candidateSkills;

    @Schema(description = "All skills required by the job posting", example = "[\"Java\", \"Spring Boot\", \"MySQL\", \"Kubernetes\", \"Kafka\"]")
    private List<String> requiredSkills;

    @Schema(description = "AI summary and fit assessment", example = "Strong match for backend core competencies; candidate exceeds standard Java/Spring criteria.")
    private String analysisSummary;

    @Schema(description = "Match calculation timestamp")
    private Instant createdAt;
}
