# Research: Fundação do sistema — Decisões Técnicas

**Created**: 2026-05-04 | **Status**: Complete

## Overview

Esta fase de pesquisa confirma as escolhas técnicas e resolve qualquer ambiguidade para a Fase 1. Não existem marcadores "NEEDS CLARIFICATION" na especificação, mas documentamos as decisões-chave que informam a arquitetura.

---

## 1. Stack Técnico — Java 21 + Spring Boot 3.3.5

### Decisão
**Escolhido**: Java 21 LTS + Spring Boot 3.3.5 (Spring MVC + Thymeleaf)

### Rationale
- **Java 21 LTS**: Suporte a 2 anos de atualizações; features modernas (records, sealed classes, pattern matching); compatível com Spring Boot 3.x
- **Spring Boot 3.3.5**: Framework web mais maduro em Portugal; ecossistema rico (Security, Data JPA, Testing); comunidade ativa
- **Spring MVC + Thymeleaf**: SSR (Server-Side Rendering) apropriado para aplicação web tradicional; Thymeleaf -> templates nativos reutilizáveis
- **ADR-02** ([Spring MVC + Thymeleaf](../../../docs/Etapa2/04-architecture-decisions/ADR-02-spring-mvc-thymeleaf-ssr.md)) confirma esta escolha

### Alternativas Consideradas e Rejeitadas
| Alternativa | Razão Rejeitada |
|------------|-----------------|
| Spring Boot 2.7 (EOL) | Fora de suporte; migração necessária em breve |
| Quarkus | Ecosistema menor; menos recursos na equipa |
| Spring Cloud + microserviços | Prematura; escala de 5 roles e ~100 alojamentos não justifica |
| React/Vue SPA | Complexidade desnecessária; Thymeleaf é suficiente |

---

## 2. Armazenamento de Dados — MySQL 8

### Decisão
**Escolhido**: MySQL 8.0 para desenvolvimento, produção e testes de integração

### Rationale
- **MySQL 8.0**: ACID garantido; suporta transações; amplamente utilizado em Portugal
- **Testes sem persistência**: Mockito para isolar services/controllers quando a base de dados não é relevante
- **Testes com persistência**: MySQL para validar queries, transações e migrations no mesmo SGBD usado pela aplicação
- **Flyway para migrations**: Versionamento de schema; rastreabilidade; suporta múltiplos ambientes
- **ADR-03** ([Persistência em SGBD relacional](../../../docs/Etapa2/04-architecture-decisions/ADR-03-persistencia-sgbd-relacional.md)) e **ADR-04** ([MySQL e padrão repositório](../../../docs/Etapa2/04-architecture-decisions/ADR-04-mysql-base-dados.md)) confirmam esta escolha

### Alternativas Consideradas e Rejeitadas
| Alternativa | Razão Rejeitada |
|------------|-----------------|
| PostgreSQL | Excelente, mas MySQL é a escolha do projeto |
| NoSQL (MongoDB) | Modelo relacional é mais apropriado para reservas/ocupação |
| SQLite | Não suporta concorrência suficiente; inadequado para servidor |

---

## 3. Modelo de Sessão — HTTP Session com Spring Security 6

### Decisão
**Escolhido**: HTTP Session (cookie JSESSIONID) com BCrypt hash para senhas

### Rationale
- **HTTP Session**: Suportado nativamente por Spring Security; mantém estado no servidor (aceitável para fase 1)
- **BCrypt**: Hash seguro; implementação nativa em Spring Security 6; força configurável (12 rounds padrão)
- **Role-Based Access Control (RBAC)**: 5 tipos de colaborador (DIRETOR, FUNCIONARIO_RECEPCAO, CUIDADOR, MEDICO_VETERINARIO, RESPONSAVEL_LIMPEZA); permissões via @PreAuthorize
- **ADR-05** ([controlo de acesso por perfil](../../../docs/Etapa2/04-architecture-decisions/ADR-05-controlo-acesso-perfil.md)) confirma esta escolha

### Alternativas Consideradas e Rejeitadas
| Alternativa | Razão Rejeitada |
|------------|-----------------|
| JWT tokens | Stateless, mas requer refresh logic; mais complexidade desnecessária em Phase 1 |
| OAuth2 | Apropriado se integração com IdP externa; não no escopo |
| Multi-factor auth | Fora do escopo de Fase 1 (RNF não o requer) |

---

## 4. Padrão de Comunicação — DTO entre Controller e Service

### Decisão
**Escolhido**: DTO (Data Transfer Objects) entre Controller e Service; Entities não são expostas

### Rationale
- **Separação de Responsabilidades**: Validação de input (DTO) separada de lógica de negócio (Entity)
- **Segurança**: Evita exposição de campos internos; controlo granular de serialização
- **Testabilidade**: Controllers testáveis com DTOs mockados
- **ADR-06** ([isolamento da apresentação através de DTOs](../../../docs/Etapa2/04-architecture-decisions/ADR-06-isolamento-apresentacao-dtos.md)) confirma esta escolha

### Exemplo
```java
// Input DTO
record LoginRequestDTO(String username, String password) {}

// Controller (recebe DTO)
@PostMapping("/login")
ResponseEntity<?> login(@RequestBody LoginRequestDTO req) { ... }

// Service (trabalha com Entities)
public Colaborador authenticate(String username, String password) { ... }
```

---

## 5. Performance — RNF-01 (< 2 segundos)

### Decisão
**Objetivo**: Dashboard carrega em < 2 segundos

### Rationale
- **Índices no DB**: estadoLimpeza, tipoColaborador para queries rápidas
- **N+1 Query Prevention**: JPA Fetch strategies (JOIN FETCH) quando apropriado
- **Caching**: Dashboard pode usar @Cacheable para indicadores de ocupação (TTL: 30-60s)
- **Connection Pooling**: HikariCP (padrão Spring Boot) com pool size = 10-20

### Validação
- Teste de carga: simular 50 logins simultâneos
- Benchmark: medir tempo de GET /dashboard com dados realistas
- Monitoramento: logs de query time para identificar gargalos

---

## 6. Testabilidade — JUnit 5 + Mockito + TestContainers

### Decisão
**Escolhido**: 
- **Unit tests**: JUnit 5 + Mockito (mocks de services)
- **Integration tests**: TestContainers (MySQL real) + @SpringBootTest
- **Security tests**: MockMvc com autenticação @WithMockUser

### Rationale
- **JUnit 5**: Moderno; suporte a parâmetros e annotations avançadas
- **Mockito 5**: Mocks simples para testes rápidos; sem overhead de container
- **TestContainers**: Env realista; garante schema é criado corretamente
- **MockMvc**: Testa controllers sem cliente HTTP real; simples e confiável

### Estratégia de Cobertura
- **AuthController**: 90%+ (login, logout, redirect)
- **DashboardController**: 85%+ (ocupação, faturação)
- **LimpezaController**: 90%+ (listar, marcar concluído)
- **Services**: 95%+ (AlojamentoService, LimpezaService)

---

## 7. Estrutura de Código — Arquitetura em Camadas

### Decisão
**Escolhido**: MVC Layered (Presentation → Application → Domain → Data)

### Rationale
```
Presentation (Controllers)
    ↓ (DTOs)
Application (Services, Transactions)
    ↓ (Domain objects)
Domain (Entities, Business Logic, Enums)
    ↓
Data (Repositories, SQL)
```

- **Coesão**: cada camada tem uma responsabilidade clara
- **Testabilidade**: interfaces bem definidas permitem mocks
- **Manutenibilidade**: fácil de entender fluxo de dados

---

## 8. Interface do Utilizador — Thymeleaf com Fragments

### Decisão
**Escolhido**: Thymeleaf SSR com HTML Fragments reutilizáveis

### Rationale
- **Fragments**: navbar, sidebar, footer, head comuns a todas as páginas
- **Layouts**: base.html que estende → dashboard.html, limpeza.html etc.
- **Mockups**: templates seguem wf01-login.html, wf02-dashboard-diretor.html, wf06-limpeza.html
- **AdminLTE**: os templates devem usar componentes AdminLTE como base visual para cards, sidebars, navbars e estrutura de dashboard, preservando coerência com os mockups e reduzindo trabalho de styling manual

### Estrutura
```
templates/
├── fragments/
│   ├── head.html (< css, meta, title >)
│   ├── navbar.html (menu superior)
│   ├── sidebar.html (menu esquerdo)
│   ├── footer.html (rodapé)
├── auth/
│   └── login.html (formulário)
├── dashboard/
│   └── index.html (indicadores)
├── limpeza/
│   └── listar.html (estado de limpeza)
└── placeholders/
    └── modulo.html (pontos de entrada futuros)
```

---

## 9. Containerização — Docker + docker-compose

### Decisão
**Escolhido**: Dockerfile para imagem Spring Boot + docker-compose para local dev

### Rationale
- **Dockerfile**: Multi-stage build (build jar, depois lightweight image)
- **docker-compose**: MySQL + app em um único comando (`docker-compose up`)
- **Makefile**: Shortcuts para `make dev`, `make test`, `make build`

### Configuração
```yaml
# docker-compose.yml
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: hotel_animais
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/hotel_animais
```

---

## 10. Configuração — application.properties

### Decisão
**Perfis**:
- `application-mysql.properties` (MySQL)
- `src/test/resources/application.properties` (testes com MySQL)
- Padrão: MySQL

### Rationale
```properties
# application.properties (padrão: MySQL)
spring.profiles.active=mysql

# application-mysql.properties
spring.datasource.url=jdbc:mysql://localhost:3306/hotel_animais
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true

# src/test/resources/application.properties (testes com MySQL)
spring.datasource.url=jdbc:mysql://localhost:3307/hotelanimais
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.flyway.enabled=true
```

---

## 11. Segurança — Configuração do Spring Security

### Decisão
**SecurityConfig.java**:
- In-memory users com 5 roles em Phase 1 (persistir em Phase 5)
- BCrypt strength = 12 (padrão aceitável)
- CSRF protection habilitada
- HTTPS forçado em produção

### Rationale
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    http
        .csrf().and()
        .authorizeRequests()
            .antMatchers("/login").permitAll()
            .antMatchers("/dashboard/**").hasAnyRole("DIRETOR")
            .antMatchers("/limpeza/**").hasRole("RESPONSAVEL_LIMPEZA")
            .anyRequest().authenticated()
        .and()
        .formLogin().loginPage("/login");
    return http.build();
}
```

---

## Summary

Todas as decisões técnicas estão **alinhadas com ADRs** (Etapa 2) e **confirmadas pela especificação** (Etapa 1). Não existem bloqueadores técnicos; o projeto pode proceder para implementação.

**Status**: ✅ **Pronto para Phase 1 Design & Phase 2 Implementation**
