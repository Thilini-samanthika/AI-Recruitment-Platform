package com.recruitment.company.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "recruiters")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recruiter {

    @Id
    private String id;

    @Indexed
    private String companyId;

    private String departmentId;

    private Long userId;

    private String fullName;

    @Indexed
    private String email;

    private String phone;

    private String title;

    @Builder.Default
    private RecruiterStatus status = RecruiterStatus.ACTIVE;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
