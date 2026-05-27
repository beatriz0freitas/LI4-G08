# ADR-05 - Autenticação e autorização com controlo de acesso por perfil

**Estado:** Aceite
**Data:** 2026-04-25
**Decisores:** Equipa LI4-G08
**Contexto:** Etapa 2 - Arquitetura e Design

## Contexto
O sistema suporta diferentes perfis de utilizador, nomeadamente Diretor, Rececionista, Cuidador, Médico Veterinário e Responsável pela Limpeza. Cada perfil possui responsabilidades e níveis de acesso distintos. O requisito RNF-04 exige autenticação individual e controlo de permissões por perfil.

## Decisão
Utilizar autenticação por formulário e autorização por perfil, com credenciais persistidas em `Colaborador` e carregadas a partir da base de dados. As permissões são aplicadas de forma centralizada em `SecurityConfig`, complementadas por validações de método quando necessário, e refletidas na camada de apresentação, garantindo que cada utilizador apenas acede às funcionalidades adequadas ao seu perfil.

## Alternativas consideradas
- Autorização apenas na interface, rejeitada por permitir duplicação de regras e por não garantir proteção suficiente caso existam acessos diretos a operações internas.
- Controlo de acesso baseado em atributos, rejeitado por introduzir complexidade adicional sem necessidade face aos perfis identificados.
- Utilizadores definidos apenas em memória, rejeitados por não permitirem gestão funcional de colaboradores nem rastreabilidade individual persistente.

## Consequencias
### Positivas
- Controlo de acesso centralizado.
- Credenciais e perfis mantidos na base de dados, coerentes com a gestão de colaboradores.
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

## Conformidade com a implementação atual
- `SecurityConfig` define as regras por rota e perfil.
- `ColaboradorUserDetailsService` obtém o utilizador autenticável a partir de `ColaboradorRepository`.
- `Colaborador` guarda o papel funcional através de `TipoColaborador` e a palavra-passe com hash BCrypt.
- Os testes MVC importam `SecurityConfig`, cobrindo a autorização das áreas sensíveis.

## Status atual
- [ ] Proposta
- [x] Aprovada
- [x] Implementada
- [ ] Validada
