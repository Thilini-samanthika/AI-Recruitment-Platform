import React, { useState } from 'react';

export default function EditCompanyProfileModal({ company, isOpen, onClose, onSave }) {
  const profile = company?.profile || {};

  const [formData, setFormData] = useState({
    industry: profile.industry || 'Information Technology & AI',
    companySize: profile.companySize || '51-200',
    website: profile.website || '',
    description: profile.description || '',
    logoUrl: profile.logoUrl || ''
  });

  if (!isOpen) return null;

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave(formData);
    onClose();
  };

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-card" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3 className="section-title">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#a855f7" strokeWidth="2"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M22 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>
            Edit Extended Company Profile
          </h3>
          <button className="skill-delete-btn" onClick={onClose} aria-label="Close">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Industry Domain *</label>
                <select
                  className="form-select"
                  value={formData.industry}
                  onChange={(e) => setFormData({ ...formData, industry: e.target.value })}
                >
                  <option value="Information Technology & AI">Information Technology & AI</option>
                  <option value="Fintech & Blockchain">Fintech & Blockchain</option>
                  <option value="Healthcare & Biotech">Healthcare & Biotech</option>
                  <option value="E-Commerce & Retail">E-Commerce & Retail</option>
                  <option value="Cybersecurity">Cybersecurity</option>
                  <option value="Aerospace & Defense">Aerospace & Defense</option>
                  <option value="Education & EdTech">Education & EdTech</option>
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Company Size *</label>
                <select
                  className="form-select"
                  value={formData.companySize}
                  onChange={(e) => setFormData({ ...formData, companySize: e.target.value })}
                >
                  <option value="1-10">1-10 Employees (Startup)</option>
                  <option value="11-50">11-50 Employees (Early Stage)</option>
                  <option value="51-200">51-200 Employees (Growth)</option>
                  <option value="201-500">201-500 Employees (Mid-Market)</option>
                  <option value="500+">500+ Employees (Enterprise)</option>
                </select>
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">Company Website URL</label>
              <input
                type="url"
                className="form-input"
                value={formData.website}
                onChange={(e) => setFormData({ ...formData, website: e.target.value })}
                placeholder="https://acmetech.io"
              />
            </div>

            <div className="form-group">
              <label className="form-label">Company Logo Image URL</label>
              <input
                type="url"
                className="form-input"
                value={formData.logoUrl}
                onChange={(e) => setFormData({ ...formData, logoUrl: e.target.value })}
                placeholder="https://example.com/logo.png"
              />
            </div>

            <div className="form-group">
              <label className="form-label">About the Company & Mission *</label>
              <textarea
                className="form-textarea"
                rows="4"
                required
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                placeholder="Share your company story, core mission, product highlights, and culture..."
              />
            </div>
          </div>

          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary">
              Save Profile
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
