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
import com.recruitment.candidate.repository.EducationRepository;
import com.recruitment.candidate.repository.ExperienceRepository;
import com.recruitment.candidate.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final SkillRepository skillRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;

    @Override
    @Transactional
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
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .headline(request.getHeadline())
                .summary(request.getSummary())
                .build();

        Candidate saved = candidateRepository.save(candidate);
        log.info("Created candidate profile ID: {} for user: {}", saved.getId(), targetUserId);
        return mapToCandidateResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CandidateResponse> getAllCandidates() {
        return candidateRepository.findAll().stream()
                .map(this::mapToCandidateResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CandidateResponse getCandidateById(Long id) {
        Candidate candidate = findCandidateOrThrow(id);
        return mapToCandidateResponse(candidate);
    }

    @Override
    @Transactional(readOnly = true)
    public CandidateResponse getCandidateByUserId(Long userId) {
        Candidate candidate = candidateRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found for user ID: " + userId));
        return mapToCandidateResponse(candidate);
    }

    @Override
    @Transactional
    public CandidateResponse updateCandidate(Long id, UpdateCandidateRequest request, Long authenticatedUserId, String authenticatedRole) {
        Candidate candidate = findCandidateOrThrow(id);
        validateOwnership(candidate, authenticatedUserId, authenticatedRole);

        candidate.setFullName(request.getFullName());
        candidate.setPhone(request.getPhone());
        candidate.setAddress(request.getAddress());
        candidate.setHeadline(request.getHeadline());
        candidate.setSummary(request.getSummary());

        Candidate updated = candidateRepository.save(candidate);
        log.info("Updated candidate profile ID: {}", updated.getId());
        return mapToCandidateResponse(updated);
    }

    @Override
    @Transactional
    public void deleteCandidate(Long id, Long authenticatedUserId, String authenticatedRole) {
        Candidate candidate = findCandidateOrThrow(id);
        validateOwnership(candidate, authenticatedUserId, authenticatedRole);

        candidateRepository.delete(candidate);
        log.info("Deleted candidate profile ID: {}", id);
    }

    @Override
    @Transactional
    public SkillResponse addSkill(Long candidateId, SkillRequest request, Long authenticatedUserId, String authenticatedRole) {
        Candidate candidate = findCandidateOrThrow(candidateId);
        validateOwnership(candidate, authenticatedUserId, authenticatedRole);

        Skill skill = Skill.builder()
                .candidate(candidate)
                .skillName(request.getSkillName().trim())
                .proficiencyLevel(request.getProficiencyLevel())
                .build();

        candidate.addSkill(skill);
        Skill saved = skillRepository.save(skill);
        log.info("Added skill '{}' (ID: {}) to candidate ID: {}", saved.getSkillName(), saved.getId(), candidateId);
        return mapToSkillResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponse> getSkills(Long candidateId) {
        findCandidateOrThrow(candidateId);
        return skillRepository.findByCandidateId(candidateId).stream()
                .map(this::mapToSkillResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteSkill(Long candidateId, Long skillId, Long authenticatedUserId, String authenticatedRole) {
        Candidate candidate = findCandidateOrThrow(candidateId);
        validateOwnership(candidate, authenticatedUserId, authenticatedRole);

        Skill skill = skillRepository.findByIdAndCandidateId(skillId, candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill ID " + skillId + " not found for candidate ID " + candidateId));

        candidate.removeSkill(skill);
        skillRepository.delete(skill);
        log.info("Deleted skill ID: {} from candidate ID: {}", skillId, candidateId);
    }

    @Override
    @Transactional
    public EducationResponse addEducation(Long candidateId, EducationRequest request, Long authenticatedUserId, String authenticatedRole) {
        Candidate candidate = findCandidateOrThrow(candidateId);
        validateOwnership(candidate, authenticatedUserId, authenticatedRole);

        Education education = Education.builder()
                .candidate(candidate)
                .institution(request.getInstitution())
                .degree(request.getDegree())
                .fieldOfStudy(request.getFieldOfStudy())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        candidate.addEducation(education);
        Education saved = educationRepository.save(education);
        log.info("Added education ID: {} to candidate ID: {}", saved.getId(), candidateId);
        return mapToEducationResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EducationResponse> getEducation(Long candidateId) {
        findCandidateOrThrow(candidateId);
        return educationRepository.findByCandidateId(candidateId).stream()
                .map(this::mapToEducationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteEducation(Long candidateId, Long educationId, Long authenticatedUserId, String authenticatedRole) {
        Candidate candidate = findCandidateOrThrow(candidateId);
        validateOwnership(candidate, authenticatedUserId, authenticatedRole);

        Education education = educationRepository.findByIdAndCandidateId(educationId, candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Education ID " + educationId + " not found for candidate ID " + candidateId));

        candidate.removeEducation(education);
        educationRepository.delete(education);
        log.info("Deleted education ID: {} from candidate ID: {}", educationId, candidateId);
    }

    @Override
    @Transactional
    public ExperienceResponse addExperience(Long candidateId, ExperienceRequest request, Long authenticatedUserId, String authenticatedRole) {
        Candidate candidate = findCandidateOrThrow(candidateId);
        validateOwnership(candidate, authenticatedUserId, authenticatedRole);

        Experience experience = Experience.builder()
                .candidate(candidate)
                .companyName(request.getCompanyName())
                .jobTitle(request.getJobTitle())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .description(request.getDescription())
                .build();

        candidate.addExperience(experience);
        Experience saved = experienceRepository.save(experience);
        log.info("Added experience ID: {} to candidate ID: {}", saved.getId(), candidateId);
        return mapToExperienceResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExperienceResponse> getExperience(Long candidateId) {
        findCandidateOrThrow(candidateId);
        return experienceRepository.findByCandidateId(candidateId).stream()
                .map(this::mapToExperienceResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteExperience(Long candidateId, Long experienceId, Long authenticatedUserId, String authenticatedRole) {
        Candidate candidate = findCandidateOrThrow(candidateId);
        validateOwnership(candidate, authenticatedUserId, authenticatedRole);

        Experience experience = experienceRepository.findByIdAndCandidateId(experienceId, candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience ID " + experienceId + " not found for candidate ID " + candidateId));

        candidate.removeExperience(experience);
        experienceRepository.delete(experience);
        log.info("Deleted experience ID: {} from candidate ID: {}", experienceId, candidateId);
    }

    // Helper methods
    private Candidate findCandidateOrThrow(Long id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found with ID: " + id));
    }

    private void validateOwnership(Candidate candidate, Long authenticatedUserId, String authenticatedRole) {
        // If no user context provided (e.g. internal service call with API key or public reading), permit
        if (authenticatedUserId == null) {
            return;
        }
        // Admins can manage any profile
        if ("ROLE_ADMIN".equalsIgnoreCase(authenticatedRole)) {
            return;
        }
        // Candidate can only modify their own profile
        if (!candidate.getUserId().equals(authenticatedUserId)) {
            log.warn("Access denied: User ID {} attempted to modify Candidate ID {} (owned by User ID {})",
                    authenticatedUserId, candidate.getId(), candidate.getUserId());
            throw new UnauthorizedException("You are not authorized to modify this candidate profile");
        }
    }

    private CandidateResponse mapToCandidateResponse(Candidate candidate) {
        List<SkillResponse> skillResponses = candidate.getSkills() != null
                ? candidate.getSkills().stream().map(this::mapToSkillResponse).collect(Collectors.toList())
                : Collections.emptyList();

        List<EducationResponse> educationResponses = candidate.getEducations() != null
                ? candidate.getEducations().stream().map(this::mapToEducationResponse).collect(Collectors.toList())
                : Collections.emptyList();

        List<ExperienceResponse> experienceResponses = candidate.getExperiences() != null
                ? candidate.getExperiences().stream().map(this::mapToExperienceResponse).collect(Collectors.toList())
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

    private SkillResponse mapToSkillResponse(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .candidateId(skill.getCandidate() != null ? skill.getCandidate().getId() : null)
                .skillName(skill.getSkillName())
                .proficiencyLevel(skill.getProficiencyLevel())
                .build();
    }

    private EducationResponse mapToEducationResponse(Education education) {
        return EducationResponse.builder()
                .id(education.getId())
                .candidateId(education.getCandidate() != null ? education.getCandidate().getId() : null)
                .institution(education.getInstitution())
                .degree(education.getDegree())
                .fieldOfStudy(education.getFieldOfStudy())
                .startDate(education.getStartDate())
                .endDate(education.getEndDate())
                .build();
    }

    private ExperienceResponse mapToExperienceResponse(Experience experience) {
        return ExperienceResponse.builder()
                .id(experience.getId())
                .candidateId(experience.getCandidate() != null ? experience.getCandidate().getId() : null)
                .companyName(experience.getCompanyName())
                .jobTitle(experience.getJobTitle())
                .startDate(experience.getStartDate())
                .endDate(experience.getEndDate())
                .description(experience.getDescription())
                .build();
    }
}
