package com.recruitment.company.dto;

import com.recruitment.company.entity.RecruiterStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
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
@Schema(description = "Request payload to update recruiter details")
public class UpdateRecruiterRequest {

    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    @Schema(description = "Recruiter full name", example = "Jane Doe-Smith")
    private String fullName;

    @Email(message = "Invalid email format")
    @Schema(description = "Recruiter corporate email", example = "jane.smith@acmetech.io")
    private String email;

    @Pattern(regexp = "^$|^\\+?[0-9\\s()\\-\\.]+$", message = "Invalid phone number format")
    @Schema(description = "Recruiter contact phone number", example = "+1 (555) 345-9999")
    private String phone;

    @Size(max = 100, message = "Title cannot exceed 100 characters")
    @Schema(description = "Job title", example = "Head of Talent Acquisition")
    private String title;

    @Schema(description = "Department ID assignment", example = "66c25a1f2b3e8c0012345679")
    private String departmentId;

    @Schema(description = "Recruiter status", example = "ACTIVE")
    private RecruiterStatus status;
}
