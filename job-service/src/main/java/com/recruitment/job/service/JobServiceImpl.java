package com.recruitment.job.service;

import com.recruitment.job.dto.*;
import com.recruitment.job.entity.Application;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    @Override
    @Transactional
    public JobResponse createJob(CreateJobRequest request, Long authenticatedUserId, String role) {
        log.info("Creating new job listing for company ID: {}, title: {}", request.getCompanyId(), request.getTitle());

        Job job = Job.builder()
                .companyId(request.getCompanyId())
                .title(request.getTitle().trim())
                .description(request.getDescription().trim())
                .requiredSkills(request.getRequiredSkills())
                .location(request.getLocation())
                .salaryRange(request.getSalaryRange())
                .jobType(request.getJobType())
                .status(JobStatus.OPEN)
                .build();

        Job savedJob = jobRepository.save(job);
        log.info("Job successfully created with ID: {}", savedJob.getId());
        return JobResponse.fromEntity(savedJob, 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> getAllJobs(JobStatus status) {
        log.debug("Fetching all jobs with status filter: {}", status);
        List<Job> jobs = (status != null) ? jobRepository.findByStatus(status) : jobRepository.findAll();
        return jobs.stream()
                .map(j -> JobResponse.fromEntity(j, applicationRepository.countByJobId(j.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> searchJobs(String keyword, String location, String jobType, JobStatus status) {
        log.debug("Searching jobs keyword: '{}', location: '{}', jobType: '{}', status: '{}'", keyword, location, jobType, status);
        List<Job> jobs = jobRepository.searchJobs(keyword, location, jobType, status);
        return jobs.stream()
                .map(j -> JobResponse.fromEntity(j, applicationRepository.countByJobId(j.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponse getJobById(Long id) {
        log.debug("Fetching job by ID: {}", id);
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
        long appCount = applicationRepository.countByJobId(id);
        return JobResponse.fromEntity(job, appCount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> getJobsByCompanyId(Long companyId) {
        log.debug("Fetching jobs for company ID: {}", companyId);
        return jobRepository.findByCompanyId(companyId).stream()
                .map(j -> JobResponse.fromEntity(j, applicationRepository.countByJobId(j.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JobResponse updateJob(Long id, UpdateJobRequest request, Long authenticatedUserId, String role) {
        log.info("Updating job ID: {}", id);
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            job.setTitle(request.getTitle().trim());
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            job.setDescription(request.getDescription().trim());
        }
        if (request.getRequiredSkills() != null) {
            job.setRequiredSkills(request.getRequiredSkills());
        }
        if (request.getLocation() != null) {
            job.setLocation(request.getLocation());
        }
        if (request.getSalaryRange() != null) {
            job.setSalaryRange(request.getSalaryRange());
        }
        if (request.getJobType() != null) {
            job.setJobType(request.getJobType());
        }
        if (request.getStatus() != null) {
            job.setStatus(request.getStatus());
        }

        Job updatedJob = jobRepository.save(job);
        long appCount = applicationRepository.countByJobId(id);
        log.info("Job ID: {} updated successfully", id);
        return JobResponse.fromEntity(updatedJob, appCount);
    }

    @Override
    @Transactional
    public void deleteJob(Long id, Long authenticatedUserId, String role) {
        log.info("Deleting job ID: {}", id);
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

        jobRepository.delete(job);
        log.info("Job ID: {} deleted successfully", id);
    }

    @Override
    @Transactional
    public ApplicationResponse applyToJob(Long jobId, ApplyJobRequest request, Long authenticatedUserId, String role) {
        log.info("Candidate ID: {} applying to Job ID: {}", request.getCandidateId(), jobId);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (job.getStatus() == JobStatus.CLOSED) {
            throw new IllegalArgumentException("Cannot apply to a closed job posting");
        }

        if (applicationRepository.existsByJobIdAndCandidateId(jobId, request.getCandidateId())) {
            throw new DuplicateResourceException("Candidate ID " + request.getCandidateId() + " has already applied for this job");
        }

        Application application = Application.builder()
                .job(job)
                .candidateId(request.getCandidateId())
                .notes(request.getNotes())
                .resumeUrl(request.getResumeUrl())
                .build();

        Application savedApp = applicationRepository.save(application);
        log.info("Application created successfully with ID: {}", savedApp.getId());
        return ApplicationResponse.fromEntity(savedApp);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsByJobId(Long jobId, Long authenticatedUserId, String role) {
        log.debug("Fetching applications for job ID: {}", jobId);
        if (!jobRepository.existsById(jobId)) {
            throw new ResourceNotFoundException("Job not found with id: " + jobId);
        }
        return applicationRepository.findByJobId(jobId).stream()
                .map(ApplicationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsByCandidateId(Long candidateId, Long authenticatedUserId, String role) {
        log.debug("Fetching applications submitted by candidate ID: {}", candidateId);
        return applicationRepository.findByCandidateId(candidateId).stream()
                .map(ApplicationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApplicationResponse updateApplicationStatus(Long applicationId, UpdateApplicationStatusRequest request, Long authenticatedUserId, String role) {
        log.info("Updating application ID: {} to status: {}", applicationId, request.getStatus());
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        application.setStatus(request.getStatus());
        Application saved = applicationRepository.save(application);
        log.info("Application ID: {} status updated to {}", applicationId, saved.getStatus());
        return ApplicationResponse.fromEntity(saved);
    }
}
