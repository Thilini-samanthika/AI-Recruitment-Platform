package com.recruitment.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Skill Extraction Result")
public class SkillExtractionResponse {

    @Schema(description = "Resume ID", example = "1")
    private Long resumeId;

    @Schema(description = "Processing status", example = "PARSED")
    private String status;

    @Schema(description = "Total skills identified", example = "12")
    private Integer totalSkillsFound;

    @Schema(description = "List of extracted skills", example = "[\"Java\", \"Spring Boot\", \"Docker\", \"Kubernetes\", \"React\"]")
    private List<String> extractedSkills;

    @Schema(description = "Extracted text snippet preview")
    private String textPreview;
}
