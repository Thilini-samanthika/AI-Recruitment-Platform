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
@Schema(description = "Job Recommendation for Candidate")
public class RecommendationResponse {

    @Schema(description = "Recommendation ID", example = "1")
    private Long id;

    @Schema(description = "Candidate ID", example = "101")
    private Long candidateId;

    @Schema(description = "Recommended Job ID", example = "5")
    private Long jobId;

    @Schema(description = "Job Title", example = "Senior Java Engineer")
    private String jobTitle;

    @Schema(description = "Company Name", example = "TechCorp Solutions")
    private String companyName;

    @Schema(description = "Recommendation Match Score (0 to 100)", example = "92.4")
    private Double score;

    @Schema(description = "Matched key skills", example = "[\"Java\", \"Spring Boot\", \"MySQL\"]")
    private List<String> matchedSkills;

    @Schema(description = "Recommendation generated timestamp")
    private Instant createdAt;
}
