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

    @Schema(description = "Skill ID", example = "1")
    private Long id;

    @Schema(description = "Candidate ID", example = "10")
    private Long candidateId;

    @Schema(description = "Name of the skill", example = "Java")
    private String skillName;

    @Schema(description = "Proficiency level", example = "ADVANCED")
    private String proficiencyLevel;
}
