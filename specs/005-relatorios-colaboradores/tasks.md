# Tasks: Relatórios e Colaboradores (Spec 005)

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
- [x] T026 [US-04] Implementar geração PDF server-side com ficheiro PDF real.
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
- [x] T037 Configurar Maven Javadoc Plugin para gerar documentação HTML do código.
- [ ] T038 Rever Javadoc em controllers, services, DTOs e exceptions; completar documentação em classes públicas relevantes. **Obrigatório**: AuditoriaService, AuditoriaController (validar com `mvn javadoc:javadoc` sem warnings). **Desejável**: RelatorioService, ColaboradorService.

## Phase 8: Relatórios — PDF Válido & Agrupamento Integrado (LAC-14)

**Goal**: Corrigir geração de PDF para ser binário real e parseável; integrar agrupamento ao nível de agregação de dados.

**Independent Test**: Gerar relatório com `agruparPor=DIA`, exportar para PDF, validar que ficheiro começa com `%PDF-` e é parseável com ferramenta PDF; validar que totais por dia aparecem em PDF, CSV e web.

- [x] T078 [P] [LAC-14] Adicionar dependência Maven Apache PDFBox ao `pom.xml`: `<dependency><groupId>org.apache.pdfbox</groupId><artifactId>pdfbox</artifactId><version>3.0.0</version></dependency>`.
- [x] T079 [LAC-14] Substituir gerador manual de PDF em `RelatorioService.gerarPdf()` com Apache PDFBox:
  - Remover métodos privados: `construirPdf()`, `escaparPdf()`, `normalizarTextoPdf()`, `escreverPdf()`.
  - Implementar novo método que: (a) cria `PDDocument`; (b) adiciona página com tabelas/texto; (c) retorna bytes binários parseáveis.
  - Testar com simples estrutura texto/tabela; não exigir design complexo.
- [x] T080 [LAC-14] Validar DTO de agregação `RelatorioAgrupamentoDto` como estrutura comum para web, CSV e PDF.
- [x] T081 [LAC-14] Integrar agrupamento em `RelatorioService.gerarRelatorio()`/`gerarAgrupamentos()`:
  - Receber `RelatorioFiltroFormDto` com `agruparPor`.
  - Aplicar lógica de agrupamento **ao nível de agregação**, não apenas na apresentação.
  - Retornar lista de `RelatorioAgrupamentoDto`.
  - Garantir que CSV, PDF e web reutilizam mesma lista agregada.
- [x] T082 [LAC-14] Atualizar `RelatorioService.gerarCsv()` para usar estrutura agregada: cabeçalhos refletem grupos, totais por grupo aparecem no CSV.
- [x] T083 [LAC-14] Atualizar `RelatorioService.gerarPdf()` para usar estrutura agregada: secções ou tabelas por grupo, totais por grupo no PDF.
- [x] T084 [LAC-14] Adicionar validação de limite de período em `RelatorioController.gerarRelatorio()`:
  - Validar que `dataFim - dataInicio <= 3 meses`.
  - Se > 3 meses: regressar à página com erro claro: "Período máximo para exportação imediata é 3 meses. Selecione um intervalo menor ou contacte o suporte para processamento offline."
  - Aplicar validação também antes de chamar `gerarCsv()` e `gerarPdf()`.
- [x] T085 [LAC-14] [P] Criar testes de validade e agrupamento:
  - `RelatorioServiceTest.testGerarPdfDeveProducirBinariosParseavel()`: valida `%PDF-` e parseabilidade com PDFBox.
  - `testGerarPdfContemDadosEsperados()`: extrai texto com PDFBox e valida títulos, datas, totais.
  - `testGerarPdfComAgrupamentoDia()`: PDF com `agruparPor=DIA` contém totais por dia.
  - `testGerarCsvComAgrupamento()`: CSV totais coincidem com PDF e web.
  - `RelatorioControllerTest.testExportarPdfComValoresParaGrupos()`: valida `Content-Type: application/pdf`, `Content-Disposition`, parseabilidade.
  - `testExportarPeriodoAcima3MesesRetornaErro()`: tenta período > 3 meses, valida erro 400.
  - `testAgrupamentoIdenticoEmCsvPdfWeb()`: mesmos totais em todos os formatos.
- [x] T086 [LAC-14] Atualizar `specs/005-relatorios-colaboradores/contracts/contract.md` com semântica de agrupamento e limites:
  - Documentar parâmetro `agruparPor` com valores permitidos: `DIA`, `SEMANA`, `MES`, `ALOJAMENTO`, `COLABORADOR`, `TIPO_SERVICO`.
  - Documentar limite de período: máximo 3 meses para exportação síncrona; períodos maiores retornam HTTP 400.
  - Documentar que CSV/PDF refletem mesma agregação que web.
  - Documentar estrutura PDF mínima esperada (cabeçalhos, grupos, rodapé).

## Phase 9: Auditoria Centralizada — Fundação (LAC-13)

**Goal**: Implementar tabela `AuditoriaEvento` e serviço de auditoria como base transversal para todas as specs.

**Independent Test**: Criar um evento de auditoria via `AuditoriaService` e validar persistência em BD com campos corretos (timestamp, utilizadorId, operacao, entidade, entityId, resultado).

- [x] T039 [P] Criar migração Flyway `V12__create_auditoria_evento.sql` em `PatasBigodesApp/src/main/resources/db/migration/` com tabela `auditoria_evento` e índices (timestamp, utilizador_id+timestamp, operacao+timestamp, entidade+entity_id+timestamp).
- [x] T040 Criar enum `ResultadoAuditoria` com valores `SUCESSO`, `FALHA` em `PatasBigodesApp/src/main/java/pt/hotel/animais/model/enums/ResultadoAuditoria.java`.
- [x] T041 Criar entidade JPA `AuditoriaEvento` em `PatasBigodesApp/src/main/java/pt/hotel/animais/model/auditoria/AuditoriaEvento.java` com 10 campos: id, timestamp, utilizadorId (FK), operacao, entidade, entityId, acao, detalhes (JSON), resultado, motivoFalha.
- [x] T042 [P] Criar `AuditoriaRepository` com queries: `findByTimestampBetween()`, `findByUtilizador_IdAndTimestampBetween()`, `findByOperacaoContainingIgnoreCaseAndTimestampBetween()`, `findByEntidadeContainingIgnoreCaseAndTimestampBetween()`, `deleteByTimestampBefore()`.
- [x] T043 Criar interface `IAuditoriaService` em `PatasBigodesApp/src/main/java/pt/hotel/animais/service/auditoria/IAuditoriaService.java` com assinatura pública de métodos (sem implementação).
- [x] T044 Criar `AuditoriaService` em `PatasBigodesApp/src/main/java/pt/hotel/animais/service/auditoria/AuditoriaService.java` com métodos: `registarEvento()`, `consultarPorPeriodo()`, `consultarPorUtilizador()`, `limparzardosAntigos()`.
- [x] T045 [P] Criar DTOs: `AuditoriaFiltroDTO` e `AuditoriaEventoDTO` em `pt.hotel.animais.dto.auditoria`.
- [x] T046 Criar `AuditoriaServiceTest` em `PatasBigodesApp/src/test/java/...` com testes: `testRegistarEvento()` (evento persistido com sucesso), `testRegistarEventoComUtilizadorNulo_LancaExcecao()` (validar que utilizadorId null lança exceção), `testLimparzardosAntigos()` (remove eventos > 12 meses), `testConsultarComFiltros()` (filtros retornam dados corretos).
- [x] T047 [P] Criar `AuditoriaRepositoryTest` com testes de queries de filtro e limpeza.
- [x] T048 Publicar documento `PatasBigodesApp/docs/auditoria-interface.md` com contrato público: (1) assinatura de `IAuditoriaService`; (2) exemplo: `AuditoriaService.registarEvento(utilizadorId, operacao, entidade, entityId, acao, detalhes, resultado, motivoFalha)`; (3) schema JSON para `detalhes`; (4) operações esperadas por spec (Spec 003: CRIAR_RESERVA, CHECK_IN, CHECK_OUT, PAGAMENTO; Spec 004: CUIDADO, INTERVENCAO_CLINICA, SERVICO_EXTRA, LIMPEZA_REALIZADA).

## Phase 10: Auditoria — Integração com Spec 005

**Goal**: Integrar `AuditoriaService` em `ColaboradorService` e criar UI de consulta de auditoria.

**Independent Test**: Criar colaborador, verificar evento em BD; consultar auditoria filtrada por data e utilizador em `/auditoria`.

- [x] T049 Adicionar chamada a `AuditoriaService.registarEvento()` em `ColaboradorService.criar()` com operacao="CRIAR_COLABORADOR", entidade="Colaborador".
- [x] T050 Adicionar chamada a `AuditoriaService.registarEvento()` em `ColaboradorService.editar()` com operacao="EDITAR_COLABORADOR", incluir detalhes de campos alterados em JSON.
- [x] T051 Adicionar chamada a `AuditoriaService.registarEvento()` em `ColaboradorService.desativar()` com operacao="DESATIVAR_COLABORADOR".
- [x] T052 Implementar `AuditoriaController` em `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/AuditoriaController.java` com rotas: `GET /auditoria` (com filtros, paginação), `GET /auditoria/exportar/csv`.
- [x] T053 Aplicar `@PreAuthorize("hasRole('DIRETOR')")` a todas as rotas de `AuditoriaController`.
- [x] T054 Criar template `auditoria/list.html` em `PatasBigodesApp/src/main/resources/templates/auditoria/` com tabela paginada de eventos, filtros (dataInicio, dataFim, utilizador, operacao, entidade) e botão de exportação CSV.
- [x] T055 [P] Implementar exportação CSV em `AuditoriaController.exportarCsv()` com cabeçalhos estáveis e todos os campos de evento.
- [x] T056 Criar `AuditoriaControllerTest` com testes: `testListarAuditoria_DireToR()`, `testListarAuditoria_OutrosPerfis()`, `testFiltrosPorData()`, `testExportarCsv()`.
- [x] T057 Criar `AuditoriaIntegrationTest` que verifica: criar colaborador → evento em BD com resultado=SUCESSO; editar colaborador → evento com detalhes em JSON; desativar → evento registado.
- [x] T058 Atualizar `quickstart.md` com passos de navegação: "Consultar auditoria" → abrir `/auditoria` → filtrar por data/utilizador/operacao → exportar CSV.

## Phase 11: Auditoria — Integração com Specs 003 e 004

**Goal**: Auditar operações críticas em ReservaService, EstadiaService, PagamentoService, CuidadosService, etc. Coordenação com teams de specs 003 e 004.

**Independent Test**: Criar reserva em spec 003 → verificar evento em `AuditoriaEvento` com operacao="CRIAR_RESERVA", entidade="Reserva", resultado="SUCESSO". Similar para check-in, pagamento, cuidado, intervenção, etc.

- [ ] T059 Coordenar com spec 003: adicionar chamada a `AuditoriaService.registarEvento()` em `ReservaService.criar()`, `ReservaService.editar()`, `ReservaService.cancelar()`. Parcial: `criar()` e `cancelar()` implementados; `editar()` pendente porque não existe fluxo de edição de reserva na aplicação atual.
- [x] T060 Coordenar com spec 003: adicionar chamada em `EstadiaService.abrirEstadia()` (check-in) com operacao="CHECK_IN", entidade="Estadia".
- [x] T061 Coordenar com spec 003: adicionar chamada em `EstadiaService.fecharEstadia()` (check-out) com operacao="CHECK_OUT", entidade="Estadia".
- [x] T062 Coordenar com spec 003: adicionar chamada em `PagamentoService.criar()`, `PagamentoService.liquidar()` com operacao="PAGAMENTO_CRIADO", "PAGAMENTO_LIQUIDADO", entidade="Pagamento".
- [x] T063 Coordenar com spec 004: adicionar chamada em `CuidadosService.registarCuidado()` com operacao="CUIDADO_REGISTADO", entidade="Cuidado".
- [x] T064 Coordenar com spec 004: adicionar chamada em `IntervenaoClinicaService.registar()` com operacao="INTERVENCAO_CLINICA", entidade="Intervencao".
- [x] T065 Coordenar com spec 004: adicionar chamada em `ServicoExtraService.criar()` com operacao="SERVICO_EXTRA", entidade="ServicoExtra".
- [x] T066 Coordenar com spec 004: adicionar chamada em `LimpezaService.marcarLimpo()` com operacao="LIMPEZA_REALIZADA", entidade="Alojamento".
- [ ] T067 [P] Criar testes de integração E2E: ReservaIntegrationTest, EstadiaAuditoriaTest, PagamentoAuditoriaTest, CuidadosAuditoriaTest.
- [ ] T067.5 [P] **TEST GATE — E2E com Specs 003, 004**: Script de validação que verifica: ReservaService.criar() → evento AuditoriaEvento (operacao="CRIAR_RESERVA"); EstadiaService.check_in() → operacao="CHECK_IN"; PagamentoService.criar() → operacao="PAGAMENTO_CRIADO"; CuidadosService.registarCuidado() → operacao="CUIDADO_REGISTADO"; similares para IntervenaoClinicaService, ServicoExtraService, LimpezaService. **Blocker para merge**: teste deve passar antes de merge.
- [x] T068 Atualizar `docs/auditoria-interface.md` com resultados de testes de integração e lições aprendidas.

## Phase 12: Auditoria — Limpeza de Dados e Job Scheduler

**Goal**: Implementar política de retenção de 12 meses e job de limpeza automática.

**Independent Test**: Executar `AuditoriaService.limparzardosAntigos(12)`, validar que eventos com timestamp > 12 meses são apagados.

- [x] T069 Criar classe `AuditoriaSchedulerJob` em `PatasBigodesApp/src/main/java/pt/hotel/animais/job/` com método `@Scheduled(cron = "0 0 3 * * ?")` que chama `AuditoriaService.limparzardosAntigos(1)` para reter 1 ano, equivalente a 12 meses.
- [x] T070 Criar testes de job: `AuditoriaSchedulerJobTest` que valida execução periódica e remoção de dados.
- [x] T071 Documentar política de retenção em `quickstart.md` ou ficheiro técnico `docs/auditoria-retencao.md`.

## Phase 13: Auditoria — Documentação Final e QA

**Goal**: Validar cobertura de testes, documentação e conformidade com LAC-13.

**Independent Test**: Cobertura >= 80% em AuditoriaService e AuditoriaController; todos os requisitos de FR-011, SC-008, SC-009 validados.

- [ ] T072 [P] Validar cobertura de testes com JaCoCo: AuditoriaService >= 80%, AuditoriaController >= 80%.
- [ ] T073 Rever Javadoc em `AuditoriaEvento`, `AuditoriaService`, `AuditoriaController` e completar documentação.
- [x] T074 Criar checklist de validação em `specs/005-relatorios-colaboradores/checklists/lac-13-auditoria.md` com verificações de: FR-011, SC-008, SC-009, operações auditadas, retenção, permissões.
- [x] T075 Atualizar `spec.md` com referência a artefatos gerados: Tabela `AuditoriaEvento` ativa, rotas `/auditoria` funcionais, testes passando.
- [ ] T076 Documentar casos de uso falhados: "Falha em criar auditoria → evento com resultado=FALHA registado com motivoFalha".
- [x] T077 Rever conformidade com RD-04 (auditoria obrigatória) e RNF-04 (permissões).

## Dependencies & Execution Order

**Critical Path**:
1. **Phase 1-2** (Setup, Segurança): Fundação obrigatória.
2. **Phase 3-7** (Relatórios, Colaboradores): Funcionalidades existentes.
3. **Phase 8 (LAC-14)** + **Phase 9 (LAC-13)**: PARALELAS — Ambas podem começar simultaneamente após Phase 7.
   - Phase 8 (PDF & Agrupamento): independente de auditoria.
   - Phase 9 (Auditoria Fundação): BLOQUEANTE para fases 10-13.
4. **Phase 10** (Auditoria Spec 005): Integração com colaboradores.
5. **Phase 11** (Auditoria Specs 003, 004): Coordenação paralela com outras teams.
6. **Phase 12-13** (Job, QA Final): Finalização.

**Parallelizável**:
- **Phase 8 (LAC-14)**: T078, T079 independentes; T080-T083 dependem de T079; T085 dependem de T082-T083.
- **Phase 9**: T039-T042 (entidades/repo) paralelos; T047, T048 paralelos a T044-046.
- **Phase 11**: T059-T066 podem começar em paralelo após Phase 10 completada.
2. **Phase 3-7** (Relatórios, Colaboradores): Funcionalidades existentes.
3. **Phase 8** (Auditoria Fundação): BLOQUEANTE — todas as fases seguintes dependem deste.
4. **Phase 9** (Auditoria Spec 005): Integração com colaboradores.
5. **Phase 10** (Auditoria Specs 003, 004): Coordenação paralela com outras teams.
6. **Phase 11-12** (Job, QA Final): Finalização.

**Parallelizável**:
- T039, T040, T041, T042 (criação de entidades/repo).
- T047, T048 (testes e documentação paralelos a T044-046).
- T059-T066 (coordenação inter-specs pode ocorrer em paralelo após Phase 8).

## Summary

**Original Total**: 38 tasks (`T001`..`T038`).
**New (LAC-14)**: 9 tasks (`T078`..`T086`).
**New (LAC-13)**: 39 tasks (`T039`..`T077`).
**Grand Total**: 86 tasks.

**Distribution**:
- Phase 1-7: 38 tasks (Original)
- **Phase 8 (NEW - LAC-14)**: 9 tasks (PDF Validity & Grouping Integration)
- Phase 9: 10 tasks (Fundação Auditoria - LAC-13)
- Phase 10: 10 tasks (Integração Spec 005 - LAC-13)
- Phase 11: 10 tasks (Integração Inter-specs - LAC-13)
- Phase 12: 3 tasks (Job Scheduler - LAC-13)
- Phase 13: 6 tasks (QA Final - LAC-13)
