# AD-02 - Spring MVC + Thymeleaf (SSR)

**Estado:** Aceite
**Data:** 2026-04-25
**Decisores:** Equipa LI4-G08
**Contexto:** Etapa 2 - Arquitetura e Design

## Contexto
A aplicacao requer UI web para perfis internos (rececao, cuidador, veterinario, limpeza e diretor), com fluxos transacionais claros e sem necessidade forte de interatividade SPA complexa.

## Decisao
Usar Spring MVC com renderizacao server-side (Thymeleaf) para a camada de apresentacao.

## Alternativas consideradas
- Alternativa 1: API REST + SPA React.
- Alternativa 2: API REST + SPA Angular.

## Consequencias
### Positivas
- Reduz complexidade de estado no frontend e sobrecarga de integracao.
- Melhor alinhamento com equipa e stack Java/Spring.
- Facilita seguranca de sessao e controlo de acesso por perfil.

### Negativas
- Menor flexibilidade para experiencias frontend altamente dinamicas.
- Possivel necessidade de refatoracao futura se a UI exigir interatividade rica.

## Impacto na arquitetura
- `docs/Etapa2/01-architecture/architecture.md`
- `docs/Etapa2/01-architecture/components.mmd`
- `docs/Etapa2/05-ui-mockup/`
- `docs/Etapa2/03-seq-diagrams/README.md`

## Rastreabilidade
- RF/RD/RNF/UC relacionados: RNF-02, RNF-04, UC-01..UC-13.
- Decisoes dependentes: AD-01.

## Status atual
- [ ] Proposta
- [x] Aprovada
- [ ] Implementada
- [ ] Validada
