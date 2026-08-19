package com.recruitment.company.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload to update department details")
public class UpdateDepartmentRequest {

    @Size(min = 2, max = 100, message = "Department name must be between 2 and 100 characters")
    @Schema(description = "Department name", example = "Core Engineering & AI")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Schema(description = "Department mission and scope", example = "Core platform engineering, ML models, and infrastructure.")
    private String description;

    @Size(max = 100, message = "Head of department name cannot exceed 100 characters")
    @Schema(description = "Name of department director or lead", example = "Sarah Jenkins-Ross")
    private String headOfDepartment;
}
