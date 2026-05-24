<!--
Sync Impact Report
Version change: 1.0.1 to 1.0.2
Modified principles: none
Added sections: none
Removed sections: none
Templates checked: specs 001-005 reference docs/Etapa3/convencoes.md as implementation style convention
Follow-up TODOs: none
-->

# LI4-G08 Constitution

## Core Principles

### I. Domain Scope First
Every requirement, design decision, and implementation task MUST stay inside the hotel-for-animals domain described by the project brief. Version 1 covers only dogs and cats, and every feature MUST be traceable to a stakeholder need, a user story, and a measurable acceptance criterion. Scope changes, new animal categories, or new business capabilities require an explicit amendment before they are treated as in scope.

### II. Scenario-Driven Requirements
Requirements MUST be written as verifiable scenarios, not as vague intentions. Ambiguous terms such as fast, easy, simple, or real time are forbidden unless they are quantified. Each feature MUST define the actor, preconditions, observable behavior, failure cases, and success criteria so that the result can be tested independently.

### III. Modular Separation of Concerns
The system MUST be decomposed into cohesive domain capabilities: reservations, stay management, care records, clinical history, billing, reporting, and access control. Cross-cutting concerns such as validation, persistence, logging, and authentication MUST be shared and centralized rather than duplicated in each feature. Interfaces and data contracts SHOULD be stable before implementation begins.

### IV. Verification Before Expansion
No feature is complete until it has an explicit verification path. Every user story MUST have acceptance scenarios and a test strategy before implementation is considered done, and new work MUST be delivered in small, independently testable increments. Contract changes, workflow changes, and data model changes require regression coverage before they are accepted.

### V. Data Integrity, Security, and Operational Reliability
Business data MUST remain consistent under concurrent use, especially for reservations, occupancy, payments, and clinical records. Access control MUST follow least privilege, personal data MUST be protected according to the project's privacy obligations, and important actions MUST be auditable. Availability checks, check-in/check-out actions, and billing operations MUST remain responsive enough for front-desk use, and failures MUST be visible through logging and recoverable behavior.

## Project Scope and Quality Constraints

### In Scope for the First Release
- Reservation and occupancy management for animal accommodation.
- Check-in and check-out workflows.
- Daily care registration and handover notes between staff.
- Clinical history and intervention records.
- Billing, payments, and service extras.
- Operational dashboards and period-based reporting.
- Role-based access for director, reception, caregivers, cleaning staff, and veterinary responsibility.

### Out of Scope for the First Release
- Support for species other than dogs and cats.
- Client-facing portals, mobile apps, and self-service bookings unless explicitly added later.
- Machine learning recommendations, predictive analytics, and automation that changes business decisions without human approval.
- Infrastructure or platform choices that are not required by the approved plan.

### Quality Constraints
- Requirements MUST be measurable, testable, and traceable from stakeholder need to final acceptance.
- The project MUST prevent overbooking and preserve consistency between reservations, stays, and cleaning state.
- User-facing operations MUST be simple enough for non-technical staff to perform after minimal training.
- Key actions SHOULD complete within a few seconds for front-desk use, and availability lookups SHOULD feel near immediate.
- Documentation, diagrams, prompts, and decisions MUST remain versioned with the repository.
- Java/Spring Boot implementation MUST follow the style and structure conventions defined in `docs/Etapa3/convencoes.md`.
- The project MAY use AI assistance, but AI output is always a draft until a human reviews and approves it.
- All significant technical and architectural decisions MUST be recorded in a decision log (`docs/decisions/`) to ensure a clear audit trail for project evaluation.

### Normative Basis
- Sommerville, I. (2016). Software Engineering (10th ed.). Pearson Education.
- The project should follow Sommerville's guidance on clear requirements, iterative validation, modular design, traceability, and disciplined quality control.
- Where relevant, the project SHOULD also stay consistent with common software engineering quality expectations such as verifiability, maintainability, reliability, and security.

## Delivery Workflow and Review Gates

### Required Workflow
1. Start from the constitution and the project brief before writing any feature spec.
2. Produce the feature specification before the implementation plan.
3. Produce the implementation plan before tasks.
4. Produce tasks before implementation.
5. Validate each stage against the constitution before moving forward.

### Review Gates
- A feature spec MUST include prioritized user stories, edge cases, functional requirements, and success criteria.
- A plan MUST show the technical context, the constitution check, and the chosen structure.
- Tasks MUST be organized by user story so that each story can be implemented and tested independently.
- Implementation MUST not begin until the story being worked on is approved and traceable.
- Any deviation from the constitution MUST be justified in the plan and revisited before merge.

### AI and Workspace Guidance
- Workspace-shared prompts, agents, and instructions SHOULD live in `.github/` so the team can version and review them.
- Reusable prompting patterns and examples SHOULD also be documented under `docs/llm-patterns/` when they are part of the project workflow.
- Personal experiments that are not meant for the team MAY stay outside version control.
- Generated content MUST be reviewed by a human before it is treated as authoritative.

## Governance

This constitution is the highest local process authority for the project. It overrides informal practices, ad hoc prompting habits, and conflicting guidance in lower-level documents. Any amendment MUST state the reason for the change, list the affected principles or sections, and identify the downstream artifacts that need updates.

Versioning follows semantic rules: a MAJOR bump is required for principle removal or redefinition; a MINOR bump is required for a new principle or materially expanded guidance; and a PATCH bump is required for wording clarifications or non-semantic refinements. Ratification happens when the team accepts the constitution for active use, and the ratified version MUST be updated whenever an amendment is approved.

Every significant milestone MUST include a constitution check during review of the spec, the plan, and the task list. If a change conflicts with the constitution, the change is rejected until either the artifact is corrected or the constitution is formally amended.

**Version**: 1.0.2 | **Ratified**: 2026-04-19 | **Last Amended**: 2026-05-24
