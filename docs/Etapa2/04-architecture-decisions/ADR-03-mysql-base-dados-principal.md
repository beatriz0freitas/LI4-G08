# AD-03 - MySQL como Base de Dados Principal

**Estado:** Aceite
**Data:** 2026-04-25
**Decisores:** Equipa LI4-G08
**Contexto:** Etapa 2 - Arquitetura e Design

## Contexto
O sistema precisa de persistencia relacional para reservas, estadias, pagamentos e historico clinico, com simplicidade operacional e suporte local.

## Decisao
Adotar MySQL 8 como base de dados principal.

## Alternativas consideradas
- Alternativa 1: PostgreSQL como BD principal.
- Alternativa 2: SQLite para simplificar instalacao local.

## Consequencias
### Positivas
- Familiaridade da equipa e maturidade da stack com Spring Data JPA.
- Boa compatibilidade com ambiente local e scripts de backup.
- Transicoes e integridade referencial adequadas ao dominio.

### Negativas
- Dependencia de instalacao/configuracao de servidor MySQL em producao.
- Potencial lock-in de dialeto SQL se houver queries especificas.

## Impacto na arquitetura
- `docs/Etapa2/01-architecture/architecture.md`
- `docs/Etapa2/01-architecture/deployment.mmd`
- `docs/Etapa2/02-class-diagram/class-diagram.mmd`

## Rastreabilidade
- RF/RD/RNF/UC relacionados: RNF-01, RNF-08, RD-01, RD-04, UC-04, UC-06, UC-07, UC-08.
- Decisoes dependentes: AD-01.

## Status atual
- [ ] Proposta
- [x] Aprovada
- [ ] Implementada
- [ ] Validada
