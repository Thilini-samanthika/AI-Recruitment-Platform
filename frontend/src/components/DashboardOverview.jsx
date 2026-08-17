import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';

export default function DashboardOverview({
  onNavigate,
  totalJobs = 4,
  totalCompanies = 3,
  totalCandidates = 4,
  resumesCount = 1
}) {
  const { user, openAuthModal, switchRole } = useAuth();
  const [servicesStatus, setServicesStatus] = useState({
    gateway: { name: 'API Gateway (Member 1)', port: 8080, status: 'ONLINE', latency: '12ms' },
    auth: { name: 'Auth Service (Member 1)', port: 8081, status: 'ONLINE', latency: '18ms' },
    candidate: { name: 'Candidate Service (Member 2)', port: 8082, status: 'ONLINE', latency: '24ms' },
    company: { name: 'Company Service (Member 3)', port: 8083, status: 'ONLINE', latency: '15ms' },
    job: { name: 'Job Service (Member 4)', port: 8084, status: 'ONLINE', latency: '20ms' },
    ai: { name: 'AI Resume Service (Member 5)', port: 8085, status: 'ONLINE', latency: '35ms' }
  });

  return (
    <div className="dashboard-view">
      {/* Hero Welcome Banner */}
      <div className="hero-banner" style={{
        background: 'linear-gradient(135deg, rgba(88, 28, 135, 0.45) 0%, rgba(30, 27, 75, 0.6) 100%)',
        border: '1px solid rgba(168, 85, 247, 0.3)',
        borderRadius: '16px',
        padding: '2rem 2.5rem',
        marginBottom: '2rem',
        position: 'relative',
        overflow: 'hidden'
      }}>
        <div style={{ position: 'relative', zIndex: 2 }}>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem', background: 'rgba(168, 85, 247, 0.2)', padding: '0.35rem 0.85rem', borderRadius: '999px', fontSize: '0.8rem', color: '#d8b4fe', marginBottom: '0.75rem', fontWeight: 600 }}>
            <span>⚡ AI-Driven Autonomous Talent Ecosystem</span>
            <span style={{ width: 6, height: 6, borderRadius: '50%', background: '#34d399' }}></span>
          </div>

          <h1 style={{ fontSize: '2.2rem', fontWeight: 800, margin: '0 0 0.5rem', background: 'linear-gradient(to right, #ffffff, #e9d5ff)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
            Welcome back, {user ? (user.fullName || user.email) : 'Guest Developer'}!
          </h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '1rem', maxWidth: '680px', margin: '0 0 1.5rem', lineHeight: 1.6 }}>
            Unified enterprise hiring platform powering end-to-end recruitment with microservices, automated resume NLP extraction, intelligent job matching, and candidate ranking.
          </p>

          <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap' }}>
            <button
              className="btn-primary"
              onClick={() => onNavigate('ai-resume')}
              style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', padding: '0.75rem 1.25rem', borderRadius: '10px' }}
            >
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="17 8 12 3 7 8"></polyline><line x1="12" y1="3" x2="12" y2="15"></line></svg>
              Upload & Parse Resume (AI)
            </button>

            <button
              className="btn-secondary"
              onClick={() => onNavigate('ai-recommendations')}
              style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', padding: '0.75rem 1.25rem', borderRadius: '10px' }}
            >
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5"><circle cx="12" cy="12" r="10"></circle><path d="m4.93 4.93 4.24 4.24"></path><path d="m14.83 9.17 4.24-4.24"></path><path d="m14.83 14.83 4.24 4.24"></path><path d="m9.17 14.83-4.24 4.24"></path><circle cx="12" cy="12" r="4"></circle></svg>
              AI Job Matcher & Scores
            </button>

            <button
              className="btn-secondary"
              onClick={() => onNavigate('jobs')}
              style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', padding: '0.75rem 1.25rem', borderRadius: '10px' }}
            >
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5"><rect x="2" y="7" width="20" height="14" rx="2" ry="2"></rect><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path></svg>
              Explore Open Positions
            </button>
          </div>
        </div>
      </div>

      {/* Real-time KPI Stats Grid */}
      <div className="stats-grid" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '1.25rem', marginBottom: '2.5rem' }}>
        <div className="stat-card" style={{ background: 'var(--surface-card)', border: '1px solid var(--border-color)', borderRadius: '12px', padding: '1.25rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
            <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)', fontWeight: 600 }}>Active Job Postings</span>
            <span style={{ background: 'rgba(59, 130, 246, 0.15)', color: '#60a5fa', padding: '0.25rem 0.5rem', borderRadius: '6px', fontSize: '0.75rem', fontWeight: 700 }}>Member 4</span>
          </div>
          <div style={{ fontSize: '2rem', fontWeight: 800, margin: '0.5rem 0 0.25rem', color: '#60a5fa' }}>{totalJobs}</div>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Synchronized via Job Service</span>
        </div>

        <div className="stat-card" style={{ background: 'var(--surface-card)', border: '1px solid var(--border-color)', borderRadius: '12px', padding: '1.25rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
            <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)', fontWeight: 600 }}>Resumes Extracted</span>
            <span style={{ background: 'rgba(168, 85, 247, 0.15)', color: '#c084fc', padding: '0.25rem 0.5rem', borderRadius: '6px', fontSize: '0.75rem', fontWeight: 700 }}>Member 5</span>
          </div>
          <div style={{ fontSize: '2rem', fontWeight: 800, margin: '0.5rem 0 0.25rem', color: '#c084fc' }}>{resumesCount}</div>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>PDFBox & POI Text Parsing</span>
        </div>

        <div className="stat-card" style={{ background: 'var(--surface-card)', border: '1px solid var(--border-color)', borderRadius: '12px', padding: '1.25rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
            <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)', fontWeight: 600 }}>Registered Companies</span>
            <span style={{ background: 'rgba(16, 185, 129, 0.15)', color: '#34d399', padding: '0.25rem 0.5rem', borderRadius: '6px', fontSize: '0.75rem', fontWeight: 700 }}>Member 3</span>
          </div>
          <div style={{ fontSize: '2rem', fontWeight: 800, margin: '0.5rem 0 0.25rem', color: '#34d399' }}>{totalCompanies}</div>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Verified Employer Profiles</span>
        </div>

        <div className="stat-card" style={{ background: 'var(--surface-card)', border: '1px solid var(--border-color)', borderRadius: '12px', padding: '1.25rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
            <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)', fontWeight: 600 }}>Candidate Profiles</span>
            <span style={{ background: 'rgba(245, 158, 11, 0.15)', color: '#fbbf24', padding: '0.25rem 0.5rem', borderRadius: '6px', fontSize: '0.75rem', fontWeight: 700 }}>Member 2</span>
          </div>
          <div style={{ fontSize: '2rem', fontWeight: 800, margin: '0.5rem 0 0.25rem', color: '#fbbf24' }}>{totalCandidates}</div>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Skills & Education Records</span>
        </div>
      </div>

      {/* Core Platform Modules Grid */}
      <h2 style={{ fontSize: '1.4rem', fontWeight: 700, marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
        <span>🚀 Platform Modules & Services</span>
      </h2>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '1.5rem', marginBottom: '2.5rem' }}>
        {/* Card 1: AI Resume Studio */}
        <div
          onClick={() => onNavigate('ai-resume')}
          style={{ background: 'var(--surface-card)', border: '1px solid var(--border-color)', borderRadius: '14px', padding: '1.5rem', cursor: 'pointer', transition: 'all 0.2s' }}
          className="feature-hover-card"
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem' }}>
            <div style={{ background: 'rgba(168, 85, 247, 0.2)', color: '#c084fc', padding: '0.65rem', borderRadius: '10px' }}>
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>
            </div>
            <div>
              <h3 style={{ margin: 0, fontSize: '1.1rem', fontWeight: 700 }}>AI Resume Studio</h3>
              <span style={{ fontSize: '0.75rem', color: '#c084fc', fontWeight: 600 }}>Member 5 Lead &bull; Port 8085</span>
            </div>
          </div>
          <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)', lineHeight: 1.5, margin: '0 0 1rem' }}>
            Upload resume in PDF, DOCX, or TXT format. High-precision Apache PDFBox & POI engine extracts candidate skills and generates categorized taxonomy.
          </p>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#c084fc', fontSize: '0.85rem', fontWeight: 600 }}>
            <span>Launch Resume Studio</span>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5"><path d="M5 12h14"></path><path d="m12 5 7 7-7 7"></path></svg>
          </div>
        </div>

        {/* Card 2: AI Job Matcher & Recommendations */}
        <div
          onClick={() => onNavigate('ai-recommendations')}
          style={{ background: 'var(--surface-card)', border: '1px solid var(--border-color)', borderRadius: '14px', padding: '1.5rem', cursor: 'pointer', transition: 'all 0.2s' }}
          className="feature-hover-card"
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem' }}>
            <div style={{ background: 'rgba(236, 72, 153, 0.2)', color: '#f472b6', padding: '0.65rem', borderRadius: '10px' }}>
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg>
            </div>
            <div>
              <h3 style={{ margin: 0, fontSize: '1.1rem', fontWeight: 700 }}>AI Job Matcher & Scorer</h3>
              <span style={{ fontSize: '0.75rem', color: '#f472b6', fontWeight: 600 }}>Member 5 Lead &bull; /api/match</span>
            </div>
          </div>
          <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)', lineHeight: 1.5, margin: '0 0 1rem' }}>
            Compute semantic compatibility scores (0-100%), identify matched strengths vs missing skill gaps, and explore AI recommendations.
          </p>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#f472b6', fontSize: '0.85rem', fontWeight: 600 }}>
            <span>Calculate Compatibility</span>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5"><path d="M5 12h14"></path><path d="m12 5 7 7-7 7"></path></svg>
          </div>
        </div>

        {/* Card 3: Job Listings & Applications */}
        <div
          onClick={() => onNavigate('jobs')}
          style={{ background: 'var(--surface-card)', border: '1px solid var(--border-color)', borderRadius: '14px', padding: '1.5rem', cursor: 'pointer', transition: 'all 0.2s' }}
          className="feature-hover-card"
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem' }}>
            <div style={{ background: 'rgba(59, 130, 246, 0.2)', color: '#60a5fa', padding: '0.65rem', borderRadius: '10px' }}>
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5"><rect x="2" y="7" width="20" height="14" rx="2" ry="2"></rect><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path></svg>
            </div>
            <div>
              <h3 style={{ margin: 0, fontSize: '1.1rem', fontWeight: 700 }}>Job Portal & Catalog</h3>
              <span style={{ fontSize: '0.75rem', color: '#60a5fa', fontWeight: 600 }}>Member 4 &bull; Port 8084</span>
            </div>
          </div>
          <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)', lineHeight: 1.5, margin: '0 0 1rem' }}>
            Browse open positions across companies. Filter by stack, role level, and salary. Instant 1-click match against your active resume.
          </p>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#60a5fa', fontSize: '0.85rem', fontWeight: 600 }}>
            <span>Browse All Openings</span>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5"><path d="M5 12h14"></path><path d="m12 5 7 7-7 7"></path></svg>
          </div>
        </div>
      </div>

      {/* Microservice Mesh Telemetry Status Banner */}
      <div style={{
        background: 'var(--surface-card)',
        border: '1px solid var(--border-color)',
        borderRadius: '14px',
        padding: '1.5rem',
        marginBottom: '2rem'
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem', flexWrap: 'wrap', gap: '0.5rem' }}>
          <div>
            <h3 style={{ margin: '0 0 0.25rem', fontSize: '1.1rem', fontWeight: 700 }}>
              🌐 Microservice Mesh Status
            </h3>
            <p style={{ margin: 0, fontSize: '0.82rem', color: 'var(--text-muted)' }}>
              Live orchestration map of all 5 team services routing through API Gateway
            </p>
          </div>
          <button
            className="btn-secondary"
            onClick={() => onNavigate('api-info')}
            style={{ fontSize: '0.82rem', padding: '0.45rem 0.85rem' }}
          >
            Explore API Docs & Specs &rarr;
          </button>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '0.75rem' }}>
          {Object.entries(servicesStatus).map(([key, svc]) => (
            <div key={key} style={{ background: 'var(--surface-subtle)', borderRadius: '10px', padding: '0.85rem', border: '1px solid var(--border-color)' }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '0.35rem' }}>
                <span style={{ fontSize: '0.78rem', fontWeight: 700, color: 'var(--text-main)' }}>{svc.name.split(' (')[0]}</span>
                <span style={{ width: 8, height: 8, borderRadius: '50%', background: '#10b981', display: 'inline-block' }}></span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.72rem', color: 'var(--text-muted)' }}>
                <span>Port {svc.port}</span>
                <span style={{ color: '#34d399', fontWeight: 600 }}>{svc.status}</span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
