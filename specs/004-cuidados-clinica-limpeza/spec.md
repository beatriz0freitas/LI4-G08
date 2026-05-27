# Spec: Fase 4 — Operação diária, clínica e limpeza avançada

**Short name**: cuidados-clinica-limpeza
**Feature directory**: specs/004-cuidados-clinica-limpeza
**Criado**: 2026-05-18
**Status**: Implementado e validado em Etapa 4

## Resumo
Completar a rastreabilidade da operação diária e do acompanhamento clínico durante estadias: registo de cuidados diários, notas operacionais em reservas, serviços extra (banho, passeio, outro) e intervenções clínicas com histórico. Esta especificação descreve cenários de utilizador, requisitos funcionais testáveis, entidades de domínio, critérios de sucesso e dependências com a documentação existente (Etapa 1 e Etapa 2).

## Clarifications

### Session 2026-05-25

- **Q: Qual é a origem/fonte do plano de cuidados?** → **A:** Combinação: histórico do animal + instruções da reserva (US-17) + ajustes manuais na estadia.
- **Q: Plano estático ou dinâmico durante a estadia?** → **A:** Dinâmico; pode ser modificado a qualquer momento, com histórico de alterações mantido para auditoria.
- **Q: Granularidade do plano (tarefas estruturadas vs instruções livres)?** → **A:** Híbrido: tarefas recorrentes estruturadas (ex.: ALIMENTACAO_MANHA, MEDICACAO_12H) + campo de notas/instruções adicionais.
- **Q: Plano vinculado à Estadia ou ao Animal?** → **A:** Duplo vínculo: animal mantém histórico persistente de cuidados recorrentes; cada estadia herda/cria cópia ajustável do plano do animal.
- **Q: Estados/ciclos de vida do plano durante a estadia?** → **A:** Plano com priorização (ROTINA, URGENTE, CRÍTICO) que muda conforme alterações de saúde (US-16); encerra automaticamente pós-check-out.

### Session 2026-05-26

- **Q: Em que condições uma intervenção clínica pode ser registada?** → **A:** Apenas durante uma estadia ativa, com veterinário responsável obrigatório e custo não negativo.
- **Q: Registos de cuidado e serviços extra seguem as mesmas validações base?** → **A:** Sim. Só podem ser registados durante uma estadia ativa; o `RegistoCuidado` exige autor autenticado autorizado e o `ServicoExtra` exige também custo não negativo.
- **Q: Que filtros devem ser suportados no histórico consolidado, e como se combinam?** → **A:** Animal, cliente, estadia, intervalo de datas e tipo de evento; combinar todos com AND.

## Mapeamento a artefactos existentes
- Use Cases: [UC-09 - Registar Cuidados Diarios](docs/Etapa1/03-use-cases/UC-09.md#L1), [UC-10 - Registar Servico Extra](docs/Etapa1/03-use-cases/UC-10.md#L1), [UC-11 - Gerir Historial Clinico](docs/Etapa1/03-use-cases/UC-11.md#L1)
- User Stories: US-14, US-15, US-16, US-17, US-18, US-22, US-23 (ver rastreabilidade em [docs/Etapa1/01-user-stories/user-stories.md](docs/Etapa1/01-user-stories/user-stories.md#L1))
- Requisitos não-funcionais relevantes: [RNF-02](docs/Etapa1/02-requirements/non-functional/RNF-02.md#L1), [RNF-09](docs/Etapa1/02-requirements/non-functional/RNF-09.md#L1)
- Arquitetura / decisões: [ADR-01 Monolito Camadas](../../docs/Etapa2/04-architecture-decisions/ADR-01-monolito-camadas.md#L1), [ADR-02 Spring MVC + Thymeleaf SSR](../../docs/Etapa2/04-architecture-decisions/ADR-02-spring-mvc-thymeleaf-ssr.md#L1), [ADR-06 DTOs na apresentação](../../docs/Etapa2/04-architecture-decisions/ADR-06-isolamento-apresentacao-dtos.md#L1)
- Componentes e diagramas: [components.mmd](docs/Etapa2/01-architecture/components.mmd#L1), fluxos de sequência relevantes em [docs/Etapa2/03-seq-diagrams/UC-09.mmd](docs/Etapa2/03-seq-diagrams/UC-09.mmd#L1), [UC-10.mmd](docs/Etapa2/03-seq-diagrams/UC-10.mmd#L1), [UC-11.mmd](docs/Etapa2/03-seq-diagrams/UC-11.mmd#L1)
- Mockups UI: [docs/Etapa2/05-ui-interface-mockup/](../../../docs/Etapa2/05-ui-interface-mockup/)
- [wf04-plano-cuidados.html](../../docs/Etapa2/05-ui-interface-mockup/wf04-plano-cuidados.html)
- [wf05-historico-clinico.html](../../docs/Etapa2/05-ui-interface-mockup/wf05-historico-clinico.html)
- [wf06-limpeza.html](../../docs/Etapa2/05-ui-interface-mockup/wf06-limpeza.html)
- [wf09-historial.html](../../docs/Etapa2/05-ui-interface-mockup/wf09-historial.html)


## User Scenarios & Testing (Prioritizados)
Cada cenário inclui um teste de aceitação independente e executável.

### US-14 - Consultar plano de cuidados (Prioridade: P1)
Descrição: Cuidador consulta o plano de cuidados do animal em estadia, incluindo tarefas recorrentes estruturadas e instruções específicas, com prioridade atual (ROTINA/URGENTE/CRITICO).
Porque P1: Permite ao cuidador conhecer as rotinas, cumprir instruções clínicas/operacionais e adaptar-se a mudanças de saúde.
Independent Test: Abrir a vista do plano de cuidados para uma estadia activa e verificar que: (1) tarefas recorrentes aparecem conforme agendadas; (2) instruções da reserva/animal são visíveis; (3) prioridade reflete o estado de saúde atual.
Acceptance Scenarios:
1. Dado um animal em estadia activa com cuidados no histórico, quando o cuidador abre a vista "Plano de Cuidados", então o sistema devolve tarefas estruturadas (ex.: ALIMENTACAO_MANHA, MEDICACAO_12H) com checklist de conclusão, + instruções livres registadas.
2. Quando o veterinário regista uma alteração de saúde CRITICA (US-16), a prioridade do plano muda para CRITICO; cuidador vê flag visual e instrução de aviso.
3. Cuidador pode adicionar notas adicionais ao plano durante a estadia sem perder instruções originais; histórico de alterações é auditado.

### US-15 - Registar cuidado diário (Prioridade: P1)
Descrição: Funcionário/cuidador regista um cuidado diário para um animal em estadia (ex.: alimentação, medicação, passeio), com descrição livre, timestamp e autor.
Porque P1: Registo diário é essencial para segurança e continuidade clínica.
Independent Test: Criar `RegistoCuidado` associado a `Estadia` e verificar visibilidade na ficha do animal e no histórico de estadia.
Acceptance Scenarios:
1. Dado um animal em estadia activa, quando o cuidador submete um formulário de cuidado com "descricao" e "dataHora", então o sistema cria um `RegistoCuidado` ligado à `Estadia` e o retorna com id gerado.
2. Dado um cuidado registado, quando um director visualiza a estadia, então o registo aparece ordenado por `dataHora` descendente.
3. Quando o `RegistoCuidado` é submetido fora de uma estadia ativa, então a operação é rejeitada.

### US-18 - Registar serviço extra (Prioridade: P1)
Descrição: Recepção ou cuidador regista um `ServicoExtra` (BANHO, PASSEIO, OUTRO) durante a estadia incluindo custo e momento.
Porque P1: Serviços extra impactam faturação e devem ser registados em tempo real.
Independent Test: Criar `ServicoExtra` e validar impacto no resumo financeiro da estadia e no detalhe de pagamentos.
Acceptance Scenarios:
1. Dado uma estadia em curso, quando se regista um `ServicoExtra` com `tipo` e `custo`, então o serviço é persistido e aparece na secção "extras" da estadia.
2. Após check-out, a soma de `ServicoExtra` é incluída no cálculo final do pagamento.
3. Quando o serviço extra é registado fora de uma estadia ativa ou com custo negativo, então a operação é rejeitada.

### US-23 - Registar intervenção clínica (Prioridade: P2)
Descrição: Médico veterinário regista uma `IntervencaoClinica` com descrição, custo e nota clínica associada à estadia/animal.
Porque P2: Importante para historial médico, menos frequente que cuidados diários mas crítico para segurança.
Independent Test: Persistir `IntervencaoClinica` e verificar que aparece no historial clínico acessível por `UC-11`.
Acceptance Scenarios:
1. Dado um animal em estadia ativa, quando um veterinário autenticado regista uma intervenção com descrição, custo válido e data/hora, então o sistema grava autor e timestamp e associa a intervenção à estadia em curso.
2. Quando um utilizador sem perfil `VETERINARIO` tenta registar uma intervenção clínica, então a operação é recusada.
3. Quando o custo é negativo ou a estadia não está ativa, então a intervenção não é criada.

### US-17 - Notas operacionais em reserva (Prioridade: P2)
Descrição: Recepção adiciona `Nota` à `Reserva` (instruções especiais, alergias, observações).
Independent Test: Adicionar `Nota` a uma reserva e verificar visibilidade durante check-in e nas operações de cuidado.
Mapped to: RF-05 (histórico de operações/consultas).

### US-22 - Consulta consolidada do historial clínico e operacional (Prioridade: P1)
Descrição: Veterinário ou director consulta todo o historial (Registos de cuidado, Intervencoes, ServicosExtra, Notas, reservas, estadias e pagamentos quando aplicável) filtrável por animal, cliente, estadia, intervalo de datas e tipo de evento.
Independent Test: Endpoint de consulta devolve lista filtrada por `animalId`, `clienteId`, `estadiaId`, `dataInicio`/`dataFim` e `tipoEvento`, aplicando todos os filtros ativos com AND.
Acceptance Scenarios:
1. Selecção por `animalId` e intervalo devolve apenas registos desse animal nesse período.
2. Selecção por `clienteId` e `tipoEvento` devolve apenas eventos desse cliente desse tipo.
3. Quando vários filtros são fornecidos, todos têm de coincidir para o registo ser devolvido.

### US-16 - Registar alterações ao estado de saúde (Priority: P2)
Descrição: Funcionário/cuidador ou veterinário regista uma alteração do estado de saúde do animal durante a estadia, com descrição e severidade.
Porque P2: Registar alterações de saúde é crítico para acompanhamento clínico e ações de triagem.
Independent Test: Criar `AlteracaoEstadoSaude` associada a uma `Estadia` e verificar que aparece no historial clínico e na vista recente do animal.
Acceptance Scenarios:
1. Dado um animal em estadia activa, quando o cuidador regista uma alteração com `descricao` e `severidade`, então o registo é persistido e aparece na timeline clínica.
2. Uma alteração de severidade `CRITICO` dispara sinalização adicional (ex.: flag visual na interface) e é visível ao `VETERINARIO`.

### Edge Cases
- Tentativa de registar um `RegistoCuidado` para uma estadia terminada deve ser rejeitada (400 Bad Request).
- Tentativa de registar um `RegistoCuidado` fora de uma estadia ativa deve ser rejeitada (400 Bad Request).
- Serviços extra com custo negativo devem ser rejeitados.
- Serviços extra fora de uma estadia ativa devem ser rejeitados.
- Intervenções clínicas requerem autorização de perfil `VETERINARIO`.
- Intervenções clínicas fora de uma estadia ativa devem ser rejeitadas.
- Intervenções clínicas com custo negativo devem ser rejeitadas.

## Requirements
Todos os requisitos abaixo usam os identificadores canónicos do repositório.

### Requisitos Funcionais
- **RF-11**: O sistema deve disponibilizar o plano de cuidados de cada animal em estadia, consultável por qualquer cuidador.
- **RF-12**: O sistema deve permitir o registo de cada cuidado prestado a um animal em estadia, incluindo tipo de cuidado, data e hora, identificação do cuidador responsável e observações adicionais. O registo só é permitido durante uma estadia ativa.
- **RF-13**: O sistema deve permitir o registo de alterações ao estado de saúde de cada animal e disponibilizar uma lista dos animais com alterações recentes, consultável pelo médico veterinário.
- **RF-14**: O sistema deve permitir ao médico veterinário consultar o historial clínico de cada animal e registar intervenções, prescrições e o custo associado. O registo de intervenções clínicas só é permitido durante uma estadia ativa, com validação do perfil `VETERINARIO` e custo não negativo.
- **RF-17**: O sistema deve permitir registar um serviço extra com custo durante a estadia e associá-lo automaticamente à reserva em curso, para inclusão na faturação do check-out. O registo só é permitido durante uma estadia ativa e com custo não negativo.
- **RF-05**: O sistema deve manter o histórico de estadias e pagamentos consultável, para suportar a consulta consolidada do historial operacional e financeiro.
- **RF-05**: O sistema deve manter o histórico de estadias e pagamentos consultável, para suportar a consulta consolidada do historial operacional e financeiro, com filtros por animal, cliente, estadia, intervalo de datas e tipo de evento, combinados por AND.

### Requisitos Não-Funcionais
- **RNF-01**: O sistema deve garantir tempo de resposta inferior a 2 segundos para operações de leitura relevantes, incluindo a consulta de históricos e listas de registos.
- **RNF-04**: O acesso às operações de escrita deve exigir autenticação prévia e controlo de permissões por perfil de utilizador.
- **RNF-05**: O sistema deve garantir a confidencialidade dos dados clínicos dos animais e dos dados pessoais associados.

### Requisitos de Domínio
- **RD-04**: O pagamento no check-in cobre exclusivamente o valor da estadia; os serviços extra e as intervenções veterinárias são cobrados no check-out.
- **RD-09**: O custo de um serviço extra ou de uma intervenção veterinária deve ser registado no momento da sua ocorrência e associado à reserva em curso, não podendo ser alterado após o check-out. O `RegistoCuidado` e o `ServicoExtra` só podem ser criados durante uma estadia ativa. A intervenção veterinária só pode ser criada enquanto existir uma estadia ativa e por um utilizador com perfil `VETERINARIO`.
- **RD-10**: O plano de cuidados é originário da combinação: histórico de cuidados recorrentes do animal + instruções da reserva (notas US-17) + ajustes manuais durante a estadia. O plano é dinâmico e pode ser modificado, com todas as alterações auditadas (autor, timestamp). A prioridade do plano (ROTINA/URGENTE/CRITICO) muda conforme alterações de saúde registadas (US-16). O plano encerra automaticamente no check-out.

## Key Entities (Resumo de domínio)

### Plano de Cuidados (Nova entidade)
- `PlanoCuidados` (id, animalId, estadiaId, dataInicio, dataFim, prioridade: Enum {ROTINA, URGENTE, CRITICO}, ativo: Boolean)
  - Contém lista de `TarefaCuidado` (tarefas recorrentes estruturadas)
  - Contém campo `instrucoes` (texto livre para notas especiais)
  - Vínculo duplo: animal mantém histórico; estadia cria cópia ajustável
  - Dinâmico: pode ser modificado durante estadia; alterações auditadas

### Tarefa de Cuidado (Entidade secundária)
- `TarefaCuidado` (id, planoCuidadosId, tipo: String {ALIMENTACAO_MANHA, ALIMENTACAO_TARDE, MEDICACAO_12H, PASSEIO, LIMPEZA, OUTRO}, descricao, periodicidade: Enum {UNICA, DIARIA, SEMANAL}, dataHora, concluida: Boolean, autorConclusao: UUID)

### Outras entidades (já documentadas)
- `RegistoCuidado` (id, estadiaId, descricao, dataHora, autorId) — anotações livres sobre cuidados realizados; apenas para estadia ativa
- `ServicoExtra` (id, estadiaId, tipo: Enum {BANHO, PASSEIO, OUTRO}, custo, dataHora, autorId) — apenas para estadia ativa e com custo não negativo
- `IntervencaoClinica` (id, estadiaId, descricao, custo, dataHora, medicoId) — apenas para estadia ativa, com custo não negativo e autor com perfil `VETERINARIO`
- `Nota` (id, reservaId, descricao, autorId, dataHora)
- `AlteracaoEstadoSaude` (id, estadiaId, descricao, severidade: Enum {ROTINA, URGENTE, CRITICO}, dataHora, autorId) — dispara mudança de prioridade do plano

### Relações
- `Animal` 1..* `PlanoCuidados` (histórico)
- `Estadia` 1..1 `PlanoCuidados` (cópia ativa)
- `PlanoCuidados` 1..* `TarefaCuidado`
- `Estadia` 1..* `RegistoCuidado`
- `Estadia` 1..* `ServicoExtra`
- `Estadia` 1..* `IntervencaoClinica`
- `Reserva` 1..* `Nota`
- `Estadia` 1..* `AlteracaoEstadoSaude`

## Success Criteria (Mensuráveis)
- **SC-001**: Funcionalidade básica (criar/listar Registos de cuidado) implementada e testada com cobertura de integração (end-to-end) — 100% dos cenários P1 passam.
- **SC-002**: Serviços extra contabilizados e refletidos no valor de check-out em todos os testes de integração relevantes.
- **SC-003**: Historial clínico filtrável por animal/estadia/data com resposta paginada e ordenada; 95% das queries de leitura retornam em <1s num dataset de teste (1000 registos).
- **SC-004**: Autor e timestamp auditáveis para 100% dos registos criados (logs/DB).
- **SC-005**: Controlo de acesso: apenas perfis designados conseguem criar intervenções clínicas (teste de autorização automatizado).
- **SC-005A**: Intervenções clínicas fora de estadia ativa ou com custo negativo são sempre rejeitadas.
- **SC-005B**: Registos de cuidado e serviços extra fora de estadia ativa são sempre rejeitados; serviços extra com custo negativo também são rejeitados.
- **SC-006**: Cada funcionalidade P1 tem pelo menos 1 teste unitário na camada de serviço e 1 teste de integração executável no controller correspondente.
- **SC-007**: Existe pelo menos 1 teste de sistema end-to-end que cubra plano de cuidados, registo de cuidado e consulta do historial sem dependências manuais externas.

## Documentação Técnica

- O código Java deve ser documentado com Javadoc em controllers, services, DTOs e exceptions públicas ou relevantes para fluxos de negócio.
- O Maven Javadoc Plugin deve gerar a documentação HTML do código.
- A implementação deve seguir `.specify/memory/constitution.md` e as convenções de estilo em [docs/Etapa3/convencoes.md](../../docs/Etapa3/convencoes.md).

## Assunções
- A base de utilizadores, perfis e autenticação reusa a infraestrutura existente (ver [ADR-05](../../docs/Etapa2/04-architecture-decisions/ADR-05-controlo-acesso-perfil.md#L1)).
- A entidade `Estadia` já existe e tem um identificador estável usado pelas novas entidades.
- A faturação final no check-out é extensível para somar `ServicoExtra` e `IntervencaoClinica` sem refatoração profunda.
- Formatos de data/hora seguem a convenção da aplicação (ISO local) e fusos não são foco nesta fase.

## Dependências
- Depende das interfaces `IEstadiaService`/`IReservaService` e das implementações `EstadiaService`/`ReservaService` existentes para validações de estado (ver [docs/Etapa2/01-architecture/architecture.md](docs/Etapa2/01-architecture/architecture.md#L1)).
- Requer decisões de implementação sobre DTOs e mapeamento (seguindo [ADR-06](../../docs/Etapa2/04-architecture-decisions/ADR-06-isolamento-apresentacao-dtos.md#L1)).

## Architecture & Service Boundary
- Os controladores desta feature, incluindo os de histórico e operação clínica, devem delegar a execução a serviços de aplicação e não podem aceder diretamente a repositórios.
- A consulta consolidada do historial, os registos de cuidado, os serviços extra e as intervenções clínicas devem ser encapsulados em serviços de aplicação dedicados, que concentram as regras de negócio e a composição de queries.

## Testes de Aceitação Propostos
- Teste de integração: `RegistoCuidadoIntegrationTest` que cria reserva→check-in→registo de cuidado→valida visualização no historial.
- Teste de contrato: `ServicoExtraBillingTest` que regista vários extras e valida soma no cálculo de check-out.
- Teste de autorização: `ClinicaAuthTest` assegurando que apenas `VETERINARIO` pode criar `IntervencaoClinica`.

## Entregáveis & Plano de Implementação (alto nível)
1. Criar entidades JPA e migrations (V5) para `RegistoCuidado`, `ServicoExtra`, `IntervencaoClinica`, `Nota`.
2. Repositórios e serviços com operações CRUD e buscas filtradas (paginação), garantindo que a lógica de negócio e a composição de consultas ficam na camada de serviço.
3. Controllers e DTOs (segurança via perfis, validações de input), sem acesso direto a repositórios.
4. Templates/Views para recepção/cuidador/veterinario (seguindo mockups em Etapa2/05-ui-interface-mockup).
5. Testes unitários, de integração e de sistema; atualizar documentação e rastreabilidade (spec, tasks, research).

## Referências
- Etapa 1 — Use cases e requisitos: [UC-09](docs/Etapa1/03-use-cases/UC-09.md#L1), [UC-10](docs/Etapa1/03-use-cases/UC-10.md#L1), [UC-11](docs/Etapa1/03-use-cases/UC-11.md#L1), [User Stories](docs/Etapa1/01-user-stories/user-stories.md#L1), [Domain Model](docs/Etapa1/04-domain-model/domain-model.md#L1)
- Etapa 2 — Arquitetura e decisões: [components](docs/Etapa2/01-architecture/components.mmd#L1), [architecture.md](docs/Etapa2/01-architecture/architecture.md#L1), [ADR-02](../../docs/Etapa2/04-architecture-decisions/ADR-02-spring-mvc-thymeleaf-ssr.md#L1), [ADR-06 DTOs na apresentação](../../docs/Etapa2/04-architecture-decisions/ADR-06-isolamento-apresentacao-dtos.md#L1)

---

(Esboço finalizado. Se concorda, prossigo para gerar `checklists/requirements.md` e registar o spec em `.specify/feature.json`.)
