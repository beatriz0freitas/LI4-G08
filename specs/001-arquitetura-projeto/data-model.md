# Data Model: arquitetura-projeto

## Objetivo do Modelo

Documentar o modelo de informacao que orienta os diagramas arquiteturais (classes, sequencia e componentes), incluindo entidades de dominio, artefactos de documentacao e relacoes de rastreabilidade com use cases.

## Entidades de Dominio

### Tutor
- Campos: `id`, `nome`, `nif`, `email`, `contacto`, `endereco`
- Relacoes:
  - 1..* com `Animal`
  - 1..* com `Reserva` (via associacao operacional)

### Colaborador
- Campos: `id`, `nome`, `email`, `contacto`, `perfilAcesso`
- Relacoes:
  - 1..* com `Reserva` (criacao/auditoria)
  - 1..* com `Estadia` (check-in/check-out)

### Animal
- Campos: `id`, `nome`, `especie`, `raca`, `estadoSaude`, `necessidadesEspeciais`
- Relacoes:
  - 1..1 com `HistorialClinico`
  - *..1 com `Tutor`
  - 1..* com `Reserva`/`Estadia` (ao longo do tempo)

### HistorialClinico
- Campos: `id`
- Metodos relevantes: `getUltimaIntervencao()`, `temPrescricaoAtiva()`
- Relacoes:
  - 1..* com `IntervencaoClinica`

### IntervencaoClinica
- Campos: `id`, `dataHora`, `descricao`, `custo`
- Relacoes:
  - *..1 com `HistorialClinico`
  - 0..1 com `ServicoExtra` (restricao semantica obrigatoria)

### Alojamento
- Campos: `id`, `tipoAlojamento`, `estadoLimpeza`, `capacidade`, `precoDiario`
- Relacoes:
  - 1..* com `Reserva`
  - 1..* com `Estadia`

### Reserva
- Campos: `id`, `codigo`, `dataInicio`, `dataFim`, `estadoReserva`, `criadaPor`
- Relacoes:
  - *..1 com `Tutor`
  - *..1 com `Animal`
  - *..1 com `Alojamento`
  - 0..1 com `Estadia`

### Estadia
- Campos: `id`, `codigo`, `dataCheckIn`, `dataCheckOut`, `estadoEstadia`
- Relacoes:
  - 1..1 com `Reserva`
  - *..1 com `Tutor`
  - *..1 com `Animal`
  - *..1 com `Alojamento`
  - 1..1 com `Fatura`
  - 1..* com `Cuidado`, `Nota`, `ServicoExtra`

### Cuidado
- Campos: `id`, `tipoCuidado`, `descricao`, `dataHora`, `realizadoPor`
- Relacoes:
  - *..1 com `Estadia`

### Nota
- Campos: `id`, `descricao`, `dataHora`, `criadaPor`
- Relacoes:
  - *..1 com `Estadia`

### ServicoExtra
- Campos: `id`, `tipoServicoExtra`, `descricao`, `custo`, `registadoPor`, `data`
- Relacoes:
  - *..1 com `Estadia`

### Fatura
- Campos: `id`, `numero`, `dataEmissao`, `valorBase`, `desconto`, `total`, `estadoFatura`
- Relacoes:
  - 1..1 com `Estadia`
  - *..1 com `Tutor`
  - 1..* com `Pagamento`

### Pagamento
- Campos: `id`, `valor`, `metodoPagamento`, `estadoPagamento`, `dataPagamento`, `dataVencimento`
- Relacoes:
  - *..1 com `Fatura`

## Enumeracoes de Dominio

- `EstadoSaude`
- `EstadoReserva`
- `EstadoEstadia`
- `EstadoFatura`
- `EstadoPagamento`
- `MetodoPagamento`
- `TipoCuidado`
- `TipoServicoExtra`
- `EstadoLimpeza`
- `PerfilAcesso`

## Entidades de Arquitetura (Documentacao)

### UseCase
- Campos: `codigo` (UC-01..UC-13), `titulo`, `origemRF`, `fluxoNormal`, `fluxoExcecao`

### DiagramArtifact
- Campos: `nome`, `tipo` (`class|sequence|component`), `formato` (`mmd|txt`), `path`, `statusValidacao`

### DecisionRecord (ADR)
- Campos: `id`, `titulo`, `contexto`, `decisao`, `consequencias`, `fundamentacao`

### TraceabilityLink
- Campos: `useCase`, `serviceMetodo`, `diagramArtifact`
- Regra: cada UC deve ter pelo menos uma ligacao valida a metodo e a diagrama.

## Regras de Validacao

- `Reserva -> Estadia` e no maximo 1:1 (Reserva pode nao gerar Estadia; Estadia exige Reserva).
- `Estadia -> Fatura` e exatamente 1:1.
- `Fatura -> Pagamento` e 1..*.
- `IntervencaoClinica <-> ServicoExtra` e opcional 0..1:0..1 com nota semantica.
- Toda entidade com metodo de regra de negocio dependente de dados externos deve delegar em service.

## Transicoes de Estado

### Reserva
- `Pendente -> Confirmada`
- `Pendente|Confirmada -> Cancelada`

### Estadia
- `EmCurso -> Encerrada`

### Alojamento (limpeza)
- `Concluido -> Ocupado (por estadia ativa)`
- `Ocupado -> PendenteLimpeza (apos check-out)`
- `PendenteLimpeza -> Concluido`

### Fatura
- `Emitida -> ParcialPendente -> Paga`

### Pagamento
- `Pendente -> Liquidado`
