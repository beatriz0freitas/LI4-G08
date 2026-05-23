# ADR-01 - Arquitetura em camadas numa aplicação centralizada

**Estado:** Aceite
**Data:** 2026-04-25
**Decisores:** Equipa LI4-G08
**Contexto:** Etapa 2 - Arquitetura e Design

## Contexto
O sistema destina-se a um único estabelecimento, com até dez utilizadores simultâneos, e um prazo de implementação e uma equipa de desenvolvimento reduzidos. Os requisitos identificados exigem uma solução simples de disponibilizar, com controlo centralizado de autenticação, permissões e dados pessoais.

## Decisão
Adotar uma arquitetura em camadas numa aplicação web única e centralizada. A solução foi organizada em quatro camadas principais: apresentação, aplicação, domínio e dados. A camada de apresentação suporta a interação com os utilizadores; a camada de aplicação coordena os casos de uso e aplica as regras de negócio; a camada de domínio representa os principais conceitos do sistema; e a camada de dados centraliza o acesso persistente à informação.

## Alternativas consideradas
- Microserviços, rejeitados por introduzirem complexidade de comunicação, coordenação e gestão operacional desproporcionada face à escala do sistema.
- Aplicação cliente instalada nos postos, rejeitada por exigir instalação e atualização individual em cada estação de trabalho, contrariando o objetivo de simplificar a manutenção.

## Consequencias
### Positivas
- Solução simples de desenvolver, testar e disponibilizar.
- Ausência de software específico nas estações de trabalho.
- Centralização das regras de negócio, segurança e persistência.

### Negativas
- Menor flexibilidade de escalabilidade horizontal face a soluções distribuídas, considerada aceitável para a dimensão atual do sistema.

## Impacto na arquitetura
- [architecture.md](../01-architecture/architecture.md)
- [components.mmd](../01-architecture/components.mmd)
- [deployment.mmd](../01-architecture/deployment.mmd)
- [class-diagram.mmd](../02-class-diagram/class-diagram.mmd)

## Rastreabilidade
- RF/RD/RNF/UC relacionados: RNF-01, RNF-06, RNF-07, UC-01..UC-13.
- Decisões dependentes: ADR-02, ADR-03, ADR-04, ADR-05, ADR-06, ADR-07.

## Status atual
- [ ] Proposta
- [x] Aprovada
- [ ] Implementada
- [ ] Validada
