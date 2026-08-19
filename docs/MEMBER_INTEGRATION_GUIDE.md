# 🤝 Member Integration Guide — AI Recruitment Platform

Welcome to the team! This guide explains how **Member 2 (Candidate)**, **Member 3 (Company)**, **Member 4 (Job)**, and **Member 5 (AI Resume & Frontend)** integrate with the **API Gateway (Port 8080)** and **Auth Service (Port 8081)**.

---

## 1. How the Gateway Protects Your Microservice

When a client makes a request to your service (e.g. `GET http://localhost:8080/api/candidates/profile`), the request arrives at the **API Gateway**:

1. The Gateway's `RateLimiter` verifies client request rates via Redis (20 req/s, 40 burst capacity).
2. The Gateway's `JwtAuthFilter` checks the `Authorization: Bearer <JWT_TOKEN>` header.
3. If invalid or missing, the Gateway returns `401 Unauthorized` before reaching your service.
4. If valid, the Gateway extracts user information and **forwards enriched HTTP headers** to your service:

| Header Name | Type | Description | Example |
|---|---|---|---|
| `X-User-Id` | String | The MongoDB ObjectId of the authenticated user | `66c3abc1234567890abcdef1` |
| `X-User-Email` | String | The verified email of the authenticated user | `john@example.com` |
| `X-User-Role` | String | The role assigned to the user | `ROLE_CANDIDATE`, `ROLE_COMPANY`, `ROLE_ADMIN` |

---

## 2. How to Read User Details in Spring Boot (Members 2, 3, 4)

In your Spring Boot controllers, you **do not** need to validate the JWT yourself. Simply inject the headers forwarded by the Gateway:

```java
@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(
            @RequestHeader(value = "X-User-Id", required = true) String userId,
            @RequestHeader(value = "X-User-Email", required = true) String email,
            @RequestHeader(value = "X-User-Role", required = true) String role) {

        // Your business logic using userId (String MongoDB ObjectId), email, and role
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
2. Pass the API Key in the `X-API-KEY` header in internal HTTP requests or OpenFeign clients:
   ```http
   GET /api/resume/parse
   X-API-KEY: sec_candidate_service_xyz123...
   ```

---

## 4. MongoDB 7.0 Connection Strings & Port Reference

The team has migrated all microservices to **MongoDB 7.0** using the **Database-per-Service** pattern:

| Member | Service | Service Port | Gateway Route Prefix | Database Container | Database Name | Standard Docker URI | Local Dev URI |
|---|---|---|---|---|---|---|---|
| **Member 1** | `auth-service` | `8081` | `/api/auth/**` | `auth-db` (27017) | `auth_db` | `mongodb://auth-db:27017/auth_db` | `mongodb://localhost:27017/auth_db` |
| **Member 2** | `candidate-service` | `8082` | `/api/candidates/**` | `candidate-db` (27018) | `candidate_db` | `mongodb://candidate-db:27017/candidate_db` | `mongodb://localhost:27018/candidate_db` |
| **Member 3** | `company-service` | `8083` | `/api/companies/**` | `company-db` (27019) | `company_db` | `mongodb://company-db:27017/company_db` | `mongodb://localhost:27019/company_db` |
| **Member 4** | `job-service` | `8084` | `/api/jobs/**` | `job-db` (27020) | `job_db` | `mongodb://job-db:27017/job_db` | `mongodb://localhost:27020/job_db` |
| **Member 5** | `ai-resume-service` | `8085` | `/api/resume/**`, `/api/ai/**` | `ai-db` (27021) | `ai_db` | `mongodb://ai-db:27017/ai_db` | `mongodb://localhost:27021/ai_db` |
| **Member 5** | `frontend` | `3000` | UI Direct / Gateway `8080` | N/A | N/A | N/A | N/A |

### In your microservice `application.yml`, set:
```yaml
spring:
  data:
    mongodb:
      uri: ${SPRING_DATA_MONGODB_URI:mongodb://${DB_HOST:localhost}:${DB_PORT:27017}/<your_database_name>}
      auto-index-creation: true
```

---

## 5. Running the Complete System

To run the whole system using Docker Compose:
```bash
docker-compose up --build
```

