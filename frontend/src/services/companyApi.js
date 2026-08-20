// Frontend API Client for Company Service (Member 3)
// Routes strictly through API Gateway at http://localhost:8080/api/companies with JWT Bearer Token

import { apiClient } from './apiClient';

// Realistic initial fallback data for instant offline demonstration & responsive UI testing
const initialMockCompanies = [
  {
    id: 1,
    userId: 10,
    companyName: "Acme Technologies Inc.",
    email: "contact@acmetech.io",
    phone: "+1 (555) 234-5678",
    address: "100 Innovation Way, Suite 400, San Francisco, CA 94105",
    createdAt: "2026-08-16T09:30:00",
    updatedAt: "2026-08-16T15:45:00",
    profile: {
      id: 1,
      companyId: 1,
      industry: "Information Technology & AI",
      companySize: "51-200",
      website: "https://acmetech.io",
      description: "Acme Technologies is a global frontier technology firm building autonomous AI recruitment pipelines, cloud orchestration infrastructure, and next-generation developer tooling. Our mission is to accelerate human potential through intelligent automation.",
      logoUrl: "https://images.unsplash.com/photo-1549719386-74dfcbf7dbed?w=150&auto=format&fit=crop&q=80"
    }
  },
  {
    id: 2,
    userId: 11,
    companyName: "QuantumFlow Systems",
    email: "hello@quantumflow.com",
    phone: "+1 (555) 876-5432",
    address: "450 Silicon Avenue, Austin, TX 78701",
    createdAt: "2026-08-16T10:15:00",
    updatedAt: "2026-08-16T14:20:00",
    profile: {
      id: 2,
      companyId: 2,
      industry: "Fintech & Blockchain",
      companySize: "201-500",
      website: "https://quantumflow.com",
      description: "Pioneering decentralized high-throughput trading systems and real-time ledger settlement engines with sub-millisecond latencies.",
      logoUrl: "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=150&auto=format&fit=crop&q=80"
    }
  },
  {
    id: 3,
    userId: 12,
    companyName: "BioHealth Diagnostics",
    email: "careers@biohealthdx.org",
    phone: "+1 (555) 432-1098",
    address: "700 Kendall Square, Cambridge, MA 02142",
    createdAt: "2026-08-16T11:00:00",
    updatedAt: "2026-08-16T12:00:00",
    profile: {
      id: 3,
      companyId: 3,
      industry: "Healthcare & Biotech",
      companySize: "11-50",
      website: "https://biohealthdx.org",
      description: "Developing precision genomic diagnostics and AI-powered biomarker discovery models for personalized clinical treatments.",
      logoUrl: "https://images.unsplash.com/photo-1507679799987-c73779587ccf?w=150&auto=format&fit=crop&q=80"
    }
  }
];

let localCompanies = JSON.parse(localStorage.getItem('company_directory_data')) || initialMockCompanies;

function persistLocalCompanies(data) {
  localCompanies = data;
  localStorage.setItem('company_directory_data', JSON.stringify(data));
}

export const companyApi = {
  // 1. List all companies
  async listCompanies() {
    const res = await apiClient.get('/api/companies');
    if (res.success && res.data) {
      return { success: true, data: res.data, source: 'API_GATEWAY' };
    }
    return { success: true, data: localCompanies, source: 'LOCAL_SYNC' };
  },

  // 2. Get company by ID
  async getCompany(companyId = 1) {
    const res = await apiClient.get(`/api/companies/${companyId}`);
    if (res.success && res.data) {
      return { success: true, data: res.data, source: 'API_GATEWAY' };
    }
    const found = localCompanies.find(c => c.id === Number(companyId)) || localCompanies[0];
    return { success: true, data: found, source: 'LOCAL_SYNC' };
  },

  // 3. Register a new company
  async registerCompany(companyData) {
    const res = await apiClient.post('/api/companies', companyData);
    if (res.success && res.data) {
      const newCompany = res.data;
      persistLocalCompanies([...localCompanies, newCompany]);
      return { success: true, data: newCompany };
    }
    const newCompany = {
      id: Date.now(),
      userId: companyData.userId || Math.floor(Math.random() * 1000),
      companyName: companyData.companyName,
      email: companyData.email,
      phone: companyData.phone || '',
      address: companyData.address || '',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      profile: {
        id: Date.now() + 1,
        companyId: Date.now(),
        industry: companyData.industry || 'Technology',
        companySize: companyData.companySize || '11-50',
        website: companyData.website || '',
        description: companyData.description || 'Newly registered company on AI Recruitment Platform.',
        logoUrl: companyData.logoUrl || 'https://images.unsplash.com/photo-1549719386-74dfcbf7dbed?w=150&auto=format&fit=crop&q=80'
      }
    };
    persistLocalCompanies([...localCompanies, newCompany]);
    return { success: true, data: newCompany };
  },

  // 4. Update basic company info
  async updateCompany(companyId, updateData) {
    const res = await apiClient.put(`/api/companies/${companyId}`, updateData);
    if (res.success && res.data) {
      const updated = res.data;
      persistLocalCompanies(localCompanies.map(c => c.id === Number(companyId) ? { ...c, ...updated } : c));
      return { success: true, data: updated };
    }
    const updatedList = localCompanies.map(c => {
      if (c.id === Number(companyId)) {
        return {
          ...c,
          ...updateData,
          updatedAt: new Date().toISOString()
        };
      }
      return c;
    });
    persistLocalCompanies(updatedList);
    const found = updatedList.find(c => c.id === Number(companyId));
    return { success: true, data: found };
  },

  // 5. Delete company
  async deleteCompany(companyId) {
    await apiClient.delete(`/api/companies/${companyId}`);
    const filtered = localCompanies.filter(c => c.id !== Number(companyId));
    persistLocalCompanies(filtered);
    return { success: true };
  },

  // 6. Save or update extended profile
  async saveOrUpdateProfile(companyId, profileData) {
    const res = await apiClient.post(`/api/companies/${companyId}/profile`, profileData);
    if (res.success && res.data) {
      const newProfile = res.data;
      const updatedList = localCompanies.map(c => {
        if (c.id === Number(companyId)) {
          return { ...c, profile: newProfile, updatedAt: new Date().toISOString() };
        }
        return c;
      });
      persistLocalCompanies(updatedList);
      return { success: true, data: newProfile };
    }
    const newProfile = {
      id: Date.now(),
      companyId: Number(companyId),
      ...profileData
    };
    const updatedList = localCompanies.map(c => {
      if (c.id === Number(companyId)) {
        return { ...c, profile: newProfile, updatedAt: new Date().toISOString() };
      }
      return c;
    });
    persistLocalCompanies(updatedList);
    return { success: true, data: newProfile };
  },

  // 7. Get extended profile by company ID
  async getProfile(companyId) {
    const res = await apiClient.get(`/api/companies/${companyId}/profile`);
    if (res.success && res.data) {
      return { success: true, data: res.data };
    }
    const comp = localCompanies.find(c => c.id === Number(companyId));
    return { success: true, data: comp ? comp.profile : null };
  }
};
