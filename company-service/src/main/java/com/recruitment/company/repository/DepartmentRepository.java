package com.recruitment.company.repository;

import com.recruitment.company.entity.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends MongoRepository<Department, String> {

    List<Department> findByCompanyId(String companyId);

    Optional<Department> findByIdAndCompanyId(String id, String companyId);

    boolean existsByCompanyIdAndNameIgnoreCase(String companyId, String name);

    void deleteByCompanyId(String companyId);
}
