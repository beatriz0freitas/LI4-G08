# Resultados dos Testes - PatasBigodesApp

**Data de execução:** 2026-05-25  
**Comando:** `make test-integration`  
**Java:** OpenJDK 21.0.2  
**Spring Boot:** 3.3.5

---

## 1. Sumário de Execução

| Categoria | Testes | Passaram | Falharam | Erros | Ignorados |
|-----------|--------|----------|----------|-------|-----------|
| Serviços unitários e regras de domínio | 130 | 130 | 0 | 0 | 0 |
| Controllers WebMvc sem BD | 40 | 40 | 0 | 0 | 0 |
| Integração, repositórios, timing e SpringBootTest com MySQL | 35 | 35 | 0 | 0 | 0 |
| **Total com MySQL** | **205** | **205** | **0** | **0** | **0** |

Esta execução subiu o serviço `db-tests` do Docker Compose, esperou pelo estado `healthy`, aplicou 7 migrações Flyway na base `hotelanimais_test` e correu a suíte Maven completa contra MySQL em `localhost:3308`.

Durante a execução foram emitidos avisos de Thymeleaf sobre a sintaxe antiga de fragmentos (`fragments/... :: ...`). Estes avisos não causaram falhas, mas ficam registados como ponto de manutenção futura.

---

## 2. Resultados por Ficheiro de Teste

### 2.1 Serviços

| Ficheiro | Testes | Status |
|----------|--------|--------|
| `AlojamentoServiceTest` | 10 | PASS |
| `AlteracaoEstadoSaudeServiceTest` | 7 | PASS |
| `AnimalServiceTest` | 8 | PASS |
| `ColaboradorServiceTest` | 11 | PASS |
| `DisponibilidadeServiceTest` | 4 | PASS |
| `EstadiaServiceTest` | 10 | PASS |
| `HistoricoServiceTest` | 10 | PASS |
| `LimpezaServiceTest` | 3 | PASS |
| `NotaServiceTest` | 3 | PASS |
| `PagamentoServiceTest` | 8 | PASS |
| `RegistoCuidadoServiceTest` | 5 | PASS |
| `RegraDominioServiceTest` | 10 | PASS |
| `RelatorioServiceTest` | 7 | PASS |
| `ReservaServiceCancelTest` | 2 | PASS |
| `ReservaServiceCreateTest` | 2 | PASS |
| `ReservaServiceUnitTest` | 13 | PASS |
| `ServicoExtraServiceTest` | 6 | PASS |
| `TutorServiceTest` | 11 | PASS |

### 2.2 Controllers WebMvc

| Ficheiro | Testes | Status |
|----------|--------|--------|
| `AlojamentoControllerTest` | 2 | PASS |
| `AnimalControllerTest` | 3 | PASS |
| `AuthControllerTest` | 2 | PASS |
| `ClinicaControllerTest` | 3 | PASS |
| `ColaboradorControllerTest` | 4 | PASS |
| `EstadiaControllerTest` | 6 | PASS |
| `HistoricoAuthorizationMvcTest` | 2 | PASS |
| `HistoricoControllerTest` | 2 | PASS |
| `NotaControllerTest` | 1 | PASS |
| `PagamentoControllerTest` | 3 | PASS |
| `PlanoCuidadosControllerTest` | 1 | PASS |
| `RegistoCuidadoControllerTest` | 2 | PASS |
| `RelatorioControllerTest` | 3 | PASS |
| `SecurityAuthorizationMvcTest` | 4 | PASS |
| `ServicoExtraControllerTest` | 2 | PASS |

### 2.3 Integração, Repositórios e SpringBootTest com MySQL

| Ficheiro | Testes | Status |
|----------|--------|--------|
| `HotelAnimaisApplicationTests` | 1 | PASS |
| `AlojamentoRepositoryIntegrationTest` | 1 | PASS |
| `HistoricoRepositoryIntegrationTest` | 1 | PASS |
| `CheckInIntegrationTest` | 1 | PASS |
| `CheckOutIntegrationTest` | 1 | PASS |
| `DisponibilidadeIntegrationTest` | 2 | PASS |
| `PagamentoCheckInIntegrationTest` | 2 | PASS |
| `ReservaCancelIntegrationTest` | 1 | PASS |
| `ReservaConfirmIntegrationTest` | 1 | PASS |
| `ReservaCreateIntegrationTest` | 1 | PASS |
| `SecurityIntegrationTest` | 3 | PASS |
| `DashboardControllerTest` | 1 | PASS |
| `LimpezaControllerTest` | 2 | PASS |
| `ReservaRenderingControllerTest` | 2 | PASS |
| `ReservaWizardControllerTest` | 2 | PASS |
| `TutorAnimalControllerTemplateTest` | 1 | PASS |
| `AlojamentoServiceTimingTests` | 1 | PASS |
| `CheckInServiceTest` | 3 | PASS |
| `CheckOutSequenceServiceTest` | 3 | PASS |
| `DashboardServiceIntegrationTest` | 1 | PASS |
| `PagamentoCheckInCalculoTest` | 1 | PASS |
| `ReservaServiceTests` | 2 | PASS |
| `TutorServiceTimingTests` | 1 | PASS |

**Nota de rastreabilidade:** existe também `CheckInServiceTest_simple.java` em `src/test`, mas o nome da classe não termina em `Test`, `Tests` ou `TestCase`. Por isso, não é executado pelo padrão normal do Maven Surefire usado em `mvn test` e não está incluído nos 205 testes desta execução.

---

## 3. Cobertura de Código (JaCoCo)

### 3.1 Total do Projeto

| Métrica | Coberto | Total | Percentagem |
|---------|---------|-------|-------------|
| Instruções | 5 404 | 6 719 | 80.4% |
| Linhas | 1 251 | 1 560 | 80.2% |
| Ramos | 186 | 262 | 71.0% |
| Métodos | 426 | 500 | 85.2% |

### 3.2 Observações por Pacote

| Pacote | Observação |
|--------|------------|
| `service` | Principal foco dos testes unitários; concentra a lógica de negócio e as regras RD. |
| `controller` | Coberto por WebMvcTest e por testes SpringBootTest com MySQL para fluxos que precisam de BD. |
| `model` e `model.enums` | Parcialmente cobertos por testes de serviços e validações de regras de domínio. |
| `dto` | Cobertura indireta através de controllers e serviços. |
| `config` | Segurança coberta por testes de autorização; auditoria fica sobretudo como validação de configuração. |

---

## 4. Mutation Testing (PITest)

O PITest está configurado para `pt.hotel.animais.service.*` e `pt.hotel.animais.model.*`, com mutadores `STRONGER`.

| Métrica | Valor |
|---------|-------|
| Mutações geradas | 466 |
| Mutações mortas | 336 |
| Mutações sobreviventes | 79 |
| Sem cobertura | 51 |
| Mutation score | 72.1% |

Relatório:

```text
target/pit-reports/index.html
```

---

## 5. Verificação SRS

| Grupo da SRS | Estado | Evidência |
|--------------|--------|-----------|
| RF-01 a RF-10 | Verificado | Testes de serviços, controllers e segurança |
| RF-11 | Parcial | `PlanoCuidadosControllerTest`; serviço ainda pendente |
| RF-12 a RF-17 | Verificado | Testes de cuidados, saúde, histórico, limpeza e serviços extra |
| RD-01 a RD-09 | Verificado | `RegraDominioServiceTest`, `AlojamentoServiceTest`, `ReservaService*`, `EstadiaServiceTest`, `PagamentoServiceTest` |
| RNF-01 | Parcial | Testes de tempo em `AlojamentoServiceTimingTests` e `TutorServiceTimingTests` executados com MySQL; não houve teste de carga com 10 utilizadores simultâneos |
| RNF-02 | Parcial | Verificação de templates e fluxos por MockMvc; sem teste formal com utilizadores |
| RNF-03, RNF-08, RNF-09 | Parcial | Dependem de operação/infraestrutura e não são provados por testes unitários |
| RNF-04, RNF-05 | Verificado | Spring Security, BCrypt, autorização por role e testes MockMvc |
| RNF-06, RNF-07 | Parcial | Evidência arquitetural via Docker e desenho monolítico; sem teste de carga |

---

## 6. Conclusão

A execução `make test-integration` terminou com sucesso e valida a suíte completa com MySQL. A principal limitação funcional continua a ser RF-11, por o serviço de plano de cuidados estar marcado como pendente.
