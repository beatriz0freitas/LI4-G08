# Resultados dos Testes — PatasBigodesApp

**Data de execução:** 2026-05-25  
**Comando:** `make coverage-unit`  
**Java:** OpenJDK 21.0.2  
**Spring Boot:** 3.3.5

---

## 1. Sumário de Execução

| Categoria | Testes | Passaram | Falharam | Ignorados |
|-----------|--------|----------|----------|-----------|
| Serviços (unit) | ~120 | 120 | 0 | 0 |
| Controllers (WebMvc) | ~40 | 40 | 0 | 0 |
| **Total (sem MySQL)** | **160** | **160** | **0** | **0** |

> Os testes de integração (que requerem MySQL via Docker) estão catalogados mas não incluídos nesta execução. Para os correr: `make test-integration`.

---

## 2. Resultados por Ficheiro de Teste

### 2.1 Serviços

| Ficheiro | Testes | Status |
|---------|--------|--------|
| AlojamentoServiceTest | 10 | ✅ PASS |
| AlteracaoEstadoSaudeServiceTest | 7 | ✅ PASS |
| AnimalServiceTest | 8 | ✅ PASS |
| ColaboradorServiceTest | 10 | ✅ PASS |
| DisponibilidadeServiceTest | 4 | ✅ PASS |
| EstadiaServiceTest | 6 | ✅ PASS |
| HistoricoServiceTest | 6 | ✅ PASS |
| LimpezaServiceTest | 3 | ✅ PASS |
| NotaServiceTest | 3 | ✅ PASS |
| PagamentoServiceTest | 8 | ✅ PASS |
| RegistoCuidadoServiceTest | 5 | ✅ PASS |
| RegraDominioServiceTest | 10 | ✅ PASS |
| RelatorioServiceTest | 6 | ✅ PASS |
| ReservaServiceCancelTest | 2 | ✅ PASS |
| ReservaServiceCreateTest | 2 | ✅ PASS |
| ReservaServiceUnitTest | 13 | ✅ PASS |
| ServicoExtraServiceTest | 6 | ✅ PASS |
| TutorServiceTest | 11 | ✅ PASS |

### 2.2 Controllers (WebMvcTest)

| Ficheiro | Testes | Status |
|---------|--------|--------|
| AlojamentoControllerTest | 2 | ✅ PASS |
| AnimalControllerTest | 3 | ✅ PASS |
| AuthControllerTest | 2 | ✅ PASS |
| ClinicaControllerTest | 3 | ✅ PASS |
| ColaboradorControllerTest | 4 | ✅ PASS |
| EstadiaControllerTest | 6 | ✅ PASS |
| HistoricoAuthorizationMvcTest | 2 | ✅ PASS |
| HistoricoControllerTest | 2 | ✅ PASS |
| NotaControllerTest | 1 | ✅ PASS |
| PagamentoControllerTest | 3 | ✅ PASS |
| PlanoCuidadosControllerTest | 1 | ✅ PASS |
| RegistoCuidadoControllerTest | 2 | ✅ PASS |
| RelatorioControllerTest | 3 | ✅ PASS |
| SecurityAuthorizationMvcTest | 4 | ✅ PASS |
| ServicoExtraControllerTest | 2 | ✅ PASS |

---

## 3. Cobertura de Código (JaCoCo)

### 3.1 Total do Projeto

| Métrica | Coberto | Total | Percentagem |
|---------|---------|-------|-------------|
| **Instructions** | 4 699 | 6 719 | **69.9%** |
| **Lines** | 1 065 | 1 560 | **68.3%** |
| **Branches** | 147 | 262 | **56.1%** |
| **Methods** | 376 | 500 | **75.2%** |

### 3.2 Por Pacote

| Pacote | Instruções | Métodos |
|--------|-----------|---------|
| `service` | **87.8%** | 78.0% |
| `config` | 97.6% | 75.0% |
| `dto` | 82.0% | 81.5% |
| `model.enums` | 82.4% | 81.0% |
| `model` | 59.7% | 53.3% |
| `controller` | 37.2%* | 55.2%* |

> *Controllers `LimpezaController` e `ReservaController` têm 0% porque os seus testes requerem MySQL (testes de integração). Excluindo-os, a cobertura de controllers sobe para ~75%.

### 3.3 Análise por Serviço (destaques)

| Serviço | Cobertura estimada |
|---------|-------------------|
| RelatorioService | ~95% |
| ColaboradorService | ~90% |
| TutorService | ~88% |
| ReservaService | ~85% |
| PagamentoService | ~85% |
| AlojamentoService | ~82% |
| EstadiaService | ~80% |
| PlanoCuidadosService | 0% (não implementado) |

---

## 4. Relatório HTML

O relatório detalhado está disponível em:

```
target/site/jacoco/index.html
```

Para gerar:
```bash
make coverage-unit
```

---

## 5. Testes de Integração (requerem Docker)

Os seguintes testes necessitam de MySQL e são executados com `make test-integration`:

| Ficheiro | Descrição | Estado |
|---------|-----------|--------|
| ReservaServiceTests | Overbooking + concorrência | Requer Docker |
| CheckInIntegrationTest | Fluxo completo check-in | Requer Docker |
| CheckOutIntegrationTest | Fluxo completo check-out | Requer Docker |
| DisponibilidadeIntegrationTest | Disponibilidade com BD real | Requer Docker |
| PagamentoCheckInIntegrationTest | Pagamento no check-in | Requer Docker |
| ReservaCancelIntegrationTest | Cancelamento de reserva | Requer Docker |
| ReservaConfirmIntegrationTest | Confirmação de reserva | Requer Docker |
| ReservaCreateIntegrationTest | Criação com validações | Requer Docker |
| SecurityIntegrationTest | Controlo de acesso end-to-end | Requer Docker |
| LimpezaControllerTest | Limpeza de alojamentos | Requer Docker |
| AlojamentoRepositoryIntegrationTest | Queries de disponibilidade | Requer Docker |
| HistoricoRepositoryIntegrationTest | Pesquisa histórico | Requer Docker |
