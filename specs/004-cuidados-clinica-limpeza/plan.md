# Implementation Plan: Fase 4 — Operação diária, clínica e limpeza avançada

**Branch**: `004-cuidados-clinica-limpeza` | **Date**: 2026-05-18 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/004-cuidados-clinica-limpeza/spec.md`

## Summary

Implementar a operação diária e o acompanhamento clínico durante estadias, cobrindo registo de cuidados, notas operacionais, serviços extra, alterações ao estado de saúde e historial clínico com faturação complementar no check-out. A abordagem segue a arquitetura existente Spring MVC + Thymeleaf + JPA, com evolução incremental do modelo de domínio, migrações Flyway e validação por perfis de utilizador.

## Technical Context

**Language/Version**: Java 21
**Primary Dependencies**: Spring Boot 3.3.5, Spring MVC, Thymeleaf, Spring Security, Spring Data JPA, Flyway, Hibernate, Lombok
**Storage**: Base de dados relacional via JPA/Flyway; MySQL em desenvolvimento, produção e testes de integração
**Testing**: Maven Surefire/Failsafe com `spring-boot-starter-test` e `spring-security-test`
**Target Platform**: Aplicação web server-rendered para Linux/macOS em ambiente local e servidor JVM
**Project Type**: web-service / web application monolítica com SSR
**Performance Goals**: consultas de histórico e listas paginadas com resposta inferior a 2 segundos em utilização normal
**Constraints**: manter rastreabilidade com RF/RNF/RD canónicos; preservar controlo de acesso por perfil; garantir integridade transacional nas operações de check-in, check-out e registos clínicos
**Scale/Scope**: uma aplicação monolítica para o domínio do hotel de animais, com extensões de cuidado, clínica e faturação complementar

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- Domain Scope First: conforme. A feature mantém-se no domínio do hotel de animais e está rastreada a US-14, US-15, US-16, US-17, US-18, US-22 e US-23.
- Scenario-Driven Requirements: conforme. O spec usa cenários testáveis e requisitos com IDs canónicos.
- Modular Separation of Concerns: conforme. A implementação será distribuída por entidades, repositórios, serviços, controladores, templates e migrações.
- Verification Before Expansion: conforme. Cada incremento terá cenários de aceitação e validação por testes.
- Data Integrity, Security, and Operational Reliability: conforme. O plano inclui autenticação por perfil, auditoria de autor/data e validação transacional.

## Project Structure

### Documentation (this feature)

```text
specs/004-cuidados-clinica-limpeza/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
└── tasks.md
```

### Source Code (repository root)

```text
PatasBigodesApp/
├── src/main/java/pt/hotel/animais/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
├── src/main/resources/
│   ├── templates/
│   └── db/migration/
└── src/test/java/pt/hotel/animais/
    ├── controller/
    ├── repository/
    └── service/
```

**Structure Decision**: A feature evolui dentro da aplicação monolítica existente, reutilizando Spring MVC/Thymeleaf para interação e JPA/Flyway para persistência e migrações.

## Clarifications Applied (Session 2026-05-25, LAC-02)

As seguintes clarificações foram consolidadas na spec `spec.md`:

- **Origem do plano**: Combinação de histórico do animal + instruções da reserva (US-17) + ajustes manuais na estadia.
- **Dinâmica**: Plano é dinâmico; pode ser modificado a qualquer momento durante a estadia, com histórico de alterações auditado.
- **Granularidade**: Híbrido — tarefas recorrentes estruturadas (ex.: ALIMENTACAO_MANHA, MEDICACAO_12H) + campo de notas/instruções adicionais.
- **Vínculo**: Duplo — animal mantém histórico persistente; cada estadia herda/cria cópia ajustável do plano.
- **Estados/Ciclos de vida**: Plano com priorização (ROTINA, URGENTE, CRÍTICO) que muda conforme alterações de saúde (US-16); encerra automaticamente pós-check-out.
- **Novo Requisito de Domínio**: RD-10 documenta o ciclo de vida completo e a dinâmica do plano.

**Impacto**: Necessário implementar **duas novas entidades** (`PlanoCuidados` e `TarefaCuidado`) que **não estavam previstas no plano inicial**.

## Phase 0: Research

- Confirmar o enquadramento técnico real: Java 21, Spring Boot 3.3.5, JPA, Flyway, Thymeleaf e Spring Security.
- Validar a extensão do modelo de domínio para `PlanoCuidados`, `TarefaCuidado`, `RegistoCuidado`, `ServicoExtra`, `IntervencaoClinica`, `Nota` e `AlteracaoEstadoSaude`.
- Validar a estratégia de vínculo duplo (Animal ↔ PlanoCuidados histórico; Estadia ↔ PlanoCuidados ativa/cópia).
- Definir padrões de listagem filtrada e paginação para o historial operacional e clínico.
- Confirmar estratégia de autorização para receção, cuidador e veterinário.
- Validar a dinâmica de mudança de prioridade do plano conforme alterações de saúde (integração US-16 → PlanoCuidados).
- Pesquisar melhores práticas para auditoria de alterações a planos dinâmicos (histórico de versões vs histórico de eventos).

## Phase 1: Design & Contracts

- Criar `data-model.md` com entidades **atualizadas**: `PlanoCuidados` (novo), `TarefaCuidado` (novo), `RegistoCuidado`, `ServicoExtra`, `IntervencaoClinica`, `Nota` e `AlteracaoEstadoSaude`, incluindo relações, validações, regras de auditoria e estratégia de vínculo duplo.
- Criar `quickstart.md` com instruções para executar a aplicação e validar o fluxo da feature, incluindo ciclo de vida do plano e dinâmica de priorização.
- Não criar contratos externos nesta fase: a feature é interna à aplicação e expõe apenas fluxos web server-rendered.
- **Decisão de design crítica**: Estratégia de versionamento/auditoria do plano dinâmico — decidir entre histórico de versões completas vs histórico de eventos incrementais.

## Re-check After Design

- Rever a consistência entre o modelo de dados (incluindo novo `PlanoCuidados`), os cenários do spec e os requisitos RF/NF/RD.
- Validar que a integração de priorização automática (via AlteracaoEstadoSaude → PlanoCuidados) não cria duplicação de lógica com `RegistoCuidado`.
- Confirmar que o fluxo de implementação se mantém incremental e testável antes de gerar `tasks.md` atualizado.
- **ALERTA**: Tasks.md atual refere `PlanoCuidadosService` e `PlanoCuidadosController` mas o modelo real (PlanoCuidados + TarefaCuidado) é mais complexo que previsto. Será necessário revisar e potencialmente reordenar fases.

## Implementation Details (skeletons)

**⚠️ BREAKING CHANGE**: O modelo anterior não incluía `PlanoCuidados` e `TarefaCuidado` como entidades persistentes. A clarificação de LAC-02 torna estas entidades obrigatórias para suportar a dinâmica do plano. Abaixo está o modelo **revisto**.

Below are minimal, implementation-focused artefacts to guide development: updated Flyway DDL skeleton with new entities and example DTO / service signatures.

1) **Flyway migration skeleton** (place in `src/main/resources/db/migration/V5__cuidados_clinica_limpeza.sql`):

```sql
-- Create PlanoCuidados (nova entidade: núcleo da funcionalidade)
CREATE TABLE plano_cuidados (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    animal_id BIGINT NOT NULL,
    estadia_id BIGINT NOT NULL,
    data_inicio TIMESTAMP NOT NULL,
    data_fim TIMESTAMP,
    prioridade VARCHAR(50) NOT NULL DEFAULT 'ROTINA', -- ROTINA, URGENTE, CRITICO
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    instrucoes VARCHAR(2000), -- campo de notas livres
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by BIGINT,
    CONSTRAINT fk_pc_animal FOREIGN KEY (animal_id) REFERENCES animal(id),
    CONSTRAINT fk_pc_estadia FOREIGN KEY (estadia_id) REFERENCES estadia(id),
    CONSTRAINT uk_pc_estadia UNIQUE (estadia_id)
);

-- Create TarefaCuidado (nova entidade: tarefas recorrentes estruturadas)
CREATE TABLE tarefa_cuidado (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plano_cuidados_id BIGINT NOT NULL,
    tipo VARCHAR(100) NOT NULL, -- ALIMENTACAO_MANHA, MEDICACAO_12H, PASSEIO, LIMPEZA, OUTRO
    descricao VARCHAR(500),
    periodicidade VARCHAR(50) NOT NULL, -- UNICA, DIARIA, SEMANAL
    data_hora TIMESTAMP NOT NULL,
    concluida BOOLEAN DEFAULT FALSE,
    autor_conclusao_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    CONSTRAINT fk_tc_plano FOREIGN KEY (plano_cuidados_id) REFERENCES plano_cuidados(id)
);

-- Create RegistoCuidado (anotação livre, independente de TarefaCuidado)
CREATE TABLE registo_cuidado (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    estadia_id BIGINT NOT NULL,
    descricao VARCHAR(1000) NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    autor_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    CONSTRAINT fk_rc_estadia FOREIGN KEY (estadia_id) REFERENCES estadia(id)
);

-- Create ServicoExtra
CREATE TABLE servico_extra (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    estadia_id BIGINT NOT NULL,
    tipo VARCHAR(50) NOT NULL, -- BANHO, PASSEIO, OUTRO
    custo DECIMAL(10,2) NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    autor_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    CONSTRAINT fk_se_estadia FOREIGN KEY (estadia_id) REFERENCES estadia(id)
);

-- Create IntervencaoClinica
CREATE TABLE intervencao_clinica (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    estadia_id BIGINT NOT NULL,
    descricao VARCHAR(1000) NOT NULL,
    custo DECIMAL(10,2) NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    medico_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    CONSTRAINT fk_ic_estadia FOREIGN KEY (estadia_id) REFERENCES estadia(id)
);

-- Create Nota
CREATE TABLE nota (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reserva_id BIGINT NOT NULL,
    descricao VARCHAR(1000) NOT NULL,
    autor_id BIGINT NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    CONSTRAINT fk_n_reserva FOREIGN KEY (reserva_id) REFERENCES reserva(id)
);

-- Create AlteracaoEstadoSaude
CREATE TABLE alteracao_estado_saude (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    estadia_id BIGINT NOT NULL,
    descricao VARCHAR(1000) NOT NULL,
    severidade VARCHAR(50) NOT NULL, -- ROTINA, URGENTE, CRITICO
    data_hora TIMESTAMP NOT NULL,
    autor_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    CONSTRAINT fk_aes_estadia FOREIGN KEY (estadia_id) REFERENCES estadia(id)
);

-- Indexes para performance
CREATE INDEX idx_pc_animal ON plano_cuidados(animal_id);
CREATE INDEX idx_pc_estadia ON plano_cuidados(estadia_id);
CREATE INDEX idx_tc_plano ON tarefa_cuidado(plano_cuidados_id);
CREATE INDEX idx_rc_estadia ON registo_cuidado(estadia_id);
CREATE INDEX idx_rc_datahora ON registo_cuidado(data_hora DESC);
CREATE INDEX idx_se_estadia ON servico_extra(estadia_id);
CREATE INDEX idx_ic_estadia ON intervencao_clinica(estadia_id);
CREATE INDEX idx_aes_estadia ON alteracao_estado_saude(estadia_id);
```

2) **Domain Model Classes** (new/updated):

```java
// PlanoCuidados.java (NOVA)
public class PlanoCuidados {
    private Long id;
    private Long animalId;
    private Long estadiaId;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private PrioridadePlano prioridade; // ROTINA, URGENTE, CRITICO
    private boolean ativo;
    private String instrucoes;
    @OneToMany(mappedBy = "planoCuidados", cascade = CascadeType.ALL)
    private List<TarefaCuidado> tarefas;
    // audit fields: createdAt, createdBy, updatedAt, updatedBy
}

// TarefaCuidado.java (NOVA)
public class TarefaCuidado {
    private Long id;
    private Long planoCuidadosId;
    private String tipo; // ALIMENTACAO_MANHA, MEDICACAO_12H, PASSEIO, LIMPEZA, OUTRO
    private String descricao;
    private PeriodicidadeTarefa periodicidade; // UNICA, DIARIA, SEMANAL
    private LocalDateTime dataHora;
    private boolean concluida;
    private Long autorConclausaoId;
    // audit fields: createdAt, createdBy
}

// PrioridadePlano.java (NOVO ENUM)
public enum PrioridadePlano {
    ROTINA, URGENTE, CRITICO;
}

// PeriodicidadeTarefa.java (NOVO ENUM)
public enum PeriodicidadeTarefa {
    UNICA, DIARIA, SEMANAL;
}
```

3) **DTO Examples** (updated):

```java
// PlanoCuidadosDto.java (NOVA)
public class PlanoCuidadosDto {
    private Long id;
    private Long animalId;
    private Long estadiaId;
    private LocalDateTime dataInicio;
    private PrioridadePlano prioridade;
    private String instrucoes;
    private List<TarefaCuidadoDto> tarefas;
}

// TarefaCuidadoDto.java (NOVA)
public class TarefaCuidadoDto {
    private Long id;
    private String tipo;
    private String descricao;
    private PeriodicidadeTarefa periodicidade;
    private LocalDateTime dataHora;
    private boolean concluida;
}

// RegistoCuidadoDto.java (mantém-se, mas agora independente)
public class RegistoCuidadoDto {
    private Long id;
    private Long estadiaId;
    private String descricao;
    private LocalDateTime dataHora;
    private String autorNome;
}
```

4) **Service Method Signatures** (updated):

```java
// IPlanoCuidadosService.java (NOVO)
public interface IPlanoCuidadosService {
    // criar plano para nova estadia, herdando histórico do animal
    PlanoCuidadosDto criarPlanoParaEstadia(Long estadiaId, Long animalId);
    
    // consultar plano ativo de uma estadia
    PlanoCuidadosDto obterPlanoPorEstadia(Long estadiaId);
    
    // obter histórico de planos de um animal
    Page<PlanoCuidadosDto> listarPlanosDoAnimal(Long animalId, Pageable pageable);
    
    // adicionar/modificar tarefa no plano
    TarefaCuidadoDto adicionarTarefa(Long planoCuidadosId, TarefaCuidadoFormDto req);
    
    // marcar tarefa como concluída
    void marcarTarefaConcluida(Long tarefaId, Long autorId);
    
    // adicionar notas ao plano
    void adicionarInstrucoes(Long planoCuidadosId, String instrucoes, Long autorId);
    
    // mudar prioridade do plano (chamado quando AlteracaoEstadoSaude é criada)
    void atualizarPrioridade(Long planoCuidadosId, PrioridadePlano novaPrioridade, Long autorId);
    
    // encerrar plano (chamado no check-out)
    void encerrarPlano(Long planoCuidadosId);
}

// RegistoCuidadoService.java (mantém-se)
// + integração: registar RegistoCuidado NÃO modifica TarefaCuidado (são independentes)

// AlteracaoEstadoSaudeService.java (novo hook)
// + integração: ao criar AlteracaoEstadoSaude com severidade CRITICA/URGENTE,
//   chamará IPlanoCuidadosService.atualizarPrioridade()
```

5) **Controller Sketch**:

```java
// PlanoCuidadosController.java (revisado com novas ações)
@Controller
@RequestMapping("/cuidados/plano")
public class PlanoCuidadosController {
    
    @GetMapping("/{estadiaId}")
    public String verPlano(@PathVariable Long estadiaId, Model model) {
        // fetch PlanoCuidados para estadia ativa
        // incluindo lista de TarefaCuidado e histórico de registos
    }
    
    @PostMapping("/{planoCuidadosId}/tarefa")
    public String adicionarTarefa(@PathVariable Long planoCuidadosId, 
                                   @ModelAttribute TarefaCuidadoFormDto req) {
        // validar estadia ativa, autorização
        // chamar planoCuidadosService.adicionarTarefa()
    }
    
    @PostMapping("/{planoCuidadosId}/instrucoes")
    public String adicionarInstrucoes(@PathVariable Long planoCuidadosId,
                                       @RequestParam String instrucoes) {
        // chamar planoCuidadosService.adicionarInstrucoes()
    }
}
```

These **updated skeletons** should replace the previous templates for tasks T001, T002, T006/T007 (PlanoCuidados) and newly drive tasks T006b/T006c (TarefaCuidado, migrações).

These skeletons should be copied into the repository as initial templates for the corresponding tasks (T001/T002/T009/T010 etc.).
