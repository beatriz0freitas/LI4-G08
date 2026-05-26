# Plano de Implementação — Relatórios e Colaboradores (Spec 005)

**Atualizado**: 2026-05-26 (incorpora LAC-13 — Auditoria incompleta para operações críticas)

## Visão Geral

Objetivo: implementar geração/exportação de relatórios operacionais, gestão de colaboradores e auditoria centralizada de operações críticas, conforme `spec.md` (incluindo FR-011, SC-008, SC-009 da sessão de clarificação LAC-13), respeitando a arquitetura Spring MVC + Thymeleaf descrita em `docs/Etapa2/01-architecture/architecture.md`.

## Entregáveis

- Controllers MVC que devolvem páginas Thymeleaf, redirecionamentos ou downloads de ficheiros.
- Vistas Thymeleaf de relatórios, gestão de colaboradores e consulta de auditoria.
- Serviços e repositórios para agregação de métricas, cálculo de ocupação, faturação, serviços extra e auditoria.
- Modelo persistente de `Colaborador` com `tipoColaborador` como enum.
- **Novo (LAC-13)**: Modelo persistente de `AuditoriaEvento` com estrutura relacional centralizada.
- **Novo (LAC-13)**: `AuditoriaService` e integração em serviços críticos de todas as specs (reservas, estadias, pagamentos, cuidados, clínica, limpeza, colaboradores).
- Documentação do código com Javadoc.
- Geração HTML via Maven Javadoc Plugin.
- Testes de controllers MVC, serviços e autorização por perfil.
- **Novo (LAC-13)**: Testes de auditoria para registo de eventos e consulta filtrada.
- Documentação técnica de rotas MVC, permissões e política de auditoria.

## Premissas

- A implementação segue controllers MVC, templates Thymeleaf, formulários e downloads server-side.
- Autenticação/autorização segue RNF-04 e a matriz `docs/Etapa2/06-role-permissions/permissoes.md`.
- Apenas o `DIRETOR` pode gerir colaboradores, consultar/exportar relatórios financeiros e aceder a auditoria.
- **Novo (LAC-13)**: Auditoria é centralizada em tabela `AuditoriaEvento`; retenção de 12 meses; acesso restrito a `DIRETOR`; operações críticas de todas as specs devem publicar eventos.
- **Novo (LAC-13)**: Integração de auditoria com serviços críticos de specs 003, 004 e 005 requer coordenação entre teams/branchs.
  - **Matriz de Responsabilidades**: **Specs 005 (Fases 8-9, T039-T058)** implementa `AuditoriaEvento`, `AuditoriaService`, `AuditoriaController`, integra com `ColaboradorService`. **Specs 005 → Specs 003, 004 (Fase 10, T059-T068)** fornece interface pública em `docs/auditoria-interface.md` (T048); coordena com teams de specs 003, 004 para integração. **Specs 003, 004** integram chamadas a `AuditoriaService` em serviços críticos; asseguram que eventos são auditados conforme interface. **Blocker**: T048 (auditoria-interface.md) publicado antes T049; Phase 9 paralela; Phase 10 coordenação após T048.
- Controllers, services, DTOs e exceptions devem ter Javadoc suficiente para explicar responsabilidades públicas e regras de negócio relevantes.

## Ordem de Implementação

1. **Fundação de auditoria** *(NOVO - LAC-13, crítico)*
   - Criar entidade JPA `AuditoriaEvento` com campos: `id`, `timestamp`, `utilizadorId` (FK), `operacao`, `entidade`, `entityId`, `acao`, `detalhes` (JSON), `resultado`, `motivoFalha`.
   - Implementar `AuditoriaRepository` com queries para filtro por data, utilizador, operação.
   - Implementar `AuditoriaService` com métodos: `registarEvento()`, `consultarPorPeriodo()`, `consultarPorUtilizador()`, `limparzardosAntigos()`.
   - Criar migração Flyway `V006__create_auditoria_evento.sql`.

2. **Integração de auditoria com serviços críticos** *(NOVO - LAC-13, coordenação)*
   - Adicionar chamadas a `AuditoriaService.registarEvento()` em:
     - `ReservaService`: criar, editar, cancelar (specs 003)
     - `EstadiaService`: check-in, check-out (spec 003)
     - `PagamentoService`: criar, atualizar pagamento (spec 003)
     - `CuidadosService`: registar cuidado (spec 004)
     - `IntervenaoClinicaService`: registar intervenção (spec 004)
     - `ServicoExtraService`: criar serviço (spec 004)
     - `LimpezaService`: marcar alojamento como limpo (spec 004)
     - `ColaboradorService`: criar, editar, desativar colaborador (spec 005)
   - **Nota**: Requer coordenação com branches de specs 003 e 004; pode ser feito em PR de consolidação.

3. **Contrato de interface MVC** *(prioridade alta)*
   - Documentar rotas de página, templates, formulários, query parameters e downloads em `contracts/contract.md`.
   - Incluir rota nova de auditoria: `GET /auditoria` com filtros por data, utilizador, operação.
   - Validar rotas contra `architecture.md` e `permissoes.md`.

4. **Fundação de segurança e colaboradores**
   - Implementar `TipoColaborador` como enum, se ainda não existir na aplicação.
   - Implementar `Colaborador`, `ColaboradorRepository`, `IColaboradorService` e `ColaboradorService`.
   - Migrar autenticação de utilizadores em memória para colaboradores persistidos quando a equipa avançar para Etapa 3.

5. **Gestão de colaboradores**
   - Implementar `ColaboradorController` com rotas MVC de listagem, registo, edição e desativação.
   - Integrar `AuditoriaService` em `ColaboradorController` para auditar operações.
   - Criar templates `colaboradores/list.html` e `colaboradores/form.html`.
   - Garantir que o formulário de registo é acessível apenas ao `DIRETOR`.

6. **Relatórios**
   - Implementar `IRelatorioService`/`RelatorioService` com agregações de ocupação, estadias, reservas, pagamentos e serviços extra.
   - Implementar `RelatorioController` com filtros por período e exportações CSV/PDF.
   - Criar templates `relatorios/list.html` ou integrar no dashboard do diretor.

7. **Consulta de auditoria** *(NOVO - LAC-13)*
   - Implementar `AuditoriaController` com rota `GET /auditoria` acessível apenas a `DIRETOR`.
   - Implementar filtros por data (dataInicio, dataFim), utilizador, operação, entidade.
   - Criar template `auditoria/list.html` com tabela paginada de eventos.
   - Adicionar opção de exportação simples (CSV) dos eventos filtrados.

8. **Testes e QA**
   - **Novos testes de auditoria**:
     - `AuditoriaServiceTest`: registo de eventos, limpeza de dados antigos (12 meses).
     - `AuditoriaControllerTest`: acesso restrito a `DIRETOR`, filtros, paginação.
     - `AuditoriaIntegrationTest`: verificar que operações críticas geram eventos em `AuditoriaEvento`.
   - Testes de serviços para agregações e validação de colaboradores.
   - Testes MVC para renderização de páginas, submissão de formulários e downloads.
   - Testes de autorização para confirmar a matriz de permissões (incluindo auditoria).

9. **Documentação e entrega**
   - Atualizar `quickstart.md` com passos de navegação: geração de relatórios, gestão de colaboradores e consulta de auditoria.
   - Gerar Javadoc HTML com Maven Javadoc Plugin.
   - Documentar política de retenção de auditoria em `quickstart.md` ou ficheiro técnico de operações.
   - Rever `spec.md`, `tasks.md` e `permissoes.md` antes da entrega.

## Tarefas Técnicas Chave

### Relatórios
- MVC: `GET /relatorios` para página de filtros e resultados.
- MVC: `POST /relatorios/gerar` para gerar relatório e regressar à página.
- Export: `GET /relatorios/exportar/csv` e `GET /relatorios/exportar/pdf`.

### Colaboradores
- MVC: `GET /colaboradores`, `GET /colaboradores/novo`, `POST /colaboradores`, `GET /colaboradores/{id}/editar`, `POST /colaboradores/{id}`, `POST /colaboradores/{id}/desativar`.

### Auditoria *(NOVO - LAC-13)*
- MVC: `GET /auditoria` com query params: `dataInicio` (date), `dataFim` (date), `utilizadorId` (long), `operacao` (string), `entidade` (string).
- MVC: `GET /auditoria/exportar/csv` para exportar eventos filtrados.
- Service: `AuditoriaService.registarEvento(utilizadorId, operacao, entidade, entityId, acao, detalhes, resultado, motivoFalha)`.
- Service: `AuditoriaService.consultarPorPeriodo(dataInicio, dataFim, filtrosOpcionais)` com paginação.
- Service: `AuditoriaService.limparzardosAntigos(anosRetencao)` executada periodicamente (ex: batch job).

### Segurança (geral)
- Aplicar `hasRole('DIRETOR')` nas rotas de relatórios financeiros, colaboradores e auditoria.

### Dados
- Validar `tipoColaborador` como enum `TipoColaborador`.
- Estrutura `AuditoriaEvento.detalhes` armazenada como JSON; usar `@Convert(converter = JsonConverter.class)` ou similar.

## Critérios de Aceitação

### Relatórios & Colaboradores (existentes)
- Relatório padrão é gerado e mostrado na interface com métricas definidas em `spec.md`.
- CSV/PDF são descarregados a partir da interface e preservam os filtros aplicados.
- Gestão de colaboradores suporta criação, edição e desativação pelo `DIRETOR`.
- Lista de colaboradores reflete alterações imediatamente.

### Auditoria *(NOVO - LAC-13)*
- **SC-008**: Cada operação crítica (criar/editar/cancelar reserva, check-in, check-out, pagamento, cuidados, clínica, limpeza, colaborador) registada com sucesso cria um evento em `AuditoriaEvento` com utilizador, operação, entidade, entityId, acao e resultado.
- **SC-009**: O `DIRETOR` consegue consultar a auditoria filtrada por data, utilizador autenticado e operação, com resultados retendo 12 meses históricos.
- Eventos malformados ou com utilizador nulo não são aceites; lançam exceção ou são registados com resultado=FALHA.
- Consulta de auditoria responde em tempo real para períodos até 1 mês; períodos maiores podem usar paginação.

### Testes & Documentação (geral)
- A documentação HTML de código é gerada pelo Maven sem erros bloqueantes.
- Testes automatizados cobrem agregações, validações de formulário, regras de permissão e auditoria.
- Cobertura de testes em `AuditoriaService` e `AuditoriaController` >= 80%.

## Riscos e Mitigação

### Riscos Existentes
- **Risco**: agregações lentas em períodos grandes. **Mitigação**: índices por datas e fallback com estado de processamento na página.
- **Risco**: permissões divergentes entre controller e navegação. **Mitigação**: matriz única em `permissoes.md` e testes por perfil.
- **Risco**: enum de colaborador tratado como texto livre no formulário. **Mitigação**: popular select a partir de `TipoColaborador.values()` e validar no DTO.

### Riscos Novos (LAC-13 - Auditoria)
- **Risco**: Integração de auditoria com specs 003 e 004 requer coordenação complexa entre múltiplas branchs/equipas. **Mitigação**: 
  - Definir interface padrão de `AuditoriaService` na spec 005.
  - Publicar mock/stub de `AuditoriaService` em `PatasBigodesApp` para permitir que specs 003 e 004 integrem sem aguardar implementação completa.
  - Criar testes de integração end-to-end após consolidação de todas as specs.
  
- **Risco**: Tabela `AuditoriaEvento` cresce indefinidamente. **Mitigação**:
  - Implementar política de retenção de 12 meses em `AuditoriaService.limparzardosAntigos()`.
  - Executar via Spring Scheduler em período de baixa carga (ex: 3h00 diárias).
  - Criar índice em `AuditoriaEvento.timestamp` para otimizar queries de limpeza.

- **Risco**: Campo `detalhes` (JSON) cresce sem limite. **Mitigação**:
  - Documentar schema JSON esperado em comentário ou enum.
  - Limitar tamanho da string a 2000 caracteres no DTO.
  - Não armazenar estruturas muito complexas; usar apenas mudanças delta.

- **Risco**: Inconsistência de `utilizadorId` quando auditando operações sem autenticação. **Mitigação**:
  - Exigir `@PreAuthorize` em todos os serviços que registam auditoria.
  - Se falhar autenticação, lançar exceção antes de chamar `AuditoriaService.registarEvento()`.

---

## Recursos Externos & Sincronização

### Dependências entre Specs
- **Spec 005 → Spec 003**: Integração de auditoria em `ReservaService`, `EstadiaService`, `PagamentoService`.
- **Spec 005 → Spec 004**: Integração de auditoria em `CuidadosService`, `IntervenaoClinicaService`, `ServicoExtraService`, `LimpezaService`.
- **Ação**: Criar um documento de interface em `PatasBigodesApp/docs/auditoria-interface.md` com assinatura esperada de `AuditoriaService` para referência pelos autores de specs 003 e 004.

### Dependências Técnicas
- Maven: plugin Javadoc configurado em `pom.xml`.
- Spring Boot: versão com suporte a `@EventListener` e `AuditorApplicationEvent` (se usar Spring Boot Actuator; alternativa: publicador manual).
- JPA/Hibernate: converter JSON ou usar biblioteca como Jackson para `detalhes`.
- BD: suporte a tipo JSON (PostgreSQL `jsonb`, MySQL `json`, H2 `json`).

---

## Priorização de Tarefas

### Fase 1 (Fundação - semanas 1-2)
1. Criar `AuditoriaEvento` e migração Flyway.
2. Implementar `AuditoriaRepository` e `AuditoriaService`.
3. Publicar interface em `docs/auditoria-interface.md`.

### Fase 2 (Integração com Spec 005 - semanas 3-4)
4. Implementar `ColaboradorService`, `ColaboradorController`, templates.
5. Integrar `AuditoriaService` em `ColaboradorService`.
6. Implementar `AuditoriaController` e template `auditoria/list.html`.

### Fase 3 (Integração com Specs 003, 004 - semanas 5-6)
7. Coordenar com autores de specs 003 e 004 para integração de auditoria.
8. Criar testes de integração end-to-end.

### Fase 4 (Relatórios, Testes, QA - semanas 7-8)
9. Implementar `RelatorioService`, `RelatorioController`, templates, exportações.
10. Completar suite de testes (AuditoriaServiceTest, AuditoriaControllerTest, RelatorioControllerTest, etc.).
11. Documentação final e entrega.
