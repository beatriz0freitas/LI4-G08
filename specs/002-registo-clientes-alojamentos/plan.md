# Implementation Plan: Registo Base de Clientes e Alojamentos

**Branch**: `002-registo-clientes-alojamentos` | **Date**: 2026-05-05 | **Spec**: [spec.md](./spec.md)  
**Input**: Feature specification from `/specs/002-registo-clientes-alojamentos/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Implementar a base operacional da receção para registar tutores e animais, consultar alojamentos disponíveis em tempo real e criar reservas sem overbooking, preservando a rastreabilidade aos casos de uso UC-03 e UC-04, às user stories US-06, US-09 e US-12, e aos requisitos RF-04, RF-05, RF-06, RF-07, RD-03, RD-05 e RD-06.

## Technical Context

<!--
  ACTION REQUIRED: Replace the content in this section with the technical details
  for the project. The structure here is presented in advisory capacity to guide
  the iteration process.
-->

**Language/Version**: Java 21  
**Primary Dependencies**: Spring Boot 3.3.5, Spring MVC, Thymeleaf, Spring Security 6, Spring Data JPA, Flyway, MySQL driver
**Storage**: MySQL 8 em desenvolvimento, produção e testes de integração
**Testing**: JUnit 5, Spring Boot Test, Mockito, Spring Security Test  
**Target Platform**: Aplicação web server-side em Linux/Docker, executada via Maven ou Docker Compose  
**Project Type**: Web application MVC monolítica  
**Performance Goals**: Consulta de disponibilidade em <= 1s, pesquisa de tutores em <= 500ms e criação de reserva em <= 3 min, suficientes para uso de receção  
**Constraints**: Sem overbooking; NIF único por tutor; cada animal com pelo menos um tutor; criação de reserva depende de disponibilidade e limpeza concluída  
**Scale/Scope**: Escopo faseado para o módulo de receção, com expansão posterior para check-in/out, pagamentos e clínica

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Status | Evidence |
|-----------|--------|----------|
| **Domain Scope First** | OK | A feature fica limitada ao registo de tutores, animais, disponibilidade e reservas, tal como descrito em UC-03, UC-04, US-06, US-09 e US-12. |
| **Scenario-Driven Requirements** | OK | A spec já inclui cenários de aceitação, limites (NIF duplicado, box indisponível, período inválido) e critérios mensuráveis. |
| **Modular Separation of Concerns** | OK | A implementação seguirá controllers, services, repositories e entidades JPA, alinhada com a arquitetura da Etapa 2. |
| **Verification Before Expansion** | OK | O plano gera `research.md`, `data-model.md`, `contracts/` e `quickstart.md` antes de tarefas, com base em fluxos verificáveis. |
| **Data Integrity, Security, and Operational Reliability** | OK | Unicidade de NIF, associação obrigatória tutor-animal, validação de disponibilidade e transações no service layer sustentam integridade e evitam overbooking. |

**Resultado**: Nenhuma violação conhecida. O plano pode avançar para a fase de research e design.

## Project Structure

### Documentation (this feature)

```text
specs/002-registo-clientes-alojamentos/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
PatasBigodesApp/
├── pom.xml
├── docker-compose.yml
├── Dockerfile
├── Makefile
└── src/
    ├── main/
    │   ├── java/pt/hotel/animais/
    │   │   ├── controller/
    │   │   │   ├── TutorAnimalController.java
    │   │   │   └── ReservaController.java
    │   │   ├── service/
    │   │   │   ├── TutorService.java
    │   │   │   ├── AnimalService.java
    │   │   │   ├── ReservaService.java
    │   │   │   └── AlojamentoService.java
    │   │   ├── repository/
    │   │   │   ├── TutorRepository.java
    │   │   │   ├── AnimalRepository.java
    │   │   │   ├── ReservaRepository.java
    │   │   │   └── AlojamentoRepository.java
    │   │   ├── model/
    │   │   │   ├── Tutor.java
    │   │   │   ├── Animal.java
    │   │   │   ├── Reserva.java
    │   │   │   ├── Alojamento.java
    │   │   │   └── enums/
    │   │   └── dto/
    │   └── resources/
    │       ├── db/migration/
    │       └── templates/
    │           ├── tutores/
    │           ├── animais/
    │           ├── reservas/
    │           └── alojamentos/
    └── test/
        └── java/pt/hotel/animais/
            ├── controller/
            ├── service/
            └── integration/
```

**Structure Decision**: Manter o monólito em camadas já adotado pelo projeto, estendendo a base existente em `PatasBigodesApp` com novas entidades, serviços, repositórios e templates Thymeleaf para o fluxo de receção.

### Database Migration Scope

As alterações de esquema desta feature fazem parte da planificação e devem ser entregues como migrations Flyway em `PatasBigodesApp/src/main/resources/db/migration/`. Em particular, a fase 2 introduz a migration `V3__create_tutor_animal_reserva.sql` para suportar `Tutor`, `Animal` e `Reserva` com as respetivas chaves primárias, estrangeiras, índices e constraints de integridade.

## Complexity Tracking

Não aplicável. O plano não introduz violações à constituição que exijam justificação extra.

## Phase 0: Research & Clarifications

### Research Output

O ficheiro `research.md` vai consolidar as decisões seguintes:
- stack técnica já definida pelo projeto: Java 21, Spring Boot 3.3.5, MVC server-side, Thymeleaf, Spring Security, JPA e MySQL;
- persistência com integridade relacional para tutor, animal, alojamento e reserva;
- validação de disponibilidade no service layer para impedir overbooking;
- suporte explícito por migrations Flyway para criar e versionar o esquema necessário à fase 2;
- abordagem de testes com `Spring Boot Test`, `Mockito`, `Spring Security Test` e MySQL nos testes com persistência;
- contratos de interação orientados aos fluxos de receção, não a uma API pública separada.

### Clarifications Resolved

Não existem `NEEDS CLARIFICATION` remanescentes nesta fase. As decisões relevantes já estão suportadas pela spec, pela Etapa 1 e pela Etapa 2.

## Phase 1: Design & Contracts

### 1. Domain Model (`data-model.md`)

O modelo vai cobrir:
- `Tutor` como entidade raiz do registo de cliente;
- `Animal` como entidade dependente, com associação obrigatória a pelo menos um tutor;
- `Alojamento` como recurso de disponibilidade e limpeza;
- `Reserva` como compromisso temporal entre tutor, animal e alojamento.

As regras-chave a documentar serão:
- unicidade do NIF do tutor;
- associação obrigatória tutor-animal;
- intervalo de reserva válido (`dataInicio < dataFim`);
- disponibilidade baseada em limpeza concluída e ausência de conflito temporal.

### 2. Interface Contracts (`contracts/`)

Como o sistema é uma aplicação web server-side, os contratos vão documentar os fluxos de interface e os payloads dos formulários, não uma API pública independente.

Contratos previstos:
- `contracts/tutores-animais.md` — criação e consulta de tutores e animais;
- `contracts/reservas-disponibilidade.md` — consulta de disponibilidade e criação de reservas.

### 3. Quick Start Guide (`quickstart.md`)

O guia vai cobrir:
1. arranque da stack local com `make up`;
2. execução dos testes com `make test`;
3. abertura da aplicação em `http://localhost:8080`;
4. validação manual dos fluxos de registo de tutor, registo de animal, consulta de disponibilidade e criação de reserva.

### 4. Agent Context Update

Atualizar `.github/copilot-instructions.md` para apontar para este plano:

```markdown
<!-- SPECKIT START -->
For additional context about technologies to be used, project structure,
shell commands, and other important information, read:
specs/002-registo-clientes-alojamentos/plan.md
<!-- SPECKIT END -->
```

## Phase 2: Task Generation

Phase 2 is intentionally left for `/speckit.tasks` after review and approval of this plan.

Expected task breakdown:
- preparar persistência e entidades de tutor/animal/reserva/alojamento;
- implementar serviços de registo e validação;
- implementar controllers e templates Thymeleaf;
- adicionar testes de integração e regressão;
- validar a rastreabilidade entre spec, plano e implementação.

## Corrections / Implementation Updates (2026-05-06)

Durante a execução foram aplicadas correções técnicas e de navegação que afectam parte do plano e das tasks. O registo completo dessas alterações encontra-se em `refinements.md`.

