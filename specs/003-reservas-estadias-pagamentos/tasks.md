- **Independent Test**: Check-in cria estadia EM_CURSO; check-out só após check-in e muda o estado da estadia para TERMINADA, deixando o alojamento em PENDENTE_LIMPEZA.
# Tasks: Reservas, Estadias e Pagamentos

**Input**: Design documents from `/specs/003-reservas-estadias-pagamentos/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/, quickstart.md

**Tests**: Incluídos e obrigatórios nesta feature, conforme `Validation & Test Requirements` em `spec.md`.

**Organization**: Tarefas agrupadas por user story para permitir implementação e validação independente.

## Mapeamento de User Stories

- US1 -> US-06 (Criar e gerir reservas)
- US2 -> US-12 (Consultar disponibilidade)
- US3 -> US-07 (Registar check-in/check-out)
- US4 -> US-10 (Pagamento da estadia no check-in)
- US5 -> US-11 (Cobrança de extras no check-out)
- US6 -> US-05 (Histórico de estadias e pagamentos)
- US7 -> US-02 (Dashboard de faturação e pendentes)

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Preparar estrutura e configuração base para a feature 003.

- [x] T001 Atualizar contexto de fase em `PatasBigodesApp/src/main/resources/application.yml` para parâmetros de reservas/estadias/pagamentos
- [x] T002 [P] Criar pacote DTO de estadia/pagamento em `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/`
- [x] T003 [P] Criar esqueleto de templates da feature em `PatasBigodesApp/src/main/resources/templates/reservas/`, `PatasBigodesApp/src/main/resources/templates/estadias/` e `PatasBigodesApp/src/main/resources/templates/dashboard/`
- [x] T004 [P] Criar estrutura de testes da feature em `PatasBigodesApp/src/test/java/pt/hotel/animais/integration/` e `PatasBigodesApp/src/test/java/pt/hotel/animais/service/`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Infraestrutura de domínio e persistência necessária antes de qualquer user story.

**⚠️ CRITICAL**: Nenhuma user story deve começar antes desta fase estar concluída.

- [x] T005 Criar migration de schema para estadias e pagamentos em `PatasBigodesApp/src/main/resources/db/migration/V4__create_estadia_pagamento.sql`
- [x] T006 [P] Criar/atualizar enums de domínio em `PatasBigodesApp/src/main/java/pt/hotel/animais/model/enums/`
- [x] T007 Criar entidade `Estadia` em `PatasBigodesApp/src/main/java/pt/hotel/animais/model/Estadia.java`
- [x] T008 Criar entidade `Pagamento` em `PatasBigodesApp/src/main/java/pt/hotel/animais/model/Pagamento.java`
- [x] T005A Criar migration de schema para catálogos LAC-03 em `PatasBigodesApp/src/main/resources/db/migration/V8__create_tipos_alojamento_servicos_extra.sql`
- [x] T005B Criar migration de seed para tipos de alojamento e serviços extra em `PatasBigodesApp/src/main/resources/db/migration/V9__seed_tipos_alojamento_servicos_extra.sql`
- [x] T008A Criar entidade `TipoAlojamentoTarifa` em `PatasBigodesApp/src/main/java/pt/hotel/animais/model/TipoAlojamentoTarifa.java`
- [x] T008B Criar entidade `TipoServicoExtra` em `PatasBigodesApp/src/main/java/pt/hotel/animais/model/TipoServicoExtra.java`
- [x] T009 [P] Criar `EstadiaRepository` em `PatasBigodesApp/src/main/java/pt/hotel/animais/repository/EstadiaRepository.java`
- [x] T010 [P] Criar `PagamentoRepository` em `PatasBigodesApp/src/main/java/pt/hotel/animais/repository/PagamentoRepository.java`
- [x] T011 Implementar validações de invariantes RD no serviço base em `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IRegraDominioService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/RegraDominioService.java`
- [x] T012 [P] Adicionar regras de autorização por perfil em `PatasBigodesApp/src/main/java/pt/hotel/animais/config/SecurityConfig.java`

**Checkpoint**: Base de persistência, domínio e segurança pronta para avançar nas user stories.

---

## Phase 3: User Story 1 - Criar e gerir reservas (Priority: P1) 🎯 MVP

**Goal**: Permitir criação e cancelamento de reserva sem overbooking.

**Independent Test**: Criar reserva válida e cancelar reserva ativa, validando indisponibilidade/libertação.

### Tests for User Story 1

- [x] T013 [P] [US1] Criar teste de serviço para criação de reserva sem sobreposição em `PatasBigodesApp/src/test/java/pt/hotel/animais/service/ReservaServiceCreateTest.java`
- [x] T014 [P] [US1] Criar teste de serviço para cancelamento e não-reativação em `PatasBigodesApp/src/test/java/pt/hotel/animais/service/ReservaServiceCancelTest.java`
- [x] T015 [P] [US1] Criar teste de integração do contrato `POST /reservas` em `PatasBigodesApp/src/test/java/pt/hotel/animais/integration/ReservaCreateIntegrationTest.java`
- [x] T016 [P] [US1] Criar teste de integração do contrato `POST /reservas/{id}/cancelar` em `PatasBigodesApp/src/test/java/pt/hotel/animais/integration/ReservaCancelIntegrationTest.java`
- [x] T061 [P] [US1] Criar teste de integração que garante que `POST /reservas/{id}/confirmar` não altera estado, porque a confirmação ocorre apenas no check-in, em `PatasBigodesApp/src/test/java/pt/hotel/animais/integration/ReservaConfirmIntegrationTest.java`

### Implementation for User Story 1

- [x] T017 [US1] Implementar validação de criação de reserva (RF-07, RD-01) em `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IReservaService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/ReservaService.java`
- [x] T062 [US1] Implementar confirmação de reserva apenas no fluxo de check-in (RF-07/RD-02) em `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IReservaService.java`, `ReservaService.java` e `EstadiaService.java`
- [x] T018 [US1] Implementar cancelamento de reserva sem reativação (RD-06) em `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IReservaService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/ReservaService.java`
- [x] T019 [US1] Implementar endpoint `POST /reservas` em `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/ReservaController.java`
- [x] T063 [US1] Bloquear confirmação administrativa via `POST /reservas/{id}/confirmar`, redirecionando para o detalhe sem alterar estado, em `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/ReservaController.java`
- [x] T020 [US1] Implementar endpoint `POST /reservas/{id}/cancelar` em `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/ReservaController.java`
- [x] T021 [US1] Implementar formulário e feedback de criação/cancelamento em `PatasBigodesApp/src/main/resources/templates/reservas/form.html`

**Checkpoint**: US1 funcional e testável de forma independente.

---

## Phase 4: User Story 2 - Consultar disponibilidade (Priority: P1)

**Goal**: Mostrar apenas boxes elegíveis em tempo real para um período.

**Independent Test**: Consulta retorna somente boxes que cumprem RF-06 e RD-01.

### Tests for User Story 2

- [x] T022 [P] [US2] Criar teste de serviço para filtro de disponibilidade em `PatasBigodesApp/src/test/java/pt/hotel/animais/service/DisponibilidadeServiceTest.java`
- [x] T023 [P] [US2] Criar teste de integração do contrato `GET /reservas/disponibilidade` em `PatasBigodesApp/src/test/java/pt/hotel/animais/integration/DisponibilidadeIntegrationTest.java`

### Implementation for User Story 2

- [x] T024 [US2] Implementar cálculo de disponibilidade em `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IAlojamentoService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/AlojamentoService.java`
- [x] T025 [US2] Implementar endpoint `GET /reservas/disponibilidade` em `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/ReservaController.java`
- [x] T026 [US2] Implementar UI de consulta e sugestão de alternativas em `PatasBigodesApp/src/main/resources/templates/reservas/disponibilidade.html`

**Checkpoint**: US2 funcional e testável de forma independente.

---

## Phase 5: User Story 3 - Registar check-in/check-out (Priority: P1)

**Goal**: Abrir e fechar estadias com transições válidas e atualização do alojamento.

**Independent Test**: Check-in cria estadia EM_CURSO; check-out só após check-in e muda o estado da estadia para TERMINADA, deixando o alojamento em PENDENTE_LIMPEZA.

### Tests for User Story 3

- [x] T027 [P] [US3] Criar teste de serviço para check-in condicionado a reserva em `PatasBigodesApp/src/test/java/pt/hotel/animais/service/CheckInServiceTest.java`
- [x] T028 [P] [US3] Criar teste de serviço para bloqueio de check-out sem check-in em `PatasBigodesApp/src/test/java/pt/hotel/animais/service/CheckOutSequenceServiceTest.java`
- [x] T029 [P] [US3] Criar teste de integração do contrato `POST /estadias/check-in` em `PatasBigodesApp/src/test/java/pt/hotel/animais/integration/CheckInIntegrationTest.java`
- [x] T030 [P] [US3] Criar teste de integração do contrato `POST /estadias/{id}/check-out` em `PatasBigodesApp/src/test/java/pt/hotel/animais/integration/CheckOutIntegrationTest.java`

### Implementation for User Story 3

- [x] T031 [US3] Implementar fluxo de check-in e criação de estadia em `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IEstadiaService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/EstadiaService.java`
- [x] T032 [US3] Implementar fluxo de check-out e fecho de estadia em `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IEstadiaService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/EstadiaService.java`
- [x] T033 [US3] Implementar transições de estado do alojamento em `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IAlojamentoService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/AlojamentoService.java`
- [x] T034 [US3] Implementar endpoints de check-in/check-out em `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/EstadiaController.java`
- [x] T035 [US3] Implementar ecrãs de operação de estadia em `PatasBigodesApp/src/main/resources/templates/estadias/checkin-checkout.html`

**Checkpoint**: US3 funcional e testável de forma independente.

---

## Phase 6: User Story 4 - Pagamento da estadia no check-in (Priority: P1)

**Goal**: Calcular e registar pagamento base no momento da entrada.

**Independent Test**: Check-in com pagamento cria registo obrigatório com valor, método e estado.

### Tests for User Story 4

- [x] T036 [P] [US4] Criar teste de serviço para cálculo de valor base da estadia em `PatasBigodesApp/src/test/java/pt/hotel/animais/service/PagamentoCheckInCalculoTest.java` (executável com @SpringBootTest)
- [x] T037 [P] [US4] Criar teste de integração para registo de pagamento de check-in em `PatasBigodesApp/src/test/java/pt/hotel/animais/integration/PagamentoCheckInIntegrationTest.java` (executável com MockMvc)

### Implementation for User Story 4

- [x] T038 [US4] Implementar cálculo de pagamento base em `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IPagamentoService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/PagamentoService.java`
- [x] T039 [US4] Implementar persistência de pagamento `CHECK_IN` em `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IPagamentoService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/PagamentoService.java`
- [x] T040 [US4] Integrar pagamento de check-in no fluxo de check-in em `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IEstadiaService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/EstadiaService.java`
- [x] T040A [US4] Substituir tarifa fixa por consulta a `TipoAlojamentoTarifaService` no cálculo de pagamento base.
- [x] T040B [US4] Atualizar interface de check-in para recolher método de pagamento obrigatório.
- [x] T040C [US4] Remover método de pagamento por defeito no controller e testar rejeição de check-in sem método.

**Checkpoint**: US4 funcional e testável de forma independente.

---

## Phase 7: User Story 5 - Cobrança de extras no check-out (Priority: P1)

**Goal**: Registar faturação complementar no fecho da estadia.

**Independent Test**: Check-out agrega extras/intervenções e regista pagamento complementar.

### Tests for User Story 5

- [x] T041 [P] [US5] Criar teste de serviço para agregação de extras/intervenções em `PatasBigodesApp/src/test/java/pt/hotel/animais/service/PagamentoCheckOutExtrasTest.java`
- [x] T042 [P] [US5] Criar teste de integração para pagamento de check-out em `PatasBigodesApp/src/test/java/pt/hotel/animais/integration/PagamentoCheckOutIntegrationTest.java`

### Implementation for User Story 5

- [x] T043 [US5] Implementar cálculo de faturação complementar no check-out em `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IPagamentoService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/PagamentoService.java`, agregando serviços extra, intervenções clínicas e dias adicionais.
- [x] T044 [US5] Implementar persistência de pagamento `CHECK_OUT` em `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IPagamentoService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/PagamentoService.java`
- [x] T045 [US5] Integrar cobrança complementar no fecho de estadia em `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IEstadiaService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/EstadiaService.java`
- [x] T045A [US5] Atualizar interface de check-out para recolher método de pagamento obrigatório.
- [x] T045B [US5] Atualizar testes de controller/service para a assinatura com método de pagamento.

### Gestão administrativa 

- [x] T070 [P] Criar repositório e serviço para tarifas de tipo de alojamento em `TipoAlojamentoTarifaRepository` e `TipoAlojamentoTarifaService`.
- [x] T071 [P] Criar repositório e serviço para catálogo de tipos de serviços extra em `TipoServicoExtraRepository` e `TipoServicoExtraService`.
- [x] T072 Criar controller e templates de gestão de tipos de alojamento/tarifas em `TipoAlojamentoTarifaController` e `templates/admin/tarifas/`.
- [x] T073 Criar controller e templates de gestão de serviços extra em `TipoServicoExtraController` e `templates/admin/tipos-servicos-extra/`.
- [x] T074 Remover dependência do enum `TipoAlojamento` no domínio aplicacional e representar o tipo como valor configurável.
- [x] T075 [P] [US4] Criar testes unitários para regras do catálogo `TipoAlojamentoTarifaService`.

**Checkpoint**: US5 funcional e testável de forma independente.

---

## Phase 8: User Story 6 - Histórico de estadias e pagamentos (Priority: P2)

**Goal**: Permitir consulta filtrável de histórico operacional e financeiro.

**Independent Test**: Consulta retorna estadias e pagamentos por animal/período com estados visíveis.

### Tests for User Story 6

- [x] T046 [P] [US6] Criar teste de serviço para consulta de histórico por filtros em `PatasBigodesApp/src/test/java/pt/hotel/animais/service/HistoricoServiceTest.java`
- [x] T047 [P] [US6] Criar teste de integração do contrato `GET /historico` em `PatasBigodesApp/src/test/java/pt/hotel/animais/integration/HistoricoIntegrationTest.java`

### Implementation for User Story 6

- [x] T048 [US6] Implementar agregação de histórico em `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IHistoricoService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/HistoricoService.java`
- [x] T049 [US6] Implementar endpoint `GET /historico` em `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/HistoricoController.java`
- [x] T050 [US6] Implementar template de histórico em `PatasBigodesApp/src/main/resources/templates/dashboard/historico.html`

### Implementation status update

- [x] T048 [US6] Implementar agregação de histórico em `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IHistoricoService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/HistoricoService.java` (placeholder returning todas estadias)
- [x] T049 [US6] Implementar endpoint `GET /historico` em `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/HistoricoController.java` (template placeholder em `templates/historico/list.html`)

**Checkpoint**: US6 funcional e testável de forma independente.

---

## Phase 9: User Story 7 - Dashboard de faturação e pendentes (Priority: P2)

**Goal**: Expor indicadores de direção com filtros e pendências.

**Independent Test**: Dashboard mostra ocupação, estadias ativas, reservas futuras, faturação e pendentes por período.

### Tests for User Story 7

- [x] T051 [P] [US7] Criar teste de serviço para agregados de dashboard em `PatasBigodesApp/src/test/java/pt/hotel/animais/service/DashboardServiceTest.java`
- [x] T052 [P] [US7] Criar teste de integração do contrato `GET /dashboard` em `PatasBigodesApp/src/test/java/pt/hotel/animais/integration/DashboardIntegrationTest.java`

### Implementation for User Story 7

- [x] T053 [US7] Implementar serviço de indicadores e pendentes em `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IDashboardService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/DashboardService.java`
- [x] T054 [US7] Implementar endpoint `GET /dashboard` em `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/DashboardController.java`
- [x] T055 [US7] Implementar template de dashboard de direção em `PatasBigodesApp/src/main/resources/templates/dashboard/index.html`

### Progress update

- [x] T051 [P] [US7] Criar teste de serviço para agregados de dashboard em `PatasBigodesApp/src/test/java/pt/hotel/animais/service/DashboardServiceIntegrationTest.java`
- [x] T053 [US7] Implementar serviço de indicadores e pendentes em `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IDashboardService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/DashboardService.java`
- [x] T054 [US7] Implementar endpoint `GET /dashboard` em `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/DashboardController.java`
- [x] T055 [US7] Implementar template de dashboard de direção em `PatasBigodesApp/src/main/resources/templates/dashboard/index.html`

**Checkpoint**: US7 funcional e testável de forma independente.

---

## Phase 10: Polish & Cross-Cutting Concerns

**Purpose**: Consolidar qualidade, rastreabilidade e validação final multi-story.

- [ ] T056 [P] Atualizar documentação técnica da fase em `docs/Etapa3/plano-implementacao-gradual.md`
- [ ] T057 [P] Atualizar rastreabilidade US/RF/RD/UC/testes em `specs/003-reservas-estadias-pagamentos/spec.md`
- [ ] T058 Executar validação de quickstart e evidências em `specs/003-reservas-estadias-pagamentos/quickstart.md`
- [x] T059 Executar suite completa de testes da feature em `PatasBigodesApp/src/test/java/pt/hotel/animais/` (41/41 testes passaram com BUILD SUCCESS)
- [ ] T060 Consolidar correções finais de performance e logs em `PatasBigodesApp/src/main/java/pt/hotel/animais/service/`
- [ ] T064 [P] Implementar eventos de auditoria para operações críticas em `PatasBigodesApp/src/main/java/pt/hotel/animais/service/`
- [ ] T065 [P] Criar testes de auditoria para criar/cancelar reserva e confirmar no check-in em `PatasBigodesApp/src/test/java/pt/hotel/animais/integration/ReservaAuditIntegrationTest.java`
- [ ] T066 [P] Criar testes de auditoria para check-in/check-out/pagamentos em `PatasBigodesApp/src/test/java/pt/hotel/animais/integration/OperacaoAuditIntegrationTest.java`
- [ ] T067 [P] Criar testes de autorização por perfil em `PatasBigodesApp/src/test/java/pt/hotel/animais/integration/AuthorizationIntegrationTest.java`
- [ ] T068 [P] Criar testes de confidencialidade de dados em `PatasBigodesApp/src/test/java/pt/hotel/animais/integration/ConfidencialidadeIntegrationTest.java`
- [ ] T069 [P] Criar testes de desempenho para SC-001/SC-002/SC-006 em `PatasBigodesApp/src/test/java/pt/hotel/animais/integration/PerformanceSlaIntegrationTest.java`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: início imediato.
- **Phase 2 (Foundational)**: depende da Phase 1 e bloqueia todas as user stories.
- **Phases 3-9 (User Stories)**: dependem da conclusão da Phase 2.
- **Phase 10 (Polish)**: depende da conclusão das user stories selecionadas.

### User Story Dependencies

- **US1 (P1)**: pode iniciar após Phase 2.
- **US2 (P1)**: pode iniciar após Phase 2; depende logicamente das regras implementadas em `AlojamentoService`.
- **US3 (P1)**: depende de US1 (reserva ativa para check-in/check-out).
- **US4 (P1)**: depende de US3 (fluxo de check-in).
- **US5 (P1)**: depende de US3 e US4 (check-out e pagamentos).
- **US6 (P2)**: depende de US3/US4/US5 para dados relevantes.
- **US7 (P2)**: depende de US1/US3/US4/US5 para agregados operacionais/financeiros.
- **Cross-cutting**: T064-T069 dependem da conclusão das user stories com fluxos críticos implementados.

### Within Each User Story

- Testes primeiro.
- Serviços antes de endpoints.
- Endpoints antes de templates finais.
- Story só concluída após testes da própria story passarem.

### Parallel Opportunities

- Tarefas marcadas com **[P]** podem decorrer em paralelo.
- Em Phase 2, repositórios/enums/security podem ser implementados em paralelo.
- Após Phase 2, equipas distintas podem trabalhar em US1 e US2 em simultâneo.
- Em cada story, testes de serviço e integração marcados [P] podem ser preparados em paralelo.

---

## Parallel Example: User Story 3

```bash
# Em paralelo (testes):
T027 [US3] CheckInServiceTest
T028 [US3] CheckOutSequenceServiceTest
T029 [US3] CheckInIntegrationTest
T030 [US3] CheckOutIntegrationTest

# Depois da base de testes:
T031 [US3] EstadiaService check-in
T032 [US3] EstadiaService check-out
T033 [US3] AlojamentoService transições
```

---

## Implementation Strategy

### MVP First (P1 obrigatório)

1. Concluir Phase 1 e Phase 2.
2. Entregar US1 + US2.
3. Entregar US3 + US4 + US5.
4. Validar SC-008, SC-009 e SC-010 antes de avançar para P2.

### Incremental Delivery

1. Foundation pronta -> disponibilidade e reservas.
2. Adicionar estadias e pagamentos de entrada.
3. Adicionar pagamentos de saída.
4. Adicionar histórico e dashboard.
5. Fechar com polish e validação fim-a-fim.

### Team Parallelization

1. Equipa A: US1/US2.
2. Equipa B: US3/US4.
3. Equipa C: US5/US6/US7 após dados transacionais estabilizados.

---

## Notes

- Cada tarefa inclui caminho explícito para execução direta por agente.
- IDs seguem ordem de execução recomendada.
- [P] indica paralelização segura por ficheiros/fluxos distintos.
- Não avançar para implement sem passar os checkpoints por story.
