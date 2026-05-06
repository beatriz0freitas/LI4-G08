# Modelo de Domínio — Hotel de Animais

Diagrama: [domain-model.mmd](domain-model.mmd)

## Entidades

| Entidade | Descrição | Origem |
|---|---|---|
| `Tutor` | Proprietário de um ou mais animais. Deve ser registado antes do animal. | US-09, RD-05 |
| `Animal` | Animal hóspede do hotel. Apenas cão ou gato (RD-08). Tem estado de saúde rastreado ao longo da estadia. | US-09, US-16, RD-05, RD-08 |
| `Alojamento` | Espaço físico (box/suite) onde o animal fica alojado. Só fica disponível quando sem reserva/estadia ativa **e** limpeza concluída. | US-01, US-12, US-20, US-21, RD-01 |
| `Reserva` | Compromisso de alojamento para um animal num período definido. Após cancelamento não pode ser reativada. | US-06, US-12, RD-06 |
| `Estadia` | Período real de alojamento, iniciado pelo check-in e encerrado pelo check-out. Só pode existir com reserva prévia. | US-07, US-11, RD-02, RD-03, RD-07 |
| `Pagamento` | Registo de um ato de liquidação. Existem até dois por estadia: o do check-in (valor da estadia) e o do check-out (extras e intervenções). | US-10, US-11, RD-04 |
| `ServicoExtra` | Serviço opcional (banho, passeio ou outro) prestado durante a estadia. Custo registado no momento da ocorrência e imutável após check-out. | US-13, US-18, US-19, RD-09 |
| `IntervencaoClinica` | Ato ou prescrição veterinária. Custo registado no momento da ocorrência e imutável após check-out. | US-22, US-23, RD-09 |
| `RegistoCuidado` | Registo de cuidado diário (alimentação, medicação, etc.) efetuado por um cuidador durante a estadia. | US-14, US-15 |
| `Nota` | Observação livre associada a uma reserva, escrita por um colaborador para comunicação entre turnos. | US-17 |
| `Colaborador` | Utilizador do sistema com perfil de acesso definido pelo diretor. | US-03, UC-01 |

## Relações

| Relação | Multiplicidade | Justificação |
|---|---|---|
| `Tutor` possui `Animal` | 1..* ↔ * | RD-05: cada animal tem pelo menos um tutor |
| `Animal` origina `Reserva` | 1 ↔ * | Um animal pode ter várias reservas ao longo do tempo |
| `Alojamento` aloja `Reserva` | 1 ↔ * | Um alojamento pode ter várias reservas (não sobrepostas) |
| `Reserva` gera `Estadia` | 1 ↔ 0..1 | RD-02: só existe estadia se houver reserva; a reserva pode ser cancelada antes do check-in |
| `Reserva` contém `Nota` | 1 ↔ * | US-17: cuidadores adicionam notas a reservas |
| `Estadia` regista `RegistoCuidado` | 1 ↔ * | US-15: cuidados diários associados à estadia |
| `Estadia` inclui `ServicoExtra` | 1 ↔ * | US-18, RD-09: serviços extras ocorrem durante a estadia |
| `Estadia` inclui `IntervencaoClinica` | 1 ↔ * | US-23, RD-09: atos veterinários ocorrem durante a estadia |
| `Estadia` gera `Pagamento` | 1 ↔ 0..2 | RD-04: um pagamento no check-in (estadia), outro no check-out (extras); o segundo só existe se houver extras ou intervenções |
| `Colaborador` escreve `Nota` | 1 ↔ * | US-17: o autor da nota é um colaborador |
| `Colaborador` regista `RegistoCuidado` | 1 ↔ * | US-15: o cuidador que efetuou o cuidado fica rastreado |

## Enumerações

| Enumeração | Valores | Origem |
|---|---|---|
| `Especie` | `CAO`, `GATO` | RD-08 |
| `EstadoSaude` | `NORMAL`, `ALTERADO`, `CRITICO` | US-16, US-24 |
| `TipoAlojamento` | `CANINO`, `FELINO` | US-12 — refinado por compatibilidade com `Especie` |
| `EstadoLimpeza` | `PENDENTE`, `CONCLUIDO` | RD-01, US-21 |
| `EstadoReserva` | `ATIVA`, `CANCELADA`, `CONCLUIDA` | US-06, RD-06 |
| `EstadoEstadia` | `EM_CURSO`, `TERMINADA` | US-07, RD-03 |
| `EstadoPagamento` | `PENDENTE`, `LIQUIDADO` | US-10, US-11 |
| `MetodoPagamento` | `NUMERARIO`, `CARTAO_DEBITO`, `CARTAO_CREDITO` | US-10 — *pressuposto* (ver abaixo) |
| `MomentoPagamento` | `CHECK_IN`, `CHECK_OUT` | RD-04 |
| `TipoServicoExtra` | `BANHO`, `PASSEIO`, `OUTRO` | US-13 |
| `TipoColaborador` | `DIRETOR`, `FUNCIONARIO_RECEPCAO`, `CUIDADOR`, `MEDICO_VETERINARIO`, `RESPONSAVEL_LIMPEZA` | US-03, US-07..US-24 |

## Pressupostos

1. **`TipoAlojamento`**: os alojamentos são classificados como `CANINO` ou `FELINO` para garantir que a reserva só apresenta unidades compatíveis com a espécie do animal (`CAO` ou `GATO`). Esta decisão substitui a enumeração provisória baseada em formato de box.
2. **`MetodoPagamento`**: os requisitos registam a necessidade de processar pagamentos (US-10, US-11) mas não enumeram métodos. Adotaram-se `NUMERARIO`, `CARTAO_DEBITO` e `CARTAO_CREDITO` como valores típicos num hotel de animais. A lista deverá ser confirmada com os stakeholders.
3. **`EstadoSaude`**: US-16 menciona "alterações ao estado de saúde" e US-24 refere "alterações recentes". Os valores `NORMAL`, `ALTERADO` e `CRITICO` são considerados suficientes para cobrir a variação descrita.
4. **`RegistoCuidado` e `ServicoExtra` separados**: cuidados diários (alimentação, medicação — US-15) são distinguidos dos serviços extra faturáveis (banho, passeio — US-18) por terem natureza e impacto financeiro diferentes.
5. **Colaborador único**: todos os papéis do sistema (cuidador, receção, etc.) são modelados como uma única entidade `Colaborador` com `TipoColaborador`, pois partilham atributos comuns (autenticação, nome, email) e diferem apenas nas permissões — UC-01, US-03.
