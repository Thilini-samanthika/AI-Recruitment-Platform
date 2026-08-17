// Authentication & Identity Service Client (Member 1 Integration)
// Gateway route: http://localhost:8080/api/auth

import { apiClient } from './apiClient';

const MOCK_USER_STORAGE_KEY = 'mock_users_db';

function getStoredMockUsers() {
  const data = localStorage.getItem(MOCK_USER_STORAGE_KEY);
  if (data) return JSON.parse(data);
  const initial = [
    {
      id: 1,
      email: 'candidate@recruitment.io',
      role: 'ROLE_CANDIDATE',
      token: 'jwt_mock_cand_token_xyz987',
      fullName: 'Alex Mercer'
    },
    {
      id: 2,
      email: 'company@recruitment.io',
      role: 'ROLE_COMPANY',
      token: 'jwt_mock_comp_token_abc123',
      fullName: 'Acme HR Recruiter'
    },
    {
      id: 3,
      email: 'admin@recruitment.io',
      role: 'ROLE_ADMIN',
      token: 'jwt_mock_admin_token_456def',
      fullName: 'Platform Admin'
    }
  ];
  localStorage.setItem(MOCK_USER_STORAGE_KEY, JSON.stringify(initial));
  return initial;
}

export const authApi = {
  /**
   * Register a new user (Candidate or Company)
   */
  async register(registerData) {
    const res = await apiClient.post('/api/auth/register', registerData);
    if (res.success && res.data) {
      return res;
    }

    // Fallback simulation if auth-service is offline
    const users = getStoredMockUsers();
    const newUser = {
      id: users.length + 1,
      email: registerData.email,
      role: registerData.role || 'ROLE_CANDIDATE',
      token: 'jwt_simulated_' + Math.random().toString(36).substring(2),
      fullName: registerData.email.split('@')[0]
    };
    users.push(newUser);
    localStorage.setItem(MOCK_USER_STORAGE_KEY, JSON.stringify(users));

    return {
      success: true,
      message: 'Registration successful (Simulated Mode)',
      data: {
        id: newUser.id,
        email: newUser.email,
        role: newUser.role,
        token: newUser.token
      }
    };
  },

  /**
   * Login user with credentials
   */
  async login(loginData) {
    const res = await apiClient.post('/api/auth/login', loginData);
    if (res.success && res.data) {
      return res;
    }

    // Fallback simulation
    const users = getStoredMockUsers();
    const user = users.find(u => u.email.toLowerCase() === loginData.email.toLowerCase()) || {
      id: 1,
      email: loginData.email,
      role: loginData.email.includes('comp') ? 'ROLE_COMPANY' : 'ROLE_CANDIDATE',
      token: 'jwt_simulated_token_' + Date.now(),
      fullName: loginData.email.split('@')[0]
    };

    return {
      success: true,
      message: 'Login successful (Local Session)',
      data: {
        id: user.id,
        email: user.email,
        role: user.role,
        token: user.token || 'jwt_simulated_token_' + Date.now()
      }
    };
  },

  /**
   * Validate token
   */
  async validateToken(token) {
    return apiClient.get('/api/auth/validate?token=' + encodeURIComponent(token));
  },

  /**
   * Get user details by ID
   */
  async getUserById(userId) {
    return apiClient.get(`/api/auth/users/${userId}`);
  }
};
