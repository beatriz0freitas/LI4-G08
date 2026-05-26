# Análise de lacunas da implementação face às specs Speckit

Data da análise: 2026-05-25

## 1. Objetivo

Este documento consolida as lacunas encontradas entre a documentação atual do projeto, as specs Speckit existentes e a implementação da aplicação `PatasBigodesApp`.

O objetivo não é substituir as specs, mas indicar, para cada problema:

- qual é a falha ou ambiguidade existente;
- qual o impacto funcional, técnico ou de rastreabilidade;
- como a falha deve ser corrigida;
- em que spec a regra devia estar especificada, implementada e validada.

## 2. Âmbito verificado

Foram considerados os artefactos documentais e técnicos seguintes:

- `docs/Etapa0/Enunciado.md`;
- `docs/Etapa1/`;
- `docs/Etapa2/`;
- `specs/001-fundacao-hotel-animais/`;
- `specs/002-registo-clientes-alojamentos/`;
- `specs/003-reservas-estadias-pagamentos/`;
- `specs/004-cuidados-clinica-limpeza/`;
- `specs/005-relatorios-colaboradores/`;
- implementação Spring Boot em `PatasBigodesApp/`.

A verificação técnica executada anteriormente indicou:

- `mvn test` em `PatasBigodesApp`: `77` testes executados, `0` falhas, `0` erros, `0` testes ignorados;
- build Maven concluída com sucesso;
- cobertura JaCoCo aproximada: cerca de `52%` global, com pacotes de serviços e controladores abaixo do nível desejável para fluxos críticos.

Esta situação significa que a implementação compila e os testes atuais passam, mas não significa que todos os comportamentos exigidos pelas specs estejam implementados. Em vários casos, os testes existentes validam apenas existência de beans, carregamento de páginas ou cenários muito superficiais.

## 3. Leitura recomendada

Cada lacuna é apresentada com a mesma estrutura:

- **Problema atual**: o que existe hoje na documentação ou no código.
- **Impacto**: porque é que o problema é relevante.
- **Como corrigir**: alterações esperadas na documentação, implementação e testes.
- **Spec responsável**: spec Speckit onde a regra devia estar definida e encerrada.

Quando uma regra atravessa mais do que uma área funcional, é indicada uma spec principal e specs secundárias.

## 4. Lacunas identificadas

### LAC-01 - Estado Speckit e rastreabilidade documental inconsistentes

**Problema atual**

As specs Speckit aparentam estar ainda em estado `Draft` ou `Draft clarificado`, apesar de já existir implementação relevante em `PatasBigodesApp`.

Além disso:

- `.specify/feature.json` aponta para `specs/004-cuidados-clinica-limpeza`, embora já exista `specs/005-relatorios-colaboradores`;
- `specs/002-registo-clientes-alojamentos/tasks.md` mantém tarefas por assinalar, apesar de existirem funcionalidades implementadas;
- `specs/005-relatorios-colaboradores/checklists/requirements.md` mantém itens por fechar;
- `specs/005-relatorios-colaboradores/tasks.md` referencia `checklists/qa-results.md`, mas esse ficheiro não existe.

**Impacto**

A equipa perde a fonte de verdade operacional: não é claro que spec está ativa, que tarefas estão realmente concluídas e que requisitos foram aceites. Isto dificulta a revisão por docentes, o planeamento da equipa e a validação de conformidade com Speckit.

Também cria risco de regressões, porque uma funcionalidade pode estar codificada mas não rastreada até uma user story, requisito, tarefa ou checklist.

**Como corrigir**

1. Rever cada spec e atualizar o estado para refletir a realidade:
   - manter `Draft` apenas se ainda houver regras abertas;
   - marcar como pronta apenas quando requisitos, plano, tarefas, testes e implementação estiverem coerentes.
2. Atualizar `.specify/feature.json` para a feature atualmente ativa ou documentar o motivo de estar fixada na spec `004`.
3. Marcar tarefas concluídas apenas quando a implementação e os testes existirem.
4. Criar o ficheiro `specs/005-relatorios-colaboradores/checklists/qa-results.md` ou remover a referência caso a checklist tenha sido substituída.
5. Acrescentar, por spec, uma pequena secção de rastreabilidade com:
   - user stories cobertas;
   - requisitos funcionais cobertos;
   - casos de uso cobertos;
   - testes que validam os fluxos principais.

**Spec responsável**

- Principal: todas as specs `001` a `005`.
- Correção mais urgente: `specs/005-relatorios-colaboradores/`, porque contém referência a checklist inexistente.
- Metadados Speckit: `.specify/feature.json`.

### LAC-02 - Plano de cuidados definido mas não implementado

**Problema atual**

A spec de cuidados prevê a gestão de planos de cuidados, mas o serviço respetivo ainda não existe funcionalmente. Em `PatasBigodesApp/src/main/java/pt/hotel/animais/service/PlanoCuidadosService.java`, o método implementado lança `UnsupportedOperationException("Not implemented yet")`.

Isto indica que a funcionalidade foi modelada ou antecipada, mas não foi completada na camada de aplicação.

**Impacto**

O hotel não consegue registar ou gerir instruções recorrentes de cuidados associadas a animais, estadias ou necessidades especiais. Isso afeta a execução operacional dos cuidados diários e quebra a expectativa criada pela documentação.

Também enfraquece a rastreabilidade de `US-14` e `RF-11`, porque a aplicação tem um ponto explícito de funcionalidade não implementada.

**Como corrigir**

1. Clarificar na spec o âmbito exato do plano de cuidados:
   - se é por animal, por estadia ou por ambos;
   - que campos são obrigatórios;
   - se o plano tem periodicidade;
   - quem o pode criar e alterar;
   - que estados existem, por exemplo ativo, suspenso ou concluído.
2. Completar o modelo de dados se necessário.
3. Implementar `PlanoCuidadosService` com operações reais:
   - criar plano;
   - consultar plano ativo;
   - atualizar instruções;
   - encerrar ou desativar plano;
   - validar existência de animal, cliente e estadia quando aplicável.
4. Criar controlador e vistas, se a spec exigir interação por UI.
5. Criar testes de serviço e integração:
   - criação válida;
   - tentativa de criar plano sem animal/estadia válida;
   - atualização;
   - encerramento;
   - consulta do plano no histórico do animal ou da estadia.

**Spec responsável**

- Principal: `specs/004-cuidados-clinica-limpeza/`.
- Deve cobrir explicitamente: `US-14`, `RF-11` e os cenários de aceitação associados.

### LAC-03 - Check-out sem cobrança complementar real

**Problema atual**

O fluxo de check-out existe, mas a cobrança complementar não está implementada de forma completa. O serviço de pagamento calcula a estadia base com uma tarifa fixa e os extras devolvem atualmente `BigDecimal.ZERO`.

Referências técnicas relevantes:

- `PagamentoService.calcularValorEstadia(...)` usa uma tarifa fixa diária;
- `PagamentoService.calcularExtras(...)` devolve `BigDecimal.ZERO`;
- `EstadiaService` cria pagamentos de forma limitada no check-in/check-out.

**Impacto**

O check-out não reflete o custo real da estadia quando existem serviços extra, cuidados clínicos faturáveis, banhos, tosquias ou outros consumos. Isto compromete a integridade financeira do sistema e a utilidade operacional da aplicação.

Também reduz a validade dos relatórios financeiros, porque os dados de pagamento ficam incompletos ou incorretos.

**Como corrigir**

1. Definir na spec `003` a regra de cálculo final:
   - valor base da estadia;
   - noites/dias faturáveis;
   - extras associados à estadia;
   - arredondamentos;
   - momento em que o valor fica fechado;
   - relação entre pagamento inicial, pagamento final e total em dívida.
2. Definir na spec `004` que serviços extra e intervenções são faturáveis.
3. Implementar no `PagamentoService`:
   - agregação de serviços extra por estadia;
   - inclusão de custos clínicos quando aplicável;
   - cálculo do valor em falta no check-out;
   - persistência do pagamento complementar;
   - validação de método de pagamento.
4. Criar testes de check-out com:
   - estadia sem extras;
   - estadia com extras;
   - estadia com intervenção clínica faturável;
   - check-out parcial ou sem pagamento final, se tal for permitido;
   - falha no cálculo financeiro.

**Spec responsável**

- Principal: `specs/003-reservas-estadias-pagamentos/`.
- Secundária: `specs/004-cuidados-clinica-limpeza/`, para custos de serviços e intervenções.
- Requisitos afetados: `US-11`, `RF-09`, `RF-10`, `RD-04`, `US-18`, `US-23`, `RD-09`.

### LAC-04 - Tarifa base da estadia não está especificada como regra de negócio

**Problema atual**

A aplicação calcula o valor base da estadia com uma tarifa fixa de `10.00` por dia. O modelo de `Alojamento` não contém uma tarifa própria, e a documentação não fecha claramente onde a tarifa deve ser definida.

**Impacto**

O sistema não consegue representar alojamentos com preços diferentes, por exemplo box individual, box grande, quarto premium ou alojamento para espécies distintas.

Isto limita a utilidade da gestão de alojamentos e torna o cálculo financeiro pouco realista. Também torna difícil justificar os valores nos relatórios.

**Como corrigir**

1. Decidir na documentação qual é a origem da tarifa:
   - tarifa por alojamento;
   - tarifa por tipo de alojamento;
   - tarifa global configurável;
   - tarifa por espécie ou porte do animal.
2. Acrescentar o campo correspondente ao modelo de domínio e ao modelo persistente.
3. Atualizar formulários de criação/edição de alojamentos, se a tarifa for por alojamento.
4. Atualizar `PagamentoService` para usar a regra documentada.
5. Criar testes para cálculo com tarifas diferentes.

**Spec responsável**

- Principal: `specs/003-reservas-estadias-pagamentos/`, porque a tarifa impacta pagamentos.
- Secundária: `specs/001-fundacao-hotel-animais/` ou `specs/002-registo-clientes-alojamentos/`, se a tarifa fizer parte da configuração inicial ou da gestão de alojamentos.
- Requisitos afetados: `US-10`, `RF-08`, `RF-10`, `RD-04`.

### LAC-05 - Método de pagamento indefinido no check-in

**Problema atual**

No check-in, a aplicação cria um pagamento com `MetodoPagamento.NAO_DEFINIDO`. A documentação não esclarece se o pagamento no check-in é obrigatório, opcional, sinal, caução ou apenas registo pendente.

**Impacto**

Há ambiguidade financeira e operacional. Um pagamento criado sem método pode representar:

- uma dívida;
- um pagamento futuro;
- uma reserva financeira;
- um erro de validação não tratado.

Isto afeta a auditoria e a fiabilidade dos relatórios.

**Como corrigir**

1. Clarificar na spec `003` se o check-in exige pagamento.
2. Se exigir pagamento:
   - tornar o método obrigatório;
   - validar valor;
   - impedir check-in sem registo financeiro válido.
3. Se não exigir pagamento:
   - não criar pagamento automático;
   - criar antes um estado financeiro pendente documentado.
4. Atualizar UI, serviço e testes para cobrir os dois comportamentos possíveis.

**Spec responsável**

- Principal: `specs/003-reservas-estadias-pagamentos/`.
- Requisitos afetados: `US-10`, `RF-08`, `RF-10`, `RD-04`.

### LAC-06 - Disponibilidade de alojamentos ignora estadias ativas

**Problema atual**

A consulta de disponibilidade considera reservas ativas e estado de limpeza, mas não cruza de forma completa com estadias ativas. Assim, um alojamento ocupado por uma estadia pode continuar a surgir como disponível dependendo do caminho de consulta.

Referências técnicas relevantes:

- `AlojamentoRepository.findDisponiveisParaPeriodo(...)`;
- `AlojamentoService.contarDisponiveis(...)`;
- estado de limpeza usado como critério forte de disponibilidade.

**Impacto**

Existe risco de dupla ocupação do mesmo alojamento. Este é um problema crítico para uma aplicação de hotel, porque afeta reservas, check-in, planeamento operacional e confiança do utilizador.

**Como corrigir**

1. Definir uma única regra de disponibilidade:
   - sem reserva ativa sobreposta;
   - sem estadia ativa;
   - estado de limpeza compatível com ocupação;
   - capacidade adequada ao animal;
   - alojamento ativo.
2. Implementar a regra num serviço de domínio ou método centralizado para evitar variações entre ecrãs.
3. Atualizar queries para excluir estadias ativas.
4. Criar testes com:
   - alojamento livre;
   - alojamento reservado;
   - alojamento ocupado;
   - alojamento em limpeza;
   - sobreposição parcial de datas.

**Spec responsável**

- Principal: `specs/003-reservas-estadias-pagamentos/`.
- Secundárias: `specs/001-fundacao-hotel-animais/` e `specs/002-registo-clientes-alojamentos/`.
- Requisitos afetados: `RF-06`, `RD-01` e regras de reserva/check-in.

### LAC-07 - Estados de reserva confundem confirmação com conclusão

**Problema atual**

O enum de estado de reserva contém apenas `ATIVA`, `CANCELADA` e `CONCLUIDA`. No controlador, a ação `/confirmar` chama um método de conclusão da reserva.

Isto indica que confirmar uma reserva e concluir uma reserva estão a ser tratados como se fossem o mesmo evento.

**Impacto**

O ciclo de vida de uma reserva fica incompleto. Uma reserva pode precisar de passar por estados como:

- criada;
- confirmada;
- cancelada;
- convertida em estadia;
- concluída.

Sem estados distintos, a aplicação pode perder informação importante para operação, auditoria e relatórios.

**Como corrigir**

1. Clarificar na spec `003` o ciclo de vida formal da reserva.
2. Distinguir semanticamente:
   - confirmação administrativa da reserva;
   - check-in que transforma reserva em estadia;
   - conclusão após check-out.
3. Atualizar o enum `EstadoReserva`.
4. Atualizar serviços e controladores para usar métodos alinhados com o evento real.
5. Migrar ou adaptar dados de teste.
6. Criar testes de transições válidas e inválidas.

**Spec responsável**

- Principal: `specs/003-reservas-estadias-pagamentos/`.
- Requisitos afetados: `US-06`, `US-07`, `RF-07`, `RD-02`.

### LAC-08 - Falta regra de exclusividade de estadia ativa por animal

**Problema atual**

O serviço de estadia abre uma nova estadia sem verificar explicitamente se o animal já tem uma estadia ativa. A regra pode estar implícita no negócio, mas não está suficientemente protegida no código.

**Impacto**

O mesmo animal pode ficar associado a duas estadias ativas, o que gera inconsistência em:

- localização atual do animal;
- histórico;
- serviços extra;
- pagamentos;
- limpeza de alojamentos;
- relatórios de ocupação.

**Como corrigir**

1. Especificar na spec que um animal só pode ter uma estadia ativa de cada vez.
2. Criar query de verificação por `animalId` e estado da estadia.
3. Bloquear a abertura de nova estadia quando já existir uma estadia ativa.
4. Definir a mensagem de erro esperada para a UI.
5. Criar teste de tentativa de check-in duplicado.

**Spec responsável**

- Principal: `specs/003-reservas-estadias-pagamentos/`.
- Requisito/regra afetada: `RD-07`, caso esta regra já esteja documentada; se não estiver, deve ser acrescentada como regra de domínio da spec `003`.

### LAC-09 - Falhas críticas no check-out são engolidas pelo serviço

**Problema atual**

No fluxo de check-out, algumas exceções associadas a pagamento e limpeza são capturadas sem bloquear a operação. O serviço fecha a estadia mesmo que tarefas críticas posteriores falhem.

Referências técnicas relevantes:

- `EstadiaService` captura exceções de pagamento complementar;
- `EstadiaService` captura exceções ao atualizar o estado de limpeza.

**Impacto**

A aplicação pode ficar num estado inconsistente:

- estadia fechada sem pagamento final;
- alojamento não marcado para limpeza;
- histórico aparentemente concluído mas operação incompleta.

Este comportamento contraria a expectativa de integridade transacional num fluxo crítico.

**Como corrigir**

1. Definir na spec `003` quais operações fazem parte da transação de check-out.
2. Definir se pagamento e limpeza são obrigatórios ou compensáveis.
3. Se forem obrigatórios:
   - executar numa transação única;
   - falhar o check-out quando pagamento ou limpeza falham;
   - apresentar erro claro ao utilizador.
4. Se forem compensáveis:
   - criar estado intermédio, por exemplo `CHECKOUT_PENDENTE`;
   - registar incidente operacional;
   - permitir retoma do processo.
5. Criar testes que simulem falha de pagamento e falha de limpeza.

**Spec responsável**

- Principal: `specs/003-reservas-estadias-pagamentos/`.
- Secundária: `specs/004-cuidados-clinica-limpeza/`, pela transição do alojamento para limpeza.
- Requisitos afetados: `RD-03`, `RD-04`, `RD-01`.

### LAC-10 - Serviços extra e intervenções clínicas têm validações incompletas

**Problema atual**

Os serviços extra persistem o tipo como `String`, apesar de existir enum `TipoServicoExtra`. O custo não tem validação explícita robusta para impedir valores negativos ou inconsistentes.

Nas intervenções clínicas, não está suficientemente claro se a intervenção exige estadia ativa, médico responsável, custo obrigatório, descrição ou data coerente.

**Impacto**

Podem ser registados dados inconsistentes, difíceis de agrupar e faturar:

- tipos escritos de formas diferentes;
- serviços com custo negativo;
- intervenções sem responsável;
- atos clínicos associados a estadias inexistentes ou já encerradas.

Isto afeta histórico, faturação e relatórios.

**Como corrigir**

1. Na spec `004`, definir campos obrigatórios e regras de validação para:
   - serviço extra;
   - intervenção clínica;
   - registo de cuidado;
   - limpeza.
2. Usar enum persistente para tipo de serviço extra ou validar a string contra catálogo controlado.
3. Bloquear custos negativos.
4. Definir se custo zero é permitido.
5. Exigir estadia ativa quando o serviço ou intervenção estiver associado a alojamento durante estadia.
6. Exigir colaborador/médico responsável quando aplicável.
7. Criar testes de validação negativa e positiva.

**Spec responsável**

- Principal: `specs/004-cuidados-clinica-limpeza/`.
- Requisitos afetados: `US-18`, `US-23`, `RF-14`, `RF-17`, `RD-09`.

### LAC-11 - Histórico consolidado não aplica todos os filtros esperados

**Problema atual**

O serviço de histórico implementa a consulta consolidada principalmente quando recebe `estadiaId`. Filtros como `animalId` e datas não estão plenamente aplicados em todos os caminhos.

**Impacto**

O utilizador pode obter um histórico incompleto ou excessivo. Isto é especialmente sensível para:

- consulta do histórico de um animal;
- revisão de estadias anteriores;
- análise clínica;
- validação de serviços prestados;
- preparação de cobrança ou relatórios.

**Como corrigir**

1. Clarificar na spec que filtros são suportados:
   - por animal;
   - por cliente;
   - por estadia;
   - por intervalo de datas;
   - por tipo de evento.
2. Definir comportamento quando vários filtros são combinados.
3. Implementar query ou agregação consistente.
4. Garantir que reservas, estadias, cuidados, intervenções, serviços extra, limpezas e pagamentos entram no histórico quando aplicável.
5. Criar testes para cada filtro e combinações principais.

**Spec responsável**

- Principal: `specs/004-cuidados-clinica-limpeza/`, se o histórico consolidado for parte do acompanhamento de cuidados.
- Secundária: `specs/003-reservas-estadias-pagamentos/`, se o histórico incluir reservas, estadias e pagamentos.
- Requisitos afetados: `US-22`, `US-05`, `RF-05`.

### LAC-12 - Controladores contornam a camada de serviço

**Problema atual**

Alguns controladores injetam repositórios ou serviços de áreas vizinhas diretamente, em vez de delegarem o fluxo completo a um serviço de aplicação.

Exemplos observados:

- `HistoricoController` injeta repositórios diretamente;
- `ReservaController` injeta `EstadiaRepository` e serviços de pagamento diretamente.

**Impacto**

Esta abordagem espalha regras de negócio pela camada web, dificultando:

- testes unitários;
- reutilização dos fluxos;
- validação transacional;
- manutenção das regras de domínio;
- rastreabilidade entre UC, RF e implementação.

Também contraria a orientação arquitetural de separar controladores, serviços e persistência.

**Como corrigir**

1. Rever a decisão arquitetural relevante, nomeadamente a separação entre MVC, serviços e repositórios.
2. Criar serviços de aplicação para os fluxos compostos:
   - histórico consolidado;
   - confirmação de reserva;
   - check-in;
   - check-out;
   - cobrança.
3. Deixar controladores responsáveis apenas por:
   - receber input;
   - validar DTO/formulário;
   - chamar serviço;
   - escolher vista/resposta.
4. Adicionar testes de serviço para regras de negócio.
5. Manter testes de controlador apenas para routing, validação web e autorização.

**Spec responsável**

- Principal: `specs/003-reservas-estadias-pagamentos/`, para fluxos de reserva, estadia e pagamento.
- Secundária: `specs/004-cuidados-clinica-limpeza/`, para histórico e cuidados.
- Referência arquitetural: documentos de Etapa 2, em especial ADRs e convenções de arquitetura.

### LAC-13 - Auditoria incompleta para operações críticas

**Problema atual**

Existe configuração de auditoria, mas os eventos auditados parecem concentrar-se em colaboradores e relatórios. Operações críticas como reserva, check-in, check-out, pagamento, cuidados, intervenções clínicas e limpeza não estão claramente auditadas.

**Impacto**

Sem auditoria transversal, a aplicação não consegue responder bem a perguntas como:

- quem cancelou uma reserva;
- quem abriu uma estadia;
- quem registou um pagamento;
- quem alterou um custo;
- quem marcou um alojamento como limpo;
- quem registou uma intervenção clínica.

Isto é relevante para responsabilização, depuração e avaliação de qualidade.

**Resolução** ✅ (2026-05-26)

Clarificação executada com sucesso em sessão de `speckit.clarify`. Respostas consolidadas:

1. **Ámbito**: Auditoria Completa — todas as operações críticas (criar/editar/cancelar reserva, check-in, check-out, pagamento, cuidados, clínica, limpeza, colaboradores).
2. **Formato**: Tabela dedicada `AuditoriaEvento` com 10 campos (id, timestamp, utilizadorId, operacao, entidade, entityId, acao, detalhes JSON, resultado, motivoFalha).
3. **Retenção**: 12 meses, acesso restrito a `DIRETOR`, com filtros por data/utilizador/operação.

**Artefatos gerados**:
- `specs/005-relatorios-colaboradores/spec.md` — atualizado com FR-011, SC-008/009, Clarifications
- `specs/005-relatorios-colaboradores/plan.md` — 15 fases de implementação (de 6 originais)
- `specs/005-relatorios-colaboradores/data-model.md` — entidade `AuditoriaEvento` com índices
- `specs/005-relatorios-colaboradores/lac-13-impact-analysis.md` — análise completa de impacto (novo)
- `specs/005-relatorios-colaboradores/lac-13-resolution-summary.md` — síntese de resolução (novo)

**Próximos passos**:
1. Publicar `docs/auditoria-interface.md` com interface esperada.
2. Implementar Fase 1 (fundação de auditoria) em spec 005.
3. Coordenar integração com specs 003 e 004.

**Spec responsável**

- Principal: `specs/005-relatorios-colaboradores/` (resolvida).
- Secundárias: `specs/003-reservas-estadias-pagamentos/` e `specs/004-cuidados-clinica-limpeza/` (para integração de eventos).
- Requisitos afetados: FR-011, SC-008, SC-009 (novos), `US-01`, `US-02`, `US-03`, `US-05`.

---### LAC-14 - Relatórios não cumprem totalmente exportação PDF e agrupamento

**Problema atual**

O serviço de relatórios devolve bytes de texto simples como se fossem PDF. Existe lógica de agrupamento, mas não está claramente integrada no cálculo dos relatórios.

**Impacto**

O utilizador pode descarregar um ficheiro com extensão ou tipo esperado de PDF, mas cujo conteúdo não é um PDF real. Isto compromete a aceitação funcional e a perceção de qualidade.

Além disso, se o agrupamento não for aplicado, relatórios por período, colaborador, tipo de serviço ou alojamento podem não corresponder ao que a spec promete.

**Como corrigir**

1. Clarificar na spec `005` que formatos são obrigatórios:
   - HTML;
   - CSV;
   - PDF real;
   - JSON.
2. Se PDF for obrigatório:
   - usar uma biblioteca adequada para gerar PDF válido;
   - definir layout mínimo;
   - testar `Content-Type`, nome de ficheiro e assinatura do ficheiro.
3. Integrar o agrupamento no cálculo dos relatórios.
4. Criar testes para:
   - filtros por período;
   - agrupamento;
   - exportação;
   - conteúdo mínimo esperado.

**Spec responsável**

- Principal: `specs/005-relatorios-colaboradores/`.
- Requisitos afetados: `FR-001`, `FR-003` e critérios de aceitação de exportação/agrupamento.

### LAC-15 - Testes verdes mas pouco representativos dos fluxos críticos

**Problema atual**

A suite de testes passa, mas vários testes são superficiais. Alguns exemplos:

- testes de check-in/check-out que apenas validam carregamento de páginas;
- testes de pagamento que apenas verificam existência de beans;
- testes de sequência que não validam os efeitos completos no domínio;
- ausência de cenários negativos para regras críticas.

**Impacto**

A equipa pode ter uma falsa sensação de segurança. A build passa mesmo quando:

- extras não são faturados;
- plano de cuidados não está implementado;
- disponibilidade pode ignorar estadias ativas;
- PDF não é PDF real;
- estados de reserva estão semanticamente errados.

**Como corrigir**

1. Para cada spec, criar testes alinhados com os critérios de aceitação.
2. Priorizar testes de serviço para regras de negócio.
3. Criar testes de integração para fluxos completos:
   - reserva;
   - confirmação;
   - check-in;
   - registo de cuidados;
   - serviço extra;
   - intervenção clínica;
   - check-out;
   - pagamento final;
   - limpeza;
   - relatório.
4. Aumentar cobertura nos pacotes de serviços e controladores.
5. Ligar cada teste a uma user story ou requisito quando possível, pelo nome do teste ou comentário curto.

**Spec responsável**

- Principal: todas as specs `001` a `005`.
- Mais urgentes: `specs/003-reservas-estadias-pagamentos/` e `specs/004-cuidados-clinica-limpeza/`, por conterem os fluxos operacionais mais críticos.
- Referência adicional: metas de qualidade e cobertura definidas na documentação de Etapa 4 ou nos critérios da spec `001`, quando aplicável.

## 5. Correções prioritárias por spec

### `specs/001-fundacao-hotel-animais`

Deve clarificar:

- conceito de alojamento ativo;
- estados possíveis de limpeza/ocupação;
- relação entre alojamento, capacidade e disponibilidade;
- eventual existência de tarifa base por alojamento ou tipo de alojamento.

Correções técnicas associadas:

- rever modelo `Alojamento`;
- garantir que a disponibilidade não depende apenas da limpeza;
- acrescentar testes de fundação para estados de alojamento.

### `specs/002-registo-clientes-alojamentos`

Deve clarificar:

- dados obrigatórios de cliente;
- dados obrigatórios de animal;
- dados obrigatórios de alojamento;
- se a tarifa é configurada no alojamento;
- regras de edição quando existem reservas ou estadias associadas.

Correções técnicas associadas:

- atualizar formulários e validações;
- rever tarefas ainda por assinalar em `tasks.md`;
- criar testes para validações de registo e edição.

### `specs/003-reservas-estadias-pagamentos`

É a spec com maior concentração de lacunas críticas.

Deve clarificar:

- ciclo de vida completo da reserva;
- diferença entre reserva confirmada, reserva concluída e estadia concluída;
- regra de disponibilidade;
- exclusividade de estadia ativa por animal;
- cálculo financeiro;
- método de pagamento obrigatório ou não;
- transação de check-in;
- transação de check-out;
- comportamento em falhas parciais.

Correções técnicas associadas:

- rever `EstadoReserva`;
- rever `ReservaService` e `ReservaController`;
- centralizar regra de disponibilidade;
- implementar cobrança complementar;
- impedir check-in duplicado por animal;
- tornar check-out transacional ou explicitamente compensável;
- criar testes end-to-end de reserva a check-out.

### `specs/004-cuidados-clinica-limpeza`

Deve clarificar:

- plano de cuidados;
- validações de cuidados;
- serviços extra faturáveis;
- intervenções clínicas;
- relação com estadia ativa;
- custos obrigatórios, opcionais ou proibidos;
- regras de limpeza após check-out;
- histórico consolidado.

Correções técnicas associadas:

- implementar `PlanoCuidadosService`;
- validar tipos e custos de serviços extra;
- validar intervenções clínicas;
- integrar extras e clínica no pagamento final;
- completar filtros do histórico;
- criar testes de cuidados, clínica, extras e limpeza.

### `specs/005-relatorios-colaboradores`

Deve clarificar:

- relatórios obrigatórios;
- filtros obrigatórios;
- agrupamentos obrigatórios;
- formatos de exportação;
- requisitos de auditoria;
- permissões por perfil;
- critérios de QA.

Correções técnicas associadas:

- gerar PDF real se PDF for requisito;
- integrar agrupamento no cálculo de relatórios;
- completar checklist de requisitos;
- criar `checklists/qa-results.md` ou corrigir a referência;
- auditar operações críticas;
- testar exportação e permissões.

## 6. Ordem recomendada de implementação

1. **Regularizar Speckit**
   - Fechar estado das specs, tarefas e checklists.
   - Corrigir `.specify/feature.json`.
   - Criar ou remover referências a checklists inexistentes.

2. **Corrigir regras críticas de estadia e disponibilidade**
   - Disponibilidade deve excluir reservas e estadias ativas.
   - Um animal não deve poder ter duas estadias ativas.
   - Estados de reserva devem representar o ciclo de vida real.

3. **Completar pagamentos**
   - Substituir tarifa fixa por regra documentada.
   - Exigir ou remover método de pagamento no check-in conforme decisão.
   - Calcular extras e cobrança final no check-out.

4. **Completar cuidados, clínica e limpeza**
   - Implementar plano de cuidados.
   - Validar serviços extra e intervenções clínicas.
   - Garantir integração com histórico e faturação.

5. **Completar relatórios e auditoria**
   - Garantir agrupamentos.
   - Gerar exportações reais.
   - Auditar fluxos críticos.

6. **Reforçar testes**
   - Criar testes orientados aos critérios de aceitação.
   - Cobrir cenários negativos.
   - Subir cobertura dos serviços que concentram regras de negócio.

## 7. Definition of Done documental por spec

Uma spec deve ser considerada pronta apenas quando cumprir todos os pontos seguintes:

- user stories, requisitos, casos de uso e regras de domínio estão alinhados;
- ambiguidades estão resolvidas ou registadas como pressupostos;
- tasks refletem o estado real da implementação;
- checklists estão completas;
- existe rastreabilidade entre requisitos e testes;
- a implementação não contém métodos placeholder para requisitos da spec;
- testes cobrem cenários principais e cenários negativos;
- documentação operacional reflete o comportamento real da aplicação.

## 8. Conclusão

A implementação atual tem uma base funcional relevante e passa a suite de testes existente, mas ainda não está completamente alinhada com o nível de detalhe esperado pelas specs Speckit.

As lacunas mais críticas estão nos fluxos de reserva, estadia, pagamento, disponibilidade, cuidados e relatórios. O risco principal não é a falta total de implementação, mas sim a existência de comportamentos parcialmente implementados, pouco testados ou semanticamente ambíguos.

O próximo passo recomendado é tratar este documento como backlog de validação da Etapa 4, abrindo correções por spec e fechando cada uma apenas quando documentação, código e testes estiverem coerentes.
