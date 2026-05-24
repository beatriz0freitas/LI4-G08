# Tasks: Relatórios e Colaboradores (Spec 005)

**Input**: `spec.md`, `plan.md`, requisitos em `docs/Etapa1/02-requirements`

## Phase 1: Setup (Shared Infrastructure)

- [ ] T001 Create feature branch `005-relatorios-colaboradores` and push remote
- [ ] T002 Create contracts folder and API contract in specs/005-relatorios-colaboradores/contracts/contract.md
- [ ] T003 [P] Add Flyway migration placeholder: PatasBigodesApp/src/main/resources/db/migration/V005__relatorios_colaboradores.sql
- [ ] T004 [P] Add initial DTOs: PatasBigodesApp/src/main/java/.../dto/RelatorioRequest.java and ColaboradorDTO.java

---

## Phase 2: Foundational (Blocking Prerequisites)

- [ ] T005 Setup DB schema changes (entities/migrations) for `Colaborador`, `Pagamento`, `ServicoExtra` (implement JPA entities and migrations): PatasBigodesApp/src/main/java/.../entity/Colaborador.java, Pagamento.java, ServicoExtra.java
- [ ] T006 [P] Ensure authentication & roles exist and RNF-04 enforced: PatasBigodesApp/src/main/java/.../security/SecurityConfig.java (verify role constants and seed data)
- [ ] T007 [P] Create repositories: PatasBigodesApp/src/main/java/.../repository/ColaboradorRepository.java, PagamentoRepository.java, ServicoExtraRepository.java
- [ ] T008 Implement common pagination/filter utilities: PatasBigodesApp/src/main/java/.../util/Pagination.java
- [ ] T009 Create API routing/controllers scaffold: PatasBigodesApp/src/main/java/.../controller/RelatorioController.java and ColaboradorController.java

---

## Phase 3: User Story US-01 - Consultar disponibilidade e taxa de ocupação (Priority: P1)

**Goal**: Apresentar taxa de ocupação e contadores básicos no dashboard do diretor.

**Independent Test**: Usando dados de teste, aceder ao endpoint /api/dashboard/ocupacao e verificar campos retornados.

- [ ] T010 [US-01] Implement `IOcupacaoService`/`OcupacaoService` to compute occupancy percentage and counts: `PatasBigodesApp/src/main/java/.../service/IOcupacaoService.java` e `PatasBigodesApp/src/main/java/.../service/OcupacaoService.java`
- [ ] T011 [US-01] Add repository query to count occupied boxes and total boxes: PatasBigodesApp/src/main/java/.../repository/AlojamentoRepository.java
- [ ] T012 [US-01] Implement endpoint: GET `/api/dashboard/ocupacao` in PatasBigodesApp/src/main/java/.../controller/DashboardController.java
- [ ] T013 [US-01] Add Thymeleaf fragment for the dashboard summary: PatasBigodesApp/src/main/resources/templates/fragments/dashboard_occupacao.html
- [ ] T014 [US-01] Unit test for `OcupacaoService`: PatasBigodesApp/src/test/java/.../OcupacaoServiceTest.java

---

## Phase 4: User Story US-02 - Consultar indicadores de faturação e pagamentos (Priority: P1)

**Goal**: Agregar faturação por período e por método de pagamento.

**Independent Test**: Chamar endpoint `/api/indicadores/faturacao?start=...&end=...` e verificar agregações.

- [ ] T015 [US-02] Implement `IFaturacaoService`/`FaturacaoService` (sum by periodo and by metodoPagamento): `PatasBigodesApp/src/main/java/.../service/IFaturacaoService.java` e `PatasBigodesApp/src/main/java/.../service/FaturacaoService.java`
- [ ] T016 [US-02] Add `PagamentoRepository` queries to sum by date range and metodoPagamento: PatasBigodesApp/src/main/java/.../repository/PagamentoRepository.java
- [ ] T017 [US-02] Implement endpoint GET `/api/indicadores/faturacao` in PatasBigodesApp/src/main/java/.../controller/IndicadoresController.java
- [ ] T018 [US-02] Add dashboard panel fragment for faturação: PatasBigodesApp/src/main/resources/templates/fragments/dashboard_faturacao.html
- [ ] T019 [US-02] Unit test for `FaturacaoService`: PatasBigodesApp/src/test/java/.../FaturacaoServiceTest.java

---

## Phase 5: User Story US-03 - Gerir perfis de acesso dos colaboradores (Priority: P1)

**Goal**: CRUD de colaboradores com atribuição de `tipoColaborador` e desactivação.

**Independent Test**: Usar API `/api/colaboradores` para criar, editar, listar e desactivar um colaborador.

- [ ] T020 [US-03] Implement `Colaborador` JPA entity and migration (if not present): PatasBigodesApp/src/main/java/.../entity/Colaborador.java
- [ ] T021 [US-03] Implement `IColaboradorService`/`ColaboradorService` (create/edit/desactivar): `PatasBigodesApp/src/main/java/.../service/IColaboradorService.java` e `PatasBigodesApp/src/main/java/.../service/ColaboradorService.java`
- [ ] T022 [US-03] Implement `ColaboradorController` endpoints (POST, PUT, GET, DELETE[soft]): PatasBigodesApp/src/main/java/.../controller/ColaboradorController.java
- [ ] T023 [US-03] Implement Thymeleaf UI for colaboradores: PatasBigodesApp/src/main/resources/templates/colaboradores/list.html and edit.html
- [ ] T024 [US-03] Unit tests for `ColaboradorService`: PatasBigodesApp/src/test/java/.../ColaboradorServiceTest.java
- [ ] T025 [US-03] Seed script for default roles and an initial `DIRETOR` user: PatasBigodesApp/src/main/resources/data/seed-colaboradores.sql

---

## Phase 6: User Story US-04 - Gerar relatórios operacionais por período (Priority: P1) 🎯 MVP

**Goal**: Gerar relatórios agregados, permitir filtros e exportar CSV/PDF.

**Independent Test**: POST `/api/relatorios/generate` com filtros e obter o resumo/export.

- [ ] T026 [US-04] Implement `IRelatorioService`/`RelatorioService` with aggregation methods: `PatasBigodesApp/src/main/java/.../service/IRelatorioService.java` e `PatasBigodesApp/src/main/java/.../service/RelatorioService.java`
- [ ] T027 [US-04] Implement background job mechanism (optional sync/blocking fallback): PatasBigodesApp/src/main/java/.../jobs/RelatorioJob.java
- [ ] T028 [US-04] Implement CSV export util and endpoint GET `/api/relatorios/{id}/export/csv`: PatasBigodesApp/src/main/java/.../export/CsvExportService.java
- [ ] T029 [US-04] Implement PDF export util and endpoint GET `/api/relatorios/{id}/export/pdf`: PatasBigodesApp/src/main/java/.../export/PdfExportService.java
- [ ] T030 [US-04] Implement controller POST `/api/relatorios/generate` and GET `/api/relatorios/{id}`: PatasBigodesApp/src/main/java/.../controller/RelatorioController.java
- [ ] T031 [US-04] Thymeleaf view for Relatórios: PatasBigodesApp/src/main/resources/templates/relatorios/list.html and relatorio_detail.html
- [ ] T032 [US-04] Unit tests for `RelatorioService` aggregations: PatasBigodesApp/src/test/java/.../RelatorioServiceTest.java
- [ ] T033 [US-04] Contract/integration test to validate CSV columns and content: specs/005-relatorios-colaboradores/tests/integration/test_relatorio_export.feature

---

## Phase 7: User Story US-05 - Consultar histórico de estadias e pagamentos (Priority: P1)

**Goal**: Fornecer endpoints e vistas para histórico por reserva/estadia com paginação e filtros.

**Independent Test**: GET `/api/historico?reservaId=...` retorna eventos e pagamentos associados.

- [ ] T034 [US-05] Implement `IHistoricoService`/`HistoricoService` para agregar eventos de estadia e pagamentos: `PatasBigodesApp/src/main/java/.../service/IHistoricoService.java` e `PatasBigodesApp/src/main/java/.../service/HistoricoService.java`
- [ ] T035 [US-05] Implement repository queries para combinar `Estadia`, `Pagamento`, `ServicoExtra`: PatasBigodesApp/src/main/java/.../repository/EstadiaRepository.java
- [ ] T036 [US-05] Implement endpoint GET `/api/historico` and Thymeleaf view: PatasBigodesApp/src/main/java/.../controller/HistoricoController.java and PatasBigodesApp/src/main/resources/templates/historico/list.html
- [ ] T037 [US-05] Unit tests for `HistoricoService`: PatasBigodesApp/src/test/java/.../HistoricoServiceTest.java

---

## Phase N: Polish & Cross-Cutting Concerns

- [ ] T038 [P] Add logging, metrics and access audit for report generation and collaborator changes: PatasBigodesApp/src/main/java/.../aspect/AuditAspect.java
- [ ] T039 [P] Security hardening: ensure RBAC checks on all controllers (use `@PreAuthorize` annotations)
- [ ] T040 Update `spec.md` with final implementation decisions: specs/005-relatorios-colaboradores/spec.md
- [ ] T041 Create `quickstart.md` with example requests and export steps: specs/005-relatorios-colaboradores/quickstart.md
- [ ] T042 [P] Run manual QA scenario: follow Acceptance Tests in spec and record results in specs/005-relatorios-colaboradores/checklists/qa-results.md

## Auth migration tasks (In-memory -> DB)

- [ ] T043 [P] Create Flyway migration to add `colaborador` table: `PatasBigodesApp/src/main/resources/db/migration/V005__create_colaborador.sql`
- [ ] T044 [P] Implement `Colaborador` JPA entity and `ColaboradorRepository` if not present: `PatasBigodesApp/src/main/java/pt/hotel/animais/entity/Colaborador.java` and `.../repository/ColaboradorRepository.java`
- [ ] T045 [P] Implement `JpaUserDetailsService` (UserDetailsService) that loads users from `ColaboradorRepository`: `PatasBigodesApp/src/main/java/pt/hotel/animais/security/JpaUserDetailsService.java`
- [ ] T046 [P] Replace in-memory seeding in `SecurityConfig` with injected `UserDetailsService` bean and ensure `PasswordEncoder` bean present: `PatasBigodesApp/src/main/java/pt/hotel/animais/config/SecurityConfig.java`
- [ ] T047 [P] Add `ColaboradorSeeder` ApplicationRunner to seed default users at startup using `PasswordEncoder` when DB empty: `PatasBigodesApp/src/main/java/pt/hotel/animais/config/ColaboradorSeeder.java`
- [ ] T048 [P] Add integration tests for authentication: `PatasBigodesApp/src/test/java/pt/hotel/animais/auth/ColaboradorAuthIT.java` and `RelatoriosAuthIT` to validate RBAC and login flows

---

## Dependencies & Execution Order

- Complete Phase 1 and Phase 2 before implementing user stories (Phase 3+)
- User stories US-01..US-05 are P1 and can be implemented in the order above; some tasks within can run in parallel if different files are changed

---

## Summary
Total tasks: 42 (T001..T042)

Each task includes an exact path to implement; convert tasks to issues or assign to developers as needed.
