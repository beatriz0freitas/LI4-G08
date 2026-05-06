# Feature Specification: Reservas, Estadias e Pagamentos

**Feature Branch**: `003-reservas-estadias-pagamentos`  
**Created**: 6 de Maio de 2026  
**Status**: Draft  
**Input**: Fase 3 do plano de implementação gradual — ciclo operacional principal do hotel de animais

**Origem documentária**:
- [Plano de Implementação Gradual — Fase 3](../../docs/Etapa3/plano-implementacao-gradual.md#fase-3--reservas-estadias-e-pagamentos)
- [UC-04: Criar Reserva](../../docs/Etapa1/03-use-cases/UC-04.md)
- [UC-05: Cancelar Reserva](../../docs/Etapa1/03-use-cases/UC-05.md)
- [UC-06: Registar Check-in](../../docs/Etapa1/03-use-cases/UC-06.md)
- [UC-07: Registar Check-out](../../docs/Etapa1/03-use-cases/UC-07.md)
- [UC-08: Processar Faturacao e Pagamento](../../docs/Etapa1/03-use-cases/UC-08.md)

---

## User Scenarios & Testing

### User Story 1 - Criar reserva com disponibilidade confirmada (Priority: P1)

Um funcionário de receção precisa de criar uma reserva para um cliente quando este contacta para alojar o seu animal. O sistema deve verificar a disponibilidade do alojamento no período pretendido, apresentar opções e permitir a confirmação imediata.

**Why this priority**: Criação de reserva é o ponto de entrada crítico do ciclo operacional. Sem isto, não há estadias nem pagamentos. Alinha-se com UC-04 e RD-01 (gestão de disponibilidade).

**Independent Test**: Funcionário consegue criar uma reserva completa (tutor + animal + período + alojamento), e o alojamento fica marcado como indisponível no período. Teste é independente de check-in e pagamento.

**Acceptance Scenarios**:

1. **Given** um tutor e animal registados no sistema e alojamento disponível no período, **When** o funcionário preenche data inicial, data final e seleciona alojamento, **Then** a reserva é criada, confirmada e o alojamento fica indisponível.

2. **Given** um período em que não há alojamentos livres, **When** o funcionário tenta criar reserva, **Then** o sistema apresenta alternativas de datas ou comunica indisponibilidade total.

3. **Given** uma reserva já criada, **When** outro funcionário tenta usar o mesmo alojamento no mesmo período, **Then** o sistema impede a sobreposição e marca como ocupado.

---

### User Story 2 - Cancelar reserva e liberar alojamento (Priority: P1)

Um funcionário de receção precisa cancelar uma reserva quando um cliente avisa que não vai utilizar o alojamento. O sistema deve liberar o alojamento imediatamente para que outras reservas possam ser feitas.

**Why this priority**: Operação crítica de gestão de disponibilidade em tempo real. Sem isto, alojamentos ficam presos em reservas canceladas. Alinha-se com UC-05 e RD-06.

**Independent Test**: Funcionário consegue localizar uma reserva confirmada, cancela-a, e o alojamento volta a estar disponível para o período original.

**Acceptance Scenarios**:

1. **Given** uma reserva confirmada, **When** o funcionário acessa a reserva e seleciona "Cancelar", **Then** o sistema altera o estado para CANCELADA e liberta o alojamento.

2. **Given** uma reserva cancelada, **When** outro funcionário consulta o período, **Then** o alojamento aparece disponível novamente.

3. **Given** uma reserva em estado ATIVA, **When** o funcionário cancela, **Then** o sistema mantém auditoria (registo histórico da cancelação).

---

### User Story 3 - Registar check-in e processar pagamento base (Priority: P1)

Um funcionário de receção precisa registar a entrada de um animal no hotel quando o cliente chega na data combinada. O sistema deve confirmar dados do animal, calcular o custo total da estadia e processar o pagamento.

**Why this priority**: Check-in marca o início operacional da estadia e viabiliza o fluxo de cuidados. Sem isto, não há estadia registada nem cuidados podem ser rastreados. Alinha-se com UC-06 e RD-02, RD-04.

**Independent Test**: Funcionário consegue localizar reserva confirmada, processa pagamento (qualquer método) e o animal fica registado como hospedado no alojamento.

**Acceptance Scenarios**:

1. **Given** uma reserva confirmada para a data atual, **When** o funcionário pesquisa a reserva por nome de tutor/animal e confirma dados, **Then** o sistema calcula o custo total da estadia (dias × tarifa base do alojamento) e apresenta para pagamento.

2. **Given** o pagamento apresentado, **When** o funcionário seleciona método (NUMERARIO, CARTAO_DEBITO, CARTAO_CREDITO) e confirma, **Then** o sistema regista o pagamento com estado LIQUIDADO e marca a estadia como EM_CURSO.

3. **Given** um cliente sem capacidade de pagamento imediato, **When** o funcionário regista pagamento com estado PENDENTE, **Then** o sistema permite check-in e marca dívida consultável pelo diretor.

4. **Given** check-in bem-sucedido, **When** o sistema atualiza o estado do alojamento, **Then** marca-o como OCUPADO (afeta RD-01 — disponibilidade em tempo real).

---

### User Story 4 - Registar check-out e pagamento de extras (Priority: P1)

Um funcionário de receção precisa registar a saída do animal quando o cliente vem levantá-lo. Se houver serviços adicionais (banho, passeio, medicação), o sistema deve apresentar o valor incremental e processar pagamento.

**Why this priority**: Check-out é o ponto de encerramento da estadia e garante que alojamento volta a estar disponível para limpeza e nova reserva. Alinha-se com UC-07 e RD-03, RD-04.

**Independent Test**: Funcionário consegue localizar estadia em curso, confirmar dados, registar check-out. Se houver extras, sistema calcula incremento e permite pagamento. Alojamento muda estado para PENDENTE_LIMPEZA.

**Acceptance Scenarios**:

1. **Given** uma estadia com check-in registado, **When** o funcionário localiza a estadia e seleciona "Registar Check-out", **Then** o sistema apresenta resumo (datas, animal, custo base) sem serviços extras.

2. **Given** estadia com serviços adicionais registados, **When** o sistema exibe resumo, **Then** lista cada serviço com custo individual e total de extras.

3. **Given** o resumo apresentado, **When** o funcionário confirma check-out com pagamento adicional, **Then** o sistema regista pagamento e encerra estadia com estado TERMINADA.

4. **Given** check-out concluído com sucesso, **When** o sistema atualiza alojamento, **Then** marca-o com estado PENDENTE_LIMPEZA e torna-o indisponível para novas reservas até limpeza.

---

### User Story 5 - Consultar histórico de pagamentos por estadia (Priority: P2)

Um funcionário ou diretor precisa consultar o histórico de pagamentos associados a uma estadia para auditar e reconciliar com contabilidade.

**Why this priority**: Suporte operacional e de conformidade. Sem isto, não há rastreabilidade de fluxos de caixa. Alinha-se com RD-04 e requisitos de faturação.

**Independent Test**: Funcionário consegue localizar uma estadia, visualizar todos os pagamentos associados (check-in + check-out + extras se houver) com valores, métodos e estados.

**Acceptance Scenarios**:

1. **Given** uma estadia com um ou mais pagamentos, **When** o funcionário acessa detalhes da estadia, **Then** o sistema lista cronologicamente todos os pagamentos com data, valor, método, momento (CHECK_IN ou CHECK_OUT) e estado.

2. **Given** pagamentos com estado misto (alguns LIQUIDADO, alguns PENDENTE), **When** o sistema apresenta, **Then** diferencia visualmente e permite ação sobre pendentes.

---

## Requirements

### Functional Requirements

- **FR-06**: Sistema DEVE permitir criar reserva associando tutor, animal, alojamento e período, com validação de disponibilidade em tempo real (UC-04, RD-01).
- **FR-07**: Sistema DEVE permitir cancelar reserva confirmada e liberar o alojamento para o período (UC-05, RD-06).
- **FR-08**: Sistema DEVE registar check-in de estadia ligando reserva confirmada a alojamento, com transição obrigatória de estados (Reserva: ATIVA → associada a Estadia; Alojamento: DISPONIVEL → OCUPADO) (UC-06, RD-02).
- **FR-09**: Sistema DEVE registar check-out de estadia com transição de estado (Estadia: EM_CURSO → TERMINADA; Alojamento: OCUPADO → PENDENTE_LIMPEZA) (UC-07, RD-03).
- **FR-10**: Sistema DEVE registar pagamentos em dois momentos distintos: CHECK_IN (custo integral da estadia) e CHECK_OUT (serviços adicionais, se existam), com suporte a três métodos (NUMERARIO, CARTAO_DEBITO, CARTAO_CREDITO) e dois estados (LIQUIDADO, PENDENTE) (UC-08, RD-04).
- **FR-01-estadia**: Sistema DEVE manter dashboa com ocupação em tempo real, incluindo número de estadias em curso e receita acumulada (RD-02).
- **FR-06-pagamento-pendente**: Sistema DEVE manter lista de pagamentos pendentes consultável apenas pelo diretor para reconciliação com contabilidade (RD-04).

### Key Entities

- **Reserva**: Ligação entre Tutor, Animal, Alojamento e período (dataInicio, dataFim). Estados: ATIVA, CANCELADA, CONCLUIDA. Uma reserva pode desencadear no máximo uma Estadia (RD-06).
- **Estadia**: Documento de hospedagem com referência a uma Reserva. Estados: EM_CURSO, TERMINADA. Inclui checkIn (data/hora) e checkOut (data/hora). Máximo uma Estadia por Animal em qualquer período (RD-07).
- **Pagamento**: Registo de transação com valor, método, momento (CHECK_IN ou CHECK_OUT), estado (LIQUIDADO ou PENDENTE) e referência a Estadia. Máximo dois pagamentos por Estadia (um CHECK_IN obrigatório, um CHECK_OUT opcional).
- **Alojamento**: Entidade existente (Fase 1), mas agora com ciclo de estado expandido: DISPONIVEL → OCUPADO → PENDENTE_LIMPEZA → CONCLUIDO → DISPONIVEL.

---

## Success Criteria

### Measurable Outcomes

- **SC-001**: Funcionário consegue criar reserva em menos de 2 minutos (fluxo normal: pesquisa tutor/animal → verifica datas → seleciona alojamento → confirma).
- **SC-002**: Cancelamento de reserva torna alojamento disponível em tempo real (max 5 segundos) sem erros de sobreposição em consultas simultâneas.
- **SC-003**: Check-in e pagamento completam em menos de 3 minutos por cliente.
- **SC-004**: Check-out com extras listados em menos de 2 minutos.
- **SC-005**: 100% das transações de pagamento registam estado (LIQUIDADO ou PENDENTE) sem perda de dados.
- **SC-006**: Histórico de pagamentos recuperável em tempo real (< 1 segundo) por qualquer estadia.
- **SC-007**: Transições de estado de Alojamento (DISPONIVEL ↔ OCUPADO ↔ PENDENTE_LIMPEZA) ocorrem automaticamente e sem inconsistências.
- **SC-008**: Dashboard do diretor atualiza ocupação e receita em tempo real com erro máximo de 5 segundos.

---

## Assumptions

- Tutor e Animal já estão registados (Fase 2 completa).
- Tarifa base do alojamento está definida no cadastro (Fase 1).
- Serviços adicionais são registados em paralelo pela equipa de cuidados (será detalhado em Fase 4, mas o pagamento é processado aqui).
- Autenticação e autorização por perfil já estão funcionais (Fase 1: `FUNCIONARIO_RECEPCAO`, `DIRETOR`).
- Todos os pagamentos são em moeda única (não há conversão).
- Estadia não pode ter check-out registado sem check-in anterior.
- Uma vez TERMINADA, a Estadia não pode voltar a EM_CURSO.
- Reserva CANCELADA não pode regressar a ATIVA.
- Alojamento em estado PENDENTE_LIMPEZA não pode receber nova Estadia sem transitar para DISPONIVEL (Fase 1: LimpezaController marca como limpo).

---

## Technical References — Etapa 2

### Architecture & Components

- [Especificação de Arquitetura — Componentes](../../docs/Etapa2/01-architecture/architecture.md#3-componentes-e-responsabilidades):
  - `ReservaController`, `EstadiaController`, `PagamentoController` e suas responsabilidades
  - `ReservaService`, `EstadiaService`, `PagamentoService` com regras de domínio
  - `ReservaRepository`, `EstadiaRepository`, `PagamentoRepository`

- [Diagrama de Componentes](../../docs/Etapa2/01-architecture/components.mmd): Interação entre Controllers, Services, Repositories para Reservas e Estadias

- [Diagrama de Implantação](../../docs/Etapa2/01-architecture/deployment.mmd): Ambiente de execução (Spring Boot, MySQL, navegador)

### Class Design

- [Class Diagram](../../docs/Etapa2/02-class-diagram/class-diagram.md): Definição de atributos e relações entre `Reserva`, `Estadia`, `Pagamento`, `Alojamento`
- [Mermaid Class Diagram](../../docs/Etapa2/02-class-diagram/class-diagram.mmd): Representação gráfica das entidades

### Sequence Diagrams

- [UC-04: Criar Reserva](../../docs/Etapa2/03-seq-diagrams/UC-04.mmd): Fluxo de sistema (pesquisa → validação → criação)
- [UC-05: Cancelar Reserva](../../docs/Etapa2/03-seq-diagrams/UC-05.mmd): Fluxo de cancelamento
- [UC-06: Registar Check-in](../../docs/Etapa2/03-seq-diagrams/UC-06.mmd): Fluxo de check-in com pagamento
- [UC-07: Registar Check-out](../../docs/Etapa2/03-seq-diagrams/UC-07.mmd): Fluxo de check-out
- [UC-08: Processar Faturacao e Pagamento](../../docs/Etapa2/03-seq-diagrams/UC-08.mmd): Subcase incluído em UC-06 e UC-07

### Architecture Decisions

- [ADR-01: Monolito em Camadas](../../docs/Etapa2/04-architecture-decisions/ADR-01-monolito-camadas.md): Justificação de design monolítico
- [ADR-02: Spring MVC + Thymeleaf para SSR](../../docs/Etapa2/04-architecture-decisions/ADR-02-spring-mvc-thymeleaf-ssr.md): Templates renderizadas no servidor
- [ADR-03: MySQL como BD Principal](../../docs/Etapa2/04-architecture-decisions/ADR-03-mysql-base-dados-principal.md): Persistência relacional
- [ADR-04: Spring Security com Sessão HTTP](../../docs/Etapa2/04-architecture-decisions/ADR-04-spring-security-sessao-http.md): Autenticação por sessão
- [ADR-05: DTO entre Controller e Service](../../docs/Etapa2/04-architecture-decisions/ADR-05-dto-entre-controller-service.md): Padrão de transferência de dados

### UI Mockups

- [WF03: Reservas](../../docs/Etapa2/05-ui-interface-mockup/wf03-reservas.html): Interface para criar, listar e cancelar reservas
- [WF04: Plano de Cuidados (contexto)](../../docs/Etapa2/05-ui-interface-mockup/wf04-plano-cuidados.html): Visualização de estadia e cuidados (não foco aqui, mas relacionado)

---

## Validação de Rastreabilidade

| Origem | Mapeamento | Status |
|--------|-----------|--------|
| UC-04 | US-1 (Criar Reserva) | ✓ Coberto |
| UC-05 | US-2 (Cancelar Reserva) | ✓ Coberto |
| UC-06 | US-3 (Check-in + Pagamento base) | ✓ Coberto |
| UC-07 | US-4 (Check-out + Pagamento extras) | ✓ Coberto |
| UC-08 | US-3, US-4 (Processar Faturação) | ✓ Coberto (subcase) |
| RD-01 | FR-06, FR-08 (Disponibilidade) | ✓ Coberto |
| RD-02 | FR-08 (Check-in, estadias EM_CURSO) | ✓ Coberto |
| RD-03 | FR-09 (Check-out, PENDENTE_LIMPEZA) | ✓ Coberto |
| RD-04 | FR-10 (Pagamentos CHECK_IN/CHECK_OUT) | ✓ Coberto |
| RD-06 | FR-07 (Cancelar reserva, liberar alojamento) | ✓ Coberto |

---

## Notes

- Esta spec assume que a Fase 1 (Fundação com autenticação) e Fase 2 (Registo de Tutores, Animais, Disponibilidade) estão completas e estáveis.
- Serviços adicionais (banho, passeio, medicação) são registados em Fase 4, mas o pagamento dos mesmos é processado nesta fase (no check-out).
- Auditoria de cancelamentos e mudanças de estado de reservas é um requisito implícito (RD-06, RD-07) mas será documentado em detalhe no plano de implementação.
