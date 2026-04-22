# Decisoes de Arquitetura

Esta pasta guarda os ADRs (Architecture Decision Records) do projeto.

## Regra obrigatoria

Toda decisao arquitetural relevante deve ser registada aqui e fundamentada em Ian Sommerville.

## Estrutura minima de cada ADR

1. Contexto
2. Problema
3. Alternativas consideradas
4. Decisao
5. Consequencias
6. Fundamentacao em Sommerville

## Convencao de nome

`ADR-XXX-titulo-curto.md`

Exemplo: `ADR-001-services-vs-facades.md`

## ADRs existentes

- `ADR-001-services-vs-facades.md`
- `ADR-002-interfaces-e-contratos.md`
- `ADR-003-consolidacao-services.md`
- `ADR-004-padroes-factory-strategy-observer.md`

## Relacao com o spec

Os requisitos de rastreabilidade e fundamentacao arquitetural do feature estao definidos no spec:
- `FR-023` em `specs/001-arquitetura-projeto/spec.md`
- secao "Registo de Decisoes (obrigatorio)" em `specs/001-arquitetura-projeto/research.md`
