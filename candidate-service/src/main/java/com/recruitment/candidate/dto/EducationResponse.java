package com.recruitment.candidate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Education record details response")
public class EducationResponse {

    @Schema(description = "Education record ID", example = "1")
    private Long id;

    @Schema(description = "Candidate ID", example = "10")
    private Long candidateId;

    @Schema(description = "Institution name", example = "University of California, Berkeley")
    private String institution;

    @Schema(description = "Degree", example = "Bachelor of Science")
    private String degree;

    @Schema(description = "Field of study", example = "Computer Science")
    private String fieldOfStudy;

    @Schema(description = "Start date", example = "2018-09-01")
    private LocalDate startDate;

    @Schema(description = "End date", example = "2022-06-01")
    private LocalDate endDate;
}
