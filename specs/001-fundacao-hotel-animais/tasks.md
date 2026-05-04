# Tasks: Fundação do sistema do hotel de animais

**Input**: Design documents from `/specs/001-fundacao-hotel-animais/`
**Prerequisites**: `plan.md` (required), `spec.md` (required), `research.md`, `data-model.md`, `contracts/`

**Tests**: Incluídos, porque a spec define cenário de teste independente e critérios de sucesso mensuráveis.

**Organization**: Tasks are grouped by user story to enable independent implementation and validation.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (`US1`, `US2`, `US3`)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Preparar a base técnica do monólito MVC e a UI comum.

- [X] T001 [P] Configurar o build Maven e o plugin do Spring Boot em `PatasBigodesApp/pom.xml`
- [X] T002 [P] Fixar perfis de configuração e Flyway em `PatasBigodesApp/src/main/resources/application.properties` e `PatasBigodesApp/src/main/resources/application-mysql.properties`
- [X] T003 [P] Preparar a base visual AdminLTE nos fragments partilhados em `PatasBigodesApp/src/main/resources/templates/fragments/head.html`, `navbar.html`, `sidebar.html` e `footer.html`

**Checkpoint**: base técnica e visual pronta para suportar autenticação, dashboard e limpeza.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Primitivos comuns que bloqueiam todas as user stories.

**⚠️ CRITICAL**: Nenhuma user story deve avançar antes desta fase estar concluída.

- [X] T004 [P] Finalizar regras de autenticação, logout e perfis em `PatasBigodesApp/src/main/java/pt/hotel/animais/config/SecurityConfig.java`
- [X] T005 Implementar o fluxo base de login e redirect em `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/AuthController.java`
- [X] T006 [P] Criar teste de integração para login, logout e rotas protegidas em `PatasBigodesApp/src/test/java/pt/hotel/animais/integration/SecurityIntegrationTest.java`
- [X] T007 [P] Finalizar a entidade `Alojamento` e o enum `EstadoLimpeza` em `PatasBigodesApp/src/main/java/pt/hotel/animais/model/Alojamento.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/model/enums/EstadoLimpeza.java`
- [X] T008 Finalizar as queries base do repositório de alojamentos em `PatasBigodesApp/src/main/java/pt/hotel/animais/repository/AlojamentoRepository.java`
- [X] T009 Consolidar as regras de listagem e limpeza nos services em `PatasBigodesApp/src/main/java/pt/hotel/animais/service/AlojamentoService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/LimpezaService.java`
- [X] T010 [P] Ajustar o ecrã de login e o placeholder de módulos em `PatasBigodesApp/src/main/resources/templates/auth/login.html` e `PatasBigodesApp/src/main/resources/templates/placeholders/modulo.html`

**Checkpoint**: a fundação está pronta; as user stories já podem ser trabalhadas sem refatorações estruturais.

---

## Phase 3: User Story 1 - Consultar disponibilidade em tempo real (Priority: P1) 🎯 MVP

**Goal**: O diretor vê no dashboard a disponibilidade atual dos alojamentos.

**Independent Test**: após autenticação como diretor, o dashboard mostra a ocupação atual com base nos alojamentos disponíveis.

### Tests for User Story 1

- [X] T011 [P] [US1] Adicionar teste do controller do dashboard para a ocupação em `PatasBigodesApp/src/test/java/pt/hotel/animais/controller/DashboardControllerTest.java`
- [X] T012 [P] [US1] Adicionar teste do service de alojamentos para a contagem de disponíveis em `PatasBigodesApp/src/test/java/pt/hotel/animais/service/AlojamentoServiceTest.java`

### Implementation for User Story 1

- [X] T013 [US1] Ligar a contagem de alojamentos disponíveis ao controller em `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/DashboardController.java`
- [X] T014 [US1] Renderizar o cartão de ocupação com componentes AdminLTE em `PatasBigodesApp/src/main/resources/templates/dashboard/index.html`

**Checkpoint**: a história 1 fica funcional e testável de forma independente.

---

## Phase 4: User Story 2 - Consultar indicadores de faturação (Priority: P1)

**Goal**: O diretor vê indicadores de faturação e pagamentos pendentes no dashboard.

**Independent Test**: após autenticação como diretor, o dashboard mostra faturação diária, mensal e pendentes.

### Tests for User Story 2

- [ ] T015 [US2] Expandir `PatasBigodesApp/src/test/java/pt/hotel/animais/controller/DashboardControllerTest.java` com cobertura para faturação e pagamentos pendentes

### Implementation for User Story 2

- [ ] T016 [US2] Popular os atributos de faturação e pagamentos pendentes no controller em `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/DashboardController.java`
- [ ] T017 [US2] Renderizar os cartões de faturação e pendentes no dashboard em `PatasBigodesApp/src/main/resources/templates/dashboard/index.html`

**Checkpoint**: as duas histórias do dashboard ficam completas sem mexer na lógica de limpeza.

---

## Phase 5: User Story 3 - Gestão de limpeza de alojamentos (Priority: P1)

**Goal**: O responsável de limpeza consulta alojamentos pendentes e marca-os como limpos.

**Independent Test**: após autenticação como responsável de limpeza, a lista mostra alojamentos pendentes e a ação de limpar atualiza o estado.

### Tests for User Story 3

- [X] T018 [P] [US3] Adicionar teste do controller de limpeza para a lista de pendentes em `PatasBigodesApp/src/test/java/pt/hotel/animais/controller/LimpezaControllerTest.java`
- [X] T019 [P] [US3] Adicionar teste do service de limpeza para a transição de estado em `PatasBigodesApp/src/test/java/pt/hotel/animais/service/LimpezaServiceTest.java`

### Implementation for User Story 3

- [ ] T020 [US3] Renderizar a lista de alojamentos pendentes com tabela AdminLTE em `PatasBigodesApp/src/main/resources/templates/limpeza/listar.html`
- [ ] T021 [US3] Ligar a ação de marcar como limpo e as flash messages em `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/LimpezaController.java`
- [ ] T022 [US3] Garantir a transição transacional do estado de limpeza em `PatasBigodesApp/src/main/java/pt/hotel/animais/service/LimpezaService.java`

**Checkpoint**: a limpeza fica operável e a regra de disponibilidade passa a ser respeitada no fluxo diário.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Afinar a UI comum, validar cobertura e corrigir regressões.

- [ ] T023 [P] Ajustar as classes comuns do AdminLTE e acessibilidade em `PatasBigodesApp/src/main/resources/templates/fragments/head.html`, `PatasBigodesApp/src/main/resources/templates/fragments/navbar.html`, `PatasBigodesApp/src/main/resources/templates/fragments/sidebar.html`, `PatasBigodesApp/src/main/resources/templates/dashboard/index.html` e `PatasBigodesApp/src/main/resources/templates/limpeza/listar.html`
- [ ] T024 Executar `mvn test` em `PatasBigodesApp/` e corrigir regressões em `PatasBigodesApp/src/main/java` e `PatasBigodesApp/src/test/java`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: sem dependências; pode começar de imediato
- **Foundational (Phase 2)**: depende da Setup; bloqueia todas as user stories
- **User Stories (Phase 3+)**: dependem da Foundational
- **Polish (Phase 6)**: depende de todas as histórias que se pretenda entregar

### User Story Dependencies

- **US1**: pode começar após a Foundational; é o MVP da fase
- **US2**: pode começar após a Foundational; reutiliza o mesmo `DashboardController` e o mesmo template do dashboard
- **US3**: pode começar após a Foundational; depende da base de autenticação e do modelo `Alojamento`

### Within Each User Story

- Tests devem ser preparados antes da implementação correspondente
- Services antes de controller/template quando a lógica ainda não existe
- Templates depois dos atributos do controller estarem definidos
- Cada story deve fechar com validação independente antes de avançar

### Parallel Opportunities

- Setup: T001, T002 e T003 podem correr em paralelo
- Foundational: T004, T006, T007 e T010 podem avançar em paralelo depois de T005 estar alinhado
- US1: T011 e T012 podem correr em paralelo
- US3: T018 e T019 podem correr em paralelo
- US2 e US3 podem avançar em paralelo depois da fundação, desde que não haja conflito de ficheiros

---

## Parallel Example: User Story 1

```bash
# Preparar os testes e a lógica em paralelo
Task: "Add dashboard controller test for occupancy in PatasBigodesApp/src/test/java/pt/hotel/animais/controller/DashboardControllerTest.java"
Task: "Add Alojamento service test for available count in PatasBigodesApp/src/test/java/pt/hotel/animais/service/AlojamentoServiceTest.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Completar Phase 1: Setup
2. Completar Phase 2: Foundational
3. Completar Phase 3: User Story 1
4. Parar e validar o dashboard com autenticação e ocupação

### Incremental Delivery

1. Setup + Foundational → base pronta
2. US1 → dashboard operacional mínimo
3. US2 → dashboard com faturação
4. US3 → limpeza operacional
5. Cada história acrescenta valor sem quebrar o que já foi entregue

### Parallel Team Strategy

1. A equipa completa Setup + Foundational em conjunto
2. Depois divide trabalho entre dashboard e limpeza
3. Cada story fecha com testes e validação próprios

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps each task to the story in `spec.md`
- This plan assumes the current monolithic MVC architecture in `PatasBigodesApp/`
- `future-expansions.md` is intentionally excluded from implementation tasks because it is only reference material for later phases
