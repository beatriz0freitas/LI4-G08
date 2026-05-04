# Data Model: Fundação do sistema — Entidades e Enums (Phase 1)

**Created**: 2026-05-04 | **Spec**: [spec.md](./spec.md) | **Research**: [research.md](./research.md)

**Principle**: Todas as entidades e enums provêm da [documentação de Etapa 1 — Modelo de Domínio](../../../docs/Etapa1/04-domain-model/domain-model.md). Não são inventadas.

---

## Domain Entities — Phase 1

### 1. Alojamento

**Purpose**: Representa uma unidade de alojamento (quarto, cage, etc.) no hotel.

| Field | Type | Null | Unique | Index | Comment |
|-------|------|------|--------|-------|---------|
| `id` | BIGINT | NO | YES | PK | Auto-increment, gerado no insert |
| `identificacao` | VARCHAR(50) | NO | YES | YES | ex: "01", "A1", "Suite 101" |
| `estadoLimpeza` | ENUM | NO | — | YES | PENDENTE \| CONCLUIDO (default: PENDENTE) |

**Relationships**:
- Será relacionado com `Reserva` em Phase 2 (1..* alojamento).
- Será relacionado com `Estadia` em Phase 3 (1..* alojamento).

**Constraints**:
- `identificacao` é única (não pode haver dois alojamentos com mesmo ID visual).
- `estadoLimpeza` inicia como PENDENTE após check-out (Phase 3).
- Disponibilidade = `estadoLimpeza = CONCLUIDO AND no active booking` (conforme [RD-01](../../../docs/Etapa1/02-requirements/domain/RD-01.md)).

**JPA Mapping**:
```java
@Entity
@Table(name = "alojamento")
public class Alojamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String identificacao;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoLimpeza estadoLimpeza = EstadoLimpeza.PENDENTE;
}
```

**SQL Migration**:
```sql
CREATE TABLE alojamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    identificacao VARCHAR(50) NOT NULL UNIQUE,
    estado_limpeza ENUM('PENDENTE', 'CONCLUIDO') NOT NULL DEFAULT 'PENDENTE',
    INDEX idx_estado_limpeza (estado_limpeza)
);
```

---

### 2. Colaborador

**Purpose**: Representa um membro da equipa com papel específico no hotel.

| Field | Type | Null | Unique | Index | Comment |
|-------|------|------|--------|-------|---------|
| `id` | BIGINT | NO | YES | PK | Auto-increment |
| `nome` | VARCHAR(100) | NO | — | — | ex: "João Silva" |
| `email` | VARCHAR(100) | NO | YES | YES | ex: "joao@hotel.pt" |
| `passwordHash` | VARCHAR(255) | NO | — | — | BCrypt hash (60 char) |
| `tipoColaborador` | ENUM | NO | — | YES | DIRETOR \| FUNCIONARIO_RECEPCAO \| CUIDADOR \| MEDICO_VETERINARIO \| RESPONSAVEL_LIMPEZA |

**Relationships**: Nenhuma em Phase 1. Phase 4 irá relacionar com RegistoCuidado, IntervencaoClinica, etc.

**Constraints**:
- `email` é única (login identifier).
- `passwordHash` é armazenado com BCrypt (12 rounds).
- `tipoColaborador` determina permissões via Spring Security ROLE_*.

**Phase 1 Note**: Em Phase 1, Colaborador é apenas persistido em in-memory (SecurityConfig). Será migrado para BD em Phase 5.

**JPA Mapping** (stub para Phase 1):
```java
@Entity
@Table(name = "colaborador")
public class Colaborador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String passwordHash;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoColaborador tipoColaborador;
}
```

**Future Relationships** (Phases 2-5):
- Colaborador → RegistoCuidado (1..* collab)
- Colaborador → IntervencaoClinica (1..* collab)
- Colaborador → Pagamento (1..* collab, "processado por")
- Colaborador → Nota (1..* collab, "autor")

---

## Enums

### 1. EstadoLimpeza

**Conforme**: [RD-01 — Disponibilidade de alojamento](../../../docs/Etapa1/02-requirements/domain/RD-01.md)

```java
public enum EstadoLimpeza {
    PENDENTE,    // Alojamento requer limpeza após check-out
    CONCLUIDO    // Alojamento foi limpo e está pronto para nova reserva
}
```

**State Machine**:
```
[PENDENTE] --mark-clean--> [CONCLUIDO]
   ↓
(não disponível para reservas)

[CONCLUIDO]
   ↓
(disponível para reservas, até ocupar)
   ↓
[PENDENTE]  (após check-out)
```

**Uso em Phase 1**:
- Default em novo Alojamento: PENDENTE
- Transição: LimpezaController → LimpezaService.marcarConcluido(alojamentoId)
- Consulta: AlojamentoService.obterDisponibilidade() filtra por `estadoLimpeza = CONCLUIDO`

---

### 2. TipoColaborador

**Conforme**: [UC-01 — Autenticar no Sistema](../../../docs/Etapa1/03-use-cases/UC-01.md) e mockups Etapa 2

```java
public enum TipoColaborador {
    DIRETOR,                    // Gestor, vista geral, dashboards, relatórios
    FUNCIONARIO_RECEPCAO,       // Receção, reservas, check-in/out
    CUIDADOR,                   // Atendimento animal, cuidados diários
    MEDICO_VETERINARIO,         // Histórico clínico, intervenções
    RESPONSAVEL_LIMPEZA         // Limpeza de alojamentos
}
```

**Permissões Mapeadas** (Spring Security):
```
DIRETOR                 → GET /dashboard
FUNCIONARIO_RECEPCAO    → GET /reservas, POST /checkin, POST /checkout
CUIDADOR                → GET /cuidados, POST /cuidados
MEDICO_VETERINARIO      → GET /clinica, POST /intervencao
RESPONSAVEL_LIMPEZA     → GET /limpeza, POST /limpeza/marcar-concluido
```

---

## Data Dictionary

| Entity | Attributes | Mutability | Lifecycle |
|--------|-----------|------------|-----------|
| **Alojamento** | id, identificacao, estadoLimpeza | estadoLimpeza mutable | Criado uma vez (seed), estado muda em limpeza/ocupação |
| **Colaborador** | id, nome, email, passwordHash, tipoColaborador | tipoColaborador, passwordHash mutáveis | Criado seed/admin, pode ser modificado (Phase 5) |

---

## Relationships Matrix (Cross-Phase)

```
Phase 1 (Foundation):
Alojamento [A]
Colaborador [C]

Phase 2 (Clients & Bookings):
Tutor
Animal [A] ← (1..*)
Reserva [A] ← (1..*)

Phase 3 (Stays & Payments):
Estadia [A] ← (1..*)
Pagamento

Phase 4 (Care & Clinic):
RegistoCuidado [C] → (1..*)
Nota
ServicoExtra
IntervencaoClinica [C] → (1..*)

Phase 5 (Reporting):
RelatorioService
```

---

## Constraints & Validations

### Business Rules (RD-01)

```java
// Validation: Alojamento não está disponível se estadoLimpeza != CONCLUIDO
public class AlojamentoService {
    public boolean estaDisponivel(Long alojamentoId) {
        Alojamento alojamento = repository.findById(alojamentoId).orElseThrow();
        
        return alojamento.getEstadoLimpeza() == EstadoLimpeza.CONCLUIDO
            && !temReservaAtiva(alojamentoId);  // Phase 2
    }
}
```

### Database Constraints

```sql
ALTER TABLE alojamento
    ADD CONSTRAINT `chk_estado_limpeza` 
    CHECK (estado_limpeza IN ('PENDENTE', 'CONCLUIDO'));

ALTER TABLE colaborador
    ADD CONSTRAINT `chk_tipo_colaborador` 
    CHECK (tipo_colaborador IN (
        'DIRETOR',
        'FUNCIONARIO_RECEPCAO',
        'CUIDADOR',
        'MEDICO_VETERINARIO',
        'RESPONSAVEL_LIMPEZA'
    ));
```

---

## Persistence Strategy

### Flyway Migrations

**V1__create_alojamento.sql** (existing):
```sql
CREATE TABLE alojamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    identificacao VARCHAR(50) NOT NULL UNIQUE,
    estado_limpeza ENUM('PENDENTE', 'CONCLUIDO') NOT NULL DEFAULT 'PENDENTE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_estado_limpeza (estado_limpeza)
);
```

**V2__seed_alojamentos.sql** (existing):
```sql
INSERT INTO alojamento (identificacao, estado_limpeza) VALUES
    ('01', 'CONCLUIDO'),
    ('02', 'PENDENTE'),
    ('03', 'CONCLUIDO'),
    ('04', 'CONCLUIDO');
```

**V3__create_colaborador.sql** (to be created):
```sql
CREATE TABLE colaborador (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    tipo_colaborador ENUM(
        'DIRETOR',
        'FUNCIONARIO_RECEPCAO',
        'CUIDADOR',
        'MEDICO_VETERINARIO',
        'RESPONSAVEL_LIMPEZA'
    ) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_tipo_colaborador (tipo_colaborador)
);
```

**V4__seed_colaboradores.sql** (to be created — optional if using in-memory):
```sql
-- May be populated in Phase 5 based on SecurityConfig users
```

---

## Testing Fixtures

```java
// AlojamentoTestFixture.java
public class AlojamentoTestFixture {
    public static Alojamento createAlojamento(String id) {
        Alojamento a = new Alojamento();
        a.setIdentificacao(id);
        a.setEstadoLimpeza(EstadoLimpeza.CONCLUIDO);
        return a;
    }
}

// TipoColaboradorTestFixture.java
public class ColaboradorTestFixture {
    public static Colaborador createDiretor() {
        Colaborador c = new Colaborador();
        c.setNome("Director Test");
        c.setEmail("director@test.pt");
        c.setPasswordHash(hashPassword("password"));
        c.setTipoColaborador(TipoColaborador.DIRETOR);
        return c;
    }
}
```

---

## ER Diagram (Text)

```
┌─────────────────────────┐
│      Alojamento         │
├─────────────────────────┤
│ id (PK)                 │
│ identificacao (UNIQUE)  │
│ estadoLimpeza (ENUM)    │
└─────────────────────────┘
         ↑
         │
    (1..* in Phase 2)
         │
    Reserva (Phase 2)
         │
         ↓  (1..1)

┌─────────────────────────┐
│      Colaborador        │
├─────────────────────────┤
│ id (PK)                 │
│ nome                    │
│ email (UNIQUE)          │
│ passwordHash            │
│ tipoColaborador (ENUM)  │
└─────────────────────────┘
         ↑
         │
    (1..* in Phases 4-5)
         │
  RegistoCuidado, IntervencaoClinica, etc.
```

---

## Summary

- **Entidades Phase 1**: Alojamento, Colaborador
- **Enums Phase 1**: EstadoLimpeza, TipoColaborador
- **Constraints**: RD-01 aplicada; validações em camada de serviço e DB
- **Migrações**: V1-V2 existentes; V3-V4 a criar
- **Testabilidade**: Fixtures para ambos; mocks simples para testes unitários

**Status**: ✅ **Pronto para implementação** (Phase 2 tasks)
