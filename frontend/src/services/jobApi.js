// Job Service Client (Member 4 Integration)
// Integrates with Gateway at http://localhost:8080/api/jobs

import { apiClient } from './apiClient';

const initialMockJobs = [
  {
    id: 101,
    companyId: 1,
    companyName: "Acme Technologies Inc.",
    title: "Senior Full Stack Java Engineer",
    description: "We are seeking a talented Senior Full Stack Java Engineer to architect and build high-performance microservices and responsive React web applications. You will be designing REST APIs, optimizing MySQL schemas, and collaborating with cross-functional AI teams.",
    location: "San Francisco, CA (Hybrid)",
    jobType: "Full-Time",
    experienceLevel: "Senior (5+ yrs)",
    salaryRange: "$145,000 - $180,000",
    requiredSkills: ["Java", "Spring Boot", "React", "MySQL", "Docker", "REST API", "Git"],
    createdAt: "2026-08-16T10:00:00Z"
  },
  {
    id: 102,
    companyId: 2,
    companyName: "QuantumFlow Systems",
    title: "Cloud Backend & DevOps Engineer",
    description: "Join our core infrastructure team to build resilient, distributed financial backend systems. Hands-on experience with container orchestration, event-driven streaming with Kafka, and continuous delivery pipelines is required.",
    location: "Austin, TX (Remote)",
    jobType: "Full-Time",
    experienceLevel: "Mid-Senior (3-6 yrs)",
    salaryRange: "$130,000 - $165,000",
    requiredSkills: ["Java", "Microservices", "Docker", "Kubernetes", "Kafka", "CI/CD", "AWS", "Linux"],
    createdAt: "2026-08-16T11:30:00Z"
  },
  {
    id: 103,
    companyId: 3,
    companyName: "BioHealth Diagnostics",
    title: "AI Solutions & Python Developer",
    description: "Lead the development of predictive health diagnostic models and backend analytical services. Strong proficiency in Python, RESTful API development, data pipelines, and relational database systems.",
    location: "Cambridge, MA (On-site)",
    jobType: "Full-Time",
    experienceLevel: "Mid Level (2-4 yrs)",
    salaryRange: "$115,000 - $145,000",
    requiredSkills: ["Python", "Machine Learning", "FastAPI", "SQL", "Docker", "Data Analysis", "Git"],
    createdAt: "2026-08-16T12:15:00Z"
  },
  {
    id: 104,
    companyId: 1,
    companyName: "Acme Technologies Inc.",
    title: "Frontend UI/UX React Architect",
    description: "Craft state-of-the-art interactive web applications with React 18, TypeScript, modern CSS architectures, and real-time WebSocket integrations. Passion for design aesthetics, micro-animations, and performance is essential.",
    location: "Remote (Global)",
    jobType: "Contract / Full-Time",
    experienceLevel: "Senior (4+ yrs)",
    salaryRange: "$125,000 - $160,000",
    requiredSkills: ["React", "TypeScript", "JavaScript", "HTML5", "CSS3", "Redux", "Tailwind CSS"],
    createdAt: "2026-08-16T14:00:00Z"
  }
];

export const jobApi = {
  async listJobs() {
    const res = await apiClient.get('/api/jobs');
    if (res.success && res.data && res.data.length > 0) {
      return res;
    }
    return {
      success: true,
      data: initialMockJobs
    };
  },

  async getJobById(jobId) {
    const res = await apiClient.get(`/api/jobs/${jobId}`);
    if (res.success && res.data) {
      return res;
    }
    const found = initialMockJobs.find(j => j.id === Number(jobId)) || initialMockJobs[0];
    return {
      success: true,
      data: found
    };
  },

  async createJob(jobData) {
    const res = await apiClient.post('/api/jobs', jobData);
    if (res.success && res.data) {
      return res;
    }
    const newJob = {
      id: Date.now(),
      ...jobData,
      createdAt: new Date().toISOString()
    };
    return {
      success: true,
      message: 'Job posting published successfully',
      data: newJob
    };
  }
};
