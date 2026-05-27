# Plano de Implementação Gradual — Sistema de Gestão de Hotel de Animais

## Objetivo

Este plano organiza a implementação do sistema em fases pequenas e coerentes, preservando a base definida em Etapa 1 e Etapa 2. A prioridade é construir uma fundação sólida, mantendo rastreabilidade aos use cases, user stories e requisitos documentados.


## Princípios

- Cada fase deve entregar valor observável e utilizável.
- Nenhuma fase deve obrigar a reescrever a arquitetura base.
- O detalhe funcional deve crescer apenas quando a fase anterior estiver estável.
- A documentação da Etapa 1 e da Etapa 2 continua a ser a fonte de verdade. 
- Toda a regra de negócio, classe e relação provém da Etapa 1 ou Etapa 2. Nada é inventado.
- Documentação com Javadoc para controllers, services, DTOs e exceptions.

## Fase 1 — Fundação transversal

### Objetivo

Estabilizar a aplicação base, a navegação inicial, o controlo de acesso e a primeira visão operacional.

### Mapeamento aos Requisitos

- **Use Case**: UC-01 (Autenticação)
- **User Stories**: US-01, US-02, US-20, US-21
- **Requisitos Funcionais**: RF-01 (Dashboard)
- **Requisitos de Domínio**: RD-01 (Disponibilidade de alojamento)

### Inclui

- Arranque da aplicação e configuração geral (pom.xml, application.properties).
- Acesso autenticado por sessão HTTP com BCrypt e separação por perfis.
- Área inicial do diretor com indicadores básicos de ocupação e faturação.
- Listagem de alojamentos e estado de limpeza (PENDENTE, CONCLUIDO).
- Navegação base para os módulos principais (tutores, animais, reservas, etc.).
- Fragments visuais comuns e estrutura de páginas reutilizável (Thymeleaf).
- Templates conforme mockups: wf01-login.html, wf02-dashboard-diretor.html, wf06-limpeza.html.

### Classes e Artefactos Associados

**Aplicação**:
- `HotelAnimaisApplication`

**Configuração**:
- `SecurityConfig` (autenticação, autorização, BCrypt)

**Controllers**:
- `AuthController` (login, logout)
- `DashboardController` (painel do diretor)
- `LimpezaController` (gestão de limpeza)
- `ModuloPlaceholderController` (pontos de entrada para módulos futuros)

**Modelo/Entidades**:
- `Alojamento` (id, identificacao, estadoLimpeza)
- `EstadoLimpeza` (enum: PENDENTE, CONCLUIDO)

**Repositories**:
- `AlojamentoRepository` (CRUD e queries para disponibilidade)

**Services**:
- `AlojamentoService` (consulta, contagem)
- `LimpezaService` (marcar como limpo)

**Templates** (Thymeleaf):
- Login, Dashboard, Limpeza, Fragments (navbar, sidebar, footer)

---

## Fase 2 — Registo base de clientes e alojamentos

### Objetivo

Implementar a base funcional para tutores, animais e reservas.

### Mapeamento aos Requisitos

- **Use Cases**: UC-03, UC-04
- **User Stories**: US-06, US-09, US-12
- **Requisitos de Domínio**: RD-03, RD-05, RD-06

### Inclui

- Registo e consulta de tutores (NIF, contacto, email).
- Registo e consulta de animais (espécie, estado de saúde).
- Associação tutor ↔ animal (relação 1..*).
- Criação de reservas com controlo de disponibilidade.
- Regras conforme RD-01 e RD-06 (ocupação e estados).

### Classes e Artefactos Associados

**Entidades**:
- `Tutor` (id, nome, nif, contacto, email)
- `Animal` (id, nome, especie, raca, estadoSaude)
- `Reserva` (id, dataInicio, dataFim, estado)
- Enums: `Especie` (CAO, GATO), `EstadoSaude` (NORMAL, ALTERADO, CRITICO), `EstadoReserva` (ATIVA, CONFIRMADA, CANCELADA, CONCLUIDA)

**Controllers**: `TutorAnimalController`, `ReservaController`

**Services**: `TutorService`, `AnimalService`, `ReservaService`

**Repositories**: `TutorRepository`, `AnimalRepository`, `ReservaRepository`

---

## Fase 3 — Reservas, estadias e pagamentos

### Objetivo

Cobrir o ciclo operacional principal do hotel, desde a reserva até à saída com pagamento.

### Mapeamento aos Requisitos

- **Use Cases**: UC-04, UC-05, UC-06, UC-07, UC-08
- **User Stories**: US-07, US-10, US-11
- **Requisitos de Domínio**: RD-02, RD-04

### Inclui

- Check-in e check-out de estadias.
- Registo de pagamentos em dois momentos: check-in (base) e check-out (extras).
- Validação de estados e transições.
- Atualização de limpeza após check-out.

### Classes e Artefactos Associados

**Entidades**:
- `Estadia` (id, checkIn, checkOut, estado)
- `Pagamento` (id, valor, metodo, estado, momento)
- Enums: `EstadoEstadia` (EM_CURSO, TERMINADA), `EstadoPagamento` (PENDENTE, LIQUIDADO), `MomentoPagamento` (CHECK_IN, CHECK_OUT), `MetodoPagamento` (NUMERARIO, CARTAO_DEBITO, CARTAO_CREDITO)

**Controllers**: `EstadiaController`, `PagamentoController`

**Services**: `EstadiaService`, `PagamentoService`

**Repositories**: `EstadiaRepository`, `PagamentoRepository`

---

## Fase 4 — Operação diária, clínica e limpeza avançada

### Objetivo

Completar a rastreabilidade da operação diária e do acompanhamento clínico.

### Mapeamento aos Requisitos

- **Use Cases**: UC-09, UC-10, UC-11
- **User Stories**: US-14, US-15, US-16, US-17, US-18, US-22, US-23

### Inclui

- Registo de cuidados diários.
- Notas operacionais em reservas.
- Serviços extra (banho, passeio, outro).
- Intervenções clínicas com histórico.

### Classes e Artefactos Associados

**Entidades**:
- `RegistoCuidado` (id, descricao, dataHora)
- `Nota` (id, descricao)
- `ServicoExtra` (id, tipo, custo, dataHora)
- `IntervencaoClinica` (id, descricao, custo, dataHora)
- Enum: `TipoServicoExtra` (BANHO, PASSEIO, OUTRO)

**Controllers**: `ServicoExtraController`, `ClinicaController`

**Services**: `ServicoExtraService`, `ClinicaService`

**Repositories**: `ServicoExtraRepository`, `ClinicaRepository`, `RegistoCuidadoRepository`

---

## Fase 5 — Relatórios, colaboradores e consolidação

### Objetivo

Fechar a visão executiva, a administração de perfis e a consolidação dos indicadores operacionais.

### Mapeamento aos Requisitos

- **Use Cases**: UC-13, UC-02
- **User Stories**: US-01, US-02, US-03, US-04, US-05

### Inclui

- Dashboard consolidado com todos os indicadores.
- Relatórios por período.
- Migração de Colaborador da memory para base de dados.
- Gestão de perfis e permissões.
- Configuração transversal da auditoria funcional própria persistida em `auditoria_evento`.
- Registo pelo `AuditoriaOperacaoService` nos fluxos críticos: colaboradores/permissões, relatórios/exportações, tutores/animais, reservas/estadias, pagamentos/faturação, cuidados/clínica/serviços extra e alojamentos/limpeza.

### Classes e Artefactos Associados

**Entidades**:
- `Colaborador` (id, nome, email, passwordHash, tipoColaborador)
- Enum: `TipoColaborador` (DIRETOR, FUNCIONARIO_RECEPCAO, CUIDADOR, MEDICO_VETERINARIO, RESPONSAVEL_LIMPEZA)

**Controllers**: `RelatorioController`, `ColaboradorController`

**Services**: `ColaboradorService`, `RelatorioService`

**Repositories**: `ColaboradorRepository`

**Configuração operacional**: `AuditoriaOperacaoService` resolve o colaborador autenticado e delega em `AuditoriaService`, que persiste `AuditoriaEvento`. O Actuator é usado apenas para monitorização (`health`/`info`) e não constitui rasto de auditoria.

---

## Sequência de Implementação

1. **Spec 001-fundacao-hotel-animais** — Fase 1
2. **Spec 002-registo-clientes-alojamentos** — Fase 2
3. **Spec 003-reservas-estadias-pagamentos** — Fase 3
4. **Spec 004-cuidados-clinica-limpeza** — Fase 4
5. **Spec 005-relatorios-colaboradores** — Fase 5

## Critérios de Passagem entre Fases

- A fase anterior deve estar estável e testada.
- Cada nova fase mantém compatibilidade com módulos entregues.
- Nenhuma fase introduz relações que contradigam Etapa 1.
- Rastreabilidade preservada até UCs, User Stories e Requisitos documentados.
