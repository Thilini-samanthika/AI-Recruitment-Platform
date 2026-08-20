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

@Document(collection = "recommendations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation {

    @Id
    private String id;

    @Indexed
    private Long candidateId;

    @Indexed
    private Long jobId;

    private Double score;

    private String jobTitle;

    private String companyName;

    @Builder.Default
    private List<String> matchedSkills = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;
}
