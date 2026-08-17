package com.recruitment.ai.repository;

import com.recruitment.ai.entity.MatchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {
    List<MatchResult> findByResumeIdOrderByCreatedAtDesc(Long resumeId);
    List<MatchResult> findByCandidateIdOrderByCreatedAtDesc(Long candidateId);
    Optional<MatchResult> findFirstByResumeIdAndJobIdOrderByCreatedAtDesc(Long resumeId, Long jobId);
}
