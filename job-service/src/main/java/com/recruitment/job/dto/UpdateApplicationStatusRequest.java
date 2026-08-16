package com.recruitment.job.dto;

import com.recruitment.job.entity.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload to update job application status")
public class UpdateApplicationStatusRequest {

    @NotNull(message = "Status is required")
    @Schema(description = "New application status (APPLIED, SHORTLISTED, REJECTED, ACCEPTED)", example = "SHORTLISTED", requiredMode = Schema.RequiredMode.REQUIRED)
    private ApplicationStatus status;
}
