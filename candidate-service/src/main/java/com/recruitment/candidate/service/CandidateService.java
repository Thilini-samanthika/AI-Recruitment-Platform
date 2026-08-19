package com.recruitment.candidate.service;

import com.recruitment.candidate.dto.*;

import java.util.List;

public interface CandidateService {

    CandidateResponse createCandidate(CreateCandidateRequest request, Long authenticatedUserId);

    List<CandidateResponse> getAllCandidates();

    CandidateResponse getCandidateById(String id);

    CandidateResponse getCandidateByUserId(Long userId);

    CandidateResponse updateCandidate(String id, UpdateCandidateRequest request, Long authenticatedUserId, String authenticatedRole);

    void deleteCandidate(String id, Long authenticatedUserId, String authenticatedRole);

    SkillResponse addSkill(String candidateId, SkillRequest request, Long authenticatedUserId, String authenticatedRole);

    List<SkillResponse> getSkills(String candidateId);

    void deleteSkill(String candidateId, String skillId, Long authenticatedUserId, String authenticatedRole);

    EducationResponse addEducation(String candidateId, EducationRequest request, Long authenticatedUserId, String authenticatedRole);

    List<EducationResponse> getEducation(String candidateId);

    void deleteEducation(String candidateId, String educationId, Long authenticatedUserId, String authenticatedRole);

    ExperienceResponse addExperience(String candidateId, ExperienceRequest request, Long authenticatedUserId, String authenticatedRole);

    List<ExperienceResponse> getExperience(String candidateId);

    void deleteExperience(String candidateId, String experienceId, Long authenticatedUserId, String authenticatedRole);
}
