# ADR-05 - Autenticação e autorização com controlo de acesso por perfil

**Estado:** Aceite
**Data:** 2026-04-25
**Decisores:** Equipa LI4-G08
**Contexto:** Etapa 2 - Arquitetura e Design

## Contexto
O sistema suporta diferentes perfis de utilizador, nomeadamente Diretor, Rececionista, Cuidador, Médico Veterinário e Responsável pela Limpeza. Cada perfil possui responsabilidades e níveis de acesso distintos. O requisito RNF-04 exige autenticação individual e controlo de permissões por perfil.

## Decisão
Utilizar o Spring Security para implementar a autenticação e autorização do sistema, com controlo de acesso baseado em perfis de utilizador. As permissões são aplicadas de forma centralizada na camada de aplicação e refletidas na camada de apresentação, garantindo que cada utilizador apenas acede às funcionalidades adequadas ao seu perfil. 

## Alternativas consideradas
- Autorização apenas na interface, rejeitada por permitir duplicação de regras e por não garantir proteção suficiente caso existam acessos diretos a operações internas.
- Controlo de acesso baseado em atributos, rejeitado por introduzir complexidade adicional sem necessidade face aos perfis identificados.

## Consequencias
### Positivas
- Controlo de acesso centralizado.
- Maior coerência na aplicação de permissões.
- Melhor proteção de dados pessoais e clínicos.

### Negativas
- A adição de novos perfis pode exigir atualização das regras de autorização e das vistas associadas.

## Impacto na arquitetura
- [architecture.md](../01-architecture/architecture.md)
- [components.mmd](../01-architecture/components.mmd)
- [UC-01.mmd](../03-seq-diagrams/UC-01.mmd)

## Rastreabilidade
- RF/RD/RNF/UC relacionados: RNF-04, RNF-05, UC-01.
- Decisões dependentes: ADR-01, ADR-02.

## Status atual
- [ ] Proposta
- [x] Aprovada
- [ ] Implementada
- [ ] Validada
