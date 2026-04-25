# AD-01 - Monolito em Camadas

**Estado:** Aceite
**Data:** 2026-04-25
**Decisores:** Equipa LI4-G08
**Contexto:** Etapa 2 - Arquitetura e Design

## Contexto
O sistema e destinado a um hotel de animais com baixa concorrencia (ate 10 utilizadores simultaneos), operacao em rede local e prazo academico curto para implementacao.

## Decisao
Adotar arquitetura monolitica em camadas (Apresentacao, Aplicacao, Dominio, Dados).

## Alternativas consideradas
- Alternativa 1: Microservicos por dominio funcional.
- Alternativa 2: Modular monolith com fronteiras de modulos mais estritas desde o inicio.

## Consequencias
### Positivas
- Reduz complexidade de deploy e operacao para o contexto academico.
- Acelera desenvolvimento e testes integrados na Etapa 3.
- Simplifica rastreabilidade entre casos de uso e implementacao.

### Negativas
- Escalabilidade horizontal mais limitada face a microservicos.
- Acoplamento maior entre modulos se nao houver disciplina de camadas.

## Impacto na arquitetura
- `docs/Etapa2/01-architecture/architecture.md`
- `docs/Etapa2/01-architecture/components.mmd`
- `docs/Etapa2/01-architecture/deployment.mmd`
- `docs/Etapa2/02-class-diagram/class-diagram.mmd`

## Rastreabilidade
- RF/RD/RNF/UC relacionados: RNF-01, RNF-06, RNF-07, UC-01..UC-13.
- Decisoes dependentes: AD-02, AD-03, AD-04, AD-05.

## Status atual
- [ ] Proposta
- [x] Aprovada
- [ ] Implementada
- [ ] Validada
