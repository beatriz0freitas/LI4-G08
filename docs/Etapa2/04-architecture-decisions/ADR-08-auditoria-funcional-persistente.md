# ADR-08 - Auditoria funcional persistente própria

**Estado:** Aceite
**Data:** 2026-05-27
**Decisores:** Equipa LI4-G08
**Contexto:** Etapa 2 - Arquitetura e Design

## Contexto
O requisito RF-19 exige consultar operações críticas, incluindo relatórios, com autor e momento. Existiam dois rastos: eventos Actuator em memória para relatórios e eventos persistidos `AuditoriaEvento` para os restantes fluxos. Esta duplicação impedia uma consulta completa e uma política de retenção única.

## Decisão
Registar todos os eventos funcionais através de `AuditoriaOperacaoService` e `IAuditoriaService`/`AuditoriaService`, persistindo-os na tabela `auditoria_evento`. O Actuator fica limitado aos endpoints operacionais `health` e `info`, sem `AuditEventRepository`, `AuditApplicationEvent` ou endpoint `auditevents`.

O evento `RELATORIO_GERADO` usa `entidade="Relatorio"` e `acao="READ"`. Como o resultado é calculado e não corresponde a um registo de domínio persistido, `entityId` é nulo nesse evento; mantém-se obrigatório na prática para eventos que alteram entidades identificáveis.

## Alternativas consideradas
- Manter Actuator para relatórios e auditoria própria nos restantes fluxos, rejeitada por criar dois históricos incompletos.
- Atribuir um identificador artificial a um relatório não persistido, rejeitada por prejudicar a semântica e a rastreabilidade.

## Consequências
- A consulta `/auditoria` e a retenção de 12 meses abrangem também relatórios.
- O esquema admite `entity_id` nulo para `RELATORIO_GERADO`, o evento sem entidade persistida atualmente previsto.
- A geração de relatórios necessita de transação apta a persistir o evento de auditoria.

## Rastreabilidade
- Requisitos: RF-19, RNF-09.
- Casos de uso: UC-13, UC-16.
- Implementação: `AuditoriaEvento`, `AuditoriaOperacaoService`, `AuditoriaService`, `RelatorioService`.
