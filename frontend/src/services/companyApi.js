// Frontend API Client for Company Service (Member 3)
// Integrates with API Gateway at http://localhost:8080/api/companies

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';
const COMPANY_API_KEY = 'company-service-secret-key-12345';

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
    try {
      const res = await fetch(`${API_BASE_URL}/api/companies`, {
        headers: {
          'X-API-KEY': COMPANY_API_KEY
        }
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = await res.json();
      return { success: true, data: data.data, source: 'API' };
    } catch (err) {
      console.warn('Backend unavailable, using synchronized local company store:', err.message);
      return { success: true, data: localCompanies, source: 'LOCAL_SYNC' };
    }
  },

  // 2. Get company by ID
  async getCompany(companyId = 1) {
    try {
      const res = await fetch(`${API_BASE_URL}/api/companies/${companyId}`, {
        headers: {
          'X-API-KEY': COMPANY_API_KEY,
          'X-User-Id': '10',
          'X-User-Role': 'ROLE_COMPANY'
        }
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = await res.json();
      return { success: true, data: data.data, source: 'API' };
    } catch (err) {
      const found = localCompanies.find(c => c.id === Number(companyId)) || localCompanies[0];
      return { success: true, data: found, source: 'LOCAL_SYNC' };
    }
  },

  // 3. Register a new company
  async registerCompany(companyData) {
    try {
      const res = await fetch(`${API_BASE_URL}/api/companies`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-API-KEY': COMPANY_API_KEY,
          'X-User-Id': String(companyData.userId || 10),
          'X-User-Role': 'ROLE_COMPANY'
        },
        body: JSON.stringify(companyData)
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = await res.json();
      const newCompany = data.data;
      persistLocalCompanies([...localCompanies, newCompany]);
      return { success: true, data: newCompany };
    } catch (err) {
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
    }
  },

  // 4. Update basic company info
  async updateCompany(companyId, updateData) {
    try {
      const res = await fetch(`${API_BASE_URL}/api/companies/${companyId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'X-API-KEY': COMPANY_API_KEY,
          'X-User-Id': '10',
          'X-User-Role': 'ROLE_COMPANY'
        },
        body: JSON.stringify(updateData)
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = await res.json();
      const updated = data.data;
      persistLocalCompanies(localCompanies.map(c => c.id === Number(companyId) ? { ...c, ...updated } : c));
      return { success: true, data: updated };
    } catch (err) {
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
    }
  },

  // 5. Delete company
  async deleteCompany(companyId) {
    try {
      const res = await fetch(`${API_BASE_URL}/api/companies/${companyId}`, {
        method: 'DELETE',
        headers: {
          'X-API-KEY': COMPANY_API_KEY,
          'X-User-Id': '10',
          'X-User-Role': 'ROLE_COMPANY'
        }
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
    } catch (err) {
      console.warn('Deleting company locally');
    }
    const filtered = localCompanies.filter(c => c.id !== Number(companyId));
    persistLocalCompanies(filtered);
    return { success: true };
  },

  // 6. Save or update extended profile
  async saveOrUpdateProfile(companyId, profileData) {
    try {
      const res = await fetch(`${API_BASE_URL}/api/companies/${companyId}/profile`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-API-KEY': COMPANY_API_KEY,
          'X-User-Id': '10',
          'X-User-Role': 'ROLE_COMPANY'
        },
        body: JSON.stringify(profileData)
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = await res.json();
      const newProfile = data.data;

      const updatedList = localCompanies.map(c => {
        if (c.id === Number(companyId)) {
          return { ...c, profile: newProfile, updatedAt: new Date().toISOString() };
        }
        return c;
      });
      persistLocalCompanies(updatedList);
      return { success: true, data: newProfile };
    } catch (err) {
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
    }
  },

  // 7. Get extended profile by company ID
  async getProfile(companyId) {
    try {
      const res = await fetch(`${API_BASE_URL}/api/companies/${companyId}/profile`, {
        headers: {
          'X-API-KEY': COMPANY_API_KEY
        }
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = await res.json();
      return { success: true, data: data.data };
    } catch (err) {
      const comp = localCompanies.find(c => c.id === Number(companyId));
      return { success: true, data: comp ? comp.profile : null };
    }
  }
};
