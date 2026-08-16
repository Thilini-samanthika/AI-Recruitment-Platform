# Candidate Service (Member 2) — AI Recruitment Platform

The **Candidate Service** is a Spring Boot microservice responsible for candidate profile management, technical skills, education records, and work experience histories.

- **Port:** `8082`
- **Database:** `candidate_db` (MySQL on port 3307 / 3306)
- **API Gateway Path:** `http://localhost:8080/api/candidates/**`
- **OpenAPI / Swagger UI:** `http://localhost:8082/swagger-ui.html`
- **OpenAPI JSON Docs:** `http://localhost:8082/v3/api-docs`

---

## 1. Database Schema

- **`candidate`**: `id` (PK), `user_id` (FK to auth_service), `full_name`, `phone`, `address`, `headline`, `summary`, `created_at`, `updated_at`
- **`skills`**: `id` (PK), `candidate_id` (FK), `skill_name`, `proficiency_level` (BEGINNER, INTERMEDIATE, ADVANCED, EXPERT)
- **`education`**: `id` (PK), `candidate_id` (FK), `institution`, `degree`, `field_of_study`, `start_date`, `end_date`
- **`experience`**: `id` (PK), `candidate_id` (FK), `company_name`, `job_title`, `start_date`, `end_date`, `description`

---

## 2. API Endpoints Reference

### Candidate Profile
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/candidates` | Create a new candidate profile |
| `GET` | `/api/candidates` | List all candidates |
| `GET` | `/api/candidates/{id}` | Get candidate details by ID |
| `GET` | `/api/candidates/me` | Get current candidate profile via `X-User-Id` |
| `GET` | `/api/candidates/user/{userId}` | Get candidate profile by Auth User ID |
| `PUT` | `/api/candidates/{id}` | Update candidate details |
| `DELETE` | `/api/candidates/{id}` | Delete candidate profile |

### Skills Management
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/candidates/{id}/skills` | Add skill to candidate |
| `GET` | `/api/candidates/{id}/skills` | List candidate's skills |
| `DELETE` | `/api/candidates/{id}/skills/{skillId}` | Delete a candidate skill |

### Education History
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/candidates/{id}/education` | Add education entry |
| `GET` | `/api/candidates/{id}/education` | List candidate's education |
| `DELETE` | `/api/candidates/{id}/education/{educationId}` | Delete an education entry |

### Experience History
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/candidates/{id}/experience` | Add work experience entry |
| `GET` | `/api/candidates/{id}/experience` | List candidate's experience |
| `DELETE` | `/api/candidates/{id}/experience/{experienceId}` | Delete an experience entry |

---

## 3. Security & Authentication

- **API-Key Security**: Direct service-to-service calls must include `X-API-KEY: candidate-service-secret-key-12345` (or configured `CANDIDATE_SERVICE_KEY`).
- **Gateway Forwarded Headers**: API Gateway forwards `X-User-Id`, `X-User-Email`, and `X-User-Role`. The Candidate Service validates that candidates can only modify their own profile (or admins).

---

## 4. Running & Testing Locally

### Run Unit & Integration Tests:
```bash
mvn test -f candidate-service/pom.xml
```

### Run Locally with Spring Boot:
```bash
mvn spring-boot:run -f candidate-service/pom.xml
```

### Build Docker Image:
```bash
docker build -t candidate-service ./candidate-service
```
