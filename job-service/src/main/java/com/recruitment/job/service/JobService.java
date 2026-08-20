package com.recruitment.job.service;

import com.recruitment.job.dto.*;
import com.recruitment.job.entity.JobStatus;

import java.util.List;

public interface JobService {

    JobResponse createJob(CreateJobRequest request, Long authenticatedUserId, String role);

    List<JobResponse> getAllJobs(JobStatus status);

    List<JobResponse> searchJobs(String keyword, String location, String jobType, JobStatus status);

    JobResponse getJobById(String id);

    List<JobResponse> getJobsByCompanyId(Long companyId);

    JobResponse updateJob(String id, UpdateJobRequest request, Long authenticatedUserId, String role);

    void deleteJob(String id, Long authenticatedUserId, String role);

    ApplicationResponse applyToJob(String jobId, ApplyJobRequest request, Long authenticatedUserId, String role);

    List<ApplicationResponse> getApplicationsByJobId(String jobId, Long authenticatedUserId, String role);

    List<ApplicationResponse> getApplicationsByCandidateId(Long candidateId, Long authenticatedUserId, String role);

    ApplicationResponse updateApplicationStatus(String applicationId, UpdateApplicationStatusRequest request, Long authenticatedUserId, String role);
}
