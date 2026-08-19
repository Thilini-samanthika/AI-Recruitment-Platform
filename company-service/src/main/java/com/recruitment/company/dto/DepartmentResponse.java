package com.recruitment.company.dto;

import com.recruitment.company.entity.Department;
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
@Schema(description = "Department details response")
public class DepartmentResponse {

    @Schema(description = "Department unique ID", example = "66c25a1f2b3e8c0012345679")
    private String id;

    @Schema(description = "Associated company ID", example = "66c25a1f2b3e8c0012345678")
    private String companyId;

    @Schema(description = "Department name", example = "Engineering")
    private String name;

    @Schema(description = "Department mission and operational scope", example = "Software engineering, infrastructure, and AI research teams.")
    private String description;

    @Schema(description = "Head of department name", example = "Sarah Jenkins")
    private String headOfDepartment;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    public static DepartmentResponse fromEntity(Department department) {
        if (department == null) {
            return null;
        }
        return DepartmentResponse.builder()
                .id(department.getId())
                .companyId(department.getCompanyId())
                .name(department.getName())
                .description(department.getDescription())
                .headOfDepartment(department.getHeadOfDepartment())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .build();
    }
}
