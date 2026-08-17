import React, { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '../services/authApi';

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      const stored = localStorage.getItem('auth_user');
      if (stored) return JSON.parse(stored);
    } catch (e) {
      console.warn('Failed to parse cached auth_user:', e);
    }
    // Default initial authenticated candidate for instant development & university demo
    return {
      id: 1,
      candidateId: 1,
      email: 'alex.mercer@recruitment.io',
      role: 'ROLE_CANDIDATE',
      fullName: 'Alex Mercer',
      token: 'jwt_demo_token_candidate_123'
    };
  });

  const [isAuthModalOpen, setIsAuthModalOpen] = useState(false);
  const [authModalMode, setAuthModalMode] = useState('login'); // 'login' or 'register'

  useEffect(() => {
    if (user) {
      localStorage.setItem('auth_user', JSON.stringify(user));
      if (user.token) {
        localStorage.setItem('auth_token', user.token);
      }
    } else {
      localStorage.removeItem('auth_user');
      localStorage.removeItem('auth_token');
    }
  }, [user]);

  const login = async (email, password) => {
    const res = await authApi.login({ email, password });
    if (res.success && res.data) {
      const role = res.data.role || (email.includes('comp') ? 'ROLE_COMPANY' : 'ROLE_CANDIDATE');
      const loggedUser = {
        id: res.data.id || 1,
        candidateId: role === 'ROLE_CANDIDATE' ? (res.data.id || 1) : null,
        companyId: role === 'ROLE_COMPANY' ? (res.data.id || 1) : null,
        email: res.data.email,
        role: role,
        fullName: res.data.email.split('@')[0],
        token: res.data.token
      };
      setUser(loggedUser);
      setIsAuthModalOpen(false);
      return { success: true, user: loggedUser };
    }
    return { success: false, message: res.message || 'Login failed' };
  };

  const register = async (email, password, role) => {
    const res = await authApi.register({ email, password, role });
    if (res.success && res.data) {
      const regUser = {
        id: res.data.id || 1,
        candidateId: role === 'ROLE_CANDIDATE' ? (res.data.id || 1) : null,
        companyId: role === 'ROLE_COMPANY' ? (res.data.id || 1) : null,
        email: res.data.email,
        role: role,
        fullName: res.data.email.split('@')[0],
        token: res.data.token
      };
      setUser(regUser);
      setIsAuthModalOpen(false);
      return { success: true, user: regUser };
    }
    return { success: false, message: res.message || 'Registration failed' };
  };

  const logout = () => {
    setUser(null);
  };

  const switchRole = (newRole) => {
    setUser(prev => ({
      ...(prev || { id: 1, email: 'demo@recruitment.io', fullName: 'Demo User' }),
      role: newRole,
      candidateId: newRole === 'ROLE_CANDIDATE' ? 1 : null,
      companyId: newRole === 'ROLE_COMPANY' ? 1 : null
    }));
  };

  const openAuthModal = (mode = 'login') => {
    setAuthModalMode(mode);
    setIsAuthModalOpen(true);
  };

  const closeAuthModal = () => {
    setIsAuthModalOpen(false);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        login,
        register,
        logout,
        switchRole,
        isAuthModalOpen,
        authModalMode,
        openAuthModal,
        closeAuthModal
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
