package com.recruitment.candidate.repository;

import com.recruitment.candidate.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findByCandidateId(Long candidateId);

    Optional<Skill> findByIdAndCandidateId(Long id, Long candidateId);

    void deleteByIdAndCandidateId(Long id, Long candidateId);
}
