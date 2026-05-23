# Data Model: Fase 4 — Operação diária, clínica e limpeza avançada

## Entidades

### RegistoCuidado
- **Campos**: id, estadiaId, descricao, dataHora, autorId
- **Validações**: descricao obrigatória; dataHora obrigatória; estadia deve estar ativa no momento do registo; autor deve ter perfil autorizado.
- **Relações**: pertence a uma `Estadia`.

### ServicoExtra
- **Campos**: id, estadiaId, tipo, custo, dataHora, autorId
- **Validações**: tipo obrigatório; custo >= 0; dataHora obrigatória; associar à estadia em curso.
- **Relações**: pertence a uma `Estadia`.

### IntervencaoClinica
- **Campos**: id, estadiaId, descricao, custo, dataHora, medicoId
- **Validações**: descricao obrigatória; custo >= 0; dataHora obrigatória; autoria de utilizador com perfil `VETERINARIO`.
- **Relações**: pertence a uma `Estadia`.

### Nota
- **Campos**: id, reservaId, descricao, autorId, dataHora
- **Validações**: descricao obrigatória; reserva obrigatória; autor obrigatório.
- **Relações**: pertence a uma `Reserva`.

### AlteracaoEstadoSaude
- **Campos**: id, estadiaId, descricao, severidade, dataHora, autorId
- **Validações**: descricao obrigatória; severidade obrigatória; dataHora obrigatória; autor deve ser cuidador ou veterinário.
- **Relações**: pertence a uma `Estadia` e fica visível no historial clínico.

## Relações Principais
- `Estadia` 1..* `RegistoCuidado`
- `Estadia` 1..* `ServicoExtra`
- `Estadia` 1..* `IntervencaoClinica`
- `Estadia` 1..* `AlteracaoEstadoSaude`
- `Reserva` 1..* `Nota`

## Regras de Domínio
- O custo de serviços extra e intervenções clínicas é registado no momento da ocorrência e não é alterado após o check-out.
- Registos de cuidado e alterações de saúde só podem ser criados enquanto a estadia estiver ativa.
- Todas as entidades devem guardar autor e timestamp para auditoria.
- Listagens de historial devem suportar paginação e filtros por período, animal, estadia e tipo de registo.

## Índices e integridade sugeridos
- Índice por `estadiaId` em todas as entidades ligadas à estadia.
- Índice por `dataHora` para ordenar o historial.
- Índice por `reservaId` em `Nota`.
- Restrições de chave estrangeira para garantir integridade referencial.
