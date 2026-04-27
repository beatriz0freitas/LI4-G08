# AD-04 - Spring Security com Sessao HTTP

**Estado:** Aceite
**Data:** 2026-04-25
**Decisores:** Equipa LI4-G08
**Contexto:** Etapa 2 - Arquitetura e Design

## Contexto
O sistema e interno, com autenticacao de colaboradores por perfil e navegacao web tradicional. O risco principal e acesso indevido a dados operacionais e pessoais.

## Decisao
Adotar Spring Security com autenticacao por formulario, sessao HTTP e passwords em BCrypt.

## Alternativas consideradas
- Alternativa 1: JWT stateless.
- Alternativa 2: Autenticacao custom sem framework de seguranca.

## Consequencias
### Positivas
- Implementacao mais simples e robusta para aplicacao SSR interna.
- Integracao nativa com autorizacao por roles e filtros de seguranca.
- Boa cobertura de mecanismos padrao (CSRF, invalidacao de sessao, hashing).

### Negativas
- Menos adequado para clientes API externos e distribuicao de tokens.
- Dependencia da gestao correta de sessao em servidor.

## Impacto na arquitetura
- `docs/Etapa2/01-architecture/architecture.md`
- `docs/Etapa2/01-architecture/components.mmd`
- `docs/Etapa2/03-seq-diagrams/UC-01.mmd`

## Rastreabilidade
- RF/RD/RNF/UC relacionados: RNF-04, RNF-05, UC-01.
- Decisoes dependentes: AD-01, AD-02.

## Status atual
- [ ] Proposta
- [x] Aprovada
- [ ] Implementada
- [ ] Validada
