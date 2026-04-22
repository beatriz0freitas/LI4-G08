# ADR-003: Consolidacao de Services por coesao funcional

## Contexto

A analise identificou risco de granularidade excessiva e sobreposicao de responsabilidades em services.

## Problema

Determinar fronteiras de service que maximizem coesao e minimizem acoplamento acidental.

## Alternativas consideradas

### A. Manter todos os services atomizados

- Pro: aparente separacao fina
- Contra: aumento de coordenacao e dependencias cruzadas

### B. Consolidar services com mesma razao de mudanca

- Pro: menor ruido arquitetural
- Pro: ownership mais claro
- Pro: melhor alinhamento com casos de uso

## Decisao

Adotar alternativa B com estrutura alvo:
- UtilizadorService
- AnimalService
- ClinicaService
- AlojamentoService (inclui limpeza)
- ReservaService
- EstadiaService (inclui cuidados, notas e servicos extra)
- FaturacaoService
- PagamentoService
- RelatorioService

## Consequencias

- Menor custo de manutencao
- Melhor legibilidade arquitetural
- Menor risco de responsabilidade difusa
- Reducao da malha de dependencias entre services
- Modelo mais claro para geracao do diagrama UML

## Fundamentacao em Sommerville

A decomposicao deve seguir coesao e razoes de mudanca. Componentes com forte dependencia funcional e operacional devem ser agrupados para evitar fragmentacao artificial.
