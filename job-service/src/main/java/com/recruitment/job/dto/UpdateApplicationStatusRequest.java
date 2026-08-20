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
@Schema(description = "Request payload to transition job application lifecycle status")
public class UpdateApplicationStatusRequest {

    @NotNull(message = "Status is required")
    @Schema(
            description = "Target application status. Valid values: APPLIED, REVIEWED, SHORTLISTED, INTERVIEW, OFFERED, ACCEPTED, REJECTED, WITHDRAWN. Must follow state machine transition rules.",
            example = "SHORTLISTED",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private ApplicationStatus status;
}
