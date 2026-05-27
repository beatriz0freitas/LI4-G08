# Modelo de Domínio - Hotel de Animais

Diagramas:
- [domain-model.mmd](domain-model.mmd)
- [modelo_dominio.plantuml](modelo_dominio.plantuml)

## Âmbito

O modelo de domínio representa os principais conceitos de negócio necessários para suportar reservas, estadias, pagamentos, cuidados, serviços extra, acompanhamento clínico, limpeza, perfis de colaboradores e auditoria. O objetivo é manter uma visão conceptual simples, alinhada com os requisitos da Etapa 1, as specs Speckit e a implementação atual.

## Entidades do Domínio

| Entidade | Papel no domínio | Origem |
|---|---|---|
| `Tutor` | Pessoa responsável por um ou mais animais e associada às reservas solicitadas. | RF-04, RD-05 |
| `Animal` | Animal registado no hotel, associado a um tutor e usado em reservas, estadias, cuidados e histórico. | RF-04, RF-05, RD-05, RD-08 |
| `TipoAlojamentoTarifa` | Catálogo de tipos de alojamento e respetivas tarifas diárias ativas/inativas, gerido pela direção. | RF-08, RF-10, RF-18, RD-04, RD-11 |
| `Alojamento` | Unidade física onde o animal fica alojado; a disponibilidade depende de tipo compatível, período, limpeza, reservas e estadias ativas. | RF-06, RF-15, RD-01 |
| `Reserva` | Compromisso de alojamento para um animal num período, antes de ser convertido em estadia. | RF-06, RF-07, RF-16, RD-01, RD-02, RD-06 |
| `Estadia` | Execução real da reserva, iniciada no check-in e terminada no check-out. | RF-08, RF-09, RD-02, RD-03, RD-07 |
| `PlanoCuidados` | Plano ativo da estadia e histórico do animal, com instruções, prioridade e tarefas de acompanhamento. | RF-11, RD-10 |
| `TarefaCuidado` | Tarefa estruturada prevista no plano de cuidados, com periodicidade e estado de conclusão. | RF-11, RF-16, RD-10 |
| `RegistoCuidado` | Registo operacional de cuidado prestado, com descrição, autor e data/hora. | RF-12, RNF-09 |
| `Nota` | Nota operacional associada à reserva, usada para continuidade entre colaboradores e instruções de cuidado. | RF-05, RF-16, RD-10 |
| `TipoServicoExtra` | Tipo de serviço extra disponível em catálogo, gerido pela direção. | RF-17, RF-18, RD-11 |
| `ServicoExtra` | Serviço extra efetivamente realizado durante uma estadia ativa e com custo associado. | RF-17, RD-09 |
| `IntervencaoClinica` | Intervenção ou prescrição veterinária registada durante uma estadia, podendo ter custo associado. | RF-14, RD-09 |
| `AlteracaoEstadoSaude` | Registo de alteração ao estado de saúde do animal, com severidade e data/hora. | RF-13, RD-10 |
| `Pagamento` | Registo financeiro de valores liquidados ou pendentes no check-in ou check-out. | RF-10, RD-04 |
| `Colaborador` | Profissional do hotel com perfil de acesso e autoria sobre operações realizadas. | RF-02, RNF-04 |
| `AuditoriaEvento` | Registo de operação crítica realizada, identificando autor, momento, operação, entidade e resultado. | RF-19, RNF-09 |

## Relações do Domínio

| Relação | Multiplicidade | Justificação |
|---|---|---|
| `Tutor` responsável por `Animal` | `1` para `0..*` | Um tutor pode ter vários animais; cada animal fica associado a um único tutor. |
| `Tutor` solicita `Reserva` | `1` para `0..*` | As reservas são feitas para animais de um tutor. |
| `Animal` tem `Reserva` | `1` para `0..*` | Um animal pode ter várias reservas ao longo do tempo. |
| `TipoAlojamentoTarifa` classifica `Alojamento` | `1` para `0..*` | O tipo do alojamento é usado para compatibilidade e para obter a tarifa ativa. |
| `Alojamento` é reservado em `Reserva` | `1` para `0..*` | O mesmo alojamento pode ter várias reservas, desde que não exista sobreposição e a limpeza esteja concluída. |
| `Reserva` origina `Estadia` | `1` para `0..1` | Uma reserva pode ser cancelada antes do check-in; se for concretizada, origina uma única estadia. |
| `Reserva` contém `Nota` | `1` para `0..*` | Notas e instruções podem ser registadas antes da estadia e alimentar o plano de cuidados. |
| `Animal` mantém `PlanoCuidados` | `1` para `0..*` | O animal mantém histórico persistente de planos ao longo das estadias. |
| `Estadia` ativa `PlanoCuidados` | `1` para `1` | Durante a estadia existe um plano de cuidados ativo para orientar os cuidadores. |
| `PlanoCuidados` organiza `TarefaCuidado` | `1` para `0..*` | O plano agrupa tarefas recorrentes, instruções e serviços previstos. |
| `Estadia` regista `RegistoCuidado` | `1` para `0..*` | Cuidados prestados ficam contextualizados pela estadia ativa. |
| `Estadia` inclui `ServicoExtra` | `1` para `0..*` | Serviços extra só são registados durante uma estadia ativa e entram na cobrança complementar. |
| `TipoServicoExtra` tipifica `ServicoExtra` | `1` para `0..*` | Serviços realizados usam o catálogo controlado pela direção. |
| `Estadia` inclui `IntervencaoClinica` | `1` para `0..*` | Intervenções clínicas com impacto no histórico e faturação ocorrem no contexto da estadia. |
| `Estadia` regista `AlteracaoEstadoSaude` | `1` para `0..*` | Alterações ao estado de saúde são observadas durante a estadia e podem alterar a prioridade do plano. |
| `Estadia` gera `Pagamento` | `1` para `0..*` | A estadia pode ter pagamento base no check-in e pagamento complementar no check-out. |
| `Colaborador` regista eventos operacionais ou clínicos | `1` para `0..*` | Notas, cuidados, serviços, intervenções e alterações de saúde mantêm autoria. |
| `Colaborador` origina `AuditoriaEvento` | `1` para `0..*` | Operações críticas ficam auditadas com autor, entidade, ação e resultado. |

## Enumerações

| Enumeração | Valores | Origem |
|---|---|---|
| `Especie` | `CAO`, `GATO` | RD-08 |
| `EstadoSaude` | `NORMAL`, `ALTERADO`, `CRITICO` | RF-13 |
| `EstadoLimpeza` | `PENDENTE`, `CONCLUIDO` | RF-15, RD-01 |
| `EstadoReserva` | `ATIVA`, `CONFIRMADA`, `CANCELADA`, `CONCLUIDA` | RF-07, RD-02, RD-06 |
| `EstadoEstadia` | `EM_CURSO`, `TERMINADA` | RF-08, RF-09, RD-03, RD-07 |
| `EstadoPagamento` | `LIQUIDADO`, `PENDENTE` | RF-10 |
| `MomentoPagamento` | `CHECK_IN`, `CHECK_OUT` | RD-04 |
| `MetodoPagamento` | `NUMERARIO`, `CARTAO_DEBITO`, `CARTAO_CREDITO` | RF-10 |
| `PrioridadePlano` | `ROTINA`, `URGENTE`, `CRITICO` | RF-11, RD-10 |
| `PeriodicidadeTarefa` | `UNICA`, `DIARIA`, `SEMANAL` | RF-11, RF-16, RD-10 |
| `TipoColaborador` | `DIRETOR`, `FUNCIONARIO_RECEPCAO`, `CUIDADOR`, `MEDICO_VETERINARIO`, `RESPONSAVEL_LIMPEZA` | RF-02, RNF-04 |
| `ResultadoAuditoria` | `SUCESSO`, `FALHA` | RF-19, RNF-09 |

## Pressupostos

1. **Tipo de alojamento**: `Alojamento.tipo` e `TipoAlojamentoTarifa.tipoAlojamento` representam o mesmo conceito de negócio. Os valores iniciais são `CANINO` e `FELINO`, mas a tarifa é gerida por catálogo para permitir evolução controlada.
2. **Método de pagamento**: os métodos aceites são `NUMERARIO`, `CARTAO_DEBITO` e `CARTAO_CREDITO`. Não existe opção `NAO_DEFINIDO`; o método é obrigatório no check-in e no check-out.
3. **Histórico consolidado**: o historial clínico/operacional é uma visão derivada de cuidados, notas, alterações de saúde, intervenções clínicas, serviços extra e estadias; não é modelado como entidade persistente autónoma.
4. **Faturação**: não existe uma entidade `Fatura` separada no domínio atual. A evidência financeira é composta por `Pagamento`, `ServicoExtra`, `IntervencaoClinica`, tarifa base e relatórios.
5. **Plano de cuidados**: cada estadia deve ter um plano ativo, e o animal mantém o histórico dos planos anteriores. `TarefaCuidado` estrutura o planeamento; `RegistoCuidado` representa a execução observada.
6. **Auditoria**: `AuditoriaEvento` é transversal às operações críticas e complementa a rastreabilidade funcional sem substituir os registos de domínio.
