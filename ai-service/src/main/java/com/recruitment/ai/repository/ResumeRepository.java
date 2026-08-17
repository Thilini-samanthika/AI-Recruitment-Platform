package com.recruitment.ai.repository;

import com.recruitment.ai.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByCandidateIdOrderByUploadedAtDesc(Long candidateId);
    Optional<Resume> findFirstByCandidateIdOrderByUploadedAtDesc(Long candidateId);
}
