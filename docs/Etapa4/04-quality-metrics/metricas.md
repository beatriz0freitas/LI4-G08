# Métricas de Qualidade — ISO/IEC 25010

**Projeto:** PatasBigodesApp (LI4-G08)  
**Norma:** ISO/IEC 25010:2011 — Systems and Software Quality Requirements and Evaluation (SQuaRE)  
**Data:** 2026-05-25

---

## 1. Enquadramento

A norma ISO/IEC 25010 define um modelo de qualidade de produto de software com 8 características principais. Esta análise avalia o sistema **PatasBigodesApp** face a cada característica, com evidências dos testes automatizados e do código produzido.

---

## 2. Características de Qualidade

### 2.1 Adequação Funcional *(Functional Suitability)*

> Grau em que o produto fornece funções que satisfazem necessidades declaradas.

| Sub-característica | Avaliação | Evidência |
|-------------------|-----------|-----------|
| Completude funcional | **Alta** | RF-01 a RF-17 implementados (16/17 com testes ✅) |
| Correção funcional | **Alta** | 160 testes unitários passam, 0 falhas |
| Adequação funcional | **Alta** | Casos de uso UC-01 a UC-13 cobertos |

**Métrica:** 16 de 17 requisitos funcionais verificados por testes automatizados (94%).  
**Nota:** RF-11 (Plano de cuidados) está parcialmente implementado — serviço marcado como pendente.

---

### 2.2 Eficiência de Desempenho *(Performance Efficiency)*

> Desempenho em relação aos recursos utilizados.

| Sub-característica | Avaliação | Evidência |
|-------------------|-----------|-----------|
| Comportamento temporal | **Adequado** | Queries JPA otimizadas com `JOIN FETCH` para evitar N+1 |
| Utilização de recursos | **Adequado** | Arquitetura monolítica, sem overhead de microserviços |
| Capacidade | **Adequado** | Índices definidos nas colunas de pesquisa frequente |

**Métrica:** `AlojamentoServiceTimingTests` valida que queries de disponibilidade executam em < 200ms.

---

### 2.3 Compatibilidade *(Compatibility)*

> Capacidade de trocar informação com outros sistemas.

| Sub-característica | Avaliação | Evidência |
|-------------------|-----------|-----------|
| Coexistência | **Alta** | Aplicação Docker isolada, portas configuráveis |
| Interoperabilidade | **N/A** | Sistema standalone sem integrações externas definidas |

---

### 2.4 Usabilidade *(Usability)*

> Grau em que o produto pode ser usado efetivamente.

| Sub-característica | Avaliação | Evidência |
|-------------------|-----------|-----------|
| Reconhecimento de adequação | **Alta** | Interface Thymeleaf com páginas por módulo, breadcrumbs, navegação lateral |
| Aprendizagem | **Alta** | UI consistente com roles bem definidos (diretor, receção, cuidador, etc.) |
| Operabilidade | **Alta** | Mensagens flash de sucesso/erro em todas as operações |
| Proteção contra erros | **Alta** | Validação de formulários com erros de campo; exceções tratadas no controller |

**Nota:** Testes de usabilidade formal (Selenium) não foram realizados nesta etapa.

---

### 2.5 Fiabilidade *(Reliability)*

> Grau em que o sistema executa funções especificadas sem falhas.

| Sub-característica | Avaliação | Evidência |
|-------------------|-----------|-----------|
| Maturidade | **Alta** | 160 testes passam, 0 falhas; lógica de negócio defensiva |
| Disponibilidade | **Alta** | Spring Boot com health check via Actuator |
| Tolerância a falhas | **Média** | Exceções de negócio tratadas; sem circuit breaker (não aplicável a monolito) |
| Recuperabilidade | **Alta** | Transações JPA com rollback automático em falha |

**Métricas de cobertura:**

| Métrica | Valor |
|---------|-------|
| Cobertura de instruções | **69.9%** |
| Cobertura de linhas | **68.3%** |
| Cobertura de ramos | **56.1%** |
| Cobertura de métodos | **75.2%** |

---

### 2.6 Segurança *(Security)*

> Grau em que o produto protege informação e dados.

| Sub-característica | Avaliação | Evidência |
|-------------------|-----------|-----------|
| Confidencialidade | **Alta** | Passwords armazenadas em BCrypt; sem dados sensíveis em logs |
| Integridade | **Alta** | CSRF protection activo; validação de entrada em formulários |
| Não-repúdio | **Média** | Eventos de auditoria via `ApplicationEventPublisher` (AuditApplicationEvent) |
| Responsabilização | **Alta** | Roles bem definidos (DIRETOR, FUNCIONARIO_RECEPCAO, CUIDADOR, MEDICO_VETERINARIO, RESPONSAVEL_LIMPEZA) |
| Autenticidade | **Alta** | Spring Security com autenticação por formulário; logout com invalidação de sessão |

**Evidências de testes:**
- `SecurityAuthorizationMvcTest`: verifica que endpoints restritos devolvem 403 sem role correto
- `HistoricoAuthorizationMvcTest`: verifica controlo de acesso ao histórico
- `ColaboradorServiceTest`: verifica BCrypt e unicidade de username/email

---

### 2.7 Manutenibilidade *(Maintainability)*

> Grau de eficácia com que o produto pode ser modificado.

| Sub-característica | Avaliação | Evidência |
|-------------------|-----------|-----------|
| Modularidade | **Alta** | Arquitetura em camadas (Controller → Service → Repository); interfaces para serviços (IAlojamentoService, IReservaService, etc.) |
| Reusabilidade | **Alta** | DTOs reutilizados; `RegraDominioService` centraliza validações comuns |
| Analisabilidade | **Alta** | Cobertura de 75.2% dos métodos facilita deteção de regressões |
| Modificabilidade | **Alta** | Testes unitários com Mockito permitem refactoring seguro |
| Testabilidade | **Alta** | 57 ficheiros de teste; padrão consistente (MockitoExtension + @WebMvcTest) |

**Mutation Testing (PITest):**

| Configuração | Valor |
|-------------|-------|
| Ferramenta | PITest 1.17.0 |
| Mutadores | STRONGER |
| Targets | `service.*`, `model.*` |
| Comando | `make mutation` |

> O PITest está configurado e funcional. Para obter o mutation score:
> ```bash
> make mutation
> # Relatório em: target/pit-reports/index.html
> ```

---

### 2.8 Portabilidade *(Portability)*

> Grau em que o sistema pode ser transferido para outro ambiente.

| Sub-característica | Avaliação | Evidência |
|-------------------|-----------|-----------|
| Adaptabilidade | **Alta** | Docker Compose para todos os ambientes (dev, test, prod) |
| Instalabilidade | **Alta** | `make up` inicia toda a stack; Flyway gere migrações automáticas |
| Substituibilidade | **Alta** | Abstração via interfaces de serviço; BD configurável por variáveis de ambiente |

---

## 3. Síntese

| Característica ISO/IEC 25010 | Classificação |
|-----------------------------|---------------|
| Adequação Funcional | ⭐⭐⭐⭐⭐ Alta |
| Eficiência de Desempenho | ⭐⭐⭐⭐ Boa |
| Compatibilidade | ⭐⭐⭐⭐ Boa |
| Usabilidade | ⭐⭐⭐⭐ Boa |
| Fiabilidade | ⭐⭐⭐⭐ Boa |
| Segurança | ⭐⭐⭐⭐⭐ Alta |
| Manutenibilidade | ⭐⭐⭐⭐⭐ Alta |
| Portabilidade | ⭐⭐⭐⭐⭐ Alta |

---

## 4. Limitações e Pontos de Melhoria

| Área | Limitação | Sugestão |
|------|-----------|---------|
| Cobertura de ramos | 56.1% — abaixo do ideal | Adicionar testes para caminhos alternativos em `if/else` complexos |
| RF-11 | PlanoCuidadosService não implementado | Implementar e adicionar testes |
| Testes de UI | Sem testes Selenium/Playwright | Considerar para validação de aceitação |
| Mutation score | Não medido ainda (PITest configurado) | Executar `make mutation` e registar score |
| Cobertura de modelo | 59.7% | Adicionar testes para métodos de domínio (`Reserva.podeSerCancelada()`, etc.) |
