# Tasks: Relatórios e Colaboradores (Spec 005)














































































































































































































































































































































































- Coordenação com specs 003 e 004 é crítica; documentar blockers em `docs/auditoria-interface.md`.- Qualquer "❌ não" deve gerar tarefa correctiva.- Este checklist deve ser revisado após conclusão de cada fase (8-12).**Notas**:---  - [ ] Spec 005 pronta para merge: ✅ SIM / ❌ NÃO  - [ ] Responsável de QA validou: ___________________ (data: _______)  - [ ] Responsável técnico assinou-se: ___________________ (data: _______)- [ ] **Aprovação Final**  - [ ] Nenhuma tarefa bloqueada  - [ ] Todas as tarefas T039-T077 marcadas como concluídas em `tasks.md`- [ ] **Revisão de Tarefas**  - [ ] `data-model.md` documenta `AuditoriaEvento` com índices  - [ ] `plan.md` reflete conclusão de todas as fases  - [ ] `spec.md` referencia FR-011, SC-008, SC-009 como implementados- [ ] **Revisão de Especificação**## 12. Sign-off Finais---  - [ ] CSRF validado  - [ ] SQL injection protegido (JPA)  - [ ] `@PreAuthorize` em controllers  - [ ] Nenhuma operação sem autenticação- [ ] **Segurança**  - [ ] Sem avisos de "missing tags" em métodos públicos  - [ ] `mvn javadoc:javadoc` gera HTML sem erros bloqueantes  - [ ] `mvn clean test` passa todos os testes- [ ] **Build Maven**  - [ ] AuditoriaEvento: >= 70% (entidade simples)  - [ ] AuditoriaController: >= 80%  - [ ] AuditoriaService: >= 80%- [ ] **JaCoCo Coverage**## 11. Cobertura de Testes & Qualidade---  - [ ] Entidade e entityId permitem rastrear objeto afetado  - [ ] Operação descritiva (ex: "CRIAR_RESERVA")  - [ ] Timestamp do evento registado automaticamente  - [ ] Cada evento vinculado a utilizador (utilizadorId)- [ ] **Rastreabilidade**  - [ ] Filtros funcionais em `/auditoria`  - [ ] Acesso restrito a `DIRETOR` apenas  - [ ] Eventos antigos apagados via job scheduler  - [ ] Dados retidos por 12 meses- [ ] **Retenção (Opção A - 12 meses + acesso admin)**  - [ ] Estrutura com 10 campos conforme especificação  - [ ] `AuditoriaEvento` é entidade JPA persistida em BD- [ ] **Formato (Opção A - Tabela relacional)**    - [ ] Colaboradores: criar, editar, desativar    - [ ] Limpeza: marcar limpo    - [ ] Serviços extra: criar    - [ ] Intervenções clínicas: registar    - [ ] Cuidados: registar    - [ ] Pagamentos: criar, liquidar    - [ ] Estadias: check-in, check-out    - [ ] Reservas: criar, editar, cancelar  - [ ] Todas as 8 operações críticas estão auditadas:- [ ] **Ámbito de auditoria (Opção A - Completa)**## 10. Conformidade com LAC-13 & Requisitos---  - [ ] Gerar com `mvn javadoc:javadoc` sem erros  - [ ] `AuditoriaController.java` — descreve endpoints e permissões  - [ ] `AuditoriaService.java` — descreve cada método  - [ ] `AuditoriaEvento.java` — descreve campos e constraints- [ ] **Javadoc**  - [ ] Impacto esperado: ~X registos/dia (estimativa)  - [ ] Job scheduler: 3h00 diárias  - [ ] Política de retenção: 12 meses- [ ] **docs/auditoria-retencao.md** (operacional)  - [ ] Passos de filtro e exportação  - [ ] Passo: "Consultar auditoria" → abrir `/auditoria`- [ ] **quickstart.md** atualizado  - [ ] Operações esperadas por spec  - [ ] Exemplo de uso: `AuditoriaService.registarEvento(...)`  - [ ] Assinatura de `IAuditoriaService`- [ ] **docs/auditoria-interface.md** (público para specs 003, 004)## 9. Documentação---  - [ ] Paginação com links Previous/Next  - [ ] Mensagem "Sem dados" quando lista vazia  - [ ] Botão de exportação CSV  - [ ] Tabela paginada de eventos com colunas: timestamp, utilizador, operacao, entidade, entityId, resultado  - [ ] Formulário de filtros: dataInicio, dataFim, utilizador, operacao, entidade- [ ] **auditoria/list.html**## 8. Templates Thymeleaf---    - [ ] NOT NULL em campos obrigatórios    - [ ] FK para `colaborador(id)`  - [ ] Constraints:    - [ ] idx_entidade_id_timestamp    - [ ] idx_operacao_timestamp    - [ ] idx_utilizador_timestamp    - [ ] idx_timestamp  - [ ] Índices criados:  - [ ] Cria tabela `auditoria_evento` com 10 colunas- [ ] **V006__create_auditoria_evento.sql**## 7. Migrações de Dados---  - [ ] Marcar limpeza → evento com operacao="LIMPEZA_REALIZADA"- [ ] **LimpezaAuditoriaTest.java** (coordenação spec 004)  - [ ] Criar serviço → evento com operacao="SERVICO_EXTRA"- [ ] **ServicoExtraAuditoriaTest.java** (coordenação spec 004)  - [ ] Registar intervenção → evento com operacao="INTERVENCAO_CLINICA"- [ ] **IntervenaoAuditoriaTest.java** (coordenação spec 004)  - [ ] Registar cuidado → evento com operacao="CUIDADO_REGISTADO"- [ ] **CuidadosAuditoriaTest.java** (coordenação spec 004)  - [ ] Liquidar pagamento → evento com operacao="PAGAMENTO_LIQUIDADO"  - [ ] Criar pagamento → evento com operacao="PAGAMENTO_CRIADO"- [ ] **PagamentoAuditoriaTest.java** (coordenação spec 003)  - [ ] Check-out → evento com operacao="CHECK_OUT"  - [ ] Check-in → evento com operacao="CHECK_IN"- [ ] **EstadiaAuditoriaTest.java** (coordenação spec 003)  - [ ] Cancelar reserva → evento com operacao="CANCELAR_RESERVA"  - [ ] Editar reserva → evento com operacao="EDITAR_RESERVA"  - [ ] Criar reserva → evento com operacao="CRIAR_RESERVA"- [ ] **ReservaAuditoriaTest.java** (coordenação spec 003)## 6. Testes de Integração Inter-specs---  - [ ] Consultar auditoria → dados filtrados corretamente  - [ ] Editar colaborador → evento com detalhes  - [ ] Criar colaborador → evento em BD- [ ] **AuditoriaIntegrationTest.java**  - [ ] Cobertura: >= 80%  - [ ] `testExportarCsv()` — ficheiro descarregado corretamente  - [ ] `testFiltrosPorData()` — filtros funcionam  - [ ] `testListarAuditoria_OutrosPerfis()` — acesso negado (403)  - [ ] `testListarAuditoria_DireToR()` — acesso permitido- [ ] **AuditoriaControllerTest.java**  - [ ] Testes de delete: `deleteByTimestampBefore()`  - [ ] Testes de queries: `findByTimestampBetween()`, `findByUtilizadorId()`, etc.- [ ] **AuditoriaRepositoryTest.java**  - [ ] Cobertura: >= 80%  - [ ] `testConsultarPorUtilizador()` — filtra por utilizador  - [ ] `testConsultarPorPeriodo()` — retorna dados corretos  - [ ] `testLimparzardosAntigos()` — remove eventos > 12 meses  - [ ] `testRegistarEventoSemUtilizador()` — lança exceção ou registado com FALHA  - [ ] `testRegistarEvento()` — evento persistido com sucesso- [ ] **AuditoriaServiceTest.java**## 5. Testes Unitários---  - [ ] Logging: "Limpeza de auditoria iniciada/concluída"  - [ ] Chama `AuditoriaService.limparzardosAntigos(12)`  - [ ] `@Scheduled(cron = "0 0 3 * * ?")` — 3h00 cada dia- [ ] **AuditoriaSchedulerJob.java**  - [ ] Paginação com `Pageable` do Spring  - [ ] Retorna template Thymeleaf ou ficheiro CSV  - [ ] Recebe query params: dataInicio, dataFim, utilizadorId, operacao, entidade  - [ ] Rotas MVC: `/auditoria` (GET), `/auditoria/exportar/csv` (GET)  - [ ] `@PreAuthorize("hasRole('DIRETOR')")` em todas as rotas- [ ] **AuditoriaController.java**  - [ ] Javadoc em métodos públicos  - [ ] Logging de operações (info para sucesso, warn para falha)  - [ ] Transacional: `@Transactional`  - [ ] Validação: utilizadorId not null antes de persistência  - [ ] Métodos públicos implementados (registarEvento, consultarPor*, limparzardosAntigos)- [ ] **AuditoriaService.java**## 4. Serviços e Controllers---  - [ ] Validações em `@NotNull`, `@Size`  - [ ] AuditoriaEventoDTO (id, timestamp, utilizador, operacao, entidade, entityId, acao, detalhes, resultado, motivoFalha)  - [ ] AuditoriaFiltroDTO (dataInicio, dataFim, utilizadorId, operacao, entidade)- [ ] **DTOs**  - [ ] Javadoc descritivo  - [ ] Valores: SUCESSO, FALHA- [ ] **ResultadoAuditoria.java** (enum)  - [ ] Javadoc completo  - [ ] @Convert(converter = JsonConverter.class) para detalhes  - [ ] @ManyToOne para utilizadorId  - [ ] Todos os 10 campos mapeados  - [ ] @Entity, @Table com índices corretos- [ ] **AuditoriaEvento.java**## 3. Entidades de Dados---  - [ ] `RESPONSAVEL_LIMPEZA` recebe 403 Forbidden  - [ ] `MEDICO_VETERINARIO` recebe 403 Forbidden  - [ ] `CUIDADOR` recebe 403 Forbidden  - [ ] `FUNCIONARIO_RECEPCAO` recebe 403 Forbidden- [ ] **SC-009.9**: Perfis sem permissão não conseguem aceder  - [ ] Job scheduler remove dados mensalmente  - [ ] Apenas eventos com timestamp >= (hoje - 12 meses) são visíveis- [ ] **SC-009.8**: Retenção de 12 meses  - [ ] Dados completos (sem truncamento)  - [ ] Cabeçalhos: id, timestamp, utilizador, operacao, entidade, entityId, acao, resultado  - [ ] Ficheiro descarregado com extensão `.csv`- [ ] **SC-009.7**: Exportação CSV  - [ ] Navegação entre páginas  - [ ] Tamanho de página: 20 por página (configurável)- [ ] **SC-009.6**: Paginação funciona  - [ ] Query retorna apenas eventos da entidade- [ ] **SC-009.5**: Filtro por entidade  - [ ] Query retorna apenas eventos da operação selecionada- [ ] **SC-009.4**: Filtro por operação  - [ ] Query retorna apenas eventos do utilizador- [ ] **SC-009.3**: Filtro por utilizador  - [ ] Resposta em tempo real para períodos até 1 mês  - [ ] Query retorna apenas eventos no período- [ ] **SC-009.2**: Filtro por data (dataInicio, dataFim)  - [ ] Tabela vazia inicialmente  - [ ] Página carrega sem erros- [ ] **SC-009.1**: `DIRETOR` consegue aceder a `/auditoria`### SC-009: Consulta de Auditoria  - [ ] motivoFalha preenchido com mensagem de erro  - [ ] resultado = "FALHA"- [ ] **SC-008.12**: Operação falha → evento com resultado=FALHA  - [ ] entidade = "Alojamento"  - [ ] operacao = "LIMPEZA_REALIZADA"- [ ] **SC-008.11**: Marcar limpeza (spec 004) → evento registado  - [ ] entidade = "ServicoExtra"  - [ ] operacao = "SERVICO_EXTRA"- [ ] **SC-008.10**: Criar serviço extra (spec 004) → evento registado  - [ ] entidade = "Intervencao"  - [ ] operacao = "INTERVENCAO_CLINICA"- [ ] **SC-008.9**: Registar intervenção clínica (spec 004) → evento registado  - [ ] entidade = "Cuidado"  - [ ] operacao = "CUIDADO_REGISTADO"- [ ] **SC-008.8**: Registar cuidado (spec 004) → evento registado  - [ ] entidade = "Pagamento"  - [ ] operacao = "PAGAMENTO_CRIADO" ou "PAGAMENTO_LIQUIDADO"- [ ] **SC-008.7**: Criar/liquidar pagamento (spec 003) → evento registado  - [ ] entidade = "Estadia"  - [ ] operacao = "CHECK_OUT"- [ ] **SC-008.6**: Check-out (spec 003) → evento registado  - [ ] entidade = "Estadia"  - [ ] operacao = "CHECK_IN"- [ ] **SC-008.5**: Check-in (spec 003) → evento registado  - [ ] entityId = reserva.id  - [ ] entidade = "Reserva"  - [ ] operacao = "CRIAR_RESERVA"- [ ] **SC-008.4**: Criar reserva (spec 003) → evento registado  - [ ] resultado = "SUCESSO"  - [ ] operacao = "DESATIVAR_COLABORADOR"- [ ] **SC-008.3**: Desativar colaborador → evento registado  - [ ] resultado = "SUCESSO"  - [ ] detalhes contém JSON com campos alterados  - [ ] operacao = "EDITAR_COLABORADOR"- [ ] **SC-008.2**: Editar colaborador → evento com detalhes de mudança  - [ ] timestamp = now()  - [ ] utilizadorId preenchido (utilizador autenticado)  - [ ] resultado = "SUCESSO"  - [ ] entidade = "Colaborador"  - [ ] operacao = "CRIAR_COLABORADOR"- [ ] **SC-008.1**: Criar colaborador → evento em `AuditoriaEvento`### SC-008: Registo de Operações Críticas## 2. Success Criteria: SC-008 e SC-009---- [ ] **FR-011.5**: Interface pública `IAuditoriaService` publicada em `docs/auditoria-interface.md`  - [ ] Autorização: `@PreAuthorize("hasRole('DIRETOR')")`  - [ ] `GET /auditoria/exportar/csv`  - [ ] `GET /auditoria` com filtros e paginação- [ ] **FR-011.4**: `AuditoriaController` com rotas de consulta  - [ ] `limparzardosAntigos(anosRetencao)` — removes eventos > 12 meses  - [ ] `consultarPorUtilizador(utilizadorId, filtros, pageable)`  - [ ] `consultarPorPeriodo(dataInicio, dataFim, filtros, pageable)`  - [ ] `registarEvento(utilizadorId, operacao, entidade, entityId, acao, detalhes, resultado, motivoFalha)`- [ ] **FR-011.3**: `AuditoriaService` implementado com métodos públicos  - [ ] `deleteByTimestampBefore(data)`  - [ ] `findByEntidadeAndEntityId(ent, id)`  - [ ] `findByOperacao(op)`  - [ ] `findByUtilizadorId(id)`  - [ ] `findByTimestampBetween(inicio, fim)`- [ ] **FR-011.2**: `AuditoriaRepository` implementado com queries de filtro  - [ ] motivoFalha (String 500, optional)  - [ ] resultado (Enum: SUCESSO/FALHA)  - [ ] detalhes (JSON, max 2000 chars)  - [ ] acao (String 50, not null)  - [ ] entityId (Long, not null)  - [ ] entidade (String 100, not null)  - [ ] operacao (String 100, not null)  - [ ] utilizadorId (FK para Colaborador, not null)  - [ ] timestamp (not null, default CURRENT_TIMESTAMP)  - [ ] id (PK)- [ ] **FR-011.1**: Tabela `AuditoriaEvento` criada em BD com 10 campos obrigatórios## 1. Requisitos Funcionais (FR-011)---**Data de criação**: 2026-05-26**Status**: ✅ Especificação clarificada; Tarefas geradas (T039-T077)  **Lacuna**: LAC-13 — Auditoria incompleta para operações críticas  **Spec**: 005-relatorios-colaboradores  **Input**: `spec.md`, `plan.md`, requisitos em `docs/Etapa1/02-requirements`, arquitetura em `docs/Etapa2/01-architecture/architecture.md` e permissões em `docs/Etapa2/06-role-permissions/permissoes.md`.

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
- [x] T037 Configurar Maven Javadoc Plugin para gerar documentação HTML do código.
- [ ] T038 Rever Javadoc em controllers, services, DTOs e exceptions; completar documentação em classes públicas relevantes. **Obrigatório**: AuditoriaService, AuditoriaController (validar com `mvn javadoc:javadoc` sem warnings). **Desejável**: RelatorioService, ColaboradorService.

## Phase 8: Relatórios — PDF Válido & Agrupamento Integrado (LAC-14)

**Goal**: Corrigir geração de PDF para ser binário real e parseável; integrar agrupamento ao nível de agregação de dados.

**Independent Test**: Gerar relatório com `agruparPor=DIA`, exportar para PDF, validar que ficheiro começa com `%PDF-` e é parseável com ferramenta PDF; validar que totais por dia aparecem em PDF, CSV e web.

- [ ] T078 [P] [LAC-14] Adicionar dependência Maven Apache PDFBox ao `pom.xml`: `<dependency><groupId>org.apache.pdfbox</groupId><artifactId>pdfbox</artifactId><version>3.0.0</version></dependency>`.
- [ ] T079 [LAC-14] Substituir gerador manual de PDF em `RelatorioService.gerarPdf()` com Apache PDFBox:
  - Remover métodos privados: `construirPdf()`, `escaparPdf()`, `normalizarTextoPdf()`, `escreverPdf()`.
  - Implementar novo método que: (a) cria `PDDocument`; (b) adiciona página com tabelas/texto; (c) retorna bytes binários parseáveis.
  - Testar com simples estrutura texto/tabela; não exigir design complexo.
- [ ] T080 [LAC-14] Criar DTOs de agregação: `RelatorioAgregacao` (base abstrata), `RelatorioAgregacaoDia`, `RelatorioAgregacaoSemana`, `RelatorioAgregacaoMes`, `RelatorioAgregacaoAlojamento`, `RelatorioAgregacaoColaborador`, `RelatorioAgregacaoTipoServico` em `pt.hotel.animais.dto.relatorio`.
- [ ] T081 [LAC-14] Integrar agrupamento em `RelatorioService.calcularMetricas()`:
  - Receber `RelatorioFiltroFormDto` com `agruparPor`.
  - Aplicar lógica de agrupamento **ao nível de agregação**, não apenas na apresentação.
  - Retornar lista de `RelatorioAgregacao*` (polimórfica ou typed).
  - Garantir que CSV, PDF e web reutilizam mesma lista agregada.
- [ ] T082 [LAC-14] Atualizar `RelatorioService.gerarCsv()` para usar estrutura agregada: cabeçalhos refletem grupos, totais por grupo aparecem no CSV.
- [ ] T083 [LAC-14] Atualizar `RelatorioService.gerarPdf()` para usar estrutura agregada: secções ou tabelas por grupo, totais por grupo no PDF.
- [ ] T084 [LAC-14] Adicionar validação de limite de período em `RelatorioController.gerarRelatorio()`:
  - Validar que `dataFim - dataInicio <= 3 meses`.
  - Se > 3 meses: regressar à página com erro claro: "Período máximo para exportação imediata é 3 meses. Selecione um intervalo menor ou contacte o suporte para processamento offline."
  - Aplicar validação também antes de chamar `gerarCsv()` e `gerarPdf()`.
- [ ] T085 [LAC-14] [P] Criar testes de validade e agrupamento:
  - `RelatorioServiceTest.testGerarPdfDeveProducirBinariosParseavel()`: valida `%PDF-` e parseabilidade com PDFBox.
  - `testGerarPdfContemDadosEsperados()`: extrai texto com PDFBox e valida títulos, datas, totais.
  - `testGerarPdfComAgrupamentoDia()`: PDF com `agruparPor=DIA` contém totais por dia.
  - `testGerarCsvComAgrupamento()`: CSV totais coincidem com PDF e web.
  - `RelatorioControllerTest.testExportarPdfComValoresParaGrupos()`: valida `Content-Type: application/pdf`, `Content-Disposition`, parseabilidade.
  - `testExportarPeriodoAcima3MesesRetornaErro()`: tenta período > 3 meses, valida erro 400.
  - `testAgrupamentoIdenticoEmCsvPdfWeb()`: mesmos totais em todos os formatos.
- [ ] T086 [LAC-14] Atualizar `specs/005-relatorios-colaboradores/contracts/contract.md` com semântica de agrupamento e limites:
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
