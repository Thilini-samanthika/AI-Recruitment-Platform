package com.recruitment.auth.repository;

import com.recruitment.auth.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findByKeyValueAndActiveTrue(String keyValue);
    Optional<ApiKey> findByServiceNameAndActiveTrue(String serviceName);
    List<ApiKey> findByServiceName(String serviceName);
}
