# API Contract: Dashboard (Phase 1)

**Version**: 1.0 | **Status**: Specification | **Conformance**: [RF-01](../../../../docs/Etapa1/02-requirements/functional/RF-01.md), [US-01](../../../../docs/Etapa1/01-user-stories/user-stories.md), [US-02](../../../../docs/Etapa1/01-user-stories/user-stories.md)

---

## Overview

The Dashboard contract defines the director/manager operational view with occupancy and billing indicators. Phase 1 uses controller-populated values rendered with Thymeleaf.

---

## Endpoints

### GET /dashboard

**Purpose**: Display operational dashboard with key indicators for authenticated user

**Request**:
```http
GET /dashboard HTTP/1.1
Host: localhost:8080
Cookie: JSESSIONID=ABC123DEF456; Path=/
```

**Response** (200 OK):
```http
HTTP/1.1 200 OK
Content-Type: text/html;charset=UTF-8

<!DOCTYPE html>
<html lang="pt">
<head>
    <title>Dashboard — Hotel Animais</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
    <nav><!-- navbar fragment --></nav>
    <aside><!-- sidebar fragment --></aside>
    
    <main>
        <h1>Dashboard Operacional</h1>
        
        <section id="occupancy">
            <div class="card">
                <h3>Ocupação</h3>
                <p class="metric">4/10 (40%)</p>
                <p class="detail">Alojamentos ocupados / total disponível</p>
            </div>
        </section>
        
        <section id="billing">
            <div class="card">
                <h3>Faturação Diária</h3>
                <p class="metric">€ 240.00</p>
                <p class="detail">Pagamentos líquidos hoje</p>
            </div>
            <div class="card">
                <h3>Faturação Mensal</h3>
                <p class="metric">€ 5,280.00</p>
                <p class="detail">Pagamentos mês corrente</p>
            </div>
        </section>
        
        <section id="pending-payments">
            <div class="card">
                <h3>Pagamentos Pendentes</h3>
                <p class="metric">0</p>
                <p class="detail">Estadias sem pagamento</p>
            </div>
        </section>
    </main>
    
    <footer><!-- footer fragment --></footer>
</body>
</html>
```

**Response Headers**:
```
Cache-Control: no-store, no-cache, must-revalidate
Pragma: no-cache
Expires: 0
```

---

## Indicators

| Indicator | Source in Phase 1 | Unit | Notes |
|-----------|-------------------|------|-------|
| **Ocupação** | Controller value | % | Exibido no dashboard |
| **Faturação Diária** | Controller value | € | Exibido no dashboard |
| **Faturação Mensal** | Controller value | € | Exibido no dashboard |
| **Pagamentos Pendentes** | Controller value | Count | Exibido no dashboard |

---

## View Model

Os indicadores são passados do controller para a view via `Model` e renderizados em Thymeleaf. A implementação segue o padrão MVC monolítico desta fase, sem API JSON.

---

## Access Control

| Role | Access | View Variant |
|------|--------|-------------|
| DIRETOR | ✅ Full | All indicators visible |
| FUNCIONARIO_RECEPCAO | ⚠️ Limited | Occupancy + upcoming checkouts only |
| CUIDADOR | ❌ No | Access denied (403) |
| MEDICO_VETERINARIO | ❌ No | Access denied (403) |
| RESPONSAVEL_LIMPEZA | ❌ No | Access denied (403) |

**Authorization Check** (Phase 1):
```java
@GetMapping("/dashboard")
@PreAuthorize("hasAnyRole('DIRETOR', 'FUNCIONARIO_RECEPCAO')")
public String dashboard(Model model) {
    // Controller logic
    return "dashboard/index";
}
```

---

## Performance Specification (RNF-01)

**Requirement**: Dashboard must load in < 2 seconds (95th percentile)

**Measurement Points**:
```
Navigation: /dashboard (click)
    ↓ [Browser: Time to First Byte]
Load: GET /dashboard (server processing)
    ↓ [Server: Thymeleaf render]
Display: HTML received + displayed
    ↓ [Browser: onLoad event]
Total: < 2000 ms
```

**Benchmark (Phase 1 Expected)**:
- Server processing: ~200-400ms
- Template rendering: ~150-250ms
- Network latency: ~50-100ms (local Docker)
- **Total**: ~400-750ms (well under 2s goal)

---

## Error Handling

| Scenario | HTTP Status | Response |
|----------|------------|----------|
| Not authenticated (no JSESSIONID) | 302 | Redirect to /login |
| Session expired | 302 | Redirect to /login |
| Unauthorized role | 403 | "Access Denied" page |
| Indicator calculation failure | 500 | Display fallback "N/A", log error |

---

## Cache Strategy (Phase 1)

**Currently**: No caching (real-time controller values)

**Nota**: Estratégias de cache e agregação de dados futuros ficam documentadas em [future-expansions.md](../future-expansions.md).

---

## Related Templates

### Layout Structure (Thymeleaf)

```html
<!-- templates/dashboard/index.html -->
<html xmlns:th="http://www.thymeleaf.org">
  <head>
    <th:block th:insert="fragments/head.html :: head"> </th:block>
  </head>
  <body>
    <th:block th:insert="fragments/navbar.html :: navbar"> </th:block>
    <div class="container">
      <th:block th:insert="fragments/sidebar.html :: sidebar"> </th:block>
      <main>
        <!-- Dashboard indicators here -->
      </main>
    </div>
    <th:block th:insert="fragments/footer.html :: footer"> </th:block>
  </body>
</html>
```

### Bootstrap/AdminLTE Card Example

```html
<div class="card">
  <div class="card-header">
    <h5>Ocupação</h5>
  </div>
  <div class="card-body">
    <p class="metric" th:text="${occupancyPercentage}">
      4/10 (40%)
    </p>
    <p class="detail">Alojamentos ocupados / total</p>
  </div>
</div>
```

---

## Testing

### Unit Test Example

```java
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
class DashboardControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired WebApplicationContext context;
    
    @Test
    @WithMockUser(username = "diretor", roles = {"DIRETOR"})
    void testDashboardLoad() throws Exception {
        mockMvc.perform(get("/dashboard"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("occupancyPercentage", "dailyBilling"))
            .andExpect(view().name("dashboard/index"));
    }
    
    @Test
    @WithMockUser(username = "cuidador", roles = {"CUIDADOR"})
    void testDashboardAccessDenied() throws Exception {
        mockMvc.perform(get("/dashboard"))
            .andExpect(status().isForbidden());
    }
}
```

### Performance Test Example

```java
@SpringBootTest
class DashboardPerformanceTest {
    @Autowired TestRestTemplate restTemplate;
    
    @Test
    void testDashboardLoadTime() {
        long startTime = System.currentTimeMillis();
        
        ResponseEntity<String> response = restTemplate.getForEntity("/dashboard", String.class);
        
        long elapsedTime = System.currentTimeMillis() - startTime;
        
        assertEquals(200, response.getStatusCode().value());
        assertTrue(elapsedTime < 2000, "Dashboard must load in < 2s");
    }
}
```

---

## Related Artifacts

- **User Story**: [US-01 — Consultar disponibilidade](../../../../docs/Etapa1/01-user-stories/user-stories.md)
- **User Story**: [US-02 — Consultar indicadores de faturação](../../../../docs/Etapa1/01-user-stories/user-stories.md)
- **Requirement**: [RF-01 — Dashboard operacional](../../../../docs/Etapa1/02-requirements/functional/RF-01.md)
- **Non-Functional**: [RNF-01 — Performance < 2s](../../../../docs/Etapa1/02-requirements/non-functional/RNF-01.md)
- **Mockup**: [wf02-dashboard-diretor.html](../../../../docs/Etapa2/05-ui-interface-mockup/wf02-dashboard-diretor.html)
- **Data Model**: [data-model.md](../data-model.md)

---

**Contract Status**: ✅ **Approved for implementation**
