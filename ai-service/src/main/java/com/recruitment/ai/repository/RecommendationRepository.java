package com.recruitment.ai.repository;

import com.recruitment.ai.entity.Recommendation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends MongoRepository<Recommendation, String> {
    List<Recommendation> findByCandidateIdOrderByScoreDescCreatedAtDesc(Long candidateId);
    void deleteByCandidateId(Long candidateId);
}
