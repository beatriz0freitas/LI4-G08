# Tasks: Relatórios e Colaboradores (Spec 005)

**Input**: `spec.md`, `plan.md`, requisitos em `docs/Etapa1/02-requirements`, arquitetura em `docs/Etapa2/01-architecture/architecture.md` e permissões em `docs/Etapa2/06-role-permissions/permissoes.md`.

## Phase 1: Setup e Contrato MVC

- [ ] T001 Criar branch de feature `005-relatorios-colaboradores` e publicar no remoto.
- [x] T002 Atualizar `specs/005-relatorios-colaboradores/contracts/contract.md` com rotas MVC, templates, formulários e downloads.
- [x] T003 [P] Criar migrations Flyway para colaboradores e diretor inicial: `PatasBigodesApp/src/main/resources/db/migration/V6__create_colaborador.sql` e `V7__seed_diretor.sql`.
- [x] T004 [P] Criar DTOs de formulário: `RelatorioFiltroFormDto` e `ColaboradorFormDto`.

## Phase 2: Fundação de Segurança e Domínio

- [x] T005 Criar/validar enum `TipoColaborador` em `PatasBigodesApp/src/main/java/pt/hotel/animais/model/enums/TipoColaborador.java`.
- [x] T006 Criar entidade `Colaborador` com `tipoColaborador: TipoColaborador`, password BCrypt e estado ativo.
- [x] T007 [P] Criar `ColaboradorRepository`.
- [x] T008 Implementar `IColaboradorService`/`ColaboradorService` com criar, editar, desativar e listar.
- [x] T009 Migrar autenticação para `UserDetailsService` baseado em `ColaboradorRepository`, sem utilizadores de teste hardcoded em runtime.

## Phase 3: US-03 - Gestão de Colaboradores

**Goal**: O `DIRETOR` gere colaboradores através de páginas Thymeleaf.

**Independent Test**: Autenticar como `DIRETOR`, abrir `/colaboradores/novo`, criar colaborador com `tipoColaborador = CUIDADOR` e confirmar presença em `/colaboradores`.

- [x] T010 [US-03] Implementar `ColaboradorController` com `GET /colaboradores`, `GET /colaboradores/novo`, `POST /colaboradores`, `GET /colaboradores/{id}/editar`, `POST /colaboradores/{id}` e `POST /colaboradores/{id}/desativar`.
- [x] T011 [US-03] Aplicar `@PreAuthorize("hasRole('DIRETOR')")` às rotas de colaboradores.
- [x] T012 [US-03] Criar templates `colaboradores/list.html` e `colaboradores/form.html`.
- [x] T013 [US-03] Popular o campo `tipoColaborador` no formulário a partir de `TipoColaborador.values()`.
- [x] T014 [US-03] Testar validações de username/email duplicado, password obrigatória na criação e tipo inválido.
- [x] T015 [US-03] Criar `ColaboradorControllerTest` e `ColaboradorServiceTest`.

## Phase 4: US-01 e US-02 - Dashboard do Diretor

**Goal**: Apresentar taxa de ocupação, estadias, reservas, faturação e pagamentos pendentes no dashboard.

**Independent Test**: Autenticar como `DIRETOR`, abrir `/dashboard` e confirmar métricas principais renderizadas.

- [x] T016 [US-01] Atualizar `IDashboardService`/`DashboardService` ou criar métodos de agregação necessários para ocupação por período.
- [x] T017 [US-02] Criar agregações de faturação por período, método e estado em `PagamentoRepository`.
- [x] T018 [US-02] Renderizar painel financeiro em `templates/dashboard/index.html`.
- [x] T019 [US-01/US-02] Garantir que `/dashboard` está acessível ao `DIRETOR` e bloqueado a perfis sem permissão financeira.
- [x] T020 [US-01/US-02] Criar testes de serviço e controller para métricas do dashboard.

## Phase 5: US-04 - Relatórios Operacionais

**Goal**: Gerar relatórios filtráveis e exportar CSV/PDF pela interface web.

**Independent Test**: Autenticar como `DIRETOR`, abrir `/relatorios`, filtrar período mensal, gerar relatório e exportar CSV.

- [x] T021 [US-04] Implementar `IRelatorioService`/`RelatorioService` com agregações de ocupação, reservas, estadias, faturação e serviços extra.
- [x] T022 [US-04] Implementar `RelatorioController` com `GET /relatorios`, `POST /relatorios/gerar`, `GET /relatorios/exportar/csv` e `GET /relatorios/exportar/pdf`.
- [x] T023 [US-04] Aplicar `@PreAuthorize("hasRole('DIRETOR')")` ao `RelatorioController`.
- [x] T024 [US-04] Criar template `relatorios/list.html` com filtros, tabela de resultados e ações de exportação.
- [x] T025 [US-04] Implementar exportação CSV com cabeçalhos estáveis.
- [x] T026 [US-04] Implementar geração PDF server-side simples.
- [x] T027 [US-04] Criar `RelatorioServiceTest` e `RelatorioControllerTest`.

## Phase 6: US-05 - Histórico de Estadias e Pagamentos

**Goal**: Consolidar histórico operacional/financeiro nas páginas existentes.

**Independent Test**: Autenticar como perfil autorizado, abrir `/historico` com filtros e confirmar eventos e pagamentos associados.

- [x] T028 [US-05] Rever `HistoricoController` e `templates/historico/list.html` para garantir filtros por reserva/estadia/período.
- [x] T029 [US-05] Completar `IHistoricoService`/`HistoricoService` com agregação de eventos, pagamentos e serviços extra.
- [x] T030 [US-05] Aplicar permissões conforme `permissoes.md`.
- [x] T031 [US-05] Criar testes de histórico por perfil.

## Phase 7: Segurança, QA e Documentação

- [x] T032 [P] Criar testes de segurança com combinações da matriz de permissões.
- [x] T033 [P] Configurar auditoria Spring Boot Actuator para geração de relatórios e alterações de colaboradores.
- [x] T034 [P] Validar CSRF em todos os formulários POST.
- [x] T035 Atualizar `quickstart.md` com passos de navegação na UI.
- [x] T036 Registar resultados de QA em `specs/005-relatorios-colaboradores/checklists/qa-results.md`.

## Dependencies & Execution Order

- Completar Phase 1 e Phase 2 antes das histórias de utilizador.
- US-03 e US-04 são prioritárias porque materializam o pedido de direção: colaboradores e relatórios.
- Testes de segurança devem ser executados depois de cada controller ficar funcional.

## Summary

Total tasks: 36 (`T001`..`T036`).
