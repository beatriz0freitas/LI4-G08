# ADR-001: Services como abstracao principal e Facades apenas em fronteiras externas

## Contexto

O sistema atual e um gestor de hotel para animais com escala moderada:
- equipa pequena
- unico estabelecimento
- sem processamento externo de pagamento nesta fase
- dominio coeso e centralizado

## Problema

Definir se a arquitetura interna deve usar:
1. subsistemas com facades genericamente
2. services coesos como abstracao principal

## Alternativas consideradas

### A. Subsistemas com Facades genericamente

- Pro: interface unica para varios modulos
- Contra: camada adicional em quase todos os fluxos
- Contra: risco de over-engineering para escala atual

### B. Services coesos + Facades apenas na fronteira externa

- Pro: responsabilidades mais claras por caso de uso
- Pro: menor acoplamento acidental
- Pro: preserva simplicidade e manutenibilidade
- Contra: exige disciplina de contratos por interface

## Decisao

Adotar a alternativa B:
- services como abstracao principal interna
- facades apenas quando houver cliente externo que beneficie de simplificacao de composicao

## Consequencias

- Menor complexidade arquitetural no curto prazo
- Melhor alinhamento com consolidacao de services por coesao funcional
- Evolucao futura para integracoes externas continua viavel sem reestruturacao global

## Fundamentacao em Sommerville

A decisao segue principios de design arquitetural defendidos por Sommerville:
- decomposicao por coesao funcional
- separacao clara de responsabilidades
- ocultacao de informacao por contratos de interface
- evitar abstracoes adicionais sem justificacao de contexto

No contexto atual, a camada de service e suficiente para modularidade, e o uso de facade deve ser orientado por necessidade real de simplificacao na fronteira de integracao.
