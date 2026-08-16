package com.recruitment.candidate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for adding a candidate skill")
public class SkillRequest {

    @NotBlank(message = "Skill name is required")
    @Schema(description = "Name of the skill", example = "Java", requiredMode = Schema.RequiredMode.REQUIRED)
    private String skillName;

    @Schema(description = "Proficiency level (e.g. BEGINNER, INTERMEDIATE, ADVANCED, EXPERT)", example = "ADVANCED")
    private String proficiencyLevel;
}
