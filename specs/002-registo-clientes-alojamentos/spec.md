# Feature Specification: Registo Base de Clientes e Alojamentos

**Feature Branch**: `002-registo-clientes-alojamentos`  
**Created**: 2026-05-05  
**Status**: Draft  
**Phase**: Fase 2 do Plano de Implementação Gradual  
**Mapped Use Cases**: UC-03 (Registar Tutor e Animal), UC-04 (Criar Reserva)  
**Mapped User Stories**: US-06, US-08, US-09, US-12  
**Mapped Domain Requirements**: RD-03, RD-05, RD-06  

---

## Referências de Apoio (Etapa 2)

Esta feature deve ser interpretada e implementada em coerência com os artefactos de Etapa 2, em particular:

- [architecture.md](../../docs/Etapa2/01-architecture/architecture.md) para a arquitetura MVC em camadas e responsabilidades dos componentes.
- [class-diagram.md](../../docs/Etapa2/02-class-diagram/class-diagram.md) para a estrutura de entidades, services e repositories.
- [UC-03.mmd](../../docs/Etapa2/03-seq-diagrams/UC-03.mmd) e [UC-04.mmd](../../docs/Etapa2/03-seq-diagrams/UC-04.mmd) para os fluxos de registo e criação de reserva.
- [ADR-01-monolito-camadas.md](../../docs/Etapa2/04-architecture-decisions/ADR-01-monolito-camadas.md), [ADR-02-spring-mvc-thymeleaf-ssr.md](../../docs/Etapa2/04-architecture-decisions/ADR-02-spring-mvc-thymeleaf-ssr.md), [ADR-03-persistencia-sgbd-relacional.md](../../docs/Etapa2/04-architecture-decisions/ADR-03-persistencia-sgbd-relacional.md), [ADR-04-mysql-base-dados.md](../../docs/Etapa2/04-architecture-decisions/ADR-04-mysql-base-dados.md), [ADR-05-controlo-acesso-perfil.md](../../docs/Etapa2/04-architecture-decisions/ADR-05-controlo-acesso-perfil.md) e [ADR-06-isolamento-apresentacao-dtos.md](../../docs/Etapa2/04-architecture-decisions/ADR-06-isolamento-apresentacao-dtos.md) para as decisões arquiteturais base.
- Os mockups de interface de Etapa 2 devem ser usados como referência visual para os ecrãs de disponibilidade e criação de reservas. Em particular:

- [wf03-reservas.html](../../docs/Etapa2/05-ui-interface-mockup/wf03-reservas.html) — ecrã principal de reservas e assistente de criação.
- [wf08-disponibilidade.html](../../docs/Etapa2/05-ui-interface-mockup/wf08-disponibilidade.html) — vista de disponibilidade com filtros por período.
- [wf05-historico-clinico.html](../../docs/Etapa2/05-ui-interface-mockup/wf05-historico-clinico.html) — referência para a apresentação de históricos por animal.

Ver também a pasta completa de mockups: `docs/Etapa2/05-ui-interface-mockup/` para consistência visual.

---

## User Scenarios & Testing *(mandatory)*

### US-09 - Registar e consultar dados de tutores e animais (Priority: P1)

Como funcionário de receção, quero registar e consultar os dados dos tutores e dos seus animais, para ter toda a informação disponível no momento do atendimento.

**Why this priority**: P1 — Sem dados de tutor/animal não é possível criar reservas nem garantir atendimento informado.

**Independent Test**: Registar tutor e animal com dados obrigatórios, e depois consultar ficha completa por NIF/nome.

**Acceptance Scenarios**:

1. **Given** funcionário autenticado, **When** regista tutor com nome, contacto, email e NIF, **Then** o sistema guarda o registo e impede duplicação de NIF.
2. **Given** tutor existente, **When** regista animal com espécie, raça, data de nascimento, peso, estado de saúde, necessidades alimentares e medicação, **Then** o sistema associa o animal ao tutor.
3. **Given** tutor registado, **When** é consultada a ficha do tutor, **Then** o sistema apresenta dados do tutor e lista de animais associados.

---

### US-12 - Consultar disponibilidade das boxes em tempo real (Priority: P1)

Como funcionário de receção, quero consultar a disponibilidade das boxes em tempo real, para responder de imediato a pedidos de reserva.

**Why this priority**: P1 — A disponibilidade é condição obrigatória para criação de reservas sem overbooking.

**Independent Test**: Selecionar período e obter apenas boxes elegíveis (sem conflito de reserva/estadia e com limpeza concluída).

**Acceptance Scenarios**:

1. **Given** período definido, **When** o funcionário consulta disponibilidade, **Then** o sistema devolve apenas boxes válidas para esse intervalo.
2. **Given** inexistência de boxes disponíveis, **When** a consulta é executada, **Then** o sistema informa indisponibilidade e sugere alternativas.

---

### US-06 - Criar e gerir reservas com controlo automático de disponibilidade (Priority: P1)

Como funcionário de receção, quero criar e gerir reservas com controlo automático de disponibilidade, para evitar situações de overbooking.

**Why this priority**: P1 — Reserva é a transação operacional principal da receção nesta fase.

**Independent Test**: Criar reserva válida e cancelar reserva ativa, validando atualização imediata da disponibilidade.

**Acceptance Scenarios**:

1. **Given** tutor/animal existentes e box disponível, **When** o funcionário confirma uma reserva, **Then** a reserva fica ativa e a box fica indisponível no período.
2. **Given** reserva ativa, **When** o funcionário cancela, **Then** o estado passa a CANCELADA e a reserva não pode ser reativada.

---

### US-08 - Consultar histórico de cada animal (Priority: P2)

Como funcionário de receção, quero consultar o histórico de cada animal, para prestar um serviço mais personalizado e informado.

**Why this priority**: P2 — Melhora qualidade de atendimento e contexto operacional, sem bloquear criação de reservas.

**Independent Test**: Pesquisar tutor/animal e aceder ao histórico do animal com reservas/estadias anteriores.

**Acceptance Scenarios**:

1. **Given** animal com histórico, **When** o funcionário abre o detalhe do animal, **Then** visualiza histórico de reservas e estadias.
2. **Given** pesquisa por nome/NIF do tutor, **When** o tutor é selecionado, **Then** o sistema permite navegar para os detalhes de cada animal associado.

**Nota (actualização 2026-05-06)**: A navegação foi clarificada para consistência das URLs. A listagem geral de animais está disponível em `/animais` e o detalhe de um animal é servido em `/animais/{id}`. Links a partir da ficha de tutor continuam a listar os animais do tutor, mas os detalhes do animal usam o endpoint raiz para manter rastreabilidade e permitir acesso direto.

---

## Requirements *(mandatory)*

### Functional Requirements

- **RF-04**: O sistema DEVE permitir o registo de tutores com dados obrigatórios: nome completo, NIF, contacto telefónico, email, e garantir unicidade de NIF.
- **RF-04**: O sistema DEVE permitir o registo de animais com dados obrigatórios: nome, espécie (Cão ou Gato), raça, data de nascimento, peso, estado de saúde atual, necessidades alimentares e medicação em curso.
- **RF-04**: O sistema DEVE garantir que cada animal está associado a pelo menos um tutor no momento do registo (relação obrigatória).
- **RF-04**: O sistema DEVE permitir a consulta de dados de tutores e animais por utilizadores autorizados (receção, diretor, médico veterinário).
- **RF-06**: O sistema DEVE determinar a disponibilidade de cada box em tempo real com base em três condições cumulativas: (1) inexistência de reserva ativa ou confirmada para o período; (2) inexistência de estadia ativa no período; (3) estado de limpeza registado como "CONCLUÍDO".
- **RF-06**: O sistema DEVE impedir automaticamente a marcação de uma box como disponível se qualquer uma das três condições deixar de se verificar.
- **RF-06**: O sistema DEVE impedir a criação de reservas que violem o controlo de disponibilidade e apresentar alternativas válidas.
- **RF-07**: O sistema DEVE permitir a criação de reservas, registando: período (data início e data fim), box, animal, tutor e estado inicial "ATIVA".
- **RF-07**: O sistema DEVE permitir o cancelamento de reservas; uma reserva cancelada não pode ser reativada (deverá criar-se uma nova).
- **RF-05**: O sistema DEVE manter um histórico completo das reservas e estadias de cada animal, consultável pela receção e direção.

### Validation & Test Requirements

- A conclusão desta feature exige testes automatizados sobre as funcionalidades P1 (US-09, US-12, US-06).
- Cada funcionalidade P1 deve ter pelo menos um teste de caminho feliz e um teste de validação/erro (ex.: NIF duplicado, box indisponível, conflito de período).
- As regras de disponibilidade e integridade (RF-06, RF-07, RD-05, RD-06) devem ter testes dedicados na camada de serviço.
- Deve existir pelo menos um teste de integração para UC-03 e UC-04.
- Cada funcionalidade P1 deve ter pelo menos um teste unitário na camada de serviço, um teste de integração no controller/contrato e um teste de sistema end-to-end.
- Deve existir pelo menos um teste de sistema que percorra o fluxo tutor → animal → disponibilidade → reserva sem depender de dados partilhados de outras features.

### Key Entities

- **Tutor**: Representa o proprietário/responsável do animal. Atributos: `id`, `nome`, `nif`, `contacto`, `email`, `dataRegisto`.
- **Animal**: Representa um animal sob cuidado do hotel. Atributos: `id`, `nome`, `especie` (enum: CAO, GATO), `raca`, `dataNascimento`, `peso`, `estadoSaude` (enum: NORMAL, ALTERADO, CRITICO), `necessidadesAlimentares`, `medicacaoCurso`, `dataRegisto`.
- **Reserva**: Representa uma alocação de box para um período. Atributos: `id`, `dataInicio`, `dataFim`, `alojamento` (box), `animal`, `tutor`, `estado` (enum: ATIVA, CONFIRMADA, CANCELADA, CONCLUIDA), `dataCriacao`.
- **Alojamento** (Box): Representa uma box/quarto do hotel. Atributos: `id`, `identificacao`, `tipo` (enum: CANINO, FELINO), `capacidade`, `estadoLimpeza` (enum: PENDENTE, CONCLUIDO).
- Relações:
  - `Tutor 1:* Animal` (um tutor pode ter múltiplos animais)
  - `Animal 1:* Reserva` (um animal pode ter múltiplas reservas)
  - `Alojamento 1:* Reserva` (um alojamento pode ter múltiplas reservas em períodos diferentes)
  - `Tutor 1:* Reserva` (um tutor pode ter múltiplas reservas)

---

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Funcionário consegue registar um tutor completo em menos de 2 minutos.
- **SC-002**: Funcionário consegue registar um animal (novo ou adicional) em menos de 3 minutos.
- **SC-003**: Sistema consulta e apresenta disponibilidade de boxes em menos de 1 segundo.
- **SC-004**: Funcionário consegue criar uma reserva válida em menos de 3 minutos a partir dos dados do tutor/animal.
- **SC-005**: 100% das reservas criadas respeitam as regras de não-overbooking (disponibilidade, limpeza, sem conflitos de período).
- **SC-006**: Consulta de tutor/animal por NIF ou nome retorna resultados em menos de 500ms.
- **SC-007**: Sistema previne com sucesso 100% das tentativas de criar reservas em boxes indisponíveis.
- **SC-008**: Histórico completo de reservas de um animal é consultável em menos de 1 segundo.
- **SC-009**: Existe pelo menos 1 teste automatizado por funcionalidade P1 desta feature (US-09, US-12, US-06).
- **SC-010**: Existe pelo menos 1 teste de integração por caso de uso coberto nesta fase (UC-03 e UC-04).
- **SC-011**: As regras críticas de disponibilidade e associação tutor-animal têm testes automatizados com resultado verde no pipeline local.
- **SC-012**: Cada funcionalidade P1 tem pelo menos 1 teste unitário, 1 teste de integração e 1 teste de sistema executável no pipeline local.

---

## Documentação Técnica

- O código Java deve ser documentado com Javadoc em controllers, services, DTOs e exceptions públicas ou relevantes para fluxos de negócio.
- O Maven Javadoc Plugin deve gerar a documentação HTML do código.
- A implementação deve seguir `.specify/memory/constitution.md` e as convenções de estilo em [docs/Etapa3/convencoes.md](../../docs/Etapa3/convencoes.md).

---

## Assumptions

- **Utilizadores**: Apenas funcionários de receção autenticados podem aceder a registo de tutores e criação de reservas.
- **Dados de Tutor**: Email e contacto telefónico não precisam de validação complexa nesta fase; serão consultados apenas para contacto.
- **Espécies**: Sistema suporta apenas Cão e Gato conforme RD definido.
- **Estado de Saúde**: Valores iniciais são NORMAL, ALTERADO, CRITICO (conforme modelo de domínio).
- **Período de Reserva**: O sistema não valida datas passadas; assume-se que o utilizador insere datas futuras válidas.
- **Limpeza**: O estado da box é atualizado manualmente pelo responsável de limpeza (funcionalidade de Fase 1 — Limpeza).
- **Integração com Autenticação**: Sistema reutiliza a autenticação da Fase 1 (Spring Security com sessão HTTP).
- **Banco de Dados**: Dados persistidos em MySQL conforme ADR-03.
- **Performance**: O controlo de disponibilidade assume indexação adequada de datas e estados para suportar queries rápidas.

---

## Design Considerations

### Persistence & Relationships

- Tutor-Animal: Relação um-para-muitos com integridade referencial (foreign key em `Animal.tutor_id`).
- Reserva: Deve referencia `Animal`, `Tutor` e `Alojamento` para rastreabilidade completa.
- Índices: `(tutor.nif)`, `(animal.tutor_id)`, `(reserva.dataInicio, reserva.dataFim)`, `(alojamento.id, reserva.dataInicio, reserva.dataFim)` para suportar queries de disponibilidade.

### Validation Rules

- NIF único por tutor (constraint UNIQUE em BD).
- Cada animal obrigatoriamente associado a um tutor (NOT NULL constraint).
- Período de reserva: `dataInicio < dataFim` (validação na camada service).
- Cancelamento de reserva: Apenas reservas com estado ATIVA podem ser canceladas.

### API Contracts

- **Registar Tutor**: `POST /api/tutores` — Request: `{nome, nif, contacto, email}` → Response: `{id, nome, nif, ...}`
- **Registar Animal**: `POST /api/animais` — Request: `{tutorId, nome, especie, raca, ...}` → Response: `{id, tutorId, nome, ...}`
- **Consultar Disponibilidade**: `GET /api/alojamentos/disponibilidade?dataInicio=...&dataFim=...` → Response: Array de boxes disponíveis.
- **Criar Reserva**: `POST /api/reservas` — Request: `{animalId, tutorId, alojamentoId, dataInicio, dataFim}` → Response: `{id, estado, ...}`

**UI Routes (server-side)**

- `GET /tutores` — Lista e pesquisa de tutores.
- `GET /tutores/{id}` — Detalhe de tutor com lista de animais associados.
- `GET /animais` — Lista geral de animais (rota adicionada).
- `GET /animais/{id}` — Detalhe de animal (rota adicionada).
- `GET /reservas` — Lista de reservas.
- `GET /reservas/novo` — Assistente de criação de reservas (wizard).
- `POST /reservas` — Submissão do formulário de criação de reserva.

---

## Out of Scope (Fase 2)

- Pagamentos (Fase 3).
- Check-in/check-out de estadias (Fase 3).
- Cuidados diários e intervenções clínicas (Fase 4).
- Relatórios executivos (Fase 5).
- Modificação de dados de tutor/animal após registo (mudanças futuras marcadas como "deverá ser revisto em iterações posteriores").
