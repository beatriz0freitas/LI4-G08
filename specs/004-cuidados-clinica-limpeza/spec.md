# Spec: Fase 4 — Operação diária, clínica e limpeza avançada

**Short name**: cuidados-clinica-limpeza
**Feature directory**: specs/004-cuidados-clinica-limpeza
**Criado**: 2026-05-18
**Status**: Draft

## Resumo
Completar a rastreabilidade da operação diária e do acompanhamento clínico durante estadias: registo de cuidados diários, notas operacionais em reservas, serviços extra (banho, passeio, outro) e intervenções clínicas com histórico. Esta especificação descreve cenários de utilizador, requisitos funcionais testáveis, entidades de domínio, critérios de sucesso e dependências com a documentação existente (Etapa 1 e Etapa 2).

## Mapeamento a artefactos existentes
- Use Cases: [UC-09 - Registar Cuidados Diarios](docs/Etapa1/03-use-cases/UC-09.md#L1), [UC-10 - Registar Servico Extra](docs/Etapa1/03-use-cases/UC-10.md#L1), [UC-11 - Gerir Historial Clinico](docs/Etapa1/03-use-cases/UC-11.md#L1)
- User Stories: US-14, US-15, US-16, US-17, US-18, US-22, US-23 (ver rastreabilidade em [docs/Etapa1/01-user-stories/user-stories.md](docs/Etapa1/01-user-stories/user-stories.md#L1))
- Requisitos não-funcionais relevantes: [RNF-02](docs/Etapa1/02-requirements/non-functional/RNF-02.md#L1), [RNF-09](docs/Etapa1/02-requirements/non-functional/RNF-09.md#L1)
- Arquitetura / decisões: [ADR-01 Monolito Camadas](../../docs/Etapa2/04-architecture-decisions/ADR-01-monolito-camadas.md#L1), [ADR-02 Spring MVC + Thymeleaf SSR](../../docs/Etapa2/04-architecture-decisions/ADR-02-spring-mvc-thymeleaf-ssr.md#L1), [ADR-06 DTOs na apresentação](../../docs/Etapa2/04-architecture-decisions/ADR-06-isolamento-apresentacao-dtos.md#L1)
- Componentes e diagramas: [components.mmd](docs/Etapa2/01-architecture/components.mmd#L1), fluxos de sequência relevantes em [docs/Etapa2/03-seq-diagrams/UC-09.mmd](docs/Etapa2/03-seq-diagrams/UC-09.mmd#L1), [UC-10.mmd](docs/Etapa2/03-seq-diagrams/UC-10.mmd#L1), [UC-11.mmd](docs/Etapa2/03-seq-diagrams/UC-11.mmd#L1)


## User Scenarios & Testing (Prioritizados)
Cada cenário inclui um teste de aceitação independente e executável.

### US-14 - Consultar plano de cuidados (Prioridade: P1)
Descrição: Cuidador consulta o plano de cuidados do animal em estadia, incluindo tarefas recorrentes e instruções específicas.
Porque P1: Permite ao cuidador conhecer as rotinas e cumprir instruções clínicas/operacionais.
Independent Test: Abrir a vista do plano de cuidados para uma estadia activa e verificar que as entradas correspondem às instruções registadas na reserva/animal.
Acceptance Scenarios:
1. Dado um animal em estadia activa, quando o cuidador abre a vista "Plano de Cuidados", então o sistema devolve a lista de cuidados planeados e registos recentes.
2. A vista permite filtrar por tipo de cuidado e por período.

### US-15 - Registar cuidado diário (Prioridade: P1)
Descrição: Funcionário/cuidador regista um cuidado diário para um animal em estadia (ex.: alimentação, medicação, passeio), com descrição livre, timestamp e autor.
Porque P1: Registo diário é essencial para segurança e continuidade clínica.
Independent Test: Criar `RegistoCuidado` associado a `Estadia` e verificar visibilidade na ficha do animal e no histórico de estadia.
Acceptance Scenarios:
1. Dado um animal em estadia activa, quando o cuidador submete um formulário de cuidado com "descricao" e "dataHora", então o sistema cria um `RegistoCuidado` ligado à `Estadia` e o retorna com id gerado.
2. Dado um cuidado registado, quando um director visualiza a estadia, então o registo aparece ordenado por `dataHora` descendente.

### US-18 - Registar serviço extra (Prioridade: P1)
Descrição: Recepção ou cuidador regista um `ServicoExtra` (BANHO, PASSEIO, OUTRO) durante a estadia incluindo custo e momento.
Porque P1: Serviços extra impactam faturação e devem ser registados em tempo real.
Independent Test: Criar `ServicoExtra` e validar impacto no resumo financeiro da estadia e no detalhe de pagamentos.
Acceptance Scenarios:
1. Dado uma estadia em curso, quando se regista um `ServicoExtra` com `tipo` e `custo`, então o serviço é persistido e aparece na secção "extras" da estadia.
2. Após check-out, a soma de `ServicoExtra` é incluída no cálculo final do pagamento.

### US-23 - Registar intervenção clínica (Prioridade: P2)
Descrição: Médico veterinário regista uma `IntervencaoClinica` com descrição, custo e nota clínica associada à estadia/animal.
Porque P2: Importante para historial médico, menos frequente que cuidados diários mas crítico para segurança.
Independent Test: Persistir `IntervencaoClinica` e verificar que aparece no historial clínico acessível por `UC-11`.
Acceptance Scenarios:
1. Médico autenticado cria intervenção; sistema grava autor e timestamp; intervenção aparece na ficha clínica do animal.

### US-17 - Notas operacionais em reserva (Prioridade: P2)
Descrição: Recepção adiciona `Nota` à `Reserva` (instruções especiais, alergias, observações).
Independent Test: Adicionar `Nota` a uma reserva e verificar visibilidade durante check-in e nas operações de cuidado.
Mapped to: RF-05 (histórico de operações/consultas).

### US-22 - Consulta consolidada do historial clínico e operacional (Prioridade: P1)
Descrição: Veterinário ou director consulta todo o historial (Registos de cuidado, Intervencoes, ServicosExtra, Notas) filtrável por periodo e por estadia/animal.
Independent Test: Endpoint de consulta devolve lista filtrada por `animalId`, `estadiaId`, `dataInicio`/`dataFim` e tipo de registo.
Acceptance Scenarios:
1. Selecção por `animalId` e intervalo devolve apenas registos nesse periodo.
2. Ordenação por data funciona conforme escolhido (asc/desc).

### US-16 - Registar alterações ao estado de saúde (Priority: P2)
Descrição: Funcionário/cuidador ou veterinário regista uma alteração do estado de saúde do animal durante a estadia, com descrição e severidade.
Porque P2: Registar alterações de saúde é crítico para acompanhamento clínico e ações de triagem.
Independent Test: Criar `AlteracaoEstadoSaude` associada a uma `Estadia` e verificar que aparece no historial clínico e na vista recente do animal.
Acceptance Scenarios:
1. Dado um animal em estadia activa, quando o cuidador regista uma alteração com `descricao` e `severidade`, então o registo é persistido e aparece na timeline clínica.
2. Uma alteração de severidade `CRITICO` dispara sinalização adicional (ex.: flag visual na interface) e é visível ao `VETERINARIO`.

### Edge Cases
- Tentativa de registar um `RegistoCuidado` para uma estadia terminada deve ser rejeitada (400 Bad Request).
- Serviços extra com custo negativo devem ser rejeitados.
- Intervenções clínicas requerem autorização de perfil `VETERINARIO`.

## Requirements
Todos os requisitos abaixo usam os identificadores canónicos do repositório.

### Requisitos Funcionais
- **RF-11**: O sistema deve disponibilizar o plano de cuidados de cada animal em estadia, consultável por qualquer cuidador.
- **RF-12**: O sistema deve permitir o registo de cada cuidado prestado a um animal em estadia, incluindo tipo de cuidado, data e hora, identificação do cuidador responsável e observações adicionais.
- **RF-13**: O sistema deve permitir o registo de alterações ao estado de saúde de cada animal e disponibilizar uma lista dos animais com alterações recentes, consultável pelo médico veterinário.
- **RF-14**: O sistema deve permitir ao médico veterinário consultar o historial clínico de cada animal e registar intervenções, prescrições e o custo associado.
- **RF-17**: O sistema deve permitir registar um serviço extra com custo durante a estadia e associá-lo automaticamente à reserva em curso, para inclusão na faturação do check-out.
- **RF-05**: O sistema deve manter o histórico de estadias e pagamentos consultável, para suportar a consulta consolidada do historial operacional e financeiro.

### Requisitos Não-Funcionais
- **RNF-01**: O sistema deve garantir tempo de resposta inferior a 2 segundos para operações de leitura relevantes, incluindo a consulta de históricos e listas de registos.
- **RNF-04**: O acesso às operações de escrita deve exigir autenticação prévia e controlo de permissões por perfil de utilizador.
- **RNF-05**: O sistema deve garantir a confidencialidade dos dados clínicos dos animais e dos dados pessoais associados.

### Requisitos de Domínio
- **RD-04**: O pagamento no check-in cobre exclusivamente o valor da estadia; os serviços extra e as intervenções veterinárias são cobrados no check-out.
- **RD-09**: O custo de um serviço extra ou de uma intervenção veterinária deve ser registado no momento da sua ocorrência e associado à reserva em curso, não podendo ser alterado após o check-out.

## Key Entities (Resumo de domínio)
- `RegistoCuidado` (id, estadiaId, descricao, dataHora, autorId)
- `ServicoExtra` (id, estadiaId, tipo: Enum {BANHO, PASSEIO, OUTRO}, custo, dataHora, autorId)
- `IntervencaoClinica` (id, estadiaId, descricao, custo, dataHora, medicoId)
- `Nota` (id, reservaId, descricao, autorId, dataHora)
- `AlteracaoEstadoSaude` (id, estadiaId, descricao, severidade, dataHora, autorId)

Relações: `Estadia` 1..* `RegistoCuidado` ; `Estadia` 1..* `ServicoExtra` ; `Estadia` 1..* `IntervencaoClinica` ; `Reserva` 1..* `Nota` ; `Estadia` 1..* `AlteracaoEstadoSaude`.

## Success Criteria (Mensuráveis)
- **SC-001**: Funcionalidade básica (criar/listar Registos de cuidado) implementada e testada com cobertura de integração (end-to-end) — 100% dos cenários P1 passam.
- **SC-002**: Serviços extra contabilizados e refletidos no valor de check-out em todos os testes de integração relevantes.
- **SC-003**: Historial clínico filtrável por animal/estadia/data com resposta paginada e ordenada; 95% das queries de leitura retornam em <1s num dataset de teste (1000 registos).
- **SC-004**: Autor e timestamp auditáveis para 100% dos registos criados (logs/DB).
- **SC-005**: Controlo de acesso: apenas perfis designados conseguem criar intervenções clínicas (teste de autorização automatizado).

## Assunções
- A base de utilizadores, perfis e autenticação reusa a infraestrutura existente (ver [ADR-05](../../docs/Etapa2/04-architecture-decisions/ADR-05-controlo-acesso-perfil.md#L1)).
- A entidade `Estadia` já existe e tem um identificador estável usado pelas novas entidades.
- A faturação final no check-out é extensível para somar `ServicoExtra` e `IntervencaoClinica` sem refatoração profunda.
- Formatos de data/hora seguem a convenção da aplicação (ISO local) e fusos não são foco nesta fase.

## Dependências
- Depende das interfaces `IEstadiaService`/`IReservaService` e das implementações `EstadiaService`/`ReservaService` existentes para validações de estado (ver [docs/Etapa2/01-architecture/architecture.md](docs/Etapa2/01-architecture/architecture.md#L1)).
- Requer decisões de implementação sobre DTOs e mapeamento (seguindo [ADR-06](../../docs/Etapa2/04-architecture-decisions/ADR-06-isolamento-apresentacao-dtos.md#L1)).

## Testes de Aceitação Propostos
- Teste de integração: `RegistoCuidadoIntegrationTest` que cria reserva→check-in→registo de cuidado→valida visualização no historial.
- Teste de contrato: `ServicoExtraBillingTest` que regista vários extras e valida soma no cálculo de check-out.
- Teste de autorização: `ClinicaAuthTest` assegurando que apenas `VETERINARIO` pode criar `IntervencaoClinica`.

## Entregáveis & Plano de Implementação (alto nível)
1. Criar entidades JPA e migrations (V5) para `RegistoCuidado`, `ServicoExtra`, `IntervencaoClinica`, `Nota`.
2. Repositórios e serviços com operações CRUD e buscas filtradas (paginação).
3. Controllers e DTOs (segurança via perfis, validações de input).
4. Templates/Views para recepção/cuidador/veterinario (seguindo mockups em Etapa2/05-ui-interface-mockup).
5. Testes de integração e unitários; atualizar documentação e rastreabilidade (spec, tasks, research).

## Referências
- Etapa 1 — Use cases e requisitos: [UC-09](docs/Etapa1/03-use-cases/UC-09.md#L1), [UC-10](docs/Etapa1/03-use-cases/UC-10.md#L1), [UC-11](docs/Etapa1/03-use-cases/UC-11.md#L1), [User Stories](docs/Etapa1/01-user-stories/user-stories.md#L1), [Domain Model](docs/Etapa1/04-domain-model/domain-model.md#L1)
- Etapa 2 — Arquitetura e decisões: [components](docs/Etapa2/01-architecture/components.mmd#L1), [architecture.md](docs/Etapa2/01-architecture/architecture.md#L1), [ADR-02](../../docs/Etapa2/04-architecture-decisions/ADR-02-spring-mvc-thymeleaf-ssr.md#L1), [ADR-06 DTOs na apresentação](../../docs/Etapa2/04-architecture-decisions/ADR-06-isolamento-apresentacao-dtos.md#L1)

---

(Esboço finalizado. Se concorda, prossigo para gerar `checklists/requirements.md` e registar o spec em `.specify/feature.json`.)
