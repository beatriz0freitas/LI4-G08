# Tasks: arquitetura-projeto

## Phase 1: Setup

**Purpose**: Prepare traceability and documentation entry points for the architecture feature.

- [x] T001 [P] Create the use-case traceability scaffold in [docs/requirements/use-cases/traceability.md](docs/requirements/use-cases/traceability.md)
- [x] T002 [P] Refresh architecture and use-case indexes in [docs/requirements/use-cases/README.md](docs/requirements/use-cases/README.md) and [docs/architecture/decisoes/README.md](docs/architecture/decisoes/README.md)

---

## Phase 2: Foundational

**Purpose**: Lock the contracts and shared architectural assumptions that all diagrams depend on.

- [x] T003 Define the class diagram contract, including services, repositories, enums, cardinalities, exceptions, and use-case traceability in [specs/001-arquitetura-projeto/contracts/class-diagram-contract.md](specs/001-arquitetura-projeto/contracts/class-diagram-contract.md)
- [x] T004 Define the sequence diagram contract, including the required flows, file formats, and UC-to-sequence mapping in [specs/001-arquitetura-projeto/contracts/sequence-diagram-contract.md](specs/001-arquitetura-projeto/contracts/sequence-diagram-contract.md)
- [x] T005 Define the component diagram contract, including modules, layers, and UML notation in [specs/001-arquitetura-projeto/contracts/component-diagram-contract.md](specs/001-arquitetura-projeto/contracts/component-diagram-contract.md)
- [x] T006 Consolidate the architecture data model, entities, enums, and traceability links in [specs/001-arquitetura-projeto/data-model.md](specs/001-arquitetura-projeto/data-model.md)
- [x] T007 Update the architecture quickstart with validation steps for classes, sequences, components, ADRs, and use-case coverage in [specs/001-arquitetura-projeto/quickstart.md](specs/001-arquitetura-projeto/quickstart.md)

**Checkpoint**: Architectural contracts, data model, and validation flow are ready before detailed story work.

---

## Phase 3: User Story 1 - Arquitetura de Classes Modular e Implementavel (Priority: P1)

**Goal**: Define a complete, explicit class-model contract that covers all domain entities, service/repository interfaces, cardinalities, and method-to-use-case traceability.

**Independent Test**: The class diagram contract and traceability matrix can be reviewed on their own to confirm that UC-01..UC-13 are covered by the model and that every service/repository contract has the required methods.

- [x] T008 [P] [US1] Expand the service/repository contract details and method inventory by use case in [specs/001-arquitetura-projeto/contracts/class-diagram-contract.md](specs/001-arquitetura-projeto/contracts/class-diagram-contract.md)
- [x] T009 [P] [US1] Build the UC-01..UC-13 to service-method traceability matrix in [docs/requirements/use-cases/traceability.md](docs/requirements/use-cases/traceability.md)
- [x] T010 [P] [US1] Finalize the domain entity and enum mapping for the class diagram in [specs/001-arquitetura-projeto/data-model.md](specs/001-arquitetura-projeto/data-model.md)
- [x] T011 [US1] Create the Mermaid class diagram skeleton in [docs/architecture/diagramas/classes.mmd](docs/architecture/diagramas/classes.mmd)
- [x] T012 [US1] Annotate the class diagram with cardinalities, UML visibility, exceptions, and RD-01..RD-09 notes in [docs/architecture/diagramas/classes.mmd](docs/architecture/diagramas/classes.mmd)

**Checkpoint**: The class model is explicit, traceable to use cases, and ready for review.

---

## Phase 4: User Story 2 - Decisoes Arquiteturais Rastreaveis (Priority: P2)

**Goal**: Ensure all architectural decisions are documented as ADRs with explicit Sommerville-based justification and kept aligned with the current class/service model.

**Independent Test**: Each ADR can be read independently and must contain context, alternatives, decision, consequences, and Sommerville-based rationale.

- [x] T013 [P] [US2] Finalize the services-vs-facades decision record in [docs/architecture/decisoes/ADR-001-services-vs-facades.md](docs/architecture/decisoes/ADR-001-services-vs-facades.md)
- [x] T014 [P] [US2] Finalize the interfaces-and-contracts decision record in [docs/architecture/decisoes/ADR-002-interfaces-e-contratos.md](docs/architecture/decisoes/ADR-002-interfaces-e-contratos.md)
- [x] T015 [P] [US2] Finalize the service-consolidation decision record in [docs/architecture/decisoes/ADR-003-consolidacao-services.md](docs/architecture/decisoes/ADR-003-consolidacao-services.md)
- [x] T016 [P] [US2] Finalize the patterns decision record for Factory, Strategy, and Observer in [docs/architecture/decisoes/ADR-004-padroes-factory-strategy-observer.md](docs/architecture/decisoes/ADR-004-padroes-factory-strategy-observer.md)
- [x] T017 [US2] Synthesize the architectural rationale and decision summary in [specs/001-arquitetura-projeto/research.md](specs/001-arquitetura-projeto/research.md)

**Checkpoint**: The architectural rationale is complete, consistent, and ready to support diagram generation.

---

## Phase 5: User Story 3 - Consistencia de Enumeracoes e Regras de Dominio (Priority: P3)

**Goal**: Produce the sequence and component diagram contracts and artifacts, with explicit use-case traceability and the required business-rule notes.

**Independent Test**: The sequence and component diagram contracts and artifacts can be reviewed independently and must clearly map the required use cases to the required diagrams.

- [x] T018 [P] [US3] Finalize the sequence-diagram contract with the mandatory UC-to-sequence mapping in [specs/001-arquitetura-projeto/contracts/sequence-diagram-contract.md](specs/001-arquitetura-projeto/contracts/sequence-diagram-contract.md)
- [x] T019 [P] [US3] Finalize the component-diagram contract with the required modules, layers, and UML notation in [specs/001-arquitetura-projeto/contracts/component-diagram-contract.md](specs/001-arquitetura-projeto/contracts/component-diagram-contract.md)
- [x] T020 [P] [US3] Create the reservation, check-in, and check-out sequence diagrams in [docs/architecture/diagramas/seq-reserva.mmd](docs/architecture/diagramas/seq-reserva.mmd), [docs/architecture/diagramas/seq-reserva.txt](docs/architecture/diagramas/seq-reserva.txt), [docs/architecture/diagramas/seq-checkin.mmd](docs/architecture/diagramas/seq-checkin.mmd), [docs/architecture/diagramas/seq-checkin.txt](docs/architecture/diagramas/seq-checkin.txt), [docs/architecture/diagramas/seq-checkout.mmd](docs/architecture/diagramas/seq-checkout.mmd), and [docs/architecture/diagramas/seq-checkout.txt](docs/architecture/diagramas/seq-checkout.txt)
- [x] T021 [P] [US3] Create the care, billing, cleaning, and veterinary sequence diagrams in [docs/architecture/diagramas/seq-cuidados.mmd](docs/architecture/diagramas/seq-cuidados.mmd), [docs/architecture/diagramas/seq-cuidados.txt](docs/architecture/diagramas/seq-cuidados.txt), [docs/architecture/diagramas/seq-faturacao.mmd](docs/architecture/diagramas/seq-faturacao.mmd), [docs/architecture/diagramas/seq-faturacao.txt](docs/architecture/diagramas/seq-faturacao.txt), [docs/architecture/diagramas/seq-limpeza.mmd](docs/architecture/diagramas/seq-limpeza.mmd), [docs/architecture/diagramas/seq-limpeza.txt](docs/architecture/diagramas/seq-limpeza.txt), [docs/architecture/diagramas/seq-veterinario.mmd](docs/architecture/diagramas/seq-veterinario.mmd), and [docs/architecture/diagramas/seq-veterinario.txt](docs/architecture/diagramas/seq-veterinario.txt)
- [x] T022 [US3] Create the component diagram with the required modules and layers in [docs/architecture/diagramas/componentes.mmd](docs/architecture/diagramas/componentes.mmd)
- [x] T023 [US3] Document the use-case to method-to-diagram traceability summary in [docs/requirements/use-cases/traceability.md](docs/requirements/use-cases/traceability.md)
- [x] T024 [US3] Align export/import guidance and validation steps with the final diagram set in [specs/001-arquitetura-projeto/quickstart.md](specs/001-arquitetura-projeto/quickstart.md)

**Checkpoint**: The sequence and component diagram set is complete and traceable to the documented use cases.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final consistency review across docs, indexes, and traceability.

- [x] T025 [P] Normalize use-case and architecture index links in [docs/requirements/use-cases/README.md](docs/requirements/use-cases/README.md) and [docs/architecture/decisoes/README.md](docs/architecture/decisoes/README.md)
- [x] T026 [P] Recheck checklist and spec cross-references in [specs/001-arquitetura-projeto/checklists/requirements.md](specs/001-arquitetura-projeto/checklists/requirements.md) and [specs/001-arquitetura-projeto/spec.md](specs/001-arquitetura-projeto/spec.md)
- [x] T027 Confirm the final artifact inventory in [specs/001-arquitetura-projeto/plan.md](specs/001-arquitetura-projeto/plan.md) and [specs/001-arquitetura-projeto/quickstart.md](specs/001-arquitetura-projeto/quickstart.md)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - blocks all user stories
- **User Stories (Phase 3+)**: Depend on Foundational phase completion
- **Polish (Phase 6)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Starts after Foundational phase; no dependency on other user stories
- **User Story 2 (P2)**: Starts after Foundational phase; may use US1 outputs but remains independently reviewable
- **User Story 3 (P3)**: Starts after Foundational phase; may use US1/US2 outputs but remains independently reviewable

### Within Each User Story

- Contracts before artifacts that consume them
- Traceability before final validation
- Models and diagrams before polish
- Story complete before moving to the next priority

### Parallel Opportunities

- T001 and T002 can run in parallel
- T003, T004, T005, T006, and T007 can run in parallel after Setup
- T008, T009, and T010 can run in parallel after the foundational phase
- T013, T014, T015, and T016 can run in parallel after the foundational phase
- T018 and T019 can run in parallel after the foundational phase
- T020 and T021 can run in parallel after the foundational phase
- T025 and T026 can run in parallel during polish

## MVP Scope

- The MVP for this feature is the complete class-diagram contract plus the use-case traceability matrix (Phase 3 / User Story 1).

## Implementation Strategy

### Incremental Delivery

1. Complete Setup and Foundational phases.
2. Deliver User Story 1 to lock the class model and use-case coverage.
3. Deliver User Story 2 to lock down the architectural rationale.
4. Deliver User Story 3 to complete sequence/component coverage and cross-artifact traceability.
5. Finish with polish and inventory reconciliation.

### Validation Strategy

- Validate each phase independently using the checklist in [specs/001-arquitetura-projeto/checklists/requirements.md](specs/001-arquitetura-projeto/checklists/requirements.md)
- Confirm use-case coverage against [docs/requirements/use-cases/README.md](docs/requirements/use-cases/README.md)
- Confirm diagram filenames and paths match the contract files and quickstart instructions

## Format Validation

- Every task follows the checklist format `- [ ] T### [P] [US#] Description with file path`
- Setup and Foundational tasks omit user-story labels as required
- User story tasks include the correct `[US#]` labels
- All task descriptions include exact file paths
