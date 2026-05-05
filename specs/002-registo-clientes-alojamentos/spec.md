# Feature Specification: Registo Base de Clientes e Alojamentos

**Feature Branch**: `002-registo-clientes-alojamentos`  
**Created**: 2026-05-05  
**Status**: Draft  
**Phase**: Fase 2 do Plano de Implementação Gradual  
**Mapped Use Cases**: UC-03 (Registar Tutor e Animal), UC-04 (Criar Reserva)  
**Mapped User Stories**: US-05, US-06, US-09, US-12  
**Mapped Domain Requirements**: RD-03, RD-05, RD-06  

---

## Referências de Apoio (Etapa 2)

Esta feature deve ser interpretada e implementada em coerência com os artefactos de Etapa 2, em particular:

- [architecture.md](../../docs/Etapa2/01-architecture/architecture.md) para a arquitetura MVC em camadas e responsabilidades dos componentes.
- [class-diagram.md](../../docs/Etapa2/02-class-diagram/class-diagram.md) para a estrutura de entidades, services e repositories.
- [UC-03.mmd](../../docs/Etapa2/03-seq-diagrams/UC-03.mmd) e [UC-04.mmd](../../docs/Etapa2/03-seq-diagrams/UC-04.mmd) para os fluxos de registo e criação de reserva.
- [ADR-01-monolito-camadas.md](../../docs/Etapa2/04-architecture-decisions/ADR-01-monolito-camadas.md), [ADR-02-spring-mvc-thymeleaf-ssr.md](../../docs/Etapa2/04-architecture-decisions/ADR-02-spring-mvc-thymeleaf-ssr.md), [ADR-03-mysql-base-dados-principal.md](../../docs/Etapa2/04-architecture-decisions/ADR-03-mysql-base-dados-principal.md), [ADR-04-spring-security-sessao-http.md](../../docs/Etapa2/04-architecture-decisions/ADR-04-spring-security-sessao-http.md) e [ADR-05-dto-entre-controller-service.md](../../docs/Etapa2/04-architecture-decisions/ADR-05-dto-entre-controller-service.md) para as decisões arquiteturais base.
- Os mockups de interface de Etapa 2, em especial [wf03-reservas.html](../../docs/Etapa2/05-ui-interface-mockup/wf03-reservas.html), devem ser usados como referência visual para os ecrãs de disponibilidade e criação de reservas; os restantes mockups da pasta `05-ui-interface-mockup` devem orientar a consistência visual do fluxo.

---

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Registo de Tutor (Priority: P1)

Como funcionário de receção, quero registar um novo tutor no sistema com os seus dados de contacto, para que fique disponível para futuras reservas.

**Why this priority**: P1 — Fundação essencial para o módulo de reservas. Sem tutores registados, não é possível criar animais nem reservas. Bloqueia todas as funcionalidades subsequentes.

**Independent Test**: Um funcionário pode registar um tutor completo (nome, NIF, contacto, email) e recuperá-lo posteriormente pelo NIF ou nome. A funcionalidade é testável isoladamente e permite ao utilizador criar um registo base.

**Acceptance Scenarios**:

1. **Given** funcionário autenticado na aplicação, **When** acessa a secção "Registar Tutor", **Then** vê um formulário com campos obrigatórios: nome completo, NIF, contacto telefónico e email.
2. **Given** um funcionário preenche corretamente todos os campos do tutor, **When** submete o formulário, **Then** o sistema guarda o registo e apresenta uma mensagem de confirmação.
3. **Given** um funcionário tenta registar um tutor com NIF já existente, **When** submete o formulário, **Then** o sistema apresenta um erro e impede a duplicação.
4. **Given** um tutor foi registado, **When** o funcionário pesquisa pelo NIF ou nome, **Then** o sistema apresenta os dados completos do tutor.

---

### User Story 2 - Registo de Animal (Priority: P1)

Como funcionário de receção, quero registar um novo animal associado a um tutor já existente, fornecendo espécie, raça, data de nascimento e estado de saúde, para que o sistema tenha o historial completo do animal.

**Why this priority**: P1 — Essencial para criar reservas. Um animal é a entidade central do negócio; sem poder registar animais associados aos tutores, não é possível prosseguir para reservas ou estadias.

**Independent Test**: Um funcionário pode registar um animal (nome, espécie, raça, data de nascimento, peso, estado de saúde, necessidades alimentares, medicação) associado a um tutor existente. O animal fica consultável e pronto para reservas.

**Acceptance Scenarios**:

1. **Given** um tutor existe no sistema, **When** o funcionário seleciona "Adicionar Animal" para esse tutor, **Then** vê um formulário com campos obrigatórios: nome, espécie (Cão ou Gato), raça, data de nascimento, peso, estado de saúde, necessidades alimentares e medicação em curso.
2. **Given** o funcionário preenche todos os campos do animal, **When** submete, **Then** o sistema cria a ficha clínica do animal e a associa ao tutor.
3. **Given** um animal foi registado, **When** o funcionário consulta os animais do tutor, **Then** o sistema lista todos os animais associados.
4. **Given** um tutor tem múltiplos animais, **When** o funcionário consulta o tutor, **Then** o sistema apresenta todos os animais com respetivos estados de saúde.

---

### User Story 3 - Consulta de Disponibilidade de Alojamentos (Priority: P1)

Como funcionário de receção, quero consultar a disponibilidade de alojamentos (boxes) em tempo real para um período específico, para responder imediatamente a pedidos de reserva.

**Why this priority**: P1 — Crítica para o fluxo de reservas. Sem poder visualizar disponibilidade, é impossível criar uma reserva válida. Afeta diretamente a experiência do utilizador na receção.

**Independent Test**: Um funcionário consegue selecionar um período de datas e ver quais as boxes disponíveis (com estado de limpeza concluído, sem reservas confirmadas e sem estadias ativas nesse período).

**Acceptance Scenarios**:

1. **Given** funcionário autenticado na aplicação, **When** acessa o módulo "Criar Reserva", **Then** vê um campo para selecionar período (data início e data fim).
2. **Given** o funcionário seleciona um período válido, **When** clica em "Consultar Disponibilidade", **Then** o sistema apresenta apenas as boxes que cumprem as três condições: sem reserva confirmada, sem estadia ativa, com limpeza concluída.
3. **Given** uma box tem uma reserva confirmada para o período pretendido, **When** o funcionário consulta disponibilidade nesse período, **Then** essa box não aparece como disponível.
4. **Given** não existem boxes disponíveis no período solicitado, **When** o funcionário submete a consulta, **Then** o sistema apresenta uma mensagem informativa e sugere alternativas de datas.

---

### User Story 4 - Criação de Reserva (Priority: P1)

Como funcionário de receção, quero criar uma reserva para um animal, selecionando um período e uma box disponível, para garantir a alocação e evitar overbooking.

**Why this priority**: P1 — Transação operacional central do sistema. Toda a cadeia de valor (check-in, pagamentos, cuidados) depende de uma reserva criada corretamente.

**Independent Test**: Um funcionário consegue criar uma reserva selecionando um tutor, um animal desse tutor, um período e uma box disponível. A reserva fica confirmada e a box fica marcada como indisponível no período.

**Acceptance Scenarios**:

1. **Given** um tutor e animal existem no sistema, **When** o funcionário acessa "Criar Reserva" e seleciona o tutor, **Then** o sistema lista todos os animais do tutor.
2. **Given** um animal é selecionado, **When** o funcionário indica o período pretendido, **Then** o sistema apresenta as boxes disponíveis apenas para esse período.
3. **Given** o funcionário seleciona uma box disponível, **When** confirma a reserva, **Then** o sistema cria a reserva com estado "ATIVA" e marca a box como indisponível no período.
4. **Given** uma reserva foi criada, **When** o funcionário consulta a disponibilidade novamente para o mesmo período, **Then** a box agora aparece como indisponível.
5. **Given** um funcionário tenta criar uma reserva com período ou box inconsistente, **When** submete, **Then** o sistema valida e impede a criação, apresentando motivo do erro.

---

### User Story 5 - Consulta de Dados de Tutor e Animal (Priority: P2)

Como funcionário de receção, quero consultar rapidamente os dados completos de um tutor e dos seus animais no momento do atendimento, para prestar um serviço personalizado e informado.

**Why this priority**: P2 — Suporte operacional importante mas não bloqueia reservas básicas. Melhora a qualidade do atendimento ao disponibilizar histórico completo.

**Independent Test**: Um funcionário consegue pesquisar um tutor pelo nome ou NIF e aceder a um painel com todos os dados do tutor, lista de animais associados, e histórico de reservas/estadias passadas.

**Acceptance Scenarios**:

1. **Given** funcionário na receção, **When** pesquisa um tutor pelo nome ou NIF, **Then** o sistema apresenta o tutor encontrado.
2. **Given** um tutor foi encontrado, **When** o funcionário abre a ficha do tutor, **Then** vê todos os dados: nome, NIF, contacto, email, e lista de animais associados.
3. **Given** um animal está listado, **When** o funcionário clica no animal, **Then** acede ao histórico completo: data de nascimento, raça, estado de saúde, necessidades alimentares, medicação, e histórico de reservas.

---

## Requirements *(mandatory)*

### Functional Requirements

- **RF-04**: O sistema DEVE permitir o registo de tutores com dados obrigatórios: nome completo, NIF, contacto telefónico, email, e garantir unicidade de NIF.
- **RF-04**: O sistema DEVE permitir o registo de animais com dados obrigatórios: nome, espécie (Cão ou Gato), raça, data de nascimento, peso, estado de saúde atual, necessidades alimentares e medicação em curso.
- **RF-04**: O sistema DEVE garantir que cada animal está associado a pelo menos um tutor no momento do registo (relação obrigatória).
- **RF-04**: O sistema DEVE permitir a consulta de dados de tutores e animais por utilizadores autorizados (receção, diretor, médico veterinário).
- **RF-06**: O sistema DEVE determinar a disponibilidade de cada box em tempo real com base em três condições cumulativas: (1) inexistência de reserva confirmada para o período; (2) inexistência de estadia ativa no período; (3) estado de limpeza registado como "CONCLUÍDO".
- **RF-06**: O sistema DEVE impedir automaticamente a marcação de uma box como disponível se qualquer uma das três condições deixar de se verificar.
- **RF-06**: O sistema DEVE impedir a criação de reservas que violem o controlo de disponibilidade e apresentar alternativas válidas.
- **RF-07**: O sistema DEVE permitir a criação de reservas, registando: período (data início e data fim), box, animal, tutor e estado inicial "ATIVA".
- **RF-07**: O sistema DEVE permitir o cancelamento de reservas; uma reserva cancelada não pode ser reativada (deverá criar-se uma nova).
- **RF-05**: O sistema DEVE manter um histórico completo das reservas e estadias de cada animal, consultável pela receção e direção.

### Key Entities

- **Tutor**: Representa o proprietário/responsável do animal. Atributos: `id`, `nome`, `nif`, `contacto`, `email`, `dataRegisto`.
- **Animal**: Representa um animal sob cuidado do hotel. Atributos: `id`, `nome`, `especie` (enum: CAO, GATO), `raca`, `dataNascimento`, `peso`, `estadoSaude` (enum: NORMAL, ALTERADO, CRITICO), `necessidadesAlimentares`, `medicacaoCurso`, `dataRegisto`.
- **Reserva**: Representa uma alocação de box para um período. Atributos: `id`, `dataInicio`, `dataFim`, `alojamento` (box), `animal`, `tutor`, `estado` (enum: ATIVA, CANCELADA, CONCLUIDA), `dataCriacao`.
- **Alojamento** (Box): Representa uma box/quarto do hotel. Atributos: `id`, `identificacao`, `tipo`, `capacidade`, `estadoLimpeza` (enum: PENDENTE, CONCLUIDO).
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

---

## Out of Scope (Fase 2)

- Pagamentos (Fase 3).
- Check-in/check-out de estadias (Fase 3).
- Cuidados diários e intervenções clínicas (Fase 4).
- Relatórios executivos (Fase 5).
- Modificação de dados de tutor/animal após registo (mudanças futuras marcadas como "deverá ser revisto em iterações posteriores").
