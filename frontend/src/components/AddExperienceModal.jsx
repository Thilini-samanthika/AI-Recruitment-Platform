import React, { useState } from 'react';

export default function AddExperienceModal({ onClose, onAdd }) {
  const [formData, setFormData] = useState({
    companyName: '',
    jobTitle: '',
    startDate: '',
    endDate: '',
    description: '',
    isCurrent: false
  });
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    await onAdd({
      companyName: formData.companyName,
      jobTitle: formData.jobTitle,
      startDate: formData.startDate || null,
      endDate: formData.isCurrent ? null : (formData.endDate || null),
      description: formData.description
    });
    setLoading(false);
    onClose();
  };

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-card" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>Add Work Experience</h3>
          <button className="btn-secondary btn-sm" onClick={onClose}>✕</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="form-group">
              <label className="form-label">Company Name *</label>
              <input
                type="text"
                required
                className="form-input"
                value={formData.companyName}
                onChange={(e) => setFormData({ ...formData, companyName: e.target.value })}
                placeholder="e.g. Google, Stripe, Microsoft"
              />
            </div>

            <div className="form-group">
              <label className="form-label">Job Title *</label>
              <input
                type="text"
                required
                className="form-input"
                value={formData.jobTitle}
                onChange={(e) => setFormData({ ...formData, jobTitle: e.target.value })}
                placeholder="e.g. Senior Software Engineer"
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Start Date</label>
                <input
                  type="date"
                  className="form-input"
                  value={formData.startDate}
                  onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                />
              </div>
              <div className="form-group">
                <label className="form-label">End Date</label>
                <input
                  type="date"
                  disabled={formData.isCurrent}
                  className="form-input"
                  value={formData.endDate}
                  onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                />
              </div>
            </div>

            <div className="form-group" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1.25rem' }}>
              <input
                type="checkbox"
                id="isCurrent"
                checked={formData.isCurrent}
                onChange={(e) => setFormData({ ...formData, isCurrent: e.target.checked })}
              />
              <label htmlFor="isCurrent" style={{ fontSize: '0.875rem', color: '#cbd5e1', cursor: 'pointer' }}>
                I currently work here
              </label>
            </div>

            <div className="form-group">
              <label className="form-label">Role Description & Key Accomplishments</label>
              <textarea
                rows={3}
                className="form-textarea"
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                placeholder="Engineered microservices, boosted performance, led engineering initiatives..."
              />
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Saving...' : 'Add Experience'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
