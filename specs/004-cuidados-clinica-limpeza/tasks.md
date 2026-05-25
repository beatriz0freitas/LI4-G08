# Tasks: Fase 4 — Operação diária, clínica e limpeza avançada

**Input**: Design documents from `/specs/004-cuidados-clinica-limpeza/`
**Prerequisites**: `plan.md` (required), `spec.md` (required for user stories), `research.md`, `data-model.md`

**Tests**: Incluídas porque a spec desta feature já define cenários de aceitação e testes de integração propostos.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

**Last Updated**: 2026-05-25 (LAC-02 Clarifications applied) — Nova estrutura para suportar `PlanoCuidados` e `TarefaCuidado`.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US14, US15, US22)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Preparar a camada de persistência e migrações da feature

**⚠️ CHANGES**: Migration agora inclui `PlanoCuidados` e `TarefaCuidado` (novas tabelas por LAC-02)

- [ ] T001 [P] **UPDATE** Flyway migration `PatasBigodesApp/src/main/resources/db/migration/V5__cuidados_clinica_limpeza.sql` with **expanded** DDL including `plano_cuidados`, `tarefa_cuidado`, `registo_cuidado`, `servico_extra`, `intervencao_clinica`, `nota`, `alteracao_estado_saude` tables; foreign keys, indexes (including `idx_pc_animal`, `idx_pc_estadia`, `idx_tc_plano`, `idx_rc_datahora`), and audit columns for all entities

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Infrastructure comum que deve existir antes de qualquer user story

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

**⚠️ CHANGES**: Domain model classes agora incluem `PlanoCuidados`, `TarefaCuidado`, e enums associados (LAC-02)

- [ ] T002 [P] **UPDATE** Create shared domain model classes in `PatasBigodesApp/src/main/java/pt/hotel/animais/model/`:
  - `PlanoCuidados.java` (NOVA — por LAC-02)
  - `TarefaCuidado.java` (NOVA — por LAC-02)
  - `PrioridadePlano.java` (NOVO ENUM)
  - `PeriodicidadeTarefa.java` (NOVO ENUM)
  - `RegistoCuidado.java` (existente)
  - `ServicoExtra.java` (existente)
  - `AlteracaoEstadoSaude.java` (existente)
  - `IntervencaoClinica.java` (existente)
  - `Nota.java` (existente)
  - `enums/TipoServicoExtra.java` (existente)

- [ ] T003 [P] **UPDATE** Create repository interfaces in `PatasBigodesApp/src/main/java/pt/hotel/animais/repository/`:
  - `PlanoCuidadosRepository.java` (NOVA — por LAC-02) with methods: `findByEstadiaId()`, `findByAnimalId(Pageable)`, `findUniqueActiveByEstadiaId()`
  - `TarefaCuidadoRepository.java` (NOVA — por LAC-02) with methods: `findByPlanoCuidadosId()`, `findByPlanoCuidadosIdAndConcluida()`
  - `RegistoCuidadoRepository.java` (existente)
  - `ServicoExtraRepository.java` (existente)
  - `AlteracaoEstadoSaudeRepository.java` (existente)
  - `IntervencaoClinicaRepository.java` (existente)
  - `NotaRepository.java` (existente)

- [ ] T004 [P] **UPDATE** Add shared validation and ordering helpers in `PatasBigodesApp/src/main/java/pt/hotel/animais/service/`:
  - `IRegraDominioService.java` e `RegraDominioService.java` — add new method `validarPlanoCuidadosAtivo(Long planoCuidadosId)` and `mudarPrioridadePlano(Long planoCuidadosId, PrioridadePlano novaPrioridade)` (para integração com US-16)
  - `IHistoricoService.java` e `HistoricoService.java` — update for consolidated history queries com support para `PlanoCuidados` events

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 2b: Foundational — Plan de Cuidados (New Blocking Prerequisites for US-14)

**Purpose**: Estrutura de base para o plano dinâmico de cuidados

**⚠️ CRITICAL**: US-14 depende destas tasks. Deve completar-se **após T002/T003/T004** e **antes de T005/T006/T007**

- [ ] T006a [P] Create service interfaces and implementations in `PatasBigodesApp/src/main/java/pt/hotel/animais/service/`:
  - `IPlanoCuidadosService.java` com métodos:
    - `criarPlanoParaEstadia(Long estadiaId, Long animalId): PlanoCuidadosDto` — herda histórico do animal, cria cópia ajustável
    - `obterPlanoPorEstadia(Long estadiaId): PlanoCuidadosDto` — fetch plano ativo
    - `listarPlanosDoAnimal(Long animalId, Pageable): Page<PlanoCuidadosDto>` — histórico do animal
    - `adicionarTarefa(Long planoCuidadosId, TarefaCuidadoFormDto): TarefaCuidadoDto` — dinâmico
    - `marcarTarefaConcluida(Long tarefaId, Long autorId): void` — auditado
    - `adicionarInstrucoes(Long planoCuidadosId, String instrucoes, Long autorId): void` — dinâmico
    - `atualizarPrioridade(Long planoCuidadosId, PrioridadePlano novaPrioridade, Long autorId): void` — hook automático (chamado por US-16)
    - `encerrarPlano(Long planoCuidadosId): void` — chamado no check-out
  - `PlanoCuidadosService.java` (implementação com validações e auditoria)

- [ ] T006b [P] Create DTOs in `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/`:
  - `PlanoCuidadosDto.java` (id, animalId, estadiaId, dataInicio, prioridade, instrucoes, tarefas List)
  - `PlanoCuidadosFormDto.java` (para criar/editar plano — atualmente read-only via herança, mas suporta adicionar instruções)
  - `TarefaCuidadoDto.java` (id, tipo, descricao, periodicidade, dataHora, concluida)
  - `TarefaCuidadoFormDto.java` (para criar/editar tarefa)

**Checkpoint**: Plan de Cuidados infrastructure ready - US-14 can now proceed

---

## Phase 3: User Story US-14 - Consultar plano de cuidados (Priority: P1)

**Goal**: Disponibilizar o plano de cuidados de cada animal em estadia para consulta do cuidador

**Independent Test**: Abrir o ecrã do plano de cuidados para uma estadia ativa e confirmar que: (1) tarefas estruturadas aparecem, (2) instruções da reserva/animal são visíveis, (3) prioridade reflete estado de saúde atual, (4) apenas cuidadores autorizados conseguem aceder

**⚠️ DEPENDENCY**: Depende de T006a/T006b (Plan infrastructure)

### Tests for User Story US-14

- [ ] T005 [P] [US14] Add integration test in `PatasBigodesApp/src/test/java/pt/hotel/animais/controller/PlanoCuidadosControllerTest.java` for viewing the care plan of an active stay, including:
  - Cenário: plano com prioridade ROTINA (padrão)
  - Cenário: prioridade muda para CRITICO quando AlteracaoEstadoSaude com severidade CRITICO é criada
  - Cenário: apenas cuidadores/veterinários conseguem visualizar
  - (skeleton, disabled)

### Implementation for User Story US-14

- [ ] T006 [P] [US14] Extend `IPlanoCuidadosService` e `PlanoCuidadosService` (criadas em T006a) with métodos de consulta e lógica de priorização. Criar `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/PlanoCuidadosResponseDto.java` para formato de resposta (inclui tarefas + instruções + prioridade visual)

- [ ] T007 [US14] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/PlanoCuidadosController.java` with endpoints:
  - `GET /cuidados/plano/{estadiaId}` — exibe plano com tarefas e prioridade
  - `POST /cuidados/plano/{planoCuidadosId}/instrucoes` — adiciona notas (dinâmico)
  - Implementar `PatasBigodesApp/src/main/resources/templates/cuidados/plano.html` with:
    - Lista de tarefas estruturadas com checkbox
    - Campo de prioridade (com cor/ícone conforme ROTINA/URGENTE/CRITICO)
    - Seção de instruções (notas livres)
    - Histórico de registos de cuidado (link para US-15 dados)

**Checkpoint**: User Story US-14 should now be fully functional and testable independently

---

## Phase 4: User Story US-15 - Registar cuidado diário (Priority: P1)

**Goal**: Permitir o registo diário de cuidados prestados durante a estadia

**Independent Test**: Criar um registo de cuidado para uma estadia ativa e confirmá-lo na ficha do animal e no histórico da estadia, com ordenação por data descendente

### Tests for User Story US-15

- [ ] T008 [P] [US15] Add integration test in `PatasBigodesApp/src/test/java/pt/hotel/animais/controller/RegistoCuidadoControllerTest.java` for create-and-list care records (skeleton, disabled)

### Implementation for User Story US-15

- [ ] T009 [P] [US15] Add request/form and DTO in `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/RegistoCuidadoFormDto.java` and `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/RegistoCuidadoDto.java`
- [ ] T010 [US15] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IRegistoCuidadoService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/RegistoCuidadoService.java` with active-stay validation, author stamping, and descending ordering
- [ ] T011 [US15] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/RegistoCuidadoController.java`, `PatasBigodesApp/src/main/resources/templates/cuidados/registos.html`, and the recent-care fragment in `PatasBigodesApp/src/main/resources/templates/animais/detail.html`

**Checkpoint**: User Story US-15 should now be fully functional and testable independently

---

## Phase 5: User Story US-18 - Registar serviço extra (Priority: P1)

**Goal**: Registar serviços extra com custo e associá-los à reserva/estadia em curso

**Independent Test**: Criar um serviço extra durante a estadia e confirmar que o valor entra no cálculo do check-out

### Tests for User Story US-18

- [ ] T012 [P] [US18] Add integration test in `PatasBigodesApp/src/test/java/pt/hotel/animais/controller/ServicoExtraControllerTest.java` for registering extras and verifying billing inclusion (skeleton, disabled)

### Implementation for User Story US-18

- [ ] T013 [P] [US18] Add request/form and DTO in `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/ServicoExtraFormDto.java` and `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/ServicoExtraDto.java`
- [ ] T014 [US18] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IServicoExtraService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/ServicoExtraService.java` and the billing hook in `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IPagamentoService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/PagamentoService.java`
- [ ] T015 [US18] Extend `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/EstadiaController.java`, `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/ReservaController.java`, `PatasBigodesApp/src/main/resources/templates/estadias/checkin-checkout.html`, and `PatasBigodesApp/src/main/resources/templates/reservas/index.html` to register extras and show their totals

**Checkpoint**: User Story US-18 should now be fully functional and testable independently

---

## Phase 6: User Story US-22 - Consulta consolidada do historial clínico e operacional (Priority: P1)

**Goal**: Permitir consulta filtrada e paginada do historial clínico e operacional

**Independent Test**: Consultar o historial por animal, estadia e intervalo temporal e confirmar que os resultados devolvidos respeitam os filtros e a ordenação escolhida

### Tests for User Story US-22

- [ ] T016 [P] [US22] Add integration test in `PatasBigodesApp/src/test/java/pt/hotel/animais/controller/HistoricoControllerTest.java` for filtered history consultation (skeleton, disabled)

### Implementation for User Story US-22

- [ ] T017 [P] [US22] Add history filter/DTOs in `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/HistoricoFiltroDto.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/HistoricoItemDto.java`
- [ ] T018 [US22] Implement consolidated query logic in `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IHistoricoService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/HistoricoService.java` with support for PlanoCuidados events
- [ ] T019 [US22] Extend `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/HistoricoController.java` and `PatasBigodesApp/src/main/resources/templates/historico/list.html` with filters, pagination, and sort order (added consolidated events endpoint and template)

**Checkpoint**: User Story US-22 should now be fully functional and testable independently

---

## Phase 7: User Story US-16 - Registar alterações ao estado de saúde (Priority: P2)

**Goal**: Registar alterações ao estado de saúde e torná-las visíveis ao médico veterinário

**Independent Test**: Criar uma alteração de saúde para uma estadia ativa e verificar que: (1) aparece no historial clínico, (2) **prioridade do plano muda automaticamente se severidade = CRITICO** (integração com LAC-02)

**⚠️ INTEGRATION**: T022 deve incluir hook que chama `IPlanoCuidadosService.atualizarPrioridade()` quando severidade = CRITICO

### Tests for User Story US-16

- [ ] T020 [P] [US16] Add integration test in `PatasBigodesApp/src/test/java/pt/hotel/animais/controller/ClinicaControllerTest.java` for recording health-state changes, including:
  - Cenário: criar alteração com severidade CRITICO
  - Cenário: validar que prioridade do plano correspondente muda para CRITICO
  - (skeleton, disabled)

### Implementation for User Story US-16

- [ ] T021 [P] [US16] Add request/form and DTO in `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/AlteracaoEstadoSaudeFormDto.java` and `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/AlteracaoEstadoSaudeDto.java`
- [ ] T022 [US16] **UPDATE** Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IAlteracaoEstadoSaudeService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/AlteracaoEstadoSaudeService.java` with:
  - Role checks and recent-change listing
  - **NOVO (LAC-02)**: Hook automático — quando severidade = CRITICO/URGENTE, chamar `IPlanoCuidadosService.atualizarPrioridade()` para o plano correspondente
  - Auditoria de quem mudou a prioridade do plano
- [ ] T023 [US16] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/ClinicaController.java` and `PatasBigodesApp/src/main/resources/templates/clinica/alteracoes.html`

**Checkpoint**: User Story US-16 should now be fully functional and testable independently

---

## Phase 8: User Story US-17 - Notas operacionais em reserva (Priority: P2)

**Goal**: Permitir adicionar notas operacionais a uma reserva para passagem de turno e contexto de atendimento

**Independent Test**: Adicionar uma nota a uma reserva e confirmá-la na vista da reserva e durante o check-in

### Tests for User Story US-17

- [ ] T024 [P] [US17] Add integration test in `PatasBigodesApp/src/test/java/pt/hotel/animais/controller/NotaControllerTest.java` for adding and viewing reservation notes (skeleton, disabled)

### Implementation for User Story US-17

- [ ] T025 [P] [US17] Add request/form and DTO in `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/NotaFormDto.java` and `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/NotaDto.java`
- [ ] T026 [US17] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/service/INotaService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/NotaService.java` with author stamping and reservation linkage
- [ ] T027 [US17] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/NotaController.java` and `PatasBigodesApp/src/main/resources/templates/reservas/notas.html`

**Checkpoint**: User Story US-17 should now be fully functional and testable independently

---

## Phase 9: User Story US-23 - Registar intervenção clínica (Priority: P2)

**Goal**: Permitir ao médico veterinário registar intervenções clínicas e custo associado

**Independent Test**: Criar uma intervenção clínica com utilizador veterinário autenticado e confirmar que fica visível no historial clínico e na faturação complementar

### Tests for User Story US-23

- [ ] T028 [P] [US23] Add integration test in `PatasBigodesApp/src/test/java/pt/hotel/animais/controller/ClinicaControllerTest.java` for veterinary interventions and billing inclusion (skeleton, disabled)

### Implementation for User Story US-23

- [ ] T029 [P] [US23] Add request/form and DTO in `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/IntervencaoClinicaFormDto.java` and `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/IntervencaoClinicaDto.java`
- [ ] T030 [US23] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IIntervencaoClinicaService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IntervencaoClinicaService.java` and the billing hook in `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IPagamentoService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/PagamentoService.java`
- [ ] T031 [US23] Implement `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/ClinicaController.java` and `PatasBigodesApp/src/main/resources/templates/clinica/intervencoes.html`

**Checkpoint**: User Story US-23 should now be fully functional and testable independently

---

## Phase 10: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T032 [P] Update feature documentation in `specs/004-cuidados-clinica-limpeza/plan.md`, `specs/004-cuidados-clinica-limpeza/research.md`, `specs/004-cuidados-clinica-limpeza/data-model.md`, and `specs/004-cuidados-clinica-limpeza/quickstart.md` after implementation details are finalized
- [ ] T033 [P] Run targeted regression tests in `PatasBigodesApp` with `mvn test -Dtest=PlanoCuidadosControllerTest,RegistoCuidadoControllerTest,ServicoExtraControllerTest,HistoricoControllerTest,ClinicaControllerTest`
- [ ] T034 [P] Replace the disabled skeleton tests with executable integration coverage in all controller tests for Plan, RegistoCuidado, ServicoExtra, Historico, and Clinica
- [ ] T035 [P] Add unit tests for service-layer validation, ordering, and hook integration (plan priority updates via AlteracaoEstadoSaude) in service test classes
- [ ] T036 [P] Add a system smoke test that traverses: criar plano → adicionar tarefa → registar cuidado → registar alteração de saúde CRITICA → validar prioridade do plano muda → registar serviço extra → consultar historial in `PatasBigodesApp/src/test/java/pt/hotel/animais/integration/CuidadosClinicaSystemSmokeTest.java`
- [ ] T037 [P] **NEW (LAC-02)** Verify complete auditoria trail for plan modifications: criar plano, adicionar tarefa, adicionar instrucoes, marcar tarefa concluida, alterar prioridade — todos com autor e timestamp registados e visiveis no histórico

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2 + 2b)**: Depends on Setup completion - BLOCKS all user stories
  - Phase 2b (Plan infrastructure) must complete **after** Phase 2 and **before** Phase 3 (US-14)
- **User Stories (Phase 3+)**: All depend on Foundational phases completion
  - US-14 specifically depends on Phase 2b (Plan infrastructure)
  - All other stories can proceed in parallel after Foundational
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies (Updated by LAC-02)

- **User Story US-14 (P1)**: Depends on Phase 2 + 2b (Plan infrastructure)
- **User Story US-15 (P1)**: Depends on Phase 2 only - independent of US-14
- **User Story US-18 (P1)**: Depends on Phase 2 only
- **User Story US-22 (P1)**: Depends on Phase 2 only
- **User Story US-16 (P2)**: Depends on Phase 2 + Plan infrastructure (for hook integration) — recommended to implement **after** US-14 is working
- **User Story US-17 (P2)**: Depends on Phase 2 only
- **User Story US-23 (P2)**: Depends on Phase 2 only

### Within Each User Story

- Tests (if included) MUST be written and FAIL before implementation
- DTOs and view models before services
- Services before controllers/views
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- Phase 1: All tasks can run in parallel (different files)
- Phase 2: T002, T003, T004 can run in parallel (different files)
- Phase 2b: T006a, T006b can run in parallel after Phase 2 complete
- Once Phase 2b complete, US-14 can proceed while other user stories (US-15, US-18, US-22) run in parallel
- US-16 should start only after US-14 demonstrates the plan dynamic behavior (to validate hook integration)

---

## Parallel Example: After Phase 2b Complete

```
T005 (US14 Test) — prepare test first
T006 (US14 Service) — implement service
T007 (US14 Controller) — implement controller/view
(Parallel with US-14:)
T008 (US15 Test)
T009 (US15 DTO)
T010 (US15 Service)
T012 (US18 Test)
T013 (US18 DTO)
T014 (US18 Service)
T016 (US22 Test)
T017 (US22 DTO)
T018 (US22 Service)
```

---

## Implementation Strategy

### MVP First (User Story US-14 Only)

1. Complete Phase 1: Setup (T001)
2. Complete Phase 2: Foundational (T002, T003, T004)
3. Complete Phase 2b: Plan Infrastructure (T006a, T006b)
4. Complete Phase 3: User Story US-14 (T005, T006, T007)
5. **STOP and VALIDATE**: Test US-14 independently, including plan dynamic behavior
6. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational + Plan Infrastructure → Foundation ready
2. Add User Story US-14 → Test independently → Deploy/Demo
3. Add User Stories US-15, US-18, US-22 in parallel → Test independently → Deploy/Demo
4. Add User Story US-16 with plan hook integration → Test independently → Deploy/Demo
5. Add User Stories US-17, US-23 in parallel → Test independently → Deploy/Demo
6. Polish + Regression Testing → Final demo

### Parallel Team Strategy (6+ developers)

1. Team completes Phase 1 + Phase 2 together
2. 2 developers complete Phase 2b (Plan infrastructure)
3. Once Phase 2b done:
   - Developer A: User Story US-14 (Plan consultation + dynamic behavior validation)
   - Developer B: User Story US-15 (Care records)
   - Developer C: User Story US-18 (Extra services)
   - Developer D: User Story US-22 (Consolidated history)
   - Developer E: User Story US-16 (Health changes + plan hook)
   - Developer F: User Story US-17 + US-23 (Notes + Clinical interventions)
4. Stories complete and integrate independently
5. Team validates hook integration (US-16 → Plan priority changes)

---

## Notes & Changes (LAC-02)

- **New entities**: `PlanoCuidados` and `TarefaCuidado` model the dynamic, hybrid plan with dual bindings
- **New Phase 2b**: Introduced to handle Plan infrastructure that blocks US-14
- **Updated T001, T002, T003, T004**: Expanded to cover new DDL, domain model, repositories, and services
- **New T006a, T006b**: Dedicated to Plan service and DTOs
- **Updated T022**: Includes hook for automatic priority updates when health severity changes
- **New T037**: Validates complete audit trail for plan modifications
- **Dependency change**: US-14 now depends on Phase 2b completion
- **Reordered tasks**: T005-T007 (US-14) now come after Phase 2b
- All [P] tasks remain parallelizable within their phase
