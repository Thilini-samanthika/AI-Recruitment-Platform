import React, { useState } from 'react';

export default function AddSkillModal({ onClose, onAdd }) {
  const [skillName, setSkillName] = useState('');
  const [proficiencyLevel, setProficiencyLevel] = useState('INTERMEDIATE');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!skillName.trim()) return;
    setLoading(true);
    await onAdd({ skillName: skillName.trim(), proficiencyLevel });
    setLoading(false);
    onClose();
  };

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-card" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>Add Technical Skill</h3>
          <button className="btn-secondary btn-sm" onClick={onClose}>✕</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="form-group">
              <label className="form-label">Skill Name *</label>
              <input
                type="text"
                required
                className="form-input"
                value={skillName}
                onChange={(e) => setSkillName(e.target.value)}
                placeholder="e.g. Spring Boot, React, Kubernetes, Python"
              />
            </div>

            <div className="form-group">
              <label className="form-label">Proficiency Level</label>
              <select
                className="form-select"
                value={proficiencyLevel}
                onChange={(e) => setProficiencyLevel(e.target.value)}
              >
                <option value="BEGINNER">BEGINNER</option>
                <option value="INTERMEDIATE">INTERMEDIATE</option>
                <option value="ADVANCED">ADVANCED</option>
                <option value="EXPERT">EXPERT</option>
              </select>
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Adding...' : 'Add Skill'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
