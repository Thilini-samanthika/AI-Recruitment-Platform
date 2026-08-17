package com.recruitment.ai.repository;

import com.recruitment.ai.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByCandidateIdOrderByScoreDescCreatedAtDesc(Long candidateId);
    void deleteByCandidateId(Long candidateId);
}
