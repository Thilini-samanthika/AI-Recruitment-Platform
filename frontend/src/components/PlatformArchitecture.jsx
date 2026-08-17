import React, { useState } from 'react';
import { apiClient } from '../services/apiClient';

export default function PlatformArchitecture() {
  const [selectedEndpoint, setSelectedEndpoint] = useState('/api/resume/health');
  const [testMethod, setTestMethod] = useState('GET');
  const [requestBody, setRequestBody] = useState('');
  const [responseResult, setResponseResult] = useState(null);
  const [isCalling, setIsCalling] = useState(false);

  const services = [
    {
      id: 'gateway',
      name: 'API Gateway',
      member: 'Member 1',
      port: 8080,
      tech: 'Spring Cloud Gateway Reactive',
      routes: ['/api/auth/**', '/api/candidates/**', '/api/companies/**', '/api/jobs/**', '/api/resume/**', '/api/match/**', '/api/recommendations/**'],
      swagger: 'http://localhost:8080/swagger-ui.html'
    },
    {
      id: 'auth',
      name: 'Auth Service',
      member: 'Member 1',
      port: 8081,
      tech: 'Spring Boot 3.3.5 & JWT (jjwt 0.12.6)',
      routes: ['POST /api/auth/register', 'POST /api/auth/login', 'GET /api/auth/validate', 'GET /api/auth/users/{id}'],
      swagger: 'http://localhost:8081/swagger-ui.html'
    },
    {
      id: 'candidate',
      name: 'Candidate Service',
      member: 'Member 2',
      port: 8082,
      tech: 'Spring Boot, JPA, MySQL (candidate_db)',
      routes: ['GET /api/candidates', 'POST /api/candidates', 'GET /api/candidates/{id}', 'POST /api/candidates/{id}/skills'],
      swagger: 'http://localhost:8082/swagger-ui.html'
    },
    {
      id: 'company',
      name: 'Company Service',
      member: 'Member 3',
      port: 8083,
      tech: 'Spring Boot, JPA, MySQL (company_db)',
      routes: ['GET /api/companies', 'POST /api/companies', 'PUT /api/companies/{id}', 'POST /api/companies/{id}/profile'],
      swagger: 'http://localhost:8083/swagger-ui.html'
    },
    {
      id: 'job',
      name: 'Job Service',
      member: 'Member 4',
      port: 8084,
      tech: 'Spring Boot, JPA, MySQL (job_db)',
      routes: ['GET /api/jobs', 'POST /api/jobs', 'GET /api/jobs/{id}', 'GET /api/jobs/company/{companyId}'],
      swagger: 'http://localhost:8084/swagger-ui.html'
    },
    {
      id: 'ai',
      name: 'AI Resume Service',
      member: 'Member 5 (Me)',
      port: 8085,
      tech: 'Spring Boot 3.3.5, Apache PDFBox, Apache POI, MySQL (ai_db)',
      routes: ['POST /api/resume/upload', 'POST /api/resume/extract/{id}', 'GET /api/resume/{candidateId}', 'POST /api/match', 'GET /api/recommendations/{candidateId}'],
      swagger: 'http://localhost:8085/swagger-ui.html',
      isLead: true
    }
  ];

  const handleTestCall = async () => {
    setIsCalling(true);
    setResponseResult(null);

    try {
      let bodyData = null;
      if (testMethod === 'POST' && requestBody.trim()) {
        try {
          bodyData = JSON.parse(requestBody);
        } catch (e) {
          bodyData = requestBody;
        }
      }

      let res;
      if (testMethod === 'GET') {
        res = await apiClient.get(selectedEndpoint);
      } else {
        res = await apiClient.post(selectedEndpoint, bodyData || {});
      }

      setResponseResult(res);
    } catch (err) {
      setResponseResult({ success: false, error: err.message });
    } finally {
      setIsCalling(false);
    }
  };

  const handleSelectPreset = (ep, method, body = '') => {
    setSelectedEndpoint(ep);
    setTestMethod(method);
    setRequestBody(body ? JSON.stringify(body, null, 2) : '');
  };

  return (
    <div className="architecture-view">
      {/* Header */}
      <div style={{ marginBottom: '2rem' }}>
        <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.4rem', background: 'rgba(168, 85, 247, 0.15)', color: '#c084fc', padding: '0.2rem 0.65rem', borderRadius: '6px', fontSize: '0.75rem', fontWeight: 700, marginBottom: '0.4rem' }}>
          <span>🌐 5-Member Distributed Microservices Architecture</span>
        </div>
        <h2 style={{ fontSize: '1.8rem', fontWeight: 800, margin: 0 }}>System Topology & Interactive API Explorer</h2>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', margin: '0.25rem 0 0' }}>
          Real-time orchestration overview of all 5 team microservices routing through the reactive Spring Cloud API Gateway.
        </p>
      </div>

      {/* Services Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '1.25rem', marginBottom: '2.5rem' }}>
        {services.map(svc => (
          <div
            key={svc.id}
            style={{
              background: svc.isLead ? 'linear-gradient(135deg, rgba(88, 28, 135, 0.3) 0%, rgba(17, 24, 39, 0.9) 100%)' : 'var(--surface-card)',
              border: `1px solid ${svc.isLead ? '#a855f7' : 'var(--border-color)'}`,
              borderRadius: '14px',
              padding: '1.25rem'
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.6rem' }}>
              <div>
                <span style={{ fontSize: '0.75rem', color: svc.isLead ? '#c084fc' : 'var(--text-muted)', fontWeight: 700 }}>
                  {svc.member}
                </span>
                <h3 style={{ margin: '0.1rem 0', fontSize: '1.1rem', fontWeight: 800 }}>{svc.name}</h3>
              </div>
              <span style={{
                background: 'rgba(16, 185, 129, 0.15)',
                color: '#34d399',
                padding: '0.2rem 0.5rem',
                borderRadius: '6px',
                fontSize: '0.75rem',
                fontWeight: 700
              }}>
                Port: {svc.port}
              </span>
            </div>

            <p style={{ fontSize: '0.78rem', color: 'var(--text-muted)', margin: '0 0 0.85rem' }}>
              ⚙️ {svc.tech}
            </p>

            <div style={{ marginBottom: '1rem' }}>
              <span style={{ fontSize: '0.72rem', color: 'var(--text-muted)', display: 'block', marginBottom: '0.35rem', fontWeight: 600 }}>
                Service Routes & Endpoints:
              </span>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
                {svc.routes.slice(0, 3).map((r, idx) => (
                  <code key={idx} style={{ background: 'var(--surface-subtle)', padding: '0.2rem 0.4rem', borderRadius: '4px', fontSize: '0.72rem', color: '#e2e8f0', display: 'block', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                    {r}
                  </code>
                ))}
              </div>
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', paddingTop: '0.5rem', borderTop: '1px solid var(--border-color)' }}>
              <a
                href={svc.swagger}
                target="_blank"
                rel="noreferrer"
                style={{ fontSize: '0.75rem', color: '#60a5fa', textDecoration: 'none', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '0.3rem' }}
              >
                <span>Swagger UI</span>
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5"><path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"></path><polyline points="15 3 21 3 21 9"></polyline><line x1="10" y1="14" x2="21" y2="3"></line></svg>
              </a>
              <span style={{ fontSize: '0.72rem', color: '#34d399', fontWeight: 600 }}>
                ● Active via Gateway
              </span>
            </div>
          </div>
        ))}
      </div>

      {/* Interactive API Request Simulator */}
      <div style={{ background: 'var(--surface-card)', border: '1px solid var(--border-color)', borderRadius: '16px', padding: '1.75rem' }}>
        <h3 style={{ fontSize: '1.2rem', fontWeight: 800, margin: '0 0 0.4rem' }}>
          ⚡ Interactive Live API Request Simulator
        </h3>
        <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', margin: '0 0 1.25rem' }}>
          Execute live HTTP requests against the backend microservices through the API Gateway (port 8080) or direct instances.
        </p>

        {/* Preset Endpoints Chips */}
        <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap', marginBottom: '1.25rem' }}>
          <button
            className="btn-secondary"
            onClick={() => handleSelectPreset('/api/resume/health', 'GET')}
            style={{ fontSize: '0.75rem', padding: '0.35rem 0.65rem' }}
          >
            GET /api/resume/health (AI Service)
          </button>
          <button
            className="btn-secondary"
            onClick={() => handleSelectPreset('/api/recommendations/1', 'GET')}
            style={{ fontSize: '0.75rem', padding: '0.35rem 0.65rem' }}
          >
            GET /api/recommendations/1
          </button>
          <button
            className="btn-secondary"
            onClick={() => handleSelectPreset('/api/match', 'POST', {
              resumeId: 1,
              jobId: 101,
              jobTitle: "Senior Full Stack Java Engineer",
              requiredSkills: ["Java", "Spring Boot", "React", "MySQL", "Docker"]
            })}
            style={{ fontSize: '0.75rem', padding: '0.35rem 0.65rem' }}
          >
            POST /api/match
          </button>
          <button
            className="btn-secondary"
            onClick={() => handleSelectPreset('/api/jobs', 'GET')}
            style={{ fontSize: '0.75rem', padding: '0.35rem 0.65rem' }}
          >
            GET /api/jobs (Job Service)
          </button>
          <button
            className="btn-secondary"
            onClick={() => handleSelectPreset('/api/companies', 'GET')}
            style={{ fontSize: '0.75rem', padding: '0.35rem 0.65rem' }}
          >
            GET /api/companies (Company Service)
          </button>
        </div>

        {/* Request Config Row */}
        <div style={{ display: 'flex', gap: '0.75rem', marginBottom: '1rem', flexWrap: 'wrap' }}>
          <select
            value={testMethod}
            onChange={e => setTestMethod(e.target.value)}
            style={{ padding: '0.65rem 0.85rem', borderRadius: '8px', background: 'var(--surface-subtle)', border: '1px solid var(--border-color)', color: 'var(--text-main)', fontWeight: 700, fontSize: '0.85rem' }}
          >
            <option value="GET">GET</option>
            <option value="POST">POST</option>
          </select>

          <input
            type="text"
            value={selectedEndpoint}
            onChange={e => setSelectedEndpoint(e.target.value)}
            style={{ flex: 1, minWidth: '220px', padding: '0.65rem 0.85rem', borderRadius: '8px', background: 'var(--surface-subtle)', border: '1px solid var(--border-color)', color: 'var(--text-main)', fontFamily: 'monospace', fontSize: '0.85rem' }}
          />

          <button
            className="btn-primary"
            onClick={handleTestCall}
            disabled={isCalling}
            style={{ padding: '0.65rem 1.25rem', borderRadius: '8px', fontWeight: 700, fontSize: '0.85rem' }}
          >
            {isCalling ? 'Sending...' : 'Send Request'}
          </button>
        </div>

        {testMethod === 'POST' && (
          <div style={{ marginBottom: '1rem' }}>
            <label style={{ display: 'block', fontSize: '0.78rem', fontWeight: 600, marginBottom: '0.35rem', color: 'var(--text-muted)' }}>
              Request JSON Payload
            </label>
            <textarea
              rows="4"
              value={requestBody}
              onChange={e => setRequestBody(e.target.value)}
              placeholder='{ "key": "value" }'
              style={{ width: '100%', padding: '0.75rem', borderRadius: '8px', background: 'var(--surface-subtle)', border: '1px solid var(--border-color)', color: 'var(--text-main)', fontFamily: 'monospace', fontSize: '0.8rem' }}
            ></textarea>
          </div>
        )}

        {/* Live Response Box */}
        {responseResult && (
          <div style={{ marginTop: '1.25rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.4rem' }}>
              <span style={{ fontSize: '0.78rem', fontWeight: 700, color: responseResult.success ? '#34d399' : '#f87171' }}>
                {responseResult.success ? '● 200 OK — Payload Received' : '● Response Output'}
              </span>
              <span style={{ fontSize: '0.72rem', color: 'var(--text-muted)' }}>
                Time: {new Date().toLocaleTimeString()}
              </span>
            </div>
            <pre style={{
              background: '#090d16',
              border: '1px solid var(--border-color)',
              borderRadius: '8px',
              padding: '1rem',
              color: '#38bdf8',
              fontFamily: 'monospace',
              fontSize: '0.8rem',
              maxHeight: '300px',
              overflowY: 'auto',
              whiteSpace: 'pre-wrap'
            }}>
              {JSON.stringify(responseResult, null, 2)}
            </pre>
          </div>
        )}
      </div>
    </div>
  );
}
