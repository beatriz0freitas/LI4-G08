# Data Model: Reservas, Estadias e Pagamentos

## Overview

Este modelo cobre o ciclo operacional de Fase 3: criação/cancelamento de reservas, check-in/check-out, registo de pagamentos e consultas operacionais de histórico e indicadores. O objetivo é assegurar consistência entre disponibilidade, ocupação e faturação.

## Entities

### Reserva

**Purpose**: Representar a reserva de alojamento para um animal num intervalo temporal.

**Attributes**:
- `id`
- `dataInicio`
- `dataFim`
- `estado` (`ATIVA`, `CANCELADA`, `CONCLUIDA`)
- `dataHoraConfirmacao` (opcional)
- `dataCriacao`
- `animalId`
- `tutorId`
- `alojamentoId`

**Validation rules**:
- `dataInicio < dataFim`.
- Confirmação operacional de reserva deve registar timestamp e utilizador responsável.
- Só reservas com estado `ATIVA` podem ser canceladas.
- Reserva `CANCELADA` não pode regressar a `ATIVA` (RD-06).
- Reserva sobreposta para a mesma box é inválida quando conflita com disponibilidade (RD-01).

### Estadia

**Purpose**: Representar a execução da estadia associada a uma reserva.

**Attributes**:
- `id`
- `reservaId`
- `dataHoraCheckIn`
- `dataHoraCheckOut`
- `estado` (`EM_CURSO`, `TERMINADA`)

**Validation rules**:
- Check-in só é permitido com reserva confirmada/ativa (RD-02).
- Check-out só é permitido após check-in para a mesma estadia (RD-03).
- Não pode existir mais do que uma estadia `EM_CURSO` por animal (RD-07).

### Pagamento

**Purpose**: Representar transações financeiras da estadia em dois momentos operacionais.

**Attributes**:
- `id`
- `estadiaId`
- `momentoPagamento` (`CHECK_IN`, `CHECK_OUT`)
- `valor`
- `metodoPagamento` (`NAO_DEFINIDO`, `NUMERARIO`, `CARTAO_DEBITO`, `CARTAO_CREDITO`)
- `estadoPagamento` (`LIQUIDADO`, `PENDENTE`)
- `dataHoraRegisto`

**Validation rules**:
- `valor > 0` para pagamentos registados.
- `metodoPagamento` e `estadoPagamento` são obrigatórios (RF-10).
- Pagamento de `CHECK_IN` cobre base da estadia; `CHECK_OUT` cobre extras/intervenções (RD-04).

### Enums

- `EstadoPagamento`: `LIQUIDADO`, `PENDENTE`
- `MetodoPagamento`: `NAO_DEFINIDO`, `NUMERARIO`, `CARTAO_DEBITO`, `CARTAO_CREDITO`
- `MomentoPagamento`: `CHECK_IN`, `CHECK_OUT`

### Alojamento

**Purpose**: Recurso físico usado em reserva e estadia.

**Attributes relevantes nesta fase**:
- `id`
- `identificacao`
- `tipo`
- `estadoLimpeza`
- `estadoOcupacao` (derivado por regras de domínio)

**State behavior**:
- `Estadia`: `EM_CURSO -> TERMINADA` no check-out.
- `Alojamento`: `DISPONIVEL -> OCUPADO` no check-in.
- `Alojamento`: `OCUPADO -> PENDENTE_LIMPEZA` no check-out.
- `Alojamento`: `PENDENTE_LIMPEZA -> DISPONIVEL` após confirmação de limpeza (fluxo da fase anterior).

## Relationships

- `Reserva 1:0..1 Estadia` (uma reserva pode originar no máximo uma estadia).
- `Estadia 1:1..2 Pagamento` (pagamento base obrigatório no check-in e pagamento complementar opcional no check-out).
- `Alojamento 1:* Reserva` (sem sobreposição temporal válida).
- `Animal 1:* Reserva` e `Animal 1:* Estadia` (sem estadias concorrentes ativas).

## Domain Invariants

- Disponibilidade depende de reserva/estadia ativa e limpeza concluída (RD-01).
- Check-in exige reserva válida (RD-02).
- Check-out exige check-in prévio (RD-03).
- Reserva cancelada não reativa (RD-06).
- Exclusividade de estadia ativa por animal (RD-07).
- Custos extra registados na ocorrência e imutáveis após check-out (RD-09).

## Traceability

- `RF-07` mapeia criação/cancelamento em `Reserva`.
- `RF-08` mapeia abertura de `Estadia` e pagamento de entrada.
- `RF-09` mapeia fecho de `Estadia` e faturação complementar.
- `RF-10` mapeia estrutura e rastreabilidade de `Pagamento`.
- `RF-01` e `RF-05` consomem projeções de leitura sobre `Reserva`, `Estadia` e `Pagamento`.
