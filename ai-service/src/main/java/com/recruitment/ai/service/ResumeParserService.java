package com.recruitment.ai.service;

import com.recruitment.ai.exception.FileProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ResumeParserService {

    // Comprehensive dictionary of technical and domain skills
    private static final List<String> SKILL_DICTIONARY = List.of(
            // Languages
            "Java", "Python", "JavaScript", "TypeScript", "C#", "C++", "Golang", "Go", "Rust", "PHP",
            "Ruby", "Kotlin", "Swift", "Dart", "Scala", "R", "SQL", "HTML5", "CSS3", "Bash", "Shell",

            // Frameworks & Libraries
            "Spring Boot", "Spring Cloud", "Spring Security", "Hibernate", "JPA", "Node.js", "Express.js",
            "Nest.js", "React", "React.js", "Next.js", "Vue.js", "Angular", "Django", "FastAPI", "Flask",
            "ASP.NET", ".NET Core", "Laravel", "Ruby on Rails", "Tailwind CSS", "Bootstrap", "Redux",

            // Databases & Caching
            "MySQL", "PostgreSQL", "MongoDB", "Redis", "Elasticsearch", "Cassandra", "Oracle",
            "SQL Server", "DynamoDB", "Firebase", "Neo4j", "MariaDB", "SQLite",

            // Cloud & DevOps & Infra
            "AWS", "Amazon Web Services", "Azure", "Google Cloud", "GCP", "Docker", "Kubernetes",
            "K8s", "Terraform", "Ansible", "Jenkins", "GitHub Actions", "GitLab CI", "CI/CD",
            "Prometheus", "Grafana", "Nginx", "Linux", "Microservices", "Kafka", "RabbitMQ",

            // AI / ML / Data
            "Machine Learning", "Deep Learning", "TensorFlow", "PyTorch", "NLP", "Computer Vision",
            "Pandas", "NumPy", "Scikit-Learn", "OpenCV", "Large Language Models", "LLM", "Prompt Engineering",
            "Data Analysis", "Data Science", "Tableau", "Power BI", "Spark", "Hadoop",

            // Architecture & Concepts
            "REST API", "GraphQL", "gRPC", "WebSocket", "OAuth2", "JWT", "Design Patterns",
            "System Design", "Agile", "Scrum", "TDD", "Unit Testing", "JUnit", "Mockito", "Jest",
            "Cypress", "Selenium", "Clean Architecture", "Event-Driven Architecture",

            // Soft Skills & Methodologies
            "Team Leadership", "Project Management", "Problem Solving", "Code Review", "Mentorship"
    );

    // Map of normalized skill names to avoid duplicates (e.g., "React.js" -> "React")
    private static final Map<String, String> SKILL_NORMALIZATION = Map.ofEntries(
            Map.entry("react.js", "React"),
            Map.entry("react", "React"),
            Map.entry("spring boot", "Spring Boot"),
            Map.entry("springboot", "Spring Boot"),
            Map.entry("node.js", "Node.js"),
            Map.entry("nodejs", "Node.js"),
            Map.entry("express.js", "Express.js"),
            Map.entry("k8s", "Kubernetes"),
            Map.entry("amazon web services", "AWS"),
            Map.entry("google cloud", "GCP"),
            Map.entry(".net core", ".NET Core"),
            Map.entry("c#", "C#"),
            Map.entry("c++", "C++")
    );

    /**
     * Extracts text content from uploaded multipart file based on file extension
     */
    public String extractText(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            throw new FileProcessingException("Invalid file name");
        }

        String lowerName = filename.toLowerCase();
        try (InputStream is = file.getInputStream()) {
            if (lowerName.endsWith(".pdf")) {
                return extractTextFromPdf(is);
            } else if (lowerName.endsWith(".docx")) {
                return extractTextFromDocx(is);
            } else if (lowerName.endsWith(".txt") || lowerName.endsWith(".md")) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            } else {
                throw new FileProcessingException("Unsupported file type. Supported formats: .pdf, .docx, .txt");
            }
        } catch (Exception e) {
            log.error("Failed to extract text from file {}: {}", filename, e.getMessage(), e);
            throw new FileProcessingException("Error parsing resume file: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts text content from a stored file on disk
     */
    public String extractTextFromDisk(File file) {
        String lowerName = file.getName().toLowerCase();
        try (InputStream is = new FileInputStream(file)) {
            if (lowerName.endsWith(".pdf")) {
                return extractTextFromPdf(is);
            } else if (lowerName.endsWith(".docx")) {
                return extractTextFromDocx(is);
            } else if (lowerName.endsWith(".txt") || lowerName.endsWith(".md")) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            } else {
                throw new FileProcessingException("Unsupported file type: " + file.getName());
            }
        } catch (Exception e) {
            log.error("Failed to read stored resume file {}: {}", file.getPath(), e.getMessage(), e);
            throw new FileProcessingException("Error reading stored resume: " + e.getMessage(), e);
        }
    }

    /**
     * Extract plain text from PDF stream using PDFBox 3.x Loader
     */
    private String extractTextFromPdf(InputStream inputStream) throws Exception {
        byte[] pdfBytes = inputStream.readAllBytes();
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            if (document.isEncrypted()) {
                throw new FileProcessingException("Cannot extract text from password-encrypted PDF");
            }
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(document).trim();
        }
    }

    /**
     * Extract plain text from DOCX stream using Apache POI
     */
    private String extractTextFromDocx(InputStream inputStream) throws Exception {
        try (XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText().trim();
        }
    }

    /**
     * Extracts recognized skills from text content using regex word boundaries
     */
    public List<String> extractSkills(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        Set<String> matchedSkills = new LinkedHashSet<>();
        String normalizedText = text.toLowerCase();

        for (String skill : SKILL_DICTIONARY) {
            String lowerSkill = skill.toLowerCase();

            // Special cases for single chars or special symbols like C++, C#, .NET
            Pattern pattern;
            if (skill.equals("C++")) {
                pattern = Pattern.compile("(?i)(?<![a-zA-Z0-9])c\\+\\+(?![a-zA-Z0-9])");
            } else if (skill.equals("C#")) {
                pattern = Pattern.compile("(?i)(?<![a-zA-Z0-9])c#(?![a-zA-Z0-9])");
            } else if (skill.equals(".NET Core") || skill.equals("ASP.NET")) {
                pattern = Pattern.compile("(?i)\\b" + Pattern.quote(skill) + "\\b");
            } else if (skill.equalsIgnoreCase("Go")) {
                pattern = Pattern.compile("(?i)\\b(golang|go language|go)\\b");
            } else if (skill.equalsIgnoreCase("R")) {
                pattern = Pattern.compile("(?i)\\b(r language|r programming)\\b");
            } else {
                pattern = Pattern.compile("(?i)\\b" + Pattern.quote(lowerSkill) + "\\b");
            }

            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String canonical = SKILL_NORMALIZATION.getOrDefault(lowerSkill, skill);
                matchedSkills.add(canonical);
            }
        }

        return new ArrayList<>(matchedSkills);
    }
}
