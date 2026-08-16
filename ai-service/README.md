# AI Resume Service (Member 5)

Port: `8085`  
Stack: Python (FastAPI / Spacy / Transformers / PyPDF)  
Gateway Path: `/api/resume/**`, `/api/ai/**`

## Endpoints to implement:
- `POST /api/resume/parse` - Upload and parse resume (PDF/DOCX)
- `POST /api/resume/match` - Match candidate resume with job description
- `GET /api/resume/health` - AI model health check
