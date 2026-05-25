# Casos de Teste — Rastreabilidade RF → Testes

**Projeto:** PatasBigodesApp (LI4-G08)  
**Data:** 2026-05-25  
**Gerado com apoio de:** LLM, com revisão manual sobre requisitos e código

> Cada caso de teste (TC) está ligado ao requisito funcional (RF) que verifica.  
> A coluna "Teste automatizado" indica o método `@Test` correspondente ou a classe de teste quando o requisito é coberto por mais do que um método.

---

## RF-01 — Dashboard operacional

| ID | Descrição | Tipo | Teste automatizado |
|----|-----------|------|--------------------|
| TC-01.1 | Dashboard apresenta taxa de ocupação, estadias ativas e reservas futuras | Web | `DashboardControllerTest` (integração) |
| TC-01.2 | Taxa de ocupação calculada como (ocupados / total alojamentos) × 100 | Unit | `RelatorioServiceTest.gerarRelatorioDeveAgregarMetricasPrincipais` |

---

## RF-02 — Gestão de perfis de acesso

| ID | Descrição | Tipo | Teste automatizado |
|----|-----------|------|--------------------|
| TC-02.1 | Criação de colaborador com password encriptada em BCrypt | Unit | `ColaboradorServiceTest.criarDeveCodificarPasswordEGuardarTipoEnum` |
| TC-02.2 | Criação falha se password em branco | Unit | `ColaboradorServiceTest.criarSemPasswordDeveFalhar` |
| TC-02.3 | Criação falha se username já existe | Unit | `ColaboradorServiceTest.criarComUsernameExistenteDeveFalhar` |
| TC-02.4 | Criação falha se email já existe | Unit | `ColaboradorServiceTest.criarComEmailExistenteDeveFalhar` |
| TC-02.5 | Desativar colaborador mantém registo e altera estado | Unit | `ColaboradorServiceTest.desativarDeveManterRegistoEAlterarEstado` |
| TC-02.6 | Atualizar colaborador altera campos e guarda | Unit | `ColaboradorServiceTest.atualizarDeveAlterarCamposEGuardar` |
| TC-02.7 | Acesso a `/colaboradores` restrito ao role DIRETOR | Web | `ColaboradorControllerTest.listarDeveRenderizarColaboradores` |
| TC-02.8 | Acesso a `/relatorios` proibido sem DIRETOR | Web | `SecurityAuthorizationMvcTest` |

---

## RF-03 — Relatórios operacionais

| ID | Descrição | Tipo | Teste automatizado |
|----|-----------|------|--------------------|
| TC-03.1 | Gerar relatório agrega métricas principais (taxa ocupação, faturação, serviços extra) | Unit | `RelatorioServiceTest.gerarRelatorioDeveAgregarMetricasPrincipais` |
| TC-03.2 | Período inválido (fim antes de início) lança exceção | Unit | `RelatorioServiceTest.periodoInvalidoDeveFalhar` |
| TC-03.3 | Datas nulas lançam exceção com mensagem "Período" | Unit | `RelatorioServiceTest.periodoComDatasNulasDeveFalhar` |
| TC-03.4 | Exportação CSV contém cabeçalho e dados do relatório | Unit | `RelatorioServiceTest.gerarCsvDeveConterCabecalhoEDadosDoRelatorio` |
| TC-03.5 | Exportação PDF devolve bytes não vazios com conteúdo esperado | Unit | `RelatorioServiceTest.gerarPdfDeveRetornarBytesNaoVazios` |
| TC-03.6 | `filtroMesAtual` devolve período do 1º dia até hoje | Unit | `RelatorioServiceTest.filtroMesAtualDeveRetornarPrimeiroDiaDeMesAteDia` |

---

## RF-04 — Registo de tutores e animais

| ID | Descrição | Tipo | Teste automatizado |
|----|-----------|------|--------------------|
| TC-04.1 | Registar tutor com NIF único persiste e devolve entidade | Unit | `TutorServiceTest.registarDeveGuardarTutorComNifUnico` |
| TC-04.2 | Registar tutor com NIF duplicado lança exceção | Unit | `TutorServiceTest.registarDeveFalharComNifDuplicado` |
| TC-04.3 | Procurar tutor por NIF devolve resultado correto | Unit | `TutorServiceTest.procurarPorNifDeveRetornarTutorCorreto` |
| TC-04.4 | Procurar tutor por nome (vazio) devolve lista vazia | Unit | `TutorServiceTest.procurarPorNomeComListaVaziaDeveRetornarVazio` |
| TC-04.5 | Atualizar tutor com mesmo NIF é aceite | Unit | `TutorServiceTest.atualizarComMesmoNifDeveSerAceite` |
| TC-04.6 | Atualizar tutor com NIF de outro lança exceção | Unit | `TutorServiceTest.atualizarComNifDeOutroTutorDeveFalhar` |
| TC-04.7 | Registar animal liga-o ao tutor correto | Unit | `AnimalServiceTest.registarDeveAssociarAnimalAoTutor` |
| TC-04.8 | Registar animal com tutor inexistente lança exceção | Unit | `AnimalServiceTest.registarDeveFalharSeTutorNaoExiste` |
| TC-04.9 | Procurar animal por nome retorna resultados | Unit | `AnimalServiceTest.procurarPorNomeDeveRetornarResultados` |
| TC-04.10 | Atualizar animal persiste alterações | Unit | `AnimalServiceTest.atualizarDeveAlterarCampos` |
| TC-04.11 | Eliminar animal delega no repositório | Unit | `AnimalServiceTest.eliminarDeveDelegarParaRepositorio` |

---

## RF-05 — Histórico de estadias e pagamentos

| ID | Descrição | Tipo | Teste automatizado |
|----|-----------|------|--------------------|
| TC-05.1 | Consultar histórico sem estadiaId devolve lista vazia | Unit | `HistoricoServiceTest.consultarSemEstadiaIdRetornaListaVazia` |
| TC-05.2 | Consultar histórico com estadiaId agrega todos os tipos (cuidados, extras, intervenções, notas) | Unit | `HistoricoServiceTest.consultarComEstadiaIdAgregaTodosOsTipos` |
| TC-05.3 | Histórico sem reserva associada não carrega notas | Unit | `HistoricoServiceTest.consultarSemReservaAssociadaNaoCarregaNotas` |
| TC-05.4 | Histórico ordenado por dataHora descendente | Unit | `HistoricoServiceTest.consultarRetornaResultadosOrdenadosPorDataHoraDescendente` |
| TC-05.5 | Listar histórico delega no repositório com filtros | Unit | `HistoricoServiceTest.listarHistoricoDeveDelegarNoRepositorio` |

---

## RF-06 — Controlo de disponibilidade de boxes

| ID | Descrição | Tipo | Teste automatizado |
|----|-----------|------|--------------------|
| TC-06.1 | Alojamento com tipo incompatível com espécie devolve falso | Unit | `AlojamentoServiceTest.estaDisponivelComEspecieRetornaFalsoSeTipoIncompativel` |
| TC-06.2 | `consultarDisponibilidade` rejeita datas nulas | Unit | `AlojamentoServiceTest.consultarDisponibilidadeDeveRejeitarDatasInvalidas` |
| TC-06.3 | `consultarDisponibilidade` rejeita fim antes de início | Unit | `AlojamentoServiceTest.consultarDisponibilidadeDeveRejeitarDatasInvalidas` |
| TC-06.4 | Contar disponíveis conta apenas estado CONCLUIDO | Unit | `AlojamentoServiceTest.contarAlojamentosDisponiveisDeveContarConcluidos` |
| TC-06.5 | Verificação de disponibilidade rápida (< 200ms) | Integration | `AlojamentoServiceTimingTests` (requer Docker) |
| TC-06.6 | Disponibilidade na presença de reservas sobrepostas | Unit | `DisponibilidadeServiceTest` |

---

## RF-07 — Gestão de reservas

| ID | Descrição | Tipo | Teste automatizado |
|----|-----------|------|--------------------|
| TC-07.1 | Criar reserva válida persiste entidade | Unit | `ReservaServiceCreateTest.criarReservaValidaDeveRetornarEntidade` |
| TC-07.2 | Criar reserva com datas inválidas lança exceção | Unit | `ReservaServiceCreateTest` |
| TC-07.3 | Overbooking não é permitido na mesma box | Integration | `ReservaServiceTests.shouldPreventOverbooking` (requer Docker) |
| TC-07.4 | Cancelar reserva ativa altera estado para CANCELADA | Unit | `ReservaServiceUnitTest.cancelarDeveAlterarEstadoParaCancelada` |
| TC-07.5 | Cancelar reserva não cancelável lança exceção | Unit | `ReservaServiceUnitTest.cancelarDeveRejeitarReservaNaoCancelavel` |
| TC-07.6 | Concluir reserva ativa altera estado para CONCLUIDA | Unit | `ReservaServiceUnitTest.concluirDeveAlterarEstadoParaConcluida` |
| TC-07.7 | Concluir reserva não ativa lança exceção | Unit | `ReservaServiceUnitTest.concluirDeveRejeitarReservaNaoAtiva` |
| TC-07.8 | Listar reservas por tutor delega no repositório | Unit | `ReservaServiceUnitTest.procurarPorTutorDeveDelegarNoRepositorio` |
| TC-07.9 | Contar reservas ativas delega no repositório | Unit | `ReservaServiceUnitTest.contarReservasAtivasDeveDelegarNoRepositorio` |

---

## RF-08 — Check-in e pagamento de estadia

| ID | Descrição | Tipo | Teste automatizado |
|----|-----------|------|--------------------|
| TC-08.1 | Abrir estadia por reserva ativa cria registo EM_CURSO | Unit | `EstadiaServiceTest.abrirEstadiaPorReservaDeveRetornarEstadiaAtiva` |
| TC-08.2 | Abrir estadia com reserva não ativa lança exceção | Unit | `EstadiaServiceTest.abrirEstadiaPorReservaDeveFalharSemReservaAtiva` |
| TC-08.3 | POST check-in válido redireciona para /estadias com flash de sucesso | Web | `EstadiaControllerTest.checkInValidoDeveRedirecionarParaEstadiasComSucesso` |
| TC-08.4 | POST check-in com erro redireciona para /reservas com flash de erro | Web | `EstadiaControllerTest.checkInComErroDeveRedirecionarParaReservas` |
| TC-08.5 | Calcular valor base de estadia (1 dia) | Unit | `PagamentoServiceTest.calcularValorBaseParaEstadiaDe1Dia` |
| TC-08.6 | Calcular valor base de estadia (2 dias) | Unit | `PagamentoServiceTest.calcularValorBaseParaEstadiaDe2Dias` |

---

## RF-09 — Check-out e faturação complementar

| ID | Descrição | Tipo | Teste automatizado |
|----|-----------|------|--------------------|
| TC-09.1 | Check-out de estadia EM_CURSO altera estado para TERMINADA | Unit | `EstadiaServiceTest.checkOutDeveRetornarEstadiaTerminada` |
| TC-09.2 | Check-out de estadia já terminada lança exceção | Unit | `EstadiaServiceTest.checkOutDeveFalharSeEstadiaNaoEmCurso` |
| TC-09.3 | Check-out de estadia inexistente lança exceção | Unit | `EstadiaServiceTest.checkOutDeveFalharSeNaoExiste` |
| TC-09.4 | POST check-out válido redireciona para /historico com flash de sucesso | Web | `EstadiaControllerTest.checkOutValidoDeveRedirecionarParaHistorico` |
| TC-09.5 | POST check-out com erro redireciona para /historico com flash de erro | Web | `EstadiaControllerTest.checkOutComErroDeveRedirecionarParaHistoricoComErro` |
| TC-09.6 | Calcular extras de estadia agrega serviços extra | Unit | `PagamentoServiceTest.calcularExtrasDeveAgregarServicosExtra` |

---

## RF-10 — Registo de pagamentos

| ID | Descrição | Tipo | Teste automatizado |
|----|-----------|------|--------------------|
| TC-10.1 | Registar pagamento com dados válidos persiste | Unit | `PagamentoServiceTest.registrarPagamentoDeveGuardarComValorEMetodo` |
| TC-10.2 | Registar pagamento com estado nulo lança exceção | Unit | `PagamentoServiceTest.registrarPagamentoComEstadoNuloDeveFalhar` |
| TC-10.3 | Registar pagamento para estadia inexistente lança exceção | Unit | `PagamentoServiceTest.registrarPagamentoParaEstadiaInexistenteDeveFalhar` |
| TC-10.4 | Registar pagamento check-out sem método lança exceção | Unit | `PagamentoServiceTest.registrarPagamentoCheckOutSemMetodoDeveFalhar` |
| TC-10.5 | POST /pagamentos válido redireciona com sucesso | Web | `PagamentoControllerTest.registrarValidoDeveRedirecionarParaHistorico` |
| TC-10.6 | POST /pagamentos com erro adiciona flash de erro | Web | `PagamentoControllerTest.registrarComErroDeveAdicionarMensagemFlash` |

---

## RF-11 — Plano de cuidados

| ID | Descrição | Tipo | Teste automatizado |
|----|-----------|------|--------------------|
| TC-11.1 | GET /plano-cuidados renderiza página | Web | `PlanoCuidadosControllerTest` |

> Nota: `PlanoCuidadosService` está marcado como "Not implemented yet" — funcionalidade pendente.

---

## RF-12 — Registo de cuidados prestados

| ID | Descrição | Tipo | Teste automatizado |
|----|-----------|------|--------------------|
| TC-12.1 | Criar registo de cuidado para estadia EM_CURSO persiste | Unit | `RegistoCuidadoServiceTest.createDeveGuardarRegistoCuidadoParaEstadiaEmCurso` |
| TC-12.2 | Criar registo para estadia terminada lança exceção | Unit | `RegistoCuidadoServiceTest.createDeveRejeitarEstadiaTerminada` |
| TC-12.3 | Criar registo para estadia inexistente lança exceção | Unit | `RegistoCuidadoServiceTest.createDeveLancarExcecaoSeEstadiaNaoExistir` |
| TC-12.4 | Listar cuidados por estadia devolve página com registos | Unit | `RegistoCuidadoServiceTest.listByEstadiaDeveRetornarPaginaComRegistos` |

---

## RF-13 — Alterações ao estado de saúde

| ID | Descrição | Tipo | Teste automatizado |
|----|-----------|------|--------------------|
| TC-13.1 | Registar alteração com severidade CRITICO persiste | Unit | `AlteracaoEstadoSaudeServiceTest.registerDevePersistirAlteracaoComSeveridade` |
| TC-13.2 | Severidade em minúsculas é normalizada | Unit | `AlteracaoEstadoSaudeServiceTest.registerDeveAceitarSeveridadeEmMinusculas` |
| TC-13.3 | Severidade inválida lança exceção | Unit | `AlteracaoEstadoSaudeServiceTest.registerDeveRejeitarSeveridadeInvalida` |
| TC-13.4 | Severidade nula é aceite | Unit | `AlteracaoEstadoSaudeServiceTest.registerDeveAceitarSeveridadeNula` |
| TC-13.5 | Estadia inexistente lança exceção | Unit | `AlteracaoEstadoSaudeServiceTest.registerDeveLancarExcecaoSeEstadiaNaoExistir` |

---

## RF-14 — Histórico clínico e intervenções

| ID | Descrição | Tipo | Teste automatizado |
|----|-----------|------|--------------------|
| TC-14.1 | Histórico com estadiaId agrega intervenções clínicas | Unit | `HistoricoServiceTest.consultarComEstadiaIdAgregaTodosOsTipos` |
| TC-14.2 | GET /clinica renderiza página clínica | Web | `ClinicaControllerTest` |

---

## RF-15 — Estado de limpeza dos alojamentos

| ID | Descrição | Tipo | Teste automatizado |
|----|-----------|------|--------------------|
| TC-15.1 | Marcar alojamento como PENDENTE altera estado | Unit | `AlojamentoServiceTest.marcarPendenteLimpezaDeveAtualizarEstado` |
| TC-15.2 | Marcar limpeza como CONCLUIDA altera estado | Unit | `AlojamentoServiceTest.marcarLimpezaConcluidaDeveAtualizarEstado` |
| TC-15.3 | Contar pendentes delega no repositório | Unit | `AlojamentoServiceTest.contarAlojamentosPendentesLimpezaDeveDelegarParaRepositorio` |
| TC-15.4 | GET /limpeza lista alojamentos pendentes | Integration | `LimpezaControllerTest` (requer Docker) |
| TC-15.5 | POST /limpeza/{id}/limpo atualiza estado | Integration | `LimpezaControllerTest` (requer Docker) |

---

## RF-16 — Agendamento de serviços opcionais

| ID | Descrição | Tipo | Teste automatizado |
|----|-----------|------|--------------------|
| TC-16.1 | Validar período nulo lança exceção | Unit | `RegraDominioServiceTest.validarPeriodoComInicioNuloDeveFalhar` |
| TC-16.2 | Período invertido (fim antes de início) lança exceção | Unit | `RegraDominioServiceTest.validarPeriodoComDatasInvertidasDeveFalhar` |
| TC-16.3 | Período com mesmo dia lança exceção | Unit | `RegraDominioServiceTest.validarPeriodoComMesmoDiaDeveFalhar` |
| TC-16.4 | Período válido não lança exceção | Unit | `RegraDominioServiceTest.validarPeriodoValidoNaoDeveFalhar` |

---

## RF-17 — Registo de serviços extra

| ID | Descrição | Tipo | Teste automatizado |
|----|-----------|------|--------------------|
| TC-17.1 | Registar serviço extra para estadia EM_CURSO persiste | Unit | `ServicoExtraServiceTest.registerDeveGuardarServicoExtraParaEstadiaEmCurso` |
| TC-17.2 | Registar serviço extra para estadia terminada lança exceção | Unit | `ServicoExtraServiceTest.registerDeveRejeitarEstadiaTerminada` |
| TC-17.3 | Registar serviço extra para estadia inexistente lança exceção | Unit | `ServicoExtraServiceTest.registerDeveLancarExcecaoSeEstadiaNaoExistir` |
| TC-17.4 | Erro em calcularExtras não impede registo do serviço | Unit | `ServicoExtraServiceTest.registerNaoFalhaSeCalcularExtrasFalhar` |
| TC-17.5 | Listar serviços por estadia devolve página com resultados | Unit | `ServicoExtraServiceTest.listByEstadiaDeveRetornarPaginaComServicos` |

---

## Resumo de Rastreabilidade

| RF | Nº TCs | Cobertura |
|----|--------|-----------|
| RF-01 | 2 | ✅ |
| RF-02 | 8 | ✅ |
| RF-03 | 6 | ✅ |
| RF-04 | 11 | ✅ |
| RF-05 | 5 | ✅ |
| RF-06 | 6 | ✅ |
| RF-07 | 9 | ✅ |
| RF-08 | 6 | ✅ |
| RF-09 | 6 | ✅ |
| RF-10 | 6 | ✅ |
| RF-11 | 1 | ⚠️ parcial |
| RF-12 | 4 | ✅ |
| RF-13 | 5 | ✅ |
| RF-14 | 2 | ✅ |
| RF-15 | 5 | ✅ |
| RF-16 | 4 | ✅ |
| RF-17 | 5 | ✅ |
| **Total** | **91** | **16/17 ✅** |

---

## Verificação da SRS

Esta secção resume a satisfação dos requisitos da SRS com base nos testes automatizados existentes e nos resultados de `make test-integration`.

### Requisitos Funcionais

| Grupo | Estado | Evidência |
|-------|--------|-----------|
| RF-01 a RF-10 | Verificado | Casos TC-01.1 a TC-10.6 |
| RF-11 | Parcial | Existe teste de controller; serviço de plano de cuidados ainda pendente |
| RF-12 a RF-17 | Verificado | Casos TC-12.1 a TC-17.5 |

### Regras de Domínio

| RD | Estado | Evidência principal |
|----|--------|---------------------|
| RD-01 | Verificado | TC-06.2, TC-06.3, TC-06.6 |
| RD-02 | Verificado | TC-08.1, TC-08.2 |
| RD-03 | Verificado | TC-08.1, TC-09.1, TC-09.2 |
| RD-04 | Verificado | TC-08.5, TC-08.6, TC-09.6, TC-10.1 |
| RD-05 | Verificado | TC-04.7, TC-04.8 |
| RD-06 | Verificado | TC-07.4, TC-07.5 |
| RD-07 | Verificado | TC-07.3 e testes de integração catalogados |
| RD-08 | Verificado | TC-06.1 |
| RD-09 | Verificado | TC-17.1, TC-17.4, TC-17.5 |

### Requisitos Não Funcionais

| RNF | Estado | Evidência / Limitação |
|-----|--------|-----------------------|
| RNF-01 | Parcial | Testes temporais foram executados com MySQL, mas não houve teste de carga com 10 utilizadores simultâneos |
| RNF-02 | Parcial | MockMvc valida templates e fluxos; não houve teste formal com utilizadores |
| RNF-03 | Parcial | Suportado por desenho operacional; disponibilidade real depende da infraestrutura |
| RNF-04 | Verificado | `SecurityAuthorizationMvcTest`, `HistoricoAuthorizationMvcTest` |
| RNF-05 | Verificado | BCrypt, autenticação e autorização por perfil |
| RNF-06 | Parcial | Suportado por aplicação web e Docker; sem validação em hardware real do hotel |
| RNF-07 | Parcial | Sem teste de carga ou crescimento de dados nesta etapa |
| RNF-08 | Parcial | Cópias de segurança não exercitadas por teste automatizado |
| RNF-09 | Parcial | Arquitetura permite extensão, mas operação contínua não foi ensaiada |

### Testes de Aceitação por UC

| UC | Estado | Evidência |
|----|--------|-----------|
| UC-01 | Verificado | Testes de autenticação/autorização |
| UC-02 | Verificado | Testes de disponibilidade |
| UC-03 | Verificado | Testes de tutor e animal |
| UC-04 | Verificado | Testes de criação de reserva |
| UC-05 | Verificado | Testes de cancelamento de reserva |
| UC-06 | Verificado | Testes de check-in |
| UC-07 | Verificado | Testes de check-out |
| UC-08 | Verificado | Testes de pagamento |
| UC-09 | Verificado | Testes de registo de cuidados |
| UC-10 | Verificado | Testes de serviços extra |
| UC-11 | Verificado | Testes de histórico e clínica |
| UC-12 | Verificado | Testes de limpeza |
| UC-13 | Verificado | Testes de relatórios e dashboard |
