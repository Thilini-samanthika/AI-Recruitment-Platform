import React, { useState } from 'react';

export default function CandidateDirectory({ candidates = [], onSelectCandidate }) {
  const [searchTerm, setSearchTerm] = useState('');

  const filteredCandidates = candidates.filter(c => {
    const term = searchTerm.toLowerCase();
    const nameMatch = c.fullName?.toLowerCase().includes(term);
    const headlineMatch = c.headline?.toLowerCase().includes(term);
    const skillMatch = c.skills?.some(s => s.skillName.toLowerCase().includes(term));
    return nameMatch || headlineMatch || skillMatch;
  });

  return (
    <div className="glass-panel section-card">
      <div className="section-header" style={{ flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <h2>Candidate Talent Pool</h2>
          <p style={{ color: '#9ca3af', fontSize: '0.9rem' }}>
            Browse registered candidates available for matching with job opportunities
          </p>
        </div>
        <div style={{ minWidth: '280px', flex: '1', maxWidth: '400px' }}>
          <input
            type="text"
            className="form-input"
            placeholder="Search by name, title, or skill..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      {filteredCandidates.length === 0 ? (
        <div className="empty-state">
          <p>No candidates found matching "{searchTerm}"</p>
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: '1.25rem', marginTop: '1.25rem' }}>
          {filteredCandidates.map((cand) => (
            <div key={cand.id} className="timeline-card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
              <div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '0.75rem' }}>
                  <div className="avatar-wrapper" style={{ width: '48px', height: '48px', fontSize: '1.2rem' }}>
                    {cand.fullName ? cand.fullName[0] : 'C'}
                  </div>
                  <div>
                    <h3 style={{ fontSize: '1.15rem' }}>{cand.fullName}</h3>
                    <div style={{ color: '#a5b4fc', fontSize: '0.85rem' }}>{cand.headline || 'Software Professional'}</div>
                  </div>
                </div>

                <p style={{ fontSize: '0.85rem', color: '#9ca3af', marginBottom: '1rem', lineClamp: 2, display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>
                  {cand.summary || 'No summary provided.'}
                </p>

                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.4rem', marginBottom: '1rem' }}>
                  {cand.skills?.slice(0, 4).map(s => (
                    <span key={s.id} className="skill-level-badge level-INTERMEDIATE" style={{ fontSize: '0.75rem' }}>
                      {s.skillName}
                    </span>
                  ))}
                  {(cand.skills?.length || 0) > 4 && (
                    <span style={{ fontSize: '0.75rem', color: '#6b7280', alignSelf: 'center' }}>
                      +{(cand.skills.length - 4)} more
                    </span>
                  )}
                </div>
              </div>

              <div style={{ borderTop: '1px solid var(--border-glass)', paddingTop: '0.75rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ fontSize: '0.75rem', color: '#6b7280' }}>
                  {cand.experiences?.length || 0} roles &bull; {cand.educations?.length || 0} degrees
                </span>
                <button className="btn btn-primary btn-sm" onClick={() => onSelectCandidate(cand)}>
                  View Profile
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
