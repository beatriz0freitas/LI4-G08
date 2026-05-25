# Métricas de Qualidade - ISO/IEC 25010

**Projeto:** PatasBigodesApp (LI4-G08)  
**Norma:** ISO/IEC 25010:2011 - Systems and Software Quality Requirements and Evaluation (SQuaRE)  
**Data:** 2026-05-25

---

## 1. Enquadramento

A norma ISO/IEC 25010 define um modelo de qualidade de produto de software com 8 características principais. Esta análise avalia o sistema **PatasBigodesApp** face a cada característica, usando evidências dos testes automatizados, relatórios de cobertura e decisões arquiteturais documentadas nas etapas anteriores.

---

## 2. Indicadores Medidos

| Indicador | Resultado |
|-----------|-----------|
| Testes automatizados executados | 205 |
| Falhas / erros / ignorados | 0 / 0 / 0 |
| Cobertura de instruções | 80.4% |
| Cobertura de linhas | 80.2% |
| Cobertura de ramos | 71.0% |
| Cobertura de métodos | 85.2% |
| Mutation score PITest | 72.1% |
| Requisitos funcionais com verificação automatizada completa | 16/17 |
| Requisitos funcionais com verificação parcial | RF-11 |

---

## 3. Características de Qualidade

### 3.1 Adequação Funcional

| Sub-característica | Avaliação | Evidência |
|--------------------|-----------|-----------|
| Completude funcional | Alta, com uma exceção | RF-01 a RF-10 e RF-12 a RF-17 têm testes automatizados; RF-11 está parcial |
| Correção funcional | Alta | 205 testes com MySQL passam sem falhas |
| Adequação funcional | Alta | Testes de aceitação cobrem UC-01 a UC-13 |

**Métrica:** 16 de 17 requisitos funcionais verificados de forma completa por testes automatizados.  
**Limitação:** RF-11, Plano de cuidados, tem controller testado mas serviço ainda pendente.

### 3.2 Eficiência de Desempenho

| Sub-característica | Avaliação | Evidência |
|--------------------|-----------|-----------|
| Comportamento temporal | Adequado | `AlojamentoServiceTimingTests` e `TutorServiceTimingTests` validam tempos de resposta em cenários específicos com MySQL |
| Utilização de recursos | Adequado | Arquitetura monolítica em camadas, sem overhead distribuído |
| Capacidade | Parcial | Testes unitários e alguns testes temporais existem, mas não há teste de carga formal |

**Relação com RNF-01:** parcialmente verificado. O requisito define limites até 10 utilizadores simultâneos; essa concorrência não foi medida nesta etapa.

### 3.3 Compatibilidade

| Sub-característica | Avaliação | Evidência |
|--------------------|-----------|-----------|
| Coexistência | Boa | Docker Compose isola aplicação e MySQL |
| Interoperabilidade | Não aplicável | O enunciado não define integrações externas |

**Relação com RNF-06:** suportado por desenho, mas não medido por teste automatizado em hardware real.

### 3.4 Usabilidade

| Sub-característica | Avaliação | Evidência |
|--------------------|-----------|-----------|
| Reconhecimento de adequação | Boa | Interface Thymeleaf organizada por módulos operacionais |
| Aprendizagem | Boa | Navegação e formulários consistentes por perfil |
| Operabilidade | Boa | Controllers testam redirecionamentos e mensagens flash |
| Proteção contra erros | Boa | Validações de formulário e exceções de negócio cobertas |

**Relação com RNF-02:** parcialmente verificado por inspeção e testes WebMvc. Não foram feitos testes com utilizadores reais.

### 3.5 Fiabilidade

| Sub-característica | Avaliação | Evidência |
|--------------------|-----------|-----------|
| Maturidade | Alta | 205 testes passam; PITest apresenta 72.1% de mutation score |
| Disponibilidade | Boa | Spring Boot Actuator e Docker Compose suportam operação controlada |
| Tolerância a falhas | Média | Exceções de negócio tratadas; sem mecanismos distribuídos, por não serem necessários no contexto |
| Recuperabilidade | Parcial | Transações JPA existem; backups automáticos dependem de operação/infraestrutura |

**Métricas de cobertura:**

| Métrica | Valor |
|---------|-------|
| Instruções | 80.4% |
| Linhas | 80.2% |
| Ramos | 71.0% |
| Métodos | 85.2% |

### 3.6 Segurança

| Sub-característica | Avaliação | Evidência |
|--------------------|-----------|-----------|
| Confidencialidade | Alta | Passwords em BCrypt; acesso condicionado por autenticação |
| Integridade | Alta | CSRF ativo e validação de entradas |
| Não repúdio | Média | Eventos de auditoria configurados; sem trilha de auditoria funcional completa testada |
| Responsabilização | Alta | Perfis `DIRETOR`, `FUNCIONARIO_RECEPCAO`, `CUIDADOR`, `MEDICO_VETERINARIO`, `RESPONSAVEL_LIMPEZA` |
| Autenticidade | Alta | Spring Security com formulário de login e logout |

**Evidências de testes:**
- `SecurityAuthorizationMvcTest` verifica endpoints restritos.
- `HistoricoAuthorizationMvcTest` verifica acesso ao histórico.
- `ColaboradorServiceTest` verifica BCrypt e unicidade de username/email.

**Relação com RNF-04 e RNF-05:** verificada por desenho e testes automatizados.

### 3.7 Manutenibilidade

| Sub-característica | Avaliação | Evidência |
|--------------------|-----------|-----------|
| Modularidade | Alta | Arquitetura em camadas Controller -> Service -> Repository |
| Reusabilidade | Boa | DTOs e `RegraDominioService` concentram validações reutilizáveis |
| Analisabilidade | Boa | Cobertura de métodos de 85.2% e relatórios HTML JaCoCo/PITest |
| Modificabilidade | Boa | Testes unitários com Mockito reduzem risco de regressão |
| Testabilidade | Alta | 205 testes automatizados, incluindo integração com MySQL |

**Mutation testing:**

| Métrica PITest | Valor |
|----------------|-------|
| Mutações geradas | 466 |
| Mortas | 336 |
| Sobreviventes | 79 |
| Sem cobertura | 51 |
| Mutation score | 72.1% |

### 3.8 Portabilidade

| Sub-característica | Avaliação | Evidência |
|--------------------|-----------|-----------|
| Adaptabilidade | Alta | Perfis de configuração e variáveis de ambiente |
| Instalabilidade | Alta | `Makefile`, Dockerfile e Docker Compose |
| Substituibilidade | Boa | Separação por interfaces de serviço e configuração externa da BD |

---

## 4. Síntese ISO/IEC 25010

| Característica | Classificação | Justificação curta |
|----------------|---------------|--------------------|
| Adequação funcional | Alta | RF quase totalmente cobertos; RF-11 parcial |
| Eficiência de desempenho | Boa | Testes temporais existem, sem teste de carga completo |
| Compatibilidade | Boa | Docker e aplicação standalone |
| Usabilidade | Boa | Fluxos WebMvc e templates testados parcialmente |
| Fiabilidade | Boa | Suíte estável e cobertura suficiente |
| Segurança | Alta | Autenticação, autorização e BCrypt testados |
| Manutenibilidade | Alta | Camadas claras, cobertura e PITest |
| Portabilidade | Alta | Docker, Makefile e configuração externa |

---

## 5. Limitações e Pontos de Melhoria

| Área | Limitação | Sugestão |
|------|-----------|----------|
| RF-11 | `PlanoCuidadosService` ainda não implementado | Implementar serviço e adicionar testes unitários e de aceitação |
| Cobertura de ramos | 71.0%, ainda inferior à cobertura de métodos | Adicionar testes para caminhos alternativos de validação e erro |
| Testes de integração | Executados com sucesso, mas dependem de Docker/MySQL local | Manter `make test-integration` como validação antes da entrega final |
| Nomeação de testes | `CheckInServiceTest_simple.java` existe em `src/test`, mas não é executado por `mvn test` devido ao padrão do Maven Surefire | Renomear para terminar em `Test` ou remover se for apenas um teste auxiliar antigo |
| Usabilidade | Sem teste formal com utilizadores | Fazer sessão curta de aceitação com perfis reais ou simulados |
| RNF-08 | Backups não provados por teste automatizado | Documentar estratégia operacional e ensaiar recuperação |
| PITest | 79 mutações sobreviveram e 51 ficaram sem cobertura | Priorizar mutações sobreviventes em serviços críticos |
