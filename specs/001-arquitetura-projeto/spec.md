# Feature Specification: arquitetura-projeto

**Feature Branch**: `[001-arquitetura-projeto]`  
**Created**: 2026-04-21  
**Updated**: 2026-04-22  
**Status**: Draft  

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Arquitetura de Classes Modular e Implementavel (Priority: P1)

Como equipa de desenvolvimento, queremos um modelo de classes completo e coerente para implementar o sistema sem ambiguidades de responsabilidade.

**Why this priority**: Sem fronteiras claras entre dominio, services e repositories, o risco de retrabalho e defeitos arquiteturais aumenta antes da implementacao.

**Independent Test**: Validar que o diagrama de classes define entidades, services e repositories com responsabilidades nao sobrepostas, metodos minimos e cardinalidades corretas.

**Acceptance Scenarios**:

1. **Given** o diagrama de classes atualizado, **When** a equipa valida responsabilidades por modulo, **Then** cada classe pertence a um package claro e sem contradicoes funcionais.
2. **Given** a separacao entre entidades e services, **When** se revem as regras de negocio, **Then** regras dependentes apenas do estado da entidade ficam no dominio e orquestracao de caso de uso fica nos services.

---

### User Story 2 - Decisoes Arquiteturais Rastreaveis (Priority: P2)

Como equipa de projeto, queremos que todas as decisoes arquiteturais relevantes fiquem explicadas e fundamentadas em Sommerville na pasta de decisoes.

**Why this priority**: Esta rastreabilidade e obrigatoria para consistencia tecnica, auditoria academica e onboarding da equipa.

**Independent Test**: Validar que cada decisao critica (ex.: services vs facades, consolidacao de services, padroes de design) possui registo ADR com contexto, alternativas, decisao e consequencias.

**Acceptance Scenarios**:

1. **Given** uma decisao arquitetural critica, **When** a decisao e aprovada, **Then** existe um ADR em `docs/architecture/decisoes/` com fundamentacao em Sommerville.
2. **Given** um novo elemento da equipa, **When** consulta a pasta de decisoes, **Then** compreende o racional tecnico sem depender de contexto oral.

---

### User Story 3 - Consistencia de Enumeracoes e Regras de Dominio (Priority: P3)

Como analista funcional, quero enumeracoes e invariantes de negocio corretamente ligadas as classes certas para evitar inconsistencias no modelo e nos fluxos.

**Why this priority**: A consistencia do dominio reduz erros de implementacao, regressao e ambiguidades na validacao de requisitos.

**Independent Test**: Validar que cada enumeracao e usada por classes de dominio corretas e que as regras RD-01 a RD-09 estao anotadas no diagrama com notas/restricoes.

**Acceptance Scenarios**:

1. **Given** o diagrama de classes atualizado, **When** se valida o uso de enumeracoes, **Then** `EstadoSaude`, `EstadoReserva`, `MetodoPagamento`, `TipoCuidado` e `TipoServicoExtra` estao ligados as classes adequadas.
2. **Given** as regras de dominio criticas, **When** se revem as notas de restricao, **Then** todas as regras RD-01 a RD-09 estao representadas de forma rastreavel.

### Edge Cases

- Tentativa de iniciar estadia com reserva nao confirmada.
- Tentativa de encerrar estadia com pagamentos pendentes.
- Mudanca de estado de alojamento para pendente sem notificar limpeza.
- Sobreposicao semantica entre intervencao clinica e servico extra faturavel.
- Reserva criada mas ainda nao confirmada (estado `Pendente`).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: O sistema de arquitetura MUST manter decomposicao principal em services (nao em subsistemas distribuidos) para o contexto atual de escala do projeto.
- **FR-002**: Cada service MUST ser definido por interface de contrato e implementacao concreta separada.
- **FR-003**: O modelo MUST usar facades apenas para fronteiras externas ou simplificacao de composicao para clientes externos, evitando facade interna sem necessidade.
- **FR-004**: O modelo MUST consolidar services com baixa independencia funcional, mantendo separacao onde existem razoes de mudanca distintas.
- **FR-005**: O modelo MUST incluir explicitamente os 9 services alvo: `UtilizadorService`, `AnimalService`, `ClinicaService`, `AlojamentoService`, `ReservaService`, `EstadiaService`, `FaturacaoService`, `PagamentoService` e `RelatorioService`.
- **FR-006**: O modelo MUST declarar metodos relevantes nos repositories (nao interfaces vazias), incluindo consultas por periodo, estado e relacoes de negocio.
- **FR-007**: O `FaturacaoService` MUST focar-se no ciclo de vida da fatura, enquanto o `PagamentoService` MUST gerir registo e validacao de pagamentos.
- **FR-008**: O modelo MUST incluir `FaturaFactory` para construcao de faturas a partir de estadias e custos agregados.
- **FR-009**: O modelo MUST incluir uma estrategia de calculo de faturacao (`EstrategiaCalculoFatura`) para evolucao de regras sem alterar contratos principais.
- **FR-010**: O modelo MUST representar notificacao de mudanca de estado de alojamento para limpeza atraves de mecanismo observavel (padrao observer).
- **FR-011**: O modelo MUST remover regras de autorizacao de entidades de dominio e atribui-las ao modulo de seguranca/acesso.
- **FR-012**: O modelo MUST adicionar associacao direta `Estadia -> Tutor` para rastreabilidade operacional.
- **FR-013**: O modelo MUST adicionar associacao direta `Fatura -> Tutor` para consultas e relatorios financeiros por cliente.
- **FR-014**: O modelo MUST adicionar referencia `Reserva.criadaPor : Colaborador` para auditoria.
- **FR-015**: O modelo MUST corrigir uso de disponibilidade para evitar dependencias implicitas de repositorio dentro de entidades.
- **FR-016**: O modelo MUST tipar `Animal.estadoSaude` como `EstadoSaude` (enum) e nao como string livre.
- **FR-017**: O modelo MUST ligar `ItemPlanoCuidado.tipo` ao enum `TipoCuidado`.
- **FR-018**: O enum `EstadoReserva` MUST incluir `Pendente`, `Confirmada` e `Cancelada`.
- **FR-019**: O enum `MetodoPagamento` MUST incluir opcao de referencia multibanco (`MBReference` ou equivalente acordado).
- **FR-020**: O modelo MUST clarificar a associacao opcional entre `IntervencaoClinica` e `ServicoExtra` com nota de restricao.
- **FR-021**: O modelo MUST definir excecoes de dominio nos contratos dos services para falhas de negocio relevantes.
- **FR-022**: O diagrama de classes MUST estar separado por packages modulares explicitos (`dominio.*`, `servico`, `repositorio`, `fabrica`, `estrategia`).
- **FR-023**: O projeto MUST registar e explicar todas as decisoes arquiteturais relevantes em `docs/architecture/decisoes/`, cada uma com fundamentacao explicita em Ian Sommerville.
- **FR-024**: O modelo MUST anotar as invariantes RD-01 a RD-09 nas classes ou relacoes mais relevantes do diagrama.
- **FR-025**: O projeto MUST produzir diagramas de classes, sequencia e componentes em PlantUML ou Mermaid.
- **FR-026**: Todos os diagramas MUST seguir UML 2.5 estrita (notacao, simbolos, relacoes e agrupamentos), sem estilos fora do standard.
- **FR-027**: O projeto MUST produzir diagramas de sequencia para os fluxos: reserva, check-in, check-out, cuidados, faturacao, limpeza e intervencao veterinaria.
- **FR-028**: Para cada fluxo de sequencia obrigatorio, o projeto MUST manter ficheiro `.mmd` e ficheiro `.txt` PlantUML importavel no Visual Paradigm.
- **FR-029**: Os diagramas de sequencia MUST modelar apenas logica de negocio (sem HTTP, sem controllers, sem endpoints).
- **FR-030**: Os ficheiros de arquitetura MUST ser exportaveis para formatos standard de partilha (imagem/PDF) sem perda semantica.
- **FR-031**: Cada ficheiro em `docs/architecture/` MUST conter cabecalho com nome, tipo, data e descricao breve.
- **FR-032**: O repositorio MUST manter estrutura centralizada de documentacao em `docs/architecture/`, incluindo os ficheiros obrigatorios de classes, componentes e sequencia.
- **FR-033**: O diagrama de classes MUST explicitar as regras de negocio criticas: disponibilidade de alojamento, compatibilidade especie-tipologia, reserva com no maximo uma estadia e estadia com exatamente uma fatura.
- **FR-034**: O projeto SHOULD incluir outros diagramas relevantes (ex.: casos de uso, dominio) quando contribuirem para clarificar arquitetura.

### Contrato Explicito para Geracao do Diagrama de Classes


#### 1. Services obrigatorios como interface + implementacao

- `UtilizadorService` + `UtilizadorServiceImpl`
- `AnimalService` + `AnimalServiceImpl`
- `ClinicaService` + `ClinicaServiceImpl`
- `AlojamentoService` + `AlojamentoServiceImpl`
- `ReservaService` + `ReservaServiceImpl`
- `EstadiaService` + `EstadiaServiceImpl`
- `FaturacaoService` + `FaturacaoServiceImpl`
- `PagamentoService` + `PagamentoServiceImpl`
- `RelatorioService` + `RelatorioServiceImpl`

#### 2. Repositories obrigatorios como interface + implementacao

- `AlojamentoRepository` + `AlojamentoRepositoryImpl`
- `ReservaRepository` + `ReservaRepositoryImpl`
- `EstadiaRepository` + `EstadiaRepositoryImpl`
- `FaturaRepository` + `FaturaRepositoryImpl`
- `PagamentoRepository` + `PagamentoRepositoryImpl`
- `AnimalRepository` + `AnimalRepositoryImpl`
- `HistorialClinicoRepository` + `HistorialClinicoRepositoryImpl`

#### 3. Metodos minimos obrigatorios de repository

- `AlojamentoRepository.findDisponiveis(inicio, fim, especie)`
- `AlojamentoRepository.findByEstadoLimpeza(estado)`
- `ReservaRepository.findByAlojamentoAndPeriodo(alojamento, inicio, fim)`
- `ReservaRepository.findByAnimal(animal)`
- `ReservaRepository.findByEstado(estado)`
- `EstadiaRepository.findEmCurso()`
- `EstadiaRepository.findByAnimal(animal)`
- `EstadiaRepository.findByPeriodo(inicio, fim)`
- `FaturaRepository.findPendentes()`
- `FaturaRepository.findByPeriodo(inicio, fim)`
- `PagamentoRepository.findByEstado(estado)`
- `PagamentoRepository.findByFatura(fatura)`

#### 4. Consolidacoes obrigatorias de service

- `EstadiaService` absorve operacoes de cuidados, notas, servicos extra e plano de cuidado.
- `AlojamentoService` absorve operacoes de limpeza.

#### 5. Separacoes obrigatorias de service

- `FaturacaoService` e `PagamentoService` permanecem separados.
- `AnimalService` e `ClinicaService` permanecem separados.
- `ReservaService` e `EstadiaService` permanecem separados.

#### 6. Patterns obrigatorios no diagrama

- `FaturaFactory` em `fabrica`
- `EstrategiaCalculoFatura` + `CalculoStandard` em `estrategia`
- `AlojamentoStateListener` para observer de mudanca de estado de alojamento

#### 7. Enumeracoes obrigatorias

- `EstadoSaude = {Estavel, EmObservacao, Critico}`
- `EstadoReserva = {Pendente, Confirmada, Cancelada}`
- `MetodoPagamento = {Numerario, Cartao, MBWay, Transferencia, MBReference}`
- `TipoCuidado` associado a `Cuidado` e `ItemPlanoCuidado`
- `TipoServicoExtra` com nota de restricao para `MedicacaoPrescrita`

#### 8. Relacoes e cardinalidades obrigatorias

- `Reserva (0..1) <-> (1) Estadia`
- `Estadia (*) -> (1) Tutor` (associacao direta)
- `Fatura (*) -> (1) Tutor` (associacao direta)
- `Reserva (*) -> (1) Colaborador` via `criadaPor`
- `IntervencaoClinica (0..1) <-> (0..1) ServicoExtra` com nota semantica obrigatoria

#### 9. Excecoes de dominio obrigatorias (contrato de service)

- `ReservaNaoConfirmadaException`
- `AlojamentoIndisponivelException`
- `PagamentoPendenteException`
- `AnimalEmEstadiaException`

#### 10. Regras de desenho UML

- Mostrar visibilidade (`+`, `-`, `#`) em atributos e metodos.
- Mostrar estereotipos (`<<interface>>`, `<<service>>`, `<<repository>>`, `<<factory>>`).
- Incluir notas para RD-01..RD-09 junto das classes/relacoes relevantes.
- Proibir elementos HTTP/controller no diagrama de classes de negocio.

### Contrato Explicito para Geracao de Diagramas de Sequencia

#### 1. Fluxos obrigatorios

- `seq-reserva`
- `seq-checkin`
- `seq-checkout`
- `seq-cuidados`
- `seq-faturacao`
- `seq-limpeza`
- `seq-veterinario`

#### 2. Formato obrigatorio por fluxo

- 1 ficheiro Mermaid: `*.mmd`
- 1 ficheiro PlantUML importavel: `*.txt`

#### 3. Regras de modelacao

- Participantes devem representar objetos de dominio e services/repositorios relevantes.
- Proibido modelar HTTP, endpoint, controller ou payload de API.
- Mensagens devem refletir ordem de negocio do caso de uso.

### Contrato Explicito para Geracao do Diagrama de Componentes

#### 1. Componentes logicos minimos

- Reservas
- Estadias
- Cuidados
- Faturacao
- Limpeza
- Gestao de Animais
- Gestao de Utilizadores

#### 2. Camadas obrigatorias

- Frontend
- Backend/API
- Base de Dados

#### 3. Notacao UML obrigatoria

- Uso de estereotipos UML standard (`<<component>>`, `<<interface>>`)
- Dependencias e interfaces expostas claramente indicadas

### Estrutura de Ficheiros Esperada

```text
docs/
└── architecture/
	├── class-domain.mmd
	├── component.mmd
	├── seq-reserva.mmd
	├── seq-reserva.txt
	├── seq-checkin.mmd
	├── seq-checkin.txt
	├── seq-checkout.mmd
	├── seq-checkout.txt
	├── seq-cuidados.mmd
	├── seq-cuidados.txt
	├── seq-faturacao.mmd
	├── seq-faturacao.txt
	├── seq-limpeza.mmd
	├── seq-limpeza.txt
	├── seq-veterinario.mmd
	├── seq-veterinario.txt
	└── decisoes/
```

### Requisitos de Qualidade dos Diagramas

1. Notacao UML 2.5 estrita, sem cores/simbolos/estilos fora do standard.
2. Ficheiros `.txt` de sequencia importam no Visual Paradigm com 0 erros.
3. Diagramas devem ser claros, simples e compreensiveis pela equipa.
4. Regras de negocio criticas devem estar explicitamente visiveis.

### Key Entities

- **ServiceContract**: Interface de servico que define operacoes de caso de uso e excecoes de negocio.
- **RepositoryContract**: Interface de acesso a dados com metodos de consulta orientados ao dominio.
- **DomainEntity**: Classe de dominio com estado, invariantes locais e comportamento sem dependencias externas implicitas.
- **DecisionRecord (ADR)**: Documento de decisao com contexto, alternativas, decisao, consequencias e fundamentacao em Sommerville.
- **FaturaFactory**: Componente de construcao de `Fatura` com agregacao de custos.
- **EstrategiaCalculoFatura**: Contrato para variacao de algoritmo de calculo financeiro.
- **EnumeracaoDominio**: Tipos fechados associados ao dominio (`EstadoSaude`, `EstadoReserva`, `MetodoPagamento`, etc.).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% dos repositories definidos no diagrama possuem pelo menos 2 metodos de consulta relevantes documentados.
- **SC-002**: 100% dos services definidos no diagrama possuem interface de contrato separada da implementacao.
- **SC-003**: 100% das decisoes arquiteturais criticas listadas no research possuem ADR na pasta `docs/architecture/decisoes/` com fundamentacao em Sommerville.
- **SC-004**: 100% das enumeracoes de dominio estao associadas a pelo menos uma classe correta e sem conflito semantico identificado em revisao.
- **SC-005**: 100% das regras RD-01 a RD-09 estao anotadas no diagrama de classes com notas de restricao rastreaveis.
- **SC-006**: Ficheiros PlantUML `.txt` dos fluxos obrigatorios importam no Visual Paradigm com 0 erros de sintaxe.

## Assumptions

- O sistema permanece no escopo de hotel para caes e gatos na release atual.
- O pagamento e registado no sistema, mas o processamento financeiro externo pode surgir numa fase futura.
- A equipa pretende arquitetura simples, modular e preparada para evolucao incremental.
- A documentacao de arquitetura e a principal fonte de alinhamento antes da implementacao.

## Dependencies

- Mermaid e PlantUML para modelacao e exportacao.
- Visual Paradigm para validacao de importacao PlantUML.
- Pasta versionada de decisoes em `docs/architecture/decisoes/`.

## Out of Scope

- Implementacao de codigo-fonte de producao.
- Definicao de endpoints REST e payloads de API.
- Esquema SQL detalhado e tuning de base de dados.

## Clarifications

### Session 2026-04-21
- Q: Qual o nivel de detalhe esperado para classes e componentes? -> A: Detalhe completo com classes, metodos, atributos e relacoes.

### Session 2026-04-22
- Q: Services ou facades genericas para este contexto? -> A: Services coesos como principal abstracao, com facades apenas em fronteiras externas.
- Q: Onde registar e fundamentar escolhas arquiteturais? -> A: Em ADRs na pasta `docs/architecture/decisoes/` com referencia explicita a Sommerville.