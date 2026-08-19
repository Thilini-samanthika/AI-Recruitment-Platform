package com.recruitment.company.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload to add a new recruiter to a company")
public class CreateRecruiterRequest {

    @NotBlank(message = "Recruiter full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    @Schema(description = "Recruiter full name", example = "Jane Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fullName;

    @NotBlank(message = "Recruiter email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Recruiter corporate email", example = "jane.doe@acmetech.io", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Pattern(regexp = "^$|^\\+?[0-9\\s()\\-\\.]+$", message = "Invalid phone number format")
    @Schema(description = "Recruiter contact phone number", example = "+1 (555) 345-6789")
    private String phone;

    @Size(max = 100, message = "Title cannot exceed 100 characters")
    @Schema(description = "Job title / role within company", example = "Senior Technical Recruiter")
    private String title;

    @Schema(description = "Optional department ID assignment", example = "66c25a1f2b3e8c0012345679")
    private String departmentId;

    @Schema(description = "Optional Auth User ID if recruiter has an individual login", example = "105")
    private Long userId;
}
