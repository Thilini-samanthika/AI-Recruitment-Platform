package com.recruitment.job.repository;

import com.recruitment.job.entity.Job;
import com.recruitment.job.entity.JobStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Repository
@RequiredArgsConstructor
public class JobRepositoryCustomImpl implements JobRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Job> searchJobs(String keyword, String location, String jobType, JobStatus status) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            String sanitizedKeyword = Pattern.quote(keyword.trim());
            Criteria keywordCriteria = new Criteria().orOperator(
                    Criteria.where("title").regex(sanitizedKeyword, "i"),
                    Criteria.where("description").regex(sanitizedKeyword, "i"),
                    Criteria.where("requiredSkills").regex(sanitizedKeyword, "i")
            );
            criteriaList.add(keywordCriteria);
        }

        if (location != null && !location.trim().isEmpty()) {
            String sanitizedLocation = Pattern.quote(location.trim());
            criteriaList.add(Criteria.where("location").regex(sanitizedLocation, "i"));
        }

        if (jobType != null && !jobType.trim().isEmpty()) {
            String sanitizedJobType = Pattern.quote(jobType.trim());
            criteriaList.add(Criteria.where("jobType").regex("^" + sanitizedJobType + "$", "i"));
        }

        if (status != null) {
            criteriaList.add(Criteria.where("status").is(status));
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        return mongoTemplate.find(query, Job.class);
    }
}
