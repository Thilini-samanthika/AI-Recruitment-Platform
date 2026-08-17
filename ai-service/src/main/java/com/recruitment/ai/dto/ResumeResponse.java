package com.recruitment.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resume Metadata & Content Details")
public class ResumeResponse {

    @Schema(description = "Resume ID", example = "1")
    private Long id;

    @Schema(description = "Candidate ID", example = "101")
    private Long candidateId;

    @Schema(description = "File name", example = "john_doe_resume.pdf")
    private String fileName;

    @Schema(description = "File MIME type", example = "application/pdf")
    private String fileType;

    @Schema(description = "File size in bytes", example = "1048576")
    private Long fileSize;

    @Schema(description = "Internal stored file path", example = "uploads/resumes/101_resume.pdf")
    private String filePath;

    @Schema(description = "Extracted raw resume text")
    private String extractedText;

    @Schema(description = "Extracted skills from resume", example = "[\"Java\", \"Spring Boot\", \"MySQL\", \"React\"]")
    private List<String> extractedSkills;

    @Schema(description = "Resume processing status", example = "PARSED")
    private String status;

    @Schema(description = "Uploaded timestamp")
    private Instant uploadedAt;

    @Schema(description = "Last updated timestamp")
    private Instant updatedAt;
}
