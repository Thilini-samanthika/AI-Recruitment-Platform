package com.recruitment.job.repository;

import com.recruitment.job.entity.Job;
import com.recruitment.job.entity.JobStatus;

import java.util.List;

public interface JobRepositoryCustom {

    List<Job> searchJobs(String keyword, String location, String jobType, JobStatus status);
}
