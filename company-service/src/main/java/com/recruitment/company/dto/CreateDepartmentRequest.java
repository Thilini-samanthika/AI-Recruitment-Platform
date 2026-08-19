package com.recruitment.company.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload to create a new department within a company")
public class CreateDepartmentRequest {

    @NotBlank(message = "Department name is required")
    @Size(min = 2, max = 100, message = "Department name must be between 2 and 100 characters")
    @Schema(description = "Department name", example = "Engineering", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Schema(description = "Department mission and operational scope", example = "Software engineering, infrastructure, and AI research teams.")
    private String description;

    @Size(max = 100, message = "Head of department name cannot exceed 100 characters")
    @Schema(description = "Name of department director or lead", example = "Sarah Jenkins")
    private String headOfDepartment;
}
