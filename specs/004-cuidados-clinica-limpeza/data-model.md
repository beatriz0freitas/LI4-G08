# Data Model: Fase 4 — Operação diária, clínica e limpeza avançada

## Clarifications & Changes (Session 2026-05-25, LAC-02)

Adicionadas duas **novas entidades** para suportar a dinâmica do plano de cuidados:
- `PlanoCuidados` — núcleo do plano, com vínculo duplo e priorização dinâmica
- `TarefaCuidado` — tarefas recorrentes estruturadas dentro do plano

Estas entidades **não estavam no modelo inicial** mas são **obrigatórias** para cumprir os requisitos da spec (RD-10, US-14, US-16).

---

## Entidades

### PlanoCuidados (NOVA)
- **Campos principais**:
  - `id` (PK)
  - `animalId` (FK to Animal) — vínculo histórico do animal
  - `estadiaId` (FK to Estadia, UNIQUE) — vínculo ativo da estadia
  - `dataInicio` (TIMESTAMP)
  - `dataFim` (TIMESTAMP, nullable)
  - `prioridade` (ENUM: ROTINA, URGENTE, CRITICO) — muda conforme AlteracaoEstadoSaude
  - `ativo` (BOOLEAN) — false após check-out
  - `instrucoes` (VARCHAR 2000) — campo de notas livres adicionadas durante a estadia
  - Audit: `createdAt`, `createdBy`, `updatedAt`, `updatedBy`

- **Validações**:
  - `animalId` e `estadiaId` obrigatórios
  - `estadiaId` único (uma estadia = um plano ativo)
  - Apenas uma `estadiaId` pode estar ativa por animal (verificar regra em `EstadiaService`)
  - `prioridade` muda automaticamente quando `AlteracaoEstadoSaude` com severidade CRITICA é criada
  - `ativo` = true até check-out; é setado para false automaticamente

- **Relações**:
  - 1..1 com `Estadia` (ativa)
  - 0..* com `Animal` (histórico)
  - 1..* com `TarefaCuidado` (compsição)

- **Regra de domínio**: Quando `AlteracaoEstadoSaude.severidade = CRITICO` é criada, o `PlanoCuidados` correspondente muda `prioridade` para CRITICO. Sistema dispara flag visual para cuidador.

### TarefaCuidado (NOVA)
- **Campos principais**:
  - `id` (PK)
  - `planoCuidadosId` (FK to PlanoCuidados)
  - `tipo` (VARCHAR 100: ALIMENTACAO_MANHA, MEDICACAO_12H, PASSEIO, LIMPEZA, OUTRO)
  - `descricao` (VARCHAR 500, optional)
  - `periodicidade` (ENUM: UNICA, DIARIA, SEMANAL)
  - `dataHora` (TIMESTAMP) — agendamento da próxima execução
  - `concluida` (BOOLEAN) — marca conclusão da tarefa
  - `autorConclausaoId` (BIGINT, nullable) — quem marcou como concluída
  - Audit: `createdAt`, `createdBy`

- **Validações**:
  - `planoCuidadosId` obrigatório
  - `tipo` obrigatório
  - `periodicidade` obrigatório
  - `dataHora` obrigatório
  - Apenas cuidadores/veterinários podem marcar `concluida = true`

- **Relações**:
  - N..1 com `PlanoCuidados` (composição)

- **Regra de domínio**: Tarefas são **estruturadas e checklist**, diferente de `RegistoCuidado` (anotação livre). Um cuidador marca `concluida = true` ao executar a tarefa, registando o autor e timestamp.

### RegistoCuidado
- **Campos**: id, estadiaId, descricao, dataHora, autorId
- **Validações**: descricao obrigatória; dataHora obrigatória; estadia deve estar ativa no momento do registo; autor deve ter perfil autorizado.
- **Relações**: pertence a uma `Estadia`.
- **Observação importante**: `RegistoCuidado` é **independente de `TarefaCuidado`**. Pode existir uma tarefa `ALIMENTACAO_MANHA` no plano, e um cuidador registar um `RegistoCuidado` com "Alimentação realizada, animal comeu bem." sem relação explícita. O histórico consolidado junta ambos.

### ServicoExtra
- **Campos**: id, estadiaId, tipo, custo, dataHora, autorId
- **Validações**: tipo obrigatório; custo >= 0; dataHora obrigatória; associar à estadia em curso; custo não pode ser alterado após check-out.
- **Relações**: pertence a uma `Estadia`.

### IntervencaoClinica
- **Campos**: id, estadiaId, descricao, custo, dataHora, medicoId
- **Validações**: descricao obrigatória; custo >= 0; dataHora obrigatória; autoria de utilizador com perfil `VETERINARIO`; custo não pode ser alterado após check-out.
- **Relações**: pertence a uma `Estadia`.

### Nota
- **Campos**: id, reservaId, descricao, autorId, dataHora
- **Validações**: descricao obrigatória; reserva obrigatória; autor obrigatório.
- **Relações**: pertence a uma `Reserva`.
- **Observação**: Notas são origens de instruções que alimentam o `PlanoCuidados` (via RD-10). Quando criada uma nota com instruções especiais, ela deve estar visível no plano de cuidados.

### AlteracaoEstadoSaude
- **Campos**: id, estadiaId, descricao, severidade, dataHora, autorId
- **Validações**: descricao obrigatória; severidade obrigatória (ROTINA, URGENTE, CRITICO); dataHora obrigatória; autor deve ser cuidador ou veterinário.
- **Relações**: pertence a uma `Estadia` e fica visível no historial clínico.
- **Hook automático**: Quando severidade = CRITICO, dispara `IPlanoCuidadosService.atualizarPrioridade()` para o plano correspondente.

---

## Relações Principais
- `Animal` 0..* `PlanoCuidados` (histórico de planos)
- `Estadia` 1..1 `PlanoCuidados` (ativa, UNIQUE constraint)
- `PlanoCuidados` 1..* `TarefaCuidado` (composição)
- `Estadia` 1..* `RegistoCuidado` (anotações livres, independentes)
- `Estadia` 1..* `ServicoExtra`
- `Estadia` 1..* `IntervencaoClinica`
- `Reserva` 1..* `Nota` (instruções que alimentam plano)
- `Estadia` 1..* `AlteracaoEstadoSaude` (hook para mudança de prioridade do plano)

---

## Regras de Domínio

### RD-04 (Pagamento)
O pagamento no check-in cobre exclusivamente o valor da estadia; os serviços extra e as intervenções veterinárias são cobrados no check-out.

### RD-09 (Custo não alterável)
O custo de um serviço extra ou de uma intervenção veterinária deve ser registado no momento da sua ocorrência e associado à reserva em curso, não podendo ser alterado após o check-out.

### RD-10 (Plano dinâmico — NOVO)
O plano de cuidados é originário da **combinação**:
- Histórico de cuidados recorrentes do animal (tarefas estruturadas já realizadas em estadias anteriores)
- Instruções da reserva (notas `US-17` e contexto do animal)
- Ajustes manuais durante a estadia (novas tarefas, instruções adicionadas, atualizações)

O plano é **dinâmico**: pode ser modificado a qualquer momento durante a estadia, com todas as alterações auditadas (autor, timestamp).

A **prioridade do plano** (ROTINA/URGENTE/CRITICO) muda automaticamente conforme alterações de saúde registadas (`US-16`):
- Quando `AlteracaoEstadoSaude.severidade = CRITICO`, o plano correspondente muda para prioridade CRITICO
- Quando `AlteracaoEstadoSaude.severidade = URGENTE` e plano estava em ROTINA, muda para URGENTE
- O sistema exibe flag visual ao cuidador indicando mudança de prioridade

O plano **encerra automaticamente** no check-out (transição de `ativo = true` para `ativo = false`).

---

## Índices e Integridade Sugeridos

- Índice por `planoCuidados.animalId` — para listar histórico de planos do animal
- Índice por `planoCuidados.estadiaId` — para acesso rápido ao plano ativo (UNIQUE)
- Índice por `tarefaCuidado.planoCuidadosId` — para listar tarefas de um plano
- Índice por `registoCuidado.estadiaId` — para listar registos da estadia
- Índice por `registoCuidado.dataHora DESC` — para ordenação do historial
- Índice por `servicoExtra.estadiaId` — para cálculo de extras no check-out
- Índice por `intervencaoClinica.estadiaId` — para cálculo de clínica no check-out
- Índice por `nota.reservaId` — para instruções associadas à reserva
- Índice por `alteracaoEstadoSaude.estadiaId` — para histórico clínico e triggers

Restrições de chave estrangeira obrigatórias para garantir integridade referencial e cascata de soft-deletes se aplicável.

---

## Data Integrity & Audit

Todas as entidades devem guardar:
- `createdAt` (TIMESTAMP DEFAULT CURRENT_TIMESTAMP)
- `createdBy` (BIGINT — user ID)
- `updatedAt` (TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)
- `updatedBy` (BIGINT — user ID)

Isto permite rastreabilidade completa e auditoria de quem fez cada ação e quando.
