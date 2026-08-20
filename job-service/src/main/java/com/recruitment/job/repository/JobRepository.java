package com.recruitment.job.repository;

import com.recruitment.job.entity.Job;
import com.recruitment.job.entity.JobStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends MongoRepository<Job, String>, JobRepositoryCustom {

    List<Job> findByCompanyId(Long companyId);

    List<Job> findByStatus(JobStatus status);
}
