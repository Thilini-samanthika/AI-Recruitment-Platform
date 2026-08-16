# Job Service (Member 4)

Port: `8084`  
Database: `job_db` (MySQL)  
Gateway Path: `/api/jobs/**`

## Endpoints to implement:
- `GET /api/jobs` - List open job postings (Public / Filtered)
- `POST /api/jobs` - Post a new job (Recruiter/Company only)
- `GET /api/jobs/{id}` - View job details
- `POST /api/jobs/{id}/apply` - Submit candidate job application
