package com.recruitment.ai.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "match_results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResult {

    @Id
    private String id;

    @Indexed
    private String resumeId;

    @Indexed
    private Long candidateId;

    @Indexed
    private Long jobId;

    private Double matchPercentage;

    @Builder.Default
    private List<String> matchedSkills = new ArrayList<>();

    @Builder.Default
    private List<String> missingSkills = new ArrayList<>();

    private String analysisSummary;

    @CreatedDate
    private Instant createdAt;
}
