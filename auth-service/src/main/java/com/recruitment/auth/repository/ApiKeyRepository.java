package com.recruitment.auth.repository;

import com.recruitment.auth.entity.ApiKey;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiKeyRepository extends MongoRepository<ApiKey, String> {
    Optional<ApiKey> findByKeyValueAndActiveTrue(String keyValue);
    Optional<ApiKey> findByServiceNameAndActiveTrue(String serviceName);
    List<ApiKey> findByServiceName(String serviceName);
}
