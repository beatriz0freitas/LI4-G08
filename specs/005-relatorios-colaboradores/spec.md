# Feature Specification: Relatórios e Colaboradores (Fase 5)

**Feature Branch**: `005-relatorios-colaboradores`
**Created**: 2026-05-24
**Status**: Implementado e validado em Etapa 4
**Input**: Clarificar o spec 005 para Spring MVC + Thymeleaf, com gestão de colaboradores exclusiva do diretor e `TipoColaborador` como enum.

## Âmbito

Esta feature completa os fluxos de direção e administração do Hotel de Animais: dashboard/relatórios por período, exportação de relatórios e gestão de colaboradores. A implementação deve seguir a arquitetura documentada em [architecture.md](../../docs/Etapa2/01-architecture/architecture.md): aplicação monolítica em camadas, controllers Spring MVC (`@Controller`) e páginas Thymeleaf renderizadas no servidor.

Os fluxos desta feature devem ser implementados exclusivamente por controllers MVC. Os controllers recebem dados por formulários e query parameters, invocam serviços de aplicação e devolvem nomes de templates Thymeleaf, `redirect:` ou ficheiros de exportação (`text/csv`, `application/pdf`) quando o utilizador pede download.

## Mockups de UI

Referências visuais para esta feature (Etapa 2 - `05-ui-interface-mockup`):

- [wf02-dashboard-diretor.html](../../docs/Etapa2/05-ui-interface-mockup/wf02-dashboard-diretor.html) — dashboard e indicadores para o `DIRETOR`.
- [wf07-colaboradores.html](../../docs/Etapa2/05-ui-interface-mockup/wf07-colaboradores.html) — ecrã de listagem e gestão de colaboradores.

## User Scenarios & Testing *(mandatory)*

As user stories de referência são as definidas em [user-stories.md](../../docs/Etapa1/01-user-stories/user-stories.md), na secção "Diretor / Gestor". Este spec usa essas histórias sem as reescrever:

| ID        | User Story                                                                                                                                                                                                                 | Prioridade |
| --------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------- |
| `US-01` | Como diretor, quero consultar a disponibilidade em tempo real dos alojamentos e a taxa de ocupação atual, para ter uma visão operacional do hotel.                                                                      | Must Have  |
| `US-02` | Como diretor, quero consultar indicadores de faturação e pagamentos pendentes filtráveis por período, para acompanhar o desempenho financeiro do hotel.                                                                | Must Have  |
| `US-03` | Como diretor, quero gerir os perfis de acesso dos colaboradores — criando, editando e removendo permissões —, para garantir que cada um acede apenas às funcionalidades necessárias ao exercício das suas funções. | Must Have  |
| `US-04` | Como diretor, quero gerar relatórios operacionais por período, para apoiar a tomada de decisão e o planeamento do negócio.                                                                                             | Must Have  |
| `US-05` | Como diretor, quero consultar o histórico completo de estadias e pagamentos, para ter uma visão financeira e operacional do hotel.                                                                                       | Must Have  |

### Cenários de Teste Derivados

- `US-01`/`US-02`: o `DIRETOR` abre o dashboard e consulta disponibilidade, taxa de ocupação, faturação e pagamentos pendentes filtráveis por período.
- `US-03`: o `DIRETOR` acede à gestão de colaboradores, cria um colaborador com `tipoColaborador` válido, edita dados e remove/desativa permissões.
- `US-04`: o `DIRETOR` gera um relatório operacional por período e exporta o resultado.
- `US-05`: o `DIRETOR` consulta o histórico completo de estadias e pagamentos.

### Edge Cases

- Intervalo de datas inválido: a página deve apresentar mensagem de validação e preservar os valores preenchidos.
- Período sem dados: a página deve apresentar estado "Sem dados" e permitir exportação vazia quando fizer sentido.
- Colaborador com `email`/`username` duplicado: a página deve apresentar erro de validação.
- Tentativa de acesso por perfil sem permissão: a navegação deve ser bloqueada pela autorização Spring Security.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001 (map: RF-01, RF-03)**: O sistema deve disponibilizar uma página de dashboard/relatórios para o `DIRETOR`, com filtros por `dataInicio`, `dataFim`, `tipoAlojamento`, `incluirServicosExtra` e `agruparPor` (`DIA`, `SEMANA`, `MES`, `ALOJAMENTO`, `COLABORADOR`, `TIPO_SERVICO`).
- **FR-002 (map: RF-01, RF-03)**: A página de relatório deve apresentar taxa de ocupação, estadias ativas no período, reservas no período, faturação total, faturação por método de pagamento, pagamentos pendentes e total de serviços extra.
- **FR-003 (map: RF-03, RF-10, RF-17)**: O `DIRETOR` deve poder exportar o relatório filtrado para `CSV` e `PDF`. A exportação deve preservar os filtros aplicados e incluir cabeçalhos/sumário. **(LAC-14)**: PDF exportado deve ser um ficheiro PDF válido e parseável (não texto simples com extensão .pdf); utilizar biblioteca robusta (ex: Apache PDFBox ou OpenPDF).
- **FR-004 (map: RF-02, RNF-04)**: O sistema deve disponibilizar ao `DIRETOR` páginas de listagem, registo, edição e desativação de colaboradores.
- **FR-005 (map: RF-02, UC-01)**: Cada colaborador deve ter `id`, `username`, `nome`, `email`, `passwordHash`, `tipoColaborador`, `ativo`, `dataCriacao` e `ultimoLogin`.
- **FR-006 (map: RF-02, UC-01)**: `tipoColaborador` deve ser validado e persistido como enum `TipoColaborador` com valores `DIRETOR`, `FUNCIONARIO_RECEPCAO`, `CUIDADOR`, `MEDICO_VETERINARIO`, `RESPONSAVEL_LIMPEZA`.
- **FR-007 (map: RNF-04)**: A autorização deve seguir a matriz em [permissoes.md](../../docs/Etapa2/06-role-permissions/permissoes.md). Relatórios financeiros e gestão de colaboradores são exclusivos do `DIRETOR`.
- **FR-008 (map: RF-01)**: Os dados do dashboard/relatórios devem refletir eventos relevantes (check-in, check-out, criação/cancelamento de reserva, registo de pagamento) com latência máxima de 60 segundos ou por ação explícita de atualização.
- **FR-009 (map: RF-10, RF-17)**: Os relatórios financeiros devem manter rastreabilidade de pagamentos e serviços extra agregados por período, método, estado e estadia/reserva quando aplicável.
- **(LAC-14) FR-012**: O relatório deve suportar agrupamento explícito de dados conforme parâmetro `agruparPor`: `DIA`, `SEMANA`, `MES`, `ALOJAMENTO`, `COLABORADOR`, `TIPO_SERVICO`. O agrupamento deve ser aplicado ao nível da agregação de dados, não apenas na apresentação; os totais grupalizados devem aparecer identicamente em CSV, PDF e visualização na web.
- **(LAC-14) FR-013**: Exportações síncronas de relatório devem completar em até 30 segundos para períodos até 3 meses (com até ~10.000 registos). Períodos > 3 meses devem indicar ao utilizador a necessidade de usar um intervalo menor ou aguardar processamento assíncrono (vide RNF-08 de planeamento futuro).
- **FR-010**: Validações de formulário devem regressar à mesma página com mensagens claras.
- **FR-011 *(novo, LAC-13)***: O sistema deve manter tabela de auditoria `AuditoriaEvento` com registo de todas as operações críticas: criar/editar/cancelar reserva, check-in, check-out, registo/alteração de pagamento, cuidados, intervenção clínica, limpeza, geração de relatórios e gestão de colaboradores. Cada evento deve registar `timestamp`, utilizador autenticado, operação, entidade, ação, detalhes em JSON e resultado (sucesso/falha). O ID da entidade é obrigatório quando a operação afeta um registo persistido e opcional para `RELATORIO_GERADO`, que não cria nem altera entidade de domínio.

### Rotas MVC Previstas

| Fluxo                     | Rota MVC                          | Método  | Resposta                                                |
| ------------------------- | --------------------------------- | -------- | ------------------------------------------------------- |
| Dashboard/relatórios     | `/dashboard` ou `/relatorios` | `GET`  | Template Thymeleaf com filtros e métricas              |
| Gerar relatório filtrado | `/relatorios/gerar`             | `POST` | Template Thymeleaf ou `redirect:/relatorios?...`      |
| Exportar CSV              | `/relatorios/exportar/csv`      | `GET`  | Download `text/csv`                                   |
| Exportar PDF              | `/relatorios/exportar/pdf`      | `GET`  | Download `application/pdf`                            |
| Listar colaboradores      | `/colaboradores`                | `GET`  | Template Thymeleaf                                      |
| Formulário de registo    | `/colaboradores/novo`           | `GET`  | Template Thymeleaf com seleção de `TipoColaborador` |
| Criar colaborador         | `/colaboradores`                | `POST` | `redirect:/colaboradores` ou formulário com erros    |
| Formulário de edição   | `/colaboradores/{id}/editar`    | `GET`  | Template Thymeleaf                                      |
| Atualizar colaborador     | `/colaboradores/{id}`           | `POST` | `redirect:/colaboradores` ou formulário com erros    |
| Desativar colaborador     | `/colaboradores/{id}/desativar` | `POST` | `redirect:/colaboradores`                             |

### Key Entities

- **Colaborador**: `id`, `username`, `nome`, `email`, `passwordHash`, `tipoColaborador: TipoColaborador`, `ativo`, `dataCriacao`, `ultimoLogin`.
- **TipoColaborador**: enum com `DIRETOR`, `FUNCIONARIO_RECEPCAO`, `CUIDADOR`, `MEDICO_VETERINARIO`, `RESPONSAVEL_LIMPEZA`.
- **Pagamento**: `id`, `valor`, `metodoPagamento`, `estado`, `momento`, `dataHora`, `estadiaId`/`reservaId`.
- **ServicoExtra**: `id`, `tipoServico`, `custo`, `dataHora`, `estadiaId`/`reservaId`.
- **FiltroRelatorio**: `dataInicio`, `dataFim`, `tipoAlojamento`, `incluirServicosExtra`, `agruparPor`.
- **RelatorioResumo**: métricas agregadas, filtros aplicados, `geradoEm`, `geradoPor`.
- **AuditoriaEvento** *(novo, LAC-13)*: `id`, `timestamp`, `utilizadorId` (FK para `Colaborador`), `operacao` (string: "CRIAR_RESERVA", "CHECK_IN", "RELATORIO_GERADO", etc.), `entidade` (string: "Reserva", "Estadia", "Relatorio", etc.), `entityId` (Long opcional apenas quando não há entidade persistida afetada), `acao` (string: "CREATE", "UPDATE", "DELETE", "READ"), `detalhes` (JSON com campos alterados ou contexto), `resultado` (string: "SUCESSO", "FALHA"), `motivoFalha` (string, opcional).

## Success Criteria *(mandatory)*

- **SC-001**: O `DIRETOR` consegue gerar um relatório mensal em até 5 segundos através da interface web e visualizar métricas operacionais e financeiras (taxa de ocupação %, estadias ativas, reservas, faturação total, pagamentos pendentes).
- **SC-002**: A exportação CSV/PDF contém os filtros aplicados, cabeçalhos estáveis e sumário das métricas principais.
- **(LAC-14) SC-010**: Ficheiro PDF exportado começa com assinatura `%PDF-` e é parseável por ferramentas PDF standard; testes validam contenção de dados esperados e formatação legível.
- **(LAC-14) SC-011**: Agrupamento selecionado em `agruparPor` é aplicado aos dados antes de renderizar: CSV contém secções/totais por grupo, PDF contém quebras de página ou sumário por grupo, e web visualiza dados agrupados. Totais grupalizados são idênticos em todos os formatos.
- **(LAC-14) SC-012**: Exportação de relatório com período > 3 meses completa ou apresenta mensagem clara ao utilizador indicando limite de período e opções (usar período menor, ou agendar processamento offline).
- **SC-003**: Apenas o `DIRETOR` consegue abrir, submeter e concluir a criação/edição/desativação de colaboradores.
- **SC-004**: Um colaborador novo criado pelo `DIRETOR` aparece imediatamente na lista de colaboradores.
- **SC-005**: O formulário de colaborador apresenta `tipoColaborador` como lista fechada baseada na enum `TipoColaborador`.
- **SC-006**: Perfis sem permissão não conseguem aceder a relatórios financeiros nem páginas de colaboradores.
- **SC-007**: 95% das consultas interativas de relatório para períodos até 3 meses respondem em até 2 segundos (com índices em `timestamp`, `estado`; dataset até 12 meses; sem cache aplicacional); períodos até 12 meses devem responder em até 5 segundos ou apresentar estado de processamento.
- **SC-008 *(novo, LAC-13)***: Cada operação crítica (criar/editar/cancelar reserva, check-in, check-out, pagamento, cuidados, clínica, limpeza, relatório, colaborador) registada com sucesso cria um evento em `AuditoriaEvento` com utilizador, operação, entidade, ação e resultado; `entityId` é preenchido quando existe registo persistido afetado.
- **SC-009 *(novo, LAC-13)***: O `DIRETOR` consegue consultar a auditoria filtrada por data, utilizador autenticado e operação, com resultados retendo 12 meses históricos.

## RBAC

A autorização normativa desta feature está centralizada em [docs/Etapa2/06-role-permissions/permissoes.md](../../docs/Etapa2/06-role-permissions/permissoes.md).

Resumo para esta feature:

- `DIRETOR`: consultar dashboard/relatórios, exportar CSV/PDF, ver campos financeiros, criar/editar/desativar colaboradores.
- `FUNCIONARIO_RECEPCAO`: sem acesso à gestão de colaboradores; pode consultar histórico operacional conforme matriz global.
- `CUIDADOR`: sem acesso à gestão de colaboradores e sem acesso a relatórios financeiros.
- `MEDICO_VETERINARIO`: sem acesso à gestão de colaboradores e sem acesso a relatórios financeiros.
- `RESPONSAVEL_LIMPEZA`: sem acesso à gestão de colaboradores e sem acesso a relatórios financeiros.

## Integridade de Dados

- Palavras-passe devem ser armazenadas apenas como hash BCrypt.
- Desativar colaborador é operação lógica; não deve apagar histórico associado.
- Pagamentos e serviços extra já faturados não devem ser alterados depois do check-out, conforme regras de imutabilidade associadas a RD-09.
- Auditoria (vide secção "Clarifications" abaixo): todas as operações críticas (criar/editar/cancelar reserva, check-in, check-out, pagamento, cuidados, intervenção clínica, limpeza, geração de relatórios, gestão de colaboradores) devem ser auditadas em tabela relacional dedicada `AuditoriaEvento`, com retenção de 12 meses e acesso restrito a administradores.
- Não deve coexistir um segundo rasto funcional (por exemplo, `AuditEventRepository`/`AuditApplicationEvent` do Actuator); a auditoria deve ser registada de forma centralizada em `AuditoriaService` através de `AuditoriaOperacaoService`.

## Documentação Técnica

- O código Java deve ser documentado com Javadoc em controllers, services, DTOs e exceptions públicas ou relevantes para fluxos de negócio.
- O Maven Javadoc Plugin deve gerar a documentação HTML do código.
- A implementação deve seguir `.specify/memory/constitution.md` e as convenções de estilo em [docs/Etapa3/convencoes.md](../../docs/Etapa3/convencoes.md).

## Test Coverage Mapping

- `RelatorioControllerTest`: renderização das páginas de relatório, validações de filtros e exportação.
- `RelatorioServiceTest`: agregações de ocupação, faturação, pagamentos e serviços extra.
- `ColaboradorControllerTest`: acesso exclusivo do `DIRETOR`, submissão de formulários e mensagens de validação.
- `ColaboradorServiceTest`: criação, edição, desativação, BCrypt e validação de `TipoColaborador`.
- `SecurityIntegrationTest`: combinações de perfis da matriz de permissões.

## Assumptions

- A autenticação existente será migrada de utilizadores em memória para `Colaborador` persistido, mantendo Spring Security.
- `RelatorioController` e `ColaboradorController` podem ainda não existir no código; esta feature define o comportamento esperado.
- A exportação CSV/PDF pode ser síncrona enquanto cumprir os limites de desempenho definidos.
- **(LAC-14)**: PDF é exportado como ficheiro binário real, não como texto com extensão .pdf. O `Content-Type` é `application/pdf` e o cliente recebe bytes parseáveis.

## Traceability

- Use Cases: `UC-01`, `UC-13`.
- User Stories: `US-01`, `US-02`, `US-03`, `US-04`, `US-05`.
- Functional Requirements: `RF-01`, `RF-02`, `RF-03`, `RF-09`, `RF-10`, `RF-17`.
- Non-functional: `RNF-04`.
- Architecture: [architecture.md](../../docs/Etapa2/01-architecture/architecture.md).
- Role permissions: [permissoes.md](../../docs/Etapa2/06-role-permissions/permissoes.md).

## Acceptance Tests

- Test A: autenticar como `DIRETOR`, abrir `/relatorios`, gerar relatório mensal e exportar CSV.
- Test B: autenticar como `DIRETOR`, abrir `/colaboradores/novo`, criar colaborador com `tipoColaborador = CUIDADOR` e confirmar presença na lista.
- Test C: autenticar como `FUNCIONARIO_RECEPCAO` e confirmar que `/colaboradores` e `/relatorios` não ficam acessíveis.
- Test D: submeter colaborador com `tipoColaborador` inválido e confirmar erro de validação na página, sem criação de registo.

## Clarifications

### Session 2026-05-26

**LAC-13 – Auditoria incompleta para operações críticas (resolvida)**

- Q: Qual é o ámbito completo de operações a auditar? → A: Auditoria Completa: todas as operações críticas (criar/editar/cancelar reserva, check-in, check-out, pagamento, cuidados, intervenção clínica, limpeza, gestão de colaboradores).
- Q: Qual é o formato de armazenamento e estrutura de auditoria? → A: Tabela dedicada `AuditoriaEvento` com campos: `id`, `timestamp`, `utilizador`, `operacao`, `entidade`, `entityId`, `acao`, `detalhes` (JSON), `resultado`.
- Q: Qual é a política de retenção e acesso a auditoria? → A: Retenção de 12 meses, acesso restrito a administradores (`DIRETOR`), com filtros por data, utilizador e operação.

### Estado de Implementação 2026-05-27

- A tabela `auditoria_evento` está definida por migração Flyway e mapeada pela entidade `AuditoriaEvento`.
- O contrato `IAuditoriaService` e a implementação `AuditoriaService` estão ativos para registo, consulta e limpeza de eventos.
- As rotas `GET /auditoria` e `GET /auditoria/exportar/csv` estão funcionais e protegidas para `DIRETOR`.
- A retenção de 12 meses está operacional em `AuditoriaSchedulerJob`, executado diariamente às 03h00.
- A auditoria foi integrada em colaboradores, reservas existentes, estadias, pagamentos, cuidados, intervenção clínica, serviços extra e limpeza.
- A geração de relatórios regista `RELATORIO_GERADO` na mesma tabela através de `AuditoriaOperacaoService`; não é usado o rasto em memória do Spring Boot Actuator.
- A operação `EDITAR_RESERVA` permanece pendente porque o fluxo de edição de reserva ainda não existe na aplicação atual.
- A validação focada de auditoria passou e `HotelAnimaisApplicationTests` carregou o contexto contra MySQL de testes, aplicando Flyway até `V13`; a suite global não foi reexecutada nesta alteração.
