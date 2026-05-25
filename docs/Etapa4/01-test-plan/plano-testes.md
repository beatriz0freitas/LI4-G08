# Plano de Testes — PatasBigodesApp

**Projeto:** Sistema de Gestão de Hotel de Animais (LI4-G08)  
**Etapa:** 4 — Verificação, Validação e Avaliação da Qualidade  
**Data:** 2026-05-25  
**Ferramenta de geração:** Claude Code (LLM assistido)

---

## 1. Âmbito

Este plano cobre todos os testes automatizados desenvolvidos para o sistema **PatasBigodesApp**, um sistema Spring Boot MVC com persistência MySQL, que implementa os requisitos funcionais RF-01 a RF-17.

O objetivo é garantir que:
- Cada componente funciona isoladamente (testes unitários)
- Os módulos colaboram corretamente (testes de integração)
- Os fluxos de utilizador são aceites pelo sistema (testes de aceitação via MockMvc)
- A qualidade do código é mensurável (cobertura JaCoCo + mutation testing PITest)

---

## 2. Estratégia de Testes

### 2.1 Níveis

| Nível | Tecnologia | Requisitos | Âmbito |
|-------|-----------|-----------|--------|
| **Unitários** | JUnit 5 + Mockito | Sem BD | Lógica de serviços e domínio |
| **Integração** | JUnit 5 + @SpringBootTest | MySQL (Docker) | Fluxos completos com BD real |
| **Sistema / Web** | @WebMvcTest + MockMvc | Sem BD | Controllers e segurança HTTP |

### 2.2 Ferramentas

| Ferramenta | Versão | Uso |
|-----------|--------|-----|
| JUnit 5 | 5.x (via Spring Boot) | Framework de testes |
| Mockito | 5.x (via Spring Boot) | Mocking de dependências |
| MockMvc | Spring Boot Test | Testes de controllers HTTP |
| JaCoCo | 0.8.12 | Cobertura de código |
| PITest | 1.17.0 | Mutation testing |
| Spring Security Test | 6.x | Autenticação em testes |

### 2.3 Geração Assistida por LLM

Os casos de teste foram gerados com apoio do **Claude Code** (Anthropic), utilizado como agente de análise de requisitos e geração de testes. O LLM analisou:
- Os requisitos funcionais (RF-01 a RF-17)
- O código de produção das classes `Service` e `Controller`
- Os casos de borda (edge cases) e caminhos de erro
- Os critérios de aceitação das user stories

---

## 3. Categorias de Testes

### 3.1 Testes Unitários de Serviço (`/service/`)

Testam a lógica de negócio sem base de dados, usando Mockito para substituir os repositórios JPA.

| Ficheiro | Serviço testado | Nº testes |
|---------|----------------|-----------|
| `AlojamentoServiceTest` | AlojamentoService | 10 |
| `AlteracaoEstadoSaudeServiceTest` | AlteracaoEstadoSaudeService | 7 |
| `AnimalServiceTest` | AnimalService | 8 |
| `ColaboradorServiceTest` | ColaboradorService | 10 |
| `DisponibilidadeServiceTest` | DisponibilidadeService | 4 |
| `EstadiaServiceTest` | EstadiaService | 6 |
| `HistoricoServiceTest` | HistoricoService | 6 |
| `LimpezaServiceTest` | LimpezaService | 3 |
| `NotaServiceTest` | NotaService | 3 |
| `PagamentoServiceTest` | PagamentoService | 8 |
| `RegistoCuidadoServiceTest` | RegistoCuidadoService | 5 |
| `RegraDominioServiceTest` | RegraDominioService | 10 |
| `RelatorioServiceTest` | RelatorioService | 6 |
| `ReservaServiceCancelTest` | ReservaService (cancelar) | 2 |
| `ReservaServiceCreateTest` | ReservaService (criar) | 2 |
| `ReservaServiceUnitTest` | ReservaService (restante) | 13 |
| `ServicoExtraServiceTest` | ServicoExtraService | 6 |
| `TutorServiceTest` | TutorService | 11 |

**Total unitários de serviço:** ~120 testes

### 3.2 Testes de Controllers Web (`/controller/`)

Testam os endpoints HTTP sem base de dados, usando `@WebMvcTest` com MockMvc e `@MockBean` para serviços.

| Ficheiro | Controller testado | Nº testes |
|---------|-------------------|-----------|
| `AlojamentoControllerTest` | AlojamentoController | 2 |
| `AnimalControllerTest` | AnimalController | 3 |
| `AuthControllerTest` | AuthController | 2 |
| `ClinicaControllerTest` | ClinicaController | 3 |
| `ColaboradorControllerTest` | ColaboradorController | 4 |
| `EstadiaControllerTest` | EstadiaController | 6 |
| `HistoricoControllerTest` | HistoricoController | 2 |
| `HistoricoAuthorizationMvcTest` | Autorização Histórico | 2 |
| `NotaControllerTest` | NotaController | 1 |
| `PagamentoControllerTest` | PagamentoController | 3 |
| `PlanoCuidadosControllerTest` | PlanoCuidadosController | 1 |
| `RegistoCuidadoControllerTest` | RegistoCuidadoController | 2 |
| `RelatorioControllerTest` | RelatorioController | 3 |
| `SecurityAuthorizationMvcTest` | Segurança geral | 4 |
| `ServicoExtraControllerTest` | ServicoExtraController | 2 |

**Total controllers:** ~40 testes

### 3.3 Testes de Integração (`/integration/`) — requerem MySQL

Testam fluxos completos com a base de dados real (Docker).

| Ficheiro | Fluxo testado |
|---------|--------------|
| `CheckInIntegrationTest` | Check-in completo |
| `CheckOutIntegrationTest` | Check-out completo |
| `DisponibilidadeIntegrationTest` | Verificação de disponibilidade |
| `PagamentoCheckInIntegrationTest` | Pagamento no check-in |
| `ReservaCancelIntegrationTest` | Cancelamento de reserva |
| `ReservaConfirmIntegrationTest` | Confirmação de reserva |
| `ReservaCreateIntegrationTest` | Criação de reserva |
| `SecurityIntegrationTest` | Controlo de acesso |

---

## 4. Exclusões

| O que não é testado | Motivo |
|--------------------|--------|
| Templates Thymeleaf (HTML) | Teste de UI requereria Selenium/Playwright |
| `PlanoCuidadosService` | Método marcado como `Not implemented yet` |
| `DashboardService` | Requer base de dados — coberto por testes de integração |
| Migrações Flyway | Verificadas implicitamente nos testes de integração |

---

## 5. Critérios de Aceitação

| Critério | Meta | Resultado |
|---------|------|-----------|
| Testes unitários passam sem BD | 100% | ✅ 160/160 |
| Cobertura de instruções | ≥ 60% | ✅ 69.9% |
| Cobertura de métodos | ≥ 70% | ✅ 75.2% |
| Zero falhas em CI (sem Docker) | 0 falhas | ✅ 0 |

---

## 6. Execução

```bash
# Testes unitários (sem MySQL) — rápido
make test-unit

# Testes com cobertura JaCoCo
make coverage-unit

# Testes de integração (requer Docker)
make test-integration

# Todos os testes (requer Docker)
make test

# Mutation testing PITest
make mutation
```

Ver relatórios em:
- JaCoCo: `target/site/jacoco/index.html`
- PITest: `target/pit-reports/index.html`
