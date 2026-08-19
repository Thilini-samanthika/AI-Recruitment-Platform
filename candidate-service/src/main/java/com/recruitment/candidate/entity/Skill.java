package com.recruitment.candidate.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {

    private String id;
    private String skillName;
    private String proficiencyLevel; // e.g. BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
}
