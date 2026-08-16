import React from 'react';

export default function Navbar({ activeTab, setActiveTab }) {
  return (
    <header className="navbar">
      <div className="navbar-inner">
        <div className="brand-logo" onClick={() => setActiveTab('company-dashboard')} style={{ cursor: 'pointer' }}>
          <div className="logo-badge">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <rect x="2" y="7" width="20" height="14" rx="2" ry="2"></rect>
              <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path>
            </svg>
          </div>
          <span>
            AI Recruitment Platform <span style={{ fontSize: '0.8rem', color: '#a855f7', fontWeight: 600 }}>[Company &bull; Member 3]</span>
          </span>
        </div>

        <div className="nav-links">
          <button
            className={`nav-btn ${activeTab === 'company-dashboard' ? 'active' : ''}`}
            onClick={() => setActiveTab('company-dashboard')}
          >
            🏢 Company Dashboard
          </button>

          <button
            className={`nav-btn ${activeTab === 'company-directory' ? 'active' : ''}`}
            onClick={() => setActiveTab('company-directory')}
          >
            🏛️ Company Directory
          </button>

          <button
            className={`nav-btn ${activeTab === 'candidate-profile' ? 'active' : ''}`}
            onClick={() => setActiveTab('candidate-profile')}
          >
            👤 Candidate Portal
          </button>

          <button
            className={`nav-btn ${activeTab === 'candidate-directory' ? 'active' : ''}`}
            onClick={() => setActiveTab('candidate-directory')}
          >
            📋 Candidate Directory
          </button>

          <button
            className={`nav-btn ${activeTab === 'api-info' ? 'active' : ''}`}
            onClick={() => setActiveTab('api-info')}
          >
            ⚡ Member 3 API & Architecture
          </button>

          <div className="service-pill" title="Company Service is active on port 8083 / Gateway 8080">
            <span className="pulse-dot"></span>
            <span>Port: 8083</span>
          </div>
        </div>
      </div>
    </header>
  );
}
