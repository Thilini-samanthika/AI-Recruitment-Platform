import React from 'react';

export default function Navbar({ activeTab, setActiveTab }) {
  return (
    <header className="navbar">
      <div className="navbar-inner">
        <a href="#candidate" className="brand-logo" onClick={() => setActiveTab('profile')}>
          <div className="logo-badge">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path>
              <circle cx="9" cy="7" r="4"></circle>
              <path d="M22 21v-2a4 4 0 0 0-3-3.87"></path>
              <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
            </svg>
          </div>
          <span>AI Recruitment Platform <span style={{ fontSize: '0.8rem', color: '#818cf8', fontWeight: 600 }}>[Candidate]</span></span>
        </a>

        <div className="nav-links">
          <button
            className={`nav-btn ${activeTab === 'profile' ? 'active' : ''}`}
            onClick={() => setActiveTab('profile')}
          >
            My Profile & Dashboard
          </button>
          <button
            className={`nav-btn ${activeTab === 'directory' ? 'active' : ''}`}
            onClick={() => setActiveTab('directory')}
          >
            Candidate Directory
          </button>
          <button
            className={`nav-btn ${activeTab === 'api' ? 'active' : ''}`}
            onClick={() => setActiveTab('api')}
          >
            Microservice Info
          </button>

          <div className="service-pill" title="Candidate Service is running on port 8082 / Gateway 8080">
            <span className="pulse-dot"></span>
            <span>Port: 8082</span>
          </div>
        </div>
      </div>
    </header>
  );
}
