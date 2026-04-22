# ADR-004: Padroes obrigatorios para Faturacao e Mudanca de Estado

## Contexto

A analise identificou acoplamento excessivo no fluxo de faturacao e falta de mecanismo explicito para reacao a mudancas de estado de alojamento.

## Problema

Definir como representar no diagrama e na arquitetura:
- construcao de fatura complexa
- variacao de algoritmo de calculo
- notificacao de mudanca de estado

## Decisao

Aplicar os seguintes padroes:

1. Factory Method
- Classe: `FaturaFactory`
- Responsabilidade: construir `Fatura` a partir de `Estadia` e custos agregados.

2. Strategy
- Interface: `EstrategiaCalculoFatura`
- Implementacao base: `CalculoStandard`
- Responsabilidade: isolar algoritmo de calculo de faturacao.

3. Observer
- Interface: `AlojamentoStateListener`
- Evento chave: transicao de estado para pendente de limpeza
- Responsabilidade: desacoplar notificacoes operacionais da mudanca de estado.

## Regras normativas para o diagrama

- `FaturacaoService` depende de `FaturaFactory`, nao construi `Fatura` diretamente.
- `FaturaFactory` depende de `EstrategiaCalculoFatura`.
- Mudanca de estado de `Alojamento` para pendente de limpeza dispara notificacao via observer.

## Consequencias

- Menor acoplamento no modulo financeiro
- Melhor extensibilidade de regras de calculo
- Melhor rastreabilidade operacional no fluxo de limpeza

## Fundamentacao em Sommerville

A separacao de responsabilidades, isolamento de variacoes e acoplamento baixo entre componentes sao principios centrais do design arquitetural modular em Sommerville.