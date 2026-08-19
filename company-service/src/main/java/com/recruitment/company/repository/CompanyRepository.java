package com.recruitment.company.repository;

import com.recruitment.company.entity.Company;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends MongoRepository<Company, String> {

    Optional<Company> findByUserId(Long userId);

    Optional<Company> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUserId(Long userId);
}
