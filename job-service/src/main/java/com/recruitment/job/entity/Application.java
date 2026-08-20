package com.recruitment.job.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "applications")
@CompoundIndex(name = "idx_job_candidate_unique", def = "{'jobId': 1, 'candidateId': 1}", unique = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    private String id;

    @Indexed
    private String jobId;

    private String jobTitle;

    private Long companyId;

    @Indexed
    private Long candidateId;

    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    private String notes;

    private String resumeUrl;

    @CreatedDate
    private LocalDateTime appliedAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
