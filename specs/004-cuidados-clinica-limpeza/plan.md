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

## Phase 0: Research

- Confirmar o enquadramento técnico real: Java 21, Spring Boot 3.3.5, JPA, Flyway, Thymeleaf e Spring Security.
- Validar a extensão do modelo de domínio para `RegistoCuidado`, `ServicoExtra`, `IntervencaoClinica`, `Nota` e `AlteracaoEstadoSaude`.
- Definir padrões de listagem filtrada e paginação para o historial operacional e clínico.
- Confirmar estratégia de autorização para receção, cuidador e veterinário.

## Phase 1: Design & Contracts

- Criar `data-model.md` com entidades, relações, validações e regras de auditoria.
- Criar `quickstart.md` com instruções para executar a aplicação e validar o fluxo da feature.
- Não criar contratos externos nesta fase: a feature é interna à aplicação e expõe apenas fluxos web server-rendered.

## Re-check After Design

- Rever a consistência entre o modelo de dados, os cenários do spec e os requisitos RF/RNF/RD.
- Confirmar que o fluxo de implementação se mantém incremental e testável antes de gerar `tasks.md`.

## Implementation Details (skeletons)

Below are minimal, implementation-focused artefacts to guide development: a Flyway DDL skeleton and example DTO / service signatures.

1) Flyway migration skeleton (place in `src/main/resources/db/migration/V5__cuidados_clinica_limpeza.sql`):

-- Create RegistoCuidado
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
    tipo VARCHAR(50) NOT NULL,
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
    severidade VARCHAR(50) NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    autor_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    CONSTRAINT fk_aes_estadia FOREIGN KEY (estadia_id) REFERENCES estadia(id)
);

2) DTO examples (Java):
- `RegistoCuidadoCreateRequest { Long estadiaId; String descricao; LocalDateTime dataHora; }`
- `RegistoCuidadoView { Long id; Long estadiaId; String descricao; LocalDateTime dataHora; String autorNome; }`
- `ServicoExtraCreateRequest { Long estadiaId; String tipo; BigDecimal custo; LocalDateTime dataHora; }`

3) Service method signatures (examples):
- `RegistoCuidadoView create(RegistoCuidadoCreateRequest req)` in `RegistoCuidadoService`
- `Page<RegistoCuidadoView> listByEstadia(Long estadiaId, Pageable pageable)` in `RegistoCuidadoService`
- `ServicoExtraView register(ServicoExtraCreateRequest req)` in `ServicoExtraService`
- `IntervencaoClinicaView register(IntervencaoClinicaCreateRequest req)` in `ClinicaService`

These skeletons should be copied into the repository as initial templates for the corresponding tasks (T001/T002/T009/T010 etc.).
