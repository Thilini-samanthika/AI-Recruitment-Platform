package com.recruitment.candidate.controller;

import com.recruitment.candidate.dto.*;
import com.recruitment.candidate.service.CandidateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "Candidate Management", description = "Endpoints for candidate profile creation, retrieval, updates, skills, education, and experience")
public class CandidateController {

    private final CandidateService candidateService;

    // ==========================================
    // CANDIDATE PROFILE ENDPOINTS
    // ==========================================

    @PostMapping
    @Operation(
            summary = "Create candidate profile",
            description = "Creates a new candidate profile linked to an authenticated user account.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Candidate profile created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CandidateResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or validation failed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid API key / JWT authentication",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "409", description = "Candidate profile already exists for this User ID",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.candidate.dto.ApiResponse<CandidateResponse>> createCandidate(
            @Valid @RequestBody CreateCandidateRequest request,
            @Parameter(description = "Authenticated User ID forwarded by Gateway", hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId) {

        log.info("REST request to create candidate profile: {}", request.getFullName());
        CandidateResponse response = candidateService.createCandidate(request, authenticatedUserId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.recruitment.candidate.dto.ApiResponse.success("Candidate profile created successfully", response));
    }

    @GetMapping
    @Operation(
            summary = "List all candidates",
            description = "Retrieves a list of all registered candidate profiles with embedded skills, education, and experience.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Candidate profiles retrieved successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CandidateResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.candidate.dto.ApiResponse<List<CandidateResponse>>> getAllCandidates() {
        log.info("REST request to retrieve all candidate profiles");
        List<CandidateResponse> candidates = candidateService.getAllCandidates();
        return ResponseEntity.ok(com.recruitment.candidate.dto.ApiResponse.success("Candidates retrieved successfully", candidates));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get candidate by ID",
            description = "Retrieves full candidate profile details by Candidate unique MongoDB ID.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Candidate profile retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CandidateResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Candidate profile not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.candidate.dto.ApiResponse<CandidateResponse>> getCandidateById(
            @Parameter(description = "Candidate Profile ID (MongoDB ObjectId)", required = true, example = "66c3abc1234567890abcdef1")
            @PathVariable("id") String id) {

        log.info("REST request to get candidate profile ID: {}", id);
        CandidateResponse response = candidateService.getCandidateById(id);
        return ResponseEntity.ok(com.recruitment.candidate.dto.ApiResponse.success("Candidate profile retrieved successfully", response));
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get current candidate profile",
            description = "Retrieves profile of the currently authenticated user based on the forwarded X-User-Id header.",
            security = { @SecurityRequirement(name = "bearerAuth"), @SecurityRequirement(name = "apiKeyAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Current candidate profile retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CandidateResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing user context",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Candidate profile not found for the authenticated user",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.candidate.dto.ApiResponse<CandidateResponse>> getCurrentCandidate(
            @Parameter(description = "Authenticated User ID forwarded by Gateway", required = true, example = "1")
            @RequestHeader(value = "X-User-Id", required = true) Long authenticatedUserId) {

        log.info("REST request to get candidate profile for authenticated user ID: {}", authenticatedUserId);
        CandidateResponse response = candidateService.getCandidateByUserId(authenticatedUserId);
        return ResponseEntity.ok(com.recruitment.candidate.dto.ApiResponse.success("Candidate profile retrieved successfully", response));
    }

    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Get candidate by User ID",
            description = "Retrieves candidate profile linked to a specific Auth Service User ID.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Candidate profile retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CandidateResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Candidate profile not found for user ID",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.candidate.dto.ApiResponse<CandidateResponse>> getCandidateByUserId(
            @Parameter(description = "Auth Service User ID", required = true, example = "1")
            @PathVariable("userId") Long userId) {

        log.info("REST request to get candidate profile for user ID: {}", userId);
        CandidateResponse response = candidateService.getCandidateByUserId(userId);
        return ResponseEntity.ok(com.recruitment.candidate.dto.ApiResponse.success("Candidate profile retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update candidate profile",
            description = "Updates details of an existing candidate profile.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Candidate profile updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CandidateResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or validation failed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized to update this profile",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Candidate profile not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.candidate.dto.ApiResponse<CandidateResponse>> updateCandidate(
            @Parameter(description = "Candidate Profile ID (MongoDB ObjectId)", required = true, example = "66c3abc1234567890abcdef1")
            @PathVariable("id") String id,
            @Valid @RequestBody UpdateCandidateRequest request,
            @Parameter(description = "Authenticated User ID", hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId,
            @Parameter(description = "Authenticated User Role", hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String authenticatedRole) {

        log.info("REST request to update candidate profile ID: {}", id);
        CandidateResponse response = candidateService.updateCandidate(id, request, authenticatedUserId, authenticatedRole);
        return ResponseEntity.ok(com.recruitment.candidate.dto.ApiResponse.success("Candidate profile updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete candidate profile",
            description = "Deletes a candidate profile along with all embedded skills, education, and experience data.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Candidate profile deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized to delete this profile",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Candidate profile not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.candidate.dto.ApiResponse<Void>> deleteCandidate(
            @Parameter(description = "Candidate Profile ID (MongoDB ObjectId)", required = true, example = "66c3abc1234567890abcdef1")
            @PathVariable("id") String id,
            @Parameter(description = "Authenticated User ID", hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId,
            @Parameter(description = "Authenticated User Role", hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String authenticatedRole) {

        log.info("REST request to delete candidate profile ID: {}", id);
        candidateService.deleteCandidate(id, authenticatedUserId, authenticatedRole);
        return ResponseEntity.ok(com.recruitment.candidate.dto.ApiResponse.success("Candidate profile deleted successfully", null));
    }

    // ==========================================
    // SKILLS ENDPOINTS
    // ==========================================

    @PostMapping("/{id}/skills")
    @Operation(
            summary = "Add skill to candidate",
            description = "Appends a new skill item into candidate's embedded skills list.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Skill added successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SkillResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or validation failed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized to modify candidate skills",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Candidate profile not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.candidate.dto.ApiResponse<SkillResponse>> addSkill(
            @Parameter(description = "Candidate Profile ID (MongoDB ObjectId)", required = true, example = "66c3abc1234567890abcdef1")
            @PathVariable("id") String id,
            @Valid @RequestBody SkillRequest request,
            @Parameter(description = "Authenticated User ID", hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId,
            @Parameter(description = "Authenticated User Role", hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String authenticatedRole) {

        log.info("REST request to add skill '{}' to candidate ID: {}", request.getSkillName(), id);
        SkillResponse response = candidateService.addSkill(id, request, authenticatedUserId, authenticatedRole);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.recruitment.candidate.dto.ApiResponse.success("Skill added successfully", response));
    }

    @GetMapping("/{id}/skills")
    @Operation(
            summary = "List candidate skills",
            description = "Retrieves all embedded skills for a candidate.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Skills retrieved successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SkillResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Candidate profile not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.candidate.dto.ApiResponse<List<SkillResponse>>> getCandidateSkills(
            @Parameter(description = "Candidate Profile ID (MongoDB ObjectId)", required = true, example = "66c3abc1234567890abcdef1")
            @PathVariable("id") String id) {

        log.info("REST request to get skills for candidate ID: {}", id);
        List<SkillResponse> skills = candidateService.getSkills(id);
        return ResponseEntity.ok(com.recruitment.candidate.dto.ApiResponse.success("Skills retrieved successfully", skills));
    }

    @DeleteMapping("/{id}/skills/{skillId}")
    @Operation(
            summary = "Delete candidate skill",
            description = "Removes a skill from candidate's embedded skills list.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Skill deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized to delete this skill",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Candidate profile or skill ID not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.candidate.dto.ApiResponse<Void>> deleteSkill(
            @Parameter(description = "Candidate Profile ID (MongoDB ObjectId)", required = true, example = "66c3abc1234567890abcdef1")
            @PathVariable("id") String id,
            @Parameter(description = "Skill unique ID", required = true, example = "skill-9e3b4a2c-1234")
            @PathVariable("skillId") String skillId,
            @Parameter(description = "Authenticated User ID", hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId,
            @Parameter(description = "Authenticated User Role", hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String authenticatedRole) {

        log.info("REST request to delete skill ID: {} from candidate ID: {}", skillId, id);
        candidateService.deleteSkill(id, skillId, authenticatedUserId, authenticatedRole);
        return ResponseEntity.ok(com.recruitment.candidate.dto.ApiResponse.success("Skill deleted successfully", null));
    }

    // ==========================================
    // EDUCATION ENDPOINTS
    // ==========================================

    @PostMapping("/{id}/education")
    @Operation(
            summary = "Add education to candidate",
            description = "Appends an education record into candidate's embedded education list.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Education record added successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EducationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or validation failed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized to modify education",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Candidate profile not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.candidate.dto.ApiResponse<EducationResponse>> addEducation(
            @Parameter(description = "Candidate Profile ID (MongoDB ObjectId)", required = true, example = "66c3abc1234567890abcdef1")
            @PathVariable("id") String id,
            @Valid @RequestBody EducationRequest request,
            @Parameter(description = "Authenticated User ID", hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId,
            @Parameter(description = "Authenticated User Role", hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String authenticatedRole) {

        log.info("REST request to add education from '{}' to candidate ID: {}", request.getInstitution(), id);
        EducationResponse response = candidateService.addEducation(id, request, authenticatedUserId, authenticatedRole);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.recruitment.candidate.dto.ApiResponse.success("Education record added successfully", response));
    }

    @GetMapping("/{id}/education")
    @Operation(
            summary = "List candidate education records",
            description = "Retrieves all embedded education records for a candidate.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Education records retrieved successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = EducationResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Candidate profile not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.candidate.dto.ApiResponse<List<EducationResponse>>> getCandidateEducation(
            @Parameter(description = "Candidate Profile ID (MongoDB ObjectId)", required = true, example = "66c3abc1234567890abcdef1")
            @PathVariable("id") String id) {

        log.info("REST request to get education for candidate ID: {}", id);
        List<EducationResponse> educations = candidateService.getEducation(id);
        return ResponseEntity.ok(com.recruitment.candidate.dto.ApiResponse.success("Education records retrieved successfully", educations));
    }

    @DeleteMapping("/{id}/education/{educationId}")
    @Operation(
            summary = "Delete candidate education",
            description = "Removes an education record from candidate's embedded education list.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Education record deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized to delete this education",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Candidate profile or education ID not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.candidate.dto.ApiResponse<Void>> deleteEducation(
            @Parameter(description = "Candidate Profile ID (MongoDB ObjectId)", required = true, example = "66c3abc1234567890abcdef1")
            @PathVariable("id") String id,
            @Parameter(description = "Education unique ID", required = true, example = "edu-7c1a8e9f-5678")
            @PathVariable("educationId") String educationId,
            @Parameter(description = "Authenticated User ID", hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId,
            @Parameter(description = "Authenticated User Role", hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String authenticatedRole) {

        log.info("REST request to delete education ID: {} from candidate ID: {}", educationId, id);
        candidateService.deleteEducation(id, educationId, authenticatedUserId, authenticatedRole);
        return ResponseEntity.ok(com.recruitment.candidate.dto.ApiResponse.success("Education record deleted successfully", null));
    }

    // ==========================================
    // EXPERIENCE ENDPOINTS
    // ==========================================

    @PostMapping("/{id}/experience")
    @Operation(
            summary = "Add experience to candidate",
            description = "Appends a work experience record into candidate's embedded experience list.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Experience record added successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExperienceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or validation failed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized to modify experience",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Candidate profile not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.candidate.dto.ApiResponse<ExperienceResponse>> addExperience(
            @Parameter(description = "Candidate Profile ID (MongoDB ObjectId)", required = true, example = "66c3abc1234567890abcdef1")
            @PathVariable("id") String id,
            @Valid @RequestBody ExperienceRequest request,
            @Parameter(description = "Authenticated User ID", hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId,
            @Parameter(description = "Authenticated User Role", hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String authenticatedRole) {

        log.info("REST request to add experience at '{}' to candidate ID: {}", request.getCompanyName(), id);
        ExperienceResponse response = candidateService.addExperience(id, request, authenticatedUserId, authenticatedRole);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.recruitment.candidate.dto.ApiResponse.success("Experience record added successfully", response));
    }

    @GetMapping("/{id}/experience")
    @Operation(
            summary = "List candidate experience records",
            description = "Retrieves all embedded work experience records for a candidate.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Experience records retrieved successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ExperienceResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Candidate profile not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.candidate.dto.ApiResponse<List<ExperienceResponse>>> getCandidateExperience(
            @Parameter(description = "Candidate Profile ID (MongoDB ObjectId)", required = true, example = "66c3abc1234567890abcdef1")
            @PathVariable("id") String id) {

        log.info("REST request to get experience for candidate ID: {}", id);
        List<ExperienceResponse> experiences = candidateService.getExperience(id);
        return ResponseEntity.ok(com.recruitment.candidate.dto.ApiResponse.success("Experience records retrieved successfully", experiences));
    }

    @DeleteMapping("/{id}/experience/{experienceId}")
    @Operation(
            summary = "Delete candidate experience",
            description = "Removes an experience record from candidate's embedded experience list.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Experience record deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized to delete this experience",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Candidate profile or experience ID not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.recruitment.candidate.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.candidate.dto.ApiResponse<Void>> deleteExperience(
            @Parameter(description = "Candidate Profile ID (MongoDB ObjectId)", required = true, example = "66c3abc1234567890abcdef1")
            @PathVariable("id") String id,
            @Parameter(description = "Experience unique ID", required = true, example = "exp-4b8c2d1e-9012")
            @PathVariable("experienceId") String experienceId,
            @Parameter(description = "Authenticated User ID", hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId,
            @Parameter(description = "Authenticated User Role", hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String authenticatedRole) {

        log.info("REST request to delete experience ID: {} from candidate ID: {}", experienceId, id);
        candidateService.deleteExperience(id, experienceId, authenticatedUserId, authenticatedRole);
        return ResponseEntity.ok(com.recruitment.candidate.dto.ApiResponse.success("Experience record deleted successfully", null));
    }
}
