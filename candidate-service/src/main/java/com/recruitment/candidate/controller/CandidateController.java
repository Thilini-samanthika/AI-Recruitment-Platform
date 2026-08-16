package com.recruitment.candidate.controller;

import com.recruitment.candidate.dto.*;
import com.recruitment.candidate.service.CandidateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
@Tag(name = "Candidate Management", description = "Endpoints for candidate profiles, skills, education, and experience")
public class CandidateController {

    private final CandidateService candidateService;

    // ==========================================
    // CANDIDATE PROFILE ENDPOINTS
    // ==========================================

    @PostMapping
    @Operation(summary = "Create candidate profile", description = "Creates a new candidate profile linked to a user account.")
    public ResponseEntity<ApiResponse<CandidateResponse>> createCandidate(
            @Valid @RequestBody CreateCandidateRequest request,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId) {

        log.info("REST request to create candidate profile: {}", request.getFullName());
        CandidateResponse response = candidateService.createCandidate(request, authenticatedUserId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Candidate profile created successfully", response));
    }

    @GetMapping
    @Operation(summary = "List all candidates", description = "Retrieves a list of all candidate profiles.")
    public ResponseEntity<ApiResponse<List<CandidateResponse>>> getAllCandidates() {
        log.info("REST request to retrieve all candidate profiles");
        List<CandidateResponse> candidates = candidateService.getAllCandidates();
        return ResponseEntity.ok(ApiResponse.success("Candidates retrieved successfully", candidates));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get candidate by ID", description = "Retrieves full candidate profile details by Candidate ID.")
    public ResponseEntity<ApiResponse<CandidateResponse>> getCandidateById(
            @PathVariable("id") Long id) {

        log.info("REST request to get candidate profile ID: {}", id);
        CandidateResponse response = candidateService.getCandidateById(id);
        return ResponseEntity.ok(ApiResponse.success("Candidate profile retrieved successfully", response));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current candidate profile", description = "Retrieves profile of the authenticated user forwarding X-User-Id.")
    public ResponseEntity<ApiResponse<CandidateResponse>> getCurrentCandidate(
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = true) Long authenticatedUserId) {

        log.info("REST request to get candidate profile for authenticated user ID: {}", authenticatedUserId);
        CandidateResponse response = candidateService.getCandidateByUserId(authenticatedUserId);
        return ResponseEntity.ok(ApiResponse.success("Candidate profile retrieved successfully", response));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get candidate by User ID", description = "Retrieves candidate profile linked to a specific Auth Service User ID.")
    public ResponseEntity<ApiResponse<CandidateResponse>> getCandidateByUserId(
            @PathVariable("userId") Long userId) {

        log.info("REST request to get candidate profile for user ID: {}", userId);
        CandidateResponse response = candidateService.getCandidateByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Candidate profile retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update candidate profile", description = "Updates details of an existing candidate profile.")
    public ResponseEntity<ApiResponse<CandidateResponse>> updateCandidate(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateCandidateRequest request,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Role", required = false) String authenticatedRole) {

        log.info("REST request to update candidate profile ID: {}", id);
        CandidateResponse response = candidateService.updateCandidate(id, request, authenticatedUserId, authenticatedRole);
        return ResponseEntity.ok(ApiResponse.success("Candidate profile updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete candidate profile", description = "Deletes a candidate profile and all associated data.")
    public ResponseEntity<ApiResponse<Void>> deleteCandidate(
            @PathVariable("id") Long id,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Role", required = false) String authenticatedRole) {

        log.info("REST request to delete candidate profile ID: {}", id);
        candidateService.deleteCandidate(id, authenticatedUserId, authenticatedRole);
        return ResponseEntity.ok(ApiResponse.success("Candidate profile deleted successfully", null));
    }

    // ==========================================
    // SKILLS ENDPOINTS
    // ==========================================

    @PostMapping("/{id}/skills")
    @Operation(summary = "Add skill to candidate", description = "Adds a new skill entry to a candidate's profile.")
    public ResponseEntity<ApiResponse<SkillResponse>> addSkill(
            @PathVariable("id") Long id,
            @Valid @RequestBody SkillRequest request,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Role", required = false) String authenticatedRole) {

        log.info("REST request to add skill '{}' to candidate ID: {}", request.getSkillName(), id);
        SkillResponse response = candidateService.addSkill(id, request, authenticatedUserId, authenticatedRole);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Skill added successfully", response));
    }

    @GetMapping("/{id}/skills")
    @Operation(summary = "List candidate skills", description = "Retrieves all skills for a candidate.")
    public ResponseEntity<ApiResponse<List<SkillResponse>>> getCandidateSkills(
            @PathVariable("id") Long id) {

        log.info("REST request to get skills for candidate ID: {}", id);
        List<SkillResponse> skills = candidateService.getSkills(id);
        return ResponseEntity.ok(ApiResponse.success("Skills retrieved successfully", skills));
    }

    @DeleteMapping("/{id}/skills/{skillId}")
    @Operation(summary = "Delete candidate skill", description = "Removes a skill from a candidate's profile.")
    public ResponseEntity<ApiResponse<Void>> deleteSkill(
            @PathVariable("id") Long id,
            @PathVariable("skillId") Long skillId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Role", required = false) String authenticatedRole) {

        log.info("REST request to delete skill ID: {} from candidate ID: {}", skillId, id);
        candidateService.deleteSkill(id, skillId, authenticatedUserId, authenticatedRole);
        return ResponseEntity.ok(ApiResponse.success("Skill deleted successfully", null));
    }

    // ==========================================
    // EDUCATION ENDPOINTS
    // ==========================================

    @PostMapping("/{id}/education")
    @Operation(summary = "Add education to candidate", description = "Adds an education record to a candidate's profile.")
    public ResponseEntity<ApiResponse<EducationResponse>> addEducation(
            @PathVariable("id") Long id,
            @Valid @RequestBody EducationRequest request,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Role", required = false) String authenticatedRole) {

        log.info("REST request to add education from '{}' to candidate ID: {}", request.getInstitution(), id);
        EducationResponse response = candidateService.addEducation(id, request, authenticatedUserId, authenticatedRole);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Education record added successfully", response));
    }

    @GetMapping("/{id}/education")
    @Operation(summary = "List candidate education records", description = "Retrieves all education records for a candidate.")
    public ResponseEntity<ApiResponse<List<EducationResponse>>> getCandidateEducation(
            @PathVariable("id") Long id) {

        log.info("REST request to get education for candidate ID: {}", id);
        List<EducationResponse> educations = candidateService.getEducation(id);
        return ResponseEntity.ok(ApiResponse.success("Education records retrieved successfully", educations));
    }

    @DeleteMapping("/{id}/education/{educationId}")
    @Operation(summary = "Delete candidate education", description = "Removes an education record from a candidate's profile.")
    public ResponseEntity<ApiResponse<Void>> deleteEducation(
            @PathVariable("id") Long id,
            @PathVariable("educationId") Long educationId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Role", required = false) String authenticatedRole) {

        log.info("REST request to delete education ID: {} from candidate ID: {}", educationId, id);
        candidateService.deleteEducation(id, educationId, authenticatedUserId, authenticatedRole);
        return ResponseEntity.ok(ApiResponse.success("Education record deleted successfully", null));
    }

    // ==========================================
    // EXPERIENCE ENDPOINTS
    // ==========================================

    @PostMapping("/{id}/experience")
    @Operation(summary = "Add experience to candidate", description = "Adds a work experience record to a candidate's profile.")
    public ResponseEntity<ApiResponse<ExperienceResponse>> addExperience(
            @PathVariable("id") Long id,
            @Valid @RequestBody ExperienceRequest request,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Role", required = false) String authenticatedRole) {

        log.info("REST request to add experience at '{}' to candidate ID: {}", request.getCompanyName(), id);
        ExperienceResponse response = candidateService.addExperience(id, request, authenticatedUserId, authenticatedRole);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Experience record added successfully", response));
    }

    @GetMapping("/{id}/experience")
    @Operation(summary = "List candidate experience records", description = "Retrieves all work experience records for a candidate.")
    public ResponseEntity<ApiResponse<List<ExperienceResponse>>> getCandidateExperience(
            @PathVariable("id") Long id) {

        log.info("REST request to get experience for candidate ID: {}", id);
        List<ExperienceResponse> experiences = candidateService.getExperience(id);
        return ResponseEntity.ok(ApiResponse.success("Experience records retrieved successfully", experiences));
    }

    @DeleteMapping("/{id}/experience/{experienceId}")
    @Operation(summary = "Delete candidate experience", description = "Removes an experience record from a candidate's profile.")
    public ResponseEntity<ApiResponse<Void>> deleteExperience(
            @PathVariable("id") Long id,
            @PathVariable("experienceId") Long experienceId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Role", required = false) String authenticatedRole) {

        log.info("REST request to delete experience ID: {} from candidate ID: {}", experienceId, id);
        candidateService.deleteExperience(id, experienceId, authenticatedUserId, authenticatedRole);
        return ResponseEntity.ok(ApiResponse.success("Experience record deleted successfully", null));
    }
}
