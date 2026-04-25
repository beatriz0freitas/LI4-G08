# Traceability Matrix

Este documento liga os use cases UC-01..UC-13 aos services, metodos principais e diagramas de arquitetura.

| Use Case | Service / metodo principal | Diagramas de cobertura | Observacoes |
|---|---|---|---|
| UC-01 - Autenticar no Sistema | `IColaboradorService.autenticar()` | `class-diagram.mmd`, `UC-01.mmd`, `UC-01.puml`, `components.mmd` | Fluxo de suporte de acesso ao sistema. |
| UC-02 - Consultar Disponibilidade de Alojamentos | `IAlojamentoService.consultarDisponibilidade()` | `class-diagram.mmd`, `UC-02.mmd`, `UC-02.puml`, `components.mmd` | Base para pesquisa de alojamentos disponiveis. |
| UC-03 - Registar Tutor e Animal | `ITutorService.registarTutor()` + `IAnimalService.registarAnimal()` | `class-diagram.mmd`, `UC-03.mmd`, `UC-03.puml`, `components.mmd` | Cria a base de clientes e animais. |
| UC-04 - Criar Reserva | `IReservaService.criarReserva()` | `class-diagram.mmd`, `UC-04.mmd`, `UC-04.puml`, `components.mmd` | Fluxo principal de reservas. |
| UC-05 - Cancelar Reserva | `IReservaService.cancelarReserva()` | `class-diagram.mmd`, `UC-05.mmd`, `UC-05.puml`, `components.mmd` | Atualiza estado da reserva e disponibilidade. |
| UC-06 - Registar Check-in | `IEstadiaService.registarCheckIn()` | `class-diagram.mmd`, `UC-06.mmd`, `UC-06.puml`, `components.mmd` | Abre a estadia e atualiza limpeza. |
| UC-07 - Registar Check-out | `IEstadiaService.registarCheckOut()` | `class-diagram.mmd`, `UC-07.mmd`, `UC-07.puml`, `components.mmd` | Fecha a estadia e valida pendencias. |
| UC-08 - Processar Faturacao e Pagamento | `IPagamentoService.registarPagamentoCheckIn()` + `IPagamentoService.registarPagamentoCheckOut()` | `class-diagram.mmd`, `UC-08.mmd`, `UC-08.puml`, `components.mmd` | Gera a fatura e regista o pagamento. |
| UC-09 - Registar Cuidados Diarios | `IEstadiaService.registarCuidadoDiario()` | `class-diagram.mmd`, `UC-09.mmd`, `UC-09.puml`, `components.mmd` | Mantem o historico operacional da estadia. |
| UC-10 - Registar Servico Extra | `IServicoExtraService.registarServicoExtra()` | `class-diagram.mmd`, `UC-10.mmd`, `UC-10.puml`, `components.mmd` | Pode ser associado a faturacao posterior. |
| UC-11 - Gerir Historial Clinico | `IClinicaService.registarIntervencaoClinica()` + `IClinicaService.consultarHistorial()` | `class-diagram.mmd`, `UC-11.mmd`, `UC-11.puml`, `components.mmd` | Cobre a logica clinica e rastreabilidade. |
| UC-12 - Registar Limpeza de Alojamento | `IAlojamentoService.registarLimpezaConcluida()` | `class-diagram.mmd`, `UC-12.mmd`, `UC-12.puml`, `components.mmd` | Atualiza o ciclo operacional do alojamento. |
| UC-13 - Consultar Dashboard e Gerar Relatorios | `IRelatorioService.gerarDashboard()` + `IRelatorioService.gerarRelatorioPeriodo()` | `class-diagram.mmd`, `UC-13.mmd`, `UC-13.puml`, `components.mmd` | Consolida indicadores e leitura executiva. |

## Regras de uso

- Cada UC deve ter pelo menos uma ligacao valida a um metodo de service.
- Os fluxos com sequencia obrigatoria devem aparecer tambem em `UC-*.mmd` e `UC-*.puml`.
- Os diagramas devem manter coerencia com os contracts em `specs/001-arquitetura-projeto/contracts/`.