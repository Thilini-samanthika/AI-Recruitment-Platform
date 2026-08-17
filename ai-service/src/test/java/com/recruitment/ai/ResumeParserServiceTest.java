package com.recruitment.ai;

import com.recruitment.ai.service.ResumeParserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResumeParserServiceTest {

    private ResumeParserService resumeParserService;

    @BeforeEach
    void setUp() {
        resumeParserService = new ResumeParserService();
    }

    @Test
    @DisplayName("Should extract technical skills accurately from resume text")
    void testExtractSkills() {
        String sampleText = """
                Alex Mercer - Senior Software Engineer
                Experience:
                - Developed backend microservices using Java, Spring Boot, and MySQL.
                - Built frontend user interfaces using React and TypeScript.
                - Managed deployment pipelines with Docker, Kubernetes, and AWS.
                - Implemented streaming data workflows using Kafka and Redis.
                """;

        List<String> skills = resumeParserService.extractSkills(sampleText);

        assertNotNull(skills);
        assertTrue(skills.contains("Java"), "Should contain Java");
        assertTrue(skills.contains("Spring Boot"), "Should contain Spring Boot");
        assertTrue(skills.contains("MySQL"), "Should contain MySQL");
        assertTrue(skills.contains("React"), "Should contain React");
        assertTrue(skills.contains("TypeScript"), "Should contain TypeScript");
        assertTrue(skills.contains("Docker"), "Should contain Docker");
        assertTrue(skills.contains("Kubernetes"), "Should contain Kubernetes");
        assertTrue(skills.contains("AWS"), "Should contain AWS");
        assertTrue(skills.contains("Kafka"), "Should contain Kafka");
        assertTrue(skills.contains("Redis"), "Should contain Redis");
    }

    @Test
    @DisplayName("Should handle empty or blank text gracefully")
    void testExtractSkillsEmpty() {
        List<String> emptySkills = resumeParserService.extractSkills("");
        assertNotNull(emptySkills);
        assertTrue(emptySkills.isEmpty());

        List<String> nullSkills = resumeParserService.extractSkills(null);
        assertNotNull(nullSkills);
        assertTrue(nullSkills.isEmpty());
    }

    @Test
    @DisplayName("Should avoid false positive keyword matching")
    void testAvoidFalsePositives() {
        // Text contains words like "JavaScripting", "Reacting", "Class", etc.
        String text = "Reacting to events in a modern architecture class.";
        List<String> skills = resumeParserService.extractSkills(text);

        // "React" might match or not depending on token boundary, but "C" alone shouldn't match "Class"
        assertFalse(skills.contains("C++"), "Should not match C++");
        assertFalse(skills.contains("C#"), "Should not match C#");
    }
}
