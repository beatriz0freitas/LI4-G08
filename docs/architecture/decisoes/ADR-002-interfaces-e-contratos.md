# ADR-002: Interfaces e Implementacoes Concretas como Contrato Arquitetural

## Contexto

O sistema precisa de manter baixo acoplamento e evolucao incremental, incluindo a possibilidade de integrar pagamentos externos no futuro sem alterar o nucleo da faturacao e estadia.

## Problema

Definir se os modulos de negocio devem depender de classes concretas ou de contratos estaveis.

## Alternativas consideradas

### A. Dependencia direta de implementacoes concretas

- Pro: menor esforco inicial
- Contra: propagacao de mudanca para consumidores
- Contra: dificuldade de substituicao e teste

### B. Dependencia por interfaces com implementacoes concretas separadas

- Pro: menor acoplamento estrutural
- Pro: substituicao de implementacao sem alterar consumidores
- Pro: melhor isolamento para testes e simulacoes

## Decisao

Adotar alternativa B como regra obrigatoria.

### Regras normativas (DO)

1. Todo service deve existir como interface e como implementacao concreta.
2. Todo repository deve existir como interface e como implementacao concreta.
3. Consumidores (controllers, outros services, facades) dependem apenas da interface.
4. O diagrama de classes arquitetural mostra interfaces de service/repository; 

### Regras normativas (DO NOT)

1. Nao injetar implementacoes concretas diretamente em componentes de negocio.
2. Nao criar interface vazia sem operacoes de negocio.
3. Nao acoplar consumidores a detalhes de persistencia/fornecedor externo.

## Convencao obrigatoria de nome

- Interface de service: `XxxService`
- Implementacao de service: `XxxServiceImpl`
- Interface de repository: `XxxRepository`
- Implementacao de repository: `XxxRepositoryImpl`

## Contrato minimo por tipo

### Service interface

- Operacoes de caso de uso
- Excecoes de dominio
- Sem detalhes de infraestrutura

### Repository interface

- Metodos de query relevantes do negocio
- Sem SQL/raw query no contrato UML

## Caso especifico de pagamento e integracao externa

`PagamentoService` deve ser tratado como ponto de extensao:

- Implementacao inicial: registo interno de pagamento (`RegistoPagamentoServiceImpl`)
- Implementacao futura: adaptador externo (`MBWayPagamentoServiceImpl`, `TerminalPagamentoServiceImpl`)

Os consumidores de pagamento nao mudam, pois dependem apenas de `PagamentoService`.

## Facade vs Service (limite arquitetural)

- Service: encapsula logica de negocio de dominio
- Facade: simplifica composicao para cliente externo

Regra: facade nao substitui service interno; facade orquestra services quando necessario na fronteira externa.

## Consequencias

- Reducao de impacto de mudancas locais
- Melhor evolucao para integracoes futuras
- Maior disciplina de modelacao e naming

## Fundamentacao em Sommerville

A decisao segue os principios de modularidade, ocultacao de informacao e definicao de interfaces estaveis discutidos por Sommerville no design arquitetural. O componente deve ser definido pelo contrato observado externamente, nao pela implementacao interna.
