# Quick Start: Fundação do sistema — Instalação e Teste Manual

**Created**: 2026-05-04 | **For**: Developers and QA | **Updated**: 2026-05-04

This guide will take you from a clean clone to running the first Phase 1 feature and verifying success criteria.

---

## Prerequisites

- Git
- Java 21 (JDK)
- Maven 3.9.x
- Docker + Docker Compose (for MySQL local dev)
- IDE: VS Code or IntelliJ (optional)

**Verify Java**:
```bash
java -version
# Expected: openjdk version "21.x.x"

mvn -version
# Expected: Apache Maven 3.9.x
```

---

# 1. Clone and Setup

```bash
# Clone the repository
git clone <repo-url>
cd LI4-G08

# Navigate to the application directory
cd PatasBigodesApp
```

---

# 2. Running the Application

The application can be run in two different ways:

- Option A — Fully with Docker
- Option B — Run MySQL in Docker and Spring Boot locally

---

# Option A — Run Everything with Docker

## Start the full stack

```bash
make up
```

Or manually:

```bash
docker compose up -d --build
```

## Verify containers

```bash
docker ps
```

Expected:

```text
hotelanimais-app   Up
hotelanimais-db    Up (healthy)
```

## Access the application

Open:

```text
http://localhost:8080
```

---

# Option B — Run MySQL with Docker and Spring Boot Locally

## Start only the database

```bash
make db-up
```

Or manually:

```bash
docker compose up -d db
```

## Wait for MySQL to become healthy

```bash
docker ps
```

Expected:

```text
hotelanimais-db   Up (...) (healthy)   127.0.0.1:3307->3306/tcp
```

You can also inspect database logs:

```bash
make logs-db
```

Expected output:

```text
... ready for connections
```

Press `Ctrl+C` to stop following logs.

---

## Build and run the Spring Boot application locally

### Using Make

```bash
make package
make run
```

### Using Maven directly

```bash
# Clean and build
mvn clean package -DskipTests

# Run Spring Boot with MySQL profile
DB_HOST=127.0.0.1 \
DB_PORT=3307 \
DB_NAME=hotelanimais \
DB_USERNAME=hoteluser \
DB_PASSWORD=hotelpass \
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

Expected output:

```text
Started HotelAnimaisApplication in X.XXX seconds
Tomcat started on port 8080
```

## Access the application

Open:

```text
http://localhost:8080
```

---

# Useful Commands

## Stop only the database

```bash
make db-stop
```

## Stop the full stack

```bash
make down
```

## Remove containers and volumes

```bash
make destroy
```

## Show logs

```bash
make logs
```

## Show database logs

```bash
make logs-db
```

## Open MySQL shell inside the container

```bash
make db-shell
```

---

## 5. Login and Verify SC-001

**Success Criterion**: *100% of 5 profiles can login and reach correct dashboard*

### Test Login for Each Role

Test with these credentials (seeded in-memory in Phase 1):

| Role | Username | Password | Expected Redirect |
|------|----------|----------|-------------------|
| DIRETOR | diretor | password | /dashboard (manager view) |
| FUNCIONARIO_RECEPCAO | recepcao | password | /dashboard (reception view) |
| CUIDADOR | cuidador | password | /dashboard (caregiver view) |
| MEDICO_VETERINARIO | veterinario | password | /dashboard (vet view) |
| RESPONSAVEL_LIMPEZA | limpeza | password | /dashboard (cleaner view) |

**Manual Test for SC-001**:

1. **Login as DIRETOR**:
   - Enter username: `diretor`
   - Enter password: `password`
   - Click "Entrar"
   - **Expected**: Redirect to `/dashboard`

2. **Verify dashboard loads**:
   - Should see director-specific indicators
   - **Expected**: Page title contains "Dashboard"

3. **Logout**:
   - Click logout button
   - **Expected**: Redirect to login page

4. **Login as RESPONSAVEL_LIMPEZA**:
   - Enter username: `limpeza`
   - Enter password: `password`
   - Click "Entrar"
   - **Expected**: Redirect to `/dashboard` with different layout

5. **Repeat for all 5 roles**:
   - Each should authenticate successfully
   - Each should see role-appropriate dashboard
   - *Assertion*: SC-001 ✅ **PASS**

---

## 6. Verify Dashboard Load Time (SC-002)

**Success Criterion**: *Dashboard loads in < 2 seconds (RNF-01)*

### Browser DevTools Method

1. Open Dashboard (already logged in)
2. Open DevTools: `F12` or `Cmd+Option+I`
3. Go to **Networks** tab
4. Refresh page (`F5`)
5. Check first document request ("localhost:8080/dashboard")
   - Look for **Time × (total request time)**
   - Should be **< 2000 ms**
   - *Assertion*: SC-002 ✅ **PASS** (if < 2s)

### Server Logs Method

Look for query execution times in Spring Boot logs:
```
... Query took: 145ms
... Template rendering: 210ms
... Total response: 355ms
```

**Expected**: Total < 2000ms for 90th percentile of requests.

---

## 7. Verify Cleaning Management (SC-003)

**Success Criterion**: *Responsible for cleaning can list and mark alojamentos as clean without transaction errors*

### Manual Test for SC-003

1. **Login as RESPONSAVEL_LIMPEZA** (`limpeza` / `password`)

2. **Navigate to Cleaning**:
   - Click "Limpeza" in sidebar (or `/limpeza`)
   - **Expected**: See list of alojamentos

3. **View Pending and Completed**:
   - Should see columns: `Identificação`, `Estado de Limpeza`, `Ações`
   - Some alojamentos should have `PENDENTE`
   - Some should have `CONCLUIDO`

4. **Mark as Cleaned**:
   - Find an alojamento with state `PENDENTE`
   - Click "Marcar como Limpo" button
   - **Expected**: 
     - No error message
     - Page refreshes
     - Alojamento now shows `CONCLUIDO`

5. **Verify in Dashboard**:
   - Go back to Dashboard
   - Cleaned alojamento should now count toward available accommodation
   - *Assertion*: SC-003 ✅ **PASS**

---

## 8. Verify RD-01 Implementation (SC-004)

**Success Criterion**: *Alojamento with `PENDENTE` cleaning does not appear as available (RD-01)*

### Manual/API Test for SC-004

1. **Using Dashboard**:
   - Login as **DIRETOR**
   - Check "Ocupação" indicator
   - Should NOT count alojamentos with `estadoLimpeza = PENDENTE`
   - Count should match only `CONCLUIDO` items

2. **Using API** (optional, for QA with Postman/curl):
   ```bash
   # Assuming API will expose availability endpoint (Phase 2)
   curl -b "JSESSIONID=<session-cookie>" \
        http://localhost:8080/api/alojamentos/disponibilidade
   
   # Expected response: count does not include PENDENTE estado
   ```

3. **Database Check** (optional):
   ```bash
   docker exec mysql mysql -u root -proot hotel_animais -e \
       "SELECT COUNT(*) AS total_alojamentos, \
               SUM(estado_limpeza='CONCLUIDO') AS disponivel \
        FROM alojamento;"
   ```

   *Assertion*: SC-004 ✅ **PASS**

---

## 9. Run Unit and Integration Tests

```bash
# From PatasBigodesApp/
mvn test

# Or specific test classes:
mvn test -Dtest=AuthControllerTest,DashboardControllerTest,LimpezaControllerTest
```

**Expected**:
```
[INFO] Tests run: 45, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Key Test Suites

| Test Class | Purpose | Min Coverage |
|-----------|---------|--------------|
| `AuthControllerTest` | Login, logout, redirects | 90% |
| `DashboardControllerTest` | Dashboard rendering, indicators | 85% |
| `LimpezaControllerTest` | List alojamentos, mark clean | 90% |
| `AlojamentoServiceTest` | Availability logic, RD-01 | 95% |
| `LimpezaServiceTest` | State transitions | 90% |
| `SecurityIntegrationTest` | End-to-end auth + access control | 80% |

---

## 10. Verify Success Criteria Summary

| Criterion | Test Method | Status |
|-----------|------------|--------|
| **SC-001** | Manual login for 5 roles | ✅ PASS / ❌ FAIL |
| **SC-002** | Dashboard < 2s (DevTools or logs) | ✅ PASS / ❌ FAIL |
| **SC-003** | Mark alojamento as clean (RESPONSAVEL_LIMPEZA) | ✅ PASS / ❌ FAIL |
| **SC-004** | RD-01 rule: no PENDENTE in availability | ✅ PASS / ❌ FAIL |

**Release Gate**: All 4 criteria must be **PASS** to proceed to Phase 2.

---

## 11. Troubleshooting

### Port 8080 Already in Use
```bash
# Kill process on port 8080
lsof -i :8080
kill -9 <PID>

# Or use different port
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### MySQL Connection Refused
```bash
# Check Docker containers
docker ps

# Restart services
docker compose restart db

# Wait 10 seconds for MySQL to fully start
sleep 10
```

### Port 3306 Already in Use
The MySQL container is published on `127.0.0.1:3307` by default to avoid conflicts with a local MySQL/MariaDB service. To choose another host port:

```bash
HOST_DB_PORT=3308 make up
```

### Login Page Shows 403 Forbidden
- Verify SecurityConfig is loaded
- Check that in-memory users are created in SecurityConfig bean
- Look for logs: "User registered: diretor"

### Dashboard Loads But No Indicators
- Check application logs for SQL errors
- Verify Flyway migrations ran: `SELECT * FROM alojamento;` in MySQL
- Ensure AlojamentoService is correctly wired in DashboardController

### Tests Fail with Connection Timeout
- Ensure Docker containers are running: `docker ps`
- TestContainers will start MySQL automatically, but check logs:
  ```bash
  docker logs <container-id>
  ```

---

## 12. Next Steps

Once all success criteria pass:

1. **Commit to feature branch**:
   ```bash
   git add .
   git commit -m "feat: Phase 1 foundation — auth, dashboard, cleaning"
   git push origin 001-fundacao-hotel-animais
   ```

2. **Create Pull Request**:
   - Title: "Phase 1: Fundação do sistema (spec 001)"
   - Description: Link spec.md and list SC-001..SC-004 as checklist

3. **Proceed to Phase 2**:
   - Create spec 002 (Tutores, Animais, Reservas)
   - Follow same process: research → data-model → contracts → implementation

---

## Additional Resources

- **Spec**: [spec.md](./spec.md)
- **Plan**: [plan.md](./plan.md)
- **Research**: [research.md](./research.md)
- **Data Model**: [data-model.md](./data-model.md)
- **Architecture Decision Records**: [docs/Etapa2/04-architecture-decisions/](../../../docs/Etapa2/04-architecture-decisions/)
- **Sprint Boot 3.3.5 Docs**: https://spring.io/projects/spring-boot
- **Spring Security 6**: https://spring.io/projects/spring-security
- **Thymeleaf**: https://www.thymeleaf.org/

---

## Contact & Support

For issues or clarifications on Phase 1:
- Reference: [Etapa1 User Stories](../../../docs/Etapa1/01-user-stories/user-stories.md)
- Questions: Check [docs/Etapa2/04-architecture-decisions/](../../../docs/Etapa2/04-architecture-decisions/) (ADRs)
- Technical: See [research.md](./research.md) for design decisions

---

**Status**: ✅ **Ready to test** — All success criteria defined and measurable.
