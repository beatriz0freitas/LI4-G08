# Research: Fase 4 — Operação diária, clínica e limpeza avançada

## Clarifications Applied (Session 2026-05-25, LAC-02)

As clarificações abaixo foram integradas na spec e impactam o modelo de domínio e o plano de implementação.

### Q1: Origem/Fonte do Plano de Cuidados
- **Decision**: O plano de cuidados é uma **combinação** de:
  - Histórico de tarefas recorrentes do animal (cuidados já realizados em estadias anteriores)
  - Instruções da reserva (notas operacionais adicionadas pela receção)
  - Ajustes manuais durante a estadia (novas tarefas ou instruções adicionadas pelo cuidador/veterinário)
- **Rationale**: Isto reflete a realidade operacional de um hotel de animais, onde cada animal tem rotinas conhecidas que se repetem, mas cada estadia pode ter instrução especiais ou mudanças.
- **Implicações técnicas**: Necessário criar entidade `PlanoCuidados` com vínculo duplo (Animal histórico + Estadia ativa).

### Q2: Plano Estático vs Dinâmico
- **Decision**: O plano é **dinâmico** — pode ser modificado a qualquer momento durante a estadia, com todas as alterações auditadas (autor, timestamp, data).
- **Rationale**: Permite adaptar os cuidados conforme mudanças no estado do animal (ex.: alergias, restrições veterinárias emergentes).
- **Implicações técnicas**: Necessário guardar histórico de alterações, auditoria completa, e suportar operações de update do plano sem perder a versão original.

### Q3: Granularidade do Plano
- **Decision**: Modelo **híbrido** — tarefas recorrentes **estruturadas** (ALIMENTACAO_MANHA, MEDICACAO_12H, PASSEIO, LIMPEZA, OUTRO) + campo de **notas/instruções adicionais** (texto livre).
- **Rationale**: Oferece melhor rastreabilidade (tarefas estruturadas = auditoria automática, relatórios, checklist clara) + flexibilidade (notas livres = realidade clínica não padronizável).
- **Implicações técnicas**: Criar entidade `TarefaCuidado` com enum de tipos; `PlanoCuidados` com campo `instrucoes` separado.

### Q4: Vínculo do Plano
- **Decision**: **Duplo vínculo**:
  - Animal mantém histórico **persistente** de planos (0..* PlanoCuidados por animal)
  - Cada Estadia herda/cria **cópia ajustável** do plano do animal (1..1 PlanoCuidados por Estadia, UNIQUE)
- **Rationale**: Rastreabilidade longitudinal do animal (médico veterinário vê histórico de cuidados) + independência operacional da estadia (ajustes não afetam outras estadias).
- **Implicações técnicas**: Constraint UNIQUE(estadiaId) em PlanoCuidados; índice em (animalId, dataInicio) para listagem histórica.

### Q5: Estados/Ciclos de Vida do Plano
- **Decision**: Plano com **priorização dinâmica** (ROTINA, URGENTE, CRITICO) que **muda conforme alterações de saúde**:
  - Quando `AlteracaoEstadoSaude.severidade = CRITICO` é criada, o plano correspondente muda para prioridade CRITICO
  - Sistema exibe flag visual ao cuidador (ex.: cor vermelha, ícone de alerta)
  - Encerra automaticamente pós-check-out
- **Rationale**: Reflete urgência clínica real; permite que cuidadores priorizem tarefas conforme saúde do animal; auditoria clara de mudanças de prioridade.
- **Implicações técnicas**: Campo `prioridade` em PlanoCuidados; hook automático em `AlteracaoEstadoSaudeService` que chama `IPlanoCuidadosService.atualizarPrioridade()`.

---

## 1. Stack técnica confirmada
- Decision: A feature será implementada em Java 21 com Spring Boot 3.3.5, Spring MVC, Thymeleaf, Spring Security, Spring Data JPA e Flyway.
- Rationale: Esta é a stack já usada pela aplicação e preserva a arquitetura atual de SSR, persistência relacional e controlo de acesso por perfil.
- Alternatives considered: Introduzir uma API separada ou frontend desacoplado. Rejeitado porque aumentaria a complexidade sem benefício claro para esta fase.

## 2. Persistência e migrações
- Decision: As novas capacidades serão modeladas com entidades JPA e suportadas por migrações Flyway.
- Rationale: O projeto já usa JPA/Flyway, o que facilita integridade, versionamento do esquema e validação em testes de integração com MySQL.
- Alternatives considered: SQL manual ou criação de tabelas ad hoc. Rejeitado por reduzir rastreabilidade e aumentar o risco de divergência entre ambientes.

## 3. Modelo de domínio para saúde e operação diária
- Decision: A feature vai incluir `PlanoCuidados` (NOVO), `TarefaCuidado` (NOVO), `RegistoCuidado`, `ServicoExtra`, `IntervencaoClinica`, `Nota` e `AlteracaoEstadoSaude`.
- Rationale: O spec cobre cuidados diários, serviços extra, historial clínico, notas operacionais e alterações ao estado de saúde. As duas novas entidades (`PlanoCuidados` e `TarefaCuidado`) materializam a dinâmica do plano descrita em RD-10.
- Alternatives considered: Registar instruções apenas em `Nota` e marcar tarefas apenas em `RegistoCuidado`. Rejeitado porque não modelaria a priorização dinâmica nem o vínculo duplo necessário.

## 4. Autorização e rastreabilidade
- Decision: Operações de escrita serão protegidas por Spring Security e associadas ao perfil correto; cada registo guardará autor e timestamp.
- Rationale: O domínio exige controlo de acesso por perfil, confidencialidade e auditoria; mudanças de prioridade do plano devem ser auditadas (ex.: quem registou alteração de saúde que mudou prioridade).
- Alternatives considered: Deixar o controlo apenas na interface. Rejeitado por ser insuficiente para integridade e segurança.

## 5. Listagens e historial
- Decision: As consultas de historial e listas recentes usarão paginação e filtros por animal, estadia, tipo de registo e intervalo temporal.
- Rationale: O spec exige leitura consolidada (US-22) e o RNF-01 pede resposta rápida em operações de leitura.
- Alternatives considered: Carregamento completo de toda a lista. Rejeitado por não escalar e por dificultar a navegação no histórico.

## 6. Integração de Priorização Automática
- Decision: Quando `AlteracaoEstadoSaudeService` cria um registo com severidade CRITICO/URGENTE, chamará automaticamente `IPlanoCuidadosService.atualizarPrioridade()` para atualizar o plano correspondente.
- Rationale: Reflecte a urgência clínica em tempo real; cuidador vê imediatamente que o plano foi escalado.
- Alternatives considered: Deixar a priorização manual (interface explícita). Rejeitado porque reduziria responsividade e propenso a esquecimento.

## 7. Contratos externos
- Decision: Não serão produzidos contratos externos nesta fase.
- Rationale: A feature é interna à aplicação e expõe fluxos web server-rendered, não uma API pública separada.
- Alternatives considered: Documentar endpoints REST formais. Rejeitado porque não é a interface principal desta feature.
