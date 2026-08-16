import React, { useState } from 'react';
import EditCompanyModal from './EditCompanyModal';
import EditCompanyProfileModal from './EditCompanyProfileModal';

export default function CompanyDashboard({ company, onUpdateCompany, onUpdateProfile, onDeleteCompany, onOpenRegisterModal }) {
  const [isEditCompanyOpen, setIsEditCompanyOpen] = useState(false);
  const [isEditProfileOpen, setIsEditProfileOpen] = useState(false);

  if (!company) {
    return (
      <div className="empty-state" style={{ paddingTop: '6rem' }}>
        <div className="avatar-wrapper" style={{ margin: '0 auto 1.5rem', background: '#374151' }}>
          🏢
        </div>
        <h2>No Company Selected</h2>
        <p style={{ marginTop: '0.5rem', color: '#9ca3af' }}>Select a company from the Directory or register a new one.</p>
        <button
          className="btn btn-primary"
          style={{ marginTop: '1.5rem' }}
          onClick={onOpenRegisterModal}
        >
          + Register Company
        </button>
      </div>
    );
  }

  const profile = company.profile || {};
  const initials = (company.companyName || 'CO')
    .split(' ')
    .map(w => w[0])
    .slice(0, 2)
    .join('')
    .toUpperCase();

  const formattedCreated = company.createdAt
    ? new Date(company.createdAt).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' })
    : 'Recently';

  const formattedUpdated = company.updatedAt
    ? new Date(company.updatedAt).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
    : 'Recently';

  return (
    <div className="company-dashboard">
      {/* Company Header Card */}
      <div className="glass-panel profile-header-card">
        <div className="profile-top">
          <div style={{ display: 'flex', gap: '1.5rem', alignItems: 'center' }}>
            {profile.logoUrl ? (
              <img
                src={profile.logoUrl}
                alt={company.companyName}
                className="avatar-wrapper"
                style={{ objectFit: 'cover', border: '2px solid rgba(255,255,255,0.1)' }}
                onError={(e) => { e.target.style.display = 'none'; }}
              />
            ) : (
              <div className="avatar-wrapper">
                {initials}
              </div>
            )}

            <div className="profile-info">
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', flexWrap: 'wrap' }}>
                <h1 className="profile-name">{company.companyName}</h1>
                <span className="skill-level-badge level-ADVANCED" style={{ fontSize: '0.75rem', padding: '0.2rem 0.6rem' }}>
                  Verified Employer
                </span>
              </div>

              <p className="profile-headline">
                {profile.industry || 'Corporate Enterprise'} &bull; {profile.companySize || '50-200'} Employees
              </p>

              <div className="contact-badges">
                {company.email && (
                  <div className="contact-item">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path><polyline points="22,6 12,13 2,6"></polyline></svg>
                    <span>{company.email}</span>
                  </div>
                )}

                {company.phone && (
                  <div className="contact-item">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path></svg>
                    <span>{company.phone}</span>
                  </div>
                )}

                {company.address && (
                  <div className="contact-item">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path><circle cx="12" cy="10" r="3"></circle></svg>
                    <span>{company.address}</span>
                  </div>
                )}

                {profile.website && (
                  <a
                    href={profile.website}
                    target="_blank"
                    rel="noreferrer"
                    className="contact-item"
                    style={{ color: '#818cf8', textDecoration: 'none' }}
                  >
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><circle cx="12" cy="12" r="10"></circle><line x1="2" y1="12" x2="22" y2="12"></line><path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"></path></svg>
                    <span>{profile.website.replace(/^https?:\/\//, '')}</span>
                  </a>
                )}
              </div>
            </div>
          </div>

          <div style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap' }}>
            <button className="btn btn-secondary btn-sm" onClick={() => setIsEditCompanyOpen(true)}>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
              Edit Contact
            </button>
            <button className="btn btn-primary btn-sm" onClick={() => setIsEditProfileOpen(true)}>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M12 20h9"></path><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"></path></svg>
              Edit Extended Profile
            </button>
            <button
              className="btn-danger-outline"
              style={{ padding: '0.4rem 0.8rem', cursor: 'pointer' }}
              onClick={() => {
                if (window.confirm(`Are you sure you want to delete company '${company.companyName}'?`)) {
                  onDeleteCompany(company.id);
                }
              }}
            >
              Delete
            </button>
          </div>
        </div>

        <div className="profile-bio">
          <p>{profile.description || 'No detailed corporate description has been provided yet. Click Edit Extended Profile to add your company overview, culture, and achievements.'}</p>
        </div>
      </div>

      {/* KPI Stats Bar */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '1.25rem', marginBottom: '2rem' }}>
        <div className="glass-panel" style={{ padding: '1.5rem', display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
          <div style={{ width: '48px', height: '48px', borderRadius: '12px', background: 'rgba(99, 102, 241, 0.15)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#818cf8' }}>
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><rect x="2" y="7" width="20" height="14" rx="2" ry="2"></rect><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path></svg>
          </div>
          <div>
            <div style={{ fontSize: '0.85rem', color: '#9ca3af' }}>Active Jobs</div>
            <div style={{ fontSize: '1.5rem', fontWeight: 700, color: '#f9fafb' }}>4 Published</div>
          </div>
        </div>

        <div className="glass-panel" style={{ padding: '1.5rem', display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
          <div style={{ width: '48px', height: '48px', borderRadius: '12px', background: 'rgba(16, 185, 129, 0.15)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#34d399' }}>
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>
          </div>
          <div>
            <div style={{ fontSize: '0.85rem', color: '#9ca3af' }}>Candidate Pipeline</div>
            <div style={{ fontSize: '1.5rem', fontWeight: 700, color: '#f9fafb' }}>38 Applicants</div>
          </div>
        </div>

        <div className="glass-panel" style={{ padding: '1.5rem', display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
          <div style={{ width: '48px', height: '48px', borderRadius: '12px', background: 'rgba(168, 85, 247, 0.15)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#c084fc' }}>
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg>
          </div>
          <div>
            <div style={{ fontSize: '0.85rem', color: '#9ca3af' }}>AI Match Score</div>
            <div style={{ fontSize: '1.5rem', fontWeight: 700, color: '#f9fafb' }}>94% Precision</div>
          </div>
        </div>

        <div className="glass-panel" style={{ padding: '1.5rem', display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
          <div style={{ width: '48px', height: '48px', borderRadius: '12px', background: 'rgba(6, 182, 212, 0.15)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#22d3ee' }}>
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path></svg>
          </div>
          <div>
            <div style={{ fontSize: '0.85rem', color: '#9ca3af' }}>Service Port</div>
            <div style={{ fontSize: '1.5rem', fontWeight: 700, color: '#f9fafb' }}>Port 8083</div>
          </div>
        </div>
      </div>

      {/* Grid Content */}
      <div className="dashboard-grid">
        {/* Left Column (8 Col) */}
        <div className="grid-col-8">
          {/* Corporate Highlights Card */}
          <div className="glass-panel section-card" style={{ marginBottom: '1.5rem' }}>
            <div className="section-header">
              <h3 className="section-title">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#6366f1" strokeWidth="2"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 14 14"></polyline></svg>
                Active Hiring Campaigns & Roles
              </h3>
            </div>

            <div className="timeline-list">
              <div className="timeline-item">
                <div className="timeline-card">
                  <div className="timeline-header">
                    <div>
                      <h4 className="timeline-heading">Principal AI Infrastructure Engineer</h4>
                      <div className="timeline-subheading">{company.companyName} &bull; Remote / Hybrid</div>
                    </div>
                    <span className="timeline-date">Open Role</span>
                  </div>
                  <p className="timeline-desc">
                    Architecting distributed LLM inference engines and vector retrieval microservices using Spring Boot & PyTorch.
                  </p>
                </div>
              </div>

              <div className="timeline-item">
                <div className="timeline-card">
                  <div className="timeline-header">
                    <div>
                      <h4 className="timeline-heading">Lead Full-Stack React / Java Developer</h4>
                      <div className="timeline-subheading">{company.companyName} &bull; San Francisco, CA</div>
                    </div>
                    <span className="timeline-date">Open Role</span>
                  </div>
                  <p className="timeline-desc">
                    Leading frontend UI innovation with React, Vite, and Spring Boot REST microservices behind API Gateway.
                  </p>
                </div>
              </div>
            </div>
          </div>

          {/* Hiring Workflow Info */}
          <div className="glass-panel section-card">
            <div className="section-header">
              <h3 className="section-title">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#10b981" strokeWidth="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>
                Company Registration & Recruitment Pipeline
              </h3>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem', marginTop: '0.5rem' }}>
              <div style={{ background: 'rgba(255,255,255,0.03)', padding: '1rem', borderRadius: '8px', border: '1px solid rgba(255,255,255,0.06)' }}>
                <div style={{ fontSize: '0.8rem', color: '#818cf8', fontWeight: 600 }}>STEP 1: AUTHENTICATION</div>
                <div style={{ fontWeight: 600, marginTop: '0.25rem' }}>User Registers (Role: COMPANY)</div>
                <div style={{ fontSize: '0.8rem', color: '#9ca3af', marginTop: '0.25rem' }}>Auth Service (8081) issues JWT credential.</div>
              </div>

              <div style={{ background: 'rgba(255,255,255,0.03)', padding: '1rem', borderRadius: '8px', border: '1px solid rgba(255,255,255,0.06)' }}>
                <div style={{ fontSize: '0.8rem', color: '#a855f7', fontWeight: 600 }}>STEP 2: COMPANY CREATION</div>
                <div style={{ fontWeight: 600, marginTop: '0.25rem' }}>Company Record Created</div>
                <div style={{ fontSize: '0.8rem', color: '#9ca3af', marginTop: '0.25rem' }}>Company Service (8083) stores record in company table.</div>
              </div>

              <div style={{ background: 'rgba(255,255,255,0.03)', padding: '1rem', borderRadius: '8px', border: '1px solid rgba(255,255,255,0.06)' }}>
                <div style={{ fontSize: '0.8rem', color: '#34d399', fontWeight: 600 }}>STEP 3: EXTENDED PROFILE</div>
                <div style={{ fontWeight: 600, marginTop: '0.25rem' }}>Company Fills Profile</div>
                <div style={{ fontSize: '0.8rem', color: '#9ca3af', marginTop: '0.25rem' }}>company_profile table stores industry, logo, size, bio.</div>
              </div>

              <div style={{ background: 'rgba(255,255,255,0.03)', padding: '1rem', borderRadius: '8px', border: '1px solid rgba(255,255,255,0.06)' }}>
                <div style={{ fontSize: '0.8rem', color: '#06b6d4', fontWeight: 600 }}>STEP 4: POST JOBS</div>
                <div style={{ fontWeight: 600, marginTop: '0.25rem' }}>Job Service (8084)</div>
                <div style={{ fontSize: '0.8rem', color: '#9ca3af', marginTop: '0.25rem' }}>Post listings and evaluate candidate matches with AI.</div>
              </div>
            </div>
          </div>
        </div>

        {/* Right Column (4 Col) */}
        <div className="grid-col-4">
          {/* Metadata Card */}
          <div className="glass-panel section-card" style={{ marginBottom: '1.5rem' }}>
            <div className="section-header">
              <h3 className="section-title">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#a855f7" strokeWidth="2"><rect x="2" y="2" width="20" height="20" rx="5" ry="5"></rect><path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z"></path><line x1="17.5" y1="6.5" x2="17.51" y2="6.5"></line></svg>
                Corporate Metadata
              </h3>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.85rem', fontSize: '0.9rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid rgba(255,255,255,0.05)', paddingBottom: '0.5rem' }}>
                <span style={{ color: '#9ca3af' }}>Company ID:</span>
                <span style={{ fontWeight: 600, color: '#f3f4f6' }}>#{company.id}</span>
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid rgba(255,255,255,0.05)', paddingBottom: '0.5rem' }}>
                <span style={{ color: '#9ca3af' }}>Auth User ID:</span>
                <span style={{ fontWeight: 600, color: '#f3f4f6' }}>User #{company.userId}</span>
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid rgba(255,255,255,0.05)', paddingBottom: '0.5rem' }}>
                <span style={{ color: '#9ca3af' }}>Industry:</span>
                <span style={{ fontWeight: 600, color: '#a5b4fc' }}>{profile.industry || 'Technology'}</span>
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid rgba(255,255,255,0.05)', paddingBottom: '0.5rem' }}>
                <span style={{ color: '#9ca3af' }}>Team Size:</span>
                <span style={{ fontWeight: 600, color: '#f3f4f6' }}>{profile.companySize || '51-200'}</span>
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid rgba(255,255,255,0.05)', paddingBottom: '0.5rem' }}>
                <span style={{ color: '#9ca3af' }}>Registered On:</span>
                <span style={{ color: '#f3f4f6' }}>{formattedCreated}</span>
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: '#9ca3af' }}>Last Sync:</span>
                <span style={{ color: '#f3f4f6' }}>{formattedUpdated}</span>
              </div>
            </div>
          </div>

          {/* Microservice Security Pill */}
          <div className="glass-panel section-card">
            <div className="section-header">
              <h3 className="section-title">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#10b981" strokeWidth="2"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path></svg>
                Security & Gateway
              </h3>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', fontSize: '0.85rem' }}>
              <div style={{ background: 'rgba(99, 102, 241, 0.1)', padding: '0.75rem', borderRadius: '8px', border: '1px solid rgba(99, 102, 241, 0.25)' }}>
                <div style={{ fontWeight: 600, color: '#a5b4fc', marginBottom: '0.2rem' }}>X-API-KEY Header</div>
                <div style={{ color: '#d1d5db', fontFamily: 'monospace', fontSize: '0.75rem' }}>COMPANY_SERVICE_KEY</div>
              </div>

              <div style={{ background: 'rgba(16, 185, 129, 0.1)', padding: '0.75rem', borderRadius: '8px', border: '1px solid rgba(16, 185, 129, 0.25)' }}>
                <div style={{ fontWeight: 600, color: '#34d399', marginBottom: '0.2rem' }}>Gateway Routing</div>
                <div style={{ color: '#d1d5db', fontFamily: 'monospace', fontSize: '0.75rem' }}>/api/companies/** &rarr; :8083</div>
              </div>

              <div style={{ background: 'rgba(168, 85, 247, 0.1)', padding: '0.75rem', borderRadius: '8px', border: '1px solid rgba(168, 85, 247, 0.25)' }}>
                <div style={{ fontWeight: 600, color: '#c084fc', marginBottom: '0.2rem' }}>Swagger UI Docs</div>
                <div style={{ color: '#d1d5db', fontFamily: 'monospace', fontSize: '0.75rem' }}>http://localhost:8083/swagger-ui.html</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Modals */}
      <EditCompanyModal
        company={company}
        isOpen={isEditCompanyOpen}
        onClose={() => setIsEditCompanyOpen(false)}
        onSave={onUpdateCompany}
      />

      <EditCompanyProfileModal
        company={company}
        isOpen={isEditProfileOpen}
        onClose={() => setIsEditProfileOpen(false)}
        onSave={onUpdateProfile}
      />
    </div>
  );
}
