# Feature Specification: Reservas, Estadias e Pagamentos

**Feature Branch**: `003-reservas-estadias-pagamentos`  
**Created**: 6 de Maio de 2026  
**Status**: Draft  
**Input**: Fase 3 do plano de implementação gradual — ciclo operacional principal do hotel de animais

**Mapped User Stories**: US-06, US-07, US-10, US-11, US-12, US-05, US-02  
**Mapped Functional Requirements**: RF-01, RF-05, RF-06, RF-07, RF-08, RF-09, RF-10  
**Mapped Domain Requirements**: RD-01, RD-02, RD-03, RD-04, RD-06, RD-07, RD-09, RD-12  
**Mapped Non-Functional Requirements**: RNF-01, RNF-03, RNF-04, RNF-05

**Origem documentária**:
- [Plano de Implementação Gradual — Fase 3](../../docs/Etapa3/plano-implementacao-gradual.md#fase-3--reservas-estadias-e-pagamentos)
- [UC-04: Criar Reserva](../../docs/Etapa1/03-use-cases/UC-04.md)
- [UC-05: Cancelar Reserva](../../docs/Etapa1/03-use-cases/UC-05.md)
- [UC-06: Registar Check-in](../../docs/Etapa1/03-use-cases/UC-06.md)
- [UC-07: Registar Check-out](../../docs/Etapa1/03-use-cases/UC-07.md)
- [UC-08: Processar Faturacao e Pagamento](../../docs/Etapa1/03-use-cases/UC-08.md)

---

## Clarifications

### Session 2026-05-08

- Q: Qual a fonte de verdade para User Stories e Requisitos nesta feature? → A: User Stories em `docs/Etapa1/01-user-stories/user-stories.md` e requisitos em `docs/Etapa1/02-requirements/`.
- Q: Que nível de detalhe de referências arquiteturais deve constar na spec? → A: Referenciar explicitamente diagramas de sequência, mockups de UI e ADRs relevantes da Etapa 2.
- Q: Como tratar a prioridade de RD-06 nesta fase? → A: Embora RD-06 esteja definido como Should Have na Etapa 1, nesta feature é tratado como obrigatório operacional para garantir integridade do ciclo de reservas (sem reativação de reservas canceladas).

### Session 2026-05-26

- Q: Qual é a origem da tarifa usada no pagamento base? → A: A tarifa diária é configurada pelo diretor no catálogo `TipoAlojamentoTarifa`, identificado por `tipoAlojamento` textual e ativo/inativo, e não por enum Java fixo.
- Q: O que entra na cobrança de check-out? → A: O check-out cobra apenas valores complementares: serviços extra, intervenções clínicas faturáveis e eventuais dias reais que ultrapassem o período reservado já pago no check-in.
- Q: Como são geridos tipos de serviços extra? → A: Os tipos de serviços extra são catálogo de base de dados (`TipoServicoExtra`), gerido pela direção e referenciado por cada `ServicoExtra`.
- Q: O check-in/check-out pode omitir método de pagamento? → A: Não. As interfaces de check-in e check-out devem exigir método real (`NUMERARIO`, `CARTAO_DEBITO` ou `CARTAO_CREDITO`) para registar pagamento liquidado.

---

## User Scenarios & Testing

### US-06 - Criar, confirmar e gerir reservas com controlo automático de disponibilidade (Priority: P1)

Como funcionário de receção, quero criar, confirmar e gerir reservas com controlo automático de disponibilidade, para evitar situações de overbooking.

**Why this priority**: É o fluxo base que habilita estadias e faturação posterior. Sem gestão de reservas não existe operação diária da receção.

**Independent Test**: Criar reserva válida para um animal/tutor, confirmar a reserva e, em seguida, cancelar a reserva. O sistema deve refletir indisponibilidade e libertação do alojamento no mesmo período.

**Acceptance Scenarios**:

1. **Given** tutor e animal já registados e uma box disponível no período, **When** o funcionário cria a reserva, **Then** a reserva fica ativa e a box fica indisponível para reservas sobrepostas.
2. **Given** uma reserva recém-criada, **When** o funcionário confirma a reserva, **Then** o sistema regista o evento de confirmação e mantém o alojamento indisponível no período reservado.
3. **Given** uma reserva ativa/confirmada, **When** o funcionário executa cancelamento, **Then** a reserva muda para CANCELADA e não pode ser reativada (deve ser criada nova reserva).

---

### US-12 - Consultar disponibilidade das boxes em tempo real (Priority: P1)

Como funcionário de receção, quero consultar a disponibilidade das boxes em tempo real, para responder de imediato a pedidos de reserva.

**Why this priority**: Reduz erros operacionais e garante resposta imediata no balcão de atendimento.

**Independent Test**: Consultar disponibilidade para um intervalo de datas e validar que o sistema exclui boxes com reserva confirmada, estadia ativa ou limpeza não concluída.

**Acceptance Scenarios**:

1. **Given** período pretendido definido, **When** o funcionário consulta disponibilidade, **Then** o sistema devolve apenas boxes que cumprem as 3 condições de disponibilidade (RF-06, RD-01).
2. **Given** inexistência de boxes elegíveis, **When** a consulta é submetida, **Then** o sistema indica indisponibilidade e sugere alternativas de datas.

---

### US-07 - Registar check-in e check-out de cada animal (Priority: P1)

Como funcionário de receção, quero registar o check-in e check-out de cada animal, para garantir o controlo das estadias em curso.

**Why this priority**: Define o ciclo de vida da estadia e alimenta estados operacionais de box e faturação.

**Independent Test**: Para uma reserva confirmada, registar check-in e posteriormente check-out, validando sequência obrigatória e transições de estado.

**Acceptance Scenarios**:

1. **Given** reserva confirmada, **When** o check-in é registado, **Then** a estadia entra em EM_CURSO e a box passa a OCUPADO.
2. **Given** estadia EM_CURSO, **When** o check-out é registado, **Then** a estadia termina e a box passa a PENDENTE_LIMPEZA.
3. **Given** ausência de check-in anterior, **When** é tentado check-out, **Then** a operação é recusada (RD-03).

---

### US-10 - Pagamento da estadia no check-in (Priority: P1)

Como funcionário de receção, quero registar o pagamento da estadia no momento do check-in, com base na duração reservada e no tipo de alojamento, para que o valor base fique liquidado à entrada.

**Why this priority**: Garante liquidação do valor base no momento operacional de entrada e reduz risco financeiro.

**Independent Test**: No check-in, o sistema calcula valor da estadia e regista pagamento com método e estado obrigatórios.

**Acceptance Scenarios**:

1. **Given** check-in em execução, **When** o sistema calcula valor base (dias reservados x tarifa ativa do tipo de alojamento), **Then** apresenta montante a liquidar.
2. **Given** escolha de método de pagamento, **When** a operação é confirmada, **Then** é criado registo de pagamento com valor, método e estado.

---

### US-11 - Cobrança de extras no check-out (Priority: P1)

Como funcionário de receção, quero registar o check-out de um animal e cobrar nesse momento apenas os serviços extra e intervenções veterinárias acumulados durante a estadia, para que a faturação complementar fique liquidada à saída.

**Why this priority**: Separa claramente faturação base e complementar, conforme regras de domínio.

**Independent Test**: No check-out, o sistema agrega extras e intervenções, mostra total complementar e regista pagamento final.

**Acceptance Scenarios**:

1. **Given** estadia com extras/intervenções, **When** o check-out é iniciado, **Then** o sistema apresenta discriminação de custos complementares.
2. **Given** estadia terminada após a data reservada, **When** o check-out é calculado, **Then** o sistema adiciona à cobrança complementar apenas os dias adicionais não pagos no check-in.
3. **Given** confirmação de pagamento no check-out, **When** a operação termina, **Then** o sistema regista pagamento complementar e encerra a estadia.

---

### US-05 - Histórico completo de estadias e pagamentos (Priority: P2)

Como diretor, quero consultar o histórico completo de estadias e pagamentos, para ter uma visão financeira e operacional do hotel.

**Why this priority**: Suporta auditoria e análise operacional, mas não bloqueia o fluxo transacional base.

**Independent Test**: Diretor consulta histórico por cliente, animal, estado ou período e visualiza estadias e pagamentos associados de forma paginada.

**Acceptance Scenarios**:

1. **Given** histórico existente, **When** o diretor filtra por período, **Then** obtém lista de estadias e respetivos pagamentos.
2. **Given** transações com estados diferentes, **When** o histórico é apresentado, **Then** o estado de cada pagamento fica identificado (LIQUIDADO/PENDENTE).
3. **Given** muitos registos no histórico, **When** o diretor navega entre páginas, **Then** o sistema mantém os filtros ativos e apresenta apenas o subconjunto correspondente.

---

### US-02 - Indicadores de faturação e pagamentos pendentes (Priority: P2)

Como diretor, quero consultar indicadores de faturação e pagamentos pendentes filtráveis por período, para acompanhar o desempenho financeiro do hotel.

**Why this priority**: Necessário para controlo financeiro diário/mensal e reconciliação de pendências.

**Independent Test**: Diretor abre dashboard, aplica filtro temporal e visualiza faturação agregada e lista de pagamentos pendentes.

**Acceptance Scenarios**:

1. **Given** pagamentos registados no sistema, **When** o diretor aplica filtro por período, **Then** a faturação agregada é atualizada.
2. **Given** existência de dívidas pendentes, **When** o diretor consulta painel financeiro, **Then** o sistema apresenta lista de pendentes para reconciliação.

---

## Requirements

### Functional Requirements

- **RF-01 - Dashboard operacional**: O sistema deve disponibilizar um dashboard acessível ao perfil de diretor contendo, no mínimo, taxa de ocupação atual, número de estadias ativas, número de reservas futuras e valor total de faturação diária/mensal, com atualização automática (evento relevante ou máximo de 60 segundos). A implementação deve manter `IDashboardService`/`DashboardService`, mas esse serviço deve apenas orquestrar métricas expostas pelas interfaces dos serviços de domínio, sem aceder diretamente aos repositórios.
- **RF-05 - Histórico de estadias e pagamentos**: O sistema deve manter um histórico completo das estadias e pagamentos de cada animal, consultável pela receção e pela direção, com paginação e filtragem por cliente, animal, estado e intervalo temporal.
- **RF-06 - Controlo de disponibilidade de boxes**: O sistema deve determinar disponibilidade em tempo real por três condições cumulativas: sem reserva confirmada no período, sem estadia ativa no período e limpeza concluída; deve impedir reservas inválidas e sugerir alternativas.
- **RF-07 - Gestão de reservas**: O sistema deve permitir criação, confirmação e cancelamento de reservas, com registo de período, box e animal associados.
- **RF-08 - Check-in e pagamento de estadia**: O sistema deve suportar check-in, registar data de entrada e box atribuída, e processar pagamento da estadia no mesmo momento, usando a tarifa diária ativa do tipo de alojamento.
- **RF-09 - Check-out e faturação complementar**: O sistema deve suportar check-out, registar data de saída e calcular/processar pagamento de serviços extra, intervenções veterinárias acumuladas e dias adicionais face ao período reservado.
- **RF-10 - Registo de pagamentos**: O sistema deve registar pagamentos de check-in e check-out com valor, método (numerário, cartão de débito, cartão de crédito) e estado (liquidado ou pendente), garantindo rastreabilidade.
- **RF-18 - Gestão de tipos e tarifas**: O sistema deve permitir à direção gerir tipos de alojamento com tarifa diária e tipos de serviços extra, incluindo criação, edição, ativação e desativação.

### Domain Requirements

- **RD-01 - Disponibilidade de alojamento**: Um alojamento só é considerado disponível se não existir reserva ou estadia ativa no período e se a limpeza estiver marcada como concluída.
- **RD-02 - Check-in condicionado a reserva**: Um animal só pode realizar check-in com reserva confirmada associada.
- **RD-03 - Sequência de check-in/check-out**: O check-out só pode ocorrer após check-in registado para a mesma estadia.
- **RD-04 - Pagamento no check-in e check-out**: O check-in cobre exclusivamente a estadia base estimada pelo período reservado e tarifa ativa do tipo de alojamento; extras, intervenções veterinárias e dias adicionais são cobrados no check-out.
- **RD-06 - Cancelamento de reservas**: Uma reserva cancelada não pode ser reativada; deve ser criada nova reserva.
- **RD-07 - Exclusividade de estadia**: Um animal não pode ter duas estadias em curso em simultâneo.
- **RD-09 - Registo e imutabilidade de custos extra**: O custo de extra/intervenção deve ser registado na ocorrência e não pode ser alterado após check-out.

### Non-Functional Requirements

- **RNF-01 - Tempo de resposta**: Leituras abaixo de 2 segundos e escritas abaixo de 3 segundos, em condições normais (até 10 utilizadores simultâneos).
- **RNF-03 - Disponibilidade**: Sistema disponível durante todo o horário de funcionamento do hotel.
- **RNF-04 - Autenticação e permissões**: Acesso exige autenticação prévia e controlo de permissões por perfil.
- **RNF-05 - Confidencialidade dos dados**: Garantir confidencialidade de dados pessoais e clínicos em conformidade com RGPD.

### Validation & Test Requirements

- A conclusão desta feature exige testes automatizados sobre todas as funcionalidades P1 deste documento (US-06, US-12, US-07, US-10, US-11).
- Cada fluxo funcional deve ter, no mínimo, um teste de caminho feliz e um teste de regra de negócio/erro (ex.: indisponibilidade de box, check-out sem check-in, pagamento sem método).
- As regras de domínio críticas (RD-01, RD-02, RD-03, RD-04, RD-06, RD-07, RD-09) devem ter testes dedicados na camada de serviço.
- Deve existir pelo menos um teste de integração por caso de uso principal (UC-04, UC-05, UC-06, UC-07, UC-08), cobrindo persistência e transições de estado.
- Devem existir testes explícitos de autorização por perfil para operações críticas (reserva, check-in/out, pagamentos, dashboard e histórico), incluindo cenários de acesso negado.
- Devem existir testes de confidencialidade para garantir que dados pessoais e clínicos sensíveis não são expostos em respostas indevidas ou ecrãs sem permissão.
- Devem existir testes de desempenho para validar SC-001, SC-002 e SC-006 em condições normais de utilização.
- Devem existir testes de paginação e filtragem no histórico para garantir preservação dos filtros entre páginas e consistência dos resultados.
- Todas as ações críticas (criação, confirmação e cancelamento de reserva; check-in; check-out; registo de pagamento) devem gerar registos de auditoria verificáveis por testes automatizados.

### Enums de Pagamento

- `EstadoPagamento`: `LIQUIDADO`, `PENDENTE`
- `MetodoPagamento`: `NUMERARIO`, `CARTAO_DEBITO`, `CARTAO_CREDITO`
- `MomentoPagamento`: `CHECK_IN`, `CHECK_OUT`

### Key Entities

- **Reserva**: Ligação entre Tutor, Animal, Alojamento e período (dataInicio, dataFim). Estados: ATIVA, CANCELADA, CONCLUIDA. Uma reserva pode desencadear no máximo uma Estadia (RD-06).
- **Estadia**: Documento de hospedagem com referência a uma Reserva. Estados: EM_CURSO, TERMINADA. Inclui checkIn (data/hora) e checkOut (data/hora). Máximo uma Estadia por Animal em qualquer período (RD-07).
- **Pagamento**: Registo de transação com valor, `metodoPagamento` (`MetodoPagamento`), `momentoPagamento` (`MomentoPagamento`), `estadoPagamento` (`EstadoPagamento`) e referência a Estadia. Máximo dois pagamentos por Estadia (um CHECK_IN obrigatório, um CHECK_OUT complementar). O método de pagamento é sempre um método real, sem valor indefinido.
- **Alojamento**: Entidade existente (Fase 1), mas agora com ciclo de estado expandido: DISPONIVEL → OCUPADO → PENDENTE_LIMPEZA → CONCLUIDO → DISPONIVEL.
- **TipoAlojamentoTarifa**: Catálogo gerido pela direção com `tipoAlojamento`, `tarifaDiaria`, `ativo` e `dataCriacao`. Substitui enum fixo para permitir tipos configuráveis.
- **TipoServicoExtra**: Catálogo gerido pela direção com `nome`, `descricao`, `ativo` e `dataCriacao`. Apenas tipos ativos podem ser usados no registo de serviços extra.
- **ServicoExtra**: Registo de consumo durante a estadia, com referência a `TipoServicoExtra`, custo e data/hora.
- **IntervencaoClinica**: Registo clínico faturável durante a estadia, com custo considerado no check-out quando aplicável.

---

## Success Criteria

### Measurable Outcomes

- **SC-001**: Consulta de disponibilidade e pesquisa de histórico respondem em menos de 2 segundos em condições normais de utilização (RNF-01).
- **SC-002**: Operações de escrita (criar/cancelar reserva, check-in, check-out, registo de pagamento) concluem em menos de 3 segundos de processamento do sistema (RNF-01).
- **SC-003**: 100% das reservas respeitam as regras de não-overbooking definidas por RF-06 e RD-01.
- **SC-004**: 100% dos check-outs respeitam sequência obrigatória (RD-03), sem encerramentos sem check-in prévio.
- **SC-005**: 100% dos pagamentos registam valor, método e estado conforme RF-10.
- **SC-005A**: 100% dos cálculos de check-in usam tarifa ativa de `TipoAlojamentoTarifa`; ausência de tarifa ativa bloqueia o pagamento base.
- **SC-005B**: 100% dos check-outs agregam serviços extra, intervenções clínicas e dias adicionais sem voltar a cobrar o período já pago no check-in.
- **SC-006**: Diretor consegue consultar indicadores e pendentes por período com atualização máxima de 60 segundos (RF-01).
- **SC-007**: Histórico completo de estadias e pagamentos por animal, cliente ou período está disponível para receção/direção com paginação consistente (RF-05).
- **SC-008**: Existe pelo menos 1 teste automatizado por funcionalidade P1 desta feature (US-06, US-12, US-07, US-10, US-11).
- **SC-009**: Existe pelo menos 1 teste de integração por caso de uso principal (UC-04, UC-05, UC-06, UC-07, UC-08).
- **SC-010**: 100% das regras de domínio críticas listadas nesta spec têm testes automatizados com resultado verde no pipeline local.
- **SC-011**: 100% das operações críticas do ciclo de reservas/estadias/pagamentos produzem eventos de auditoria rastreáveis.
- **SC-012**: Testes de desempenho automatizados comprovam cumprimento de SC-001, SC-002 e SC-006 em ambiente de validação da feature.

---

## Documentação Técnica

- O código Java deve ser documentado com Javadoc em controllers, services, DTOs e exceptions públicas ou relevantes para fluxos de negócio.
- O Maven Javadoc Plugin deve gerar a documentação HTML do código.
- A implementação deve seguir `.specify/memory/constitution.md` e as convenções de estilo em [docs/Etapa3/convencoes.md](../../docs/Etapa3/convencoes.md).

---

## Assumptions

- Tutor e Animal já estão registados (Fase 2 completa).
- Tarifa base diária está definida no catálogo de tipos de alojamento e pode ser gerida pela direção.
- Serviços adicionais são registados em paralelo pela equipa de cuidados com base no catálogo de tipos de serviços extra; o pagamento é processado no check-out.
- Autenticação e autorização por perfil já estão funcionais (Fase 1: `FUNCIONARIO_RECEPCAO`, `DIRETOR`).
- Todos os pagamentos são em moeda única (não há conversão).
- Estadia não pode ter check-out registado sem check-in anterior.
- Uma vez TERMINADA, a Estadia não pode voltar a EM_CURSO.
- Reserva CANCELADA não pode regressar a ATIVA.
- Alojamento em estado PENDENTE_LIMPEZA não pode receber nova Estadia sem transitar para DISPONIVEL (Fase 1: LimpezaController marca como limpo).

---

## Technical References — Etapa 2

### Refinements

- [Refinements da implementação](./refinements.md): decisões de ajuste tomadas durante a implementação.

### Architecture & Components

- [Especificação de Arquitetura — Componentes](../../docs/Etapa2/01-architecture/architecture.md#3-componentes-e-responsabilidades):
  - `ReservaController`, `EstadiaController`, `PagamentoController` e suas responsabilidades
  - Interfaces `IReservaService`, `IEstadiaService`, `IPagamentoService` e implementações `ReservaService`, `EstadiaService`, `PagamentoService` com regras de domínio
  - `ReservaRepository`, `EstadiaRepository`, `PagamentoRepository`

- [Diagrama de Componentes](../../docs/Etapa2/01-architecture/components.mmd): Interação entre Controllers, Services, Repositories para Reservas e Estadias

- [Diagrama de Implantação](../../docs/Etapa2/01-architecture/deployment.mmd): Ambiente de execução (Spring Boot, MySQL, navegador)

### Class Design

- [Class Diagram](../../docs/Etapa2/02-class-diagram/class-diagram.md): Definição de atributos e relações entre `Reserva`, `Estadia`, `Pagamento`, `Alojamento`
- [Mermaid Class Diagram](../../docs/Etapa2/02-class-diagram/class-diagram.mmd): Representação gráfica das entidades

### Sequence Diagrams

- [UC-04: Criar Reserva](../../docs/Etapa2/03-seq-diagrams/UC-04.mmd): fluxo principal de disponibilidade, seleção e confirmação de reserva
- [UC-05: Cancelar Reserva](../../docs/Etapa2/03-seq-diagrams/UC-05.mmd): fluxo de cancelamento e libertação de box
- [UC-06: Registar Check-in](../../docs/Etapa2/03-seq-diagrams/UC-06.mmd): fluxo de check-in, criação/ativação da estadia e pagamento base
- [UC-07: Registar Check-out](../../docs/Etapa2/03-seq-diagrams/UC-07.mmd): fluxo de check-out, atualização de estado e transição para limpeza
- [UC-08: Processar Faturacao e Pagamento](../../docs/Etapa2/03-seq-diagrams/UC-08.mmd): composição dos pagamentos em check-in/check-out

### UI Mockups

- [WF02: Dashboard do Diretor](../../docs/Etapa2/05-ui-interface-mockup/wf02-dashboard-diretor.html): referência para indicadores de faturação e pagamentos pendentes (US-02)
- [WF03: Reservas](../../docs/Etapa2/05-ui-interface-mockup/wf03-reservas.html): referência para consulta de disponibilidade, criação/cancelamento de reservas e ações de check-in/check-out na receção
- [WF04: Plano de Cuidados](../../docs/Etapa2/05-ui-interface-mockup/wf04-plano-cuidados.html): contexto funcional para origem de serviços extra e integração no fecho de estadia

### Architecture Decisions

- [ADR-01: Monolito em Camadas](../../docs/Etapa2/04-architecture-decisions/ADR-01-monolito-camadas.md): Justificação de design monolítico
- [ADR-02: Spring MVC + Thymeleaf para SSR](../../docs/Etapa2/04-architecture-decisions/ADR-02-spring-mvc-thymeleaf-ssr.md): Templates renderizadas no servidor
- [ADR-03: Persistência em SGBD relacional](../../docs/Etapa2/04-architecture-decisions/ADR-03-persistencia-sgbd-relacional.md): Persistência relacional
- [ADR-04: MySQL e padrão repositório](../../docs/Etapa2/04-architecture-decisions/ADR-04-mysql-base-dados.md): Base de dados e acesso a dados
- [ADR-05: Controlo de acesso por perfil](../../docs/Etapa2/04-architecture-decisions/ADR-05-controlo-acesso-perfil.md): Autenticação e autorização
- [ADR-06: Isolamento da apresentação através de DTOs](../../docs/Etapa2/04-architecture-decisions/ADR-06-isolamento-apresentacao-dtos.md): Padrão de transferência de dados

---

## Validação de Rastreabilidade

| Origem | Mapeamento | Status |
|--------|-----------|--------|
| US-06 | UC-04, UC-05, RF-06, RF-07, RD-01, RD-06 | ✓ Coberto |
| US-12 | UC-04, RF-06, RD-01 | ✓ Coberto |
| US-07 | UC-06, UC-07, RF-08, RF-09, RD-02, RD-03, RD-07 | ✓ Coberto |
| US-10 | UC-06, UC-08, RF-08, RF-10, RD-04 | ✓ Coberto |
| US-11 | UC-07, UC-08, RF-09, RF-10, RD-03, RD-04, RD-09 | ✓ Coberto |
| US-05 | RF-05 | ✓ Coberto |
| US-02 | RF-01 | ✓ Coberto |
| Gestão de tarifas e catálogos | RF-18, RF-08, RF-09, RD-04 | ✓ Coberto |

---

## Notes

- Esta spec assume que a Fase 1 (Fundação com autenticação) e Fase 2 (Registo de Tutores, Animais, Disponibilidade) estão completas e estáveis.
- Serviços adicionais (banho, passeio, medicação) são registados em Fase 4, mas o pagamento dos mesmos é processado nesta fase (no check-out).
- A rastreabilidade normativa usa os identificadores oficiais de Etapa 1 (`US-xx`, `RF-xx`, `RD-xx`, `RNF-xx`) sem alias locais (`FR-xx`).
