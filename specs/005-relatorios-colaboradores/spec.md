# Feature Specification: Relatórios e Colaboradores (Fase 5)

**Feature Branch**: `005-relatorios-colaboradores`  
**Created**: 2026-05-24  
**Status**: Draft clarificado  
**Input**: Clarificar o spec 005 para Spring MVC + Thymeleaf, com gestão de colaboradores exclusiva do diretor e `TipoColaborador` como enum.

## Âmbito

Esta feature completa os fluxos de direção e administração do Hotel de Animais: dashboard/relatórios por período, exportação de relatórios e gestão de colaboradores. A implementação deve seguir a arquitetura documentada em [architecture.md](../../docs/Etapa2/01-architecture/architecture.md): aplicação monolítica em camadas, controllers Spring MVC (`@Controller`) e páginas Thymeleaf renderizadas no servidor.

Os fluxos desta feature devem ser implementados exclusivamente por controllers MVC. Os controllers recebem dados por formulários e query parameters, invocam serviços de aplicação e devolvem nomes de templates Thymeleaf, `redirect:` ou ficheiros de exportação (`text/csv`, `application/pdf`) quando o utilizador pede download.

## User Scenarios & Testing *(mandatory)*

As user stories de referência são as definidas em [user-stories.md](../../docs/Etapa1/01-user-stories/user-stories.md), na secção "Diretor / Gestor". Este spec usa essas histórias sem as reescrever:

| ID | User Story | Prioridade |
|---|---|---|
| `US-01` | Como diretor, quero consultar a disponibilidade em tempo real dos alojamentos e a taxa de ocupação atual, para ter uma visão operacional do hotel. | Must Have |
| `US-02` | Como diretor, quero consultar indicadores de faturação e pagamentos pendentes filtráveis por período, para acompanhar o desempenho financeiro do hotel. | Must Have |
| `US-03` | Como diretor, quero gerir os perfis de acesso dos colaboradores — criando, editando e removendo permissões —, para garantir que cada um acede apenas às funcionalidades necessárias ao exercício das suas funções. | Must Have |
| `US-04` | Como diretor, quero gerar relatórios operacionais por período, para apoiar a tomada de decisão e o planeamento do negócio. | Must Have |
| `US-05` | Como diretor, quero consultar o histórico completo de estadias e pagamentos, para ter uma visão financeira e operacional do hotel. | Must Have |

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

- **FR-001 (map: RF-01, RF-03)**: O sistema deve disponibilizar uma página de dashboard/relatórios para o `DIRETOR`, com filtros por `dataInicio`, `dataFim`, `tipoAlojamento`, `incluirServicosExtra` e `agruparPor` (`DIA`, `SEMANA`, `MES`).
- **FR-002 (map: RF-01, RF-03)**: A página de relatório deve apresentar taxa de ocupação, estadias ativas no período, reservas no período, faturação total, faturação por método de pagamento, pagamentos pendentes e total de serviços extra.
- **FR-003 (map: RF-03, RF-10, RF-17)**: O `DIRETOR` deve poder exportar o relatório filtrado para `CSV` e `PDF`. A exportação deve preservar os filtros aplicados e incluir cabeçalhos/sumário.
- **FR-004 (map: RF-02, RNF-04)**: O sistema deve disponibilizar ao `DIRETOR` páginas de listagem, registo, edição e desativação de colaboradores.
- **FR-005 (map: RF-02, UC-01)**: Cada colaborador deve ter `id`, `username`, `nome`, `email`, `passwordHash`, `tipoColaborador`, `ativo`, `dataCriacao` e `ultimoLogin`.
- **FR-006 (map: RF-02, UC-01)**: `tipoColaborador` deve ser validado e persistido como enum `TipoColaborador` com valores `DIRETOR`, `FUNCIONARIO_RECEPCAO`, `CUIDADOR`, `MEDICO_VETERINARIO`, `RESPONSAVEL_LIMPEZA`.
- **FR-007 (map: RNF-04)**: A autorização deve seguir a matriz em [permissoes.md](../../docs/Etapa2/06-role-permissions/permissoes.md). Relatórios financeiros e gestão de colaboradores são exclusivos do `DIRETOR`.
- **FR-008 (map: RF-01)**: Os dados do dashboard/relatórios devem refletir eventos relevantes (check-in, check-out, criação/cancelamento de reserva, registo de pagamento) com latência máxima de 60 segundos ou por ação explícita de atualização.
- **FR-009 (map: RF-10, RF-17)**: Os relatórios financeiros devem manter rastreabilidade de pagamentos e serviços extra agregados por período, método, estado e estadia/reserva quando aplicável.
- **FR-010**: Validações de formulário devem regressar à mesma página com mensagens claras.

### Rotas MVC Previstas

| Fluxo | Rota MVC | Método | Resposta |
|---|---|---|---|
| Dashboard/relatórios | `/dashboard` ou `/relatorios` | `GET` | Template Thymeleaf com filtros e métricas |
| Gerar relatório filtrado | `/relatorios/gerar` | `POST` | Template Thymeleaf ou `redirect:/relatorios?...` |
| Exportar CSV | `/relatorios/exportar/csv` | `GET` | Download `text/csv` |
| Exportar PDF | `/relatorios/exportar/pdf` | `GET` | Download `application/pdf` |
| Listar colaboradores | `/colaboradores` | `GET` | Template Thymeleaf |
| Formulário de registo | `/colaboradores/novo` | `GET` | Template Thymeleaf com seleção de `TipoColaborador` |
| Criar colaborador | `/colaboradores` | `POST` | `redirect:/colaboradores` ou formulário com erros |
| Formulário de edição | `/colaboradores/{id}/editar` | `GET` | Template Thymeleaf |
| Atualizar colaborador | `/colaboradores/{id}` | `POST` | `redirect:/colaboradores` ou formulário com erros |
| Desativar colaborador | `/colaboradores/{id}/desativar` | `POST` | `redirect:/colaboradores` |

### Key Entities

- **Colaborador**: `id`, `username`, `nome`, `email`, `passwordHash`, `tipoColaborador: TipoColaborador`, `ativo`, `dataCriacao`, `ultimoLogin`.
- **TipoColaborador**: enum com `DIRETOR`, `FUNCIONARIO_RECEPCAO`, `CUIDADOR`, `MEDICO_VETERINARIO`, `RESPONSAVEL_LIMPEZA`.
- **Pagamento**: `id`, `valor`, `metodoPagamento`, `estado`, `momento`, `dataHora`, `estadiaId`/`reservaId`.
- **ServicoExtra**: `id`, `tipoServico`, `custo`, `dataHora`, `estadiaId`/`reservaId`.
- **FiltroRelatorio**: `dataInicio`, `dataFim`, `tipoAlojamento`, `incluirServicosExtra`, `agruparPor`.
- **RelatorioResumo**: métricas agregadas, filtros aplicados, `geradoEm`, `geradoPor`.

## Success Criteria *(mandatory)*

- **SC-001**: O `DIRETOR` consegue gerar um relatório mensal através da interface web e visualizar métricas operacionais e financeiras.
- **SC-002**: A exportação CSV/PDF contém os filtros aplicados, cabeçalhos estáveis e sumário das métricas principais.
- **SC-003**: Apenas o `DIRETOR` consegue abrir, submeter e concluir a criação/edição/desativação de colaboradores.
- **SC-004**: Um colaborador novo criado pelo `DIRETOR` aparece imediatamente na lista de colaboradores.
- **SC-005**: O formulário de colaborador apresenta `tipoColaborador` como lista fechada baseada na enum `TipoColaborador`.
- **SC-006**: Perfis sem permissão não conseguem aceder a relatórios financeiros nem páginas de colaboradores.
- **SC-007**: 95% das consultas interativas de relatório para períodos até 3 meses respondem em até 2 segundos; períodos até 12 meses devem responder em até 5 segundos ou apresentar estado de processamento.

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
- Alterações administrativas devem ser auditáveis pelo menos por data, colaborador autenticado e operação realizada.

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
