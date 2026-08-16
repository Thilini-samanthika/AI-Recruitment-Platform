package com.recruitment.candidate.repository;

import com.recruitment.candidate.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    List<Experience> findByCandidateId(Long candidateId);

    Optional<Experience> findByIdAndCandidateId(Long id, Long candidateId);

    void deleteByIdAndCandidateId(Long id, Long candidateId);
}
