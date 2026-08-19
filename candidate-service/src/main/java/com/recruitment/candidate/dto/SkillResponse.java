package com.recruitment.candidate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Skill details response")
public class SkillResponse {

    @Schema(description = "Skill unique ID", example = "skill-9e3b4a2c-1234")
    private String id;

    @Schema(description = "Candidate Profile ID", example = "66c3abc1234567890abcdef1")
    private String candidateId;

    @Schema(description = "Name of the skill", example = "Java")
    private String skillName;

    @Schema(description = "Proficiency level", example = "ADVANCED")
    private String proficiencyLevel;
}
