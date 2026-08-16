import React, { useState } from 'react';

export default function RegisterCompanyModal({ isOpen, onClose, onRegister }) {
  const [formData, setFormData] = useState({
    userId: Math.floor(100 + Math.random() * 900),
    companyName: '',
    email: '',
    phone: '',
    address: '',
    industry: 'Information Technology & AI',
    companySize: '11-50',
    website: '',
    description: '',
    logoUrl: 'https://images.unsplash.com/photo-1549719386-74dfcbf7dbed?w=150&auto=format&fit=crop&q=80'
  });

  if (!isOpen) return null;

  const handleSubmit = (e) => {
    e.preventDefault();
    onRegister(formData);
    onClose();
  };

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-card" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3 className="section-title">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#10b981" strokeWidth="2"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="16"></line><line x1="8" y1="12" x2="16" y2="12"></line></svg>
            Register New Corporate Entity
          </h3>
          <button className="skill-delete-btn" onClick={onClose} aria-label="Close">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="form-group">
              <label className="form-label">Company Name *</label>
              <input
                type="text"
                className="form-input"
                required
                value={formData.companyName}
                onChange={(e) => setFormData({ ...formData, companyName: e.target.value })}
                placeholder="e.g. Apex Artificial Intelligence Labs"
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Corporate Email *</label>
                <input
                  type="email"
                  className="form-input"
                  required
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  placeholder="contact@apexai.io"
                />
              </div>

              <div className="form-group">
                <label className="form-label">Contact Phone</label>
                <input
                  type="text"
                  className="form-input"
                  value={formData.phone}
                  onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                  placeholder="+1 (555) 300-4000"
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Industry Domain</label>
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
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Company Size</label>
                <select
                  className="form-select"
                  value={formData.companySize}
                  onChange={(e) => setFormData({ ...formData, companySize: e.target.value })}
                >
                  <option value="1-10">1-10 Employees</option>
                  <option value="11-50">11-50 Employees</option>
                  <option value="51-200">51-200 Employees</option>
                  <option value="201-500">201-500 Employees</option>
                  <option value="500+">500+ Employees</option>
                </select>
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">Website URL</label>
              <input
                type="url"
                className="form-input"
                value={formData.website}
                onChange={(e) => setFormData({ ...formData, website: e.target.value })}
                placeholder="https://apexai.io"
              />
            </div>

            <div className="form-group">
              <label className="form-label">Headquarters Address</label>
              <input
                type="text"
                className="form-input"
                value={formData.address}
                onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                placeholder="500 Tech Blvd, Seattle, WA 98101"
              />
            </div>

            <div className="form-group">
              <label className="form-label">Company Overview / Bio</label>
              <textarea
                className="form-textarea"
                rows="3"
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                placeholder="Summary of corporate operations, products, and talent culture..."
              />
            </div>
          </div>

          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary">
              Register Company
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
