package com.recruitment.job.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    private String id;

    @Indexed
    private Long companyId;

    private String title;

    private String description;

    private String requiredSkills;

    @Indexed
    private String location;

    private String salaryRange;

    @Indexed
    private String jobType;

    @Indexed
    @Builder.Default
    private JobStatus status = JobStatus.OPEN;

    private LocalDateTime deadline;

    @CreatedDate
    private LocalDateTime postedAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
