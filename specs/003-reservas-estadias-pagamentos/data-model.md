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
- `estado` (`ATIVA`, `CONFIRMADA`, `CANCELADA`, `CONCLUIDA`)
- `dataHoraConfirmacao` (opcional)
- `dataCriacao`
- `animalId`
- `tutorId`
- `alojamentoId`

**Validation rules**:
- `dataInicio < dataFim`.
- Confirmação operacional de reserva deve registar timestamp e utilizador responsável.
- A confirmação move a reserva de `ATIVA` para `CONFIRMADA` apenas durante o check-in.
- Só reservas com estado `ATIVA` podem ser canceladas.
- O check-in confirma automaticamente uma reserva `ATIVA` antes de criar a `Estadia`; reservas `CONFIRMADA` já têm check-in associado e não podem iniciar nova estadia.
- A conclusão da reserva (`CONCLUIDA`) só ocorre após o check-out da estadia associada.
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
- Check-in só é permitido com reserva ativa (RD-02); a reserva fica confirmada dentro da mesma transação.
- Check-out só é permitido após check-in para a mesma estadia (RD-03).
- Não pode existir mais do que uma estadia `EM_CURSO` por animal (RD-07).

### Pagamento

**Purpose**: Representar transações financeiras da estadia em dois momentos operacionais.

**Attributes**:
- `id`
- `estadiaId`
- `momentoPagamento` (`CHECK_IN`, `CHECK_OUT`)
- `valor`
- `metodoPagamento` (`NUMERARIO`, `CARTAO_DEBITO`, `CARTAO_CREDITO`)
- `estadoPagamento` (`LIQUIDADO`, `PENDENTE`)
- `dataHoraRegisto`

**Validation rules**:
- `valor >= 0`; valores negativos são inválidos.
- `metodoPagamento` e `estadoPagamento` são obrigatórios (RF-10); não é permitido registar pagamentos com método indefinido.
- Pagamento de `CHECK_IN` cobre base da estadia calculada por dias reservados e tarifa ativa do tipo de alojamento.
- Pagamento de `CHECK_OUT` cobre serviços extra, intervenções clínicas e dias adicionais face ao período reservado (RD-04).

### TipoAlojamentoTarifa

**Purpose**: Configurar tipos de alojamento e respetiva tarifa diária sem depender de enum fixo na aplicação.

**Attributes**:
- `id`
- `tipoAlojamento` (texto único, normalizado em maiúsculas)
- `tarifaDiaria`
- `ativo`
- `dataCriacao`

**Validation rules**:
- `tipoAlojamento` é obrigatório e único.
- `tarifaDiaria >= 0`.
- Apenas tarifas ativas podem ser usadas no cálculo de pagamentos.
- Um tipo desativado permanece no histórico, mas não deve ser selecionado para novas configurações operacionais.

### TipoServicoExtra

**Purpose**: Catálogo de serviços extra disponíveis para registo durante estadias.

**Attributes**:
- `id`
- `nome`
- `descricao`
- `ativo`
- `dataCriacao`

**Validation rules**:
- `nome` é obrigatório e único.
- Apenas tipos ativos podem ser usados para novos `ServicoExtra`.

### ServicoExtra

**Purpose**: Registar consumos complementares ocorridos durante uma estadia.

**Attributes**:
- `id`
- `estadiaId`
- `tipoServicoExtraId`
- `custo`
- `dataHora`
- `autorId`

**Validation rules**:
- Apenas estadias `EM_CURSO` aceitam novos serviços extra.
- `custo >= 0`.
- O custo registado é imutável após check-out (RD-09).

### IntervencaoClinica

**Purpose**: Registar intervenções veterinárias associadas a uma estadia e potencialmente faturáveis no check-out.

**Attributes relevantes nesta fase**:
- `id`
- `estadiaId`
- `descricao`
- `custo`
- `dataHora`
- `medicoId`

**Validation rules**:
- `custo >= 0`.
- O custo registado é imutável após check-out (RD-09).

### Enums

- `EstadoPagamento`: `LIQUIDADO`, `PENDENTE`
- `MetodoPagamento`: `NUMERARIO`, `CARTAO_DEBITO`, `CARTAO_CREDITO`
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
- `TipoAlojamentoTarifa`: `ativo=true -> ativo=false -> ativo=true` por ação da direção.
- `TipoServicoExtra`: `ativo=true -> ativo=false -> ativo=true` por ação da direção.

## Relationships

- `Reserva 1:0..1 Estadia` (uma reserva pode originar no máximo uma estadia).
- `Estadia 1:1..2 Pagamento` (pagamento base obrigatório no check-in e pagamento complementar opcional no check-out).
- `Alojamento 1:* Reserva` (sem sobreposição temporal válida).
- `Animal 1:* Reserva` e `Animal 1:* Estadia` (sem estadias concorrentes ativas).
- `TipoAlojamentoTarifa 1:* Alojamento` por correspondência textual de `tipo`.
- `TipoServicoExtra 1:* ServicoExtra`.
- `Estadia 1:* ServicoExtra`.
- `Estadia 1:* IntervencaoClinica`.

## Domain Invariants

- Disponibilidade depende de reserva/estadia ativa e limpeza concluída (RD-01).
- Check-in exige reserva válida (RD-02).
- Check-out exige check-in prévio (RD-03).
- Reserva cancelada não reativa (RD-06).
- Exclusividade de estadia ativa por animal (RD-07).
- Custos extra registados na ocorrência e imutáveis após check-out (RD-09).
- Cálculo do check-in usa tarifa ativa do tipo de alojamento; se não existir tarifa ativa, a operação deve falhar.
- Cálculo do check-out não volta a cobrar dias já cobertos pelo pagamento base.

## Traceability

- `RF-07` mapeia criação/cancelamento em `Reserva`.
- `RF-08` mapeia abertura de `Estadia` e pagamento de entrada.
- `RF-09` mapeia fecho de `Estadia` e faturação complementar.
- `RF-10` mapeia estrutura e rastreabilidade de `Pagamento`.
- `RF-18` mapeia `TipoAlojamentoTarifa` e `TipoServicoExtra`.
- `RF-01` e `RF-05` consomem projeções de leitura sobre `Reserva`, `Estadia` e `Pagamento`.
