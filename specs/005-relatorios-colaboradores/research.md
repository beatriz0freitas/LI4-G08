# Research: Relatórios e Colaboradores (Spec 005)

## Objectivo
Documentar decisões técnicas, alternativas e observações relevantes para a implementação de `specs/005-relatorios-colaboradores`.

## Contexto técnico existente
- Base: Java 21, Spring Boot 3.3.x (MVC + Thymeleaf)
- Segurança: actualmente `SecurityConfig` usa `InMemoryUserDetailsManager` (seed hardcoded). Planeado migrar para persistência (Phase 5).
- Persistência: Spring Data JPA + Hibernate; Flyway para migrações.

## Autenticação e migração de colaboradores
- Estado actual: utilizadores e passwords hardcoded em `PatasBigodesApp/src/main/java/pt/hotel/animais/config/SecurityConfig.java` (InMemoryUserDetailsManager).
- Risco: credenciais em memória dificultam gestão de utilizadores, rotação de passwords, e integração com UI de gestão de colaboradores.
- Recomendação: migrar para JPA-backed `UserDetailsService` (`Colaborador` entity + `ColaboradorRepository`), usar `PasswordEncoder` (BCrypt) e adicionar migração Flyway para criar a tabela `colaborador` e opcional seed SQL. Implementar `ApplicationRunner` se preferir seed dinâmico.

## Data model status (presença)
- Verificado: não existe `data-model.md` específico para o spec 005 na pasta; modelos relevantes estão em `specs/001-fundacao-hotel-animais/data-model.md` e outros specs.
- Recomendação: adicionar `specs/005-relatorios-colaboradores/data-model.md` contendo as entidades `Colaborador`, `Relatorio` (projeção/DTO), campos de `Pagamento` e `ServicoExtra` usados nos relatórios, índices recomendados para consultas por período e campos de data.

## API contracts (presença)
- Verificado: não existe `contracts/openapi.yaml` em `specs/005-relatorios-colaboradores`.
- Recomendação: criar `specs/005-relatorios-colaboradores/contracts/openapi.yaml` com as rotas principais: `/api/relatorios/generate`, `/api/relatorios/{id}/export/csv`, `/api/colaboradores` CRUD, `/api/indicadores/faturacao`, `/api/historico`.

## Performance & testes
- Metas reconciliadas: PT-1 (≤1s para 3 meses), PT-2 (≤5s para 12 meses) — adicionar testes de carga sintética (Gatling/JMeter skeleton) e integrar com CI ou job manual em `specs/.../tests/perf/`.

## Segurança e RBAC
- Incluir matriz RBAC no spec (já adicionada a `spec.md`) e codificá-la com `@PreAuthorize` nos controllers. Adicionar testes de integração `RelatoriosAuthIT` e `ClinicaAuthTest`.

## Próximos passos recomendados (prioridade)
1. Criar `data-model.md` com entidades e índices (prioridade alta).
2. Criar `contracts/openapi.yaml` (esqueleto) e anotar endpoints principais (prioridade alta).
3. Implementar `Colaborador` entity e `ColaboradorRepository`, completar `JpaUserDetailsService` (já esqueleto criado) e `ColaboradorSeeder` (feito).
4. Adicionar testes de integração de autenticação e RBAC; adicionar migration seed se desejado.
5. Criar perf tests skeleton e tasks para CI (ver `tasks.md` atualizadas).

## Referências
- `PatasBigodesApp/src/main/java/pt/hotel/animais/config/SecurityConfig.java`
- `specs/001-fundacao-hotel-animais/data-model.md` (modelos base)
- `specs/005-relatorios-colaboradores/spec.md` (este spec)
