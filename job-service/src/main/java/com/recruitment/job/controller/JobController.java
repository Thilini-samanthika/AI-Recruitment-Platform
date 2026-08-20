package com.recruitment.job.controller;

import com.recruitment.job.dto.*;
import com.recruitment.job.entity.ApplicationStatus;
import com.recruitment.job.entity.JobStatus;
import com.recruitment.job.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Management", description = "REST APIs for job vacancies, multi-criteria search, candidate applications, and state machine lifecycle management")
@SecurityRequirement(name = "bearerAuth")
@SecurityRequirement(name = "apiKeyAuth")
public class JobController {

    private final JobService jobService;

    @PostMapping
    @Operation(
            summary = "Post a new job opening",
            description = "Creates a new job listing on behalf of a registered company. " +
                    "Authorized roles: ROLE_COMPANY (matching company ID) or ROLE_ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Job posted successfully",
                    content = @Content(schema = @Schema(implementation = JobResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or validation failure"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid API key / authentication"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Role not authorized (e.g. Candidates cannot post jobs)")
    })
    public ResponseEntity<com.recruitment.job.dto.ApiResponse<JobResponse>> postJob(
            @Valid @RequestBody CreateJobRequest request,
            @Parameter(name = "X-User-Id", in = ParameterIn.HEADER, description = "Authenticated User ID forwarded by Gateway", example = "10")
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Parameter(name = "X-User-Role", in = ParameterIn.HEADER, description = "User role (ROLE_COMPANY, ROLE_ADMIN)", example = "ROLE_COMPANY")
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.info("REST request to post job: {}", request.getTitle());
        JobResponse response = jobService.createJob(request, userId, role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.recruitment.job.dto.ApiResponse.success("Job posted successfully", response));
    }

    @GetMapping
    @Operation(
            summary = "List all jobs",
            description = "Retrieves all job vacancies with an optional lifecycle status filter (e.g. OPEN or CLOSED)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Jobs retrieved successfully",
                    content = @Content(schema = @Schema(implementation = JobResponse.class)))
    })
    public ResponseEntity<com.recruitment.job.dto.ApiResponse<List<JobResponse>>> getAllJobs(
            @Parameter(name = "status", description = "Optional status filter (OPEN or CLOSED)", schema = @Schema(implementation = JobStatus.class))
            @RequestParam(required = false) JobStatus status) {
        log.debug("REST request to get all jobs with status: {}", status);
        List<JobResponse> jobs = jobService.getAllJobs(status);
        return ResponseEntity.ok(com.recruitment.job.dto.ApiResponse.success("Jobs retrieved successfully", jobs));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search jobs by multiple criteria",
            description = "Performs dynamic case-insensitive search across job vacancies by matching keyword in title, description, or required skills, filtering by location, jobType, and status."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results returned successfully",
                    content = @Content(schema = @Schema(implementation = JobResponse.class)))
    })
    public ResponseEntity<com.recruitment.job.dto.ApiResponse<List<JobResponse>>> searchJobs(
            @Parameter(name = "keyword", description = "Keyword to match against job title, description, or required skills", example = "Spring Boot")
            @RequestParam(required = false) String keyword,
            @Parameter(name = "location", description = "Work location or remote policy filter", example = "San Francisco")
            @RequestParam(required = false) String location,
            @Parameter(name = "jobType", description = "Engagement type filter (e.g. FULL_TIME, CONTRACT, REMOTE)", example = "FULL_TIME")
            @RequestParam(required = false) String jobType,
            @Parameter(name = "status", description = "Job status filter (defaults to OPEN if specified)", schema = @Schema(implementation = JobStatus.class), example = "OPEN")
            @RequestParam(required = false) JobStatus status) {
        log.debug("REST request to search jobs with keyword='{}', location='{}', jobType='{}', status='{}'", keyword, location, jobType, status);
        List<JobResponse> results = jobService.searchJobs(keyword, location, jobType, status);
        return ResponseEntity.ok(com.recruitment.job.dto.ApiResponse.success("Job search results", results));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get job details by ID",
            description = "Retrieves complete information and current applicant count for a specific job listing."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job retrieved successfully",
                    content = @Content(schema = @Schema(implementation = JobResponse.class))),
            @ApiResponse(responseCode = "404", description = "Job not found with given ID")
    })
    public ResponseEntity<com.recruitment.job.dto.ApiResponse<JobResponse>> getJobById(
            @Parameter(name = "id", description = "Job ID (MongoDB ObjectId)", required = true, example = "66c3abc1234567890abcdef1")
            @PathVariable String id) {
        log.debug("REST request to get job by ID: {}", id);
        JobResponse response = jobService.getJobById(id);
        return ResponseEntity.ok(com.recruitment.job.dto.ApiResponse.success("Job retrieved successfully", response));
    }

    @GetMapping("/company/{companyId}")
    @Operation(
            summary = "List jobs by company ID",
            description = "Retrieves all job listings posted by a specific company identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company jobs retrieved successfully",
                    content = @Content(schema = @Schema(implementation = JobResponse.class)))
    })
    public ResponseEntity<com.recruitment.job.dto.ApiResponse<List<JobResponse>>> getJobsByCompany(
            @Parameter(name = "companyId", description = "Company ID", required = true, example = "10")
            @PathVariable Long companyId) {
        log.debug("REST request to get jobs for company ID: {}", companyId);
        List<JobResponse> jobs = jobService.getJobsByCompanyId(companyId);
        return ResponseEntity.ok(com.recruitment.job.dto.ApiResponse.success("Company jobs retrieved", jobs));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update job details",
            description = "Modifies existing job posting details or lifecycle status. " +
                    "Authorized roles: Only the owning company (ROLE_COMPANY with matching company ID) or ROLE_ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job updated successfully",
                    content = @Content(schema = @Schema(implementation = JobResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing authentication"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not the owning company"),
            @ApiResponse(responseCode = "404", description = "Job not found")
    })
    public ResponseEntity<com.recruitment.job.dto.ApiResponse<JobResponse>> updateJob(
            @Parameter(name = "id", description = "Job ID (MongoDB ObjectId)", required = true, example = "66c3abc1234567890abcdef1")
            @PathVariable String id,
            @Valid @RequestBody UpdateJobRequest request,
            @Parameter(name = "X-User-Id", in = ParameterIn.HEADER, description = "Authenticated User ID", example = "10")
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Parameter(name = "X-User-Role", in = ParameterIn.HEADER, description = "Authenticated Role (ROLE_COMPANY, ROLE_ADMIN)", example = "ROLE_COMPANY")
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.info("REST request to update job ID: {}", id);
        JobResponse response = jobService.updateJob(id, request, userId, role);
        return ResponseEntity.ok(com.recruitment.job.dto.ApiResponse.success("Job updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a job",
            description = "Permanently removes a job opening and cascades deletion of associated applications. " +
                    "Authorized roles: Only the owning company (ROLE_COMPANY) or ROLE_ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing authentication"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized to delete this job"),
            @ApiResponse(responseCode = "404", description = "Job not found")
    })
    public ResponseEntity<com.recruitment.job.dto.ApiResponse<Void>> deleteJob(
            @Parameter(name = "id", description = "Job ID (MongoDB ObjectId)", required = true, example = "66c3abc1234567890abcdef1")
            @PathVariable String id,
            @Parameter(name = "X-User-Id", in = ParameterIn.HEADER, description = "Authenticated User ID", example = "10")
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Parameter(name = "X-User-Role", in = ParameterIn.HEADER, description = "Authenticated Role (ROLE_COMPANY, ROLE_ADMIN)", example = "ROLE_COMPANY")
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.info("REST request to delete job ID: {}", id);
        jobService.deleteJob(id, userId, role);
        return ResponseEntity.ok(com.recruitment.job.dto.ApiResponse.success("Job deleted successfully", null));
    }

    @PostMapping("/{id}/apply")
    @Operation(
            summary = "Apply to a job",
            description = "Submits a candidate application for an open job listing. " +
                    "Authorized roles: ROLE_CANDIDATE (for their own candidate profile ID) or ROLE_ADMIN. " +
                    "Application will be rejected if the job is CLOSED, if the deadline has passed, or if the candidate has already applied."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Application submitted successfully",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload, job is CLOSED, or deadline expired"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing authentication"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Companies cannot apply for jobs or candidate mismatch"),
            @ApiResponse(responseCode = "404", description = "Job not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - Candidate has already applied for this job")
    })
    public ResponseEntity<com.recruitment.job.dto.ApiResponse<ApplicationResponse>> applyToJob(
            @Parameter(name = "id", description = "Job ID (MongoDB ObjectId)", required = true, example = "66c3abc1234567890abcdef1")
            @PathVariable String id,
            @Valid @RequestBody ApplyJobRequest request,
            @Parameter(name = "X-User-Id", in = ParameterIn.HEADER, description = "Authenticated User ID", example = "1")
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Parameter(name = "X-User-Role", in = ParameterIn.HEADER, description = "Authenticated Role (ROLE_CANDIDATE, ROLE_ADMIN)", example = "ROLE_CANDIDATE")
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.info("REST request to apply to job ID: {} by candidate ID: {}", id, request.getCandidateId());
        ApplicationResponse response = jobService.applyToJob(id, request, userId, role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.recruitment.job.dto.ApiResponse.success("Application submitted successfully", response));
    }

    @GetMapping("/{id}/applications")
    @Operation(
            summary = "List applications for a job",
            description = "Retrieves all candidate applications submitted for a given job vacancy. " +
                    "Authorized roles: Only the owning company (ROLE_COMPANY with matching company ID) or ROLE_ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Applications retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing authentication"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized to view applications for this job"),
            @ApiResponse(responseCode = "404", description = "Job not found")
    })
    public ResponseEntity<com.recruitment.job.dto.ApiResponse<List<ApplicationResponse>>> getApplicationsForJob(
            @Parameter(name = "id", description = "Job ID (MongoDB ObjectId)", required = true, example = "66c3abc1234567890abcdef1")
            @PathVariable String id,
            @Parameter(name = "X-User-Id", in = ParameterIn.HEADER, description = "Authenticated User ID", example = "10")
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Parameter(name = "X-User-Role", in = ParameterIn.HEADER, description = "Authenticated Role (ROLE_COMPANY, ROLE_ADMIN)", example = "ROLE_COMPANY")
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.debug("REST request to get applications for job ID: {}", id);
        List<ApplicationResponse> applications = jobService.getApplicationsByJobId(id, userId, role);
        return ResponseEntity.ok(com.recruitment.job.dto.ApiResponse.success("Job applications retrieved", applications));
    }

    @GetMapping("/applications/user/{userId}")
    @Operation(
            summary = "List candidate applications by User ID",
            description = "Retrieves all job applications submitted by a specific candidate user ID. " +
                    "Authorized roles: The candidate themselves (matching user ID) or ROLE_ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Candidate applications retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing authentication"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Cannot view other candidates' applications")
    })
    public ResponseEntity<com.recruitment.job.dto.ApiResponse<List<ApplicationResponse>>> getApplicationsByUserId(
            @Parameter(name = "userId", description = "User / Candidate ID", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(name = "X-User-Id", in = ParameterIn.HEADER, description = "Authenticated User ID", example = "1")
            @RequestHeader(value = "X-User-Id", required = false) Long authUserId,
            @Parameter(name = "X-User-Role", in = ParameterIn.HEADER, description = "Authenticated Role (ROLE_CANDIDATE, ROLE_ADMIN)", example = "ROLE_CANDIDATE")
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.debug("REST request to get applications for user/candidate ID: {}", userId);
        List<ApplicationResponse> applications = jobService.getApplicationsByCandidateId(userId, authUserId, role);
        return ResponseEntity.ok(com.recruitment.job.dto.ApiResponse.success("Candidate applications retrieved", applications));
    }

    @GetMapping("/applications/candidate/{candidateId}")
    @Operation(
            summary = "List candidate applications by Candidate ID",
            description = "Retrieves all job applications for candidate ID. " +
                    "Authorized roles: The candidate themselves (matching candidate ID) or ROLE_ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Candidate applications retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing authentication"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Cannot view other candidates' applications")
    })
    public ResponseEntity<com.recruitment.job.dto.ApiResponse<List<ApplicationResponse>>> getApplicationsByCandidateId(
            @Parameter(name = "candidateId", description = "Candidate ID", required = true, example = "1")
            @PathVariable Long candidateId,
            @Parameter(name = "X-User-Id", in = ParameterIn.HEADER, description = "Authenticated User ID", example = "1")
            @RequestHeader(value = "X-User-Id", required = false) Long authUserId,
            @Parameter(name = "X-User-Role", in = ParameterIn.HEADER, description = "Authenticated Role (ROLE_CANDIDATE, ROLE_ADMIN)", example = "ROLE_CANDIDATE")
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.debug("REST request to get applications for candidate ID: {}", candidateId);
        List<ApplicationResponse> applications = jobService.getApplicationsByCandidateId(candidateId, authUserId, role);
        return ResponseEntity.ok(com.recruitment.job.dto.ApiResponse.success("Candidate applications retrieved", applications));
    }

    @PutMapping("/applications/{applicationId}/status")
    @Operation(
            summary = "Update application lifecycle status",
            description = "Transitions an application status through the state machine: " +
                    "APPLIED -> REVIEWED | SHORTLISTED | REJECTED | WITHDRAWN, " +
                    "REVIEWED -> SHORTLISTED | INTERVIEW | REJECTED | WITHDRAWN, " +
                    "SHORTLISTED -> INTERVIEW | OFFERED | REJECTED | WITHDRAWN, " +
                    "INTERVIEW -> OFFERED | REJECTED | WITHDRAWN, " +
                    "OFFERED -> ACCEPTED | REJECTED | WITHDRAWN. " +
                    "ACCEPTED, REJECTED, and WITHDRAWN are terminal states. " +
                    "Authorized roles: Only the owning company (ROLE_COMPANY) or ROLE_ADMIN may advance review status. " +
                    "Candidates may only transition their own application to WITHDRAWN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application status updated successfully",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Illegal status transition or invalid payload"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing authentication"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized to modify this application"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    public ResponseEntity<com.recruitment.job.dto.ApiResponse<ApplicationResponse>> updateApplicationStatus(
            @Parameter(name = "applicationId", description = "Application ID (MongoDB ObjectId)", required = true, example = "66c3abc1234567890abcdef2")
            @PathVariable String applicationId,
            @Valid @RequestBody UpdateApplicationStatusRequest request,
            @Parameter(name = "X-User-Id", in = ParameterIn.HEADER, description = "Authenticated User ID", example = "10")
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Parameter(name = "X-User-Role", in = ParameterIn.HEADER, description = "Authenticated Role (ROLE_COMPANY, ROLE_ADMIN)", example = "ROLE_COMPANY")
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.info("REST request to update application ID: {} to status: {}", applicationId, request.getStatus());
        ApplicationResponse response = jobService.updateApplicationStatus(applicationId, request, userId, role);
        return ResponseEntity.ok(com.recruitment.job.dto.ApiResponse.success("Application status updated", response));
    }
}
