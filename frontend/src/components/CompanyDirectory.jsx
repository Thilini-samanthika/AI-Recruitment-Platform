import React, { useState } from 'react';

export default function CompanyDirectory({ companies, onSelectCompany, onOpenRegisterModal }) {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedIndustry, setSelectedIndustry] = useState('ALL');
  const [selectedSize, setSelectedSize] = useState('ALL');

  const filteredCompanies = companies.filter(c => {
    const nameMatch = (c.companyName || '').toLowerCase().includes(searchQuery.toLowerCase()) ||
                      (c.email || '').toLowerCase().includes(searchQuery.toLowerCase()) ||
                      (c.address || '').toLowerCase().includes(searchQuery.toLowerCase()) ||
                      (c.profile?.description || '').toLowerCase().includes(searchQuery.toLowerCase());

    const industryMatch = selectedIndustry === 'ALL' || (c.profile?.industry || '').toLowerCase().includes(selectedIndustry.toLowerCase());
    const sizeMatch = selectedSize === 'ALL' || c.profile?.companySize === selectedSize;

    return nameMatch && industryMatch && sizeMatch;
  });

  return (
    <div className="company-directory">
      {/* Header & Controls */}
      <div className="glass-panel" style={{ padding: '2rem', marginBottom: '2rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem', marginBottom: '1.5rem' }}>
          <div>
            <h2 style={{ fontSize: '1.75rem', marginBottom: '0.25rem' }}>Corporate Directory</h2>
            <p style={{ color: '#9ca3af', fontSize: '0.95rem' }}>
              Explore and manage registered companies, corporate profiles, and hiring organizations.
            </p>
          </div>

          <button className="btn btn-primary" onClick={onOpenRegisterModal}>
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
            Register Company
          </button>
        </div>

        {/* Filter Toolbar */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem' }}>
          <div>
            <label className="form-label">Search Companies</label>
            <input
              type="text"
              className="form-input"
              placeholder="Search by name, location, keyword..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>

          <div>
            <label className="form-label">Filter by Industry</label>
            <select
              className="form-select"
              value={selectedIndustry}
              onChange={(e) => setSelectedIndustry(e.target.value)}
            >
              <option value="ALL">All Industries</option>
              <option value="Information Technology">Information Technology & AI</option>
              <option value="Fintech">Fintech & Blockchain</option>
              <option value="Healthcare">Healthcare & Biotech</option>
              <option value="E-Commerce">E-Commerce</option>
              <option value="Cybersecurity">Cybersecurity</option>
            </select>
          </div>

          <div>
            <label className="form-label">Filter by Company Size</label>
            <select
              className="form-select"
              value={selectedSize}
              onChange={(e) => setSelectedSize(e.target.value)}
            >
              <option value="ALL">All Company Sizes</option>
              <option value="1-10">1-10 Employees</option>
              <option value="11-50">11-50 Employees</option>
              <option value="51-200">51-200 Employees</option>
              <option value="201-500">201-500 Employees</option>
              <option value="500+">500+ Employees</option>
            </select>
          </div>
        </div>
      </div>

      {/* Company Cards Grid */}
      {filteredCompanies.length === 0 ? (
        <div className="glass-panel empty-state">
          <div className="empty-state-icon">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
          </div>
          <h3>No companies matched your filters</h3>
          <p style={{ marginTop: '0.5rem', color: '#9ca3af' }}>Try adjusting your search query or reset filters.</p>
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(360px, 1fr))', gap: '1.5rem' }}>
          {filteredCompanies.map((c) => {
            const profile = c.profile || {};
            const initials = (c.companyName || 'CO')
              .split(' ')
              .map(w => w[0])
              .slice(0, 2)
              .join('')
              .toUpperCase();

            return (
              <div
                key={c.id}
                className="glass-panel"
                style={{
                  padding: '1.75rem',
                  display: 'flex',
                  flexDirection: 'column',
                  justifyContent: 'space-between',
                  gap: '1.25rem',
                  transition: 'transform 0.2s, border-color 0.2s',
                  cursor: 'pointer'
                }}
                onClick={() => onSelectCompany(c)}
              >
                <div>
                  {/* Top card header */}
                  <div style={{ display: 'flex', gap: '1rem', alignItems: 'center', marginBottom: '1rem' }}>
                    {profile.logoUrl ? (
                      <img
                        src={profile.logoUrl}
                        alt={c.companyName}
                        style={{
                          width: '56px',
                          height: '56px',
                          borderRadius: '12px',
                          objectFit: 'cover',
                          border: '1px solid rgba(255,255,255,0.1)'
                        }}
                        onError={(e) => { e.target.style.display = 'none'; }}
                      />
                    ) : (
                      <div
                        style={{
                          width: '56px',
                          height: '56px',
                          borderRadius: '12px',
                          background: 'linear-gradient(135deg, #4f46e5, #9333ea)',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          fontWeight: 700,
                          fontSize: '1.2rem',
                          color: 'white',
                          boxShadow: '0 4px 12px rgba(99, 102, 241, 0.3)'
                        }}
                      >
                        {initials}
                      </div>
                    )}

                    <div style={{ flex: 1, minWidth: 0 }}>
                      <h3
                        style={{
                          fontSize: '1.2rem',
                          whiteSpace: 'nowrap',
                          overflow: 'hidden',
                          textOverflow: 'ellipsis',
                          color: '#f9fafb'
                        }}
                      >
                        {c.companyName}
                      </h3>
                      <div style={{ fontSize: '0.85rem', color: '#a5b4fc', marginTop: '0.15rem' }}>
                        {profile.industry || 'Information Technology'}
                      </div>
                    </div>
                  </div>

                  {/* Badges */}
                  <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap', marginBottom: '1rem' }}>
                    <span className="skill-level-badge level-ADVANCED" style={{ fontSize: '0.75rem' }}>
                      {profile.companySize || '50-200'} Employees
                    </span>
                    <span className="skill-level-badge level-INTERMEDIATE" style={{ fontSize: '0.75rem' }}>
                      Verified Entity
                    </span>
                  </div>

                  {/* Description snippet */}
                  <p
                    style={{
                      fontSize: '0.875rem',
                      color: '#9ca3af',
                      lineHeight: '1.5',
                      display: '-webkit-box',
                      WebkitLineClamp: 3,
                      WebkitBoxOrient: 'vertical',
                      overflow: 'hidden'
                    }}
                  >
                    {profile.description || 'Verified corporate enterprise on the AI Recruitment Platform.'}
                  </p>
                </div>

                {/* Card footer */}
                <div
                  style={{
                    borderTop: '1px solid rgba(255,255,255,0.06)',
                    paddingTop: '1rem',
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center'
                  }}
                >
                  <div style={{ fontSize: '0.8rem', color: '#6b7280' }}>
                    {c.address ? c.address.split(',')[0] : 'Remote'}
                  </div>

                  <button
                    className="btn btn-secondary btn-sm"
                    onClick={(e) => {
                      e.stopPropagation();
                      onSelectCompany(c);
                    }}
                  >
                    Manage Profile &rarr;
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
