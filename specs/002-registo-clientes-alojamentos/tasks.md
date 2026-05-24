# Tasks: Registo Base de Clientes e Alojamentos

**Input**: Design documents from `/specs/002-registo-clientes-alojamentos/`  
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/

**Organization**: Tasks are grouped by user story to enable independent implementation and validation of each story.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Initialize the feature-specific persistence and UI scaffolding used by all stories.

- [ ] T001 Create the Flyway migration for tutor, animal, reserva and availability-related constraints in `PatasBigodesApp/src/main/resources/db/migration/V3__create_tutor_animal_reserva.sql`
- [ ] T002 [P] Add the shared navigation and layout fragments aligned with the Etapa 2 mockups in `PatasBigodesApp/src/main/resources/templates/fragments/navbar.html` and `PatasBigodesApp/src/main/resources/templates/fragments/sidebar.html`
- [ ] T003 [P] Create the shared Thymeleaf layout and base fragment files in `PatasBigodesApp/src/main/resources/templates/layout.html`, `PatasBigodesApp/src/main/resources/templates/fragments/head.html`, `PatasBigodesApp/src/main/resources/templates/fragments/footer.html`, and `PatasBigodesApp/src/main/resources/templates/placeholders/modulo.html`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core domain, persistence and service foundations that must exist before any user story can be implemented.

**Checkpoint**: Foundation ready - tutor, animal, reserva and alojamento flows can be implemented on top of these shared components.

- [ ] T004 Define the shared domain enums for species, health status and reservation state in `PatasBigodesApp/src/main/java/pt/hotel/animais/model/enums/Especie.java`, `PatasBigodesApp/src/main/java/pt/hotel/animais/model/enums/EstadoSaude.java`, and `PatasBigodesApp/src/main/java/pt/hotel/animais/model/enums/EstadoReserva.java`
- [ ] T005 Create the core domain entities for tutor, animal and reserva, and adjust alojamento relations if needed, in `PatasBigodesApp/src/main/java/pt/hotel/animais/model/Tutor.java`, `PatasBigodesApp/src/main/java/pt/hotel/animais/model/Animal.java`, `PatasBigodesApp/src/main/java/pt/hotel/animais/model/Reserva.java`, and `PatasBigodesApp/src/main/java/pt/hotel/animais/model/Alojamento.java`
- [ ] T006 Create the repositories for tutor, animal and reserva, and extend alojamento availability queries, in `PatasBigodesApp/src/main/java/pt/hotel/animais/repository/TutorRepository.java`, `PatasBigodesApp/src/main/java/pt/hotel/animais/repository/AnimalRepository.java`, `PatasBigodesApp/src/main/java/pt/hotel/animais/repository/ReservaRepository.java`, and `PatasBigodesApp/src/main/java/pt/hotel/animais/repository/AlojamentoRepository.java`
- [ ] T007 Add the shared DTOs and form models used by the receção flows in `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/TutorFormDto.java`, `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/AnimalFormDto.java`, `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/ReservaFormDto.java`, and `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/DisponibilidadeAlojamentoDto.java`
- [ ] T008 Implement the shared service methods for tutor lookup, animal association, availability calculation and reservation validation in `PatasBigodesApp/src/main/java/pt/hotel/animais/service/ITutorService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/TutorService.java`, `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IAnimalService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/AnimalService.java`, `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IAlojamentoService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/AlojamentoService.java`, and `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IReservaService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/ReservaService.java`

---

## Phase 3: User Story 1 - Registo de Tutor (Priority: P1)

**Goal**: Allow receção staff to register a tutor with unique identification and retrieve the record by NIF or name.

**Independent Test**: A tutor can be created and then found again through the tutor lookup flow without relying on any animal or reservation data.

- [ ] T009 [US1] Implement tutor registration rules, including unique NIF validation, in `PatasBigodesApp/src/main/java/pt/hotel/animais/service/ITutorService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/TutorService.java` and `PatasBigodesApp/src/main/java/pt/hotel/animais/repository/TutorRepository.java`
- [ ] T010 [US1] Implement the tutor create and lookup endpoints in `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/TutorAnimalController.java`
- [ ] T011 [P] [US1] Populate the tutor list, form and detail views in `PatasBigodesApp/src/main/resources/templates/tutores/list.html`, `PatasBigodesApp/src/main/resources/templates/tutores/form.html`, and `PatasBigodesApp/src/main/resources/templates/tutores/detail.html`
- [ ] T012 [US1] Wire tutor form validation feedback and success messaging in `PatasBigodesApp/src/main/resources/templates/tutores/form.html` and `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/TutorAnimalController.java`

---

## Phase 4: User Story 2 - Registo de Animal (Priority: P1)

**Goal**: Allow receção staff to register an animal linked to an existing tutor, with the health and care information required by the domain.

**Independent Test**: An animal can be created only when a tutor exists, and the animal is immediately associated to that tutor and visible in its record.

- [ ] T013 [US2] Implement animal registration validation and tutor association in `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IAnimalService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/AnimalService.java` and `PatasBigodesApp/src/main/java/pt/hotel/animais/repository/AnimalRepository.java`
- [ ] T014 [US2] Implement the animal registration flow inside the shared receção controller in `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/TutorAnimalController.java`
- [ ] T015 [P] [US2] Populate the animal registration and detail templates in `PatasBigodesApp/src/main/resources/templates/animais/form.html` and `PatasBigodesApp/src/main/resources/templates/animais/detail.html`
- [ ] T016 [US2] Ensure the animal record persists the required health, diet and medication fields in `PatasBigodesApp/src/main/java/pt/hotel/animais/model/Animal.java` and `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IAnimalService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/AnimalService.java`

---

## Phase 5: User Story 3 - Consulta de Disponibilidade de Alojamentos (Priority: P1)

**Goal**: Allow receção staff to query real-time alojamento availability for a target period using the cleaning and reservation rules defined in the domain.

**Independent Test**: Given a date range, the system returns only alojamentos that are clean, without active stays and without overlapping confirmed reservations.

- [ ] T017 [US3] Implement the availability query logic and overlap checks in `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IAlojamentoService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/AlojamentoService.java` and `PatasBigodesApp/src/main/java/pt/hotel/animais/repository/AlojamentoRepository.java`
- [ ] T018 [US3] Implement the availability search flow and empty-state handling in `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/ReservaController.java`
- [ ] T019 [P] [US3] Populate the availability and search-result views aligned with `wf03-reservas.html` in `PatasBigodesApp/src/main/resources/templates/reservas/disponibilidade.html` and `PatasBigodesApp/src/main/resources/templates/reservas/index.html`
- [ ] T020 [US3] Add the availability view model and alternative-date helper DTOs in `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/DisponibilidadeAlojamentoDto.java` and `PatasBigodesApp/src/main/java/pt/hotel/animais/dto/AlternativaDatasDto.java`

---

## Phase 6: User Story 4 - Criação de Reserva (Priority: P1)

**Goal**: Allow receção staff to create a reservation only when tutor, animal and alojamento are all valid and the time window is available.

**Independent Test**: A reservation can be created from a tutor/animal pair and a valid alojamento, and the same period becomes unavailable immediately after confirmation.

- [ ] T021 [US4] Implement reservation creation, conflict prevention and state transitions in `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IReservaService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/ReservaService.java` and `PatasBigodesApp/src/main/java/pt/hotel/animais/repository/ReservaRepository.java`
- [ ] T022 [US4] Implement the reservation confirmation flow and alojamento blocking in `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/ReservaController.java` and `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IAlojamentoService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/AlojamentoService.java`
- [ ] T023 [P] [US4] Populate the reservation form and confirmation views in `PatasBigodesApp/src/main/resources/templates/reservas/form.html` and `PatasBigodesApp/src/main/resources/templates/reservas/confirmacao.html`
- [ ] T024 [US4] Add reservation state guardrails for cancellation and reactivation rules in `PatasBigodesApp/src/main/java/pt/hotel/animais/model/Reserva.java` and `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IReservaService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/ReservaService.java`

---

## Phase 7: User Story 5 - Consulta de Dados de Tutor e Animal (Priority: P2)

**Goal**: Allow receção staff to inspect the full tutor and animal record, including historical reservation information, during the service desk interaction.

**Independent Test**: A tutor can be opened from a search result and the detail view shows the tutor data, linked animals and the relevant reservation history.

- [ ] T025 [US5] Implement tutor and animal detail retrieval with history-aware data in `PatasBigodesApp/src/main/java/pt/hotel/animais/service/ITutorService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/TutorService.java` and `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IAnimalService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/AnimalService.java`
- [ ] T026 [US5] Implement the detailed consultation endpoints in `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/TutorAnimalController.java`
- [ ] T027 [P] [US5] Populate the tutor and animal consultation templates in `PatasBigodesApp/src/main/resources/templates/tutores/detail.html` and `PatasBigodesApp/src/main/resources/templates/animais/detail.html`
- [ ] T028 [US5] Add reservation history queries for the consultation view in `PatasBigodesApp/src/main/java/pt/hotel/animais/repository/ReservaRepository.java` and `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IReservaService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/ReservaService.java`

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Final consistency pass across views, navigation and feature documentation.

- [ ] T029 [P] Update the navigation and visual copy to stay aligned with the Etapa 2 UI mockups in `PatasBigodesApp/src/main/resources/templates/fragments/navbar.html`, `PatasBigodesApp/src/main/resources/templates/fragments/sidebar.html`, and `PatasBigodesApp/src/main/resources/templates/placeholders/modulo.html`
- [ ] T030 Validate the end-to-end quickstart flow documented in `specs/002-registo-clientes-alojamentos/quickstart.md`
- [ ] T031 Validate tutor lookup timing against SC-006 by measuring search by NIF and name in `PatasBigodesApp/src/main/java/pt/hotel/animais/service/ITutorService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/TutorService.java` and `PatasBigodesApp/src/test/java/pt/hotel/animais/service/TutorServiceTest.java`
- [ ] T032 Validate availability lookup timing against SC-003 by measuring the query path in `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IAlojamentoService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/AlojamentoService.java` and `PatasBigodesApp/src/test/java/pt/hotel/animais/service/AlojamentoServiceTest.java`
- [ ] T033 Validate reservation confirmation timing against SC-004 and the overbooking guard against SC-005 in `PatasBigodesApp/src/main/java/pt/hotel/animais/service/IReservaService.java` e `PatasBigodesApp/src/main/java/pt/hotel/animais/service/ReservaService.java` and `PatasBigodesApp/src/test/java/pt/hotel/animais/service/ReservaServiceTest.java`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No story dependencies; can start immediately.
- **Foundational (Phase 2)**: Depends on Setup completion and blocks all user stories.
- **User Stories (Phases 3+)**: All depend on Foundational phase completion.
- **Polish (Phase 8)**: Depends on completion of the desired user stories.

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational; no dependency on later stories.
- **User Story 2 (P1)**: Can start after Foundational; depends on the tutor data model and repository created in earlier phases.
- **User Story 3 (P1)**: Can start after Foundational; depends on the alojamento and reserva availability services.
- **User Story 4 (P1)**: Can start after Foundational; depends on the shared tutor, animal and availability contracts, but should not require completion of later stories.
- **User Story 5 (P2)**: Can start after Foundational; may reuse data from User Stories 1 to 4 but should not require them to be reworked.

### Within Each User Story

- Domain or repository changes before service logic.
- Service logic before controller wiring.
- Controller wiring before view updates.
- Story-specific view updates before final manual validation.

### Parallel Opportunities

- Setup tasks T002 and T003 can run in parallel after T001.
- Foundational tasks touching distinct files can be split across team members once the data model shape is agreed.
- The view tasks marked [P] within each user story can proceed in parallel with the corresponding service work when the data contract is stable.
- User Stories 1 to 5 can progress in parallel after the foundation is complete if different developers own different slices.

## Parallel Example: User Story 3

```bash
Task: "Implement the availability query logic and overlap checks in PatasBigodesApp/src/main/java/pt/hotel/animais/service/IAlojamentoService.java e PatasBigodesApp/src/main/java/pt/hotel/animais/service/AlojamentoService.java and PatasBigodesApp/src/main/java/pt/hotel/animais/repository/AlojamentoRepository.java"
Task: "Create the availability and search-result views aligned with wf03-reservas.html in PatasBigodesApp/src/main/resources/templates/reservas/disponibilidade.html and PatasBigodesApp/src/main/resources/templates/reservas/index.html"
Task: "Add the availability view model and alternative-date helper DTOs in PatasBigodesApp/src/main/java/pt/hotel/animais/dto/DisponibilidadeAlojamentoDto.java and PatasBigodesApp/src/main/java/pt/hotel/animais/dto/AlternativaDatasDto.java"
```

## Implementation Strategy

### MVP First

1. Complete Phase 1: Setup.
2. Complete Phase 2: Foundational.
3. Complete Phase 3: User Story 1.
4. Validate the tutor registration and lookup flow independently.
5. Continue with User Story 2, then User Story 3, then User Story 4, and finally User Story 5.

### Incremental Delivery

1. Deliver tutor registration first as the smallest usable slice.
2. Add animal registration on top of the tutor data.
3. Add availability consultation using the reservation and cleaning rules.
4. Add reservation creation and blocking once availability is reliable.
5. Add the detailed consultation view for reception staff last.

### Notes

- [P] tasks are intended for different files and no direct dependency chains.
- Each story is written so it can be implemented and validated without blocking later stories.
- The UI should stay consistent with the Etapa 2 mockups, especially `wf03-reservas.html` for the reservation flow.
