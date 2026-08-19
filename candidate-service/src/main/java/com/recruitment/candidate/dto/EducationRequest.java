package com.recruitment.candidate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @Size(min = 2, max = 150, message = "Institution name must be between 2 and 150 characters")
    @Schema(description = "Name of university or school", example = "University of California, Berkeley", requiredMode = Schema.RequiredMode.REQUIRED)
    private String institution;

    @NotBlank(message = "Degree is required")
    @Size(min = 2, max = 100, message = "Degree must be between 2 and 100 characters")
    @Schema(description = "Degree obtained", example = "Bachelor of Science", requiredMode = Schema.RequiredMode.REQUIRED)
    private String degree;

    @Size(max = 100, message = "Field of study must not exceed 100 characters")
    @Schema(description = "Field of study", example = "Computer Science")
    private String fieldOfStudy;

    @Schema(description = "Start date", example = "2018-09-01")
    private LocalDate startDate;

    @Schema(description = "End date or expected graduation", example = "2022-06-01")
    private LocalDate endDate;
}
