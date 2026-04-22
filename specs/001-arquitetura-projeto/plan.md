# Implementation Plan: arquitetura-projeto

**Branch**: `[001-arquitetura-projeto]` | **Date**: 2026-04-22 | **Spec**: [specs/001-arquitetura-projeto/spec.md](specs/001-arquitetura-projeto/spec.md)
**Input**: Feature specification from `/specs/001-arquitetura-projeto/spec.md`

## Summary

Consolidar a documentacao arquitetural do sistema de gestao de hotel para animais com UML 2.5 estrita, cobrindo classes, sequencia e componentes, e garantindo rastreabilidade entre requisitos, use cases (UC-01..UC-13), metodos de service/dominio e diagramas.

Abordagem tecnica:

- Definir contrato de classes com interfaces, metodos e cardinalidades obrigatorias
- Definir contrato de sequencia por fluxo de negocio (`.mmd` + `.txt`)
- Definir contrato de componentes por modulos e camadas
- Registar decisoes em ADRs fundamentadas em Sommerville

## Technical Context

**Language/Version**: Markdown + Mermaid + PlantUML (documentacao, sem runtime)  
**Primary Dependencies**: Mermaid, PlantUML, Visual Paradigm (import PlantUML), Git  
**Storage**: Ficheiros versionados no repositorio (`docs/`, `specs/`)  
**Testing**: Checklist de conformidade + revisao por pares + validacao de importacao PlantUML  
**Target Platform**: Repositorio GitHub + ferramentas locais de render/import
**Project Type**: Documentacao de arquitetura de software  
**Performance Goals**: Renderizacao sem erros e leitura clara pela equipa em revisao < 10 min por diagrama principal  
**Constraints**: UML 2.5 estrita, sem simbolos/estilos nao standard, sem HTTP nos diagramas de sequencia de negocio  
**Scale/Scope**: 1 sistema de hotel para animais (caes e gatos), 9 services de negocio, UC-01..UC-13, 7 fluxos de sequencia obrigatorios

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Pre-Phase 0 Gate Review

- Domain Scope First: PASS
  - Escopo mantido em hotel para animais (caes e gatos), sem expansao de dominio fora da release.
- Scenario-Driven Requirements: PASS
  - Spec inclui user stories, cenarios e mapeamento para use cases.
- Modular Separation of Concerns: PASS
  - Estrutura modular por dominio/services/repositories/factory/strategy/excecoes.
- Verification Before Expansion: PASS
  - Existem criterios de sucesso mensuraveis e checklist de requisitos.
- Data Integrity, Security, and Operational Reliability: PASS
  - Regras criticas e excecoes de dominio explicitas.

### Post-Phase 1 Design Re-check

- PASS: `research.md`, `data-model.md`, `contracts/` e `quickstart.md` mantem rastreabilidade e separacao modular.
- PASS: Contratos de sequencia e classes estao alinhados com UC-01..UC-13 e FR-035..FR-037.

## Project Structure

### Documentation (this feature)

```text
specs/001-arquitetura-projeto/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   ├── class-diagram-contract.md
│   ├── sequence-diagram-contract.md
│   └── component-diagram-contract.md
└── tasks.md
```

### Source Code (repository root)

```text
docs/
├── architecture/
│   ├── diagramas/
│   └── decisoes/
└── requirements/
    ├── domain/
    ├── functional/
    ├── non-functional/
    └── use-cases/

specs/
└── 001-arquitetura-projeto/

README.md
```

**Structure Decision**: Manter o trabalho arquitetural dividido entre `specs/001-arquitetura-projeto/` (artefactos de planeamento e contratos) e `docs/architecture/` + `docs/requirements/use-cases/` (fonte oficial de diagramas e requisitos operacionais), para garantir rastreabilidade clara entre definicao e execucao.

## Complexity Tracking

Sem violacoes da constituicao que exijam excecao formal nesta fase.
