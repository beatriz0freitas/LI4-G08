# Feature Specification: Relatórios e Colaboradores (Fase 5)

**Feature Branch**: `005-relatorios-colaboradores`
**Created**: 2026-05-24
**Status**: Draft
**Input**: Cria o spec 005 da fase 5 de Plano de Implementação Gradual — Sistema de Gestão de Hotel de Animais; usar documentação da Etapa 1 e Etapa 2.

## User Scenarios & Testing *(mandatory)*

As user stories para este spec são as definidas em [docs/Etapa1/01-user-stories/user-stories.md](docs/Etapa1/01-user-stories/user-stories.md#L1). Em particular este spec implementa e valida os fluxos prioritários identificados para o perfil `DIRETOR` e operações administrativas (ver secção "Diretor / Gestor" no ficheiro de user stories).

Principais user stories cobertas (extracto):

- `US-01` — Consultar disponibilidade e taxa de ocupação (Must Have)
- `US-02` — Consultar indicadores de faturação e pagamentos (Must Have)
- `US-03` — Gerir perfis de acesso dos colaboradores (Must Have)
- `US-04` — Gerar relatórios operacionais por período (Must Have)
- `US-05` — Consultar histórico de estadias e pagamentos (Must Have)

Estas user stories estão completas no ficheiro referenciado e as cenários de aceitação devem ser validados conforme descrito em `user-stories.md`.

### Edge Cases

- Intervalos de datas inválidos (data início posterior à data fim): sistema apresenta mensagem de validação.
- Grandes volumes de dados: paginar visualização antes de exportar; permitir execução em background se necessário.
- Relatórios com dados sensíveis: apenas perfis autorizados podem ver campos financeiros detalhados.
## Requirements *(mandatory)*

Os requisitos funcionais e de domínio para este spec derivam dos ficheiros em [docs/Etapa1/02-requirements](docs/Etapa1/02-requirements). Em particular, considerar os RF (RF-01..RF-17) e os RD (RD-01..RD-09) ao validar o mapeamento entre funcionalidades e regras de domínio.

### Functional Requirements (detalhado e mapeado)

- **FR-001 (map: RF-03, RF-01)**: Geração de relatórios operacionais filtráveis por período (dia/mês/intervalo). Os relatórios devem suportar filtros: dataInício, dataFim, tipoAlojamento, incluirServicosExtra, e grupo por (dia, semana, mês).

- **FR-002 (map: RF-01, RF-03)**: Relatório/Resumo executivo com métricas mínimas:
	- taxa de ocupação (%) calculada como (boxes ocupadas / boxes totais) * 100;
	- número de estadias activas no período;
	- número de reservas (futuras/iniciadas) no período;
	- total de faturação no período (sum dos pagamentos liquidados e pendentes) e breakdown por `metodoPagamento` (NUMERARIO, CARTAO_DEBITO, CARTAO_CREDITO);
	- total de serviços extra e listagem por tipo (BANHO, PASSEIO, OUTRO).

- **FR-003 (map: RF-03, RF-10, RF-17)**: Exportação de relatórios em `CSV` (colunas tabulares com cabeçalhos) e `PDF` (layout executivo). Export deve preservar filtros aplicados e incluir um sumário com as métricas principais.

- **FR-004 (map: RF-02, RNF-04)**: Gestão de colaboradores: criar/editar/listar/desactivar colaboradores. Cada `Colaborador` tem: `id`, `nome`, `email`, `tipoColaborador` (DIRETOR, FUNCIONARIO_RECEPCAO, CUIDADOR, MEDICO_VETERINARIO, RESPONSAVEL_LIMPEZA), `estado` (ACTIVO/INACTIVO), `dataCriacao`, `ultimoLogin`.

- **FR-005 (map: RNF-04)**: Controlo de acessos e autorização por perfil. Apenas perfis com permissões adequadas podem aceder às funcionalidades de geração de relatórios financeiros e gestão de colaboradores. Autenticação por credenciais individuais (ver RNF-04).

- **FR-006 (map: RF-01)**: Actualização de dados do dashboard/relatórios: dados devem reflectir eventos relevantes (check-in, check-out, registo de pagamento, criação/cancelamento de reserva) e ser actualizados com latência inferior a 60s ou sinalizar execução/refresh explícito.

- **FR-007 (map: RF-09, RF-10, RF-17)**: Relatórios financeiros devem respeitar a rastreabilidade dos pagamentos: cada registo de pagamento inclui `valor`, `metodoPagamento`, `estado` (LIQUIDADO/PENDENTE) e `momento` (CHECK_IN/CHECK_OUT). Serviços extra devem ser agregados por reserva e incluídos na faturação do período.

- **FR-008**: Validação e mensagens: intervalos inválidos (dataInício > dataFim) devem provocar mensagem clara; relatórios sem dados devem apresentar secção "Sem dados" e permitir export com zero linhas quando aplicável.

### Key Entities

### Key Entities

- **Colaborador**: `id`, `nome`, `email`, `tipoColaborador`, `estado` (ACTIVO/INACTIVO), `dataCriacao`, `ultimoLogin`. (Alinhada com RF-02)
- **Pagamento**: `id`, `valor` (decimal), `metodoPagamento` (ENUM: NUMERARIO, CARTAO_DEBITO, CARTAO_CREDITO), `estado` (ENUM: PENDENTE, LIQUIDADO), `momento` (ENUM: CHECK_IN, CHECK_OUT), `dataHora`, `reservaId` (opcional). (Alinhada com RF-10)
- **ServicoExtra**: `id`, `tipoServico` (ENUM: BANHO, PASSEIO, OUTRO), `custo`, `dataHora`, `reservaId`. (Alinhada com RF-17)
- **Relatório**: `periodo` (`dataInicio`,`dataFim`), `filtros` (tipoAlojamento, incluirServicosExtra, grupoPor), `métricas` (ocupacaoPerc, estadiasCount, reservasCount, faturacaoTotal, faturacaoPorMetodo, servicosExtraTotal), `geradoEm`, `geradoPor`.
- **FiltroRelatorio**: parâmetros seleccionáveis (intervalo, tipoAlojamento, incluirServicosExtra, agruparPor).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 95% dos relatórios padrão gerados em menos de 5 segundos para conjuntos de dados até 12 meses de actividade; se exceder, o sistema devolve um token de execução em background e notifica quando pronto.
- **SC-002**: Dados apresentados no dashboard/relatórios reflectem eventos relevantes com latência máxima de 60 segundos (RF-01).
- **SC-003**: 100% das operações de criação/edição/desactivação de colaboradores refletem-se imediatamente na lista de colaboradores visível pelo administrador.
- **SC-004**: Exportação para CSV/PDF produz ficheiros com cabeçalhos correctos; o CSV tem colunas nominais estáveis (`periodo_start,periodo_end,ocupacaoPerc,estadiasCount,reservasCount,faturacaoTotal,metodoPagamento,servicosExtraTotal`)
- **SC-005**: Acesso a relatórios financeiros detalhados é permitido apenas a perfis com privilégio `DIRETOR` (verificação via testes de autorização automatizados e logs de acesso).
- **SC-006**: Cada pagamento incluído nos relatórios tem rastreabilidade: `idPagamento`, `valor`, `metodoPagamento`, `estado`, `momento`, `reservaId`.

### Performance targets — reconciliação

Há uma sobreposição aparente entre metas não-funcionais documentadas noutros artefactos (p.ex. `RNF-01`) e os critérios medíveis listados acima. Para claridade e testabilidade adoptamos a regra reconciliada abaixo:

- **PT-1 (interactivos, períodos ≤ 3 meses)**: 95% das consultas de relatórios padrão executam em ≤ 1s em infra normal de produção (baseline de dados até 3 meses).
- **PT-2 (agregados amplos, até 12 meses)**: 95% das consultas de relatórios executam em ≤ 5s; quando ultrapassado, o sistema oferece execução em background com token de consulta e notificações ao `geradoPor`.

Os testes de aceitação devem incluir cenários de carga sintética para validar ambos os limiares; ver tasks sugeridas em `tasks.md`.

### RBAC — Matriz de Autorização (normativa)

Esta feature exige uma matriz RBAC explícita que mapa operações (ConsultarRelatorio, ExportarCSV, ExportarPDF, GerirColaborador, VerCampoFinanceiro) para perfis. A matriz mínima obrigatória:

- `DIRETOR`: ConsultarRelatorio, ExportarCSV, ExportarPDF, VerCampoFinanceiro, GerirColaborador
- `FUNCIONARIO_RECEPCAO`: ConsultarRelatorio (sem VerCampoFinanceiro pormenorizado), ExportarCSV (agregado sem campos financeiros detalhados), GerirColaborador (criar/editar limitado)
- `CUIDADOR`: ConsultarRelatorio (somente dados operacionais), sem permissões de export financeiro
- `MEDICO_VETERINARIO`: ConsultarRelatorio (dados clínicos), sem acesso a campos financeiros sensíveis por defeito
- `RESPONSAVEL_LIMPEZA`: ConsultarRelatorio (dados operacionais mínimos), sem export

Implementação exigida: controllers e endpoints deverão declarar `@PreAuthorize` com expressões claras e ter testes de integração (`RelatoriosAuthIT`, `ClinicaAuthTest`) que validem cada combinação de permissão/endpoint.

### Integridade de dados e imutabilidade (referência a RD-09)

Relatórios financeiros dependem de registos de pagamento e serviços extra que devem ser imutáveis para efeitos de fatura e auditoria após o `check-out`. Requisitos obrigatórios:

- Todas as alterações que afectem `Pagamento`/`ServicoExtra` após `Estadia` estar `CHECKED_OUT` devem ser rejeitadas pela aplicação (400/403) e auditadas.
- Preferir defesa em profundidade: restrição a nível de serviço + migração DB (audit trail / append-only pattern) quando possível.
- Testes de regressão obrigatórios: cenários que tentem alterar valores após check-out e que verifiquem resposta 400/403 e que não alterem o ficheiro de faturação já gerado.

### HTTP error codes and validation behaviour

Padronizar códigos HTTP para facilitar a escrita de testes e comportamento UI:

- `400 Bad Request` — validação de input inválido (ex.: intervalo de datas errado, custo negativo)
- `401 Unauthorized` — sem autenticação
- `403 Forbidden` — autenticado mas sem permissão para a operação (ex.: tentativa de ver campos financeiros sem privilégio)
- `404 Not Found` — recurso inexistente
- `409 Conflict` — tentativa de alteração em conflito com estado imutável (alternativa a 400/403 quando aplicável)
- `500 Internal Server Error` — falhas não previstas (registar e investigar)

Os testes de integração e os critérios de aceitação devem verificar explicitamente estes códigos nos cenários relevantes.

### UI signalling for sensitive and critical items

Para consistência visual e testabilidade, definir elementos de UI para sinalizar dados sensíveis ou críticos nos relatórios:

- **Badge sensível**: `badge--sensitive` para colunas que contenham campos financeiros detalhados — visível apenas a perfis com `VerCampoFinanceiro`.
- **Acessibilidade**: incluir `role="status"` e `aria-live="polite"` sempre que um badge for dinamicamente inserido.
- **Teste de aceitação visual**: criar um cenário que autentique como `DIRETOR`, abra um relatório com campos financeiros e verifique a presença do `badge--sensitive`.

### Test coverage mapping (transformar testes implícitos em tasks)

O spec menciona nomes de testes e verificações; para rastreabilidade cada teste referido deve existir em `tasks.md` como tarefa concreta. Exemplos a garantir:

- `RelatoriosAuthIT` / `ClinicaAuthTest` — validações RBAC para endpoints de relatórios e gestão de colaboradores
- `RelatoriosPerfIT` — benchmarks sintéticos para `PT-1` e `PT-2`
- `ImmutableAfterCheckoutIT` — tentativa de alteração de `Pagamento`/`ServicoExtra` após check-out

Adicionar estas tarefas permitirá cobertura de testes automatizados e integração com CI.

## Assumptions

- A base de dados existente contém as entidades `Reserva`, `Estadia`, `Pagamento` já normalizadas conforme Etapa 2/Etapa 3.
- O sistema já tem um mecanismo de autenticação e autorização baseado em perfis conforme `plano-implementacao-gradual.md` e ADRs (Etapa 2).
- Formatos de exportação (CSV, PDF) são formatos de saída; a implementação concreta fica para a equipa de implementação.
- Relatórios agregados são baseados apenas em dados persistidos (não em caches voláteis).

## Traceability

- Use Cases: UC-13 (Relatórios consolidados), UC-02 (Gestão administrativa).
- User Stories: `US-01`,`US-02`,`US-03`,`US-04`,`US-05` (ver [docs/Etapa1/01-user-stories/user-stories.md](docs/Etapa1/01-user-stories/user-stories.md#L1)).
- Functional Requirements (Etapa 1) mapeados explicitamente:
	- `RF-01` (Dashboard operacional) — alimenta métricas e latência ≤60s;
	- `RF-02` (Gestão de perfis) — ações de criação/edição/desactivação de colaboradores;
	- `RF-03` (Relatórios operacionais) — geração e exportação filtrável;
	- `RF-09` (Check-out e faturação complementar) — cálculo de faturação do período;
	- `RF-10` (Registo de pagamentos) — campos obrigatórios para pagamentos e rastreabilidade;
	- `RF-17` (Registo de serviços extra) — agregação de serviços na faturação.
- Non-functional: `RNF-04` (Autenticação e permissões) — controlo de acesso por perfil.

## Acceptance Tests (suggested)

- Test A: Autenticar como `DIRETOR`, gerar relatório mensal, validar presença de métricas principais e exportar CSV; verificar formato e conteúdo do CSV.
- Test B: Criar um colaborador novo via interface de gestão e verificar que não consegue autenticar se desactivado.
- Test C: Gerar relatório para intervalo sem dados — verificar mensagens de "Sem dados" e exportação com zero linhas.


---


