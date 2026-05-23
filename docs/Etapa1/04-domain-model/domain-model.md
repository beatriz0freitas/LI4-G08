# Modelo de Domínio — Hotel de Animais

Diagrama: [domain-model.mmd](domain-model.mmd)

## Âmbito

Este modelo descreve os principais conceitos do domínio do hotel de animais.

## Entidades do Domínio

| Entidade | Papel no domínio | Origem |
|---|---|---|
| `Tutor` | Proprietário de um ou mais animais.  | US-09, RD-05 |
| `Animal` | Animal hóspede do hotel. | US-09, US-16, RD-05, RD-08 |
| `Alojamento` | Unidade física de alojamento. | US-01, US-12, US-20, US-21, RD-01 |
| `Reserva` | Compromisso de alojamento num período. | US-06, US-12, RD-06 |
| `Estadia` | Execução real da reserva durante o período de alojamento. | US-07, US-11, RD-02, RD-03, RD-07 |
| `Pagamento` | Registo financeiro associado a um momento da estadia. | US-10, US-11, RF-10 |
| `ServicoExtra` | Serviço opcional com impacto financeiro prestado durante a estadia. | US-13, US-18, US-19, RD-09 |
| `RegistoCuidado` | Registo operacional de cuidados diários. | US-14, US-15 |
| `IntervencaoClinica` | Ato clínico ou prescrição veterinária. | US-22, US-23 |
| `AlteracaoEstadoSaude` | Registo de mudança de estado de saúde. | US-16, US-24 |
| `EstadoSaude` | Estado clínico do animal em cada momento do acompanhamento. | US-16, US-24 |
| `Nota` | Comunicação operacional entre turnos. | US-17 |
| `Colaborador` | Profissional do hotel com responsabilidades operacionais ou clínicas. | US-03, UC-01 |
| `HistoricoClinico` | Agregador clínico do animal ao longo do tempo. | US-16, US-22, US-23, US-24 |
| `Fatura` | Agregador financeiro da estadia. | US-10, US-11, RF-10 |

## Relações do Domínio

| Relação | Multiplicidade | Justificação |
|---|---|---|
| `Tutor` possui `Animal` | `1..* ↔ *` | Um tutor pode ter vários animais; cada animal fica associado a um tutor responsável. Origem: US-09, RD-05. |
| `Tutor` cria `Reserva` | `1 ↔ *` | O tutor participa na criação e gestão das reservas dos seus animais. Origem: US-06, US-09. |
| `Animal` origina `Reserva` | `1 ↔ *` | Um animal pode ter várias reservas ao longo do tempo. Origem: US-06, US-09. |
| `Alojamento` aloja `Reserva` | `1 ↔ *` | Um alojamento pode ser usado por várias reservas não sobrepostas. Origem: US-01, US-12, RD-01. |
| `Reserva` gera `Estadia` | `1 ↔ 0..1` | A reserva pode terminar sem estadia se for cancelada; quando concretizada gera uma única estadia. Origem: US-06, US-07, RD-02, RD-06. |
| `Reserva` contém `Nota` | `1 ↔ *` | Notas operacionais ficam associadas à reserva. Origem: US-17. |
| `Estadia` gera `Pagamento` | `1 ↔ 0..2` | A estadia pode ter pagamento de entrada e pagamento de saída. Origem: US-10, US-11, RF-10, RD-04. |
| `Estadia` inclui `ServicoExtra` | `1 ↔ *` | Serviços extra são registados durante a estadia. Origem: US-13, US-18, US-19. |
| `Estadia` regista `RegistoCuidado` | `1 ↔ *` | Os cuidados diários são contextualizados pela estadia em curso. Origem: US-14, US-15. |
| `Estadia` inclui `IntervencaoClinica` | `1 ↔ *` | Intervenções clínicas acontecem no contexto da estadia. Origem: US-22, US-23. |
| `Estadia` inclui `AlteracaoEstadoSaude` | `1 ↔ *` | Alterações de saúde são registadas durante a estadia. Origem: US-16, US-24. |
| `Animal` possui `HistoricoClinico` | `1 ↔ 1` | O histórico clínico agrega o percurso clínico do animal. Origem: US-16, US-22, US-23, US-24. |
| `Animal` tem `EstadoSaude` | `1 ↔ *` | O estado de saúde do animal pode variar ao longo do tempo. Origem: US-16, US-24. |
| `Estadia` origina `Fatura` | `1 ↔ 1` | A fatura agrega os encargos financeiros de uma estadia. Origem: US-10, US-11, RF-10. |
| `Fatura` agrega `Pagamento` | `1 ↔ 0..2` | A faturação pode ser liquidada em um ou dois momentos. Origem: US-10, US-11, RF-10. |
| `Colaborador` escreve `Nota` | `1 ↔ *` | A autoria operacional é atribuída a um colaborador. Origem: US-17, UC-01. |
| `Colaborador` regista `RegistoCuidado` | `1 ↔ *` | O cuidador responsável fica rastreado. Origem: US-14, US-15, UC-01. |
| `Colaborador` regista `ServicoExtra` | `1 ↔ *` | O registo do serviço identifica o autor. Origem: US-13, US-18, US-19. |
| `Colaborador` regista `IntervencaoClinica` | `1 ↔ *` | O médico veterinário responsável fica identificado. Origem: US-22, US-23. |
| `Colaborador` regista `AlteracaoEstadoSaude` | `1 ↔ *` | A alteração clínica fica associada a um autor. Origem: US-16, US-24. |
| `HistoricoClinico` agrega `IntervencaoClinica` | `1 ↔ 0..*` | O histórico clínico compila as intervenções do animal. |
| `HistoricoClinico` agrega `AlteracaoEstadoSaude` | `1 ↔ 0..*` | O histórico clínico compila alterações de saúde. |

## Enumerações

| Enumeração | Valores | Origem |
|---|---|---|
| `Especie` | `CAO`, `GATO` | RD-08 |
| `EstadoSaude` | `NORMAL`, `ALTERADO`, `CRITICO` | US-16, US-24 |
| `TipoAlojamento` | `CANINO`, `FELINO` | US-12, RD-08 |
| `EstadoLimpeza` | `PENDENTE`, `CONCLUIDO` | RD-01, US-21 |
| `EstadoReserva` | `ATIVA`, `CANCELADA`, `CONCLUIDA` | US-06, RD-06 |
| `EstadoEstadia` | `EM_CURSO`, `TERMINADA` | US-07, RD-03 |
| `EstadoPagamento` | `LIQUIDADO`, `PENDENTE` | RF-10 |
| `MetodoPagamento` | `NAO_DEFINIDO`, `NUMERARIO`, `CARTAO_DEBITO`, `CARTAO_CREDITO` | RF-10 |
| `MomentoPagamento` | `CHECK_IN`, `CHECK_OUT` | RD-04 |
| `TipoServicoExtra` | `BANHO`, `PASSEIO`, `OUTRO` | US-13 |
| `TipoColaborador` | `DIRETOR`, `FUNCIONARIO_RECEPCAO`, `CUIDADOR`, `MEDICO_VETERINARIO`, `RESPONSAVEL_LIMPEZA` | UC-01, US-03..US-24 |

## Pressupostos

1. **`TipoAlojamento`**: os alojamentos são classificados como `CANINO` ou `FELINO` para garantir que a reserva só apresenta unidades compatíveis com a espécie do animal (`CAO` ou `GATO`). Esta decisão substitui a enumeração provisória baseada em formato de box.
2. **`MetodoPagamento`**: os requisitos registam a necessidade de processar pagamentos (US-10, US-11) mas não enumeram métodos. Adotaram-se `NUMERARIO`, `CARTAO_DEBITO` e `CARTAO_CREDITO` como valores típicos num hotel de animais. A lista deverá ser confirmada com os stakeholders.
3. **`EstadoSaude`**: US-16 menciona "alterações ao estado de saúde" e US-24 refere "alterações recentes". Os valores `NORMAL`, `ALTERADO` e `CRITICO` são considerados suficientes para cobrir a variação descrita.
4. **`RegistoCuidado` e `ServicoExtra` separados**: cuidados diários (alimentação, medicação — US-15) são distinguidos dos serviços extra faturáveis (banho, passeio — US-18) por terem natureza e impacto financeiro diferentes.
5. **Colaborador único**: todos os papéis do sistema (cuidador, receção, etc.) são modelados como uma única entidade `Colaborador` com `TipoColaborador`, pois partilham atributos comuns (autenticação, nome, email) e diferem apenas nas permissões — UC-01, US-03.
