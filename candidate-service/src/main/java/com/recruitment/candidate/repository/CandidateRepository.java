package com.recruitment.candidate.repository;

import com.recruitment.candidate.entity.Candidate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateRepository extends MongoRepository<Candidate, String> {

    Optional<Candidate> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
