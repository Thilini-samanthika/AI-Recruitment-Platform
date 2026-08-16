package com.recruitment.candidate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Candidate full profile response")
public class CandidateResponse {

    @Schema(description = "Candidate Profile ID", example = "10")
    private Long id;

    @Schema(description = "Auth Service User ID", example = "1")
    private Long userId;

    @Schema(description = "Full name of the candidate", example = "Alice Johnson")
    private String fullName;

    @Schema(description = "Contact phone number", example = "+1-555-0199")
    private String phone;

    @Schema(description = "Physical or mailing address", example = "123 Innovation Way, San Francisco, CA")
    private String address;

    @Schema(description = "Professional headline", example = "Senior Full Stack Software Engineer")
    private String headline;

    @Schema(description = "Professional summary / bio", example = "Passionate engineer with 6+ years experience...")
    private String summary;

    @Schema(description = "Profile creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Profile last updated timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "List of skills")
    @Builder.Default
    private List<SkillResponse> skills = new ArrayList<>();

    @Schema(description = "List of education records")
    @Builder.Default
    private List<EducationResponse> educations = new ArrayList<>();

    @Schema(description = "List of experience records")
    @Builder.Default
    private List<ExperienceResponse> experiences = new ArrayList<>();
}
