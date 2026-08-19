package com.recruitment.company.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationDetails {

    @Builder.Default
    private VerificationStatus status = VerificationStatus.UNVERIFIED;

    private String taxId;

    private String businessRegistrationNumber;

    private String documentUrl;

    private String reviewNotes;

    private LocalDateTime submittedAt;

    private LocalDateTime reviewedAt;

    private String reviewedBy;
}
