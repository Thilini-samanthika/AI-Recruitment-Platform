import React from 'react';
import { useAuth } from '../context/AuthContext';

export default function Navbar({ activeTab, setActiveTab }) {
  const { user, logout, openAuthModal, switchRole } = useAuth();

  return (
    <header className="navbar">
      <div className="navbar-inner">
        {/* Brand Logo */}
        <div className="brand-logo" onClick={() => setActiveTab('dashboard')} style={{ cursor: 'pointer' }}>
          <div className="logo-badge" style={{ background: 'linear-gradient(135deg, #a855f7 0%, #ec4899 100%)' }}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#ffffff" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="12" cy="12" r="10"></circle>
              <path d="m4.93 4.93 4.24 4.24"></path>
              <path d="m14.83 9.17 4.24-4.24"></path>
              <path d="m14.83 14.83 4.24 4.24"></path>
              <path d="m9.17 14.83-4.24 4.24"></path>
              <circle cx="12" cy="12" r="4"></circle>
            </svg>
          </div>
          <span style={{ display: 'flex', flexDirection: 'column' }}>
            <span style={{ fontWeight: 800, letterSpacing: '-0.02em', fontSize: '1.05rem' }}>
              AI Recruitment Platform
            </span>
            <span style={{ fontSize: '0.7rem', color: '#c084fc', fontWeight: 600 }}>
              Member 5 &bull; AI Resume Service & Frontend Lead
            </span>
          </span>
        </div>

        {/* Navigation Tabs */}
        <div className="nav-links">
          <button
            className={`nav-btn ${activeTab === 'dashboard' ? 'active' : ''}`}
            onClick={() => setActiveTab('dashboard')}
          >
            📊 Dashboard
          </button>

          <button
            className={`nav-btn ${activeTab === 'jobs' ? 'active' : ''}`}
            onClick={() => setActiveTab('jobs')}
          >
            💼 Jobs
          </button>

          <button
            className={`nav-btn ${activeTab === 'ai-resume' ? 'active' : ''}`}
            onClick={() => setActiveTab('ai-resume')}
            style={{ position: 'relative' }}
          >
            📄 AI Resume Studio
            <span style={{ position: 'absolute', top: '-4px', right: '-4px', width: 8, height: 8, borderRadius: '50%', background: '#a855f7' }}></span>
          </button>

          <button
            className={`nav-btn ${activeTab === 'ai-recommendations' ? 'active' : ''}`}
            onClick={() => setActiveTab('ai-recommendations')}
          >
            ⚡ AI Matcher
          </button>

          <button
            className={`nav-btn ${activeTab === 'company-dashboard' || activeTab === 'company-directory' ? 'active' : ''}`}
            onClick={() => setActiveTab('company-dashboard')}
          >
            🏢 Companies
          </button>

          <button
            className={`nav-btn ${activeTab === 'candidate-profile' || activeTab === 'candidate-directory' ? 'active' : ''}`}
            onClick={() => setActiveTab('candidate-profile')}
          >
            👤 Candidate
          </button>

          <button
            className={`nav-btn ${activeTab === 'api-info' ? 'active' : ''}`}
            onClick={() => setActiveTab('api-info')}
          >
            🌐 Architecture & APIs
          </button>

          {/* User Auth Section */}
          {user ? (
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginLeft: '0.5rem' }}>
              <div
                style={{
                  background: 'var(--surface-subtle)',
                  border: '1px solid var(--border-color)',
                  borderRadius: '999px',
                  padding: '0.25rem 0.65rem',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '0.4rem',
                  fontSize: '0.78rem'
                }}
              >
                <span style={{ width: 6, height: 6, borderRadius: '50%', background: '#10b981' }}></span>
                <span style={{ fontWeight: 600, color: 'var(--text-main)' }}>
                  {user.fullName || user.email.split('@')[0]}
                </span>
                <span style={{
                  fontSize: '0.65rem',
                  fontWeight: 700,
                  background: user.role === 'ROLE_COMPANY' ? 'rgba(59, 130, 246, 0.2)' : 'rgba(168, 85, 247, 0.2)',
                  color: user.role === 'ROLE_COMPANY' ? '#60a5fa' : '#c084fc',
                  padding: '0.1rem 0.4rem',
                  borderRadius: '4px'
                }}>
                  {user.role === 'ROLE_COMPANY' ? 'COMPANY' : (user.role === 'ROLE_ADMIN' ? 'ADMIN' : 'CANDIDATE')}
                </span>
              </div>

              {/* Quick Role Switcher Button */}
              <button
                onClick={() => switchRole(user.role === 'ROLE_CANDIDATE' ? 'ROLE_COMPANY' : 'ROLE_CANDIDATE')}
                title="Toggle between Candidate and Company view"
                style={{
                  background: 'transparent',
                  border: '1px solid var(--border-color)',
                  color: 'var(--text-muted)',
                  borderRadius: '6px',
                  padding: '0.25rem 0.5rem',
                  fontSize: '0.72rem',
                  cursor: 'pointer'
                }}
              >
                ⇄ Switch
              </button>

              <button
                onClick={logout}
                style={{
                  background: 'rgba(239, 68, 68, 0.15)',
                  border: '1px solid rgba(239, 68, 68, 0.3)',
                  color: '#fca5a5',
                  borderRadius: '6px',
                  padding: '0.25rem 0.55rem',
                  fontSize: '0.72rem',
                  fontWeight: 600,
                  cursor: 'pointer'
                }}
              >
                Sign Out
              </button>
            </div>
          ) : (
            <button
              className="btn-primary"
              onClick={() => openAuthModal('login')}
              style={{ fontSize: '0.8rem', padding: '0.4rem 0.85rem', borderRadius: '6px' }}
            >
              Sign In
            </button>
          )}

          {/* AI Service Live Indicator */}
          <div className="service-pill" title="AI Resume Service is active on port 8085 / Gateway 8080">
            <span className="pulse-dot"></span>
            <span>AI: 8085</span>
          </div>
        </div>
      </div>
    </header>
  );
}
