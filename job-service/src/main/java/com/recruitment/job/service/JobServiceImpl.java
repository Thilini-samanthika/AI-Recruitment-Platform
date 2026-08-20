package com.recruitment.job.service;

import com.recruitment.job.dto.*;
import com.recruitment.job.entity.Application;
import com.recruitment.job.entity.ApplicationStatus;
import com.recruitment.job.entity.Job;
import com.recruitment.job.entity.JobStatus;
import com.recruitment.job.exception.DuplicateResourceException;
import com.recruitment.job.exception.ResourceNotFoundException;
import com.recruitment.job.exception.UnauthorizedException;
import com.recruitment.job.repository.ApplicationRepository;
import com.recruitment.job.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    private static final String ROLE_CANDIDATE = "ROLE_CANDIDATE";
    private static final String ROLE_COMPANY = "ROLE_COMPANY";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Override
    public JobResponse createJob(CreateJobRequest request, Long authenticatedUserId, String role) {
        log.info("Creating new job listing for company ID: {}, title: {}", request.getCompanyId(), request.getTitle());

        // RBAC check: Candidates cannot post jobs
        if (role != null) {
            if (ROLE_CANDIDATE.equalsIgnoreCase(role)) {
                throw new UnauthorizedException("Candidates are not authorized to create job openings");
            }
            if (ROLE_COMPANY.equalsIgnoreCase(role) && authenticatedUserId != null
                    && !authenticatedUserId.equals(request.getCompanyId())) {
                throw new UnauthorizedException("Company user is not authorized to create job listings on behalf of another company ID");
            }
        }

        LocalDateTime now = LocalDateTime.now();
        Job job = Job.builder()
                .companyId(request.getCompanyId())
                .title(request.getTitle().trim())
                .description(request.getDescription().trim())
                .requiredSkills(request.getRequiredSkills() != null ? request.getRequiredSkills().trim() : null)
                .location(request.getLocation() != null ? request.getLocation().trim() : null)
                .salaryRange(request.getSalaryRange() != null ? request.getSalaryRange().trim() : null)
                .jobType(request.getJobType() != null ? request.getJobType().trim() : null)
                .deadline(request.getDeadline())
                .status(JobStatus.OPEN)
                .postedAt(now)
                .updatedAt(now)
                .build();

        Job savedJob = jobRepository.save(job);
        log.info("Job successfully created with ID: {}", savedJob.getId());
        return JobResponse.fromEntity(savedJob, 0L);
    }

    @Override
    public List<JobResponse> getAllJobs(JobStatus status) {
        log.debug("Fetching all jobs with status filter: {}", status);
        List<Job> jobs = (status != null) ? jobRepository.findByStatus(status) : jobRepository.findAll();
        return jobs.stream()
                .map(j -> JobResponse.fromEntity(j, applicationRepository.countByJobId(j.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<JobResponse> searchJobs(String keyword, String location, String jobType, JobStatus status) {
        log.debug("Searching jobs keyword: '{}', location: '{}', jobType: '{}', status: '{}'", keyword, location, jobType, status);
        List<Job> jobs = jobRepository.searchJobs(keyword, location, jobType, status);
        return jobs.stream()
                .map(j -> JobResponse.fromEntity(j, applicationRepository.countByJobId(j.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public JobResponse getJobById(String id) {
        log.debug("Fetching job by ID: {}", id);
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
        long appCount = applicationRepository.countByJobId(id);
        return JobResponse.fromEntity(job, appCount);
    }

    @Override
    public List<JobResponse> getJobsByCompanyId(Long companyId) {
        log.debug("Fetching jobs for company ID: {}", companyId);
        return jobRepository.findByCompanyId(companyId).stream()
                .map(j -> JobResponse.fromEntity(j, applicationRepository.countByJobId(j.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public JobResponse updateJob(String id, UpdateJobRequest request, Long authenticatedUserId, String role) {
        log.info("Updating job ID: {}", id);
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

        // RBAC check: only owning company or admin can modify
        if (role != null) {
            if (ROLE_CANDIDATE.equalsIgnoreCase(role)) {
                throw new UnauthorizedException("Candidates are not authorized to update job postings");
            }
            if (ROLE_COMPANY.equalsIgnoreCase(role) && authenticatedUserId != null
                    && !authenticatedUserId.equals(job.getCompanyId())) {
                throw new UnauthorizedException("You are not authorized to update job postings belonging to another company");
            }
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            job.setTitle(request.getTitle().trim());
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            job.setDescription(request.getDescription().trim());
        }
        if (request.getRequiredSkills() != null) {
            job.setRequiredSkills(request.getRequiredSkills().trim());
        }
        if (request.getLocation() != null) {
            job.setLocation(request.getLocation().trim());
        }
        if (request.getSalaryRange() != null) {
            job.setSalaryRange(request.getSalaryRange().trim());
        }
        if (request.getJobType() != null) {
            job.setJobType(request.getJobType().trim());
        }
        if (request.getStatus() != null) {
            job.setStatus(request.getStatus());
        }
        if (request.getDeadline() != null) {
            job.setDeadline(request.getDeadline());
        }
        job.setUpdatedAt(LocalDateTime.now());

        Job updatedJob = jobRepository.save(job);
        long appCount = applicationRepository.countByJobId(id);
        log.info("Job ID: {} updated successfully", id);
        return JobResponse.fromEntity(updatedJob, appCount);
    }

    @Override
    public void deleteJob(String id, Long authenticatedUserId, String role) {
        log.info("Deleting job ID: {}", id);
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

        // RBAC check: only owning company or admin can delete
        if (role != null) {
            if (ROLE_CANDIDATE.equalsIgnoreCase(role)) {
                throw new UnauthorizedException("Candidates are not authorized to delete job postings");
            }
            if (ROLE_COMPANY.equalsIgnoreCase(role) && authenticatedUserId != null
                    && !authenticatedUserId.equals(job.getCompanyId())) {
                throw new UnauthorizedException("You are not authorized to delete job postings belonging to another company");
            }
        }

        // Clean up applications for this job in MongoDB collection
        List<Application> relatedApplications = applicationRepository.findByJobId(id);
        if (!relatedApplications.isEmpty()) {
            applicationRepository.deleteAll(relatedApplications);
        }

        jobRepository.delete(job);
        log.info("Job ID: {} and its applications deleted successfully", id);
    }

    @Override
    public ApplicationResponse applyToJob(String jobId, ApplyJobRequest request, Long authenticatedUserId, String role) {
        log.info("Candidate ID: {} applying to Job ID: {}", request.getCandidateId(), jobId);

        // RBAC check: only candidate or admin can apply
        if (role != null) {
            if (ROLE_COMPANY.equalsIgnoreCase(role)) {
                throw new UnauthorizedException("Company accounts cannot apply for jobs. Please use a candidate profile.");
            }
            if (ROLE_CANDIDATE.equalsIgnoreCase(role) && authenticatedUserId != null
                    && !authenticatedUserId.equals(request.getCandidateId())) {
                throw new UnauthorizedException("You can only submit job applications for your own candidate profile");
            }
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (job.getStatus() == JobStatus.CLOSED) {
            throw new IllegalArgumentException("Cannot apply to a closed job posting");
        }

        if (job.getDeadline() != null && job.getDeadline().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("The application deadline for this job posting has expired");
        }

        if (applicationRepository.existsByJobIdAndCandidateId(jobId, request.getCandidateId())) {
            throw new DuplicateResourceException("Candidate ID " + request.getCandidateId() + " has already applied for this job");
        }

        LocalDateTime now = LocalDateTime.now();
        Application application = Application.builder()
                .jobId(jobId)
                .jobTitle(job.getTitle())
                .companyId(job.getCompanyId())
                .candidateId(request.getCandidateId())
                .status(ApplicationStatus.APPLIED)
                .notes(request.getNotes())
                .resumeUrl(request.getResumeUrl())
                .appliedAt(now)
                .updatedAt(now)
                .build();

        Application savedApp = applicationRepository.save(application);
        log.info("Application created successfully with ID: {}", savedApp.getId());
        return ApplicationResponse.fromEntity(savedApp);
    }

    @Override
    public List<ApplicationResponse> getApplicationsByJobId(String jobId, Long authenticatedUserId, String role) {
        log.debug("Fetching applications for job ID: {}", jobId);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        // RBAC check: only owning company or admin can view applications for a job
        if (role != null) {
            if (ROLE_CANDIDATE.equalsIgnoreCase(role)) {
                throw new UnauthorizedException("Candidates are not authorized to view all applications for a job posting");
            }
            if (ROLE_COMPANY.equalsIgnoreCase(role) && authenticatedUserId != null
                    && !authenticatedUserId.equals(job.getCompanyId())) {
                throw new UnauthorizedException("You are not authorized to view applications for another company's job posting");
            }
        }

        return applicationRepository.findByJobId(jobId).stream()
                .map(ApplicationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationResponse> getApplicationsByCandidateId(Long candidateId, Long authenticatedUserId, String role) {
        log.debug("Fetching applications submitted by candidate ID: {}", candidateId);

        // RBAC check: candidates can only view their own applications
        if (role != null && ROLE_CANDIDATE.equalsIgnoreCase(role)) {
            if (authenticatedUserId != null && !authenticatedUserId.equals(candidateId)) {
                throw new UnauthorizedException("Candidates are only permitted to view their own applications");
            }
        }

        return applicationRepository.findByCandidateId(candidateId).stream()
                .map(ApplicationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public ApplicationResponse updateApplicationStatus(String applicationId, UpdateApplicationStatusRequest request, Long authenticatedUserId, String role) {
        log.info("Updating application ID: {} to status: {}", applicationId, request.getStatus());
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        // RBAC check: Only owning company or admin can update status
        if (role != null) {
            if (ROLE_CANDIDATE.equalsIgnoreCase(role)) {
                // If candidate wishes to withdraw application
                if (request.getStatus() == ApplicationStatus.WITHDRAWN) {
                    if (authenticatedUserId != null && !authenticatedUserId.equals(application.getCandidateId())) {
                        throw new UnauthorizedException("You can only withdraw your own applications");
                    }
                } else {
                    throw new UnauthorizedException("Candidates are not authorized to modify application review statuses");
                }
            } else if (ROLE_COMPANY.equalsIgnoreCase(role)) {
                if (authenticatedUserId != null && !authenticatedUserId.equals(application.getCompanyId())) {
                    throw new UnauthorizedException("You are not authorized to update applications for a job belonging to another company");
                }
            }
        }

        // Validate state transition using State Machine rules
        application.getStatus().validateTransitionTo(request.getStatus());

        application.setStatus(request.getStatus());
        application.setUpdatedAt(LocalDateTime.now());
        Application saved = applicationRepository.save(application);
        log.info("Application ID: {} status updated to {}", applicationId, saved.getStatus());
        return ApplicationResponse.fromEntity(saved);
    }
}
