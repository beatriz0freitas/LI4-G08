# Especificação de Arquitetura — Sistema de Gestão de Hotel de Animais

**Projeto:** LI4 — Sistema de Gestão de Hotel de Animais  
**Etapa:** 2 — Arquitetura e Design  
**Tecnologia base:** Java 21 + Spring Boot 3 (Spring MVC)  
**Data:** Abril 2026  

---

## 1. Visão Geral

O sistema é uma aplicação web monolítica, organizada segundo o padrão arquitetural **MVC em camadas** (_Layered MVC_), implementada em **Java 21** com **Spring Boot 3**. A interface é renderizada no servidor com **Thymeleaf**. A persistência é gerida por **Spring Data JPA** sobre uma base de dados relacional **MySQL**.

A escolha do monólito em camadas justifica-se pela dimensão do hotel (até 10 utilizadores simultâneos — RNF-01), pela necessidade de operação em rede local (RNF-06) e pela exigência de uma implementação incremental controlada nas 3 semanas previstas para a Etapa 3.

Diagrama de componentes: [components.mmd](components.mmd)  
Diagrama de implantação: [deployment.mmd](deployment.mmd)

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

Implementada com **Spring MVC** (`@Controller`) e templates **Thymeleaf** (`.html`).

| Controlador | Responsabilidade | UC cobertos |
|---|---|---|
| `AuthController` | Login, logout, redirecionamento por perfil | UC-01 |
| `AlojamentoController` | Consulta de disponibilidade em tempo real | UC-02, UC-12 |
| `TutorAnimalController` | Registo e consulta de tutores e animais | UC-03 |
| `ReservaController` | Criação, listagem e cancelamento de reservas | UC-04, UC-05 |
| `EstadiaController` | Check-in, check-out, cuidados diários | UC-06, UC-07, UC-09 |
| `PagamentoController` | Registo e consulta de pagamentos | UC-08 |
| `ServicoExtraController` | Registo de serviços extra durante estadia | UC-10 |
| `ClinicaController` | Registo e consulta de intervenções clínicas | UC-11 |
| `LimpezaController` | Marcação de alojamento como limpo | UC-12 |
| `RelatorioController` | Dashboard e geração de relatórios | UC-13 |
| `ColaboradorController` | Gestão de perfis de acesso (apenas Diretor) | US-03 |

Cada controlador recebe e devolve objetos **DTO** (_Data Transfer Objects_), nunca entidades JPA diretamente.

### 3.2 Camada de Aplicação (Services)

Cada `@Service` encapsula regras de negócio e orquestra repositórios. As operações de escrita são anotadas com `@Transactional`.

| Service | Métodos principais | Regras de domínio aplicadas |
|---|---|---|
| `ColaboradorService` | `autenticar()`, `criarColaborador()`, `alterarPermissoes()` | RNF-04 |
| `AlojamentoService` | `consultarDisponibilidade()`, `listarDisponiveis()` | RD-01 |
| `TutorService` | `registarTutor()`, `pesquisar()` | RD-05 |
| `AnimalService` | `registarAnimal()`, `atualizarEstadoSaude()`, `listarPorEstadoSaude()` | RD-05, RD-08 |
| `ReservaService` | `criarReserva()`, `cancelarReserva()`, `listar()` | RD-01, RD-06 |
| `EstadiaService` | `registarCheckIn()`, `registarCheckOut()`, `registarCuidadoDiario()` | RD-02, RD-03, RD-07 |
| `ServicoExtraService` | `registarServicoExtra()`, `listarPorEstadia()` | RD-09 |
| `ClinicaService` | `registarIntervencaoClinica()`, `consultarHistorial()` | RD-09 |
| `PagamentoService` | `registarPagamentoCheckIn()`, `registarPagamentoCheckOut()` | RD-04 |
| `LimpezaService` | `registarLimpezaConcluida()` | RD-01 |
| `RelatorioService` | `gerarDashboard()`, `gerarRelatorioPeriodo()` | US-02, US-04, US-05 |

### 3.3 Camada de Domínio

Entidades JPA anotadas com `@Entity`, mapeando diretamente o modelo de domínio da Etapa 1.

| Entidade | Tabela BD | Notas |
|---|---|---|
| `Colaborador` | `colaborador` | Inclui campo `senha` (hash BCrypt) |
| `Tutor` | `tutor` | |
| `Animal` | `animal` | Enum `Especie` limitado a CAO/GATO (RD-08) |
| `Alojamento` | `alojamento` | Estado de limpeza controlado por enum |
| `Reserva` | `reserva` | Estado não transitável para ATIVA após CANCELADA (RD-06) |
| `Estadia` | `estadia` | Relação 1:1 com Reserva |
| `Pagamento` | `pagamento` | Máx. 2 por estadia, momento CHECK_IN ou CHECK_OUT |
| `ServicoExtra` | `servico_extra` | Custo imutável após check-out (RD-09) |
| `IntervencaoClinica` | `intervencao_clinica` | Custo imutável após check-out (RD-09) |
| `RegistoCuidado` | `registo_cuidado` | |
| `Nota` | `nota` | |

Enums Java correspondem diretamente às enumerações do modelo de domínio: `Especie`, `EstadoSaude`, `TipoAlojamento`, `EstadoLimpeza`, `EstadoReserva`, `EstadoEstadia`, `EstadoPagamento`, `MetodoPagamento`, `MomentoPagamento`, `TipoServicoExtra`, `TipoColaborador`.

### 3.4 Camada de Dados (Repositories)

Interfaces que estendem `JpaRepository<T, Long>`. Consultas complexas usam JPQL com `@Query`.

| Repository | Consultas relevantes |
|---|---|
| `AlojamentoRepository` | `findDisponiveis(dataInicio, dataFim)` — cruza reservas ativas e estado de limpeza (RD-01) |
| `ReservaRepository` | `findByAnimalAndEstadoAtiva(animalId)` — verifica sobreposições |
| `EstadiaRepository` | `findEmCursoPorAnimal(animalId)` — garante unicidade (RD-07) |
| `AnimalRepository` | `findByEstadoSaudeAlteradoOuCritico()` — lista para veterinário (US-24) |
| `PagamentoRepository` | `findByEstadia(estadiaId)` |
| `RelatorioRepository` | Queries de agregação para dashboard e relatórios |

---

## 4. Autenticação e Autorização

Implementada com **Spring Security 6**.

- Autenticação por formulário HTML (`/login`) com sessão HTTP.  
- Palavras-passe armazenadas com **BCryptPasswordEncoder**.  
- Controlo de acesso por papel com `@PreAuthorize` e `hasRole()`.  
- Sessão invalidada no logout; CSRF ativo em todos os formulários.

### Mapeamento perfil → funcionalidades

| Perfil (`TipoColaborador`) | Acesso permitido |
|---|---|
| `DIRETOR` | Tudo, incluindo gestão de colaboradores e relatórios |
| `FUNCIONARIO_RECEPCAO` | Tutores, animais, reservas, check-in/out, pagamentos, disponibilidade |
| `CUIDADOR` | Cuidados diários, serviços extra, notas, estado de saúde |
| `MEDICO_VETERINARIO` | Historial clínico, intervenções, lista de animais com estado alterado |
| `RESPONSAVEL_LIMPEZA` | Lista de alojamentos pendentes, marcação como limpo |

---

## 5. Estrutura de Pacotes (Spring Boot)

```
pt.hotel.animais/
├── config/
│   ├── SecurityConfig.java
│   └── WebMvcConfig.java
├── controller/
│   ├── AuthController.java
│   ├── AlojamentoController.java
│   ├── TutorAnimalController.java
│   ├── ReservaController.java
│   ├── EstadiaController.java
│   ├── PagamentoController.java
│   ├── ServicoExtraController.java
│   ├── ClinicaController.java
│   ├── LimpezaController.java
│   ├── RelatorioController.java
│   └── ColaboradorController.java
├── service/
│   ├── ColaboradorService.java
│   ├── AlojamentoService.java
│   ├── TutorService.java
│   ├── AnimalService.java
│   ├── ReservaService.java
│   ├── EstadiaService.java
│   ├── ServicoExtraService.java
│   ├── ClinicaService.java
│   ├── PagamentoService.java
│   ├── LimpezaService.java
│   └── RelatorioService.java
├── repository/
│   ├── ColaboradorRepository.java
│   ├── AlojamentoRepository.java
│   ├── TutorRepository.java
│   ├── AnimalRepository.java
│   ├── ReservaRepository.java
│   ├── EstadiaRepository.java
│   ├── ServicoExtraRepository.java
│   ├── ClinicaRepository.java
│   ├── PagamentoRepository.java
│   └── RegistoCuidadoRepository.java
├── model/
│   ├── Colaborador.java
│   ├── Tutor.java
│   ├── Animal.java
│   ├── Alojamento.java
│   ├── Reserva.java
│   ├── Estadia.java
│   ├── Pagamento.java
│   ├── ServicoExtra.java
│   ├── IntervencaoClinica.java
│   ├── RegistoCuidado.java
│   ├── Nota.java
│   └── enums/
│       ├── Especie.java
│       ├── EstadoSaude.java
│       ├── TipoAlojamento.java
│       ├── EstadoLimpeza.java
│       ├── EstadoReserva.java
│       ├── EstadoEstadia.java
│       ├── EstadoPagamento.java
│       ├── MetodoPagamento.java
│       ├── MomentoPagamento.java
│       ├── TipoServicoExtra.java
│       └── TipoColaborador.java
└── dto/
    ├── ReservaDTO.java
    ├── EstadiaDTO.java
    ├── PagamentoDTO.java
    ├── AnimalDTO.java
    ├── TutorDTO.java
    ├── RelatorioDTO.java
    └── DashboardDTO.java
```

Templates Thymeleaf residem em `src/main/resources/templates/`, organizados por módulo funcional.

---

## 6. Tecnologias e Dependências

| Componente | Tecnologia |
|---|---|
| Linguagem | Java |
| Framework web | Spring Boot (Spring MVC) |
| Segurança | Spring Security |
| Persistência | Spring Data JPA |
| Base de dados | MySQL |
| Template engine | Thymeleaf |
| CSS / UI | AdminLTE |
| Gestão de dependências e construção | Maven |
| Testes | JUnit + Mockito + Spring Boot Test |
| Ambiente de execução | Docker |


---

## 7. Rastreabilidade com Requisitos Não Funcionais

| RNF | Decisão arquitetural |
|---|---|
| RNF-01 (tempo resposta < 2 s leitura / 3 s escrita) | Spring Data JPA com consultas indexadas; Thymeleaf caching de templates; operação em rede local |
| RNF-02 (usabilidade) | Bootstrap 5 com componentes acessíveis; fluxos simples no Thymeleaf |
| RNF-03 (disponibilidade horária) | Spring Boot embarcado (Tomcat); sem dependência de servidor externo gerido manualmente |
| RNF-04 (autenticação e perfis) | Spring Security 6 com BCrypt e `@PreAuthorize` por método |
| RNF-05 (confidencialidade / RGPD) | Dados pessoais apenas em camada de dados; BCrypt para senhas; HTTPS recomendado em produção |
| RNF-06 (compatibilidade hardware) | Aplicação web acedida via browser; sem cliente instalado |
| RNF-07 (escalabilidade) | Arquitetura em camadas facilita extração futura de módulos; Spring Boot pronto para containerização |
| RNF-08 (backups diários) | BD MySQL com mysqldump agendado; scripts em `Etapa3/03-scripts/` |
| RNF-09 (operação contínua) | Stateless HTTP + sessão gerida pelo Spring Security; sem limites de turno na arquitetura |

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

As decisoes detalhadas encontram-se em `docs/Etapa2/04-architecture-decisions/`.

| ID | Decisão | Alternativa rejeitada | Justificação |
|---|---|---|---|
| AD-01 | Monólito em camadas | Microserviços | Equipa pequena, 10 utilizadores, prazo de 3 semanas |
| AD-02 | Spring MVC + Thymeleaf (SSR) | REST + SPA (React/Angular) | Reduz complexidade de estado; SSR adequado à escala |
| AD-03 | MySQL como BD principal | PostgreSQL | Familiaridade da equipa; sem features exclusivas do Postgres necessárias |
| AD-04 | Spring Security com sessão | JWT stateless | Sistema interno em rede local; sessão é mais simples e segura no contexto |
| AD-05 | DTO entre Controller e Service | Exposição direta de entidades | Isola a camada de apresentação do modelo de domínio; evita serialização indevida |

---

## 10. Pressupostos

1. O sistema opera em rede local; não é considerado acesso remoto externo nesta fase.
2. Um único servidor Java (Spring Boot embarcado) serve toda a aplicação.
3. As cópias de segurança são geridas pelo sistema operativo/script agendado, não pela aplicação.
4. O ambiente de desenvolvimento usa H2; o ambiente de produção usa MySQL com o mesmo schema Hibernate.
