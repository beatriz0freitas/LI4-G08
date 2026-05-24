# Implementation Plan: Reservas, Estadias e Pagamentos

**Branch**: `003-reservas-estadias-pagamentos` | **Date**: 2026-05-08 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/003-reservas-estadias-pagamentos/spec.md`

**Note**: Este ficheiro é produzido no fluxo `/speckit.plan`, antes de gerar `tasks.md`.

## Summary

Implementar o ciclo operacional de receção para reservas, check-in/check-out e pagamentos, garantindo consistência de estados entre `Reserva`, `Estadia`, `Pagamento` e `Alojamento`, alinhado com US-06, US-07, US-10, US-11, US-12, US-05, US-02 e com os requisitos RF-01, RF-05, RF-06, RF-07, RF-08, RF-09, RF-10, incluindo estratégia de testes automatizados por funcionalidade crítica.

## Technical Context

**Language/Version**: Java 21  
**Primary Dependencies**: Spring Boot 3.3.5, Spring MVC, Thymeleaf, Spring Security 6, Spring Data JPA, Flyway, MySQL driver
**Storage**: MySQL 8 para desenvolvimento, produção e testes de integração
**Testing**: JUnit 5, Spring Boot Test, MockMvc, Mockito, Spring Security Test  
**Target Platform**: Aplicação web server-side (Linux/Docker), execução via Maven/Makefile  
**Project Type**: Web application MVC monolítica  
**Performance Goals**: Leituras < 2s; escritas < 3s; atualização de dashboard até 60s (RNF-01 + RF-01)  
**Constraints**: Sem overbooking; check-in exige reserva confirmada; check-out exige check-in prévio; reserva cancelada não reativa; uma estadia ativa por animal; registo obrigatório de método/estado de pagamento; operações críticas auditáveis  
**Scale/Scope**: Fluxo operacional da receção e direção para Fase 3, com evolução posterior em Fase 4/5

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Status | Evidence |
|-----------|--------|----------|
| **Domain Scope First** | OK | O plano limita-se a reservas, estadias e pagamentos do hotel de animais (cães e gatos), sem expansão indevida de escopo. |
| **Scenario-Driven Requirements** | OK | A spec define cenários Given/When/Then e critérios mensuráveis (SC-001..SC-010). |
| **Modular Separation of Concerns** | OK | Implementação em camadas `controller/service/repository/model`, mantendo contratos explícitos por fluxo. |
| **Verification Before Expansion** | OK | A feature inclui requisitos explícitos de testes por US P1 e por UC principal, antes de avançar para novas fases. |
| **Data Integrity, Security, and Operational Reliability** | OK | RD-01..RD-09 e RNF-04/RNF-05 estão mapeados a validações de domínio, permissões por perfil e rastreabilidade de transações. |

**Resultado (pré-design)**: Nenhuma violação da constituição.

## Project Structure

### Documentation (this feature)

```text
specs/003-reservas-estadias-pagamentos/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   ├── reservas-estadias-pagamentos.md
│   └── dashboard-historico.md
└── tasks.md
```

### Source Code (repository root)

```text
PatasBigodesApp/
├── src/
│   ├── main/
│   │   ├── java/pt/hotel/animais/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── model/
│   │   │   ├── model/enums/
│   │   │   └── dto/
│   │   └── resources/
│   │       ├── db/migration/
│   │       └── templates/
│   └── test/java/pt/hotel/animais/
│       ├── controller/
│       ├── service/
│       └── integration/
└── pom.xml
```

**Structure Decision**: Reutilizar a estrutura MVC monolítica existente no projeto, estendendo os módulos já criados para as capacidades de estadia e pagamento sem introduzir novos subprojetos.

## Phase 0: Research & Clarifications

### Unknowns identified and resolved

- Estratégia de pagamento em dois momentos (check-in/check-out) e regras de estado: resolvido com RF-10 + RD-04.
- Regras de transição entre reserva/estadia/alojamento: resolvido com RD-01, RD-02, RD-03, RD-06, RD-07.
- Cobertura mínima de testes obrigatórios para P1: resolvido com secção de `Validation & Test Requirements` na spec.

### Output

- `research.md` com decisões, racional e alternativas para arquitetura, domínio, integrações e estratégia de testes.

## Phase 1: Design & Contracts

### 1. Domain model

- `data-model.md` define entidades `Reserva`, `Estadia`, `Pagamento`, e impacto em `Alojamento`, incluindo atributos, validações e transições de estado.
- As regras transversais de domínio e auditoria devem ser centralizadas num serviço partilhado com interface e implementação (por exemplo, `IRegraDominioService` e `RegraDominioService`) para evitar duplicação de regras entre serviços.

### 2. Interface contracts

- `contracts/reservas-estadias-pagamentos.md`: contratos dos fluxos de receção (disponibilidade, reserva, confirmação, cancelamento, check-in, check-out, pagamento).
- `contracts/dashboard-historico.md`: contratos de consulta para direção e receção (indicadores, pendentes, histórico).

### 3. Quickstart

- `quickstart.md` com passos de execução local, validação automática e testes funcionais manuais dos fluxos críticos.
- A validação final inclui evidências para desempenho (SC-001/SC-002/SC-006), autorização por perfil, confidencialidade e auditoria.

### 4. Agent context update

Atualizar `.github/copilot-instructions.md` para referenciar:

```markdown
<!-- SPECKIT START -->
For additional context about technologies to be used, project structure,
shell commands, and other important information, read:
specs/003-reservas-estadias-pagamentos/plan.md
<!-- SPECKIT END -->
```

## Post-Design Constitution Re-check

| Principle | Status | Evidence |
|-----------|--------|----------|
| **Domain Scope First** | OK | Artefactos gerados mantêm foco exclusivo em reservas/estadias/pagamentos. |
| **Scenario-Driven Requirements** | OK | Contratos e quickstart refletem os cenários Given/When/Then da spec. |
| **Modular Separation of Concerns** | OK | Separação clara entre regras de domínio, contratos e guias operacionais. |
| **Verification Before Expansion** | OK | Plano impõe testes por US P1, RD críticos e UCs principais. |
| **Data Integrity, Security, and Operational Reliability** | OK | Regras de consistência e permissões estão incorporadas no modelo e nos contratos. |

**Resultado (pós-design)**: Gate mantido sem violações.

## Complexity Tracking

Sem exceções: não há violações da constituição a justificar.
