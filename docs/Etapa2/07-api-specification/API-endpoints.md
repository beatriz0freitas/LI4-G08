# Endpoints MVC e JSON

Este documento regista os endpoints expostos pela aplicação `PatasBigodesApp`.
A lista foi revista contra os controllers existentes em `src/main/java/pt/hotel/animais/controller`.

## Autenticação e início

| Método | Caminho | Controller | Retorno/efeito |
|---|---|---|---|
| GET | `/` | `AuthController` | Página inicial `home/index` |
| GET | `/login` | `AuthController` | Página de autenticação `auth/login` |
| POST | `/login` | Spring Security | Processa autenticação |
| POST | `/logout` | Spring Security | Termina sessão |

## Direção

| Método | Caminho | Controller | Retorno/efeito |
|---|---|---|---|
| GET | `/dashboard` | `DashboardController` | Dashboard operacional |
| GET | `/relatorios` | `RelatorioController` | Página de relatórios com filtros por defeito |
| POST | `/relatorios/gerar` | `RelatorioController` | Recalcula relatório a partir do formulário |
| GET | `/relatorios/exportar/csv` | `RelatorioController` | Exporta CSV |
| GET | `/relatorios/exportar/pdf` | `RelatorioController` | Exporta PDF |
| GET | `/auditoria` | `AuditoriaController` | Lista eventos de auditoria |
| GET | `/auditoria/exportar/csv` | `AuditoriaController` | Exporta auditoria em CSV |

## Reservas e disponibilidade

| Método | Caminho | Controller | Retorno/efeito |
|---|---|---|---|
| GET | `/reservas` | `ReservaController` | Lista reservas; filtro opcional `estado` |
| GET | `/reservas/disponibilidade` | `ReservaController` | Mapa de disponibilidade; parâmetros `dataInicio`, `dataFim`, `tipo`, `alojamentoId` |
| POST | `/reservas/procurar-disponibilidade` | `ReservaController` | Procura alojamentos disponíveis e apresenta resultados |
| GET | `/reservas/novo` | `ReservaController` | Formulário multi-passo de nova reserva |
| POST | `/reservas` | `ReservaController` | Cria reserva |
| GET | `/reservas/{id}` | `ReservaController` | Detalhe/confirmação da reserva |
| POST | `/reservas/{id}/cancelar` | `ReservaController` | Cancela reserva |
| POST | `/reservas/{id}/concluir` | `ReservaController` | Informa que a conclusão ocorre no check-out |
| POST | `/reservas/{id}/confirmar` | `ReservaController` | Informa que a confirmação ocorre no check-in |

## Estadias, check-in e check-out

| Método | Caminho | Controller | Retorno/efeito |
|---|---|---|---|
| GET | `/estadias` | `EstadiaController` | Redireciona para `/estadias/lista` |
| GET | `/estadias/lista` | `EstadiaController` | Lista estadias; filtros `estado`, `dataInicio`, `dataFim` |
| GET | `/estadias/{id}` | `EstadiaController` | Detalhe da estadia |
| GET | `/estadias/check-in` | `EstadiaController` | Página de check-in; requer `reservaId` |
| POST | `/estadias/check-in` | `EstadiaController` | Abre estadia e regista pagamento base |
| GET | `/estadias/check-out` | `EstadiaController` | Página de check-out; requer `estadiaId` |
| POST | `/estadias/check-out` | `EstadiaController` | Fecha estadia, conclui reserva, cobra complemento e marca limpeza pendente |

## Tutores e animais

| Método | Caminho | Controller | Retorno/efeito |
|---|---|---|---|
| GET | `/tutores` | `TutorAnimalController` | Lista tutores; procura opcional `search` |
| GET | `/tutores/novo` | `TutorAnimalController` | Formulário de novo tutor |
| POST | `/tutores` | `TutorAnimalController` | Cria tutor |
| GET | `/tutores/{id}` | `TutorAnimalController` | Detalhe do tutor e animais associados |
| GET | `/tutores/{id}/editar` | `TutorAnimalController` | Formulário de edição do tutor |
| POST | `/tutores/{id}` | `TutorAnimalController` | Atualiza tutor |
| GET | `/tutores/{tutorId}/animais/novo` | `TutorAnimalController` | Formulário de novo animal do tutor |
| POST | `/tutores/{tutorId}/animais` | `TutorAnimalController` | Cria animal associado ao tutor |
| GET | `/animais` | `AnimalController` | Lista geral de animais |
| GET | `/animais/{id}` | `AnimalController` | Detalhe de animal |

## Alojamentos e limpeza

| Método | Caminho | Controller | Retorno/efeito |
|---|---|---|---|
| GET | `/alojamentos` | `AlojamentoController` | Lista alojamentos |
| GET | `/alojamentos/{id}` | `AlojamentoController` | Detalhe de alojamento |
| GET | `/limpeza` | `LimpezaController` | Lista alojamentos pendentes de limpeza |
| POST | `/limpeza/{id}/limpo` | `LimpezaController` | Marca alojamento como limpo |

## Clínica, histórico e cuidados

| Método | Caminho | Controller | Retorno/efeito |
|---|---|---|---|
| GET | `/clinica` | `ClinicaController` | Lista animais para acompanhamento clínico |
| GET | `/clinica/animais/{animalId}` | `ClinicaController` | Ficha clínica do animal |
| GET | `/clinica/intervencoes` | `ClinicaController` | Formulário de intervenção clínica; parâmetro `estadiaId` |
| POST | `/clinica/intervencoes/create` | `ClinicaController` | Regista intervenção clínica |
| GET | `/clinica/alteracoes` | `ClinicaController` | Formulário de alteração de estado de saúde; parâmetro `estadiaId` |
| POST | `/clinica/alteracoes/create` | `ClinicaController` | Regista alteração de estado de saúde |
| GET | `/historico` | `HistoricoController` | Histórico de estadias |
| GET | `/historico/eventos` | `HistoricoController` | Histórico clínico por eventos |
| GET | `/cuidados` | `RegistoCuidadoController` | Formulário/lista de registos de cuidado; parâmetro `estadiaId` |
| POST | `/cuidados/create` | `RegistoCuidadoController` | Regista cuidado |
| GET | `/plano-cuidados` | `PlanoCuidadosController` | Visão do turno ou plano de uma estadia quando existe `estadiaId` |
| POST | `/plano-cuidados/criar` | `PlanoCuidadosController` | Cria ou abre plano de cuidados |
| POST | `/plano-cuidados/{planoCuidadosId}/tarefa` | `PlanoCuidadosController` | Adiciona tarefa ao plano |
| POST | `/plano-cuidados/tarefa/{tarefaId}/concluir` | `PlanoCuidadosController` | Conclui tarefa |
| POST | `/plano-cuidados/{planoCuidadosId}/instrucoes` | `PlanoCuidadosController` | Atualiza instruções |
| POST | `/plano-cuidados/{planoCuidadosId}/prioridade` | `PlanoCuidadosController` | Atualiza prioridade |
| GET | `/plano-cuidados/animal/{animalId}/historico` | `PlanoCuidadosController` | Histórico de planos do animal |
| POST | `/plano-cuidados/{planoCuidadosId}/encerrar` | `PlanoCuidadosController` | Encerra plano |
| GET | `/extras` | `ServicoExtraController` | Formulário de serviço extra; parâmetro `estadiaId` |
| POST | `/extras/create` | `ServicoExtraController` | Regista serviço extra |
| POST | `/notas/create` | `NotaController` | Regista nota associada a reserva |

## Pagamentos

| Método | Caminho | Controller | Retorno/efeito |
|---|---|---|---|
| GET | `/pagamentos` | `PagamentoController` | Página de pagamentos pendentes/registados |
| POST | `/pagamentos` | `PagamentoController` | Regista pagamento |

## Administração de catálogos e colaboradores

| Método | Caminho | Controller | Retorno/efeito |
|---|---|---|---|
| GET | `/colaboradores` | `ColaboradorController` | Lista colaboradores |
| GET | `/colaboradores/novo` | `ColaboradorController` | Formulário de novo colaborador |
| POST | `/colaboradores` | `ColaboradorController` | Cria colaborador |
| GET | `/colaboradores/{id}/editar` | `ColaboradorController` | Formulário de edição |
| POST | `/colaboradores/{id}` | `ColaboradorController` | Atualiza colaborador |
| POST | `/colaboradores/{id}/desativar` | `ColaboradorController` | Desativa colaborador |
| GET | `/admin/tarifas` | `TipoAlojamentoTarifaController` | Lista tarifas por tipo de alojamento |
| GET | `/admin/tarifas/nova` | `TipoAlojamentoTarifaController` | Formulário de nova tarifa |
| POST | `/admin/tarifas` | `TipoAlojamentoTarifaController` | Cria tarifa |
| GET | `/admin/tarifas/{id}/editar` | `TipoAlojamentoTarifaController` | Formulário de edição |
| POST | `/admin/tarifas/{id}` | `TipoAlojamentoTarifaController` | Atualiza tarifa |
| POST | `/admin/tarifas/{id}/desativar` | `TipoAlojamentoTarifaController` | Desativa tarifa |
| POST | `/admin/tarifas/{id}/reativar` | `TipoAlojamentoTarifaController` | Reativa tarifa |
| GET | `/admin/tarifas/{id}` | `TipoAlojamentoTarifaController` | JSON da tarifa |
| GET | `/admin/tarifas/api/ativas` | `TipoAlojamentoTarifaController` | JSON de tarifas ativas |
| GET | `/admin/tipos-servicos-extra` | `TipoServicoExtraController` | Lista tipos de serviços extra |
| GET | `/admin/tipos-servicos-extra/novo` | `TipoServicoExtraController` | Formulário de novo tipo |
| POST | `/admin/tipos-servicos-extra` | `TipoServicoExtraController` | Cria tipo |
| GET | `/admin/tipos-servicos-extra/{id}/editar` | `TipoServicoExtraController` | Formulário de edição |
| POST | `/admin/tipos-servicos-extra/{id}` | `TipoServicoExtraController` | Atualiza tipo |
| POST | `/admin/tipos-servicos-extra/{id}/desativar` | `TipoServicoExtraController` | Desativa tipo |
| POST | `/admin/tipos-servicos-extra/{id}/reativar` | `TipoServicoExtraController` | Reativa tipo |
| GET | `/admin/tipos-servicos-extra/{id}` | `TipoServicoExtraController` | JSON do tipo |
| GET | `/admin/tipos-servicos-extra/api/ativos` | `TipoServicoExtraController` | JSON de tipos ativos |

## Regras de acesso resumidas

- `DIRETOR`: acesso global às áreas de direção, relatórios, auditoria, administração, reservas, estadias, clínica, limpeza e cuidados.
- `FUNCIONARIO_RECEPCAO`: reservas, estadias, pagamentos, alojamentos, tutores, animais, histórico e planos de cuidados.
- `CUIDADOR`: cuidados, planos de cuidados e notas.
- `MEDICO_VETERINARIO`: clínica, histórico, cuidados, planos de cuidados e notas.
- `RESPONSAVEL_LIMPEZA`: lista e atualização de limpeza.

## Notas de validação

- Ações destrutivas ou de alteração de estado são `POST` e devem ser chamadas por formulário com CSRF.
- `/estadias` existe apenas como redirecionamento; a navegação principal deve preferir `/estadias/lista`.
- Datas em query string usam formato ISO `yyyy-MM-dd`.
