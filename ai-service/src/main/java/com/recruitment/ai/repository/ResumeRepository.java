package com.recruitment.ai.repository;

import com.recruitment.ai.entity.Resume;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends MongoRepository<Resume, String> {
    List<Resume> findByCandidateIdOrderByUploadedAtDesc(Long candidateId);
    Optional<Resume> findFirstByCandidateIdOrderByUploadedAtDesc(Long candidateId);
}
