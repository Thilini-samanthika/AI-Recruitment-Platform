package com.recruitment.company.repository;

import com.recruitment.company.entity.Recruiter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecruiterRepository extends MongoRepository<Recruiter, String> {

    List<Recruiter> findByCompanyId(String companyId);

    List<Recruiter> findByDepartmentId(String departmentId);

    Optional<Recruiter> findByIdAndCompanyId(String id, String companyId);

    Optional<Recruiter> findByEmail(String email);

    boolean existsByCompanyIdAndEmailIgnoreCase(String companyId, String email);

    void deleteByCompanyId(String companyId);
}
