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
- Recomendação: migrar para JPA-backed `UserDetailsService` (`Colaborador` entity + `ColaboradorRepository`), usar `PasswordEncoder` (BCrypt) e adicionar migrações Flyway para criar a tabela `colaborador` e semear apenas o utilizador inicial de direção.
- Decisão revista: não usar `ApplicationRunner`/`ColaboradorSeeder` com passwords em texto claro no código. A BD deve ser inicializada por migration Flyway com apenas um `DIRETOR` inicial e password armazenada como hash BCrypt.
- Os restantes colaboradores devem ser criados posteriormente pela aplicação, através da página de gestão de colaboradores acessível ao `DIRETOR`.
- Em ambiente real, a password inicial do `DIRETOR` deve ser alterada no primeiro acesso ou injetada por mecanismo de bootstrap externo ao repositório.

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
- Incluir matriz RBAC no spec (já adicionada a `spec.md`) e codificá-la com autorização por rotas no `SecurityConfig` e `@PreAuthorize` nos controllers sensíveis.
- A autorização em `SecurityConfig` com `requestMatchers(...).hasRole(...)` protege pedidos HTTP por padrão de URL. É adequada para expressar a matriz global de módulos, por exemplo: `/relatorios/**` e `/colaboradores/**` apenas para `DIRETOR`.
- A autorização com `@PreAuthorize` protege diretamente o método Java do controller ou serviço. É adequada para operações sensíveis porque mantém a regra junto da ação, mesmo que a rota seja alterada ou reutilizada no futuro.
- Decisão: usar as duas camadas nos fluxos de relatórios financeiros e gestão de colaboradores. O `SecurityConfig` centraliza a política por módulo e o `@PreAuthorize("hasRole('DIRETOR')")` nos controllers reforça as operações críticas.
- Para módulos menos sensíveis, a regra pode ficar apenas no `SecurityConfig` quando a autorização é simples e totalmente determinada pela rota.
- Adicionar testes de integração que validem a matriz RBAC para diretor, receção, cuidador, veterinário e limpeza.

## Auditoria
- Decisão revista em 2026-05-27: usar exclusivamente a auditoria persistente da aplicação, através de `AuditoriaOperacaoService` e `IAuditoriaService`/`AuditoriaService`, com eventos em `auditoria_evento`.
- O Spring Boot Actuator pode continuar disponível para monitorização (`health`/`info`), mas não expõe `auditevents`, não fornece `AuditEventRepository` e não é um mecanismo de auditoria funcional.
- Eventos de negócio relevantes, como `RELATORIO_GERADO`, `CRIAR_COLABORADOR`, `EDITAR_COLABORADOR` e `DESATIVAR_COLABORADOR`, devem ser registados pelo serviço próprio.
- `RELATORIO_GERADO` não altera uma entidade persistida; por isso usa `entidade="Relatorio"`, `acao="READ"` e `entityId=null`, preservando autor, momento, filtros e resultado.
- O conteúdo auditado deve evitar dados sensíveis: nunca incluir passwords, hashes ou campos financeiros linha a linha; incluir apenas identificadores, utilizador autenticado, tipo de operação e filtros gerais necessários à rastreabilidade.

## Próximos passos recomendados (prioridade)
1. Criar `data-model.md` com entidades e índices (prioridade alta).
2. Rever `contracts/contract.md` com as rotas MVC e templates principais (prioridade alta).
3. Implementar `Colaborador` entity e `ColaboradorRepository`, completar `JpaUserDetailsService` e criar seed Flyway apenas para o `DIRETOR` inicial.
4. Adicionar testes de integração de autenticação e RBAC; adicionar migration seed se desejado.
5. Criar perf tests skeleton e tasks para CI (ver `tasks.md` atualizadas).

## Referências
- `PatasBigodesApp/src/main/java/pt/hotel/animais/config/SecurityConfig.java`
- `specs/001-fundacao-hotel-animais/data-model.md` (modelos base)
- `specs/005-relatorios-colaboradores/spec.md` (este spec)
