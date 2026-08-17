import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';

export default function AuthModal({ isOpen, onClose, initialMode = 'login' }) {
  const { login, register } = useAuth();
  const [mode, setMode] = useState(initialMode); // 'login' | 'register'
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('ROLE_CANDIDATE'); // 'ROLE_CANDIDATE' | 'ROLE_COMPANY' | 'ROLE_ADMIN'
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMsg('');
    setSuccessMsg('');
    setLoading(true);

    try {
      if (mode === 'login') {
        const res = await login(email, password);
        if (!res.success) {
          setErrorMsg(res.message || 'Invalid credentials');
        } else {
          setSuccessMsg('Logged in successfully!');
        }
      } else {
        const res = await register(email, password, role);
        if (!res.success) {
          setErrorMsg(res.message || 'Registration failed');
        } else {
          setSuccessMsg('Account registered successfully!');
        }
      }
    } catch (err) {
      setErrorMsg(err.message || 'An error occurred');
    } finally {
      setLoading(false);
    }
  };

  const handleDemoFill = (demoRole) => {
    if (demoRole === 'ROLE_CANDIDATE') {
      setEmail('alex.mercer@recruitment.io');
      setPassword('password123');
      setRole('ROLE_CANDIDATE');
    } else if (demoRole === 'ROLE_COMPANY') {
      setEmail('recruiter@acmetech.io');
      setPassword('password123');
      setRole('ROLE_COMPANY');
    } else {
      setEmail('admin@recruitment.io');
      setPassword('password123');
      setRole('ROLE_ADMIN');
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content auth-modal-card" onClick={e => e.stopPropagation()} style={{ maxWidth: '480px' }}>
        <div className="modal-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <div className="badge-icon-wrap" style={{ background: 'rgba(168, 85, 247, 0.15)', color: '#c084fc' }}>
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
              </svg>
            </div>
            <div>
              <h2 style={{ fontSize: '1.25rem', fontWeight: 700, margin: 0 }}>
                {mode === 'login' ? 'Sign In to Platform' : 'Create Platform Account'}
              </h2>
              <p style={{ margin: 0, fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                {mode === 'login' ? 'Access your AI-powered recruitment portal' : 'Join as Candidate or Company Recruiter'}
              </p>
            </div>
          </div>
          <button className="modal-close-btn" onClick={onClose}>&times;</button>
        </div>

        {/* Tab Toggle */}
        <div className="auth-tab-group" style={{ display: 'flex', margin: '1rem 0 1.5rem', background: 'var(--surface-subtle)', borderRadius: '8px', padding: '4px' }}>
          <button
            type="button"
            className={`tab-btn-pill ${mode === 'login' ? 'active' : ''}`}
            onClick={() => { setMode('login'); setErrorMsg(''); }}
            style={{ flex: 1, padding: '0.5rem', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 600, background: mode === 'login' ? 'var(--primary)' : 'transparent', color: mode === 'login' ? '#fff' : 'var(--text-muted)' }}
          >
            Sign In
          </button>
          <button
            type="button"
            className={`tab-btn-pill ${mode === 'register' ? 'active' : ''}`}
            onClick={() => { setMode('register'); setErrorMsg(''); }}
            style={{ flex: 1, padding: '0.5rem', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 600, background: mode === 'register' ? 'var(--primary)' : 'transparent', color: mode === 'register' ? '#fff' : 'var(--text-muted)' }}
          >
            Register
          </button>
        </div>

        {errorMsg && (
          <div className="alert-box error" style={{ background: 'rgba(239, 68, 68, 0.15)', border: '1px solid rgba(239, 68, 68, 0.3)', color: '#fca5a5', padding: '0.75rem', borderRadius: '8px', marginBottom: '1rem', fontSize: '0.875rem' }}>
            {errorMsg}
          </div>
        )}

        {successMsg && (
          <div className="alert-box success" style={{ background: 'rgba(16, 185, 129, 0.15)', border: '1px solid rgba(16, 185, 129, 0.3)', color: '#6ee7b7', padding: '0.75rem', borderRadius: '8px', marginBottom: '1rem', fontSize: '0.875rem' }}>
            {successMsg}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group" style={{ marginBottom: '1rem' }}>
            <label style={{ display: 'block', marginBottom: '0.35rem', fontSize: '0.875rem', fontWeight: 600 }}>
              Email Address
            </label>
            <input
              type="email"
              className="form-input"
              required
              placeholder="e.g. alex@recruitment.io"
              value={email}
              onChange={e => setEmail(e.target.value)}
              style={{ width: '100%', padding: '0.65rem 0.85rem', borderRadius: '8px', background: 'var(--surface-card)', border: '1px solid var(--border-color)', color: 'var(--text-main)' }}
            />
          </div>

          <div className="form-group" style={{ marginBottom: '1rem' }}>
            <label style={{ display: 'block', marginBottom: '0.35rem', fontSize: '0.875rem', fontWeight: 600 }}>
              Password
            </label>
            <input
              type="password"
              className="form-input"
              required
              placeholder="••••••••"
              value={password}
              onChange={e => setPassword(e.target.value)}
              style={{ width: '100%', padding: '0.65rem 0.85rem', borderRadius: '8px', background: 'var(--surface-card)', border: '1px solid var(--border-color)', color: 'var(--text-main)' }}
            />
          </div>

          {mode === 'register' && (
            <div className="form-group" style={{ marginBottom: '1.25rem' }}>
              <label style={{ display: 'block', marginBottom: '0.35rem', fontSize: '0.875rem', fontWeight: 600 }}>
                Select Account Role
              </label>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.5rem' }}>
                <button
                  type="button"
                  onClick={() => setRole('ROLE_CANDIDATE')}
                  style={{
                    padding: '0.65rem',
                    borderRadius: '8px',
                    border: `1px solid ${role === 'ROLE_CANDIDATE' ? '#a855f7' : 'var(--border-color)'}`,
                    background: role === 'ROLE_CANDIDATE' ? 'rgba(168, 85, 247, 0.15)' : 'var(--surface-subtle)',
                    color: role === 'ROLE_CANDIDATE' ? '#d8b4fe' : 'var(--text-muted)',
                    cursor: 'pointer',
                    fontSize: '0.85rem',
                    fontWeight: 600
                  }}
                >
                  👤 Candidate / Job Seeker
                </button>
                <button
                  type="button"
                  onClick={() => setRole('ROLE_COMPANY')}
                  style={{
                    padding: '0.65rem',
                    borderRadius: '8px',
                    border: `1px solid ${role === 'ROLE_COMPANY' ? '#a855f7' : 'var(--border-color)'}`,
                    background: role === 'ROLE_COMPANY' ? 'rgba(168, 85, 247, 0.15)' : 'var(--surface-subtle)',
                    color: role === 'ROLE_COMPANY' ? '#d8b4fe' : 'var(--text-muted)',
                    cursor: 'pointer',
                    fontSize: '0.85rem',
                    fontWeight: 600
                  }}
                >
                  🏢 Company / Employer
                </button>
              </div>
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="btn-primary"
            style={{
              width: '100%',
              padding: '0.75rem',
              borderRadius: '8px',
              fontWeight: 600,
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
              gap: '0.5rem',
              cursor: loading ? 'not-allowed' : 'pointer'
            }}
          >
            {loading ? 'Processing...' : (mode === 'login' ? 'Sign In' : 'Create Account')}
          </button>
        </form>

        {/* Quick Demo Login Preset Buttons */}
        <div style={{ marginTop: '1.5rem', borderTop: '1px solid var(--border-color)', paddingTop: '1rem' }}>
          <p style={{ fontSize: '0.78rem', color: 'var(--text-muted)', margin: '0 0 0.5rem', textAlign: 'center' }}>
            ⚡ Quick Demo Logins (Click to autofill):
          </p>
          <div style={{ display: 'flex', gap: '0.4rem', justifyContent: 'center' }}>
            <button
              type="button"
              onClick={() => handleDemoFill('ROLE_CANDIDATE')}
              style={{ fontSize: '0.75rem', padding: '0.35rem 0.65rem', borderRadius: '6px', background: 'var(--surface-subtle)', border: '1px solid var(--border-color)', color: 'var(--text-main)', cursor: 'pointer' }}
            >
              Demo Candidate
            </button>
            <button
              type="button"
              onClick={() => handleDemoFill('ROLE_COMPANY')}
              style={{ fontSize: '0.75rem', padding: '0.35rem 0.65rem', borderRadius: '6px', background: 'var(--surface-subtle)', border: '1px solid var(--border-color)', color: 'var(--text-main)', cursor: 'pointer' }}
            >
              Demo Company
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
