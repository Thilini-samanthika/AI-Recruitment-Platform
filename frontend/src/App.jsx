import React, { useState, useEffect } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import Navbar from './components/Navbar';
import AuthModal from './components/AuthModal';
import DashboardOverview from './components/DashboardOverview';
import ResumeUploadView from './components/ResumeUploadView';
import AiRecommendationView from './components/AiRecommendationView';
import JobDirectory from './components/JobDirectory';
import PlatformArchitecture from './components/PlatformArchitecture';

// Member 3 Components
import CompanyDashboard from './components/CompanyDashboard';
import CompanyDirectory from './components/CompanyDirectory';
import RegisterCompanyModal from './components/RegisterCompanyModal';

// Member 2 Components
import CandidateDashboard from './components/CandidateDashboard';
import CandidateDirectory from './components/CandidateDirectory';

// Services
import { companyApi } from './services/companyApi';
import { candidateApi } from './services/candidateApi';
import { aiApi } from './services/aiApi';
import { jobApi } from './services/jobApi';

function MainAppContent() {
  const { user, isAuthModalOpen, authModalMode, closeAuthModal } = useAuth();

  const [activeTab, setActiveTab] = useState('dashboard');
  const [companies, setCompanies] = useState([]);
  const [currentCompany, setCurrentCompany] = useState(null);
  const [candidateProfile, setCandidateProfile] = useState(null);
  const [candidates, setCandidates] = useState([]);
  const [jobs, setJobs] = useState([]);
  const [resumes, setResumes] = useState([]);

  // Cross-component workflow state
  const [matchSelectedResume, setMatchSelectedResume] = useState(null);
  const [matchSelectedJob, setMatchSelectedJob] = useState(null);

  const [loading, setLoading] = useState(true);
  const [toastMessage, setToastMessage] = useState('');
  const [isRegisterModalOpen, setIsRegisterModalOpen] = useState(false);

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(''), 3500);
  };

  const loadData = async () => {
    setLoading(true);
    try {
      // 1. Load companies (Member 3)
      const compRes = await companyApi.listCompanies();
      if (compRes.success && compRes.data && compRes.data.length > 0) {
        setCompanies(compRes.data);
        setCurrentCompany(compRes.data[0]);
      }

      // 2. Load candidate data (Member 2)
      const candProfRes = await candidateApi.getProfile(user?.candidateId || 1);
      if (candProfRes.success) setCandidateProfile(candProfRes.data);

      const candListRes = await candidateApi.listCandidates();
      if (candListRes.success) setCandidates(candListRes.data);

      // 3. Load jobs (Member 4)
      const jobRes = await jobApi.listJobs();
      if (jobRes.success && jobRes.data) setJobs(jobRes.data);

      // 4. Load resumes (Member 5)
      const resumeRes = await aiApi.getResumesByCandidate(user?.candidateId || 1);
      if (resumeRes.success && resumeRes.data) setResumes(resumeRes.data);
    } catch (err) {
      console.error('Failed to load initial data:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [user]);

  // Action: Trigger Job Match from Resume Studio
  const handleMatchFromResumeStudio = (resume) => {
    setMatchSelectedResume(resume);
    setActiveTab('ai-recommendations');
    showToast(`Ready to match '${resume.fileName || 'Resume'}' against target jobs`);
  };

  // Action: Trigger Match from Job Directory
  const handleMatchFromJobDirectory = (job) => {
    setMatchSelectedJob(job);
    setActiveTab('ai-recommendations');
    showToast(`Ready to match against '${job.title}'`);
  };

  // --- Company Service Actions (Member 3) ---
  const handleUpdateCompany = async (updateData) => {
    if (!currentCompany) return;
    const res = await companyApi.updateCompany(currentCompany.id, updateData);
    if (res.success) {
      setCurrentCompany(res.data);
      setCompanies(companies.map(c => c.id === currentCompany.id ? res.data : c));
      showToast('Company details updated successfully!');
    }
  };

  const handleUpdateCompanyProfile = async (profileData) => {
    if (!currentCompany) return;
    const res = await companyApi.saveOrUpdateProfile(currentCompany.id, profileData);
    if (res.success) {
      const updated = { ...currentCompany, profile: res.data };
      setCurrentCompany(updated);
      setCompanies(companies.map(c => c.id === currentCompany.id ? updated : c));
      showToast('Company extended profile saved successfully!');
    }
  };

  const handleRegisterCompany = async (formData) => {
    const res = await companyApi.registerCompany(formData);
    if (res.success) {
      setCompanies([...companies, res.data]);
      setCurrentCompany(res.data);
      setActiveTab('company-dashboard');
      showToast(`Company '${res.data.companyName}' registered successfully!`);
    }
  };

  const handleDeleteCompany = async (companyId) => {
    const res = await companyApi.deleteCompany(companyId);
    if (res.success) {
      const remaining = companies.filter(c => c.id !== companyId);
      setCompanies(remaining);
      setCurrentCompany(remaining.length > 0 ? remaining[0] : null);
      showToast('Company deleted successfully.');
    }
  };

  const handleSelectCompanyFromDirectory = (comp) => {
    setCurrentCompany(comp);
    setActiveTab('company-dashboard');
    showToast(`Managing ${comp.companyName}`);
  };

  // --- Candidate Service Actions (Member 2) ---
  const handleUpdateCandidateProfile = async (formData) => {
    if (!candidateProfile) return;
    const res = await candidateApi.updateProfile(candidateProfile.id, formData);
    if (res.success) {
      setCandidateProfile(res.data);
      showToast('Candidate profile updated successfully!');
    }
  };

  const handleAddSkill = async (skillData) => {
    if (!candidateProfile) return;
    const res = await candidateApi.addSkill(candidateProfile.id, skillData);
    if (res.success) {
      setCandidateProfile({
        ...candidateProfile,
        skills: [...(candidateProfile.skills || []), res.data]
      });
      showToast(`Skill '${res.data.skillName}' added!`);
    }
  };

  const handleDeleteSkill = async (skillId) => {
    if (!candidateProfile) return;
    const res = await candidateApi.deleteSkill(candidateProfile.id, skillId);
    if (res.success) {
      setCandidateProfile({
        ...candidateProfile,
        skills: candidateProfile.skills.filter(s => s.id !== skillId)
      });
      showToast('Skill removed!');
    }
  };

  const handleAddEducation = async (eduData) => {
    if (!candidateProfile) return;
    const res = await candidateApi.addEducation(candidateProfile.id, eduData);
    if (res.success) {
      setCandidateProfile({
        ...candidateProfile,
        educations: [...(candidateProfile.educations || []), res.data]
      });
      showToast('Education record added!');
    }
  };

  const handleDeleteEducation = async (eduId) => {
    if (!candidateProfile) return;
    const res = await candidateApi.deleteEducation(candidateProfile.id, eduId);
    if (res.success) {
      setCandidateProfile({
        ...candidateProfile,
        educations: candidateProfile.educations.filter(e => e.id !== eduId)
      });
      showToast('Education record removed!');
    }
  };

  const handleAddExperience = async (expData) => {
    if (!candidateProfile) return;
    const res = await candidateApi.addExperience(candidateProfile.id, expData);
    if (res.success) {
      setCandidateProfile({
        ...candidateProfile,
        experiences: [...(candidateProfile.experiences || []), res.data]
      });
      showToast('Work experience record added!');
    }
  };

  const handleDeleteExperience = async (expId) => {
    if (!candidateProfile) return;
    const res = await candidateApi.deleteExperience(candidateProfile.id, expId);
    if (res.success) {
      setCandidateProfile({
        ...candidateProfile,
        experiences: candidateProfile.experiences.filter(e => e.id !== expId)
      });
      showToast('Experience record removed!');
    }
  };

  const handleSelectCandidateFromDirectory = (cand) => {
    setCandidateProfile(cand);
    setActiveTab('candidate-profile');
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
            <h2>Initializing AI Recruitment Platform Mesh...</h2>
          </div>
        ) : (
          <>
            {/* Tab 1: Unified Platform Dashboard */}
            {activeTab === 'dashboard' && (
              <DashboardOverview
                onNavigate={setActiveTab}
                totalJobs={jobs.length || 4}
                totalCompanies={companies.length || 3}
                totalCandidates={candidates.length || 4}
                resumesCount={resumes.length || 1}
              />
            )}

            {/* Tab 2: Job Catalog & Applications (Member 4 Integration) */}
            {activeTab === 'jobs' && (
              <JobDirectory
                onMatchJobWithResume={handleMatchFromJobDirectory}
                onShowToast={showToast}
              />
            )}

            {/* Tab 3: AI Resume Studio & NLP Parser (Member 5 Lead) */}
            {activeTab === 'ai-resume' && (
              <ResumeUploadView
                onMatchJob={handleMatchFromResumeStudio}
                onShowToast={showToast}
              />
            )}

            {/* Tab 4: AI Matching & Recommendations (Member 5 Lead) */}
            {activeTab === 'ai-recommendations' && (
              <AiRecommendationView
                preselectedResume={matchSelectedResume}
                preselectedJob={matchSelectedJob}
                onShowToast={showToast}
              />
            )}

            {/* Tab 5: Company Management (Member 3) */}
            {activeTab === 'company-dashboard' && (
              <CompanyDashboard
                company={currentCompany}
                onUpdateCompany={handleUpdateCompany}
                onUpdateProfile={handleUpdateCompanyProfile}
                onDeleteCompany={handleDeleteCompany}
                onOpenRegisterModal={() => setIsRegisterModalOpen(true)}
              />
            )}

            {activeTab === 'company-directory' && (
              <CompanyDirectory
                companies={companies}
                onSelectCompany={handleSelectCompanyFromDirectory}
                onOpenRegisterModal={() => setIsRegisterModalOpen(true)}
              />
            )}

            {/* Tab 6: Candidate Portal (Member 2) */}
            {activeTab === 'candidate-profile' && candidateProfile && (
              <CandidateDashboard
                profile={candidateProfile}
                onUpdateProfile={handleUpdateCandidateProfile}
                onAddSkill={handleAddSkill}
                onDeleteSkill={handleDeleteSkill}
                onAddEducation={handleAddEducation}
                onDeleteEducation={handleDeleteEducation}
                onAddExperience={handleAddExperience}
                onDeleteExperience={handleDeleteExperience}
              />
            )}

            {activeTab === 'candidate-directory' && (
              <CandidateDirectory
                candidates={candidates}
                onSelectCandidate={handleSelectCandidateFromDirectory}
              />
            )}

            {/* Tab 7: 5-Member Microservice Architecture & API Explorer */}
            {activeTab === 'api-info' && (
              <PlatformArchitecture />
            )}
          </>
        )}
      </main>

      {/* Global Auth Modal */}
      <AuthModal
        isOpen={isAuthModalOpen}
        onClose={closeAuthModal}
        initialMode={authModalMode}
      />

      {/* Global Modals */}
      <RegisterCompanyModal
        isOpen={isRegisterModalOpen}
        onClose={() => setIsRegisterModalOpen(false)}
        onRegister={handleRegisterCompany}
      />

      {/* Toast Alert */}
      {toastMessage && (
        <div className="toast-banner">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#34d399" strokeWidth="2.5"><path d="M20 6L9 17l-5-5"></path></svg>
          <span>{toastMessage}</span>
        </div>
      )}
    </div>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <MainAppContent />
    </AuthProvider>
  );
}
