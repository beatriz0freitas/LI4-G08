# Feature Specification: Fundação do sistema do hotel de animais

**Feature Branch**: `[001-fundacao-hotel-animais]`  
**Created**: 2026-05-04  
**Status**: Draft  
**Documentação de Referência**:
- Tema e Enunciado: [docs/Etapa0/Enunciado.md](../../../docs/Etapa0/Enunciado.md)
- User Stories: [docs/Etapa1/01-user-stories/user-stories.md](../../../docs/Etapa1/01-user-stories/user-stories.md)
- Requisitos Funcionais: [docs/Etapa1/02-requirements/functional/](../../../docs/Etapa1/02-requirements/functional/)
- Requisitos de Domínio: [docs/Etapa1/02-requirements/domain/](../../../docs/Etapa1/02-requirements/domain/)
- Arquitetura: [docs/Etapa2/01-architecture/architecture.md](../../../docs/Etapa2/01-architecture/architecture.md)
- Decisões Arquiteturais: [ADR-01](../../../docs/Etapa2/04-architecture-decisions/ADR-01-monolito-camadas.md), [ADR-02](../../../docs/Etapa2/04-architecture-decisions/ADR-02-spring-mvc-thymeleaf-ssr.md), [ADR-03](../../../docs/Etapa2/04-architecture-decisions/ADR-03-persistencia-sgbd-relacional.md), [ADR-04](../../../docs/Etapa2/04-architecture-decisions/ADR-04-mysql-base-dados.md), [ADR-05](../../../docs/Etapa2/04-architecture-decisions/ADR-05-controlo-acesso-perfil.md), [ADR-06](../../../docs/Etapa2/04-architecture-decisions/ADR-06-isolamento-apresentacao-dtos.md), [ADR-07](../../../docs/Etapa2/04-architecture-decisions/ADR-07-docker-desenvolvimento-testes.md)
- Diagrama de Classes: [docs/Etapa2/02-class-diagram/class-diagram.md](../../../docs/Etapa2/02-class-diagram/class-diagram.md)
- Mockups UI: [docs/Etapa2/05-ui-interface-mockup/](../../../docs/Etapa2/05-ui-interface-mockup/)

## User Scenarios & Testing *(mandatory)*

### User Story 1 - US-01: Consultar disponibilidade em tempo real (Priority: P1)

**Origem**: [docs/Etapa1/01-user-stories/user-stories.md — Diretor/Gestor](../../../docs/Etapa1/01-user-stories/user-stories.md)

Como diretor, quero consultar a disponibilidade em tempo real dos alojamentos e a taxa de ocupação atual, para ter uma visão operacional do hotel.

**Why this priority**: suportado por RF-01 (Dashboard operacional), que é Must Have na documentação. Este é o primeiro indicador operacional.

**Independent Test**: após autenticação como diretor, conseguir visualizar no dashboard a ocupação atual dos alojamentos.

**Acceptance Scenarios**:

1. **Given** que sou diretor autenticado, **When** acedo ao dashboard, **Then** vejo a taxa de ocupação atual conforme especificado em [RF-01](../../../docs/Etapa1/02-requirements/functional/RF-01.md).
2. **Given** que um alojamento tem limpeza pendente, **When** consulto a ocupação, **Then** esse alojamento não é contado como disponível, respeitando [RD-01](../../../docs/Etapa1/02-requirements/domain/RD-01.md).

---

### User Story 2 - US-02: Consultar indicadores de faturação (Priority: P1)

**Origem**: [docs/Etapa1/01-user-stories/user-stories.md — Diretor/Gestor](../../../docs/Etapa1/01-user-stories/user-stories.md)

Como diretor, quero consultar indicadores de faturação e pagamentos pendentes filtráveis por período, para acompanhar o desempenho financeiro do hotel.

**Why this priority**: suportado por RF-01 (Dashboard operacional). Integrado no painel base da fundação.

**Independent Test**: visualizar no dashboard a faturação diária e mensal, como especificado em [RF-01](../../../docs/Etapa1/02-requirements/functional/RF-01.md).

**Acceptance Scenarios**:

1. **Given** que sou diretor, **When** abro o dashboard, **Then** vejo indicadores de faturação diária e mensal.
2. **Given** que há estadias ativas, **When** consulto os indicadores, **Then** reflectem o valor esperado conforme tipos de alojamento.

---

### User Story 3 - US-20, US-21: Gestão de limpeza de alojamentos (Priority: P1)

**Origem**: [docs/Etapa1/01-user-stories/user-stories.md — Responsável pela Limpeza](../../../docs/Etapa1/01-user-stories/user-stories.md)

Como responsável pela limpeza, quero consultar a lista de alojamentos que necessitam de limpeza após um check-out e marcar um alojamento como limpo no sistema, para que fique disponível para novas reservas.

**Why this priority**: suportado por [RD-01](../../../docs/Etapa1/02-requirements/domain/RD-01.md) (estado de limpeza é factor de disponibilidade). Mockup em [wf06-limpeza.html](../../../docs/Etapa2/05-ui-interface-mockup/wf06-limpeza.html).

**Independent Test**: listar alojamentos pendentes e marcar um como concluído; confirmação de que fica disponível.

**Acceptance Scenarios**:

1. **Given** que sou responsável de limpeza, **When** acedo à área de limpeza, **Then** vejo lista de alojamentos com estado PENDENTE, conforme especificado em [RD-01](../../../docs/Etapa1/02-requirements/domain/RD-01.md).
2. **Given** que assumo a limpeza de um alojamento, **When** clico "Marcar como Limpo", **Then** o seu estado muda para CONCLUIDO e fica disponível para reservas.

---

### Edge Cases

- O que acontece se o colaborador perde a sessão durante o uso do sistema?
- Como são tratados alojamentos com estado de limpeza PENDENTE na contagem de disponibilidade?

## Requirements *(mandatory)*

### Test Database

- Os testes com persistência usam o serviço MySQL `db-tests`, com a base `hotelanimais_test`, separado do serviço principal `db` e da base `hotelanimais`.
- O alvo `make test` deve recriar o serviço `db-tests` antes de executar a suite, garantindo isolamento entre execuções.
- Testes sem persistência devem usar Mockito, sem ligação à base de dados.

### Functional Requirements

Mapeamento direto com a documentação de Etapa 1:

- **[RF-01](../../../docs/Etapa1/02-requirements/functional/RF-01.md)**: O sistema deve disponibilizar um dashboard com indicadores de ocupação, estadias ativas, reservas futuras e faturação diária/mensal, atualizados em tempo real (máximo 60s).
  - Mapa para: [US-01](../../../docs/Etapa1/01-user-stories/user-stories.md), [US-02](../../../docs/Etapa1/01-user-stories/user-stories.md)
  
- **[RD-01](../../../docs/Etapa1/02-requirements/domain/RD-01.md)**: Um alojamento só está disponível se não tiver reserva/estadia ativa E limpeza marcada como CONCLUIDO.
  - Mapa para: [US-06](../../../docs/Etapa1/01-user-stories/user-stories.md), [US-21](../../../docs/Etapa1/01-user-stories/user-stories.md)

- **Autenticação e Autorização** (conforme [UC-01](../../../docs/Etapa1/03-use-cases/UC-01.md)):
  - O sistema MUST autenticar utilizadores via username/password (BC-Crypt).
  - O sistema MUST validar permissões por papel (`TipoColaborador`).
  - O sistema MUST encaminhar para dashboard ou bloquear conforme perfil.

### Key Entities *(include if feature involves data)*

Preservados conforme [Model de Domínio — Etapa 1](../../../docs/Etapa1/04-domain-model/domain-model.md):

- **Alojamento**: `id`, `identificacao`, `estadoLimpeza` (ENUM: PENDENTE, CONCLUIDO)
- **Colaborador**: `id`, `nome`, `email`, `tipoColaborador` (ENUM conforme [UC-01](../../../docs/Etapa1/03-use-cases/UC-01.md))
- Relação Alojamento ↔ Reserva (verá sido adicionada em Fase 2)

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% dos 5 perfis de colaborador ([TipoColaborador](../../../docs/Etapa2/02-class-diagram/class-diagram.md)) conseguem fazer login e chegar ao dashboard correto.
- **SC-002**: O dashboard carrega em < 2 segundos para diretor autenticado (conforme [RNF-01](../../../docs/Etapa1/02-requirements/non-functional/RNF-01.md)).
- **SC-003**: Responsável de limpeza consegue consultar e marcar alojamentos como limpos sem erros de transação.
- **SC-004**: Regra [RD-01](../../../docs/Etapa1/02-requirements/domain/RD-01.md) é respeitada: alojamento com limpeza PENDENTE não aparece como disponível.
- **SC-005**: A fase 1 atinge pelo menos 80% de cobertura de linhas nas classes de `controller`, `service` e `repository` abrangidas por esta feature, medida por relatório JaCoCo gerado no pipeline local.

## Documentação Técnica

- O código Java deve ser documentado com Javadoc em controllers, services, DTOs e exceptions públicas ou relevantes para fluxos de negócio.
- O relatório JaCoCo deve ser gerado com o Maven e utilizado como critério explícito de cobertura para a fase 1.
- O Maven Javadoc Plugin deve gerar a documentação HTML do código.

## Assumptions

- Esta Fase 1 implementa apenas [UC-01](../../../docs/Etapa1/03-use-cases/UC-01.md) (autenticação), [RF-01](../../../docs/Etapa1/02-requirements/functional/RF-01.md) (dashboard) e [RD-01](../../../docs/Etapa1/02-requirements/domain/RD-01.md) (limpeza).
- Tutores, Animais, Reservas e Estadias não são manipuladas nesta fase (framework mockado no controlador placeholder).
- Todas as regras de negócio provêm dos documentos de Etapa 1 e 2 — nenhuma foi inventada.
- Templates Thymeleaf seguem os mockups em [docs/Etapa2/05-ui-interface-mockup/](../../../docs/Etapa2/05-ui-interface-mockup/).
