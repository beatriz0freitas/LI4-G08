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

## Contrato de interface MVC
- Verificado: a arquitetura da Etapa 2 define Spring MVC + Thymeleaf server-side.
- Recomendação: manter `specs/005-relatorios-colaboradores/contracts/contract.md` como contrato de páginas, formulários e downloads. As rotas principais são `/relatorios`, `/relatorios/gerar`, `/relatorios/exportar/csv`, `/relatorios/exportar/pdf`, `/colaboradores` e `/colaboradores/novo`.
- Decisão: documentar apenas rotas MVC, templates, formulários e downloads desta feature.

## Performance & testes
- Metas reconciliadas: PT-1 (≤1s para 3 meses), PT-2 (≤5s para 12 meses) — adicionar testes de carga sintética (Gatling/JMeter skeleton) e integrar com CI ou job manual em `specs/.../tests/perf/`.

## Segurança e RBAC
- Incluir matriz RBAC no spec (já adicionada a `spec.md`) e codificá-la com `@PreAuthorize` nos controllers. Adicionar testes de integração `RelatoriosAuthIT` e `ClinicaAuthTest`.

## Próximos passos recomendados (prioridade)
1. Criar `data-model.md` com entidades e índices (prioridade alta).
2. Rever `contracts/contract.md` com as rotas MVC e templates principais (prioridade alta).
3. Implementar `Colaborador` entity e `ColaboradorRepository`, completar `JpaUserDetailsService` (já esqueleto criado) e `ColaboradorSeeder` (feito).
4. Adicionar testes de integração de autenticação e RBAC; adicionar migration seed se desejado.
5. Criar perf tests skeleton e tasks para CI (ver `tasks.md` atualizadas).

## Referências
- `PatasBigodesApp/src/main/java/pt/hotel/animais/config/SecurityConfig.java`
- `specs/001-fundacao-hotel-animais/data-model.md` (modelos base)
- `specs/005-relatorios-colaboradores/spec.md` (este spec)
