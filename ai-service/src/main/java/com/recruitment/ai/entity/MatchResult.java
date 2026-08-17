package com.recruitment.ai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "match_results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resume_id", nullable = false)
    private Long resumeId;

    @Column(name = "candidate_id")
    private Long candidateId;

    @Column(name = "job_id", nullable = false)
    private Long jobId;

    @Column(name = "match_percentage", nullable = false)
    private Double matchPercentage;

    @Lob
    @Column(name = "matched_skills", columnDefinition = "TEXT")
    private String matchedSkills;

    @Lob
    @Column(name = "missing_skills", columnDefinition = "TEXT")
    private String missingSkills;

    @Lob
    @Column(name = "analysis_summary", columnDefinition = "TEXT")
    private String analysisSummary;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
