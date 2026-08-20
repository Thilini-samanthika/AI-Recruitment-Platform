import React, { useState, useEffect } from 'react';
import { aiApi } from '../services/aiApi';
import { jobApi } from '../services/jobApi';
import { useAuth } from '../context/AuthContext';

export default function AiRecommendationView({ preselectedResume, preselectedJob, onShowToast }) {
  const { user } = useAuth();
  const candidateId = (user && user.candidateId) ? user.candidateId : 1;

  const [activeSubTab, setActiveSubTab] = useState('matcher'); // 'matcher' | 'recommendations'
  const [resumes, setResumes] = useState([]);
  const [jobs, setJobs] = useState([]);
  const [selectedResumeId, setSelectedResumeId] = useState('');
  const [selectedJobId, setSelectedJobId] = useState('');

  // Custom Job input fallback
  const [customJobTitle, setCustomJobTitle] = useState('');
  const [customSkills, setCustomSkills] = useState('');
  const [customDescription, setCustomDescription] = useState('');

  const [matchLoading, setMatchLoading] = useState(false);
  const [matchResult, setMatchResult] = useState(null);

  const [recommendations, setRecommendations] = useState([]);
  const [recLoading, setRecLoading] = useState(false);

  // Load initial resumes & jobs
  useEffect(() => {
    const initData = async () => {
      try {
        const [resRes, jobRes] = await Promise.all([
          aiApi.getResumesByCandidate(candidateId),
          jobApi.listJobs()
        ]);

        if (resRes.success && resRes.data && resRes.data.length > 0) {
          setResumes(resRes.data);
          const initialResume = preselectedResume ? preselectedResume.id : resRes.data[0].id;
          setSelectedResumeId(initialResume);
        }

        if (jobRes.success && jobRes.data && jobRes.data.length > 0) {
          setJobs(jobRes.data);
          const initialJob = preselectedJob ? preselectedJob.id : jobRes.data[0].id;
          setSelectedJobId(initialJob);
        }
      } catch (err) {
        console.error('Failed to load matching data:', err);
      }
    };
    initData();
  }, [candidateId, preselectedResume, preselectedJob]);

  // Load recommendations
  const loadRecommendations = async () => {
    setRecLoading(true);
    try {
      const res = await aiApi.getRecommendations(candidateId);
      if (res.success && res.data) {
        setRecommendations(res.data);
      }
    } catch (err) {
      console.error('Failed to load recommendations:', err);
    } finally {
      setRecLoading(false);
    }
  };

  useEffect(() => {
    if (activeSubTab === 'recommendations') {
      loadRecommendations();
    }
  }, [activeSubTab, candidateId]);

  const handleRunMatch = async () => {
    setMatchLoading(true);
    try {
      const targetJob = jobs.find(j => j.id === Number(selectedJobId));

      let payload = {
        resumeId: selectedResumeId ? String(selectedResumeId) : null,
        candidateId: candidateId,
        jobId: targetJob ? targetJob.id : 999,
        jobTitle: targetJob ? targetJob.title : customJobTitle || "Custom Role",
        jobDescription: targetJob ? targetJob.description : customDescription,
        requiredSkills: targetJob
          ? targetJob.requiredSkills
          : customSkills.split(',').map(s => s.trim()).filter(Boolean)
      };

      const res = await aiApi.matchResumeWithJob(payload);
      if (res.success && res.data) {
        setMatchResult(res.data);
        if (onShowToast) onShowToast(`Match calculation complete: ${res.data.matchPercentage}% Compatibility!`);
      } else {
        if (onShowToast) onShowToast('Match calculation failed: ' + (res.message || 'Error'));
      }
    } catch (err) {
      console.error('Match calculation error:', err);
      if (onShowToast) onShowToast('Error calculating match score.');
    } finally {
      setMatchLoading(false);
    }
  };

  const getScoreColor = (score) => {
    if (score >= 80) return '#10b981'; // green
    if (score >= 50) return '#a855f7'; // purple
    return '#f59e0b'; // amber
  };

  return (
    <div className="ai-matching-view">
      {/* Top Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.4rem', background: 'rgba(236, 72, 153, 0.15)', color: '#f472b6', padding: '0.2rem 0.65rem', borderRadius: '6px', fontSize: '0.75rem', fontWeight: 700, marginBottom: '0.4rem' }}>
            <span> AI Matching & Recommendations</span>
            <span>&bull; /api/match</span>
          </div>
          <h2 style={{ fontSize: '1.8rem', fontWeight: 800, margin: 0 }}>AI Job Matcher & Scorer</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', margin: '0.25rem 0 0' }}>
            Evaluate resume compatibility against target jobs, discover skill overlaps, and view top recommendations.
          </p>
        </div>

        {/* Subtab Toggle */}
        <div style={{ display: 'flex', background: 'var(--surface-subtle)', borderRadius: '10px', padding: '4px', border: '1px solid var(--border-color)' }}>
          <button
            className={`tab-btn ${activeSubTab === 'matcher' ? 'active' : ''}`}
            onClick={() => setActiveSubTab('matcher')}
            style={{
              padding: '0.5rem 1rem',
              borderRadius: '8px',
              border: 'none',
              fontWeight: 600,
              fontSize: '0.85rem',
              cursor: 'pointer',
              background: activeSubTab === 'matcher' ? 'var(--primary)' : 'transparent',
              color: activeSubTab === 'matcher' ? '#fff' : 'var(--text-muted)'
            }}
          >
             Match Resume Against Job
          </button>
          <button
            className={`tab-btn ${activeSubTab === 'recommendations' ? 'active' : ''}`}
            onClick={() => setActiveSubTab('recommendations')}
            style={{
              padding: '0.5rem 1rem',
              borderRadius: '8px',
              border: 'none',
              fontWeight: 600,
              fontSize: '0.85rem',
              cursor: 'pointer',
              background: activeSubTab === 'recommendations' ? 'var(--primary)' : 'transparent',
              color: activeSubTab === 'recommendations' ? '#fff' : 'var(--text-muted)'
            }}
          >
             AI Job Recommendations
          </button>
        </div>
      </div>

      {activeSubTab === 'matcher' ? (
        <div style={{ display: 'grid', gridTemplateColumns: 'minmax(320px, 420px) 1fr', gap: '1.75rem', alignItems: 'start' }}>
          {/* Controls Form Card */}
          <div style={{ background: 'var(--surface-card)', border: '1px solid var(--border-color)', borderRadius: '14px', padding: '1.5rem' }}>
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, margin: '0 0 1.25rem' }}>
              1. Select Resume & Target Job
            </h3>

            {/* Resume Selection */}
            <div className="form-group" style={{ marginBottom: '1.25rem' }}>
              <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.4rem' }}>
                Candidate Resume to Evaluate
              </label>
              {resumes.length === 0 ? (
                <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', padding: '0.5rem 0' }}>
                  No resumes found. Please upload one in the Resume Studio first!
                </div>
              ) : (
                <select
                  className="form-input"
                  value={selectedResumeId}
                  onChange={e => setSelectedResumeId(e.target.value)}
                  style={{ width: '100%', padding: '0.65rem 0.85rem', borderRadius: '8px', background: 'var(--surface-subtle)', border: '1px solid var(--border-color)', color: 'var(--text-main)' }}
                >
                  {resumes.map(r => (
                    <option key={r.id} value={r.id}>
                      {r.fileName || `Resume #${r.id}`} ({(r.extractedSkills || []).length} skills)
                    </option>
                  ))}
                </select>
              )}
            </div>

            {/* Target Job Selection */}
            <div className="form-group" style={{ marginBottom: '1.25rem' }}>
              <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.4rem' }}>
                Target Job Position
              </label>
              <select
                className="form-input"
                value={selectedJobId}
                onChange={e => setSelectedJobId(e.target.value)}
                style={{ width: '100%', padding: '0.65rem 0.85rem', borderRadius: '8px', background: 'var(--surface-subtle)', border: '1px solid var(--border-color)', color: 'var(--text-main)' }}
              >
                {jobs.map(j => (
                  <option key={j.id} value={j.id}>
                    {j.title} &bull; {j.companyName}
                  </option>
                ))}
                <option value="custom">+ Enter Custom Job Description / Skills</option>
              </select>
            </div>

            {selectedJobId === 'custom' && (
              <div style={{ background: 'var(--surface-subtle)', padding: '1rem', borderRadius: '8px', marginBottom: '1.25rem', border: '1px solid var(--border-color)' }}>
                <div style={{ marginBottom: '0.75rem' }}>
                  <label style={{ display: 'block', fontSize: '0.78rem', fontWeight: 600, marginBottom: '0.25rem' }}>Custom Job Title</label>
                  <input
                    type="text"
                    placeholder="e.g. Lead Backend Engineer"
                    value={customJobTitle}
                    onChange={e => setCustomJobTitle(e.target.value)}
                    style={{ width: '100%', padding: '0.5rem', borderRadius: '6px', background: 'var(--surface-card)', border: '1px solid var(--border-color)', color: 'var(--text-main)', fontSize: '0.82rem' }}
                  />
                </div>
                <div>
                  <label style={{ display: 'block', fontSize: '0.78rem', fontWeight: 600, marginBottom: '0.25rem' }}>Required Skills (comma-separated)</label>
                  <input
                    type="text"
                    placeholder="Java, Spring Boot, MySQL, Docker, Kubernetes"
                    value={customSkills}
                    onChange={e => setCustomSkills(e.target.value)}
                    style={{ width: '100%', padding: '0.5rem', borderRadius: '6px', background: 'var(--surface-card)', border: '1px solid var(--border-color)', color: 'var(--text-main)', fontSize: '0.82rem' }}
                  />
                </div>
              </div>
            )}

            <button
              className="btn-primary"
              disabled={matchLoading}
              onClick={handleRunMatch}
              style={{
                width: '100%',
                padding: '0.75rem',
                borderRadius: '8px',
                fontWeight: 700,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '0.5rem',
                cursor: matchLoading ? 'not-allowed' : 'pointer'
              }}
            >
              {matchLoading ? (
                <span>Computing AI Fit Analysis...</span>
              ) : (
                <>
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5"><circle cx="12" cy="12" r="10"></circle><path d="m4.93 4.93 4.24 4.24"></path><path d="m14.83 9.17 4.24-4.24"></path><path d="m14.83 14.83 4.24 4.24"></path><path d="m9.17 14.83-4.24 4.24"></path><circle cx="12" cy="12" r="4"></circle></svg>
                  Calculate Compatibility
                </>
              )}
            </button>
          </div>

          {/* Match Results Display Card */}
          {matchResult ? (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
              {/* Score & Gauge Hero Card */}
              <div style={{
                background: 'linear-gradient(135deg, rgba(88, 28, 135, 0.35) 0%, rgba(17, 24, 39, 0.8) 100%)',
                border: '1px solid rgba(168, 85, 247, 0.4)',
                borderRadius: '16px',
                padding: '1.75rem',
                display: 'flex',
                alignItems: 'center',
                gap: '2rem',
                flexWrap: 'wrap'
              }}>
                {/* Circular Score Visual */}
                <div style={{
                  width: '120px',
                  height: '120px',
                  borderRadius: '50%',
                  background: `conic-gradient(${getScoreColor(matchResult.matchPercentage)} ${matchResult.matchPercentage * 3.6}deg, rgba(255,255,255,0.08) 0deg)`,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  padding: '10px',
                  boxShadow: `0 0 24px rgba(168, 85, 247, 0.3)`
                }}>
                  <div style={{
                    width: '100%',
                    height: '100%',
                    borderRadius: '50%',
                    background: '#0f172a',
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    justifyContent: 'center'
                  }}>
                    <span style={{ fontSize: '1.8rem', fontWeight: 800, color: getScoreColor(matchResult.matchPercentage) }}>
                      {matchResult.matchPercentage}%
                    </span>
                    <span style={{ fontSize: '0.65rem', color: 'var(--text-muted)', textTransform: 'uppercase', fontWeight: 700 }}>
                      Match Score
                    </span>
                  </div>
                </div>

                <div style={{ flex: 1 }}>
                  <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.4rem', background: 'rgba(16, 185, 129, 0.15)', color: '#34d399', padding: '0.2rem 0.6rem', borderRadius: '6px', fontSize: '0.75rem', fontWeight: 700, marginBottom: '0.4rem' }}>
                    <span>{matchResult.matchPercentage >= 80 ? '🌟 Exceptional Match' : (matchResult.matchPercentage >= 50 ? '✨ Good Potential' : '⚠️ Moderate Alignment')}</span>
                  </div>
                  <h3 style={{ margin: '0 0 0.4rem', fontSize: '1.3rem', fontWeight: 700 }}>
                    AI Fit Assessment
                  </h3>
                  <p style={{ margin: 0, fontSize: '0.9rem', color: 'var(--text-muted)', lineHeight: 1.5 }}>
                    {matchResult.analysisSummary}
                  </p>
                </div>
              </div>

              {/* Skills Breakdown: Matched vs Missing */}
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.25rem' }}>
                {/* Matched Skills */}
                <div style={{ background: 'var(--surface-card)', border: '1px solid var(--border-color)', borderRadius: '12px', padding: '1.25rem' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.85rem' }}>
                    <span style={{ width: 10, height: 10, borderRadius: '50%', background: '#10b981' }}></span>
                    <h4 style={{ margin: 0, fontSize: '0.95rem', fontWeight: 700, color: '#34d399' }}>
                      Matched Competencies ({matchResult.matchedSkills.length})
                    </h4>
                  </div>

                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.45rem' }}>
                    {matchResult.matchedSkills.length === 0 ? (
                      <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>No direct overlapping skills.</span>
                    ) : (
                      matchResult.matchedSkills.map((s, idx) => (
                        <span
                          key={idx}
                          style={{
                            background: 'rgba(16, 185, 129, 0.15)',
                            border: '1px solid rgba(16, 185, 129, 0.4)',
                            color: '#6ee7b7',
                            padding: '0.3rem 0.65rem',
                            borderRadius: '6px',
                            fontSize: '0.8rem',
                            fontWeight: 600,
                            display: 'inline-flex',
                            alignItems: 'center',
                            gap: '0.3rem'
                          }}
                        >
                          ✓ {s}
                        </span>
                      ))
                    )}
                  </div>
                </div>

                {/* Missing Skills */}
                <div style={{ background: 'var(--surface-card)', border: '1px solid var(--border-color)', borderRadius: '12px', padding: '1.25rem' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.85rem' }}>
                    <span style={{ width: 10, height: 10, borderRadius: '50%', background: '#f59e0b' }}></span>
                    <h4 style={{ margin: 0, fontSize: '0.95rem', fontWeight: 700, color: '#fbbf24' }}>
                      Skill Gaps / Missing ({matchResult.missingSkills.length})
                    </h4>
                  </div>

                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.45rem' }}>
                    {matchResult.missingSkills.length === 0 ? (
                      <span style={{ fontSize: '0.8rem', color: '#34d399' }}>No missing skill requirements! Full coverage.</span>
                    ) : (
                      matchResult.missingSkills.map((s, idx) => (
                        <span
                          key={idx}
                          style={{
                            background: 'rgba(245, 158, 11, 0.15)',
                            border: '1px solid rgba(245, 158, 11, 0.4)',
                            color: '#fcd34d',
                            padding: '0.3rem 0.65rem',
                            borderRadius: '6px',
                            fontSize: '0.8rem',
                            fontWeight: 600,
                            display: 'inline-flex',
                            alignItems: 'center',
                            gap: '0.3rem'
                          }}
                        >
                          ! {s}
                        </span>
                      ))
                    )}
                  </div>
                </div>
              </div>
            </div>
          ) : (
            <div style={{ background: 'var(--surface-card)', border: '1px solid var(--border-color)', borderRadius: '14px', padding: '3.5rem 2rem', textAlign: 'center' }}>
              <div style={{
                width: '64px',
                height: '64px',
                borderRadius: '50%',
                background: 'rgba(168, 85, 247, 0.15)',
                color: '#c084fc',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                margin: '0 auto 1rem'
              }}>
                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><circle cx="12" cy="12" r="10"></circle><path d="m4.93 4.93 4.24 4.24"></path><path d="m14.83 9.17 4.24-4.24"></path><path d="m14.83 14.83 4.24 4.24"></path><path d="m9.17 14.83-4.24 4.24"></path><circle cx="12" cy="12" r="4"></circle></svg>
              </div>
              <h3 style={{ fontSize: '1.15rem', fontWeight: 700, margin: '0 0 0.4rem' }}>
                Ready to Calculate Match Percentage
              </h3>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.88rem', maxWidth: '420px', margin: '0 auto' }}>
                Select a resume and job from the left panel and click <strong>Calculate Compatibility</strong> to run the matching algorithm.
              </p>
            </div>
          )}
        </div>
      ) : (
        /* Recommendations Feed Tab */
        <div style={{ background: 'var(--surface-card)', border: '1px solid var(--border-color)', borderRadius: '14px', padding: '1.5rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem', flexWrap: 'wrap', gap: '0.5rem' }}>
            <div>
              <h3 style={{ fontSize: '1.2rem', fontWeight: 700, margin: 0 }}>
                Top Recommended Roles for Your Profile
              </h3>
              <p style={{ margin: '0.2rem 0 0', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                Algorithmic rankings tailored to your verified technical skills from your latest resume
              </p>
            </div>

            <button
              className="btn-secondary"
              onClick={loadRecommendations}
              disabled={recLoading}
              style={{ fontSize: '0.8rem', padding: '0.45rem 0.85rem' }}
            >
              {recLoading ? 'Refreshing...' : ' Refresh AI Suggestions'}
            </button>
          </div>

          {recommendations.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '3rem 1rem', color: 'var(--text-muted)' }}>
              No recommendations found. Upload a resume with skills to activate recommendations.
            </div>
          ) : (
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '1.25rem' }}>
              {recommendations.map(rec => (
                <div
                  key={rec.id}
                  style={{
                    background: 'var(--surface-subtle)',
                    border: '1px solid var(--border-color)',
                    borderRadius: '12px',
                    padding: '1.25rem',
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'space-between'
                  }}
                >
                  <div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.5rem' }}>
                      <span style={{ fontSize: '0.78rem', color: '#c084fc', fontWeight: 700 }}>
                        {rec.companyName || 'Enterprise Partner'}
                      </span>
                      <span style={{
                        background: 'rgba(16, 185, 129, 0.18)',
                        color: '#34d399',
                        padding: '0.2rem 0.55rem',
                        borderRadius: '999px',
                        fontSize: '0.78rem',
                        fontWeight: 800
                      }}>
                        {rec.score}% Match
                      </span>
                    </div>

                    <h4 style={{ margin: '0 0 0.75rem', fontSize: '1.05rem', fontWeight: 700 }}>
                      {rec.jobTitle}
                    </h4>

                    <div style={{ marginBottom: '1rem' }}>
                      <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block', marginBottom: '0.35rem' }}>
                        Matched Keywords:
                      </span>
                      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.35rem' }}>
                        {(rec.matchedSkills || []).map((skill, idx) => (
                          <span
                            key={idx}
                            style={{
                              background: 'rgba(168, 85, 247, 0.12)',
                              border: '1px solid rgba(168, 85, 247, 0.3)',
                              color: '#d8b4fe',
                              padding: '0.15rem 0.45rem',
                              borderRadius: '4px',
                              fontSize: '0.72rem',
                              fontWeight: 600
                            }}
                          >
                            {skill}
                          </span>
                        ))}
                      </div>
                    </div>
                  </div>

                  <button
                    className="btn-primary"
                    onClick={() => {
                      setSelectedJobId(rec.jobId);
                      setActiveSubTab('matcher');
                      if (onShowToast) onShowToast(`Selected ${rec.jobTitle} for deep matching inspection.`);
                    }}
                    style={{ fontSize: '0.8rem', padding: '0.5rem', width: '100%', borderRadius: '6px' }}
                  >
                    View Deep Match Analysis &rarr;
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
