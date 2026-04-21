# Tasks: arquitetura-projeto

## Phase 1: Setup
- [x] T001 Criar diretórios para diagramas e decisões em docs/architecture/

## Phase 2: Fundacional
- [ ] T002 Instalar/extender suporte a PlantUML e Mermaid para geração de diagramas
- [ ] T003 Definir convenções de nomeação e organização dos ficheiros de diagramas

## Phase 3: User Story 1 (P1) - Visualizar Arquitetura
- [x] T004 [P] [US1] Criar diagrama de classes completo em docs/architecture/diagramas/classes.mmd
- [x] T005 [P] [US1] Criar diagrama de sequência de reserva em docs/architecture/diagramas/sequencia-reserva.mmd
- [x] T006 [P] [US1] Criar diagrama de sequência de check-in em docs/architecture/diagramas/sequencia-checkin.mmd
- [x] T007 [P] [US1] Criar diagrama de sequência de check-out em docs/architecture/diagramas/sequencia-checkout.mmd
- [x] T008 [P] [US1] Criar diagrama de sequência de cuidados em docs/architecture/diagramas/sequencia-cuidados.mmd
- [x] T009 [P] [US1] Criar diagrama de sequência de faturação em docs/architecture/diagramas/sequencia-faturacao.mmd
- [x] T010 [P] [US1] Criar diagrama de sequência de limpeza em docs/architecture/diagramas/sequencia-limpeza.mmd
- [x] T011 [P] [US1] Criar diagrama de sequência de intervenção do veterinário em docs/architecture/diagramas/sequencia-veterinario.mmd
- [x] T012 [P] [US1] Criar diagrama de componentes em docs/architecture/diagramas/componentes.mmd
- [ ] T013 [US1] Rever diagramas com equipa e validar clareza e cobertura

## Phase 4: User Story 2 (P2) - Atualizar Diagramas
- [ ] T014 [US2] Documentar processo de atualização de diagramas em docs/architecture/decisoes/atualizacao.md
- [ ] T015 [US2] Simular alteração num componente e atualizar diagramas correspondentes
- [ ] T016 [US2] Rever documentação de atualização com equipa

## Phase 5: User Story 3 (P3) - Exportar Diagramas
- [ ] T017 [P] [US3] Exportar diagramas para imagem/PDF em docs/architecture/diagramas/export/
- [ ] T018 [US3] Validar legibilidade dos diagramas exportados
- [ ] T019 [US3] Documentar processo de exportação em docs/architecture/decisoes/exportacao.md

## Phase 6: Polish & Cross-Cutting
- [ ] T020 Rever e garantir que todos os diagramas e decisões estão versionados e acessíveis
- [ ] T021 Atualizar README.md com referência à documentação de arquitetura

## Dependencies
- T001 → T002, T003
- T002, T003 → T004, T005, T006, T007, T008, T009, T010, T011, T012
- T004, T005, T006, T007, T008, T009, T010, T011, T012 → T013
- T013 → T014, T015
- T014, T015 → T016
- T004, T005, T006, T007, T008, T009, T010, T011, T012 → T017
- T017 → T018, T019
- Todos → T020, T021

## Parallel Execution Examples
- T004 a T012 podem ser feitos em paralelo após setup
- T017 pode ser feito em paralelo para cada diagrama

## MVP Scope
- Fase até T007 cobre o MVP: diagramas principais criados e validados

## Independent Test Criteria
- Cada diagrama pode ser validado isoladamente por revisão visual e checklist
- Atualização e exportação testadas por simulação e validação de legibilidade

## Format Validation
- Todos os tasks seguem o formato checklist estrito
