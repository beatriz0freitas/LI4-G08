# Traceability Matrix

Este documento liga os use cases UC-01..UC-13 aos services, metodos principais e diagramas de arquitetura.

| Use Case | Service / metodo principal | Diagramas de cobertura | Observacoes |
|---|---|---|---|
| UC-01 - Autenticar no Sistema | `ColaboradorUserDetailsService.loadUserByUsername()` | `class-diagram.mmd`, `UC-01.mmd`, `UC-01.puml`, `components.mmd` | Fluxo de suporte de acesso ao sistema. |
| UC-02 - Consultar Disponibilidade de Alojamentos | `IAlojamentoService.consultarDisponibilidade()` | `class-diagram.mmd`, `UC-02.mmd`, `UC-02.puml`, `components.mmd` | Base para pesquisa de alojamentos disponiveis. |
| UC-03 - Registar Tutor e Animal | `ITutorService.registar()` + `IAnimalService.registar()` | `class-diagram.mmd`, `UC-03.mmd`, `UC-03.puml`, `components.mmd` | Cria a base de clientes e animais. |
| UC-04 - Criar Reserva | `IReservaService.criar()` | `class-diagram.mmd`, `UC-04.mmd`, `UC-04.puml`, `components.mmd` | Cria reserva ativa apos validacao centralizada de disponibilidade. |
| UC-05 - Cancelar Reserva | `IReservaService.cancelar()` | `class-diagram.mmd`, `UC-05.mmd`, `UC-05.puml`, `components.mmd` | Atualiza estado da reserva e disponibilidade. |
| UC-06 - Registar Check-in | `IEstadiaService.abrirEstadiaPorReserva()` | `class-diagram.mmd`, `UC-06.mmd`, `UC-06.puml`, `components.mmd` | Abre estadia, confirma reserva, impede duplicacao por animal e regista pagamento base. |
| UC-07 - Registar Check-out | `IEstadiaService.checkOut()` | `class-diagram.mmd`, `UC-07.mmd`, `UC-07.puml`, `components.mmd` | Fecha estadia, conclui reserva, regista pagamento complementar e marca limpeza. |
| UC-08 - Processar Faturacao e Pagamento | `IPagamentoService.registrarPagamento()` + `IPagamentoService.registrarPagamentoCheckOut()` + `IPagamentoService.calcularCobrancaComplementar()` | `class-diagram.mmd`, `UC-08.mmd`, `UC-08.puml`, `components.mmd` | Calcula valor base/complementar e regista pagamento com metodo obrigatorio. |
| UC-09 - Registar Cuidados Diarios | `IRegistoCuidadoService.create()` + `IPlanoCuidadosService.criarPlanoParaEstadia()` | `class-diagram.mmd`, `UC-09.mmd`, `UC-09.puml`, `components.mmd` | Mantem cuidados diarios e plano de cuidados da estadia. |
| UC-10 - Registar Servico Extra | `IServicoExtraService.register()` | `class-diagram.mmd`, `UC-10.mmd`, `UC-10.puml`, `components.mmd` | Regista servico extra validado e associado a faturacao complementar. |
| UC-11 - Gerir Historial Clinico | `IIntervencaoClinicaService.register()` + `IHistoricoService.consultar()` | `class-diagram.mmd`, `UC-11.mmd`, `UC-11.puml`, `components.mmd` | Cobre logica clinica, filtros de historico e rastreabilidade. |
| UC-12 - Registar Limpeza de Alojamento | `IAlojamentoService.marcarLimpezaConcluida()` | `class-diagram.mmd`, `UC-12.mmd`, `UC-12.puml`, `components.mmd` | Atualiza o ciclo operacional do alojamento. |
| UC-13 - Consultar Dashboard e Gerar Relatorios | `IRelatorioService.gerarRelatorio()` + `IRelatorioService.gerarAgrupamentos()` + `IRelatorioService.gerarCsv()` + `IRelatorioService.gerarPdf()` | `class-diagram.mmd`, `UC-13.mmd`, `UC-13.puml`, `components.mmd` | Consolida indicadores, agrupamentos, exportacao CSV/PDF e leitura executiva. |

## Regras de uso

- Cada UC deve ter pelo menos uma ligacao valida a um metodo de service.
- Os fluxos com sequencia obrigatoria devem aparecer tambem em `UC-*.mmd` e `UC-*.puml`.
- Os diagramas devem manter coerencia com os contracts em `specs/001-arquitetura-projeto/contracts/`.
