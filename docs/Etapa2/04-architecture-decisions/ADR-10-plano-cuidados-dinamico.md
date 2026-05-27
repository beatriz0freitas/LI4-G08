# ADR-10 - Plano de cuidados dinâmico associado a animal e estadia

**Estado:** Aceite
**Data:** 2026-05-27
**Decisores:** Equipa LI4-G08
**Contexto:** Etapa 2 - Arquitetura e Design; speckit 004

## Contexto
O módulo de cuidados e clínica precisa de acompanhar tarefas previstas, tarefas executadas e alterações do estado de saúde durante a estadia. O speckit 004 define que o plano de cuidados deve ser dinâmico, consultável por turno e ajustável quando a condição clínica do animal muda.

O plano também deve manter ligação ao animal para histórico longitudinal, sem perder a ligação operacional à estadia em curso.

## Decisão
Modelar o plano de cuidados como entidade própria `PlanoCuidados`, associada simultaneamente a `Animal` e `Estadia`, e decompor o trabalho previsto em `TarefaCuidado`.

Cada estadia tem no máximo um plano ativo. O plano é criado ou obtido no fluxo operacional da estadia e pode ser enriquecido por tarefas com periodicidade, prioridade e instrução textual. Alterações de estado de saúde e registos clínicos podem originar atualização do plano através da camada de serviço.

## Alternativas consideradas
- Guardar o plano apenas como texto livre na estadia, rejeitado por não permitir tarefas, progresso nem consulta por turno.
- Associar o plano apenas ao animal, rejeitado por perder o contexto operacional da estadia concreta.
- Gerar tarefas apenas em memória para o dia atual, rejeitado por comprometer rastreabilidade e continuidade entre turnos.

## Consequências
### Positivas
- Os cuidadores conseguem consultar trabalho pendente e progresso por plano.
- O histórico do animal conserva planos anteriores associados a estadias específicas.
- A equipa clínica pode influenciar os cuidados operacionais sem duplicar estruturas.

### Negativas
- Os fluxos de check-in, alteração de saúde e cuidados ficam mais acoplados ao serviço de plano.
- É necessário garantir unicidade operacional do plano ativo por estadia.

## Impacto na arquitetura
- [architecture.md](../01-architecture/architecture.md)
- [components.mmd](../01-architecture/components.mmd)
- [class-diagram.mmd](../02-class-diagram/class-diagram.mmd)
- Implementação: `PlanoCuidados`, `TarefaCuidado`, `PlanoCuidadosService`, `AlteracaoEstadoSaudeService`, `RegistoCuidadoService`, `EstadiaService`.

## Rastreabilidade
- Requisitos: RF-11, RF-12, RF-13, RF-14, RD-10.
- Speckits: `specs/004-cuidados-clinica-limpeza/`.
- Decisões dependentes: ADR-01, ADR-03, ADR-04, ADR-06, ADR-08.

## Conformidade com a implementação atual
- `PlanoCuidadosRepository` permite procurar plano por estadia e listar planos ativos.
- `PlanoCuidadosService` cria ou obtém o plano associado a uma estadia ativa e gere tarefas.
- `EstadiaService` integra a criação do plano no fluxo operacional de estadia.
- `AlteracaoEstadoSaudeService` tem dependência explícita do serviço de plano, refletindo a atualização por contexto clínico.

## Status atual
- [ ] Proposta
- [x] Aprovada
- [x] Implementada
- [ ] Validada
