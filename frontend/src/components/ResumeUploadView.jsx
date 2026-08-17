import React, { useState, useEffect, useRef } from 'react';
import { aiApi } from '../services/aiApi';
import { useAuth } from '../context/AuthContext';

export default function ResumeUploadView({ onMatchJob, onShowToast }) {
  const { user } = useAuth();
  const candidateId = (user && user.candidateId) ? user.candidateId : 1;

  const [resumes, setResumes] = useState([]);
  const [selectedResume, setSelectedResume] = useState(null);
  const [isUploading, setIsUploading] = useState(false);
  const [isExtracting, setIsExtracting] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [dragActive, setDragActive] = useState(false);
  const [activeSkillCategory, setActiveSkillCategory] = useState('ALL');
  const [showFullText, setShowFullText] = useState(false);

  const fileInputRef = useRef(null);

  const loadResumes = async () => {
    try {
      const res = await aiApi.getResumesByCandidate(candidateId);
      if (res.success && res.data) {
        setResumes(res.data);
        if (res.data.length > 0) {
          setSelectedResume(res.data[0]);
        }
      }
    } catch (err) {
      console.error('Failed to load candidate resumes:', err);
    }
  };

  useEffect(() => {
    loadResumes();
  }, [candidateId]);

  const handleFileUpload = async (file) => {
    if (!file) return;

    const validExtensions = ['.pdf', '.docx', '.txt', '.md'];
    const hasValidExt = validExtensions.some(ext => file.name.toLowerCase().endsWith(ext));
    if (!hasValidExt) {
      if (onShowToast) onShowToast('Please upload a valid .pdf, .docx, or .txt resume file.');
      return;
    }

    if (file.size > 15 * 1024 * 1024) {
      if (onShowToast) onShowToast('File size exceeds the 15MB limit.');
      return;
    }

    setIsUploading(true);
    setUploadProgress(20);

    const interval = setInterval(() => {
      setUploadProgress(prev => (prev < 85 ? prev + 15 : prev));
    }, 200);

    try {
      const res = await aiApi.uploadResume(candidateId, file);
      clearInterval(interval);
      setUploadProgress(100);

      if (res.success && res.data) {
        setResumes(prev => [res.data, ...prev.filter(r => r.id !== res.data.id)]);
        setSelectedResume(res.data);
        if (onShowToast) onShowToast(`Resume '${file.name}' uploaded & parsed successfully!`);
      } else {
        if (onShowToast) onShowToast('Upload failed: ' + (res.message || 'Error'));
      }
    } catch (err) {
      clearInterval(interval);
      console.error('Resume upload failed:', err);
      if (onShowToast) onShowToast('Failed to upload resume file.');
    } finally {
      setTimeout(() => {
        setIsUploading(false);
        setUploadProgress(0);
      }, 600);
    }
  };

  const handleDrag = (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === 'dragenter' || e.type === 'dragover') {
      setDragActive(true);
    } else if (e.type === 'dragleave') {
      setDragActive(false);
    }
  };

  const handleDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      handleFileUpload(e.dataTransfer.files[0]);
    }
  };

  const handleReExtract = async (resumeId) => {
    setIsExtracting(true);
    try {
      const res = await aiApi.extractSkills(resumeId);
      if (res.success && res.data) {
        if (selectedResume && selectedResume.id === resumeId) {
          setSelectedResume({
            ...selectedResume,
            extractedSkills: res.data.extractedSkills
          });
        }
        setResumes(resumes.map(r => r.id === resumeId ? { ...r, extractedSkills: res.data.extractedSkills } : r));
        if (onShowToast) onShowToast(`Identified ${res.data.totalSkillsFound} skills!`);
      }
    } catch (err) {
      console.error('Skill extraction error:', err);
    } finally {
      setIsExtracting(false);
    }
  };

  const categorizeSkill = (skill) => {
    const s = skill.toLowerCase();
    if (['java', 'python', 'javascript', 'typescript', 'c#', 'c++', 'golang', 'go', 'php', 'ruby', 'sql', 'bash'].includes(s)) return 'LANGUAGES';
    if (['spring boot', 'react', 'react.js', 'next.js', 'node.js', 'express.js', 'django', 'fastapi', 'angular', 'vue.js'].includes(s)) return 'FRAMEWORKS';
    if (['docker', 'kubernetes', 'aws', 'azure', 'gcp', 'ci/cd', 'linux', 'kafka', 'nginx'].includes(s)) return 'CLOUD_DEVOPS';
    if (['mysql', 'postgresql', 'mongodb', 'redis', 'elasticsearch', 'sqlite'].includes(s)) return 'DATABASES';
    return 'OTHER';
  };

  const filteredSkills = (selectedResume && selectedResume.extractedSkills)
    ? selectedResume.extractedSkills.filter(skill => {
        if (activeSkillCategory === 'ALL') return true;
        return categorizeSkill(skill) === activeSkillCategory;
      })
    : [];

  return (
    <div className="resume-studio-container">
      {/* Studio Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.4rem', background: 'rgba(168, 85, 247, 0.15)', color: '#c084fc', padding: '0.2rem 0.65rem', borderRadius: '6px', fontSize: '0.75rem', fontWeight: 700, marginBottom: '0.4rem' }}>
            <span>⚡ Member 5: AI Resume Service</span>
            <span>&bull; Port 8085</span>
          </div>
          <h2 style={{ fontSize: '1.8rem', fontWeight: 800, margin: 0 }}>AI Resume Studio & NLP Parser</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', margin: '0.25rem 0 0' }}>
            Upload resume files (PDF, DOCX, TXT) to automatically extract text, classify technical competencies, and generate skill profiles.
          </p>
        </div>

        <div style={{ display: 'flex', gap: '0.75rem' }}>
          <button
            className="btn-primary"
            onClick={() => fileInputRef.current && fileInputRef.current.click()}
            style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', padding: '0.65rem 1.15rem', borderRadius: '8px' }}
          >
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="17 8 12 3 7 8"></polyline><line x1="12" y1="3" x2="12" y2="15"></line></svg>
            Upload New Resume
          </button>
        </div>
      </div>

      <input
        type="file"
        ref={fileInputRef}
        onChange={e => e.target.files && handleFileUpload(e.target.files[0])}
        accept=".pdf,.docx,.txt,.md"
        style={{ display: 'none' }}
      />

      {/* Drag & Drop Upload Zone */}
      <div
        className={`dropzone ${dragActive ? 'drag-active' : ''}`}
        onDragEnter={handleDrag}
        onDragLeave={handleDrag}
        onDragOver={handleDrag}
        onDrop={handleDrop}
        onClick={() => fileInputRef.current && fileInputRef.current.click()}
        style={{
          border: `2px dashed ${dragActive ? '#a855f7' : 'rgba(168, 85, 247, 0.4)'}`,
          background: dragActive ? 'rgba(168, 85, 247, 0.12)' : 'rgba(17, 24, 39, 0.6)',
          borderRadius: '16px',
          padding: '2.5rem 1.5rem',
          textAlign: 'center',
          cursor: 'pointer',
          marginBottom: '2rem',
          transition: 'all 0.2s ease',
          position: 'relative'
        }}
      >
        {isUploading ? (
          <div style={{ maxWidth: '400px', margin: '0 auto' }}>
            <div style={{ marginBottom: '0.75rem', fontWeight: 600, color: '#c084fc' }}>
              Extracting Text & Parsing Skills with Apache PDFBox...
            </div>
            <div style={{ width: '100%', height: '8px', background: 'var(--surface-subtle)', borderRadius: '999px', overflow: 'hidden' }}>
              <div
                style={{
                  width: `${uploadProgress}%`,
                  height: '100%',
                  background: 'linear-gradient(90deg, #a855f7, #ec4899)',
                  transition: 'width 0.3s ease'
                }}
              ></div>
            </div>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginTop: '0.5rem', display: 'inline-block' }}>
              {uploadProgress}% processed
            </span>
          </div>
        ) : (
          <div>
            <div style={{
              width: '56px',
              height: '56px',
              borderRadius: '50%',
              background: 'rgba(168, 85, 247, 0.15)',
              color: '#c084fc',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              margin: '0 auto 1rem'
            }}>
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="12" y1="18" x2="12" y2="12"></line><line x1="9" y1="15" x2="15" y2="15"></line></svg>
            </div>
            <h3 style={{ fontSize: '1.15rem', fontWeight: 700, margin: '0 0 0.4rem' }}>
              Drag & Drop your Resume here or <span style={{ color: '#c084fc' }}>browse files</span>
            </h3>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', margin: 0 }}>
              Supports PDF (.pdf), Word (.docx), and Plain Text (.txt) up to 15MB
            </p>
          </div>
        )}
      </div>

      {/* Main Studio Grid: Left - Resumes Library, Right - Parsed Detail & Skills Cloud */}
      <div style={{ display: 'grid', gridTemplateColumns: 'minmax(300px, 360px) 1fr', gap: '1.5rem', alignItems: 'start' }}>
        {/* Left Column: Stored Resumes List */}
        <div style={{ background: 'var(--surface-card)', border: '1px solid var(--border-color)', borderRadius: '14px', padding: '1.25rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
            <h3 style={{ fontSize: '1.05rem', fontWeight: 700, margin: 0 }}>Candidate Resumes</h3>
            <span style={{ fontSize: '0.75rem', background: 'var(--surface-subtle)', padding: '0.2rem 0.55rem', borderRadius: '6px', color: 'var(--text-muted)' }}>
              {resumes.length} stored
            </span>
          </div>

          {resumes.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '2rem 1rem', color: 'var(--text-muted)', fontSize: '0.85rem' }}>
              No resumes uploaded yet. Upload your first resume above!
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              {resumes.map(r => {
                const isSelected = selectedResume && selectedResume.id === r.id;
                const skillCount = (r.extractedSkills || []).length;
                return (
                  <div
                    key={r.id}
                    onClick={() => setSelectedResume(r)}
                    style={{
                      padding: '0.9rem',
                      borderRadius: '10px',
                      border: `1px solid ${isSelected ? '#a855f7' : 'var(--border-color)'}`,
                      background: isSelected ? 'rgba(168, 85, 247, 0.12)' : 'var(--surface-subtle)',
                      cursor: 'pointer',
                      transition: 'all 0.15s'
                    }}
                  >
                    <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', marginBottom: '0.35rem' }}>
                      <div style={{ fontWeight: 600, fontSize: '0.9rem', color: isSelected ? '#e9d5ff' : 'var(--text-main)', wordBreak: 'break-all' }}>
                        📄 {r.fileName || `Resume #${r.id}`}
                      </div>
                      <span style={{
                        fontSize: '0.7rem',
                        fontWeight: 700,
                        padding: '0.15rem 0.45rem',
                        borderRadius: '4px',
                        background: r.status === 'PARSED' ? 'rgba(16, 185, 129, 0.2)' : 'rgba(245, 158, 11, 0.2)',
                        color: r.status === 'PARSED' ? '#34d399' : '#fbbf24'
                      }}>
                        {r.status || 'PARSED'}
                      </span>
                    </div>

                    <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                      <span>{r.uploadedAt ? new Date(r.uploadedAt).toLocaleDateString() : 'Just now'}</span>
                      <span style={{ color: '#c084fc', fontWeight: 600 }}>{skillCount} skills extracted</span>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>

        {/* Right Column: Active Resume Extracted Skills & NLP Inspection */}
        {selectedResume ? (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
            {/* Skills Cloud Card */}
            <div style={{ background: 'var(--surface-card)', border: '1px solid var(--border-color)', borderRadius: '14px', padding: '1.5rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem', flexWrap: 'wrap', gap: '0.5rem' }}>
                <div>
                  <h3 style={{ fontSize: '1.2rem', fontWeight: 700, margin: 0, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <span>🧠 AI Extracted Skills Cloud</span>
                    <span style={{ fontSize: '0.8rem', background: 'rgba(168, 85, 247, 0.2)', color: '#c084fc', padding: '0.15rem 0.5rem', borderRadius: '999px' }}>
                      {(selectedResume.extractedSkills || []).length} Detected
                    </span>
                  </h3>
                  <p style={{ margin: '0.2rem 0 0', fontSize: '0.82rem', color: 'var(--text-muted)' }}>
                    Extracted via dictionary & regex matching from {selectedResume.fileName}
                  </p>
                </div>

                <div style={{ display: 'flex', gap: '0.5rem' }}>
                  <button
                    className="btn-secondary"
                    disabled={isExtracting}
                    onClick={() => handleReExtract(selectedResume.id)}
                    style={{ fontSize: '0.8rem', padding: '0.45rem 0.85rem' }}
                  >
                    {isExtracting ? 'Analyzing...' : '🔄 Re-Extract Skills'}
                  </button>
                  {onMatchJob && (
                    <button
                      className="btn-primary"
                      onClick={() => onMatchJob(selectedResume)}
                      style={{ fontSize: '0.8rem', padding: '0.45rem 0.85rem', display: 'flex', alignItems: 'center', gap: '0.4rem' }}
                    >
                      <span>⚡ Match with Jobs</span>
                    </button>
                  )}
                </div>
              </div>

              {/* Category Filter Pills */}
              <div style={{ display: 'flex', gap: '0.4rem', flexWrap: 'wrap', marginBottom: '1.25rem' }}>
                {['ALL', 'LANGUAGES', 'FRAMEWORKS', 'CLOUD_DEVOPS', 'DATABASES'].map(cat => (
                  <button
                    key={cat}
                    onClick={() => setActiveSkillCategory(cat)}
                    style={{
                      padding: '0.35rem 0.75rem',
                      borderRadius: '8px',
                      fontSize: '0.78rem',
                      fontWeight: 600,
                      border: 'none',
                      cursor: 'pointer',
                      background: activeSkillCategory === cat ? '#a855f7' : 'var(--surface-subtle)',
                      color: activeSkillCategory === cat ? '#ffffff' : 'var(--text-muted)'
                    }}
                  >
                    {cat.replace('_', ' & ')}
                  </button>
                ))}
              </div>

              {/* Badges Cloud */}
              {filteredSkills.length === 0 ? (
                <div style={{ padding: '1.5rem', textAlign: 'center', color: 'var(--text-muted)', fontSize: '0.85rem' }}>
                  No skills match the selected filter category.
                </div>
              ) : (
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.6rem' }}>
                  {filteredSkills.map((skill, idx) => (
                    <div
                      key={idx}
                      style={{
                        background: 'linear-gradient(135deg, rgba(88, 28, 135, 0.4) 0%, rgba(55, 48, 163, 0.4) 100%)',
                        border: '1px solid rgba(168, 85, 247, 0.4)',
                        color: '#f3e8ff',
                        padding: '0.45rem 0.85rem',
                        borderRadius: '999px',
                        fontSize: '0.85rem',
                        fontWeight: 600,
                        display: 'inline-flex',
                        alignItems: 'center',
                        gap: '0.4rem',
                        boxShadow: '0 2px 6px rgba(0, 0, 0, 0.2)'
                      }}
                    >
                      <span style={{ width: 6, height: 6, borderRadius: '50%', background: '#34d399' }}></span>
                      <span>{skill}</span>
                    </div>
                  ))}
                </div>
              )}
            </div>

            {/* Extracted Text Viewer Card */}
            <div style={{ background: 'var(--surface-card)', border: '1px solid var(--border-color)', borderRadius: '14px', padding: '1.5rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem' }}>
                <h3 style={{ fontSize: '1.05rem', fontWeight: 700, margin: 0 }}>
                  📄 Parsed Resume Text Content
                </h3>
                <button
                  className="btn-secondary"
                  onClick={() => setShowFullText(!showFullText)}
                  style={{ fontSize: '0.78rem', padding: '0.35rem 0.65rem' }}
                >
                  {showFullText ? 'Collapse Text' : 'Expand Full Text'}
                </button>
              </div>

              <div
                style={{
                  background: 'var(--surface-subtle)',
                  padding: '1rem',
                  borderRadius: '8px',
                  fontFamily: 'monospace',
                  fontSize: '0.82rem',
                  lineHeight: 1.6,
                  color: 'var(--text-muted)',
                  maxHeight: showFullText ? '450px' : '150px',
                  overflowY: 'auto',
                  whiteSpace: 'pre-wrap',
                  border: '1px solid var(--border-color)'
                }}
              >
                {selectedResume.extractedText || 'No plain text extracted for this resume.'}
              </div>
            </div>
          </div>
        ) : (
          <div style={{ background: 'var(--surface-card)', border: '1px solid var(--border-color)', borderRadius: '14px', padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>
            Select or upload a resume to view extracted skills and NLP preview.
          </div>
        )}
      </div>
    </div>
  );
}
