# AD-05 - DTO entre Controller e Service

**Estado:** Aceite
**Data:** 2026-04-25
**Decisores:** Equipa LI4-G08
**Contexto:** Etapa 2 - Arquitetura e Design

## Contexto
A exposicao direta de entidades de dominio na camada web aumenta acoplamento, risco de serializacao indevida e dificulta validacao de input por caso de uso.

## Decisao
Usar DTOs para comunicacao entre Controllers e Services, mantendo entidades JPA encapsuladas na camada de dominio/dados.

## Alternativas consideradas
- Alternativa 1: Expor entidades JPA diretamente nos controllers.
- Alternativa 2: Usar entidades para leitura e DTO apenas para escrita.

## Consequencias
### Positivas
- Isola a camada de apresentacao da estrutura interna de persistencia.
- Facilita validacao por fluxo de UC e controlo de campos expostos.
- Suporta evolucao independente da API interna e do modelo de dados.

### Negativas
- Introduz codigo adicional de mapeamento.
- Requer disciplina para manter DTOs alinhados com requisitos.

## Impacto na arquitetura
- `docs/Etapa2/01-architecture/architecture.md`
- `docs/Etapa2/02-class-diagram/class-diagram.mmd`
- `docs/Etapa2/03-seq-diagrams/README.md`

## Rastreabilidade
- RF/RD/RNF/UC relacionados: RNF-02, RNF-05, UC-01..UC-13.
- Decisoes dependentes: AD-01, AD-02.

## Status atual
- [ ] Proposta
- [x] Aprovada
- [ ] Implementada
- [ ] Validada
