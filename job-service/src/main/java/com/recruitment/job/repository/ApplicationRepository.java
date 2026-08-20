package com.recruitment.job.repository;

import com.recruitment.job.entity.Application;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends MongoRepository<Application, String> {

    List<Application> findByJobId(String jobId);

    List<Application> findByCandidateId(Long candidateId);

    Optional<Application> findByJobIdAndCandidateId(String jobId, Long candidateId);

    boolean existsByJobIdAndCandidateId(String jobId, Long candidateId);

    long countByJobId(String jobId);
}
