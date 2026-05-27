# Traceability Matrix

Este documento liga os casos de uso as user stories, requisitos e pontos principais de servico. A matriz mantem rastreabilidade funcional sem substituir os contratos Speckit nem os diagramas.

| Use Case | Atores principais | User Stories cobertas | Requisitos relacionados | Servico/metodo de referencia | Observacoes |
|---|---|---|---|---|---|
| UC-01 - Autenticar no Sistema | Todos os colaboradores | US-03 | RNF-04, RNF-06 | `ColaboradorUserDetailsService.loadUserByUsername()` | Garante acesso individual e aplicacao do perfil atribuido. |
| UC-02 - Consultar Disponibilidade de Alojamentos | Rececao, Diretor | US-01, US-08, US-14 | RF-06, RD-01, RNF-01, RNF-03, RNF-06 | `IAlojamentoService.consultarDisponibilidade()` | Suporta decisao antes da criacao de reservas e aplica disponibilidade centralizada. |
| UC-03 - Registar Tutor e Animal | Rececao | US-11 | RF-04, RD-05, RD-08, RNF-05 | `ITutorService.registar()` + `IAnimalService.registar()` | Garante dados base de tutor e animal para reservas e historico. |
| UC-04 - Criar Reserva | Rececao | US-08, US-15 | RF-06, RF-07, RF-16, RD-01, RD-05 | `IReservaService.criar()` | Cria reserva `ATIVA` apos validacao de disponibilidade e suporta servicos/instrucoes opcionais. |
| UC-05 - Cancelar Reserva | Rececao | US-08 | RF-07, RD-06 | `IReservaService.cancelar()` | Apenas aplicavel a reservas ainda nao convertidas em estadia. |
| UC-06 - Registar Check-in | Rececao | US-09, US-12, US-16 | RF-08, RF-10, RF-11, RD-02, RD-04, RD-07 | `IEstadiaService.abrirEstadiaPorReserva()` | Abre estadia, confirma reserva, impede duplicacao por animal e regista pagamento base. |
| UC-07 - Registar Check-out | Rececao | US-09, US-13, US-22 | RF-09, RF-10, RF-15, RD-03, RD-04 | `IEstadiaService.checkOut()` | Fecha estadia, conclui reserva, regista pagamento complementar e coloca alojamento pendente de limpeza. |
| UC-08 - Processar Pagamento | Rececao | US-12, US-13 | RF-10, RD-04 | `IPagamentoService.registrarPagamento()` + `IPagamentoService.registrarPagamentoCheckOut()` + `IPagamentoService.calcularCobrancaComplementar()` | Caso de uso incluido por check-in e check-out; metodo de pagamento e obrigatorio. |
| UC-09 - Registar Cuidados e Notas Operacionais | Cuidador, Rececao | US-16, US-17, US-18, US-19, US-21 | RF-11, RF-12, RF-13, RF-16, RD-10, RNF-02, RNF-09 | `IPlanoCuidadosService.obterPlanoPorEstadia()` + `IRegistoCuidadoService.create()` + `INotaService.create()` + `IAlteracaoEstadoSaudeService.register()` | Cobre continuidade operacional, tarefas do plano, notas e alteracoes ao estado de saude. |
| UC-10 - Registar Servico Extra | Cuidador, colaborador autorizado | US-20 | RF-17, RD-09, RD-11 | `IServicoExtraService.register()` | Regista servicos extra de catalogo ativo durante estadia ativa. |
| UC-11 - Gerir Historial Clinico | Medico Veterinario | US-24, US-25, US-26 | RF-13, RF-14, RF-17, RD-09, RNF-05 | `IHistoricoService.consultar()` + `IIntervencaoClinicaService.register()` | Cobre consulta de historial, alteracoes recentes e intervencoes clinicas faturaveis. |
| UC-12 - Registar Limpeza de Alojamento | Responsavel pela Limpeza | US-22, US-23 | RF-15, RD-01 | `IAlojamentoService.marcarLimpezaConcluida()` | Atualiza estado de limpeza para permitir nova disponibilidade. |
| UC-13 - Consultar Dashboard e Gerar Relatorios | Diretor | US-01, US-02, US-04, US-05 | RF-01, RF-02, RF-03, RF-10, RF-17, RNF-06, RNF-07 | `IDashboardService` + `IRelatorioService.gerarRelatorio()` + `IRelatorioService.gerarAgrupamentos()` + `IRelatorioService.gerarCsv()` + `IRelatorioService.gerarPdf()` | Consolida indicadores, agrupamentos, exportacao CSV/PDF e leitura executiva. |

## Regras de manutencao

- Cada UC deve ter pelo menos uma ligacao a uma user story, a um requisito e, quando ja implementado, a um servico de referencia.
- Os fluxos devem manter-se ao nivel de negocio; os metodos acima servem apenas para rastreabilidade com a implementacao.
- Os fluxos com sequencia obrigatoria devem aparecer tambem em `UC-*.mmd` e `UC-*.puml`.
- Sempre que forem adicionadas ou removidas user stories, requisitos ou contratos Speckit, esta matriz deve ser atualizada.
