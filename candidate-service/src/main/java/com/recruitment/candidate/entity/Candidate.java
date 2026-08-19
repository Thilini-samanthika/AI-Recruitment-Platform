package com.recruitment.candidate.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "candidates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {

    @Id
    private String id;

    @Indexed(unique = true)
    private Long userId;

    private String fullName;

    private String phone;

    private String address;

    private String headline;

    private String summary;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder.Default
    private List<Skill> skills = new ArrayList<>();

    @Builder.Default
    private List<Education> educations = new ArrayList<>();

    @Builder.Default
    private List<Experience> experiences = new ArrayList<>();

    // Helper methods for embedded subdocuments
    public void addSkill(Skill skill) {
        if (this.skills == null) {
            this.skills = new ArrayList<>();
        }
        this.skills.add(skill);
    }

    public boolean removeSkill(String skillId) {
        if (this.skills != null && skillId != null) {
            return this.skills.removeIf(s -> skillId.equals(s.getId()));
        }
        return false;
    }

    public void addEducation(Education education) {
        if (this.educations == null) {
            this.educations = new ArrayList<>();
        }
        this.educations.add(education);
    }

    public boolean removeEducation(String educationId) {
        if (this.educations != null && educationId != null) {
            return this.educations.removeIf(e -> educationId.equals(e.getId()));
        }
        return false;
    }

    public void addExperience(Experience experience) {
        if (this.experiences == null) {
            this.experiences = new ArrayList<>();
        }
        this.experiences.add(experience);
    }

    public boolean removeExperience(String experienceId) {
        if (this.experiences != null && experienceId != null) {
            return this.experiences.removeIf(exp -> experienceId.equals(exp.getId()));
        }
        return false;
    }
}
