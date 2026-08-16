package com.recruitment.job.service;

import com.recruitment.job.dto.*;
import com.recruitment.job.entity.Application;
import com.recruitment.job.entity.ApplicationStatus;
import com.recruitment.job.entity.Job;
import com.recruitment.job.entity.JobStatus;
import com.recruitment.job.exception.DuplicateResourceException;
import com.recruitment.job.exception.ResourceNotFoundException;
import com.recruitment.job.repository.ApplicationRepository;
import com.recruitment.job.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private JobServiceImpl jobService;

    private Job sampleJob;
    private Application sampleApplication;

    @BeforeEach
    void setUp() {
        sampleJob = Job.builder()
                .id(1L)
                .companyId(10L)
                .title("Senior Backend Engineer")
                .description("Build microservices in Java and Spring Boot")
                .requiredSkills("Java, Spring Boot, MySQL, Docker")
                .location("San Francisco, CA / Hybrid")
                .salaryRange("$140k - $180k")
                .jobType("FULL_TIME")
                .status(JobStatus.OPEN)
                .postedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleApplication = Application.builder()
                .id(100L)
                .job(sampleJob)
                .candidateId(1L)
                .status(ApplicationStatus.APPLIED)
                .notes("Interested in backend role")
                .appliedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldCreateJobSuccessfully() {
        CreateJobRequest request = CreateJobRequest.builder()
                .companyId(10L)
                .title("Senior Backend Engineer")
                .description("Build microservices in Java and Spring Boot")
                .requiredSkills("Java, Spring Boot, MySQL, Docker")
                .location("San Francisco, CA / Hybrid")
                .salaryRange("$140k - $180k")
                .jobType("FULL_TIME")
                .build();

        when(jobRepository.save(any(Job.class))).thenReturn(sampleJob);

        JobResponse response = jobService.createJob(request, 10L, "ROLE_COMPANY");

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Senior Backend Engineer", response.getTitle());
        assertEquals(10L, response.getCompanyId());
        assertEquals(JobStatus.OPEN, response.getStatus());
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    void shouldGetAllJobs() {
        when(jobRepository.findAll()).thenReturn(List.of(sampleJob));
        when(applicationRepository.countByJobId(1L)).thenReturn(2L);

        List<JobResponse> jobs = jobService.getAllJobs(null);

        assertNotNull(jobs);
        assertEquals(1, jobs.size());
        assertEquals(2L, jobs.get(0).getApplicationCount());
    }

    @Test
    void shouldGetJobById() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(sampleJob));
        when(applicationRepository.countByJobId(1L)).thenReturn(1L);

        JobResponse response = jobService.getJobById(1L);

        assertNotNull(response);
        assertEquals("Senior Backend Engineer", response.getTitle());
    }

    @Test
    void shouldThrowNotFoundWhenJobDoesNotExist() {
        when(jobRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> jobService.getJobById(999L));
    }

    @Test
    void shouldSearchJobs() {
        when(jobRepository.searchJobs("Backend", "San Francisco", "FULL_TIME", JobStatus.OPEN))
                .thenReturn(List.of(sampleJob));
        when(applicationRepository.countByJobId(1L)).thenReturn(0L);

        List<JobResponse> results = jobService.searchJobs("Backend", "San Francisco", "FULL_TIME", JobStatus.OPEN);

        assertNotNull(results);
        assertEquals(1, results.size());
    }

    @Test
    void shouldUpdateJob() {
        UpdateJobRequest updateReq = UpdateJobRequest.builder()
                .title("Lead Backend Engineer")
                .salaryRange("$160k - $200k")
                .status(JobStatus.OPEN)
                .build();

        when(jobRepository.findById(1L)).thenReturn(Optional.of(sampleJob));
        when(jobRepository.save(any(Job.class))).thenAnswer(i -> i.getArgument(0));
        when(applicationRepository.countByJobId(1L)).thenReturn(0L);

        JobResponse response = jobService.updateJob(1L, updateReq, 10L, "ROLE_COMPANY");

        assertNotNull(response);
        assertEquals("Lead Backend Engineer", response.getTitle());
    }

    @Test
    void shouldDeleteJob() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(sampleJob));
        doNothing().when(jobRepository).delete(sampleJob);

        assertDoesNotThrow(() -> jobService.deleteJob(1L, 10L, "ROLE_COMPANY"));
        verify(jobRepository, times(1)).delete(sampleJob);
    }

    @Test
    void shouldApplyToJobSuccessfully() {
        ApplyJobRequest applyReq = ApplyJobRequest.builder()
                .candidateId(1L)
                .notes("Strong background in Java")
                .resumeUrl("https://example.com/resume.pdf")
                .build();

        when(jobRepository.findById(1L)).thenReturn(Optional.of(sampleJob));
        when(applicationRepository.existsByJobIdAndCandidateId(1L, 1L)).thenReturn(false);
        when(applicationRepository.save(any(Application.class))).thenReturn(sampleApplication);

        ApplicationResponse response = jobService.applyToJob(1L, applyReq, 1L, "ROLE_CANDIDATE");

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(ApplicationStatus.APPLIED, response.getStatus());
        verify(applicationRepository, times(1)).save(any(Application.class));
    }

    @Test
    void shouldThrowDuplicateExceptionWhenCandidateAppliesTwice() {
        ApplyJobRequest applyReq = ApplyJobRequest.builder()
                .candidateId(1L)
                .build();

        when(jobRepository.findById(1L)).thenReturn(Optional.of(sampleJob));
        when(applicationRepository.existsByJobIdAndCandidateId(1L, 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> jobService.applyToJob(1L, applyReq, 1L, "ROLE_CANDIDATE"));
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    void shouldUpdateApplicationStatus() {
        UpdateApplicationStatusRequest statusReq = UpdateApplicationStatusRequest.builder()
                .status(ApplicationStatus.SHORTLISTED)
                .build();

        when(applicationRepository.findById(100L)).thenReturn(Optional.of(sampleApplication));
        when(applicationRepository.save(any(Application.class))).thenAnswer(i -> i.getArgument(0));

        ApplicationResponse response = jobService.updateApplicationStatus(100L, statusReq, 10L, "ROLE_COMPANY");

        assertNotNull(response);
        assertEquals(ApplicationStatus.SHORTLISTED, response.getStatus());
    }
}
