package com.recruitment.candidate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for adding an education record")
public class EducationRequest {

    @NotBlank(message = "Institution name is required")
    @Schema(description = "Name of university or school", example = "University of California, Berkeley", requiredMode = Schema.RequiredMode.REQUIRED)
    private String institution;

    @NotBlank(message = "Degree is required")
    @Schema(description = "Degree obtained", example = "Bachelor of Science", requiredMode = Schema.RequiredMode.REQUIRED)
    private String degree;

    @Schema(description = "Field of study", example = "Computer Science")
    private String fieldOfStudy;

    @Schema(description = "Start date", example = "2018-09-01")
    private LocalDate startDate;

    @Schema(description = "End date or expected graduation", example = "2022-06-01")
    private LocalDate endDate;
}
