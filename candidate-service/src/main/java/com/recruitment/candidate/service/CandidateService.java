package com.recruitment.candidate.service;

import com.recruitment.candidate.dto.*;

import java.util.List;

public interface CandidateService {

    CandidateResponse createCandidate(CreateCandidateRequest request, Long authenticatedUserId);

    List<CandidateResponse> getAllCandidates();

    CandidateResponse getCandidateById(Long id);

    CandidateResponse getCandidateByUserId(Long userId);

    CandidateResponse updateCandidate(Long id, UpdateCandidateRequest request, Long authenticatedUserId, String authenticatedRole);

    void deleteCandidate(Long id, Long authenticatedUserId, String authenticatedRole);

    SkillResponse addSkill(Long candidateId, SkillRequest request, Long authenticatedUserId, String authenticatedRole);

    List<SkillResponse> getSkills(Long candidateId);

    void deleteSkill(Long candidateId, Long skillId, Long authenticatedUserId, String authenticatedRole);

    EducationResponse addEducation(Long candidateId, EducationRequest request, Long authenticatedUserId, String authenticatedRole);

    List<EducationResponse> getEducation(Long candidateId);

    void deleteEducation(Long candidateId, Long educationId, Long authenticatedUserId, String authenticatedRole);

    ExperienceResponse addExperience(Long candidateId, ExperienceRequest request, Long authenticatedUserId, String authenticatedRole);

    List<ExperienceResponse> getExperience(Long candidateId);

    void deleteExperience(Long candidateId, Long experienceId, Long authenticatedUserId, String authenticatedRole);
}
