import React, { useState, useEffect } from 'react';
import { jobApi } from '../services/jobApi';

export default function JobDirectory({ onMatchJobWithResume, onShowToast }) {
  const [jobs, setJobs] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedType, setSelectedType] = useState('ALL');
  const [selectedJob, setSelectedJob] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchJobs = async () => {
      setLoading(true);
      try {
        const res = await jobApi.listJobs();
        if (res.success && res.data) {
          setJobs(res.data);
          if (res.data.length > 0) setSelectedJob(res.data[0]);
        }
      } catch (err) {
        console.error('Failed to load jobs:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchJobs();
  }, []);

  const filteredJobs = jobs.filter(job => {
    const matchesSearch =
      job.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      job.companyName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (job.requiredSkills || []).some(s => s.toLowerCase().includes(searchTerm.toLowerCase()));

    const matchesType = selectedType === 'ALL' || (job.jobType && job.jobType.toLowerCase().includes(selectedType.toLowerCase()));
    return matchesSearch && matchesType;
  });

  return (
    <div className="job-directory-view">
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.4rem', background: 'rgba(59, 130, 246, 0.15)', color: '#60a5fa', padding: '0.2rem 0.65rem', borderRadius: '6px', fontSize: '0.75rem', fontWeight: 700, marginBottom: '0.4rem' }}>
            <span>💼 Job Service & Applications (Member 4)</span>
            <span>&bull; Port 8084</span>
          </div>
          <h2 style={{ fontSize: '1.8rem', fontWeight: 800, margin: 0 }}>Open Positions & Job Directory</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', margin: '0.25rem 0 0' }}>
            Browse active job postings, check required skills, and instantly match with your uploaded resume.
          </p>
        </div>

        {/* Search & Filters */}
        <div style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap' }}>
          <input
            type="text"
            placeholder="Search by title, company, skill..."
            value={searchTerm}
            onChange={e => setSearchTerm(e.target.value)}
            style={{
              padding: '0.6rem 0.9rem',
              borderRadius: '8px',
              border: '1px solid var(--border-color)',
              background: 'var(--surface-card)',
              color: 'var(--text-main)',
              fontSize: '0.85rem',
              minWidth: '240px'
            }}
          />

          <select
            value={selectedType}
            onChange={e => setSelectedType(e.target.value)}
            style={{
              padding: '0.6rem 0.9rem',
              borderRadius: '8px',
              border: '1px solid var(--border-color)',
              background: 'var(--surface-card)',
              color: 'var(--text-main)',
              fontSize: '0.85rem'
            }}
          >
            <option value="ALL">All Types</option>
            <option value="Full-Time">Full-Time</option>
            <option value="Remote">Remote</option>
            <option value="Contract">Contract</option>
          </select>
        </div>
      </div>

      {/* Main Grid: Left Jobs List, Right Details Card */}
      <div style={{ display: 'grid', gridTemplateColumns: 'minmax(320px, 420px) 1fr', gap: '1.5rem', alignItems: 'start' }}>
        {/* Left: Job Cards */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.85rem' }}>
          {filteredJobs.length === 0 ? (
            <div style={{ background: 'var(--surface-card)', padding: '2rem', borderRadius: '12px', textAlign: 'center', color: 'var(--text-muted)' }}>
              No job postings matched your search.
            </div>
          ) : (
            filteredJobs.map(job => {
              const isSelected = selectedJob && selectedJob.id === job.id;
              return (
                <div
                  key={job.id}
                  onClick={() => setSelectedJob(job)}
                  style={{
                    background: isSelected ? 'rgba(59, 130, 246, 0.12)' : 'var(--surface-card)',
                    border: `1px solid ${isSelected ? '#3b82f6' : 'var(--border-color)'}`,
                    borderRadius: '12px',
                    padding: '1.15rem',
                    cursor: 'pointer',
                    transition: 'all 0.15s'
                  }}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.35rem' }}>
                    <h4 style={{ margin: 0, fontSize: '1rem', fontWeight: 700, color: isSelected ? '#93c5fd' : 'var(--text-main)' }}>
                      {job.title}
                    </h4>
                    <span style={{ fontSize: '0.72rem', background: 'rgba(59, 130, 246, 0.15)', color: '#60a5fa', padding: '0.15rem 0.45rem', borderRadius: '4px', fontWeight: 700 }}>
                      {job.jobType || 'Full-Time'}
                    </span>
                  </div>

                  <div style={{ fontSize: '0.82rem', color: '#c084fc', fontWeight: 600, marginBottom: '0.5rem' }}>
                    🏢 {job.companyName} &bull; <span style={{ color: 'var(--text-muted)' }}>{job.location}</span>
                  </div>

                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.35rem', marginBottom: '0.65rem' }}>
                    {(job.requiredSkills || []).slice(0, 4).map((s, idx) => (
                      <span key={idx} style={{ background: 'var(--surface-subtle)', padding: '0.15rem 0.4rem', borderRadius: '4px', fontSize: '0.72rem', color: 'var(--text-muted)' }}>
                        {s}
                      </span>
                    ))}
                    {(job.requiredSkills || []).length > 4 && (
                      <span style={{ fontSize: '0.72rem', color: 'var(--text-muted)' }}>
                        +{(job.requiredSkills || []).length - 4} more
                      </span>
                    )}
                  </div>

                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.75rem', color: '#34d399', fontWeight: 600 }}>
                    <span>{job.salaryRange || '$120k - $160k'}</span>
                    <span style={{ color: 'var(--text-muted)' }}>{job.experienceLevel}</span>
                  </div>
                </div>
              );
            })
          )}
        </div>

        {/* Right: Selected Job Full Details */}
        {selectedJob ? (
          <div style={{ background: 'var(--surface-card)', border: '1px solid var(--border-color)', borderRadius: '14px', padding: '1.75rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem', flexWrap: 'wrap', gap: '0.75rem' }}>
              <div>
                <span style={{ fontSize: '0.85rem', color: '#60a5fa', fontWeight: 700 }}>{selectedJob.companyName}</span>
                <h3 style={{ margin: '0.2rem 0 0.4rem', fontSize: '1.4rem', fontWeight: 800 }}>{selectedJob.title}</h3>
                <div style={{ display: 'flex', gap: '0.75rem', fontSize: '0.82rem', color: 'var(--text-muted)' }}>
                  <span>📍 {selectedJob.location}</span>
                  <span>💼 {selectedJob.jobType}</span>
                  <span>💰 {selectedJob.salaryRange}</span>
                </div>
              </div>

              <button
                className="btn-primary"
                onClick={() => {
                  if (onMatchJobWithResume) onMatchJobWithResume(selectedJob);
                }}
                style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', padding: '0.65rem 1.15rem', borderRadius: '8px', fontSize: '0.85rem' }}
              >
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5"><circle cx="12" cy="12" r="10"></circle><path d="m4.93 4.93 4.24 4.24"></path><path d="m14.83 9.17 4.24-4.24"></path><path d="m14.83 14.83 4.24 4.24"></path><path d="m9.17 14.83-4.24 4.24"></path><circle cx="12" cy="12" r="4"></circle></svg>
                AI Match with My Resume
              </button>
            </div>

            <hr style={{ border: 'none', borderTop: '1px solid var(--border-color)', margin: '1.25rem 0' }} />

            <div style={{ marginBottom: '1.5rem' }}>
              <h4 style={{ fontSize: '0.95rem', fontWeight: 700, margin: '0 0 0.5rem' }}>Role Description</h4>
              <p style={{ fontSize: '0.9rem', lineHeight: 1.6, color: 'var(--text-muted)', margin: 0 }}>
                {selectedJob.description}
              </p>
            </div>

            <div style={{ marginBottom: '1.5rem' }}>
              <h4 style={{ fontSize: '0.95rem', fontWeight: 700, margin: '0 0 0.75rem' }}>Required Skills & Proficiencies</h4>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
                {(selectedJob.requiredSkills || []).map((skill, idx) => (
                  <span
                    key={idx}
                    style={{
                      background: 'rgba(59, 130, 246, 0.15)',
                      border: '1px solid rgba(59, 130, 246, 0.35)',
                      color: '#93c5fd',
                      padding: '0.35rem 0.75rem',
                      borderRadius: '999px',
                      fontSize: '0.82rem',
                      fontWeight: 600
                    }}
                  >
                    {skill}
                  </span>
                ))}
              </div>
            </div>

            <div style={{ background: 'var(--surface-subtle)', padding: '1rem', borderRadius: '10px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <span style={{ display: 'block', fontSize: '0.75rem', color: 'var(--text-muted)' }}>Experience Expectation</span>
                <span style={{ fontSize: '0.9rem', fontWeight: 700 }}>{selectedJob.experienceLevel}</span>
              </div>
              <button
                className="btn-secondary"
                onClick={() => {
                  if (onShowToast) onShowToast(`Application submitted for ${selectedJob.title}!`);
                }}
                style={{ fontSize: '0.82rem', padding: '0.45rem 1rem' }}
              >
                Direct Quick Apply
              </button>
            </div>
          </div>
        ) : (
          <div style={{ background: 'var(--surface-card)', border: '1px solid var(--border-color)', borderRadius: '14px', padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>
            Select a job from the directory to view complete requirements.
          </div>
        )}
      </div>
    </div>
  );
}
