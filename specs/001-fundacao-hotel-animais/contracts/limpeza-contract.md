# API Contract: Cleaning Management (Phase 1)

**Version**: 1.0 | **Status**: Specification | **Conformance**: [US-20](../../../../docs/Etapa1/01-user-stories/user-stories.md), [US-21](../../../../docs/Etapa1/01-user-stories/user-stories.md), [RD-01](../../../../docs/Etapa1/02-requirements/domain/RD-01.md)

---

## Overview

The Cleaning contract defines the workflow for the cleaning staff (RESPONSAVEL_LIMPEZA) to view alojamentos and mark them as cleaned. The availability rule is governed by RD-01.

---

## Endpoints

### 1. GET /limpeza

**Purpose**: Display list of all alojamentos with their cleaning state

**Request**:
```http
GET /limpeza HTTP/1.1
Host: localhost:8080
Cookie: JSESSIONID=ABC123DEF456; Path=/
User-Agent: Mozilla/5.0
```

**Response** (200 OK):
```http
HTTP/1.1 200 OK
Content-Type: text/html;charset=UTF-8

<!DOCTYPE html>
<html lang="pt">
<head>
    <title>Gestão de Limpeza — Hotel Animais</title>
</head>
<body>
    <nav><!-- navbar fragment --></nav>
    <aside><!-- sidebar fragment --></aside>
    
    <main>
        <h1>Alojamentos para Limpeza</h1>
        
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>Identificação</th>
                    <th>Estado de Limpeza</th>
                    <th>Ação</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>01</td>
                    <td><span class="badge bg-warning">PENDENTE</span></td>
                    <td>
                        <form method="post" action="/limpeza/marcar-concluido">
                            <input type="hidden" name="alojamentoId" value="1">
                            <button type="submit" class="btn btn-sm btn-success">
                                Marcar como Limpo
                            </button>
                        </form>
                    </td>
                </tr>
                <tr>
                    <td>02</td>
                    <td><span class="badge bg-success">CONCLUIDO</span></td>
                    <td>—</td>
                </tr>
                <tr>
                    <td>03</td>
                    <td><span class="badge bg-success">CONCLUIDO</span></td>
                    <td>—</td>
                </tr>
                <tr>
                    <td>04</td>
                    <td><span class="badge bg-warning">PENDENTE</span></td>
                    <td>
                        <form method="post" action="/limpeza/marcar-concluido">
                            <input type="hidden" name="alojamentoId" value="4">
                            <button type="submit" class="btn btn-sm btn-success">
                                Marcar como Limpo
                            </button>
                        </form>
                    </td>
                </tr>
            </tbody>
        </table>
    </main>
    
    <footer><!-- footer fragment --></footer>
</body>
</html>
```

**Response Headers**:
```
Cache-Control: no-store, no-cache
ETag: W/"xyz123"
```

---

### 2. POST /limpeza/marcar-concluido

**Purpose**: Transition alojamento state from PENDENTE to CONCLUIDO

**Request**:
```http
POST /limpeza/marcar-concluido HTTP/1.1
Host: localhost:8080
Content-Type: application/x-www-form-urlencoded
Cookie: JSESSIONID=ABC123DEF456

alojamentoId=1&_csrf=token123456
```

**Response — Success** (302 Found):
```http
HTTP/1.1 302 Found
Location: /limpeza
Set-Cookie: success_message=Alojamento+marcado+como+limpo; Path=/; HttpOnly

<!-- Or render with flash attribute in Thymeleaf -->
```

**Response — Success (with GET redirect)** (200 OK):
```http
HTTP/1.1 302 Found
Location: /limpeza

<!-- User agent follows redirect, returns to GET /limpeza with updated state -->
```

**Response — Alojamento Not Found** (404 Not Found):
```http
HTTP/1.1 404 Not Found
Content-Type: text/html

<html>
    <body>
        <h1>404 — Alojamento não encontrado</h1>
    </body>
</html>
```

**Response — Transaction Failure** (500 Internal Server Error):
```http
HTTP/1.1 500 Internal Server Error
Content-Type: text/html

<html>
    <body>
        <h1>500 — Erro ao atualizar alojamento</h1>
        <p>Por favor, contacte o suporte técnico.</p>
    </body>
</html>
```

---

## View Model

Os alojamentos são passados do controller para a view via `Model` e iterados em Thymeleaf. Esta fase não expõe API JSON; a interação é feita por páginas HTML e submissão de formulários.

---

## State Transitions

**RD-01 Compliance**: An alojamento is available if and only if `estadoLimpeza = CONCLUIDO` and no active booking exists.

### State Machine Diagram

```
        ┌──────────────┐
        │  PENDENTE    │   (After checkout)
        │   (dirty)    │
        │              │◄─────────────────┐
        └──────────────┘                   │
               │                           │
               │ mark-clean (POST /limpeza │
               │ /marcar-concluido)        │
               │                           │
               v                           │
        ┌──────────────┐                   │
        │  CONCLUIDO   │   (Ready)         │
        │   (clean)    │                   │
        │              │─────────────────►─┤
        └──────────────┘   (next checkout) │
                                           │
```

**Valid Transitions**:
- PENDENTE → CONCLUIDO (mark clean)
- CONCLUIDO → PENDENTE (after checkout)

**Invalid Transitions** (Business Logic Errors):
- CONCLUIDO → CONCLUIDO (idempotent, returns 200)
- PENDENTE → PENDENTE (no-op, no state change)

---

## Access Control

| Role | Access | Action |
|------|--------|--------|
| DIRETOR | ✅ READ | View cleaning status (informational) |
| FUNCIONARIO_RECEPCAO | ✅ READ | View cleaning status (informational) |
| CUIDADOR | ❌ NO | Access denied (403) |
| MEDICO_VETERINARIO | ❌ NO | Access denied (403) |
| **RESPONSAVEL_LIMPEZA** | ✅ READ + WRITE | View + mark as clean |

**Authorization Check** (Phase 1):
```java
@GetMapping("/limpeza")
@PreAuthorize("hasAnyRole('RESPONSAVEL_LIMPEZA', 'DIRETOR')")
public String listarLimpeza(Model model) {
    // ...
}

@PostMapping("/limpeza/marcar-concluido")
@PreAuthorize("hasRole('RESPONSAVEL_LIMPEZA')")
public String marcarConcluido(@RequestParam Long alojamentoId) {
    // ...
}
```

---

## Error Handling

| Scenario | HTTP Status | Response | Notes |
|----------|------------|----------|-------|
| Not authenticated | 302 | Redirect to /login | No JSESSIONID |
| Unauthorized role | 403 | "Access Denied" page | Non-RESPONSAVEL_LIMPEZA user tries POST |
| Alojamento not found | 404 | "Not Found" page | Invalid alojamentoId |
| Concurrent update | 409 | "Conflict" (conditional) | Two cleaners mark same alojamento simultaneously |
| DB connection failure | 500 | "Internal Server Error" | Transient failure |
| Invalid form data | 400 | "Bad Request" | Missing required field |

**Concurrency Handling** (Phase 1): No optimistic locking. Last write wins. (Detalhes de expansão futura documentados em [future-expansions.md](../future-expansions.md).)

---

## Related Templates

### Cleaning List Template

```html
<!-- templates/limpeza/listar.html -->
<html xmlns:th="http://www.thymeleaf.org">
  <head>
    <th:block th:insert="fragments/head.html :: head"> </th:block>
  </head>
  <body>
    <th:block th:insert="fragments/navbar.html :: navbar"> </th:block>
    <div class="container">
      <th:block th:insert="fragments/sidebar.html :: sidebar"> </th:block>
      <main>
        <h1>Alojamentos para Limpeza</h1>
        
        <table class="table">
          <thead>
            <tr>
              <th>Identificação</th>
              <th>Estado</th>
              <th>Ação</th>
            </tr>
          </thead>
          <tbody>
            <tr th:each="alojamento : ${alojamentos}">
              <td th:text="${alojamento.identificacao}">01</td>
              <td>
                <span 
                  class="badge"
                  th:classappend="${alojamento.estadoLimpeza == 'PENDENTE' ? 'bg-warning' : 'bg-success'}"
                  th:text="${alojamento.estadoLimpeza}">
                  PENDENTE
                </span>
              </td>
              <td>
                <form th:if="${alojamento.estadoLimpeza == 'PENDENTE'}"
                      method="post"
                      th:action="@{/limpeza/marcar-concluido}">
                  <input type="hidden" name="alojamentoId" th:value="${alojamento.id}">
                  <button type="submit" class="btn btn-sm btn-success">
                    Marcar como Limpo
                  </button>
                </form>
              </td>
            </tr>
          </tbody>
        </table>
      </main>
    </div>
    <th:block th:insert="fragments/footer.html :: footer"> </th:block>
  </body>
</html>
```

---

## Testing

### Unit Test Example

```java
@SpringBootTest
class LimpezaControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired AlojamentoRepository alojamentoRepository;
    
    @Test
    @WithMockUser(username = "limpeza", roles = {"RESPONSAVEL_LIMPEZA"})
    void testListarLimpeza() throws Exception {
        mockMvc.perform(get("/limpeza"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("alojamentos"))
            .andExpect(view().name("limpeza/listar"));
    }
    
    @Test
    @WithMockUser(username = "limpeza", roles = {"RESPONSAVEL_LIMPEZA"})
    void testMarcarConcluido() throws Exception {
        // Setup: create test alojamento with PENDENTE state
        Alojamento alojamento = new Alojamento();
        alojamento.setIdentificacao("TEST-01");
        alojamento.setEstadoLimpeza(EstadoLimpeza.PENDENTE);
        alojamentoRepository.save(alojamento);
        
        // Action: POST /limpeza/marcar-concluido
        mockMvc.perform(post("/limpeza/marcar-concluido")
            .param("alojamentoId", alojamento.getId().toString())
            .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/limpeza"));
        
        // Assertion: state changed to CONCLUIDO
        Alojamento updated = alojamentoRepository.findById(alojamento.getId()).orElseThrow();
        assertEquals(EstadoLimpeza.CONCLUIDO, updated.getEstadoLimpeza());
    }
    
    @Test
    @WithMockUser(username = "cuidador", roles = {"CUIDADOR"})
    void testMarcarConcluidoAccessDenied() throws Exception {
        mockMvc.perform(post("/limpeza/marcar-concluido")
            .param("alojamentoId", "1")
            .with(csrf()))
            .andExpect(status().isForbidden());
    }
}
```

### Integration Test Example

```java
@SpringBootTest
class LimpezaIntegrationTest {
    @Autowired TestRestTemplate restTemplate;
    @Autowired AlojamentoRepository repository;
    
    @Test
    void testCleaningWorkflow() {
        // Step 1: Login as cleaning staff (via AuthController)
        // Step 2: GET /limpeza
        // Step 3: POST /limpeza/marcar-concluido
        // Step 4: Verify state machine transition
        // Step 5: Verify used in occupancy calculation (future phase)
    }
}
```

---

## Related Artifacts

- **User Story**: [US-20 — Consultar alojamentos a limpar](../../../../docs/Etapa1/01-user-stories/user-stories.md)
- **User Story**: [US-21 — Marcar alojamento como limpo](../../../../docs/Etapa1/01-user-stories/user-stories.md)
- **Domain Requirement**: [RD-01 — Disponibilidade de alojamento](../../../../docs/Etapa1/02-requirements/domain/RD-01.md)
- **Mockup**: [wf06-limpeza.html](../../../../docs/Etapa2/05-ui-interface-mockup/wf06-limpeza.html)
- **Data Model**: [data-model.md](../data-model.md) (EstadoLimpeza enum)

---

**Contract Status**: ✅ **Approved for implementation**
