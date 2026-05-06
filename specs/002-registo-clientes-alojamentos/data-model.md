# Data Model: Registo Base de Clientes e Alojamentos

## Overview

O modelo de dados desta fase cobre o registo base de tutores e animais, a disponibilidade de alojamentos e a criação de reservas. As entidades aqui descritas sustentam UC-03 e UC-04, bem como RF-04, RF-05, RF-06, RF-07, RD-03, RD-05 e RD-06.

## Entities

### Tutor

**Purpose**: Representa o responsável principal do animal e o ponto de contacto do hotel.

**Attributes**:
- `id`
- `nome`
- `nif`
- `contacto`
- `email`
- `dataRegisto`

**Validation rules**:
- `nif` é obrigatório e único.
- `nome`, `contacto` e `email` são obrigatórios.

### Animal

**Purpose**: Representa o hóspede do hotel.

**Attributes**:
- `id`
- `nome`
- `especie`
- `raca`
- `dataNascimento`
- `peso`
- `estadoSaude`
- `necessidadesAlimentares`
- `medicacaoCurso`
- `dataRegisto`

**Validation rules**:
- `especie` é limitada a `CAO` e `GATO`.
- Cada animal deve estar associado a pelo menos um tutor.
- `nome`, `especie`, `raca` e `dataNascimento` são obrigatórios.

### Alojamento

**Purpose**: Representa a box ou unidade física disponível para alojar um animal.

**Attributes**:
- `id`
- `identificacao`
- `tipo`
- `capacidade`
- `estadoLimpeza`

**Validation rules**:
- `identificacao` é obrigatória.
- `tipo` é obrigatório e usa `CANINO` ou `FELINO`.
- `capacidade` é obrigatória e deve ser maior ou igual a 1.
- `estadoLimpeza` usa os valores `PENDENTE` e `CONCLUIDO`.
- Um alojamento só é elegível para nova reserva quando a limpeza está concluída, não existem conflitos temporais e o `tipo` é compatível com a espécie do animal.

### Reserva

**Purpose**: Representa a reserva de um alojamento para um animal num intervalo temporal.

**Attributes**:
- `id`
- `dataInicio`
- `dataFim`
- `estado`
- `dataCriacao`
- `animal`
- `tutor`
- `alojamento`

**Validation rules**:
- `dataInicio` deve ser anterior a `dataFim`.
- Uma reserva criada nesta fase inicia em `ATIVA`.
- Uma reserva cancelada não pode voltar a `ATIVA`.

## Relationships

- `Tutor 1..* Animal`: um tutor pode ter um ou mais animais.
- `Animal 1..* Reserva`: um animal pode ter várias reservas ao longo do tempo.
- `Tutor 1..* Reserva`: um tutor pode acumular reservas para os seus animais.
- `Alojamento 1..* Reserva`: um alojamento pode ser usado por várias reservas não sobrepostas.

## Enums

### Especie

- `CAO`
- `GATO`

### EstadoSaude

- `NORMAL`
- `ALTERADO`
- `CRITICO`

### EstadoLimpeza

- `PENDENTE`
- `CONCLUIDO`

### TipoAlojamento

- `CANINO`
- `FELINO`

### EstadoReserva

- `ATIVA`
- `CANCELADA`
- `CONCLUIDA`

## State Rules

- `Reserva`: `ATIVA -> CANCELADA` é permitido; `CANCELADA -> ATIVA` não é permitido.
- `Alojamento`: a disponibilidade é determinada pela combinação de reservas, estadias e limpeza, não por um flag manual isolado.

## Notes

- O modelo mantém compatibilidade com a Etapa 2, mas a implementação desta fase deve concentrar-se apenas nos atributos necessários para o registo e a reserva.
- Os campos `tipo` e `capacidade` de `Alojamento` são usados no fluxo de reserva: `tipo` filtra alojamentos adequados à espécie e `capacidade` evita unidades incompletas/indefinidas na UI.
