# Modelo de Domínio - Hotel de Animais

Diagramas:
- [domain-model.mmd](domain-model.mmd)
- [modelo_dominio.plantuml](modelo_dominio.plantuml)

## Âmbito

O modelo de domínio representa os principais conceitos de negócio necessários para suportar reservas, estadias, pagamentos, cuidados, serviços extra, acompanhamento clínico, limpeza, perfis de colaboradores e auditoria. O objetivo é manter uma visão conceptual simples.

## Entidades do Domínio

| Entidade | Papel no domínio | Origem |
|---|---|---|
| `Tutor` | Pessoa responsável por animais e associada às reservas solicitadas. | US-08, US-11, RF-04, RD-05 |
| `Animal` | Animal registado no hotel, sempre associado a um tutor e com informação relevante para reservas, cuidados e histórico. | US-10, US-11, US-16, US-24, RD-05, RD-08 |
| `TipoAlojamentoTarifa` | Tipo de alojamento gerido pela direção, com espécie compatível, capacidade, tarifa diária e estado ativo. | US-06, RF-18, RD-01, RD-11 |
| `Alojamento` | Unidade física onde o animal fica alojado; a sua disponibilidade depende do estado de limpeza, compatibilidade e ocupação. | US-01, US-08, US-14, US-22, US-23, RF-06, RF-15, RD-01 |
| `Reserva` | Pedido confirmado para alojar um animal num período, antes de ser convertido em estadia. | US-08, RF-07, RD-02, RD-06 |
| `Estadia` | Execução real de uma reserva, iniciada no check-in e terminada no check-out. | US-09, RF-08, RF-09, RD-02, RD-03, RD-07 |
| `PlanoCuidados` | Plano ativo durante a estadia, com instruções e prioridade de acompanhamento do animal. | US-16, US-18, RF-11, RD-10 |
| `TarefaCuidado` | Tarefa prevista no plano de cuidados ou serviço opcional agendado para execução durante a reserva/estadia. | US-15, US-16, US-21, RF-11, RF-16, RD-10 |
| `RegistoCuidado` | Registo de cuidado prestado, com observações, autor e momento. | US-17, RF-12 |
| `Nota` | Nota operacional associada à reserva ou estadia, usada para continuidade entre colaboradores. | US-19, RF-05 |
| `TipoServicoExtra` | Tipo de serviço extra disponível em catálogo, gerido pela direção. | US-06, RF-18, RD-11 |
| `ServicoExtra` | Serviço extra efetivamente realizado durante uma estadia ativa e com custo associado. | US-20, RF-17, RD-09 |
| `IntervencaoClinica` | Intervenção ou prescrição veterinária registada durante uma estadia, podendo ter custo associado. | US-24, US-25, RF-14, RD-09 |
| `AlteracaoEstadoSaude` | Registo de alteração ao estado de saúde do animal, com severidade e data/hora. | US-18, US-26, RF-13 |
| `Pagamento` | Registo financeiro de valores liquidados ou pendentes no check-in ou check-out. | US-12, US-13, RF-10, RD-04 |
| `Colaborador` | Profissional do hotel com perfil de acesso e autoria sobre operações realizadas. | US-03, RF-02, RNF-04 |
| `RegistoAuditoria` | Registo de operação crítica realizada, identificando autor, momento, operação e resultado. | US-07, RF-19, RNF-09 |

## Relações do Domínio

| Relação | Multiplicidade | Justificação |
|---|---|---|
| `Tutor` responsável por `Animal` | `1` para `0..*` | Um tutor pode ter vários animais; cada animal fica associado a um único tutor. |
| `Tutor` solicita `Reserva` | `1` para `0..*` | As reservas são feitas para animais de um tutor. |
| `Animal` tem `Reserva` | `1` para `0..*` | Um animal pode ter várias reservas ao longo do tempo. |
| `TipoAlojamentoTarifa` classifica `Alojamento` | `1` para `0..*` | Cada alojamento pertence a um tipo com capacidade, compatibilidade e tarifa diária. |
| `Alojamento` é reservado em `Reserva` | `1` para `0..*` | O mesmo alojamento pode ter várias reservas, desde que não exista sobreposição. |
| `Reserva` origina `Estadia` | `1` para `0..1` | Uma reserva pode ser cancelada antes do check-in; se for concretizada, origina uma estadia. |
| `Reserva` contém `Nota` | `1` para `0..*` | Notas operacionais podem ser registadas ainda antes da estadia. |
| `Reserva` agenda `TarefaCuidado` | `1` para `0..*` | Serviços opcionais e instruções podem ser agendados no momento da reserva. |
| `Estadia` ativa `PlanoCuidados` | `1` para `1` | Durante a estadia existe um plano de cuidados ativo para orientar os cuidadores. |
| `PlanoCuidados` organiza `TarefaCuidado` | `1` para `0..*` | O plano agrupa tarefas recorrentes, instruções e serviços agendados. |
| `Estadia` regista `RegistoCuidado` | `1` para `0..*` | Cuidados prestados ficam contextualizados pela estadia ativa. |
| `Estadia` contém `Nota` | `1` para `0..*` | Notas podem ser acrescentadas durante o acompanhamento do animal. |
| `Estadia` inclui `ServicoExtra` | `1` para `0..*` | Serviços extra só são registados durante uma estadia ativa. |
| `Estadia` inclui `IntervencaoClinica` | `1` para `0..*` | Intervenções clínicas com impacto no histórico e faturação ocorrem no contexto da estadia. |
| `Estadia` regista `AlteracaoEstadoSaude` | `1` para `0..*` | Alterações ao estado de saúde são observadas durante a estadia. |
| `Estadia` gera `Pagamento` | `1` para `0..*` | A estadia pode ter pagamento base no check-in e pagamento complementar no check-out. |
| `TipoServicoExtra` tipifica `ServicoExtra` | `1` para `0..*` | Serviços realizados usam o catálogo controlado pela direção. |
| `TipoServicoExtra` pode agendar `TarefaCuidado` | `1` para `0..*` | Banhos, passeios e outros serviços podem surgir como tarefas previstas. |
| `Animal` agrega histórico operacional e clínico | `1` para `0..*` | Registos de cuidado, alterações de saúde e intervenções formam o histórico consolidado do animal. |
| `Colaborador` regista eventos operacionais ou clínicos | `1` para `0..*` | Notas, cuidados, serviços, intervenções e alterações de saúde mantêm autoria. |
| `Colaborador` origina `RegistoAuditoria` | `1` para `0..*` | Operações críticas ficam auditadas com autor e momento. |

## Enumerações

| Enumeração | Valores | Origem |
|---|---|---|
| `Especie` | `CAO`, `GATO` | RD-08 |
| `EstadoSaude` | `NORMAL`, `ALTERADO`, `CRITICO` | US-18, US-26, RF-13 |
| `EstadoLimpeza` | `PENDENTE`, `CONCLUIDO` | US-22, US-23, RF-15, RD-01 |
| `EstadoReserva` | `ATIVA`, `CONFIRMADA`, `CANCELADA`, `CONCLUIDA` | US-08, US-09, RD-02, RD-06 |
| `EstadoEstadia` | `EM_CURSO`, `TERMINADA` | US-09, RD-03, RD-07 |
| `EstadoPagamento` | `LIQUIDADO`, `PENDENTE` | US-12, US-13, RF-10 |
| `MomentoPagamento` | `CHECK_IN`, `CHECK_OUT` | US-12, US-13, RD-04 |
| `MetodoPagamento` | `NUMERARIO`, `CARTAO_DEBITO`, `CARTAO_CREDITO` | RF-10 |
| `PrioridadePlano` | `ROTINA`, `URGENTE`, `CRITICA` | US-16, US-18, RD-10 |
| `PeriodicidadeTarefa` | `UNICA`, `DIARIA`, `SEMANAL` | US-16, US-21, RD-10 |
| `TipoColaborador` | `DIRETOR`, `FUNCIONARIO_RECEPCAO`, `CUIDADOR`, `MEDICO_VETERINARIO`, `RESPONSAVEL_LIMPEZA` | US-03, RNF-04 |
