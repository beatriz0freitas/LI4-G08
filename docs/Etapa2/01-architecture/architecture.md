# Especificação de Arquitetura — Sistema de Gestão de Hotel de Animais

**Projeto:** LI4 — Sistema de Gestão de Hotel de Animais  
**Etapa:** 2 — Arquitetura e Design  
**Tecnologia base:** Java 21 + Spring Boot 3.3.5 (Spring MVC)
**Data:** Abril 2026  

---

## 1. Visão Geral

O sistema é uma aplicação web monolítica, organizada segundo o padrão arquitetural **MVC em camadas** (_Layered MVC_), implementada em **Java 21** com **Spring Boot 3.3.5**. A interface é renderizada no servidor com **Thymeleaf** e estilizada com **AdminLTE** e **Bootstrap 4.6**. A persistência é gerida por **Spring Data JPA** sobre uma base de dados relacional **MySQL**, com migrações versionadas por **Flyway**.

A escolha do monólito em camadas justifica-se pela dimensão do hotel (até 10 utilizadores simultâneos — RNF-01), pela necessidade de disponibilidade contínua em rede local (RNF-03) e pela exigência de uma implementação incremental controlada nas 3 semanas previstas para a Etapa 3.

Diagramas de componentes:
- [components.plantuml](components.plantuml) (PlantUML)
- [components.mmd](components.mmd) (Mermaid)
- [components-simple.plantuml](components-simple.plantuml) (PlantUML simplificado)

Diagramas de implantação:
- [deployment-view.puml](deployment-view.puml) (PlantUML)
- [deployment.mmd](deployment.mmd) (Mermaid)
- [deployment-simple.plantuml](deployment-simple.plantuml) (PlantUML simplificado)

Diagrama de packages:
- [packages-simple.plantuml](packages-simple.plantuml) (PlantUML simplificado)

---

## 2. Padrão Arquitetural

```
┌────────────────────────────────────────────────────────┐
│  Camada de Apresentação  (Spring MVC Controllers +     │
│                           Thymeleaf Templates)         │
├────────────────────────────────────────────────────────┤
│  Camada de Aplicação     (Spring @Service — lógica     │
│                           de negócio e orquestração)   │
├────────────────────────────────────────────────────────┤
│  Camada de Domínio        (Entidades JPA + Enums)      │
├────────────────────────────────────────────────────────┤
│  Camada de Dados          (Spring Data JPA             │
│                            Repositories)               │
├────────────────────────────────────────────────────────┤
│  Base de Dados            (MySQL 8)                    │
└────────────────────────────────────────────────────────┘
```

A dependência entre camadas é estritamente top-down: nenhuma camada inferior conhece camadas superiores.

---

## 3. Componentes e Responsabilidades

### 3.1 Camada de Apresentação

Implementada com **Spring MVC** (`@Controller`) e templates **Thymeleaf** (`.html`). Os controladores refletem os módulos funcionais hoje presentes no projeto, incluindo dashboard, histórico, auditoria, catálogo de tipos e tarifas, cuidados e clínica.

| Controlador | Responsabilidade | UC cobertos |
|---|---|---|
| `AuthController` | Login, logout, redirecionamento por perfil | UC-01 |
| `DashboardController` | Dashboard operacional com indicadores em tempo real | RF-01 |
| `AlojamentoController` | Consulta de disponibilidade e detalhe de alojamentos | RF-06, RF-01 |
| `TutorAnimalController` | Registo e consulta de tutores e animais | RF-04 |
| `AnimalController` | Consulta operacional de animais e detalhe clínico | RF-05, RF-14 |
| `ReservaController` | Criação, confirmação, listagem e cancelamento de reservas | RF-07 |
| `EstadiaController` | Check-in e check-out | RF-08, RF-09 |
| `PagamentoController` | Registo e consulta de pagamentos | RF-10 |
| `RegistoCuidadoController` | Registo e listagem de cuidados prestados | RF-12 |
| `PlanoCuidadosController` | Consulta do plano de cuidados ativo | RF-11 |
| `NotaController` | Adição e consulta de notas operacionais | RF-05 |
| `ServicoExtraController` | Registo de serviços extra durante estadia | RF-17 |
| `ClinicaController` | Registo de alterações de estado de saúde e intervenções clínicas | RF-13, RF-14 |
| `LimpezaController` | Consulta de alojamentos pendentes e marcação como limpo | RF-15 |
| `HistoricoController` | Consulta do histórico consolidado de reservas, estadias, pagamentos e eventos | RF-05, RF-14 |
| `AuditoriaController` | Consulta e exportação de auditoria | RF-19 |
| `RelatorioController` | Geração de relatórios operacionais e financeiros | RF-03 |
| `ColaboradorController` | Gestão de perfis de acesso (apenas Diretor) | RF-02 |
| `TipoAlojamentoTarifaController` | Gestão de tipos de alojamento, tarifas e catálogos associados | RF-18 |

Cada controlador recebe e devolve objetos **DTO** (_Data Transfer Objects_), nunca entidades JPA diretamente.

### 3.2 Camada de Aplicação (Services)

Os serviços principais seguem, em geral, o padrão `I*Service` + `*Service` anotado com `@Service`. A implementação encapsula regras de negócio, orquestra repositórios e publica eventos de auditoria quando necessário.

| Interface | Implementação | Métodos principais | Regras de domínio aplicadas |
|---|---|---|---|
| `IDashboardService` | `DashboardService` | `obterIndicadores()`, `listarIndicadores()` | RF-01 |
| `IAlojamentoService` | `AlojamentoService` | `consultarDisponibilidade()`, `listarDisponiveis()` | RF-06, RD-01 |
| `ITutorService` | `TutorService` | `registarTutor()`, `pesquisar()` | RF-04, RD-05 |
| `IAnimalService` | `AnimalService` | `registarAnimal()`, `atualizarEstadoSaude()`, `listarPorEstadoSaude()` | RF-04, RF-13, RD-05, RD-08 |
| `IReservaService` | `ReservaService` | `criarReserva()`, `cancelarReserva()`, `confirmarReserva()` | RF-07, RD-02, RD-06 |
| `IEstadiaService` | `EstadiaService` | `registarCheckIn()`, `registarCheckOut()` | RF-08, RF-09, RD-03, RD-07 |
| `IPagamentoService` | `PagamentoService` | `registarPagamentoCheckIn()`, `registarPagamentoCheckOut()` | RF-10, RD-04 |
| `IRegistoCuidadoService` | `RegistoCuidadoService` | `registarCuidado()`, `listarPorEstadia()` | RF-12, RNF-09 |
| `IPlanoCuidadosService` | `PlanoCuidadosService` | `consultarPlanoAtivo()`, `atualizarPlano()` | RF-11, RD-10 |
| `IServicoExtraService` | `ServicoExtraService` | `registarServicoExtra()`, `listarPorEstadia()` | RF-17, RD-09 |
| `IClinicaService` | `ClinicaService` | `registarIntervencaoClinica()`, `consultarHistorial()` | RF-14 |
| `IAlteracaoEstadoSaudeService` | `AlteracaoEstadoSaudeService` | `registarAlteracao()`, `listarAlteracoesRecentes()` | RF-13 |
| `ILimpezaService` | `LimpezaService` | `listarPendentes()`, `registarLimpezaConcluida()` | RF-15, RD-01 |
| `IRelatorioService` | `RelatorioService` | `gerarDashboard()`, `gerarRelatorioPeriodo()` | RF-01, RF-03 |
| `IHistoricoService` | `HistoricoService` | `consultarHistorico()`, `exportarHistorico()` | RF-05, RF-14 |
| `IRegraDominioService` | `RegraDominioService` | `validarDisponibilidade()`, `validarSequenciaEstadia()` | RD-01, RD-02, RD-03, RD-06, RD-07 |
| `IAvailabilityDomainService` | `AvailabilityDomainService` | `calcularDisponibilidade()`, `validarConflitos()` | RF-06, RNF-06 |
| `IAuditoriaService` | `AuditoriaService` | `registarEvento()`, `consultarPorPeriodo()`, `consultarPorUtilizador()` | RF-19, RNF-09 |

Serviços de apoio como `TipoAlojamentoTarifaService` concentram responsabilidades específicas de catálogo e não seguem sempre o par interface/implementação.

### 3.3 Camada de Domínio

Entidades JPA anotadas com `@Entity`, mapeando diretamente o modelo de domínio da Etapa 1.

| Entidade | Tabela BD | Notas |
|---|---|---|
| `Colaborador` | `colaborador` | Inclui `senha` com hash BCrypt e papel de acesso |
| `Tutor` | `tutor` | |
| `Animal` | `animal` | Associado a um tutor; suporta espécie e estado de saúde |
| `Alojamento` | `alojamento` | Unidade física sujeita a limpeza, ocupação e compatibilidade |
| `Reserva` | `reserva` | Controla a disponibilidade e a sequência de confirmação/cancelamento |
| `Estadia` | `estadia` | Execução de uma reserva; suporta check-in e check-out |
| `Pagamento` | `pagamento` | Pagamento base ou complementar associado à estadia |
| `RegistoCuidado` | `registo_cuidado` | Registo operacional com autoria e data/hora |
| `PlanoCuidados` | `plano_cuidados` | Plano ativo da estadia |
| `TarefaCuidado` | `tarefa_cuidado` | Tarefas previstas no plano de cuidados |
| `Nota` | `nota` | Informação operacional associada a reservas ou estadias |
| `ServicoExtra` | `servico_extra` | Serviço realizado durante a estadia com custo associado |
| `TipoServicoExtra` | `tipo_servico_extra` | Catálogo administrado pela direção |
| `TipoAlojamentoTarifa` | `tipo_alojamento_tarifa` | Catálogo de tipos de alojamento e tarifas |
| `IntervencaoClinica` | `intervencao_clinica` | Intervenção ou prescrição clínica faturável |
| `AlteracaoEstadoSaude` | `alteracao_estado_saude` | Registo de alteração do estado de saúde |
| `AuditoriaEvento` | `auditoria_evento` | Registo persistido de operações críticas |

Enums Java correspondem diretamente às enumerações do modelo de domínio e do código atual: `Especie`, `EstadoSaude`, `PrioridadePlano`, `PeriodicidadeTarefa`, `EstadoLimpeza`, `EstadoReserva`, `EstadoEstadia`, `EstadoPagamento`, `MetodoPagamento`, `MomentoPagamento`, `TipoColaborador`, `ResultadoAuditoria`.

### 3.4 Camada de Dados (Repositories)

Interfaces que estendem `JpaRepository<T, Long>`, com suporte adicional a `JpaSpecificationExecutor` quando é necessário filtrar por múltiplos critérios. Consultas complexas usam JPQL, criteria API e especificações Spring Data.

| Repository | Consultas relevantes |
|---|---|
| `AlojamentoRepository` | `findDisponiveis(dataInicio, dataFim)` — cruza reservas, estadias, limpeza e compatibilidade |
| `ReservaRepository` | `findByAnimalAndEstadoAtiva(animalId)` — verifica sobreposições e estados ativos |
| `EstadiaRepository` | `findEmCursoPorAnimal(animalId)` — garante exclusividade da estadia |
| `AnimalRepository` | `findByEstadoSaudeAlteradoOuCritico()` — lista para veterinário |
| `PagamentoRepository` | `findByEstadia(estadiaId)` |
| `RegistoCuidadoRepository` | Consultas por estadia, animal e período |
| `PlanoCuidadosRepository` | Consulta do plano ativo por estadia |
| `TarefaCuidadoRepository` | Consultas de tarefas previstas e executadas |
| `IntervencaoClinicaRepository` | Histórico clínico e custos associados |
| `AlteracaoEstadoSaudeRepository` | Eventos recentes de saúde |
| `TipoAlojamentoTarifaRepository` | Gestão de catálogos e tarifas ativas |
| `TipoServicoExtraRepository` | Catálogo de serviços extra |
| `NotaRepository` | Notas operacionais por reserva ou estadia |
| `AuditoriaRepository` | Consultas filtradas por período, utilizador, operação e entidade |

---

## 4. Autenticação e Autorização

Implementada com **Spring Security 6** e integração com `ColaboradorUserDetailsService`.

- Autenticação por formulário HTML (`/login`) com sessão HTTP.  
- Palavras-passe armazenadas com **BCryptPasswordEncoder**.  
- Controlo de acesso por papel com `@PreAuthorize` e `hasRole()`.  
- Sessão invalidada no logout; CSRF ativo em todos os formulários.
- Operações sensíveis geram eventos de auditoria e estão ligadas ao perfil autenticado.

### Mapeamento perfil → funcionalidades

| Perfil (`TipoColaborador`) | Acesso permitido |
|---|---|
| `DIRETOR` | Dashboard, relatórios, histórico, auditoria, catálogos e gestão de colaboradores |
| `FUNCIONARIO_RECEPCAO` | Tutores, animais, reservas, check-in/out, pagamentos e disponibilidade |
| `CUIDADOR` | Plano de cuidados, registos de cuidado, notas e serviços extra |
| `MEDICO_VETERINARIO` | Histórico clínico, intervenções e alterações do estado de saúde |
| `RESPONSAVEL_LIMPEZA` | Lista de alojamentos pendentes e marcação como limpo |

---

## 5. Estrutura de Pacotes (Spring Boot)

```
pt.hotel.animais/
├── config/
│   ├── SecurityConfig.java
│   └── converter/
│       └── AuditoriaDetalhesConverter.java
├── controller/
│   ├── AuthController.java
│   ├── DashboardController.java
│   ├── AlojamentoController.java
│   ├── TutorAnimalController.java
│   ├── AnimalController.java
│   ├── ReservaController.java
│   ├── EstadiaController.java
│   ├── PagamentoController.java
│   ├── RegistoCuidadoController.java
│   ├── PlanoCuidadosController.java
│   ├── NotaController.java
│   ├── ServicoExtraController.java
│   ├── ClinicaController.java
│   ├── LimpezaController.java
│   ├── HistoricoController.java
│   ├── RelatorioController.java
│   ├── ColaboradorController.java
│   ├── TipoAlojamentoTarifaController.java
│   └── AuditoriaController.java
├── service/
│   ├── auditoria/
│   │   ├── IAuditoriaService.java
│   │   └── AuditoriaService.java
│   ├── IColaboradorService.java
│   ├── ColaboradorService.java
│   ├── IAlojamentoService.java
│   ├── AlojamentoService.java
│   ├── ITutorService.java
│   ├── TutorService.java
│   ├── IAnimalService.java
│   ├── AnimalService.java
│   ├── IReservaService.java
│   ├── ReservaService.java
│   ├── IEstadiaService.java
│   ├── EstadiaService.java
│   ├── IPagamentoService.java
│   ├── PagamentoService.java
│   ├── IRegistoCuidadoService.java
│   ├── RegistoCuidadoService.java
│   ├── IPlanoCuidadosService.java
│   ├── PlanoCuidadosService.java
│   ├── IServicoExtraService.java
│   ├── ServicoExtraService.java
│   ├── IClinicaService.java
│   ├── ClinicaService.java
│   ├── IAlteracaoEstadoSaudeService.java
│   ├── AlteracaoEstadoSaudeService.java
│   ├── ILimpezaService.java
│   ├── LimpezaService.java
│   ├── IDashboardService.java
│   ├── DashboardService.java
│   ├── IHistoricoService.java
│   ├── HistoricoService.java
│   ├── IRelatorioService.java
│   ├── RelatorioService.java
│   ├── TipoAlojamentoTarifaService.java
│   ├── IRegraDominioService.java
│   ├── RegraDominioService.java
│   ├── IAvailabilityDomainService.java
│   └── AvailabilityDomainService.java
├── repository/
│   ├── ColaboradorRepository.java
│   ├── AlojamentoRepository.java
│   ├── TutorRepository.java
│   ├── AnimalRepository.java
│   ├── ReservaRepository.java
│   ├── EstadiaRepository.java
│   ├── PagamentoRepository.java
│   ├── RegistoCuidadoRepository.java
│   ├── PlanoCuidadosRepository.java
│   ├── TarefaCuidadoRepository.java
│   ├── ServicoExtraRepository.java
│   ├── TipoServicoExtraRepository.java
│   ├── TipoAlojamentoTarifaRepository.java
│   ├── IntervencaoClinicaRepository.java
│   ├── AlteracaoEstadoSaudeRepository.java
│   ├── NotaRepository.java
│   └── AuditoriaRepository.java
├── model/
│   ├── Colaborador.java
│   ├── Tutor.java
│   ├── Animal.java
│   ├── Alojamento.java
│   ├── Reserva.java
│   ├── Estadia.java
│   ├── Pagamento.java
│   ├── RegistoCuidado.java
│   ├── PlanoCuidados.java
│   ├── TarefaCuidado.java
│   ├── ServicoExtra.java
│   ├── TipoServicoExtra.java
│   ├── TipoAlojamentoTarifa.java
│   ├── IntervencaoClinica.java
│   ├── AlteracaoEstadoSaude.java
│   ├── Nota.java
│   ├── auditoria/
│   │   └── AuditoriaEvento.java
│   └── enums/
│       ├── Especie.java
│       ├── EstadoSaude.java
│       ├── PrioridadePlano.java
│       ├── PeriodicidadeTarefa.java
│       ├── EstadoLimpeza.java
│       ├── EstadoReserva.java
│       ├── EstadoEstadia.java
│       ├── EstadoPagamento.java
│       ├── MetodoPagamento.java
│       ├── MomentoPagamento.java
│       ├── TipoColaborador.java
│       └── ResultadoAuditoria.java
└── dto/
    ├── ReservaFormDto.java
    ├── ReservaDetalheFinanceiroDto.java
    ├── EstadiaFormDto.java
    ├── EstadiaDTO.java
    ├── PagamentoDto.java
    ├── AnimalFormDto.java
    ├── AnimalDTO.java
    ├── TutorFormDto.java
    ├── TutorDTO.java
    ├── RelatorioResumoDto.java
    ├── RelatorioFiltroFormDto.java
    ├── DashboardEstadiaDto.java
    ├── PlanoCuidadosDto.java
    ├── TarefaCuidadoDto.java
    ├── FichaClinicaDto.java
    ├── AlteracaoEstadoSaudeDto.java
    ├── IntervencaoClinicaDto.java
    ├── NotaDto.java
    ├── ServicoExtraDto.java
    └── auditoria/
        ├── AuditoriaEventoDTO.java
        └── AuditoriaFiltroDTO.java
```

Templates Thymeleaf residem em `src/main/resources/templates/`, organizados por módulo funcional.

---

## 6. Tecnologias e Dependências

| Componente | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework web | Spring Boot 3.3.5 + Spring MVC |
| Segurança | Spring Security 6 + thymeleaf-extras-springsecurity6 |
| Persistência | Spring Data JPA + Hibernate |
| Migrações | Flyway |
| Base de dados principal | MySQL 8 |
| Template engine | Thymeleaf |
| CSS / UI | AdminLTE + Bootstrap 4.6 |
| Observabilidade / auditoria | Spring Boot Actuator (`health`/`info`) + auditoria funcional persistente própria (`AuditoriaEvento`) |
| Gestão de dependências e construção | Maven |
| Testes | JUnit 5 + Mockito + Spring Boot Test + Spring Security Test + Playwright |
| Qualidade | JaCoCo + PITest |
| Utilitários de código | Lombok |
| Ambiente de execução | Docker + Docker Compose |


---

## 7. Rastreabilidade com Requisitos Não Funcionais

| RNF | Decisão arquitetural |
|---|---|
| RNF-01 (tempo de resposta) | Spring Data JPA com consultas e índices adequados; páginas renderizadas no servidor sem cliente pesado |
| RNF-02 (usabilidade) | Thymeleaf com componentes AdminLTE e Bootstrap para formulários e tabelas consistentes |
| RNF-03 (disponibilidade) | Spring Boot embarcado e deployment em contentor, sem dependência de servidor de aplicação externo |
| RNF-04 (autenticação e permissões) | Spring Security 6, BCrypt e `@PreAuthorize` por método |
| RNF-05 (confidencialidade dos dados) | Dados sensíveis ficam na camada de dados; autenticação por sessão e proteção CSRF |
| RNF-06 (consistência da informação) | Repositórios Spring Data JPA, transações atómicas e regras de domínio centralizadas |
| RNF-07 (consulta eficiente de informação acumulada) | Queries específicas, paginação e separação entre consulta operacional e histórico |
| RNF-08 (retenção da informação) | Persistência em MySQL com migrações Flyway e testes de integração sobre a base relacional |
| RNF-09 (rastreabilidade temporal) | Auditoria persistida exclusivamente por `AuditoriaOperacaoService`/`AuditoriaService` e registo de autor/data-hora em operações críticas |

---

## 8. Fluxo de Pedido HTTP (Spring MVC)

```
Browser (Thymeleaf form)
    │  HTTP POST/GET
    ▼
DispatcherServlet (Spring MVC)
    │
    ├─► SecurityFilterChain (Spring Security)
    │       └─ Valida sessão + perfil
    │
    ▼
@Controller (recebe DTO, valida @Valid)
    │
    ▼
@Service (lógica de negócio + @Transactional)
    │
    ▼
@Repository (Spring Data JPA)
    │
    ▼
MySQL (via Hibernate/HikariCP)
```

---

## 9. Decisões de Arquitetura

As decisões detalhadas encontram-se nos ADRs da pasta `docs/Etapa2/04-architecture-decisions/`:

- [ADR-01](../04-architecture-decisions/ADR-01-monolito-camadas.md) - Arquitetura em camadas numa aplicação centralizada.
- [ADR-02](../04-architecture-decisions/ADR-02-spring-mvc-thymeleaf-ssr.md) - Interface de utilizador com renderização no servidor.
- [ADR-03](../04-architecture-decisions/ADR-03-persistencia-sgbd-relacional.md) - Persistência de dados em SGBD relacional.
- [ADR-04](../04-architecture-decisions/ADR-04-mysql-base-dados.md) - Persistência de dados com MySQL e padrão repositório.
- [ADR-05](../04-architecture-decisions/ADR-05-controlo-acesso-perfil.md) - Autenticação e autorização com controlo de acesso por perfil.
- [ADR-06](../04-architecture-decisions/ADR-06-isolamento-apresentacao-dtos.md) - Isolamento da camada de apresentação através de DTOs.
- [ADR-07](../04-architecture-decisions/ADR-07-docker-desenvolvimento-testes.md) - Utilização de Docker no ambiente de desenvolvimento e testes.

| ID | Decisão | Alternativa rejeitada | Justificação |
|---|---|---|---|
| ADR-01 | Arquitetura em camadas numa aplicação centralizada | Microserviços | Equipa pequena, escala limitada e necessidade de manutenção simples |
| ADR-02 | Interface de utilizador com renderização no servidor | REST + SPA (React/Angular) | Reduz complexidade de estado e encaixa melhor na escala do projeto |
| ADR-03 | Persistência de dados em SGBD relacional | NoSQL / ficheiros planos | O domínio é fortemente relacional e exige integridade referencial |
| ADR-04 | Persistência de dados com MySQL e padrão repositório | PostgreSQL / base em memória para integração | MySQL é o SGBD usado na aplicação e nos testes de integração |
| ADR-05 | Autenticação e autorização com controlo de acesso por perfil | Autorização só na interface | O controlo precisa de ser centralizado e consistente em toda a aplicação |
| ADR-06 | Isolamento da camada de apresentação através de DTOs | Exposição direta de entidades | Reduz acoplamento e evita exposição desnecessária de dados |
| ADR-07 | Utilização de Docker no ambiente de desenvolvimento e testes | Instalação manual por máquina | Garante ambientes reproduzíveis e reduz diferenças de configuração |

---

## 10. Pressupostos

1. O sistema opera em rede local; não é considerado acesso remoto externo nesta fase.
2. Um único servidor Java (Spring Boot embarcado) serve toda a aplicação.
3. As cópias de segurança são geridas pelo sistema operativo/script agendado, não pela aplicação.
4. Os ambientes de desenvolvimento, produção e testes de integração usam MySQL; testes sem persistência usam Mockito.
