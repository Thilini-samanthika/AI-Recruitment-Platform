import React from 'react';

export default function CompanyServiceInfo() {
  const endpoints = [
    { method: 'POST', path: '/api/companies', desc: 'Register a new corporate employer record', role: 'Role: COMPANY / Service Key' },
    { method: 'GET', path: '/api/companies', desc: 'List all companies in the directory', role: 'Public / Authenticated' },
    { method: 'GET', path: '/api/companies/{id}', desc: 'Get company details by ID', role: 'Public / Authenticated' },
    { method: 'GET', path: '/api/companies/user/{userId}', desc: 'Get company by Auth User ID', role: 'Owner / Admin' },
    { method: 'PUT', path: '/api/companies/{id}', desc: 'Update company details (name, phone, address)', role: 'Owner (X-User-Id) / Admin' },
    { method: 'DELETE', path: '/api/companies/{id}', desc: 'Delete company and associated profile', role: 'Owner (X-User-Id) / Admin' },
    { method: 'POST', path: '/api/companies/{id}/profile', desc: 'Create or update extended company profile', role: 'Owner (X-User-Id) / Admin' },
    { method: 'GET', path: '/api/companies/{id}/profile', desc: 'Get extended company profile details', role: 'Public / Authenticated' }
  ];

  return (
    <div className="microservice-info">
      {/* Header */}
      <div className="glass-panel" style={{ padding: '2.5rem', marginBottom: '2rem', position: 'relative', overflow: 'hidden' }}>
        <div style={{ position: 'absolute', top: 0, left: 0, right: 0, height: '4px', background: 'var(--accent-gradient)' }}></div>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1.5rem' }}>
          <div>
            <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem', background: 'rgba(99, 102, 241, 0.15)', border: '1px solid rgba(99, 102, 241, 0.3)', padding: '0.3rem 0.8rem', borderRadius: '9999px', fontSize: '0.8rem', color: '#a5b4fc', marginBottom: '0.75rem', fontWeight: 600 }}>
              <span>🚀 Member 3 Microservice Architecture</span>
            </div>
            <h1 style={{ fontSize: '2.2rem', marginBottom: '0.5rem' }}>Company Service (`company-service`)</h1>
            <p style={{ color: '#9ca3af', maxWidth: '720px', fontSize: '1rem' }}>
              Responsible for corporate employer registration, extended profile management, company directory exploration, and ownership verification within the distributed AI Recruitment Platform.
            </p>
          </div>

          <div style={{ display: 'flex', gap: '0.75rem' }}>
            <a
              href="http://localhost:8083/swagger-ui.html"
              target="_blank"
              rel="noreferrer"
              className="btn btn-primary"
            >
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>
              OpenAPI Swagger UI (Port 8083)
            </a>
          </div>
        </div>
      </div>

      {/* Grid Overview */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '1.5rem', marginBottom: '2rem' }}>
        <div className="glass-panel" style={{ padding: '1.5rem' }}>
          <div style={{ color: '#818cf8', fontWeight: 700, fontSize: '0.85rem', marginBottom: '0.5rem' }}>PORT & PROTOCOL</div>
          <h3 style={{ fontSize: '1.4rem', color: '#f3f4f6', marginBottom: '0.5rem' }}>Port 8083 / HTTP REST</h3>
          <p style={{ fontSize: '0.875rem', color: '#9ca3af' }}>
            Direct access on <code style={{ color: '#c7d2fe' }}>:8083</code> or routed via API Gateway on <code style={{ color: '#c7d2fe' }}>http://localhost:8080/api/companies</code>.
          </p>
        </div>

        <div className="glass-panel" style={{ padding: '1.5rem' }}>
          <div style={{ color: '#34d399', fontWeight: 700, fontSize: '0.85rem', marginBottom: '0.5rem' }}>DATABASE LAYER</div>
          <h3 style={{ fontSize: '1.4rem', color: '#f3f4f6', marginBottom: '0.5rem' }}>MySQL `company_db`</h3>
          <p style={{ fontSize: '0.875rem', color: '#9ca3af' }}>
            Containerized on port <code style={{ color: '#34d399' }}>3308:3306</code> with tables <code style={{ color: '#c7d2fe' }}>company</code> and <code style={{ color: '#c7d2fe' }}>company_profile</code>.
          </p>
        </div>

        <div className="glass-panel" style={{ padding: '1.5rem' }}>
          <div style={{ color: '#c084fc', fontWeight: 700, fontSize: '0.85rem', marginBottom: '0.5rem' }}>API SECURITY</div>
          <h3 style={{ fontSize: '1.4rem', color: '#f3f4f6', marginBottom: '0.5rem' }}>X-API-KEY + JWT Context</h3>
          <p style={{ fontSize: '0.875rem', color: '#9ca3af' }}>
            Protected by <code style={{ color: '#c7d2fe' }}>ApiKeyFilter</code> verifying internal service key and forwarded identity headers (<code style={{ color: '#c7d2fe' }}>X-User-Id</code>, <code style={{ color: '#c7d2fe' }}>X-User-Role</code>).
          </p>
        </div>
      </div>

      {/* Database Schema Section */}
      <div className="glass-panel" style={{ padding: '2rem', marginBottom: '2rem' }}>
        <h2 style={{ fontSize: '1.5rem', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#6366f1" strokeWidth="2"><ellipse cx="12" cy="5" rx="9" ry="3"></ellipse><path d="M21 12c0 1.66-4 3-9 3s-9-1.34-9-3"></path><path d="M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5"></path></svg>
          Relational Database Schema (`company_db`)
        </h2>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '1.5rem', marginTop: '1rem' }}>
          <div style={{ background: 'rgba(255,255,255,0.02)', padding: '1.25rem', borderRadius: '12px', border: '1px solid rgba(255,255,255,0.06)' }}>
            <h3 style={{ fontSize: '1.1rem', color: '#a5b4fc', marginBottom: '0.75rem' }}>Table: `company`</h3>
            <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '0.4rem', fontSize: '0.875rem', color: '#d1d5db' }}>
              <li><code style={{ color: '#f472b6' }}>id</code> : BIGINT (Primary Key, AUTO_INCREMENT)</li>
              <li><code style={{ color: '#38bdf8' }}>user_id</code> : BIGINT (Unique, FK to auth-service)</li>
              <li><code style={{ color: '#34d399' }}>company_name</code> : VARCHAR(255) NOT NULL</li>
              <li><code style={{ color: '#34d399' }}>email</code> : VARCHAR(255) UNIQUE NOT NULL</li>
              <li><code style={{ color: '#9ca3af' }}>phone</code> : VARCHAR(50)</li>
              <li><code style={{ color: '#9ca3af' }}>address</code> : VARCHAR(500)</li>
              <li><code style={{ color: '#9ca3af' }}>created_at</code> : TIMESTAMP NOT NULL</li>
              <li><code style={{ color: '#9ca3af' }}>updated_at</code> : TIMESTAMP</li>
            </ul>
          </div>

          <div style={{ background: 'rgba(255,255,255,0.02)', padding: '1.25rem', borderRadius: '12px', border: '1px solid rgba(255,255,255,0.06)' }}>
            <h3 style={{ fontSize: '1.1rem', color: '#a855f7', marginBottom: '0.75rem' }}>Table: `company_profile`</h3>
            <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '0.4rem', fontSize: '0.875rem', color: '#d1d5db' }}>
              <li><code style={{ color: '#f472b6' }}>id</code> : BIGINT (Primary Key, AUTO_INCREMENT)</li>
              <li><code style={{ color: '#38bdf8' }}>company_id</code> : BIGINT (Unique, FK to company)</li>
              <li><code style={{ color: '#34d399' }}>industry</code> : VARCHAR(255)</li>
              <li><code style={{ color: '#34d399' }}>company_size</code> : VARCHAR(50) (e.g. 51-200)</li>
              <li><code style={{ color: '#9ca3af' }}>website</code> : VARCHAR(255)</li>
              <li><code style={{ color: '#9ca3af' }}>description</code> : TEXT</li>
              <li><code style={{ color: '#9ca3af' }}>logo_url</code> : VARCHAR(500)</li>
            </ul>
          </div>
        </div>
      </div>

      {/* REST API Endpoints Table */}
      <div className="glass-panel" style={{ padding: '2rem' }}>
        <h2 style={{ fontSize: '1.5rem', marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#34d399" strokeWidth="2"><polyline points="16 18 22 12 16 6"></polyline><polyline points="8 6 2 12 8 18"></polyline></svg>
          Exposed REST API Endpoints
        </h2>

        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.9rem' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid rgba(255,255,255,0.1)', color: '#9ca3af' }}>
                <th style={{ padding: '0.75rem 1rem' }}>HTTP Method</th>
                <th style={{ padding: '0.75rem 1rem' }}>Endpoint Route</th>
                <th style={{ padding: '0.75rem 1rem' }}>Description</th>
                <th style={{ padding: '0.75rem 1rem' }}>Security & Authorization</th>
              </tr>
            </thead>
            <tbody>
              {endpoints.map((ep, idx) => {
                const methodColor = ep.method === 'POST' ? '#34d399' :
                                   ep.method === 'GET' ? '#38bdf8' :
                                   ep.method === 'PUT' ? '#f59e0b' : '#f43f5e';
                return (
                  <tr key={idx} style={{ borderBottom: '1px solid rgba(255,255,255,0.04)' }}>
                    <td style={{ padding: '0.75rem 1rem' }}>
                      <span style={{ fontWeight: 700, color: methodColor, background: `${methodColor}22`, padding: '0.2rem 0.5rem', borderRadius: '4px', fontSize: '0.8rem' }}>
                        {ep.method}
                      </span>
                    </td>
                    <td style={{ padding: '0.75rem 1rem', fontFamily: 'monospace', color: '#f3f4f6' }}>
                      {ep.path}
                    </td>
                    <td style={{ padding: '0.75rem 1rem', color: '#9ca3af' }}>
                      {ep.desc}
                    </td>
                    <td style={{ padding: '0.75rem 1rem', color: '#c7d2fe', fontSize: '0.85rem' }}>
                      {ep.role}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
