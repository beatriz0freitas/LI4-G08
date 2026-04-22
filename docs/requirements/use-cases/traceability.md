# Traceability Matrix

Este documento liga os use cases UC-01..UC-13 aos services, metodos principais e diagramas de arquitetura.

| Use Case | Service / metodo principal | Diagramas de cobertura | Observacoes |
|---|---|---|---|
| UC-01 - Autenticar no Sistema | `UtilizadorService.autenticar()` | `classes.mmd`, `componentes.mmd` | Fluxo de suporte de acesso ao sistema. |
| UC-02 - Consultar Disponibilidade de Alojamentos | `AlojamentoService.consultarDisponibilidade()` | `classes.mmd`, `componentes.mmd` | Base para pesquisa de alojamentos disponiveis. |
| UC-03 - Registar Tutor e Animal | `UtilizadorService.registarTutor()` + `AnimalService.registarAnimal()` | `classes.mmd`, `componentes.mmd` | Cria a base de clientes e animais. |
| UC-04 - Criar Reserva | `ReservaService.criarReserva()` | `classes.mmd`, `seq-reserva.mmd`, `seq-reserva.txt`, `componentes.mmd` | Fluxo principal de reservas. |
| UC-05 - Cancelar Reserva | `ReservaService.cancelarReserva()` | `classes.mmd`, `componentes.mmd` | Atualiza estado da reserva e disponibilidade. |
| UC-06 - Registar Check-in | `EstadiaService.registarCheckIn()` | `classes.mmd`, `seq-checkin.mmd`, `seq-checkin.txt`, `componentes.mmd` | Abre a estadia e atualiza limpeza. |
| UC-07 - Registar Check-out | `EstadiaService.registarCheckOut()` | `classes.mmd`, `seq-checkout.mmd`, `seq-checkout.txt`, `componentes.mmd` | Fecha a estadia e valida pendencias. |
| UC-08 - Processar Faturacao e Pagamento | `FaturacaoService.emitirFatura()` + `PagamentoService.registarPagamento()` | `classes.mmd`, `seq-faturacao.mmd`, `seq-faturacao.txt`, `componentes.mmd` | Gera a fatura e regista o pagamento. |
| UC-09 - Registar Cuidados Diarios | `EstadiaService.registarCuidadoDiario()` | `classes.mmd`, `seq-cuidados.mmd`, `seq-cuidados.txt`, `componentes.mmd` | Mantem o historico operacional da estadia. |
| UC-10 - Registar Servico Extra | `EstadiaService.registarServicoExtra()` | `classes.mmd`, `componentes.mmd` | Pode ser associado a faturacao posterior. |
| UC-11 - Gerir Historial Clinico | `ClinicaService.registarIntervencaoClinica()` + `ClinicaService.consultarHistorial()` | `classes.mmd`, `seq-veterinario.mmd`, `seq-veterinario.txt`, `componentes.mmd` | Cobre a logica clinica e rastreabilidade. |
| UC-12 - Registar Limpeza de Alojamento | `AlojamentoService.registarLimpeza()` | `classes.mmd`, `seq-limpeza.mmd`, `seq-limpeza.txt`, `componentes.mmd` | Atualiza o ciclo operacional do alojamento. |
| UC-13 - Consultar Dashboard e Gerar Relatorios | `RelatorioService.gerarDashboard()` + `RelatorioService.gerarRelatorios()` | `classes.mmd`, `componentes.mmd` | Consolida indicadores e leitura executiva. |

## Regras de uso

- Cada UC deve ter pelo menos uma ligacao valida a um metodo de service.
- Os fluxos com sequencia obrigatoria devem aparecer tambem em `*.mmd` e `*.txt`.
- Os diagramas devem manter coerencia com os contracts em `specs/001-arquitetura-projeto/contracts/`.