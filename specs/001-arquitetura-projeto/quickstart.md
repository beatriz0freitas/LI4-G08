# Quickstart: arquitetura-projeto

## Objetivo

Gerar e validar os artefactos arquiteturais da feature `001-arquitetura-projeto` com rastreabilidade para UC-01..UC-13.

## Pre-requisitos

- Repositorio atualizado
- Mermaid disponivel no ambiente de edicao
- PlantUML disponivel para exportacao/import
- Visual Paradigm para validacao de `.txt` PlantUML

## Passo 1 - Rever contratos

1. Ler `specs/001-arquitetura-projeto/spec.md`
2. Ler `specs/001-arquitetura-projeto/contracts/class-diagram-contract.md`
3. Ler `specs/001-arquitetura-projeto/contracts/sequence-diagram-contract.md`
4. Ler `specs/001-arquitetura-projeto/contracts/component-diagram-contract.md`

## Passo 2 - Produzir diagrama de classes

1. Atualizar `docs/architecture/diagramas/classes.mmd`
2. Verificar:
   - interfaces + implementacoes de services/repositorios
   - metodos minimos de repository
   - cardinalidades obrigatorias
   - enums obrigatorias
   - excecoes de dominio

## Passo 3 - Produzir diagramas de sequencia

1. Produzir/atualizar os 7 fluxos obrigatorios em `.mmd`
2. Produzir pares equivalentes em `.txt` PlantUML
3. Validar mapeamento minimo:
   - UC-04 -> seq-reserva
   - UC-06 -> seq-checkin
   - UC-07 -> seq-checkout
   - UC-09 -> seq-cuidados
   - UC-08 -> seq-faturacao
   - UC-12 -> seq-limpeza
   - UC-11 -> seq-veterinario

## Passo 4 - Produzir diagrama de componentes

1. Atualizar `docs/architecture/diagramas/componentes.mmd`
2. Verificar componentes minimos e camadas obrigatorias

## Passo 5 - Validacao

1. Confirmar importacao dos `.txt` de sequencia no Visual Paradigm sem erros
2. Confirmar checklist `specs/001-arquitetura-projeto/checklists/requirements.md`
3. Confirmar ADRs em `docs/architecture/decisoes/`

## Resultado esperado

- Artefactos UML completos e coerentes
- Rastreabilidade entre requisitos, use cases, metodos e diagramas
- Documentacao pronta para `/speckit.tasks`

## Inventario Final Esperado

- `docs/requirements/use-cases/traceability.md`
- `docs/architecture/diagramas/classes.mmd`
- `docs/architecture/diagramas/componentes.mmd`
- `docs/architecture/diagramas/seq-reserva.mmd` e `seq-reserva.txt`
- `docs/architecture/diagramas/seq-checkin.mmd` e `seq-checkin.txt`
- `docs/architecture/diagramas/seq-checkout.mmd` e `seq-checkout.txt`
- `docs/architecture/diagramas/seq-cuidados.mmd` e `seq-cuidados.txt`
- `docs/architecture/diagramas/seq-faturacao.mmd` e `seq-faturacao.txt`
- `docs/architecture/diagramas/seq-limpeza.mmd` e `seq-limpeza.txt`
- `docs/architecture/diagramas/seq-veterinario.mmd` e `seq-veterinario.txt`
