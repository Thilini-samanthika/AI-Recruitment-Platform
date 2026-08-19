package com.recruitment.candidate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @Size(min = 1, max = 50, message = "Skill name must be between 1 and 50 characters")
    @Schema(description = "Name of the skill", example = "Java", requiredMode = Schema.RequiredMode.REQUIRED)
    private String skillName;

    @Size(max = 30, message = "Proficiency level must not exceed 30 characters")
    @Schema(description = "Proficiency level (e.g. BEGINNER, INTERMEDIATE, ADVANCED, EXPERT)", example = "ADVANCED")
    private String proficiencyLevel;
}
