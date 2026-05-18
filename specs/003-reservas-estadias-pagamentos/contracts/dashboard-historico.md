# Contract: Dashboard e Histórico

## Purpose

Definir os contratos de consulta para direção e receção sobre indicadores de operação e histórico financeiro/operacional.

## Interactions

### Dashboard operacional da direção

- **Method**: `GET`
- **Path**: `/dashboard`
- **Query**:
  - `dataInicio` opcional
  - `dataFim` opcional
- **Result**:
  - taxa de ocupação atual
  - número de estadias ativas
  - número de reservas futuras
  - faturação diária e mensal
  - lista de pagamentos pendentes
- **SLA funcional**:
  - atualização até 60 segundos após evento relevante (RF-01)

### Histórico de estadias e pagamentos

- **Method**: `GET`
- **Path**: `/historico`
- **Query**:
  - `animalId` opcional
  - `tutorId` opcional
  - `dataInicio` opcional
  - `dataFim` opcional
  - `estadoPagamento` opcional
- **Result**:
  - lista de estadias por filtro
  - pagamentos associados (momento, valor, método, estado, data)

## Access Rules

- Dashboard financeiro e pendentes: perfil de direção.
- Histórico operacional: receção e direção (conforme RF-05 e permissões definidas).

## Error Cases

- Filtro temporal inválido: devolver validação de datas.
- Utilizador sem permissão: devolver acesso negado.
- Sem resultados: devolver lista vazia mantendo filtros aplicados.
