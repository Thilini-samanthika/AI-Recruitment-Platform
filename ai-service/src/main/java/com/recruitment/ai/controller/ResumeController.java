package com.recruitment.ai.controller;

import com.recruitment.ai.dto.*;
import com.recruitment.ai.service.AiMatchingService;
import com.recruitment.ai.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "AI Resume & Matching API", description = "Endpoints for resume upload, automated text/skill extraction, job matching, and candidate recommendations")
public class ResumeController {

    private final ResumeService resumeService;
    private final AiMatchingService aiMatchingService;

    @Operation(
            summary = "Health check",
            description = "Checks the health status of the AI Resume Service microservice"
    )
    @GetMapping({"/api/resume/health", "/api/ai/health"})
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        return ResponseEntity.ok(ApiResponse.success("AI Resume Service is healthy", Map.of(
                "service", "ai-service",
                "port", "8085",
                "status", "UP",
                "member", "Member 5"
        )));
    }

    @Operation(
            summary = "Upload resume file",
            description = "Uploads a candidate's resume (PDF, DOCX, or TXT), extracts text and skills, and stores metadata in MySQL database.",
            security = { @SecurityRequirement(name = "ApiKeyAuth"), @SecurityRequirement(name = "BearerAuth") }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Resume uploaded and processed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid file or candidate ID"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid API Key/Token")
    })
    @PostMapping(value = "/api/resume/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ResumeResponse>> uploadResume(
            @Parameter(description = "Candidate ID owning the resume", required = true, example = "1")
            @RequestParam("candidateId") Long candidateId,
            @Parameter(description = "Resume file (.pdf, .docx, or .txt)", required = true)
            @RequestPart("file") MultipartFile file) {

        log.info("Received resume upload request for candidate ID: {}, filename: {}", candidateId, file.getOriginalFilename());
        ResumeResponse response = resumeService.uploadResume(candidateId, file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Resume uploaded and parsed successfully", response));
    }

    @Operation(
            summary = "Extract text and skills from resume",
            description = "Re-analyzes and extracts skills and keywords from an uploaded resume using NLP / keyword dictionary.",
            security = { @SecurityRequirement(name = "ApiKeyAuth"), @SecurityRequirement(name = "BearerAuth") }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Skills extracted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Resume not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/api/resume/extract/{resumeId}")
    public ResponseEntity<ApiResponse<SkillExtractionResponse>> extractSkills(
            @Parameter(description = "Resume ID", required = true, example = "1")
            @PathVariable("resumeId") Long resumeId) {

        log.info("Extracting skills for resume ID: {}", resumeId);
        SkillExtractionResponse response = resumeService.extractSkills(resumeId);
        return ResponseEntity.ok(ApiResponse.success("Resume skills extracted successfully", response));
    }

    @Operation(
            summary = "Get all resumes for a candidate",
            description = "Retrieves all uploaded resumes, parsed texts, and extracted skills for a specific candidate ID.",
            security = { @SecurityRequirement(name = "ApiKeyAuth"), @SecurityRequirement(name = "BearerAuth") }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Resumes retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/api/resume/{candidateId}")
    public ResponseEntity<ApiResponse<List<ResumeResponse>>> getResumesByCandidate(
            @Parameter(description = "Candidate ID", required = true, example = "1")
            @PathVariable("candidateId") Long candidateId) {

        log.info("Retrieving resumes for candidate ID: {}", candidateId);
        List<ResumeResponse> list = resumeService.getResumesByCandidate(candidateId);
        return ResponseEntity.ok(ApiResponse.success("Candidate resumes retrieved successfully", list));
    }

    @Operation(
            summary = "Get resume details by Resume ID",
            description = "Retrieves specific resume details by its primary key ID.",
            security = { @SecurityRequirement(name = "ApiKeyAuth"), @SecurityRequirement(name = "BearerAuth") }
    )
    @GetMapping("/api/resume/id/{resumeId}")
    public ResponseEntity<ApiResponse<ResumeResponse>> getResumeById(
            @Parameter(description = "Resume ID", required = true, example = "1")
            @PathVariable("resumeId") Long resumeId) {

        ResumeResponse response = resumeService.getResumeById(resumeId);
        return ResponseEntity.ok(ApiResponse.success("Resume retrieved successfully", response));
    }

    @Operation(
            summary = "Download raw resume file",
            description = "Streams the raw resume file (.pdf, .docx, .txt) from storage."
    )
    @GetMapping("/api/resume/file/{resumeId}")
    public ResponseEntity<Resource> downloadResumeFile(
            @Parameter(description = "Resume ID", required = true, example = "1")
            @PathVariable("resumeId") Long resumeId) {

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
            description = "Calculates compatibility score (0-100%), identifies matched and missing skills, and returns an AI analysis summary.",
            security = { @SecurityRequirement(name = "ApiKeyAuth"), @SecurityRequirement(name = "BearerAuth") }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Match computed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
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
            description = "Returns top AI-recommended job opportunities tailored to the candidate's verified resume skills.",
            security = { @SecurityRequirement(name = "ApiKeyAuth"), @SecurityRequirement(name = "BearerAuth") }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Recommendations retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/api/recommendations/{candidateId}")
    public ResponseEntity<ApiResponse<List<RecommendationResponse>>> getRecommendations(
            @Parameter(description = "Candidate ID", required = true, example = "1")
            @PathVariable("candidateId") Long candidateId) {

        log.info("Fetching job recommendations for candidate ID: {}", candidateId);
        List<RecommendationResponse> recommendations = aiMatchingService.getRecommendations(candidateId);
        return ResponseEntity.ok(ApiResponse.success("Job recommendations retrieved successfully", recommendations));
    }
}
