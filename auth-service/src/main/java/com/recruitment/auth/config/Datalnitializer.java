package com.recruitment.auth.config;

import com.recruitment.auth.entity.ApiKey;
import com.recruitment.auth.entity.Role;
import com.recruitment.auth.repository.ApiKeyRepository;
import com.recruitment.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final ApiKeyRepository apiKeyRepository;

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
            ApiKey apiKey = ApiKey.builder()
                    .serviceName(serviceName)
                    .keyValue(defaultKey)
                    .active(true)
                    .build();
            apiKeyRepository.save(apiKey);
            log.info("Initialized API Key for service {}: {}", serviceName, defaultKey);
        }
    }
}
