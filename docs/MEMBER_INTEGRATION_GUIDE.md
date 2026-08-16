# 🤝 Member Integration Guide — AI Recruitment Platform

Welcome to the team! This guide explains how **Member 2 (Candidate)**, **Member 3 (Company)**, **Member 4 (Job)**, and **Member 5 (AI Resume & Frontend)** integrate with the **API Gateway (Port 8080)** and **Auth Service (Port 8081)**.

---

## 1. How the Gateway Protects Your Microservice

When a client makes a request to your service (e.g. `GET http://localhost:8080/api/candidates/profile`), the request arrives at the **API Gateway**:

1. The Gateway's `JwtAuthFilter` checks the `Authorization: Bearer <JWT_TOKEN>` header.
2. If invalid or missing, the Gateway returns `401 Unauthorized` before reaching your service.
3. If valid, the Gateway extracts user information and **forwards enriched HTTP headers** to your service:

| Header Name | Type | Description | Example |
|---|---|---|---|
| `X-User-Id` | String / Long | The database ID of the authenticated user | `1` |
| `X-User-Email` | String | The verified email of the authenticated user | `john@example.com` |
| `X-User-Role` | String | The role assigned to the user | `ROLE_CANDIDATE`, `ROLE_COMPANY`, `ROLE_ADMIN` |

---

## 2. How to Read User Details in Spring Boot (Members 2, 3, 4)

In your Spring Boot controllers, you **do not** need to validate the JWT yourself. Simply inject the headers from the Gateway:

```java
@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(
            @RequestHeader(value = "X-User-Id", required = true) Long userId,
            @RequestHeader(value = "X-User-Email", required = true) String email,
            @RequestHeader(value = "X-User-Role", required = true) String role) {

        // Your business logic using userId/email/role
        return ResponseEntity.ok(candidateService.getProfileByUserId(userId));
    }
}
```

---

## 3. Service-to-Service Internal Communication (API Key)

When your microservice needs to call another service internally (for example, `CandidateService` calling `AIResumeService` or `JobService`):

1. Generate an API Key via `POST http://localhost:8080/api/auth/api-keys`:
   ```json
   {
     "serviceName": "CANDIDATE_SERVICE"
   }
   ```
2. Pass the API Key in the `X-API-KEY` header in internal HTTP requests:
   ```http
   GET /api/resume/parse
   X-API-KEY: your-service-api-key-here
   ```

---

## 4. Port and Routing Reference

| Member | Service | Service Port | Gateway Route Prefix | Database |
|---|---|---|---|---|
| Member 1 | `auth-service` | `8081` | `/api/auth/**` | `auth_db` (3306) |
| Member 2 | `candidate-service` | `8082` | `/api/candidates/**` | `candidate_db` (3306) |
| Member 3 | `company-service` | `8083` | `/api/companies/**` | `company_db` (3306) |
| Member 4 | `job-service` | `8084` | `/api/jobs/**` | `job_db` (3306) |
| Member 5 | `ai-resume-service` | `8085` | `/api/resume/**`, `/api/ai/**` | N/A (FastAPI NLP) |
| Member 5 | `frontend` | `3000` | UI Direct / Gateway at `8080` | N/A (React / Next.js) |

---

## 5. Running the Complete System

To run the whole system using Docker Compose:
```bash
docker-compose up --build
```

