import React, { useState } from 'react';
import EditProfileModal from './EditProfileModal';
import AddSkillModal from './AddSkillModal';
import AddEducationModal from './AddEducationModal';
import AddExperienceModal from './AddExperienceModal';

export default function CandidateDashboard({ profile, onUpdateProfile, onAddSkill, onDeleteSkill, onAddEducation, onDeleteEducation, onAddExperience, onDeleteExperience }) {
  const [showEditProfile, setShowEditProfile] = useState(false);
  const [showAddSkill, setShowAddSkill] = useState(false);
  const [showAddEdu, setShowAddEdu] = useState(false);
  const [showAddExp, setShowAddExp] = useState(false);

  const getInitials = (name) => {
    if (!name) return 'C';
    return name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return 'Present';
    try {
      const d = new Date(dateStr);
      return d.toLocaleDateString('en-US', { month: 'short', year: 'numeric' });
    } catch {
      return dateStr;
    }
  };

  return (
    <div>
      {/* Profile Header Glass Card */}
      <div className="glass-panel profile-header-card">
        <div className="profile-top">
          <div className="avatar-wrapper">
            {getInitials(profile.fullName)}
          </div>
          <div className="profile-info">
            <h1 className="profile-name">{profile.fullName || 'Candidate Name'}</h1>
            <div className="profile-headline">{profile.headline || 'No professional headline set'}</div>

            <div className="contact-badges">
              {profile.phone && (
                <div className="contact-item">
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path></svg>
                  <span>{profile.phone}</span>
                </div>
              )}
              {profile.address && (
                <div className="contact-item">
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path><circle cx="12" cy="10" r="3"></circle></svg>
                  <span>{profile.address}</span>
                </div>
              )}
              <div className="contact-item" style={{ color: '#818cf8' }}>
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
                <span>Candidate ID: #{profile.id || 1} &bull; User ID: #{profile.userId || 1}</span>
              </div>
            </div>
          </div>

          <div>
            <button className="btn btn-primary" onClick={() => setShowEditProfile(true)}>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M12 20h9"></path><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"></path></svg>
              Edit Profile
            </button>
          </div>
        </div>

        {profile.summary && (
          <div className="profile-bio">
            <p>{profile.summary}</p>
          </div>
        )}
      </div>

      {/* Dashboard Grid */}
      <div className="dashboard-grid">
        {/* Left Column: Skills & Quick Info */}
        <div className="grid-col-4">
          <div className="glass-panel section-card">
            <div className="section-header">
              <div className="section-title">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" style={{ color: '#818cf8' }}><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg>
                <span>Skills & Expertise ({profile.skills?.length || 0})</span>
              </div>
              <button className="btn btn-secondary btn-sm" onClick={() => setShowAddSkill(true)}>
                + Add
              </button>
            </div>

            {(!profile.skills || profile.skills.length === 0) ? (
              <div className="empty-state">
                <p>No skills added yet. Add your core technical competencies!</p>
              </div>
            ) : (
              <div className="skills-container">
                {profile.skills.map((skill) => (
                  <div key={skill.id} className="skill-tag">
                    <span>{skill.skillName}</span>
                    <span className={`skill-level-badge level-${skill.proficiencyLevel || 'INTERMEDIATE'}`}>
                      {skill.proficiencyLevel || 'INT'}
                    </span>
                    <button
                      className="skill-delete-btn"
                      title="Delete skill"
                      onClick={() => onDeleteSkill(skill.id)}
                    >
                      
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Right Column: Experience & Education */}
        <div className="grid-col-8">
          {/* Work Experience */}
          <div className="glass-panel section-card" style={{ marginBottom: '1.5rem' }}>
            <div className="section-header">
              <div className="section-title">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" style={{ color: '#34d399' }}><rect x="2" y="7" width="20" height="14" rx="2" ry="2"></rect><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path></svg>
                <span>Work Experience</span>
              </div>
              <button className="btn btn-secondary btn-sm" onClick={() => setShowAddExp(true)}>
                + Add Experience
              </button>
            </div>

            {(!profile.experiences || profile.experiences.length === 0) ? (
              <div className="empty-state">
                <p>No work experience recorded yet.</p>
              </div>
            ) : (
              <div className="timeline-list">
                {profile.experiences.map((exp) => (
                  <div key={exp.id} className="timeline-item">
                    <div className="timeline-card">
                      <div className="timeline-header">
                        <div>
                          <h4 className="timeline-heading">{exp.jobTitle}</h4>
                          <div className="timeline-subheading">{exp.companyName}</div>
                        </div>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                          <span className="timeline-date">
                            {formatDate(exp.startDate)} &mdash; {formatDate(exp.endDate)}
                          </span>
                          <button
                            className="btn-danger-outline"
                            title="Delete experience"
                            onClick={() => onDeleteExperience(exp.id)}
                          >
                            Delete
                          </button>
                        </div>
                      </div>
                      {exp.description && (
                        <p className="timeline-desc">{exp.description}</p>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Education */}
          <div className="glass-panel section-card">
            <div className="section-header">
              <div className="section-title">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" style={{ color: '#38bdf8' }}><path d="M22 10v6M2 10l10-5 10 5-10 5z"></path><path d="M6 12v5c3 3 9 3 12 0v-5"></path></svg>
                <span>Education</span>
              </div>
              <button className="btn btn-secondary btn-sm" onClick={() => setShowAddEdu(true)}>
                + Add Education
              </button>
            </div>

            {(!profile.educations || profile.educations.length === 0) ? (
              <div className="empty-state">
                <p>No education records added yet.</p>
              </div>
            ) : (
              <div className="timeline-list">
                {profile.educations.map((edu) => (
                  <div key={edu.id} className="timeline-item">
                    <div className="timeline-card">
                      <div className="timeline-header">
                        <div>
                          <h4 className="timeline-heading">{edu.degree} {edu.fieldOfStudy ? `in ${edu.fieldOfStudy}` : ''}</h4>
                          <div className="timeline-subheading">{edu.institution}</div>
                        </div>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                          <span className="timeline-date">
                            {formatDate(edu.startDate)} &mdash; {formatDate(edu.endDate)}
                          </span>
                          <button
                            className="btn-danger-outline"
                            title="Delete education"
                            onClick={() => onDeleteEducation(edu.id)}
                          >
                            Delete
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Modals */}
      {showEditProfile && (
        <EditProfileModal
          profile={profile}
          onClose={() => setShowEditProfile(false)}
          onSave={onUpdateProfile}
        />
      )}

      {showAddSkill && (
        <AddSkillModal
          onClose={() => setShowAddSkill(false)}
          onAdd={onAddSkill}
        />
      )}

      {showAddEdu && (
        <AddEducationModal
          onClose={() => setShowAddEdu(false)}
          onAdd={onAddEducation}
        />
      )}

      {showAddExp && (
        <AddExperienceModal
          onClose={() => setShowAddExp(false)}
          onAdd={onAddExperience}
        />
      )}
    </div>
  );
}
