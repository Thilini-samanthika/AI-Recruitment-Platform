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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;

    @Override
    public CandidateResponse createCandidate(CreateCandidateRequest request, Long authenticatedUserId) {
        Long targetUserId = request.getUserId() != null ? request.getUserId() : authenticatedUserId;

        if (targetUserId == null) {
            throw new IllegalArgumentException("User ID must be provided either in request body or authentication headers");
        }

        if (candidateRepository.existsByUserId(targetUserId)) {
            throw new DuplicateResourceException("Candidate profile already exists for User ID: " + targetUserId);
        }

        Candidate candidate = Candidate.builder()
                .userId(targetUserId)
                .fullName(request.getFullName().trim())
                .phone(request.getPhone())
                .address(request.getAddress())
                .headline(request.getHeadline())
                .summary(request.getSummary())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Candidate saved = candidateRepository.save(candidate);
        log.info("Created candidate profile ID: {} for user: {}", saved.getId(), targetUserId);
        return mapToCandidateResponse(saved);
    }

    @Override
    public List<CandidateResponse> getAllCandidates() {
        return candidateRepository.findAll().stream()
                .map(this::mapToCandidateResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CandidateResponse getCandidateById(String id) {
        Candidate candidate = findCandidateOrThrow(id);
        return mapToCandidateResponse(candidate);
    }

    @Override
    public CandidateResponse getCandidateByUserId(Long userId) {
        Candidate candidate = candidateRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found for user ID: " + userId));
        return mapToCandidateResponse(candidate);
    }

    @Override
    public CandidateResponse updateCandidate(String id, UpdateCandidateRequest request, Long authenticatedUserId, String authenticatedRole) {
        Candidate candidate = findCandidateOrThrow(id);
        validateOwnership(candidate, authenticatedUserId, authenticatedRole);

        candidate.setFullName(request.getFullName().trim());
        candidate.setPhone(request.getPhone());
        candidate.setAddress(request.getAddress());
        candidate.setHeadline(request.getHeadline());
        candidate.setSummary(request.getSummary());
        candidate.setUpdatedAt(LocalDateTime.now());

        Candidate updated = candidateRepository.save(candidate);
        log.info("Updated candidate profile ID: {}", updated.getId());
        return mapToCandidateResponse(updated);
    }

    @Override
    public void deleteCandidate(String id, Long authenticatedUserId, String authenticatedRole) {
        Candidate candidate = findCandidateOrThrow(id);
        validateOwnership(candidate, authenticatedUserId, authenticatedRole);

        candidateRepository.delete(candidate);
        log.info("Deleted candidate profile ID: {}", id);
    }

    @Override
    public SkillResponse addSkill(String candidateId, SkillRequest request, Long authenticatedUserId, String authenticatedRole) {
        Candidate candidate = findCandidateOrThrow(candidateId);
        validateOwnership(candidate, authenticatedUserId, authenticatedRole);

        Skill skill = Skill.builder()
                .id(UUID.randomUUID().toString())
                .skillName(request.getSkillName().trim())
                .proficiencyLevel(request.getProficiencyLevel())
                .build();

        candidate.addSkill(skill);
        candidate.setUpdatedAt(LocalDateTime.now());
        candidateRepository.save(candidate);
        log.info("Added skill '{}' (ID: {}) to candidate ID: {}", skill.getSkillName(), skill.getId(), candidateId);
        return mapToSkillResponse(skill, candidate.getId());
    }

    @Override
    public List<SkillResponse> getSkills(String candidateId) {
        Candidate candidate = findCandidateOrThrow(candidateId);
        if (candidate.getSkills() == null) {
            return Collections.emptyList();
        }
        return candidate.getSkills().stream()
                .map(skill -> mapToSkillResponse(skill, candidate.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSkill(String candidateId, String skillId, Long authenticatedUserId, String authenticatedRole) {
        Candidate candidate = findCandidateOrThrow(candidateId);
        validateOwnership(candidate, authenticatedUserId, authenticatedRole);

        boolean removed = candidate.removeSkill(skillId);
        if (!removed) {
            throw new ResourceNotFoundException("Skill ID " + skillId + " not found for candidate ID " + candidateId);
        }

        candidate.setUpdatedAt(LocalDateTime.now());
        candidateRepository.save(candidate);
        log.info("Deleted skill ID: {} from candidate ID: {}", skillId, candidateId);
    }

    @Override
    public EducationResponse addEducation(String candidateId, EducationRequest request, Long authenticatedUserId, String authenticatedRole) {
        Candidate candidate = findCandidateOrThrow(candidateId);
        validateOwnership(candidate, authenticatedUserId, authenticatedRole);

        Education education = Education.builder()
                .id(UUID.randomUUID().toString())
                .institution(request.getInstitution().trim())
                .degree(request.getDegree().trim())
                .fieldOfStudy(request.getFieldOfStudy())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        candidate.addEducation(education);
        candidate.setUpdatedAt(LocalDateTime.now());
        candidateRepository.save(candidate);
        log.info("Added education ID: {} to candidate ID: {}", education.getId(), candidateId);
        return mapToEducationResponse(education, candidate.getId());
    }

    @Override
    public List<EducationResponse> getEducation(String candidateId) {
        Candidate candidate = findCandidateOrThrow(candidateId);
        if (candidate.getEducations() == null) {
            return Collections.emptyList();
        }
        return candidate.getEducations().stream()
                .map(edu -> mapToEducationResponse(edu, candidate.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteEducation(String candidateId, String educationId, Long authenticatedUserId, String authenticatedRole) {
        Candidate candidate = findCandidateOrThrow(candidateId);
        validateOwnership(candidate, authenticatedUserId, authenticatedRole);

        boolean removed = candidate.removeEducation(educationId);
        if (!removed) {
            throw new ResourceNotFoundException("Education ID " + educationId + " not found for candidate ID " + candidateId);
        }

        candidate.setUpdatedAt(LocalDateTime.now());
        candidateRepository.save(candidate);
        log.info("Deleted education ID: {} from candidate ID: {}", educationId, candidateId);
    }

    @Override
    public ExperienceResponse addExperience(String candidateId, ExperienceRequest request, Long authenticatedUserId, String authenticatedRole) {
        Candidate candidate = findCandidateOrThrow(candidateId);
        validateOwnership(candidate, authenticatedUserId, authenticatedRole);

        Experience experience = Experience.builder()
                .id(UUID.randomUUID().toString())
                .companyName(request.getCompanyName().trim())
                .jobTitle(request.getJobTitle().trim())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .description(request.getDescription())
                .build();

        candidate.addExperience(experience);
        candidate.setUpdatedAt(LocalDateTime.now());
        candidateRepository.save(candidate);
        log.info("Added experience ID: {} to candidate ID: {}", experience.getId(), candidateId);
        return mapToExperienceResponse(experience, candidate.getId());
    }

    @Override
    public List<ExperienceResponse> getExperience(String candidateId) {
        Candidate candidate = findCandidateOrThrow(candidateId);
        if (candidate.getExperiences() == null) {
            return Collections.emptyList();
        }
        return candidate.getExperiences().stream()
                .map(exp -> mapToExperienceResponse(exp, candidate.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteExperience(String candidateId, String experienceId, Long authenticatedUserId, String authenticatedRole) {
        Candidate candidate = findCandidateOrThrow(candidateId);
        validateOwnership(candidate, authenticatedUserId, authenticatedRole);

        boolean removed = candidate.removeExperience(experienceId);
        if (!removed) {
            throw new ResourceNotFoundException("Experience ID " + experienceId + " not found for candidate ID " + candidateId);
        }

        candidate.setUpdatedAt(LocalDateTime.now());
        candidateRepository.save(candidate);
        log.info("Deleted experience ID: {} from candidate ID: {}", experienceId, candidateId);
    }

    // Helper methods
    private Candidate findCandidateOrThrow(String id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found with ID: " + id));
    }

    private void validateOwnership(Candidate candidate, Long authenticatedUserId, String authenticatedRole) {
        if (authenticatedUserId == null) {
            return;
        }
        if ("ROLE_ADMIN".equalsIgnoreCase(authenticatedRole)) {
            return;
        }
        if (candidate.getUserId() != null && !candidate.getUserId().equals(authenticatedUserId)) {
            log.warn("Access denied: User ID {} attempted to modify Candidate ID {} (owned by User ID {})",
                    authenticatedUserId, candidate.getId(), candidate.getUserId());
            throw new UnauthorizedException("You are not authorized to modify this candidate profile");
        }
    }

    private CandidateResponse mapToCandidateResponse(Candidate candidate) {
        List<SkillResponse> skillResponses = candidate.getSkills() != null
                ? candidate.getSkills().stream().map(s -> mapToSkillResponse(s, candidate.getId())).collect(Collectors.toList())
                : Collections.emptyList();

        List<EducationResponse> educationResponses = candidate.getEducations() != null
                ? candidate.getEducations().stream().map(e -> mapToEducationResponse(e, candidate.getId())).collect(Collectors.toList())
                : Collections.emptyList();

        List<ExperienceResponse> experienceResponses = candidate.getExperiences() != null
                ? candidate.getExperiences().stream().map(exp -> mapToExperienceResponse(exp, candidate.getId())).collect(Collectors.toList())
                : Collections.emptyList();

        return CandidateResponse.builder()
                .id(candidate.getId())
                .userId(candidate.getUserId())
                .fullName(candidate.getFullName())
                .phone(candidate.getPhone())
                .address(candidate.getAddress())
                .headline(candidate.getHeadline())
                .summary(candidate.getSummary())
                .createdAt(candidate.getCreatedAt())
                .updatedAt(candidate.getUpdatedAt())
                .skills(skillResponses)
                .educations(educationResponses)
                .experiences(experienceResponses)
                .build();
    }

    private SkillResponse mapToSkillResponse(Skill skill, String candidateId) {
        return SkillResponse.builder()
                .id(skill.getId())
                .candidateId(candidateId)
                .skillName(skill.getSkillName())
                .proficiencyLevel(skill.getProficiencyLevel())
                .build();
    }

    private EducationResponse mapToEducationResponse(Education education, String candidateId) {
        return EducationResponse.builder()
                .id(education.getId())
                .candidateId(candidateId)
                .institution(education.getInstitution())
                .degree(education.getDegree())
                .fieldOfStudy(education.getFieldOfStudy())
                .startDate(education.getStartDate())
                .endDate(education.getEndDate())
                .build();
    }

    private ExperienceResponse mapToExperienceResponse(Experience experience, String candidateId) {
        return ExperienceResponse.builder()
                .id(experience.getId())
                .candidateId(candidateId)
                .companyName(experience.getCompanyName())
                .jobTitle(experience.getJobTitle())
                .startDate(experience.getStartDate())
                .endDate(experience.getEndDate())
                .description(experience.getDescription())
                .build();
    }
}
