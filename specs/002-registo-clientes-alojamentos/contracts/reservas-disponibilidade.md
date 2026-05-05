# Contract: Disponibilidade e Reservas

## Purpose

Documentar a consulta de alojamentos disponíveis e a criação de reservas sem overbooking.

## Interactions

### Consultar disponibilidade

- **Method**: `GET`
- **Path**: `/reservas/disponibilidade`
- **Query**:
  - `dataInicio`
  - `dataFim`
  - `animalId` opcional quando a consulta já estiver ligada a um registo existente
- **Result**: lista de alojamentos elegíveis no período pretendido

### Criar reserva

- **Method**: `POST`
- **Path**: `/reservas`
- **Payload**:
  - `tutorId`
  - `animalId`
  - `alojamentoId`
  - `dataInicio`
  - `dataFim`
- **Validation**:
  - período válido com `dataInicio < dataFim`
  - tutor e animal existentes
  - alojamento disponível no período
  - limpeza concluída e ausência de conflito temporal
- **Success**: reserva criada com estado `ATIVA`

## Business Rules

- Uma reserva só pode ser criada se o alojamento cumprir simultaneamente as regras de disponibilidade.
- Uma reserva cancelada não pode ser reativada; deve ser criada uma nova reserva.
- A disponibilidade é recalculada a cada consulta e não deve depender de cache persistente.

## Error Cases

- Nenhum alojamento disponível: apresentar alternativa de datas.
- Sobreposição temporal: recusar criação da reserva.
- Estado de limpeza pendente: recusar a seleção do alojamento.