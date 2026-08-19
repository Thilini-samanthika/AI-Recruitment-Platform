package com.recruitment.auth.config;

import com.recruitment.auth.entity.ApiKey;
import com.recruitment.auth.entity.Role;
import com.recruitment.auth.repository.ApiKeyRepository;
import com.recruitment.auth.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final ApiKeyRepository apiKeyRepository;

    public DataInitializer(RoleRepository roleRepository, ApiKeyRepository apiKeyRepository) {
        this.roleRepository = roleRepository;
        this.apiKeyRepository = apiKeyRepository;
    }

    @Override
    public void run(String... args) {
        initializeRoles();
        initializeApiKeys();
    }

    private void initializeRoles() {
        seedRoleIfNotExists("ROLE_CANDIDATE");
        seedRoleIfNotExists("ROLE_COMPANY");
        seedRoleIfNotExists("ROLE_ADMIN");
    }

    private void seedRoleIfNotExists(String roleName) {
        if (!roleRepository.existsByRoleName(roleName)) {
            roleRepository.save(new Role(roleName));
            log.info("Initialized role: {}", roleName);
        }
    }

    private void initializeApiKeys() {
        seedApiKeyIfNotExists("CANDIDATE_SERVICE", "cand_sec_key_" + UUID.randomUUID().toString().replace("-", ""));
        seedApiKeyIfNotExists("COMPANY_SERVICE", "comp_sec_key_" + UUID.randomUUID().toString().replace("-", ""));
        seedApiKeyIfNotExists("JOB_SERVICE", "job_sec_key_" + UUID.randomUUID().toString().replace("-", ""));
        seedApiKeyIfNotExists("AI_SERVICE", "ai_sec_key_" + UUID.randomUUID().toString().replace("-", ""));
    }

    private void seedApiKeyIfNotExists(String serviceName, String defaultKey) {
        if (apiKeyRepository.findByServiceNameAndActiveTrue(serviceName).isEmpty()) {
            ApiKey apiKey = new ApiKey();
            apiKey.setServiceName(serviceName);
            apiKey.setKeyValue(defaultKey);
            apiKey.setActive(true);
            apiKey.setCreatedAt(Instant.now());

            apiKeyRepository.save(apiKey);
            log.info("Initialized API Key for service {}: {}", serviceName, defaultKey);
        }
    }
}
