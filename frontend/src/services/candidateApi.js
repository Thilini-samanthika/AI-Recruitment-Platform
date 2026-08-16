// Frontend API Client for Candidate Service (Member 2)
// Integrates with API Gateway at http://localhost:8080/api/candidates

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';
const CANDIDATE_API_KEY = 'candidate-service-secret-key-12345';

// Mock fallback profile for instantaneous demonstration / offline previews
const initialMockCandidate = {
  id: 1,
  userId: 1,
  fullName: "Alice Johnson",
  phone: "+1 (555) 019-2834",
  address: "San Francisco, CA 94107",
  headline: "Senior Full Stack Engineer & Cloud Architect",
  summary: "Results-driven Software Engineer with 6+ years of experience designing high-throughput distributed microservices, reactive Spring Boot APIs, and scalable React web applications. Passionate about cloud-native infrastructure, clean code, and AI-driven workflows.",
  createdAt: "2026-08-16T10:00:00",
  updatedAt: "2026-08-16T16:30:00",
  skills: [
    { id: 101, candidateId: 1, skillName: "Java / Spring Boot", proficiencyLevel: "EXPERT" },
    { id: 102, candidateId: 1, skillName: "React.js & TypeScript", proficiencyLevel: "ADVANCED" },
    { id: 103, candidateId: 1, skillName: "Docker & Kubernetes", proficiencyLevel: "ADVANCED" },
    { id: 104, candidateId: 1, skillName: "MySQL & PostgreSQL", proficiencyLevel: "EXPERT" },
    { id: 105, candidateId: 1, skillName: "Microservices Architecture", proficiencyLevel: "EXPERT" },
    { id: 106, candidateId: 1, skillName: "Python & NLP", proficiencyLevel: "INTERMEDIATE" }
  ],
  educations: [
    {
      id: 201,
      candidateId: 1,
      institution: "University of California, Berkeley",
      degree: "Bachelor of Science",
      fieldOfStudy: "Computer Science & Engineering",
      startDate: "2016-09-01",
      endDate: "2020-05-15"
    }
  ],
  experiences: [
    {
      id: 301,
      candidateId: 1,
      companyName: "Stripe",
      jobTitle: "Senior Backend Engineer",
      startDate: "2022-06-01",
      endDate: null,
      description: "Designed core payment routing services handling 40k RPS. Led adoption of Spring Boot 3 & distributed tracing across 12 microservices."
    },
    {
      id: 302,
      candidateId: 1,
      companyName: "Twilio",
      jobTitle: "Software Engineer II",
      startDate: "2020-07-01",
      endDate: "2022-05-31",
      description: "Built scalable communications APIs in Java and RESTful gateways. Reduced p99 latency by 35% using caching and query optimization."
    }
  ]
};

// In-memory mock store if server is unreachable
let localStore = JSON.parse(localStorage.getItem('candidate_profile_data')) || initialMockCandidate;

function persistLocal(data) {
  localStore = data;
  localStorage.setItem('candidate_profile_data', JSON.stringify(data));
}

export const candidateApi = {
  // Get Candidate Profile by ID (or Me)
  async getProfile(candidateId = 1) {
    try {
      const res = await fetch(`${API_BASE_URL}/api/candidates/${candidateId}`, {
        headers: {
          'X-API-KEY': CANDIDATE_API_KEY,
          'X-User-Id': '1',
          'X-User-Role': 'ROLE_CANDIDATE',
        }
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = await res.json();
      return { success: true, data: data.data, source: 'API' };
    } catch (err) {
      console.warn('Backend unavailable, using responsive local candidate state:', err.message);
      return { success: true, data: localStore, source: 'LOCAL_SYNC' };
    }
  },

  // Update Candidate Profile
  async updateProfile(candidateId, updateData) {
    try {
      const res = await fetch(`${API_BASE_URL}/api/candidates/${candidateId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'X-API-KEY': CANDIDATE_API_KEY,
          'X-User-Id': '1',
          'X-User-Role': 'ROLE_CANDIDATE',
        },
        body: JSON.stringify(updateData)
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = await res.json();
      persistLocal(data.data);
      return { success: true, data: data.data };
    } catch (err) {
      const updated = { ...localStore, ...updateData, updatedAt: new Date().toISOString() };
      persistLocal(updated);
      return { success: true, data: updated };
    }
  },

  // Add Skill
  async addSkill(candidateId, skillData) {
    try {
      const res = await fetch(`${API_BASE_URL}/api/candidates/${candidateId}/skills`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-API-KEY': CANDIDATE_API_KEY,
          'X-User-Id': '1',
          'X-User-Role': 'ROLE_CANDIDATE',
        },
        body: JSON.stringify(skillData)
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = await res.json();
      const newSkill = data.data;
      const updated = { ...localStore, skills: [...localStore.skills, newSkill] };
      persistLocal(updated);
      return { success: true, data: newSkill };
    } catch (err) {
      const newSkill = { id: Date.now(), candidateId, ...skillData };
      const updated = { ...localStore, skills: [...localStore.skills, newSkill] };
      persistLocal(updated);
      return { success: true, data: newSkill };
    }
  },

  // Delete Skill
  async deleteSkill(candidateId, skillId) {
    try {
      const res = await fetch(`${API_BASE_URL}/api/candidates/${candidateId}/skills/${skillId}`, {
        method: 'DELETE',
        headers: {
          'X-API-KEY': CANDIDATE_API_KEY,
          'X-User-Id': '1',
          'X-User-Role': 'ROLE_CANDIDATE',
        }
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
    } catch (err) {
      console.warn('Deleting skill in local state');
    }
    const updated = { ...localStore, skills: localStore.skills.filter(s => s.id !== skillId) };
    persistLocal(updated);
    return { success: true };
  },

  // Add Education
  async addEducation(candidateId, eduData) {
    try {
      const res = await fetch(`${API_BASE_URL}/api/candidates/${candidateId}/education`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-API-KEY': CANDIDATE_API_KEY,
          'X-User-Id': '1',
          'X-User-Role': 'ROLE_CANDIDATE',
        },
        body: JSON.stringify(eduData)
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = await res.json();
      const newEdu = data.data;
      const updated = { ...localStore, educations: [...localStore.educations, newEdu] };
      persistLocal(updated);
      return { success: true, data: newEdu };
    } catch (err) {
      const newEdu = { id: Date.now(), candidateId, ...eduData };
      const updated = { ...localStore, educations: [...localStore.educations, newEdu] };
      persistLocal(updated);
      return { success: true, data: newEdu };
    }
  },

  // Delete Education
  async deleteEducation(candidateId, eduId) {
    try {
      const res = await fetch(`${API_BASE_URL}/api/candidates/${candidateId}/education/${eduId}`, {
        method: 'DELETE',
        headers: {
          'X-API-KEY': CANDIDATE_API_KEY,
          'X-User-Id': '1',
          'X-User-Role': 'ROLE_CANDIDATE',
        }
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
    } catch (err) {
      console.warn('Deleting education in local state');
    }
    const updated = { ...localStore, educations: localStore.educations.filter(e => e.id !== eduId) };
    persistLocal(updated);
    return { success: true };
  },

  // Add Experience
  async addExperience(candidateId, expData) {
    try {
      const res = await fetch(`${API_BASE_URL}/api/candidates/${candidateId}/experience`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-API-KEY': CANDIDATE_API_KEY,
          'X-User-Id': '1',
          'X-User-Role': 'ROLE_CANDIDATE',
        },
        body: JSON.stringify(expData)
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = await res.json();
      const newExp = data.data;
      const updated = { ...localStore, experiences: [...localStore.experiences, newExp] };
      persistLocal(updated);
      return { success: true, data: newExp };
    } catch (err) {
      const newExp = { id: Date.now(), candidateId, ...expData };
      const updated = { ...localStore, experiences: [...localStore.experiences, newExp] };
      persistLocal(updated);
      return { success: true, data: newExp };
    }
  },

  // Delete Experience
  async deleteExperience(candidateId, expId) {
    try {
      const res = await fetch(`${API_BASE_URL}/api/candidates/${candidateId}/experience/${expId}`, {
        method: 'DELETE',
        headers: {
          'X-API-KEY': CANDIDATE_API_KEY,
          'X-User-Id': '1',
          'X-User-Role': 'ROLE_CANDIDATE',
        }
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
    } catch (err) {
      console.warn('Deleting experience in local state');
    }
    const updated = { ...localStore, experiences: localStore.experiences.filter(e => e.id !== expId) };
    persistLocal(updated);
    return { success: true };
  },

  // List all candidates
  async listCandidates() {
    try {
      const res = await fetch(`${API_BASE_URL}/api/candidates`, {
        headers: {
          'X-API-KEY': CANDIDATE_API_KEY
        }
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = await res.json();
      return { success: true, data: data.data };
    } catch (err) {
      return { success: true, data: [localStore] };
    }
  }
};
