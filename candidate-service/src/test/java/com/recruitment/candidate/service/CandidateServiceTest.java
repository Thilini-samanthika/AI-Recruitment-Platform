package com.recruitment.candidate.service;

import com.recruitment.candidate.dto.*;
import com.recruitment.candidate.entity.Candidate;
import com.recruitment.candidate.entity.Education;
import com.recruitment.candidate.entity.Experience;
import com.recruitment.candidate.entity.Skill;
import com.recruitment.candidate.exception.DuplicateResourceException;
import com.recruitment.candidate.exception.ResourceNotFoundException;
import com.recruitment.candidate.exception.UnauthorizedException;
import com.recruitment.candidate.repository.CandidateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {

    @Mock
    private CandidateRepository candidateRepository;

    @InjectMocks
    private CandidateServiceImpl candidateService;

    private Candidate sampleCandidate;

    @BeforeEach
    void setUp() {
        sampleCandidate = Candidate.builder()
                .id("66c3abc1234567890abcdef1")
                .userId(100L)
                .fullName("Jane Doe")
                .phone("+1-555-1234")
                .address("San Francisco, CA")
                .headline("Senior Java Developer")
                .summary("Experienced engineer")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .skills(new ArrayList<>())
                .educations(new ArrayList<>())
                .experiences(new ArrayList<>())
                .build();
    }

    @Test
    void shouldCreateCandidateSuccessfully() {
        CreateCandidateRequest request = CreateCandidateRequest.builder()
                .userId(100L)
                .fullName("Jane Doe")
                .phone("+1-555-1234")
                .address("San Francisco, CA")
                .headline("Senior Java Developer")
                .summary("Experienced engineer")
                .build();

        when(candidateRepository.existsByUserId(100L)).thenReturn(false);
        when(candidateRepository.save(any(Candidate.class))).thenReturn(sampleCandidate);

        CandidateResponse response = candidateService.createCandidate(request, 100L);

        assertNotNull(response);
        assertEquals("66c3abc1234567890abcdef1", response.getId());
        assertEquals("Jane Doe", response.getFullName());
        assertEquals(100L, response.getUserId());
        verify(candidateRepository, times(1)).save(any(Candidate.class));
    }

    @Test
    void shouldThrowDuplicateResourceWhenCreatingCandidateWithExistingUserId() {
        CreateCandidateRequest request = CreateCandidateRequest.builder()
                .userId(100L)
                .fullName("Jane Doe")
                .build();

        when(candidateRepository.existsByUserId(100L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> candidateService.createCandidate(request, 100L));
    }

    @Test
    void shouldGetCandidateById() {
        when(candidateRepository.findById("66c3abc1234567890abcdef1")).thenReturn(Optional.of(sampleCandidate));

        CandidateResponse response = candidateService.getCandidateById("66c3abc1234567890abcdef1");

        assertNotNull(response);
        assertEquals("Jane Doe", response.getFullName());
    }

    @Test
    void shouldThrowNotFoundWhenCandidateDoesNotExist() {
        when(candidateRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> candidateService.getCandidateById("non-existent-id"));
    }

    @Test
    void shouldGetCandidateByUserId() {
        when(candidateRepository.findByUserId(100L)).thenReturn(Optional.of(sampleCandidate));

        CandidateResponse response = candidateService.getCandidateByUserId(100L);

        assertNotNull(response);
        assertEquals(100L, response.getUserId());
    }

    @Test
    void shouldUpdateCandidateSuccessfullyWhenAuthorized() {
        UpdateCandidateRequest updateReq = UpdateCandidateRequest.builder()
                .fullName("Jane Updated")
                .phone("+1-555-9999")
                .address("New York, NY")
                .headline("Lead Architect")
                .summary("Updated bio")
                .build();

        when(candidateRepository.findById("66c3abc1234567890abcdef1")).thenReturn(Optional.of(sampleCandidate));
        when(candidateRepository.save(any(Candidate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CandidateResponse response = candidateService.updateCandidate("66c3abc1234567890abcdef1", updateReq, 100L, "ROLE_CANDIDATE");

        assertNotNull(response);
        assertEquals("Jane Updated", response.getFullName());
        assertEquals("Lead Architect", response.getHeadline());
    }

    @Test
    void shouldThrowUnauthorizedWhenModifyingOtherUsersProfile() {
        UpdateCandidateRequest updateReq = UpdateCandidateRequest.builder()
                .fullName("Jane Updated")
                .build();

        when(candidateRepository.findById("66c3abc1234567890abcdef1")).thenReturn(Optional.of(sampleCandidate));

        assertThrows(UnauthorizedException.class, () ->
                candidateService.updateCandidate("66c3abc1234567890abcdef1", updateReq, 999L, "ROLE_CANDIDATE"));
    }

    @Test
    void shouldAddSkillSuccessfully() {
        SkillRequest skillReq = SkillRequest.builder()
                .skillName("Spring Boot")
                .proficiencyLevel("ADVANCED")
                .build();

        when(candidateRepository.findById("66c3abc1234567890abcdef1")).thenReturn(Optional.of(sampleCandidate));
        when(candidateRepository.save(any(Candidate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SkillResponse response = candidateService.addSkill("66c3abc1234567890abcdef1", skillReq, 100L, "ROLE_CANDIDATE");

        assertNotNull(response);
        assertEquals("Spring Boot", response.getSkillName());
        assertEquals("ADVANCED", response.getProficiencyLevel());
        assertEquals("66c3abc1234567890abcdef1", response.getCandidateId());
        assertNotNull(response.getId());
        assertEquals(1, sampleCandidate.getSkills().size());
    }

    @Test
    void shouldDeleteSkillSuccessfully() {
        Skill existingSkill = Skill.builder()
                .id("skill-123")
                .skillName("Docker")
                .proficiencyLevel("INTERMEDIATE")
                .build();
        sampleCandidate.addSkill(existingSkill);

        when(candidateRepository.findById("66c3abc1234567890abcdef1")).thenReturn(Optional.of(sampleCandidate));
        when(candidateRepository.save(any(Candidate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        candidateService.deleteSkill("66c3abc1234567890abcdef1", "skill-123", 100L, "ROLE_CANDIDATE");

        assertEquals(0, sampleCandidate.getSkills().size());
        verify(candidateRepository, times(1)).save(sampleCandidate);
    }

    @Test
    void shouldAddEducationSuccessfully() {
        EducationRequest eduReq = EducationRequest.builder()
                .institution("MIT")
                .degree("Master of Science")
                .fieldOfStudy("Computer Science")
                .startDate(LocalDate.of(2018, 9, 1))
                .endDate(LocalDate.of(2020, 6, 1))
                .build();

        when(candidateRepository.findById("66c3abc1234567890abcdef1")).thenReturn(Optional.of(sampleCandidate));
        when(candidateRepository.save(any(Candidate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EducationResponse response = candidateService.addEducation("66c3abc1234567890abcdef1", eduReq, 100L, "ROLE_CANDIDATE");

        assertNotNull(response);
        assertEquals("MIT", response.getInstitution());
        assertEquals("Master of Science", response.getDegree());
        assertEquals("66c3abc1234567890abcdef1", response.getCandidateId());
        assertNotNull(response.getId());
        assertEquals(1, sampleCandidate.getEducations().size());
    }

    @Test
    void shouldDeleteEducationSuccessfully() {
        Education existingEdu = Education.builder()
                .id("edu-123")
                .institution("MIT")
                .degree("MS")
                .build();
        sampleCandidate.addEducation(existingEdu);

        when(candidateRepository.findById("66c3abc1234567890abcdef1")).thenReturn(Optional.of(sampleCandidate));
        when(candidateRepository.save(any(Candidate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        candidateService.deleteEducation("66c3abc1234567890abcdef1", "edu-123", 100L, "ROLE_CANDIDATE");

        assertEquals(0, sampleCandidate.getEducations().size());
        verify(candidateRepository, times(1)).save(sampleCandidate);
    }

    @Test
    void shouldAddExperienceSuccessfully() {
        ExperienceRequest expReq = ExperienceRequest.builder()
                .companyName("Acme Corp")
                .jobTitle("Backend Developer")
                .startDate(LocalDate.of(2020, 7, 1))
                .endDate(LocalDate.of(2023, 1, 1))
                .description("Developed microservices in Java")
                .build();

        when(candidateRepository.findById("66c3abc1234567890abcdef1")).thenReturn(Optional.of(sampleCandidate));
        when(candidateRepository.save(any(Candidate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExperienceResponse response = candidateService.addExperience("66c3abc1234567890abcdef1", expReq, 100L, "ROLE_CANDIDATE");

        assertNotNull(response);
        assertEquals("Acme Corp", response.getCompanyName());
        assertEquals("Backend Developer", response.getJobTitle());
        assertEquals("66c3abc1234567890abcdef1", response.getCandidateId());
        assertNotNull(response.getId());
        assertEquals(1, sampleCandidate.getExperiences().size());
    }

    @Test
    void shouldDeleteExperienceSuccessfully() {
        Experience existingExp = Experience.builder()
                .id("exp-123")
                .companyName("Acme Corp")
                .jobTitle("Backend Developer")
                .build();
        sampleCandidate.addExperience(existingExp);

        when(candidateRepository.findById("66c3abc1234567890abcdef1")).thenReturn(Optional.of(sampleCandidate));
        when(candidateRepository.save(any(Candidate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        candidateService.deleteExperience("66c3abc1234567890abcdef1", "exp-123", 100L, "ROLE_CANDIDATE");

        assertEquals(0, sampleCandidate.getExperiences().size());
        verify(candidateRepository, times(1)).save(sampleCandidate);
    }
}
