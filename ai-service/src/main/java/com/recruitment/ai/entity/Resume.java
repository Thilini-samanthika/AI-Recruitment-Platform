package com.recruitment.ai.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "resumes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resume {

    @Id
    private String id;

    @Indexed
    private Long candidateId;

    private String fileName;

    private String fileType;

    private Long fileSize;

    private String filePath;

    private String extractedText;

    @Builder.Default
    private List<String> extractedSkills = new ArrayList<>();

    @Builder.Default
    private String status = "UPLOADED";

    @CreatedDate
    private Instant uploadedAt;

    @LastModifiedDate
    private Instant updatedAt;
}
