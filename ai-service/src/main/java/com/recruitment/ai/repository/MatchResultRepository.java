package com.recruitment.ai.repository;

import com.recruitment.ai.entity.MatchResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchResultRepository extends MongoRepository<MatchResult, String> {
    List<MatchResult> findByResumeIdOrderByCreatedAtDesc(String resumeId);
    List<MatchResult> findByCandidateIdOrderByCreatedAtDesc(Long candidateId);
    Optional<MatchResult> findFirstByResumeIdAndJobIdOrderByCreatedAtDesc(String resumeId, Long jobId);
}
