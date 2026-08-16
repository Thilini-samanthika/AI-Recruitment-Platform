import React, { useState, useEffect } from 'react';
import Navbar from './components/Navbar';
import CandidateDashboard from './components/CandidateDashboard';
import CandidateDirectory from './components/CandidateDirectory';
import MicroserviceInfo from './components/MicroserviceInfo';
import { candidateApi } from './services/candidateApi';

export default function App() {
  const [activeTab, setActiveTab] = useState('profile');
  const [profile, setProfile] = useState(null);
  const [candidates, setCandidates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [toastMessage, setToastMessage] = useState('');

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(''), 3500);
  };

  const loadData = async () => {
    setLoading(true);
    try {
      const profRes = await candidateApi.getProfile(1);
      if (profRes.success) setProfile(profRes.data);

      const candRes = await candidateApi.listCandidates();
      if (candRes.success) setCandidates(candRes.data);
    } catch (err) {
      console.error('Failed to load candidate data:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleUpdateProfile = async (formData) => {
    if (!profile) return;
    const res = await candidateApi.updateProfile(profile.id, formData);
    if (res.success) {
      setProfile(res.data);
      showToast('Profile updated successfully!');
    }
  };

  const handleAddSkill = async (skillData) => {
    if (!profile) return;
    const res = await candidateApi.addSkill(profile.id, skillData);
    if (res.success) {
      setProfile({
        ...profile,
        skills: [...(profile.skills || []), res.data]
      });
      showToast(`Skill '${res.data.skillName}' added successfully!`);
    }
  };

  const handleDeleteSkill = async (skillId) => {
    if (!profile) return;
    const res = await candidateApi.deleteSkill(profile.id, skillId);
    if (res.success) {
      setProfile({
        ...profile,
        skills: profile.skills.filter(s => s.id !== skillId)
      });
      showToast('Skill removed!');
    }
  };

  const handleAddEducation = async (eduData) => {
    if (!profile) return;
    const res = await candidateApi.addEducation(profile.id, eduData);
    if (res.success) {
      setProfile({
        ...profile,
        educations: [...(profile.educations || []), res.data]
      });
      showToast('Education record added successfully!');
    }
  };

  const handleDeleteEducation = async (eduId) => {
    if (!profile) return;
    const res = await candidateApi.deleteEducation(profile.id, eduId);
    if (res.success) {
      setProfile({
        ...profile,
        educations: profile.educations.filter(e => e.id !== eduId)
      });
      showToast('Education record removed!');
    }
  };

  const handleAddExperience = async (expData) => {
    if (!profile) return;
    const res = await candidateApi.addExperience(profile.id, expData);
    if (res.success) {
      setProfile({
        ...profile,
        experiences: [...(profile.experiences || []), res.data]
      });
      showToast('Work experience record added successfully!');
    }
  };

  const handleDeleteExperience = async (expId) => {
    if (!profile) return;
    const res = await candidateApi.deleteExperience(profile.id, expId);
    if (res.success) {
      setProfile({
        ...profile,
        experiences: profile.experiences.filter(e => e.id !== expId)
      });
      showToast('Experience record removed!');
    }
  };

  const handleSelectCandidateFromDirectory = (cand) => {
    setProfile(cand);
    setActiveTab('profile');
    showToast(`Viewing profile of ${cand.fullName}`);
  };

  return (
    <div className="app-container">
      <Navbar activeTab={activeTab} setActiveTab={setActiveTab} />

      <main className="main-content">
        {loading ? (
          <div className="empty-state" style={{ paddingTop: '6rem' }}>
            <div className="avatar-wrapper" style={{ margin: '0 auto 1.5rem', animation: 'pulse 1.5s infinite' }}>
              &bull;
            </div>
            <h2>Loading Candidate Service Portal...</h2>
          </div>
        ) : (
          <>
            {activeTab === 'profile' && profile && (
              <CandidateDashboard
                profile={profile}
                onUpdateProfile={handleUpdateProfile}
                onAddSkill={handleAddSkill}
                onDeleteSkill={handleDeleteSkill}
                onAddEducation={handleAddEducation}
                onDeleteEducation={handleDeleteEducation}
                onAddExperience={handleAddExperience}
                onDeleteExperience={handleDeleteExperience}
              />
            )}

            {activeTab === 'directory' && (
              <CandidateDirectory
                candidates={candidates}
                onSelectCandidate={handleSelectCandidateFromDirectory}
              />
            )}

            {activeTab === 'api' && (
              <MicroserviceInfo />
            )}
          </>
        )}
      </main>

      {toastMessage && (
        <div className="toast-banner">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#34d399" strokeWidth="2.5"><path d="M20 6L9 17l-5-5"></path></svg>
          <span>{toastMessage}</span>
        </div>
      )}
    </div>
  );
}
