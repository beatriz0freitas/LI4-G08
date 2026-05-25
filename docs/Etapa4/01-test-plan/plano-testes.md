# Plano de Testes - PatasBigodesApp

**Projeto:** Sistema de Gestão de Hotel de Animais (LI4-G08)  
**Etapa:** 4 - Verificação, Validação e Avaliação da Qualidade  
**Data:** 2026-05-25  
**Ferramenta de apoio:** LLM assistido, com revisão sobre o código e requisitos do projeto

---

## 1. Âmbito

Este plano cobre os testes automatizados desenvolvidos para o sistema **PatasBigodesApp**, uma aplicação Spring Boot MVC com persistência MySQL, que implementa os requisitos funcionais RF-01 a RF-17 e as regras de domínio RD-01 a RD-09.

O objetivo é garantir que:
- Cada componente funciona isoladamente, através de testes unitários.
- Os módulos colaboram corretamente, através de testes de integração.
- Os fluxos de utilizador são aceites pelo sistema, através de testes WebMvc/MockMvc e testes de integração.
- A satisfação da SRS é rastreada contra RF, RD, RNF e UC.
- A qualidade do código é mensurável através de JaCoCo, PITest e métricas ISO/IEC 25010.

---

## 2. Estratégia de Testes

### 2.1 Níveis

| Nível | Tecnologia | Requisitos | Âmbito |
|-------|------------|------------|--------|
| Unitários | JUnit 5 + Mockito | Sem BD | Lógica de serviços e domínio |
| Integração | JUnit 5 + `@SpringBootTest` | MySQL via Docker | Fluxos completos com BD real |
| Sistema / Web | `@WebMvcTest` + MockMvc | Sem BD | Controllers, templates renderizados e segurança HTTP |
| Aceitação | MockMvc + fluxos de serviço | Sem BD / MySQL quando aplicável | Cenários UC-01 a UC-13 |

### 2.2 Ferramentas

| Ferramenta | Versão | Uso |
|------------|--------|-----|
| JUnit 5 | 5.x, via Spring Boot | Framework de testes |
| Mockito | 5.x, via Spring Boot | Mocking de dependências |
| MockMvc | Spring Boot Test | Testes de controllers HTTP |
| JaCoCo | 0.8.12 | Cobertura de código |
| PITest | 1.17.0 | Mutation testing |
| Spring Security Test | 6.x | Autenticação e autorização em testes |

### 2.3 Geração Assistida por LLM

Os casos de teste foram gerados e revistos com apoio de LLM, usado como agente de análise de requisitos e de identificação de casos de borda. O LLM analisou:
- Requisitos funcionais RF-01 a RF-17.
- Regras de domínio RD-01 a RD-09.
- Requisitos não funcionais RNF-01 a RNF-09.
- Código de produção das classes `Service` e `Controller`.
- Caminhos de erro, validações, permissões e critérios de aceitação das user stories.

---

## 3. Categorias de Testes

### 3.1 Testes Unitários de Serviço

Testam a lógica de negócio sem base de dados, usando Mockito para substituir os repositórios JPA.

| Ficheiro | Serviço testado | Nº testes |
|----------|-----------------|-----------|
| `AlojamentoServiceTest` | `AlojamentoService` | 10 |
| `AlteracaoEstadoSaudeServiceTest` | `AlteracaoEstadoSaudeService` | 7 |
| `AnimalServiceTest` | `AnimalService` | 8 |
| `ColaboradorServiceTest` | `ColaboradorService` | 11 |
| `DisponibilidadeServiceTest` | `DisponibilidadeService` | 4 |
| `EstadiaServiceTest` | `EstadiaService` | 10 |
| `HistoricoServiceTest` | `HistoricoService` | 10 |
| `LimpezaServiceTest` | `LimpezaService` | 3 |
| `NotaServiceTest` | `NotaService` | 3 |
| `PagamentoServiceTest` | `PagamentoService` | 8 |
| `RegistoCuidadoServiceTest` | `RegistoCuidadoService` | 5 |
| `RegraDominioServiceTest` | `RegraDominioService` | 10 |
| `RelatorioServiceTest` | `RelatorioService` | 7 |
| `ReservaServiceCancelTest` | `ReservaService` (cancelar) | 2 |
| `ReservaServiceCreateTest` | `ReservaService` (criar) | 2 |
| `ReservaServiceUnitTest` | `ReservaService` (restante) | 13 |
| `ServicoExtraServiceTest` | `ServicoExtraService` | 6 |
| `TutorServiceTest` | `TutorService` | 11 |

**Total unitários de serviço sem MySQL:** 130 testes.

### 3.2 Testes de Controllers Web

Testam endpoints HTTP sem base de dados, usando `@WebMvcTest` com MockMvc e `@MockBean` para serviços.

| Ficheiro | Controller testado | Nº testes |
|----------|--------------------|-----------|
| `AlojamentoControllerTest` | `AlojamentoController` | 2 |
| `AnimalControllerTest` | `AnimalController` | 3 |
| `AuthControllerTest` | `AuthController` | 2 |
| `ClinicaControllerTest` | `ClinicaController` | 3 |
| `ColaboradorControllerTest` | `ColaboradorController` | 4 |
| `EstadiaControllerTest` | `EstadiaController` | 6 |
| `HistoricoAuthorizationMvcTest` | Autorização do histórico | 2 |
| `HistoricoControllerTest` | `HistoricoController` | 2 |
| `NotaControllerTest` | `NotaController` | 1 |
| `PagamentoControllerTest` | `PagamentoController` | 3 |
| `PlanoCuidadosControllerTest` | `PlanoCuidadosController` | 1 |
| `RegistoCuidadoControllerTest` | `RegistoCuidadoController` | 2 |
| `RelatorioControllerTest` | `RelatorioController` | 3 |
| `SecurityAuthorizationMvcTest` | Segurança geral | 4 |
| `ServicoExtraControllerTest` | `ServicoExtraController` | 2 |

**Total controllers/WebMvc sem MySQL:** 40 testes.

### 3.3 Testes de Integração

Testam fluxos completos com base de dados MySQL em Docker. Estes testes não fazem parte da execução `coverage-unit`.

| Ficheiro | Fluxo testado | Nº testes |
|----------|---------------|-----------|
| `HotelAnimaisApplicationTests` | Arranque do contexto Spring | 1 |
| `AlojamentoRepositoryIntegrationTest` | Queries de disponibilidade | 1 |
| `HistoricoRepositoryIntegrationTest` | Pesquisa de histórico | 1 |
| `CheckInIntegrationTest` | Check-in completo | 1 |
| `CheckOutIntegrationTest` | Check-out completo | 1 |
| `DisponibilidadeIntegrationTest` | Verificação de disponibilidade | 2 |
| `PagamentoCheckInIntegrationTest` | Pagamento no check-in | 2 |
| `ReservaCancelIntegrationTest` | Cancelamento de reserva | 1 |
| `ReservaConfirmIntegrationTest` | Confirmação de reserva | 1 |
| `ReservaCreateIntegrationTest` | Criação de reserva | 1 |
| `SecurityIntegrationTest` | Controlo de acesso end-to-end | 3 |
| `DashboardControllerTest` | Dashboard com contexto completo | 1 |
| `LimpezaControllerTest` | Fluxo de limpeza | 2 |
| `ReservaRenderingControllerTest` | Renderização de reserva | 2 |
| `ReservaWizardControllerTest` | Wizard de reserva | 2 |
| `TutorAnimalControllerTemplateTest` | Template de tutor e animal | 1 |
| `AlojamentoServiceTimingTests` | Tempo de resposta de alojamentos | 1 |
| `CheckInServiceTest` | Serviço de check-in com contexto Spring | 3 |
| `CheckOutSequenceServiceTest` | Sequência de check-out | 3 |
| `DashboardServiceIntegrationTest` | Métricas do dashboard | 1 |
| `PagamentoCheckInCalculoTest` | Cálculo de pagamento no check-in | 1 |
| `ReservaServiceTests` | Overbooking e concorrência | 2 |
| `TutorServiceTimingTests` | Tempo de resposta de tutores | 1 |

**Total integração, repositórios, timing e SpringBootTest com MySQL:** 35 testes.

**Nota:** `CheckInServiceTest_simple.java` existe no código de testes, mas não é executado por `mvn test` porque o nome da classe não corresponde aos padrões normais do Maven Surefire. Caso a equipa queira mantê-lo na suíte, deve ser renomeado para terminar em `Test`.

### 3.4 Testes de Aceitação

Os testes de aceitação foram definidos a partir dos casos de uso UC-01 a UC-13. Validam respostas HTTP, templates esperados, redirecionamentos, mensagens flash e efeitos de negócio observáveis.

| UC | Critério de aceitação automatizado | Evidência |
|----|------------------------------------|-----------|
| UC-01 | Acesso autenticado e restrições por perfil | `SecurityAuthorizationMvcTest`, `HistoricoAuthorizationMvcTest` |
| UC-02 | Consulta de disponibilidade com validação de datas e espécies | `AlojamentoServiceTest`, `DisponibilidadeServiceTest` |
| UC-03 | Registo e consulta de tutores/animais | `TutorServiceTest`, `AnimalServiceTest` |
| UC-04 | Criação de reserva válida e rejeição de datas inválidas | `ReservaServiceCreateTest`, `ReservaServiceUnitTest` |
| UC-05 | Cancelamento apenas em estados permitidos | `ReservaServiceCancelTest`, `ReservaServiceUnitTest` |
| UC-06 | Check-in cria estadia em curso | `EstadiaServiceTest`, `EstadiaControllerTest` |
| UC-07 | Check-out termina estadia e redireciona para histórico | `EstadiaServiceTest`, `EstadiaControllerTest` |
| UC-08 | Pagamentos registados com método, momento e estado válidos | `PagamentoServiceTest`, `PagamentoControllerTest` |
| UC-09 | Cuidados registados apenas em estadias em curso | `RegistoCuidadoServiceTest`, `RegistoCuidadoControllerTest` |
| UC-10 | Serviços extra registados e consultáveis | `ServicoExtraServiceTest`, `ServicoExtraControllerTest` |
| UC-11 | Histórico clínico e operacional agregados | `HistoricoServiceTest`, `ClinicaControllerTest` |
| UC-12 | Estado de limpeza atualizado | `AlojamentoServiceTest`, `LimpezaServiceTest` |
| UC-13 | Relatórios e dashboard agregam métricas operacionais | `RelatorioServiceTest`, `RelatorioControllerTest` |

---

## 4. Exclusões e Limitações

| O que não é totalmente testado | Motivo |
|--------------------------------|--------|
| Testes end-to-end com browser real | Exigiriam Selenium/Playwright e não estão configurados no projeto |
| `PlanoCuidadosService` | Serviço marcado como funcionalidade pendente |
| Testes de integração na execução unitária | Requerem MySQL via Docker |
| Cópias de segurança automáticas | Requisito operacional, validado por desenho/infraestrutura e não por teste unitário |

---

## 5. Critérios de Aceitação da Etapa

| Critério | Meta | Resultado |
|----------|------|-----------|
| Testes unitários e WebMvc passam sem BD | 100% | 170/170 |
| Suíte completa com MySQL passa | 100% | 205/205 |
| Cobertura de instruções | >= 60% | 80.4% |
| Cobertura de métodos | >= 70% | 85.2% |
| Falhas na execução com Docker/MySQL | 0 | 0 |
| SRS funcional verificada por testes | >= 90% dos RF | 16/17 RF com cobertura completa; RF-11 parcial |
| Mutation testing configurado | Sim | PITest executado sobre `service.*` e `model.*` |

---

## 6. Execução

```bash
# Testes unitários e WebMvcTest, sem MySQL
make test-unit

# Testes com cobertura JaCoCo
make coverage-unit

# Testes de integração, requer Docker
make test-integration

# Todos os testes, requer Docker
make test

# Mutation testing PITest
make mutation
```

Relatórios:
- JaCoCo: `target/site/jacoco/index.html`
- PITest: `target/pit-reports/index.html`
