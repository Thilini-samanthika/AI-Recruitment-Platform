package com.recruitment.company.dto;

import com.recruitment.company.entity.Recruiter;
import com.recruitment.company.entity.RecruiterStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Recruiter details response")
public class RecruiterResponse {

    @Schema(description = "Recruiter unique ID", example = "66c25a1f2b3e8c0012345680")
    private String id;

    @Schema(description = "Associated company ID", example = "66c25a1f2b3e8c0012345678")
    private String companyId;

    @Schema(description = "Associated department ID", example = "66c25a1f2b3e8c0012345679")
    private String departmentId;

    @Schema(description = "Auth User ID", example = "105")
    private Long userId;

    @Schema(description = "Recruiter full name", example = "Jane Doe")
    private String fullName;

    @Schema(description = "Recruiter email", example = "jane.doe@acmetech.io")
    private String email;

    @Schema(description = "Recruiter phone number", example = "+1 (555) 345-6789")
    private String phone;

    @Schema(description = "Job title", example = "Senior Technical Recruiter")
    private String title;

    @Schema(description = "Recruiter status (ACTIVE/INACTIVE)", example = "ACTIVE")
    private RecruiterStatus status;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    public static RecruiterResponse fromEntity(Recruiter recruiter) {
        if (recruiter == null) {
            return null;
        }
        return RecruiterResponse.builder()
                .id(recruiter.getId())
                .companyId(recruiter.getCompanyId())
                .departmentId(recruiter.getDepartmentId())
                .userId(recruiter.getUserId())
                .fullName(recruiter.getFullName())
                .email(recruiter.getEmail())
                .phone(recruiter.getPhone())
                .title(recruiter.getTitle())
                .status(recruiter.getStatus())
                .createdAt(recruiter.getCreatedAt())
                .updatedAt(recruiter.getUpdatedAt())
                .build();
    }
}
