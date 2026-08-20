package com.recruitment.ai.controller;

import com.recruitment.ai.dto.*;
import com.recruitment.ai.service.AiMatchingService;
import com.recruitment.ai.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "AI Resume & Matching API", description = "Member 5 Microservice: Endpoints for resume upload (PDF/DOCX/TXT), automated text & skill extraction, job compatibility matching, and intelligent candidate recommendations")
public class ResumeController {

    private final ResumeService resumeService;
    private final AiMatchingService aiMatchingService;

    @Operation(
            summary = "Health check",
            description = "Checks the operational health and cluster status of the AI Resume Service microservice on port 8085."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Service is UP and operational",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping({"/api/resume/health", "/api/ai/health"})
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        return ResponseEntity.ok(ApiResponse.success("AI Resume Service is healthy", Map.of(
                "service", "ai-service",
                "port", "8085",
                "status", "UP",
                "database", "MongoDB (ai_db)",
                "member", "Member 5 - AI Resume & Recommendation Lead"
        )));
    }

    @Operation(
            summary = "Upload resume file",
            description = "Uploads a candidate's resume file, extracts plain text using Apache PDFBox (PDF) or Apache POI (DOCX), extracts categorized skills via NLP dictionary pattern matching, and persists metadata in MongoDB (ai_db). Accepted formats: PDF (.pdf), Microsoft Word (.docx), Plain Text (.txt, .md). Maximum file size limit: 15MB.",
            security = { @SecurityRequirement(name = "ApiKeyAuth"), @SecurityRequirement(name = "BearerAuth") }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Resume uploaded, parsed, and indexed in MongoDB successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad Request - Invalid candidate ID, unsupported file extension, or unreadable file content"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid API Key/JWT Authorization token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "413", description = "Payload Too Large - File size exceeds the configured 15MB limit")
    })
    @PostMapping(value = "/api/resume/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ResumeResponse>> uploadResume(
            @Parameter(description = "Numeric ID of the candidate owning the resume (e.g. 1)", required = true, example = "1")
            @RequestParam("candidateId") @NotNull(message = "candidateId is required") @Min(value = 1, message = "candidateId must be greater than 0") Long candidateId,
            @Parameter(
                    description = "Resume document file (Accepted: .pdf, .docx, .txt; Max size: 15MB)",
                    required = true,
                    schema = @Schema(type = "string", format = "binary", description = "Binary file payload (.pdf, .docx, .txt)")
            )
            @RequestPart("file") @NotNull(message = "file must be provided") MultipartFile file) {

        log.info("Received resume upload request for candidate ID: {}, filename: {}, size: {} bytes",
                candidateId, file.getOriginalFilename(), file.getSize());
        ResumeResponse response = resumeService.uploadResume(candidateId, file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Resume uploaded and parsed successfully", response));
    }

    @Operation(
            summary = "Extract text and skills from resume",
            description = "Re-analyzes an existing resume by ID, re-extracts skills and keywords using NLP dictionary pattern matching, and updates the resume record in MongoDB.",
            security = { @SecurityRequirement(name = "ApiKeyAuth"), @SecurityRequirement(name = "BearerAuth") }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Skills extracted and updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Resume not found with provided ID"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid API Key/Token")
    })
    @PostMapping("/api/resume/extract/{resumeId}")
    public ResponseEntity<ApiResponse<SkillExtractionResponse>> extractSkills(
            @Parameter(description = "Unique Resume Identifier or MongoDB ObjectId", required = true, example = "66c43ab2f89a120001bc34e1")
            @PathVariable("resumeId") @NotBlank(message = "resumeId cannot be blank") String resumeId) {

        log.info("Extracting skills for resume ID: {}", resumeId);
        SkillExtractionResponse response = resumeService.extractSkills(resumeId);
        return ResponseEntity.ok(ApiResponse.success("Resume skills extracted successfully", response));
    }

    @Operation(
            summary = "Get all resumes for a candidate",
            description = "Retrieves all uploaded resumes, parsed text previews, and extracted skill arrays for a specific candidate ID from MongoDB.",
            security = { @SecurityRequirement(name = "ApiKeyAuth"), @SecurityRequirement(name = "BearerAuth") }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Candidate resumes retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid API Key/Token")
    })
    @GetMapping("/api/resume/{candidateId}")
    public ResponseEntity<ApiResponse<List<ResumeResponse>>> getResumesByCandidate(
            @Parameter(description = "Candidate ID", required = true, example = "1")
            @PathVariable("candidateId") @NotNull(message = "candidateId is required") @Min(value = 1, message = "candidateId must be greater than 0") Long candidateId) {

        log.info("Retrieving resumes for candidate ID: {}", candidateId);
        List<ResumeResponse> list = resumeService.getResumesByCandidate(candidateId);
        return ResponseEntity.ok(ApiResponse.success("Candidate resumes retrieved successfully", list));
    }

    @Operation(
            summary = "Get resume details by Resume ID",
            description = "Retrieves specific resume details and skill taxonomy by its primary key / MongoDB ObjectId.",
            security = { @SecurityRequirement(name = "ApiKeyAuth"), @SecurityRequirement(name = "BearerAuth") }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Resume retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Resume not found with provided ID"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid API Key/Token")
    })
    @GetMapping("/api/resume/id/{resumeId}")
    public ResponseEntity<ApiResponse<ResumeResponse>> getResumeById(
            @Parameter(description = "Resume ID or MongoDB ObjectId", required = true, example = "66c43ab2f89a120001bc34e1")
            @PathVariable("resumeId") @NotBlank(message = "resumeId cannot be blank") String resumeId) {

        ResumeResponse response = resumeService.getResumeById(resumeId);
        return ResponseEntity.ok(ApiResponse.success("Resume retrieved successfully", response));
    }

    @Operation(
            summary = "Download raw resume file",
            description = "Streams the raw uploaded resume file (.pdf, .docx, .txt) from storage disk with appropriate content disposition headers."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Binary file stream returned"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Resume file not found on disk")
    })
    @GetMapping("/api/resume/file/{resumeId}")
    public ResponseEntity<Resource> downloadResumeFile(
            @Parameter(description = "Resume ID or MongoDB ObjectId", required = true, example = "66c43ab2f89a120001bc34e1")
            @PathVariable("resumeId") @NotBlank(message = "resumeId cannot be blank") String resumeId) {

        ResumeResponse meta = resumeService.getResumeById(resumeId);
        Resource resource = resumeService.getResumeFileResource(resumeId);

        String contentType = meta.getFileType() != null ? meta.getFileType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        String fileName = meta.getFileName() != null ? meta.getFileName() : "resume_" + resumeId;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @Operation(
            summary = "Match resume against job posting",
            description = "Calculates semantic compatibility score (0-100%), identifies matched competencies and missing skill gaps, and returns a detailed AI fit assessment summary.",
            security = { @SecurityRequirement(name = "ApiKeyAuth"), @SecurityRequirement(name = "BearerAuth") }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Match computed and recorded in MongoDB successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad Request - Invalid request payload or missing job ID"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid API Key/Token")
    })
    @PostMapping("/api/match")
    public ResponseEntity<ApiResponse<MatchResponse>> matchResume(
            @Valid @RequestBody MatchRequest request) {

        log.info("Matching resume (ID: {}, Candidate: {}) against Job ID: {}",
                request.getResumeId(), request.getCandidateId(), request.getJobId());
        MatchResponse response = aiMatchingService.matchResumeWithJob(request);
        return ResponseEntity.ok(ApiResponse.success("Job match analysis completed successfully", response));
    }

    @Operation(
            summary = "Get recommended jobs for candidate",
            description = "Returns top AI-recommended job opportunities tailored to the candidate's verified resume skills from MongoDB.",
            security = { @SecurityRequirement(name = "ApiKeyAuth"), @SecurityRequirement(name = "BearerAuth") }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Recommendations retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid API Key/Token")
    })
    @GetMapping("/api/recommendations/{candidateId}")
    public ResponseEntity<ApiResponse<List<RecommendationResponse>>> getRecommendations(
            @Parameter(description = "Candidate ID", required = true, example = "1")
            @PathVariable("candidateId") @NotNull(message = "candidateId is required") @Min(value = 1, message = "candidateId must be greater than 0") Long candidateId) {

        log.info("Fetching job recommendations for candidate ID: {}", candidateId);
        List<RecommendationResponse> recommendations = aiMatchingService.getRecommendations(candidateId);
        return ResponseEntity.ok(ApiResponse.success("Job recommendations retrieved successfully", recommendations));
    }
}
