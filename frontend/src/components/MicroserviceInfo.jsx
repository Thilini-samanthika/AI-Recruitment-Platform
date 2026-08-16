import React from 'react';

export default function MicroserviceInfo() {
  const endpoints = [
    { method: 'POST', path: '/api/candidates', desc: 'Create candidate profile linked to Auth user' },
    { method: 'GET', path: '/api/candidates', desc: 'List all candidates' },
    { method: 'GET', path: '/api/candidates/{id}', desc: 'Get candidate by ID with skills, education, & experience' },
    { method: 'GET', path: '/api/candidates/me', desc: 'Get authenticated user candidate profile via X-User-Id' },
    { method: 'GET', path: '/api/candidates/user/{userId}', desc: 'Get candidate by Auth User ID' },
    { method: 'PUT', path: '/api/candidates/{id}', desc: 'Update candidate profile (validates user ownership)' },
    { method: 'DELETE', path: '/api/candidates/{id}', desc: 'Delete candidate profile and cascade children' },
    { method: 'POST', path: '/api/candidates/{id}/skills', desc: 'Add technical skill with proficiency level' },
    { method: 'GET', path: '/api/candidates/{id}/skills', desc: 'List candidate skills' },
    { method: 'DELETE', path: '/api/candidates/{id}/skills/{skillId}', desc: 'Delete candidate skill' },
    { method: 'POST', path: '/api/candidates/{id}/education', desc: 'Add education history' },
    { method: 'GET', path: '/api/candidates/{id}/education', desc: 'List education records' },
    { method: 'DELETE', path: '/api/candidates/{id}/education/{eduId}', desc: 'Delete education record' },
    { method: 'POST', path: '/api/candidates/{id}/experience', desc: 'Add work experience record' },
    { method: 'GET', path: '/api/candidates/{id}/experience', desc: 'List work experience records' },
    { method: 'DELETE', path: '/api/candidates/{id}/experience/{expId}', desc: 'Delete work experience record' },
  ];

  return (
    <div className="glass-panel section-card">
      <div className="section-header">
        <div>
          <h2>Member 2: Candidate Service Specifications</h2>
          <p style={{ color: '#9ca3af', fontSize: '0.9rem' }}>
            Spring Boot 3.3.5 microservice running on port 8082, routed through API Gateway on port 8080.
          </p>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '1.25rem', marginBottom: '2rem' }}>
        <div className="timeline-card">
          <h3 style={{ color: '#818cf8', fontSize: '1rem', marginBottom: '0.5rem' }}>Service Details</h3>
          <div style={{ fontSize: '0.875rem', color: '#cbd5e1', lineHeight: '1.8' }}>
            <div><strong>Microservice:</strong> candidate-service</div>
            <div><strong>Port:</strong> 8082</div>
            <div><strong>Database:</strong> candidate_db (MySQL 3307 / H2)</div>
            <div><strong>Gateway Route:</strong> /api/candidates/**</div>
          </div>
        </div>

        <div className="timeline-card">
          <h3 style={{ color: '#34d399', fontSize: '1rem', marginBottom: '0.5rem' }}>Security Requirements</h3>
          <div style={{ fontSize: '0.875rem', color: '#cbd5e1', lineHeight: '1.8' }}>
            <div><strong>Internal API Key:</strong> X-API-KEY: CANDIDATE_SERVICE_KEY</div>
            <div><strong>Gateway Identity:</strong> X-User-Id, X-User-Role, X-User-Email</div>
            <div><strong>Ownership Checks:</strong> Validates user matching or ROLE_ADMIN</div>
          </div>
        </div>

        <div className="timeline-card">
          <h3 style={{ color: '#38bdf8', fontSize: '1rem', marginBottom: '0.5rem' }}>Swagger & Documentation</h3>
          <div style={{ fontSize: '0.875rem', color: '#cbd5e1', lineHeight: '1.8' }}>
            <div><strong>Swagger UI:</strong> <a href="http://localhost:8082/swagger-ui.html" target="_blank" rel="noreferrer" style={{ color: '#38bdf8' }}>http://localhost:8082/swagger-ui.html</a></div>
            <div><strong>OpenAPI 3.0:</strong> <a href="http://localhost:8082/v3/api-docs" target="_blank" rel="noreferrer" style={{ color: '#38bdf8' }}>http://localhost:8082/v3/api-docs</a></div>
          </div>
        </div>
      </div>

      <h3 style={{ fontSize: '1.1rem', marginBottom: '1rem' }}>Exposed API Endpoints</h3>
      <div style={{ overflowX: 'auto' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.875rem' }}>
          <thead>
            <tr style={{ borderBottom: '1px solid var(--border-glass)', textAlign: 'left' }}>
              <th style={{ padding: '0.75rem', color: '#9ca3af' }}>Method</th>
              <th style={{ padding: '0.75rem', color: '#9ca3af' }}>Endpoint Path</th>
              <th style={{ padding: '0.75rem', color: '#9ca3af' }}>Description</th>
            </tr>
          </thead>
          <tbody>
            {endpoints.map((ep, idx) => (
              <tr key={idx} style={{ borderBottom: '1px solid rgba(255,255,255,0.03)' }}>
                <td style={{ padding: '0.75rem' }}>
                  <span className={`skill-level-badge level-${ep.method === 'POST' ? 'BEGINNER' : ep.method === 'GET' ? 'INTERMEDIATE' : ep.method === 'PUT' ? 'ADVANCED' : 'EXPERT'}`}>
                    {ep.method}
                  </span>
                </td>
                <td style={{ padding: '0.75rem', fontFamily: 'monospace', color: '#c7d2fe' }}>{ep.path}</td>
                <td style={{ padding: '0.75rem', color: '#cbd5e1' }}>{ep.desc}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
