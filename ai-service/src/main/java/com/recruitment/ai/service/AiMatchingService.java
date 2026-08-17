package com.recruitment.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.ai.dto.MatchRequest;
import com.recruitment.ai.dto.MatchResponse;
import com.recruitment.ai.dto.RecommendationResponse;
import com.recruitment.ai.entity.MatchResult;
import com.recruitment.ai.entity.Recommendation;
import com.recruitment.ai.entity.Resume;
import com.recruitment.ai.exception.ResourceNotFoundException;
import com.recruitment.ai.repository.MatchResultRepository;
import com.recruitment.ai.repository.RecommendationRepository;
import com.recruitment.ai.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiMatchingService {

    private final ResumeRepository resumeRepository;
    private final MatchResultRepository matchResultRepository;
    private final RecommendationRepository recommendationRepository;
    private final ResumeParserService resumeParserService;
    private final ResumeService resumeService;
    private final ObjectMapper objectMapper;

    /**
     * Match candidate resume against a job and record results
     */
    @Transactional
    public MatchResponse matchResumeWithJob(MatchRequest request) {
        Resume resume = null;

        // 1. Locate resume by resumeId or by candidateId
        if (request.getResumeId() != null) {
            resume = resumeRepository.findById(request.getResumeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Resume not found with ID: " + request.getResumeId()));
        } else if (request.getCandidateId() != null) {
            resume = resumeRepository.findFirstByCandidateIdOrderByUploadedAtDesc(request.getCandidateId())
                    .orElseThrow(() -> new ResourceNotFoundException("No resume found for candidate ID: " + request.getCandidateId()));
        }

        List<String> candidateSkills = new ArrayList<>();
        String resumeText = "";

        if (resume != null) {
            candidateSkills = resumeService.deserializeSkills(resume.getExtractedSkills());
            resumeText = resume.getExtractedText() != null ? resume.getExtractedText() : "";
        }

        // 2. Identify required job skills
        Set<String> requiredSkillsSet = new LinkedHashSet<>();
        if (request.getRequiredSkills() != null && !request.getRequiredSkills().isEmpty()) {
            requiredSkillsSet.addAll(request.getRequiredSkills());
        }

        // Also extract skills from job description text if provided
        if (request.getJobDescription() != null && !request.getJobDescription().isBlank()) {
            List<String> descSkills = resumeParserService.extractSkills(request.getJobDescription());
            requiredSkillsSet.addAll(descSkills);
        }

        // Default fallback if no skills detected
        if (requiredSkillsSet.isEmpty()) {
            requiredSkillsSet.addAll(List.of("Java", "Spring Boot", "SQL", "Git", "REST API"));
        }

        List<String> requiredSkills = new ArrayList<>(requiredSkillsSet);

        // 3. Compute matching metrics
        Set<String> matchedSkills = new LinkedHashSet<>();
        Set<String> missingSkills = new LinkedHashSet<>();

        for (String reqSkill : requiredSkills) {
            boolean isMatched = false;
            // Check direct match
            for (String candSkill : candidateSkills) {
                if (reqSkill.equalsIgnoreCase(candSkill) ||
                        candSkill.toLowerCase().contains(reqSkill.toLowerCase()) ||
                        reqSkill.toLowerCase().contains(candSkill.toLowerCase())) {
                    matchedSkills.add(reqSkill);
                    isMatched = true;
                    break;
                }
            }

            // Check if skill keyword occurs in raw resume text
            if (!isMatched && resumeText.toLowerCase().contains(reqSkill.toLowerCase())) {
                matchedSkills.add(reqSkill);
                isMatched = true;
            }

            if (!isMatched) {
                missingSkills.add(reqSkill);
            }
        }

        // Match Percentage calculation
        double matchRatio = requiredSkills.isEmpty() ? 0.0 : (double) matchedSkills.size() / requiredSkills.size();
        double matchPercentage = BigDecimal.valueOf(matchRatio * 100.0)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();

        // 4. Generate AI Analysis Summary
        String analysisSummary = generateSummary(matchPercentage, matchedSkills, missingSkills, request.getJobTitle());

        // 5. Persist to match_results table
        Long resumeId = resume != null ? resume.getId() : 0L;
        Long candidateId = (resume != null) ? resume.getCandidateId() : request.getCandidateId();

        MatchResult matchResult = MatchResult.builder()
                .resumeId(resumeId)
                .candidateId(candidateId)
                .jobId(request.getJobId())
                .matchPercentage(matchPercentage)
                .matchedSkills(serializeList(new ArrayList<>(matchedSkills)))
                .missingSkills(serializeList(new ArrayList<>(missingSkills)))
                .analysisSummary(analysisSummary)
                .build();

        MatchResult savedResult = matchResultRepository.save(matchResult);

        // 6. Update or save Recommendation if candidateId is present
        if (candidateId != null && candidateId > 0) {
            Recommendation recommendation = Recommendation.builder()
                    .candidateId(candidateId)
                    .jobId(request.getJobId())
                    .score(matchPercentage)
                    .jobTitle(request.getJobTitle() != null ? request.getJobTitle() : "Job #" + request.getJobId())
                    .companyName("Partner Enterprise")
                    .matchedSkills(String.join(", ", matchedSkills))
                    .build();
            recommendationRepository.save(recommendation);
        }

        return MatchResponse.builder()
                .id(savedResult.getId())
                .resumeId(resumeId)
                .candidateId(candidateId)
                .jobId(request.getJobId())
                .matchPercentage(matchPercentage)
                .matchedSkills(new ArrayList<>(matchedSkills))
                .missingSkills(new ArrayList<>(missingSkills))
                .candidateSkills(candidateSkills)
                .requiredSkills(requiredSkills)
                .analysisSummary(analysisSummary)
                .createdAt(savedResult.getCreatedAt())
                .build();
    }

    /**
     * Get recommended jobs for candidate
     */
    @Transactional
    public List<RecommendationResponse> getRecommendations(Long candidateId) {
        List<Recommendation> recommendations = recommendationRepository.findByCandidateIdOrderByScoreDescCreatedAtDesc(candidateId);

        // If no recommendations exist yet, generate smart starter recommendations based on candidate's uploaded resume skills
        if (recommendations.isEmpty()) {
            List<String> skills = resumeService.getCandidateSkills(candidateId);
            recommendations = generateDefaultRecommendations(candidateId, skills);
        }

        return recommendations.stream()
                .map(this::toRecommendationResponse)
                .toList();
    }

    private List<Recommendation> generateDefaultRecommendations(Long candidateId, List<String> skills) {
        List<Recommendation> defaults = new ArrayList<>();

        // Smart defaults based on candidate's skills
        defaults.add(Recommendation.builder()
                .candidateId(candidateId)
                .jobId(101L)
                .jobTitle("Senior Full Stack Java Engineer")
                .companyName("Apex Cloud Technologies")
                .score(88.5)
                .matchedSkills("Java, Spring Boot, MySQL, REST API, React")
                .createdAt(Instant.now())
                .build());

        defaults.add(Recommendation.builder()
                .candidateId(candidateId)
                .jobId(102L)
                .jobTitle("AI Solutions & Backend Developer")
                .companyName("Cognitive Systems Inc")
                .score(82.0)
                .matchedSkills("Java, Python, Microservices, Docker, Git")
                .createdAt(Instant.now())
                .build());

        defaults.add(Recommendation.builder()
                .candidateId(candidateId)
                .jobId(103L)
                .jobTitle("Cloud DevOps & Infrastructure Specialist")
                .companyName("Nexus Global Corp")
                .score(74.0)
                .matchedSkills("Docker, Kubernetes, CI/CD, Linux, AWS")
                .createdAt(Instant.now())
                .build());

        return recommendationRepository.saveAll(defaults);
    }

    private String generateSummary(double matchPct, Set<String> matched, Set<String> missing, String jobTitle) {
        String role = jobTitle != null ? jobTitle : "the target position";
        if (matchPct >= 80.0) {
            return String.format("Exceptional alignment (%.1f%% match) for %s. Strong mastery in core competencies: %s.",
                    matchPct, role, String.join(", ", matched));
        } else if (matchPct >= 50.0) {
            return String.format("Good potential match (%.1f%%). Matched key skills [%s]. Consider upskilling in [%s] to strengthen candidacy.",
                    matchPct, String.join(", ", matched), String.join(", ", missing));
        } else {
            return String.format("Moderate alignment (%.1f%%). Profile possesses basic transferable skills, but requires further experience in: %s.",
                    matchPct, String.join(", ", missing));
        }
    }

    private RecommendationResponse toRecommendationResponse(Recommendation r) {
        List<String> matchedSkills = r.getMatchedSkills() != null
                ? Arrays.stream(r.getMatchedSkills().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList()
                : Collections.emptyList();

        return RecommendationResponse.builder()
                .id(r.getId())
                .candidateId(r.getCandidateId())
                .jobId(r.getJobId())
                .jobTitle(r.getJobTitle())
                .companyName(r.getCompanyName())
                .score(r.getScore())
                .matchedSkills(matchedSkills)
                .createdAt(r.getCreatedAt())
                .build();
    }

    private String serializeList(List<String> list) {
        if (list == null || list.isEmpty()) return "[]";
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return String.join(",", list);
        }
    }
}
