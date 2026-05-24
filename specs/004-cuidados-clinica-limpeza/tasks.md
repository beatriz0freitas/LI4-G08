# Tasks: Fase 4 — Operação diária, clínica e limpeza avançada

**Input**: Design documents from `/specs/004-cuidados-clinica-limpeza/`
**Prerequisites**: `plan.md` (required), `spec.md` (required for user stories), `research.md`, `data-model.md`

**Tests**: Incluídas porque a spec desta feature já define cenários de aceitação e testes de integração propostos.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US14, US15, US22)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Preparar a camada de persistência e migrações da feature

- [x] T001 [P] Add Flyway migration `PatasBigodesApp/src/main/resources/db/migration/V5__cuidados_clinica_limpeza.sql` with tables, foreign keys, indexes, and audit columns for the new feature

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Infrastructure comum que deve existir antes de qualquer user story

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T002 [P] Create shared domain model classes in `PatasBigodesApp/src/main/java/pt/hotel/animais/model/RegistoCuidado.java`, `PatasBigodesApp/src/main/java/pt/hotel/animais/model/ServicoExtra.java`, `PatasBigodesApp/src/main/java/pt/hotel/animais/model/AlteracaoEstadoSaude.java`, `PatasBigodesApp/src/main/java/pt/hotel/animais/model/IntervencaoClinica.java`, and `PatasBigodesApp/src/main/java/pt/hotel/animais/model/Nota.java`, plus `PatasBigodesApp/src/main/java/pt/hotel/animais/model/enums/TipoServicoExtra.java`
- [x] T003 [P] Create repository interfaces in `PatasBigodesApp/src/main/java/pt/hotel/animais/repository/RegistoCuidadoRepository.java`, `PatasBigodesApp/src/main/java/pt/hotel/animais/repository/ServicoExtraRepository.java`, `PatasBigodesApp/src/main/java/pt/hotel/animais/repository/AlteracaoEstadoSaudeRepository.java`, `PatasBigodesApp/src/main/java/pt/hotel/animais/repository/IntervencaoClinicaRepository.java`, and `PatasBigodesApp/src/main/java/pt/hotel/animais/repository/NotaRepository.java`
- [x] T004 [P] Add shared validation and ordering helpers in `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IRegraDominioService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/RegraDominioService.java` and `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IHistoricoService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/HistoricoService.java` for active-stay checks, author stamping, and paged history queries

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story US-14 - Consultar plano de cuidados (Priority: P1)

**Goal**: Disponibilizar o plano de cuidados de cada animal em estadia para consulta do cuidador

**Independent Test**: Abrir o ecrã do plano de cuidados para uma estadia ativa e confirmar que a lista apresentada corresponde ao animal correto e é acessível apenas a utilizadores autorizados

### Tests for User Story US-14

- [x] T005 [P] [US14] Add integration test in `PatasBigodesApp/src/test/java/pt/hotel/animais/controller/PlanoCuidadosControllerTest.java` for viewing the care plan of an active stay (skeleton, disabled)

### Implementation for User Story US-14

- [x] T006 [P] [US14] Create `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IPlanoCuidadosService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/PlanoCuidadosService.java` and `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/PlanoCuidadosDto.java` to fetch and shape the care plan data
- [x] T007 [US14] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/PlanoCuidadosController.java` and `PatasBigodesApp/src/main/resources/templates/cuidados/plano.html` to display the plan by animal and estadia

**Checkpoint**: User Story US-14 should now be fully functional and testable independently

---

## Phase 4: User Story US-15 - Registar cuidado diário (Priority: P1)

**Goal**: Permitir o registo diário de cuidados prestados durante a estadia

**Independent Test**: Criar um registo de cuidado para uma estadia ativa e confirmá-lo na ficha do animal e no histórico da estadia, com ordenação por data descendente

### Tests for User Story US-15

- [x] T008 [P] [US15] Add integration test in `PatasBigodesApp/src/test/java/pt/hotel/animais/controller/RegistoCuidadoControllerTest.java` for create-and-list care records (skeleton, disabled)

### Implementation for User Story US-15

- [x] T009 [P] [US15] Add request/form and DTO in `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/RegistoCuidadoFormDto.java` and `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/RegistoCuidadoDto.java`
- [x] T010 [US15] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IRegistoCuidadoService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/RegistoCuidadoService.java` with active-stay validation, author stamping, and descending ordering
- [x] T011 [US15] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/RegistoCuidadoController.java`, `PatasBigodesApp/src/main/resources/templates/cuidados/registos.html`, and the recent-care fragment in `PatasBigodesApp/src/main/resources/templates/animais/detail.html`
 - [x] T010 [US15] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IRegistoCuidadoService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/RegistoCuidadoService.java` with active-stay validation, author stamping, and descending ordering
 - [x] T011 [US15] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/RegistoCuidadoController.java`, `PatasBigodesApp/src/main/resources/templates/cuidados/registos.html`, and the recent-care fragment in `PatasBigodesApp/src/main/resources/templates/animais/detail.html`

**Checkpoint**: User Story US-15 should now be fully functional and testable independently

---

## Phase 5: User Story US-18 - Registar serviço extra (Priority: P1)

**Goal**: Registar serviços extra com custo e associá-los à reserva/estadia em curso

**Independent Test**: Criar um serviço extra durante a estadia e confirmar que o valor entra no cálculo do check-out

### Tests for User Story US-18

- [x] T012 [P] [US18] Add integration test in `PatasBigodesApp/src/test/java/pt/hotel/animais/controller/ServicoExtraControllerTest.java` for registering extras and verifying billing inclusion (skeleton, disabled)

### Implementation for User Story US-18

- [x] T013 [P] [US18] Add request/form and DTO in `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/ServicoExtraFormDto.java` and `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/ServicoExtraDto.java`
- [x] T014 [US18] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IServicoExtraService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/ServicoExtraService.java` and the billing hook in `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IPagamentoService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/PagamentoService.java`
 - [x] T014 [US18] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IServicoExtraService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/ServicoExtraService.java` and the billing hook in `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IPagamentoService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/PagamentoService.java`
- [x] T015 [US18] Extend `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/EstadiaController.java`, `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/ReservaController.java`, `PatasBigodesApp/src/main/resources/templates/estadias/checkin-checkout.html`, and `PatasBigodesApp/src/main/resources/templates/reservas/index.html` to register extras and show their totals

**Checkpoint**: User Story US-18 should now be fully functional and testable independently

---

## Phase 6: User Story US-22 - Consulta consolidada do historial clínico e operacional (Priority: P1)

**Goal**: Permitir consulta filtrada e paginada do historial clínico e operacional

**Independent Test**: Consultar o historial por animal, estadia e intervalo temporal e confirmar que os resultados devolvidos respeitam os filtros e a ordenação escolhida

### Tests for User Story US-22

- [x] T016 [P] [US22] Add integration test in `PatasBigodesApp/src/test/java/pt/hotel/animais/controller/HistoricoControllerTest.java` for filtered history consultation (skeleton, disabled)

### Implementation for User Story US-22

- [x] T017 [P] [US22] Add history filter/DTOs in `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/HistoricoFiltroDto.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/HistoricoItemDto.java`
- [x] T018 [US22] Implement consolidated query logic in `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IHistoricoService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/HistoricoService.java`
- [x] T019 [US22] Extend `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/HistoricoController.java` and `PatasBigodesApp/src/main/resources/templates/historico/list.html` with filters, pagination, and sort order (added consolidated events endpoint and template)

**Checkpoint**: User Story US-22 should now be fully functional and testable independently

---

## Phase 7: User Story US-16 - Registar alterações ao estado de saúde (Priority: P2)

**Goal**: Registar alterações ao estado de saúde e torná-las visíveis ao médico veterinário

**Independent Test**: Criar uma alteração de saúde para uma estadia ativa e verificar que aparece na timeline clínica e na vista recente do animal

### Tests for User Story US-16

- [x] T020 [P] [US16] Add integration test in `PatasBigodesApp/src/test/java/pt/hotel/animais/controller/ClinicaControllerTest.java` for recording health-state changes (skeleton, disabled)

### Implementation for User Story US-16

- [x] T021 [P] [US16] Add request/form and DTO in `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/AlteracaoEstadoSaudeFormDto.java` and `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/AlteracaoEstadoSaudeDto.java`
- [x] T022 [US16] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IAlteracaoEstadoSaudeService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/AlteracaoEstadoSaudeService.java` with role checks and recent-change listing
- [x] T023 [US16] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/ClinicaController.java` and `PatasBigodesApp/src/main/resources/templates/clinica/alteracoes.html`
 - [x] T023 [US16] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/ClinicaController.java` and `PatasBigodesApp/src/main/resources/templates/clinica/alteracoes.html`

**Checkpoint**: User Story US-16 should now be fully functional and testable independently

---

## Phase 8: User Story US-17 - Notas operacionais em reserva (Priority: P2)

**Goal**: Permitir adicionar notas operacionais a uma reserva para passagem de turno e contexto de atendimento

**Independent Test**: Adicionar uma nota a uma reserva e confirmá-la na vista da reserva e durante o check-in

### Tests for User Story US-17

- [x] T024 [P] [US17] Add integration test in `PatasBigodesApp/src/test/java/pt/hotel/animais/controller/NotaControllerTest.java` for adding and viewing reservation notes (skeleton, disabled)

### Implementation for User Story US-17

- [x] T025 [P] [US17] Add request/form and DTO in `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/NotaFormDto.java` and `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/NotaDto.java`
- [x] T026 [US17] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/service/INotaService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/NotaService.java` with author stamping and reservation linkage
- [x] T027 [US17] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/NotaController.java` and `PatasBigodesApp/src/main/resources/templates/reservas/notas.html`
 - [x] T026 [US17] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/service/INotaService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/NotaService.java` with author stamping and reservation linkage
 - [x] T027 [US17] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/NotaController.java` and `PatasBigodesApp/src/main/resources/templates/reservas/notas.html`

**Checkpoint**: User Story US-17 should now be fully functional and testable independently

---

## Phase 9: User Story US-23 - Registar intervenção clínica (Priority: P2)

**Goal**: Permitir ao médico veterinário registar intervenções clínicas e custo associado

**Independent Test**: Criar uma intervenção clínica com utilizador veterinário autenticado e confirmar que fica visível no historial clínico e na faturação complementar

### Tests for User Story US-23

- [x] T028 [P] [US23] Add integration test in `PatasBigodesApp/src/test/java/pt/hotel/animais/controller/ClinicaControllerTest.java` for veterinary interventions and billing inclusion (skeleton, disabled)

### Implementation for User Story US-23

- [x] T029 [P] [US23] Add request/form and DTO in `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/IntervencaoClinicaFormDto.java` and `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/IntervencaoClinicaDto.java`
- [x] T030 [US23] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IIntervencaoClinicaService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IntervencaoClinicaService.java` and the billing hook in `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IPagamentoService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/PagamentoService.java`
- [x] T031 [US23] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/ClinicaController.java` and `PatasBigodesApp/src/main/resources/templates/clinica/intervencoes.html`
 - [x] T030 [US23] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IIntervencaoClinicaService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IntervencaoClinicaService.java` and the billing hook in `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IPagamentoService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/PagamentoService.java`
 - [x] T031 [US23] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/ClinicaController.java` and `PatasBigodesApp/src/main/resources/templates/clinica/intervencoes.html`

**Checkpoint**: User Story US-23 should now be fully functional and testable independently

---

## Phase 10: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [x] T032 [P] Update feature documentation in `specs/004-cuidados-clinica-limpeza/plan.md`, `specs/004-cuidados-clinica-limpeza/research.md`, `specs/004-cuidados-clinica-limpeza/data-model.md`, and `specs/004-cuidados-clinica-limpeza/quickstart.md` after implementation details are finalized
- [ ] T033 [P] Run targeted regression tests in `PatasBigodesApp` with `mvn test -Dtest=PlanoCuidadosControllerTest,ClinicaControllerTest,HistoricoControllerTest`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel after the foundation is stable
  - Or sequentially in priority order (P1 before P2)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story US-14 (P1)**: Can start after Foundational (Phase 2) - no dependencies on other stories
- **User Story US-15 (P1)**: Can start after Foundational (Phase 2) - may reuse the same foundation but remains independently testable
- **User Story US-18 (P1)**: Can start after Foundational (Phase 2) - billing integration may reuse components introduced by US-15/US-22 but is still independently testable
- **User Story US-22 (P1)**: Can start after Foundational (Phase 2) - reads the shared history model and can be validated with seeded data
- **User Story US-16 (P2)**: Can start after Foundational (Phase 2) - independent of the billing flows
- **User Story US-17 (P2)**: Can start after Foundational (Phase 2) - independent of the clinical and billing flows
- **User Story US-23 (P2)**: Can start after Foundational (Phase 2) - may reuse billing hooks but should be independently testable

### Within Each User Story

- Tests (if included) MUST be written and FAIL before implementation
- DTOs and view models before services
- Services before controllers/views
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- Setup tasks marked [P] can run in parallel with each other if they touch different files
- Foundational tasks marked [P] can run in parallel
- Once Foundation is complete, user stories can proceed in parallel where the touched files do not overlap
- All tests for a user story marked [P] can run in parallel
- Different user stories can be worked on in parallel by different team members

---

## Parallel Example: User Story US-15

```bash
Task: "Add integration test in PatasBigodesApp/src/test/java/pt/hotel/animais/controller/RegistoCuidadoControllerTest.java for create-and-list care records"
Task: "Add request/form DTOs in PatasBigodesApp/src/main/java/pt/hotel/animais/dto/RegistoCuidadoFormDto.java and PatasBigodesApp/src/main/java/pt/hotel/animais/dto/RegistoCuidadoDto.java"

```

---

## Implementation Strategy

### MVP First (User Story US-14 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story US-14
4. **STOP and VALIDATE**: Test User Story US-14 independently
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story US-14 → Test independently → Deploy/Demo
3. Add User Story US-15 → Test independently → Deploy/Demo
4. Add User Story US-18 → Test independently → Deploy/Demo
5. Add User Story US-22 → Test independently → Deploy/Demo
6. Add User Story US-16 → Test independently → Deploy/Demo
7. Add User Story US-23 → Test independently → Deploy/Demo

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story US-14
   - Developer B: User Story US-15
   - Developer C: User Story US-18
   - Developer D: User Story US-22
   - Developer E: User Story US-16
   - Developer F: User Story US-23
3. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing the story
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid vague tasks, same-file conflicts, and cross-story dependencies that break independence