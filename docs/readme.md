# AI-Recruitment-Platform

An AI-powered recruitment management system built with a microservice architecture. The platform connects candidates and companies through job postings, automated resume parsing, and AI-driven job matching, all fronted by a single React web app.

---

## System Architecture Overview

```
                                    ┌─────────────────────────────┐
                                    │    Client App (React UI)    │
                                    │    http://localhost:3000    │
                                    └──────────────┬──────────────┘
                                                   │
                                                   │ HTTP / REST
                                                   ▼
                    ┌─────────────────────────────────────────────────────────────┐
                    │            API GATEWAY (`api-gateway`: 8080)               │
                    │   - Spring Cloud Gateway                                    │
                    │   - JWT Auth & Token Validation Global Filter               │
                    │   - Internal Service-to-Service API Key Filter              │
                    │   - CORS & Centralized Error Handling                       │
                    │   - Injects X-User-Email, X-User-Role, X-User-Id Headers    │
                    └───────┬──────────────┬──────────────┬─────────────┬─────────┘
                            │              │              │             │
        ┌───────────────────┼──────────────┼──────────────┼─────────────┴─────────────┐
        ▼                   ▼              ▼              ▼                           ▼
┌───────────────┐   ┌──────────────┐ ┌───────────┐ ┌─────────────┐            ┌───────────────┐
│ Auth Service  │   │  Candidate   │ │  Company  │ │ Job Service │            │ AI Resume Svc │
│ (Port: 8081)  │   │ (Port: 8082) │ │(Port:8083)│ │(Port: 8084)│            │ (Port: 8085)  │
└───────┬───────┘   └──────┬───────┘ └─────┬─────┘ └──────┬──────┘            └───────┬───────┘
        │                  │               │              │                           │
        ▼                  ▼               ▼              ▼                           ▼
┌───────────────┐   ┌──────────────┐ ┌───────────┐ ┌─────────────┐            ┌───────────────┐
│ auth_db       │   │ candidate_db │ │company_db │ │ job_db      │            │ ai_db         │
│ (MySQL 3306)  │   │ (MySQL 3306) │ │(MySQL3306)│ │(MySQL 3306) │            │ (MySQL 3306)  │
└───────────────┘   └──────────────┘ └───────────┘ └─────────────┘            └───────────────┘
```

---

## Team & Microservice Allocations

| Member | Role & Assigned Module | Port | Technology Stack | Responsibilities |
|---|---|---|---|---|
| **Member 1** | **API Gateway + Auth Service + Integration Lead** | `8080` (Gateway)<br>`8081` (Auth) | Spring Cloud Gateway, Spring Boot 3.3.5, Spring Security, JWT (JJWT), MySQL, Docker | Centralized routing, JWT validation filter, API key filter, auth (register/login/validate), Docker Compose orchestration |
| **Member 2** | **Candidate Service** | `8082` | Spring Boot, JPA, MySQL | Candidate profiles, work experience, skill management, education records |
| **Member 3** | **Company Service** | `8083` | Spring Boot, JPA, MySQL | Company profile, recruiter management, department structure |
| **Member 4** | **Job Service** | `8084` | Spring Boot, JPA, MySQL | Job postings, requirements, application tracking, status workflows |
| **Member 5** | **AI Resume Service + Frontend Lead** | `8085` (AI)<br>`3000` (UI) | Spring Boot, Apache PDFBox, Apache POI, MySQL / React 18, Vite | Resume upload & parsing (PDF/DOCX), skill extraction, candidate-job matching & recommendation, unified frontend UI |

---

## Authentication & Authorization Flow

1. **User Registration (`POST /api/auth/register`)**
   - Accepts `email`, `password`, `roleName` (`ROLE_CANDIDATE`, `ROLE_COMPANY`, `ROLE_ADMIN`).
   - Hashes password using **BCrypt** with salt.
   - Assigns role and persists user in `auth_db.users`.
   - Returns a JWT token and user profile metadata.

2. **User Login (`POST /api/auth/login`)**
   - Verifies credentials against `auth_db`.
   - Generates a signed HMAC-SHA256 JWT containing `userId`, `email`, `role`, and expiration (24h).

3. **Gateway Verification (`api-gateway`)**
   - All incoming requests (except public auth endpoints) pass through `JwtAuthFilter`.
   - Gateway verifies the cryptographic signature and expiration.
   - Gateway extracts claims and forwards enriched headers to downstream microservices:
     - `X-User-Id` — authenticated user ID
     - `X-User-Email` — authenticated user email
     - `X-User-Role` — user role (`ROLE_CANDIDATE`, `ROLE_COMPANY`, etc.)

4. **Internal Service-to-Service Security (`ApiKeyFilter`)**
   - Direct internal service calls authenticate via an `X-API-KEY` header issued by `auth-service`.

---

## API Gateway Route Matrix

| Downstream Service | Path Pattern | Gateway Port | Target URL | Protected by JWT |
|---|---|---|---|---|
| Auth Service | `/api/auth/register`, `/api/auth/login` | `8080` | `http://auth-service:8081` | No (public) |
| Auth Service | `/api/auth/validate`, `/api/auth/users/**`, `/api/auth/api-keys` | `8080` | `http://auth-service:8081` | Yes |
| Candidate Service | `/api/candidates/**` | `8080` | `http://candidate-service:8082` | Yes |
| Company Service | `/api/companies/**` | `8080` | `http://company-service:8083` | Yes |
| Job Service | `/api/jobs/**` | `8080` | `http://job-service:8084` | Yes |
| AI Resume Service | `/api/resume/**`, `/api/ai/**` | `8080` | `http://ai-service:8085` | Yes |

---

## Prerequisites

- **Java JDK 17 or 21**
- **Apache Maven 3.9+**
- **Docker & Docker Compose**
- **MySQL 8.0+** (only needed if running services locally without Docker)
- **Node.js 18+** (for the frontend)

---

## Quick Start

### 1. Run everything with Docker Compose
```bash
docker-compose up --build -d
```
All databases, microservices, the API gateway, and the frontend start up automatically.

### 2. Run a service locally (example: Auth Service + Gateway)

**Step 1 — start MySQL and create the database**
```sql
CREATE DATABASE IF NOT EXISTS auth_db;
```

**Step 2 — run Auth Service**
```bash
cd auth-service
mvn clean spring-boot:run
```
> Auth Service: `http://localhost:8081`
> Swagger UI: `http://localhost:8081/swagger-ui.html`

**Step 3 — run API Gateway**
```bash
cd api-gateway
mvn clean spring-boot:run
```
> API Gateway: `http://localhost:8080`

**Step 4 — run the frontend**
```bash
cd frontend
npm install
npm run dev
```
> Frontend: `http://localhost:3000`

---

## OpenAPI / Swagger Documentation

Each backend service exposes its own Swagger UI once running:

| Service | Swagger UI |
|---|---|
| Auth Service | `http://localhost:8081/swagger-ui.html` |
| Candidate Service | `http://localhost:8082/swagger-ui.html` |
| Company Service | `http://localhost:8083/swagger-ui.html` |
| Job Service | `http://localhost:8084/swagger-ui.html` |
| AI Resume Service | `http://localhost:8085/swagger-ui.html` |

---

## Project Structure

```
AI-Recruitment-Platform/
│
├── api-gateway/         # Spring Cloud Gateway - routing, JWT/API key filters
├── auth-service/        # Register/login, JWT issuing, API key management
├── candidate-service/   # Candidate profiles, skills, education, experience
├── company-service/     # Company profiles and management
├── job-service/         # Job postings and applications
├── ai-service/          # Resume upload/parsing, skill extraction, job matching
├── frontend/             # React (Vite) unified web app
├── docker-compose.yml    # Orchestrates all services + databases + frontend
├── docs/                 # Architecture notes & member integration guide
└── README.md
```

---

## Git Branching Strategy

- `main` — stable, production-ready code
- `develop` — primary development integration branch
- `feature/api-gateway` — Member 1 (API Gateway)
- `feature/auth-service` — Member 1 (Auth Service)
- `feature/candidate-service` — Member 2 (Candidate Service)
- `feature/company-service` — Member 3 (Company Service)
- `feature/job-service` — Member 4 (Job Service)
- `feature/ai-service` — Member 5 (AI Resume Service)
- `feature/frontend` — Member 5 (Frontend)

---

## Testing

Each service includes unit tests for its core services and controllers (JUnit + Spring Boot Test). Run tests per service with:
```bash
cd <service-name>
mvn test
```

---

## License & Team

University Project — AI-Recruitment-Platform 
