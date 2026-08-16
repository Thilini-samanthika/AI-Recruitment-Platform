package com.recruitment.job.controller;

import com.recruitment.job.dto.*;
import com.recruitment.job.entity.JobStatus;
import com.recruitment.job.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Management", description = "Endpoints for job vacancy creation, search, updates, and candidate application workflows")
public class JobController {

    private final JobService jobService;

    @PostMapping
    @Operation(summary = "Post a new job opening", description = "Creates a new job listing on behalf of a registered company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Job posted successfully",
                    content = @Content(schema = @Schema(implementation = JobResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
    public ResponseEntity<com.recruitment.job.dto.ApiResponse<JobResponse>> postJob(
            @Valid @RequestBody CreateJobRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.info("REST request to post job: {}", request.getTitle());
        JobResponse response = jobService.createJob(request, userId, role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.recruitment.job.dto.ApiResponse.success("Job posted successfully", response));
    }

    @GetMapping
    @Operation(summary = "List all jobs", description = "Retrieves all job vacancies with optional status filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Jobs retrieved successfully")
    })
    public ResponseEntity<com.recruitment.job.dto.ApiResponse<List<JobResponse>>> getAllJobs(
            @Parameter(description = "Optional status filter (OPEN / CLOSED)")
            @RequestParam(required = false) JobStatus status) {
        log.debug("REST request to get all jobs with status: {}", status);
        List<JobResponse> jobs = jobService.getAllJobs(status);
        return ResponseEntity.ok(com.recruitment.job.dto.ApiResponse.success("Jobs retrieved successfully", jobs));
    }

    @GetMapping("/search")
    @Operation(summary = "Search jobs by criteria", description = "Searches jobs by keyword in title/skills/description, location, and job type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results returned")
    })
    public ResponseEntity<com.recruitment.job.dto.ApiResponse<List<JobResponse>>> searchJobs(
            @Parameter(description = "Search keyword (title, description, skills)")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "Location filter (e.g. Remote, San Francisco)")
            @RequestParam(required = false) String location,
            @Parameter(description = "Job type (e.g. FULL_TIME, INTERNSHIP)")
            @RequestParam(required = false) String jobType,
            @Parameter(description = "Job listing status (defaults to OPEN)")
            @RequestParam(required = false) JobStatus status) {
        log.debug("REST request to search jobs with keyword='{}', location='{}', jobType='{}'", keyword, location, jobType);
        List<JobResponse> results = jobService.searchJobs(keyword, location, jobType, status);
        return ResponseEntity.ok(com.recruitment.job.dto.ApiResponse.success("Job search results", results));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job details by ID", description = "Retrieves complete information for a specific job listing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job found"),
            @ApiResponse(responseCode = "404", description = "Job not found")
    })
    public ResponseEntity<com.recruitment.job.dto.ApiResponse<JobResponse>> getJobById(
            @Parameter(description = "Job ID", required = true) @PathVariable Long id) {
        log.debug("REST request to get job by ID: {}", id);
        JobResponse response = jobService.getJobById(id);
        return ResponseEntity.ok(com.recruitment.job.dto.ApiResponse.success("Job retrieved successfully", response));
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "List jobs by company ID", description = "Retrieves all job listings posted by a specific company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company jobs retrieved")
    })
    public ResponseEntity<com.recruitment.job.dto.ApiResponse<List<JobResponse>>> getJobsByCompany(
            @Parameter(description = "Company ID", required = true) @PathVariable Long companyId) {
        log.debug("REST request to get jobs for company ID: {}", companyId);
        List<JobResponse> jobs = jobService.getJobsByCompanyId(companyId);
        return ResponseEntity.ok(com.recruitment.job.dto.ApiResponse.success("Company jobs retrieved", jobs));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update job details", description = "Modifies existing job information or status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job updated successfully"),
            @ApiResponse(responseCode = "404", description = "Job not found")
    })
    public ResponseEntity<com.recruitment.job.dto.ApiResponse<JobResponse>> updateJob(
            @Parameter(description = "Job ID", required = true) @PathVariable Long id,
            @Valid @RequestBody UpdateJobRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.info("REST request to update job ID: {}", id);
        JobResponse response = jobService.updateJob(id, request, userId, role);
        return ResponseEntity.ok(com.recruitment.job.dto.ApiResponse.success("Job updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a job", description = "Permanently removes a job posting and its associated applications")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Job not found")
    })
    public ResponseEntity<com.recruitment.job.dto.ApiResponse<Void>> deleteJob(
            @Parameter(description = "Job ID", required = true) @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.info("REST request to delete job ID: {}", id);
        jobService.deleteJob(id, userId, role);
        return ResponseEntity.ok(com.recruitment.job.dto.ApiResponse.success("Job deleted successfully", null));
    }

    @PostMapping("/{id}/apply")
    @Operation(summary = "Apply to a job", description = "Submits a candidate application for a specific open job listing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Application submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or closed job"),
            @ApiResponse(responseCode = "409", description = "Candidate already applied to this job")
    })
    public ResponseEntity<com.recruitment.job.dto.ApiResponse<ApplicationResponse>> applyToJob(
            @Parameter(description = "Job ID", required = true) @PathVariable Long id,
            @Valid @RequestBody ApplyJobRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.info("REST request to apply to job ID: {} by candidate ID: {}", id, request.getCandidateId());
        ApplicationResponse response = jobService.applyToJob(id, request, userId, role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.recruitment.job.dto.ApiResponse.success("Application submitted successfully", response));
    }

    @GetMapping("/{id}/applications")
    @Operation(summary = "List applications for a job", description = "Retrieves all candidate applications submitted for a given job listing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Applications retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Job not found")
    })
    public ResponseEntity<com.recruitment.job.dto.ApiResponse<List<ApplicationResponse>>> getApplicationsForJob(
            @Parameter(description = "Job ID", required = true) @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.debug("REST request to get applications for job ID: {}", id);
        List<ApplicationResponse> applications = jobService.getApplicationsByJobId(id, userId, role);
        return ResponseEntity.ok(com.recruitment.job.dto.ApiResponse.success("Job applications retrieved", applications));
    }

    @GetMapping("/applications/user/{userId}")
    @Operation(summary = "List candidate applications by User ID", description = "Retrieves all job applications submitted by a specific user / candidate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Candidate applications retrieved")
    })
    public ResponseEntity<com.recruitment.job.dto.ApiResponse<List<ApplicationResponse>>> getApplicationsByUserId(
            @Parameter(description = "User / Candidate ID", required = true) @PathVariable Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long authUserId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.debug("REST request to get applications for user/candidate ID: {}", userId);
        List<ApplicationResponse> applications = jobService.getApplicationsByCandidateId(userId, authUserId, role);
        return ResponseEntity.ok(com.recruitment.job.dto.ApiResponse.success("Candidate applications retrieved", applications));
    }

    @GetMapping("/applications/candidate/{candidateId}")
    @Operation(summary = "List candidate applications by Candidate ID", description = "Retrieves all job applications for candidate primary key")
    public ResponseEntity<com.recruitment.job.dto.ApiResponse<List<ApplicationResponse>>> getApplicationsByCandidateId(
            @Parameter(description = "Candidate ID", required = true) @PathVariable Long candidateId,
            @RequestHeader(value = "X-User-Id", required = false) Long authUserId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.debug("REST request to get applications for candidate ID: {}", candidateId);
        List<ApplicationResponse> applications = jobService.getApplicationsByCandidateId(candidateId, authUserId, role);
        return ResponseEntity.ok(com.recruitment.job.dto.ApiResponse.success("Candidate applications retrieved", applications));
    }

    @PutMapping("/applications/{applicationId}/status")
    @Operation(summary = "Update application status", description = "Updates an application status to SHORTLISTED, REJECTED, or ACCEPTED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application status updated"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    public ResponseEntity<com.recruitment.job.dto.ApiResponse<ApplicationResponse>> updateApplicationStatus(
            @Parameter(description = "Application ID", required = true) @PathVariable Long applicationId,
            @Valid @RequestBody UpdateApplicationStatusRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.info("REST request to update application ID: {} to status: {}", applicationId, request.getStatus());
        ApplicationResponse response = jobService.updateApplicationStatus(applicationId, request, userId, role);
        return ResponseEntity.ok(com.recruitment.job.dto.ApiResponse.success("Application status updated", response));
    }
}
