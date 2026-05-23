# ADR-04 - Persistência de dados com MySQL e padrão repositório

**Estado:** Aceite
**Data:** 2026-04-25
**Decisores:** Equipa LI4-G08
**Contexto:** Etapa 2 - Arquitetura e Design

## Contexto
Após a decisão de utilizar um SGBD relacional, foi necessário selecionar a tecnologia concreta de persistência e a forma de acesso aos dados. O sistema exige persistência fiável, suporte a transações, integridade referencial e testes de integração próximos do ambiente real de execução.

## Decisão
Adotar MySQL como SGBD do sistema e utilizar o padrão repositório para organizar o acesso aos dados. A persistência é suportada por Spring Data JPA, permitindo centralizar consultas e operações sobre as entidades do domínio. O MySQL é também utilizado nos testes de integração, através de uma base de dados própria para testes.

## Alternativas consideradas
- PostgreSQL, considerada viável, mas rejeitada por não apresentar vantagens decisivas no contexto do projeto e por a equipa estar mais familiarizada com MySQL.
- Base de dados em memória para testes, rejeitada nos testes de integração, uma vez que poderia introduzir diferenças de comportamento face ao SGBD utilizado pela aplicação.

## Consequencias
### Positivas
- Validação das consultas, transações e restrições no mesmo SGBD usado pela aplicação.
- Ambiente de testes mais próximo do ambiente real.
- Menor risco de discrepâncias entre teste e execução.

### Negativas
- Os testes de integração dependem da disponibilidade de uma instância MySQL e de uma base de dados separada para testes.

## Impacto na arquitetura
- [architecture.md](../01-architecture/architecture.md)
- [deployment.mmd](../01-architecture/deployment.mmd)
- [class-diagram.mmd](../02-class-diagram/class-diagram.mmd)

## Rastreabilidade
- RF/RD/RNF/UC relacionados: RNF-01, RNF-06, RNF-07, UC-01..UC-13.
- Decisões dependentes: ADR-03, ADR-07.

## Status atual
- [ ] Proposta
- [x] Aprovada
- [ ] Implementada
- [ ] Validada
