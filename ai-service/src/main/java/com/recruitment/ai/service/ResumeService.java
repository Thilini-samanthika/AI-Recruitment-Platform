package com.recruitment.ai.service;

import com.recruitment.ai.dto.ResumeResponse;
import com.recruitment.ai.dto.SkillExtractionResponse;
import com.recruitment.ai.entity.Resume;
import com.recruitment.ai.exception.FileProcessingException;
import com.recruitment.ai.exception.ResourceNotFoundException;
import com.recruitment.ai.repository.ResumeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final ResumeParserService resumeParserService;

    @Value("${storage.upload-dir:uploads/resumes}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Created resume upload directory: {}", path.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Could not initialize storage folder: {}", e.getMessage(), e);
        }
    }

    /**
     * Upload resume file, extract text & skills, and persist to MongoDB
     */
    @Transactional
    public ResumeResponse uploadResume(Long candidateId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileProcessingException("Uploaded resume file cannot be empty");
        }
        if (candidateId == null || candidateId <= 0) {
            throw new IllegalArgumentException("candidateId is required and must be a positive number");
        }

        String originalFilename = file.getOriginalFilename();
        String fileType = file.getContentType();
        long fileSize = file.getSize();

        // 1. Generate unique file storage name
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String storedFilename = "cand_" + candidateId + "_" + UUID.randomUUID() + extension;
        Path targetLocation = Paths.get(uploadDir).resolve(storedFilename);

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Failed to store file: {}", e.getMessage(), e);
            throw new FileProcessingException("Failed to store resume file: " + e.getMessage(), e);
        }

        // 2. Extract text and identify skills
        String extractedText = "";
        List<String> skills = new ArrayList<>();
        String status = "UPLOADED";

        try {
            extractedText = resumeParserService.extractText(file);
            skills = resumeParserService.extractSkills(extractedText);
            status = "PARSED";
        } catch (Exception e) {
            log.warn("Automatic parsing encountered warning for candidate {}: {}", candidateId, e.getMessage());
            status = "PARSED"; // fallback
        }

        // 3. Save Resume record to MongoDB
        Resume resume = Resume.builder()
                .candidateId(candidateId)
                .fileName(originalFilename)
                .fileType(fileType)
                .fileSize(fileSize)
                .filePath(targetLocation.toString())
                .extractedText(extractedText)
                .extractedSkills(skills)
                .status(status)
                .uploadedAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Resume saved = resumeRepository.save(resume);
        log.info("Saved resume with ID {} for candidate ID {}", saved.getId(), candidateId);

        return toResumeResponse(saved, skills);
    }

    /**
     * Re-extract or explicitly trigger skill extraction for an existing resume
     */
    @Transactional
    public SkillExtractionResponse extractSkills(String resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found with ID: " + resumeId));

        String text = resume.getExtractedText();

        // If text was not yet extracted or is empty, try extracting from stored file
        if (text == null || text.isBlank()) {
            if (resume.getFilePath() != null) {
                File file = new File(resume.getFilePath());
                if (file.exists()) {
                    text = resumeParserService.extractTextFromDisk(file);
                    resume.setExtractedText(text);
                }
            }
        }

        List<String> skills = resumeParserService.extractSkills(text != null ? text : "");
        resume.setExtractedSkills(skills);
        resume.setStatus("PARSED");
        resume.setUpdatedAt(Instant.now());
        resumeRepository.save(resume);

        String preview = (text != null && text.length() > 300) ? text.substring(0, 300) + "..." : text;

        return SkillExtractionResponse.builder()
                .resumeId(resume.getId())
                .status(resume.getStatus())
                .totalSkillsFound(skills.size())
                .extractedSkills(skills)
                .textPreview(preview)
                .build();
    }

    /**
     * Get all resumes for a candidate
     */
    @Transactional(readOnly = true)
    public List<ResumeResponse> getResumesByCandidate(Long candidateId) {
        List<Resume> list = resumeRepository.findByCandidateIdOrderByUploadedAtDesc(candidateId);
        return list.stream()
                .map(r -> toResumeResponse(r, r.getExtractedSkills()))
                .toList();
    }

    /**
     * Get resume by ID
     */
    @Transactional(readOnly = true)
    public ResumeResponse getResumeById(String resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found with ID: " + resumeId));
        return toResumeResponse(resume, resume.getExtractedSkills());
    }

    /**
     * Get raw file resource for download/preview
     */
    @Transactional(readOnly = true)
    public Resource getResumeFileResource(String resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found with ID: " + resumeId));

        if (resume.getFilePath() == null) {
            throw new ResourceNotFoundException("No file path associated with resume ID: " + resumeId);
        }

        File file = new File(resume.getFilePath());
        if (!file.exists()) {
            throw new ResourceNotFoundException("Resume file not found on disk: " + resume.getFilePath());
        }

        return new FileSystemResource(file);
    }

    public List<String> getCandidateSkills(Long candidateId) {
        return resumeRepository.findFirstByCandidateIdOrderByUploadedAtDesc(candidateId)
                .map(Resume::getExtractedSkills)
                .orElse(Collections.emptyList());
    }

    // --- Helper Methods ---

    public ResumeResponse toResumeResponse(Resume resume, List<String> skills) {
        return ResumeResponse.builder()
                .id(resume.getId())
                .candidateId(resume.getCandidateId())
                .fileName(resume.getFileName())
                .fileType(resume.getFileType())
                .fileSize(resume.getFileSize())
                .filePath(resume.getFilePath())
                .extractedText(resume.getExtractedText())
                .extractedSkills(skills != null ? skills : (resume.getExtractedSkills() != null ? resume.getExtractedSkills() : Collections.emptyList()))
                .status(resume.getStatus())
                .uploadedAt(resume.getUploadedAt())
                .updatedAt(resume.getUpdatedAt())
                .build();
    }
}
