# Implementation Plan: Fundação do sistema do hotel de animais (Fase 1)

**Branch**: `001-fundacao-hotel-animais` | **Date**: 2026-05-04 | **Spec**: [spec.md](./spec.md)  
**Input**: Feature specification from `/specs/001-fundacao-hotel-animais/spec.md`

## Summary

Implementar a fundação do sistema com autenticação por sessão HTTP, dashboard inicial do diretor com indicadores de ocupação e faturação, e gestão de estado de limpeza de alojamentos. Esta fase estabelece controlo de acesso, navegação base, e as primeiras operações viáveis. Suportada por UC-01 (autenticação), RF-01 (dashboard operacional), e RD-01 (regras de disponibilidade).

## Technical Context

**Language/Version**: Java 21 + Spring Boot 3.3.5  
**Primary Dependencies**: Spring Web (MVC), Spring Security 6 (sessão HTTP + BCrypt), Thymeleaf (view), Spring Data JPA, Hibernate  
**Storage**: MySQL 8 (production), H2 (testing), Flyway (migrations)  
**Testing**: JUnit 5, Mockito 5, TestContainers (MySQL)  
**Target Platform**: Linux/Docker (Dockerfile + docker-compose.yml)  
**Project Type**: Web application (MVC monolith, layered architecture)  
**Performance Goals**: Dashboard < 2s load (RNF-01); Real-time availability checks; Session timeout configurable  
**Constraints**: Single user role per session; HTTP stateless communication; Concurrent requests on alojamento listing  
**Scale/Scope**: 5 role types; ~4-10 initial test alojamentos; ~100 max concurrent users expected in Phase 1

### Current Code State

Existem já no projeto:
- `HotelAnimaisApplication` — entry point configurado
- `SecurityConfig` — in-memory auth com 5 roles, BCrypt
- `AuthController`, `DashboardController`, `AlojamentoController`, `LimpezaController` (parcialmente preenchidos)
- Modelo: `Alojamento` com `EstadoLimpeza` enum
- Services: `AlojamentoService`, `LimpezaService`
- Templates Thymeleaf: login, dashboard, limpeza (estrutura base presente)
- Migrations Flyway: V1 (alojamento table), V2 (seed data)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Status | Evidence |
|-----------|--------|----------|
| **Domain Scope First** | ✓ OK | Todas as user stories (US-01, US-02, US-20, US-21) e requisitos (RF-01, RD-01) são do domínio hotel de animais e rastreáveis ao enunciado. Sem invenções. |
| **Scenario-Driven Requirements** | ✓ OK | Spec inclui acceptance scenarios quantificados (< 2s em RNF-01, ENUM values conhecidos). Não há termos vagos não quantificados. |
| **Modular Separation of Concerns** | ✓ OK | Arquitetura MVC em camadas (Presentation → Application → Domain → Data), conforme ADRs. SecurityConfig centraliza autenticação; AlojamentoService centraliza lógica de ocupação. |
| **Verification Before Expansion** | ✓ OK | Spec define 4 success criteria com caminhos de teste independentes (SC-001..SC-004). Todas as user stories têm acceptance scenarios. |
| **Data Integrity, Security, Operational Reliability** | ✓ OK | BCrypt para senhas; Spring Security para least privilege; RD-01 garante consistência de limpeza em ocupação; Logs e auditoria preparados. |

**Resultado**: Nenhuma violação. OK para proceder.

## Project Structure

### Documentation (this feature)

```text
specs/001-fundacao-hotel-animais/
├── spec.md              # Feature specification (existing)
├── plan.md              # This file (CREATED NOW)
├── research.md          # Phase 0: Resolved clarifications (TO BE CREATED)
├── data-model.md        # Phase 1: Domain entities and enums (TO BE CREATED)
├── quickstart.md        # Phase 1: Setup and test walkthrough (TO BE CREATED)
├── contracts/           # Phase 1: API/data contracts (TO BE CREATED)
│   ├── auth-contract.md
│   ├── dashboard-contract.md
│   └── limpeza-contract.md
├── checklists/
│   └── requirements.md   # Quality validation (existing)
└── tasks.md             # Phase 2: Actionable tasks (NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
PatasBigodesApp/
├── pom.xml                              # Maven configuration
├── Dockerfile                           # Docker image
├── docker-compose.yml                   # Local dev environment
├── Makefile                             # Build shortcuts
├── src/
│   ├── main/
│   │   ├── java/pt/hotel/animais/
│   │   │   ├── HotelAnimaisApplication.java
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java       (login/logout)
│   │   │   │   ├── DashboardController.java  (indicators)
│   │   │   │   ├── LimpezaController.java    (cleaning state)
│   │   │   │   ├── AlojamentoController.java (listing)
│   │   │   │   └── ModuloPlaceholderController.java
│   │   │   ├── domain/
│   │   │   │   ├── Alojamento.java
│   │   │   │   ├── Colaborador.java
│   │   │   │   └── enums/
│   │   │   │       ├── EstadoLimpeza.java
│   │   │   │       └── TipoColaborador.java
│   │   │   ├── repository/
│   │   │   │   └── AlojamentoRepository.java
│   │   │   ├── service/
│   │   │   │   ├── AlojamentoService.java
│   │   │   │   └── LimpezaService.java
│   │   │   └── util/
│   │   │       └── SecurityUtils.java (helper para user context)
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-mysql.properties
│   │       ├── db/migration/
│   │       │   ├── V1__create_alojamento.sql
│   │       │   └── V2__seed_alojamentos.sql
│   │       └── templates/
│   │           ├── fragments/
│   │           │   ├── head.html
│   │           │   ├── navbar.html
│   │           │   ├── sidebar.html
│   │           │   └── footer.html
│   │           ├── auth/
│   │           │   └── login.html
│   │           ├── dashboard/
│   │           │   └── index.html
│   │           ├── alojamento/
│   │           │   └── listar.html
│   │           ├── limpeza/
│   │           │   └── listar.html
│   │           └── placeholders/
│   │               └── modulo.html
│   └── test/
│       └── java/pt/hotel/animais/
│           ├── HotelAnimaisApplicationTests.java
│           ├── controller/
│           │   ├── AuthControllerTest.java
│           │   ├── DashboardControllerTest.java
│           │   └── LimpezaControllerTest.java
│           ├── service/
│           │   ├── AlojamentoServiceTest.java
│           │   └── LimpezaServiceTest.java
│           └── integration/
│               └── SecurityIntegrationTest.java
```

**Structure Decision**: Monolith layered architecture (Presentation → Application → Domain → Data), conforme [ADR-01](../../../docs/Etapa2/04-architecture-decisions/ADR-01-monolito-camadas.md). Package structure `pt.hotel.animais.*` maintains cohesion and allows incremental growth through Phases 2-5.

## Phase 0: Research & Clarifications

**Gate**: All NEEDS CLARIFICATION resolved before proceeding to Phase 1.

### Clarifications Resolved

This feature spec is well-defined:
- All user stories mapped to Etapa 1 documentation (US-01, US-02, US-20, US-21)
- All functional requirements linked (RF-01, RD-01, UC-01)
- All success criteria are measurable (SC-001..SC-004)
- Technical stack is defined (Java 21, Spring Boot 3.3.5, MySQL, Thymeleaf)
- Existing code is partially present and consistent

**No NEEDS CLARIFICATION markers found.**

### Research Output: `research.md`

A `research.md` file will be created with the following sections:
1. **Technical Stack Decisions** — Java 21 / Spring Boot 3.3.5 rationale, MySQL choice, Thymeleaf + Fragments
2. **Security Architecture** — HTTP session model, BCrypt hashing, Spring Security 6 filters, role mapping
3. **Performance Baseline** — Dashboard caching strategy, availability query optimization, RNF-01 compliance
4. **Testing Strategy** — Unit tests (Mockito), integration tests (TestContainers), security tests (with auth context)
5. **Migration Path** — Flyway setup, seeding strategy for local dev

**To be created as**: `specs/001-fundacao-hotel-animais/research.md`

---

## Phase 1: Design & Contracts

### 1. Domain Model (`data-model.md`)

**Entities to extract from spec**:

| Entity | Fields | Enums | Relations | Notes |
|--------|--------|-------|-----------|-------|
| **Alojamento** | `id` (PK), `identificacao` (VARCHAR), `estadoLimpeza` | PENDENTE, CONCLUIDO | — | Available = no active booking + estadoLimpeza = CONCLUIDO (RD-01) |
| **Colaborador** | `id` (PK), `nome`, `email`, `passwordHash`, `tipoColaborador` | DIRETOR, FUNCIONARIO_RECEPCAO, CUIDADOR, MEDICO_VETERINARIO, RESPONSAVEL_LIMPEZA | — | In-memory in Phase 1; persisted in Phase 5 |

**State Transitions**:
- EstadoLimpeza: PENDENTE → CONCLUIDO (via LimpezaController action)
- Session: logged-out → authenticated → logged-out (via AuthController)

**Constraints** (RD-01):
- Alojamento is available iff `estadoLimpeza = CONCLUIDO` AND no active booking exists
- Colaborador role determines dashboard view and available actions

**Output**: `specs/001-fundacao-hotel-animais/data-model.md` (will include Mermaid ER or text table)

### 2. Interface Contracts (`contracts/`)

**Contract 1**: Authentication API  
**Path**: `contracts/auth-contract.md`  
**Defines**: POST /login, POST /logout, session cookie handling, error responses

**Contract 2**: Dashboard API  
**Path**: `contracts/dashboard-contract.md`  
**Defines**: GET /dashboard (JSON payload with occupancy %, billing indicators, requires auth)

**Contract 3**: Cleaning API  
**Path**: `contracts/limpeza-contract.md`  
**Defines**: GET /limpeza/listar, POST /limpeza/marcar-concluido, state transition validation

**Note on Templates**: Contracts reference Thymeleaf forms and templates from mockups (wf01-login.html, wf02-dashboard-diretor.html, wf06-limpeza.html).

### 3. Quick Start Guide (`quickstart.md`)

**Outline**:
1. Clone repo and checkout branch `001-fundacao-hotel-animais`
2. Run `make dev` to start Docker containers (MySQL, app)
3. Login with test user (diretor / password)
4. Access dashboard at `http://localhost:8080/dashboard`
5. Test limpeza workflow: list pending, mark as done
6. Run tests: `mvn test`
7. Verify success criteria (SC-001..SC-004)

### 4. Agent Context Update

Update `.github/copilot-instructions.md` to point to this plan:

```markdown
<!-- SPECKIT START -->
For additional context about technologies to be used, project structure,
shell commands, and other important information, read:
specs/001-fundacao-hotel-animais/plan.md
<!-- SPECKIT END -->
```

---

## Phase 2: Task Generation

Phase 2 (task generation) is **not** part of `/speckit.plan`. It will be generated by `/speckit.tasks` command after this plan is reviewed and approved.

Expected task breakdown:
- Task 1: Finalize SecurityConfig and user seeding
- Task 2: Implement AuthController (login/logout)
- Task 3: Implement DashboardController and views
- Task 4: Implement LimpezaController and state transitions
- Task 5: Write integration tests (TestContainers)
- Task 6: Validate success criteria (SC-001..SC-004)

---

## Next Steps

1. ✅ **This plan is now complete.**
2. 📝 **Create `research.md`** — confirm technical choices and resolve any clarifications.
3. 📋 **Create `data-model.md`** — define entities, enums, constraints.
4. 📄 **Create `quickstart.md`** — setup and manual test walkthrough.
5. 📋 **Create `contracts/*.md`** — API and data contracts.
6. 🔄 **Update `.github/copilot-instructions.md`** with link to this plan.
7. ✅ **Commit plan.md to feature branch** `001-fundacao-hotel-animais`.
8. 📊 **Run `/speckit.tasks`** to generate `tasks.md` for implementation.
