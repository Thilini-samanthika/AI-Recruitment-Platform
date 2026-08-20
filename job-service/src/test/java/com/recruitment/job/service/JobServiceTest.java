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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
                .id("66c3abc1234567890abcdef1")
                .companyId(10L)
                .title("Senior Backend Engineer")
                .description("Build microservices in Java and Spring Boot with MongoDB")
                .requiredSkills("Java, Spring Boot, MongoDB, Docker")
                .location("San Francisco, CA / Hybrid")
                .salaryRange("$140,000 - $180,000 / yr")
                .jobType("FULL_TIME")
                .status(JobStatus.OPEN)
                .deadline(LocalDateTime.now().plusDays(30))
                .postedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleApplication = Application.builder()
                .id("66c3abc1234567890abcdef2")
                .jobId("66c3abc1234567890abcdef1")
                .jobTitle("Senior Backend Engineer")
                .companyId(10L)
                .candidateId(1L)
                .status(ApplicationStatus.APPLIED)
                .notes("Interested in backend role")
                .resumeUrl("https://example.com/resume.pdf")
                .appliedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create job successfully for authorized company")
    void shouldCreateJobSuccessfully() {
        CreateJobRequest request = CreateJobRequest.builder()
                .companyId(10L)
                .title("Senior Backend Engineer")
                .description("Build microservices in Java and Spring Boot with MongoDB")
                .requiredSkills("Java, Spring Boot, MongoDB, Docker")
                .location("San Francisco, CA / Hybrid")
                .salaryRange("$140,000 - $180,000 / yr")
                .jobType("FULL_TIME")
                .deadline(LocalDateTime.now().plusDays(30))
                .build();

        when(jobRepository.save(any(Job.class))).thenReturn(sampleJob);

        JobResponse response = jobService.createJob(request, 10L, "ROLE_COMPANY");

        assertNotNull(response);
        assertEquals("66c3abc1234567890abcdef1", response.getId());
        assertEquals("Senior Backend Engineer", response.getTitle());
        assertEquals(10L, response.getCompanyId());
        assertEquals(JobStatus.OPEN, response.getStatus());
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    @DisplayName("Should reject job creation when candidate tries to post")
    void shouldRejectJobCreationForCandidateRole() {
        CreateJobRequest request = CreateJobRequest.builder()
                .companyId(10L)
                .title("Senior Backend Engineer")
                .description("Build microservices in Java and Spring Boot with MongoDB")
                .build();

        assertThrows(UnauthorizedException.class, () ->
                jobService.createJob(request, 1L, "ROLE_CANDIDATE"));
        verify(jobRepository, never()).save(any(Job.class));
    }

    @Test
    @DisplayName("Should get all jobs with application count")
    void shouldGetAllJobs() {
        when(jobRepository.findAll()).thenReturn(List.of(sampleJob));
        when(applicationRepository.countByJobId("66c3abc1234567890abcdef1")).thenReturn(2L);

        List<JobResponse> jobs = jobService.getAllJobs(null);

        assertNotNull(jobs);
        assertEquals(1, jobs.size());
        assertEquals(2L, jobs.get(0).getApplicationCount());
    }

    @Test
    @DisplayName("Should get job by ID")
    void shouldGetJobById() {
        when(jobRepository.findById("66c3abc1234567890abcdef1")).thenReturn(Optional.of(sampleJob));
        when(applicationRepository.countByJobId("66c3abc1234567890abcdef1")).thenReturn(1L);

        JobResponse response = jobService.getJobById("66c3abc1234567890abcdef1");

        assertNotNull(response);
        assertEquals("Senior Backend Engineer", response.getTitle());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when job does not exist")
    void shouldThrowNotFoundWhenJobDoesNotExist() {
        when(jobRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> jobService.getJobById("nonexistent"));
    }

    @Test
    @DisplayName("Should search jobs using criteria")
    void shouldSearchJobs() {
        when(jobRepository.searchJobs("Backend", "San Francisco", "FULL_TIME", JobStatus.OPEN))
                .thenReturn(List.of(sampleJob));
        when(applicationRepository.countByJobId("66c3abc1234567890abcdef1")).thenReturn(0L);

        List<JobResponse> results = jobService.searchJobs("Backend", "San Francisco", "FULL_TIME", JobStatus.OPEN);

        assertNotNull(results);
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Should update job successfully by owner")
    void shouldUpdateJob() {
        UpdateJobRequest updateReq = UpdateJobRequest.builder()
                .title("Lead Backend Engineer")
                .salaryRange("$160,000 - $200,000 / yr")
                .status(JobStatus.OPEN)
                .build();

        when(jobRepository.findById("66c3abc1234567890abcdef1")).thenReturn(Optional.of(sampleJob));
        when(jobRepository.save(any(Job.class))).thenAnswer(i -> i.getArgument(0));
        when(applicationRepository.countByJobId("66c3abc1234567890abcdef1")).thenReturn(0L);

        JobResponse response = jobService.updateJob("66c3abc1234567890abcdef1", updateReq, 10L, "ROLE_COMPANY");

        assertNotNull(response);
        assertEquals("Lead Backend Engineer", response.getTitle());
    }

    @Test
    @DisplayName("Should reject job update by unauthorized company")
    void shouldRejectJobUpdateByDifferentCompany() {
        UpdateJobRequest updateReq = UpdateJobRequest.builder()
                .title("Unauthorized Title Change")
                .build();

        when(jobRepository.findById("66c3abc1234567890abcdef1")).thenReturn(Optional.of(sampleJob));

        assertThrows(UnauthorizedException.class, () ->
                jobService.updateJob("66c3abc1234567890abcdef1", updateReq, 999L, "ROLE_COMPANY"));
        verify(jobRepository, never()).save(any(Job.class));
    }

    @Test
    @DisplayName("Should delete job and associated applications")
    void shouldDeleteJob() {
        when(jobRepository.findById("66c3abc1234567890abcdef1")).thenReturn(Optional.of(sampleJob));
        when(applicationRepository.findByJobId("66c3abc1234567890abcdef1")).thenReturn(List.of(sampleApplication));
        doNothing().when(applicationRepository).deleteAll(anyList());
        doNothing().when(jobRepository).delete(sampleJob);

        assertDoesNotThrow(() -> jobService.deleteJob("66c3abc1234567890abcdef1", 10L, "ROLE_COMPANY"));
        verify(jobRepository, times(1)).delete(sampleJob);
        verify(applicationRepository, times(1)).deleteAll(anyList());
    }

    @Test
    @DisplayName("Should apply to job successfully for authorized candidate")
    void shouldApplyToJobSuccessfully() {
        ApplyJobRequest applyReq = ApplyJobRequest.builder()
                .candidateId(1L)
                .notes("Strong background in Java and MongoDB")
                .resumeUrl("https://example.com/resume.pdf")
                .build();

        when(jobRepository.findById("66c3abc1234567890abcdef1")).thenReturn(Optional.of(sampleJob));
        when(applicationRepository.existsByJobIdAndCandidateId("66c3abc1234567890abcdef1", 1L)).thenReturn(false);
        when(applicationRepository.save(any(Application.class))).thenReturn(sampleApplication);

        ApplicationResponse response = jobService.applyToJob("66c3abc1234567890abcdef1", applyReq, 1L, "ROLE_CANDIDATE");

        assertNotNull(response);
        assertEquals("66c3abc1234567890abcdef2", response.getId());
        assertEquals(ApplicationStatus.APPLIED, response.getStatus());
        verify(applicationRepository, times(1)).save(any(Application.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when candidate applies twice")
    void shouldThrowDuplicateExceptionWhenCandidateAppliesTwice() {
        ApplyJobRequest applyReq = ApplyJobRequest.builder()
                .candidateId(1L)
                .build();

        when(jobRepository.findById("66c3abc1234567890abcdef1")).thenReturn(Optional.of(sampleJob));
        when(applicationRepository.existsByJobIdAndCandidateId("66c3abc1234567890abcdef1", 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () ->
                jobService.applyToJob("66c3abc1234567890abcdef1", applyReq, 1L, "ROLE_CANDIDATE"));
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when applying to closed job")
    void shouldThrowWhenApplyingToClosedJob() {
        sampleJob.setStatus(JobStatus.CLOSED);
        ApplyJobRequest applyReq = ApplyJobRequest.builder().candidateId(1L).build();

        when(jobRepository.findById("66c3abc1234567890abcdef1")).thenReturn(Optional.of(sampleJob));

        assertThrows(IllegalArgumentException.class, () ->
                jobService.applyToJob("66c3abc1234567890abcdef1", applyReq, 1L, "ROLE_CANDIDATE"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when applying past deadline")
    void shouldThrowWhenApplyingPastDeadline() {
        sampleJob.setDeadline(LocalDateTime.now().minusDays(1)); // Expired
        ApplyJobRequest applyReq = ApplyJobRequest.builder().candidateId(1L).build();

        when(jobRepository.findById("66c3abc1234567890abcdef1")).thenReturn(Optional.of(sampleJob));

        assertThrows(IllegalArgumentException.class, () ->
                jobService.applyToJob("66c3abc1234567890abcdef1", applyReq, 1L, "ROLE_CANDIDATE"));
    }

    @Test
    @DisplayName("Should update application status when state transition is valid")
    void shouldUpdateApplicationStatusValidTransition() {
        UpdateApplicationStatusRequest statusReq = UpdateApplicationStatusRequest.builder()
                .status(ApplicationStatus.SHORTLISTED)
                .build();

        when(applicationRepository.findById("66c3abc1234567890abcdef2")).thenReturn(Optional.of(sampleApplication));
        when(applicationRepository.save(any(Application.class))).thenAnswer(i -> i.getArgument(0));

        ApplicationResponse response = jobService.updateApplicationStatus("66c3abc1234567890abcdef2", statusReq, 10L, "ROLE_COMPANY");

        assertNotNull(response);
        assertEquals(ApplicationStatus.SHORTLISTED, response.getStatus());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when illegal state machine transition is attempted")
    void shouldThrowOnIllegalStateTransition() {
        // Set current state to REJECTED (terminal state)
        sampleApplication.setStatus(ApplicationStatus.REJECTED);

        UpdateApplicationStatusRequest statusReq = UpdateApplicationStatusRequest.builder()
                .status(ApplicationStatus.INTERVIEW) // Illegal transition from REJECTED
                .build();

        when(applicationRepository.findById("66c3abc1234567890abcdef2")).thenReturn(Optional.of(sampleApplication));

        assertThrows(IllegalStateException.class, () ->
                jobService.updateApplicationStatus("66c3abc1234567890abcdef2", statusReq, 10L, "ROLE_COMPANY"));
        verify(applicationRepository, never()).save(any(Application.class));
    }
}
