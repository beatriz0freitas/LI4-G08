# Research: Fase 4 — Operação diária, clínica e limpeza avançada

## 1. Stack técnica confirmada
- Decision: A feature será implementada em Java 21 com Spring Boot 3.3.5, Spring MVC, Thymeleaf, Spring Security, Spring Data JPA e Flyway.
- Rationale: Esta é a stack já usada pela aplicação e preserva a arquitetura atual de SSR, persistência relacional e controlo de acesso por perfil.
- Alternatives considered: Introduzir uma API separada ou frontend desacoplado. Rejeitado porque aumentaria a complexidade sem benefício claro para esta fase.

## 2. Persistência e migrações
- Decision: As novas capacidades serão modeladas com entidades JPA e suportadas por migrações Flyway.
- Rationale: O projeto já usa JPA/Flyway, o que facilita integridade, versionamento do esquema e validação em testes com H2.
- Alternatives considered: SQL manual ou criação de tabelas ad hoc. Rejeitado por reduzir rastreabilidade e aumentar o risco de divergência entre ambientes.

## 3. Modelo de domínio para saúde e operação diária
- Decision: A feature vai incluir `RegistoCuidado`, `ServicoExtra`, `IntervencaoClinica`, `Nota` e `AlteracaoEstadoSaude`.
- Rationale: O spec cobre cuidados diários, serviços extra, historial clínico, notas operacionais e alterações ao estado de saúde; a entidade de saúde fecha o requisito RF-13 sem depender de descrições informais.
- Alternatives considered: Registar alterações ao estado de saúde apenas como texto livre em `RegistoCuidado`. Rejeitado porque mistura semânticas diferentes e dificulta filtragem e consulta clínica.

## 4. Autorização e rastreabilidade
- Decision: Operações de escrita serão protegidas por Spring Security e associadas ao perfil correto; cada registo guardará autor e timestamp.
- Rationale: O domínio exige controlo de acesso por perfil, confidencialidade e auditoria.
- Alternatives considered: Deixar o controlo apenas na interface. Rejeitado por ser insuficiente para integridade e segurança.

## 5. Listagens e historial
- Decision: As consultas de historial e listas recentes usarão paginação e filtros por animal, estadia, tipo de registo e intervalo temporal.
- Rationale: O spec exige leitura consolidada e o RNF-01 pede resposta rápida em operações de leitura.
- Alternatives considered: Carregamento completo de toda a lista. Rejeitado por não escalar e por dificultar a navegação no histórico.

## 6. Contratos externos
- Decision: Não serão produzidos contratos externos nesta fase.
- Rationale: A feature é interna à aplicação e expõe fluxos web server-rendered, não uma API pública separada.
- Alternatives considered: Documentar endpoints REST formais. Rejeitado porque não é a interface principal desta feature.
