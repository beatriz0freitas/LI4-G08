# Guia detalhado dos diagramas do relatório LI4-G08

## 1. Objetivo e âmbito

Este guia explica, de forma detalhada, os diagramas incluídos em `LI4-G08.pdf`, cruzando a leitura do relatório com os ficheiros-fonte disponíveis no repositório, em especial os ficheiros PlantUML da `Etapa2`.

O documento tem quatro objetivos:

1. Explicar a função de cada diagrama no processo de engenharia de software.
2. Identificar os elementos principais representados e a sua interpretação.
3. Relacionar os diagramas com requisitos, casos de uso, decisões arquiteturais e implementação.
4. Registar análises críticas, pressupostos, riscos e pontos de atenção úteis para manutenção futura.

Foram considerados como diagramas ou representações visuais relevantes:

| Figura no PDF | Artefacto                          | Fonte principal no repositório                                                     | Tipo               |
| ------------- | ---------------------------------- | ----------------------------------------------------------------------------------- | ------------------ |
| 1.1           | Diagrama de Gantt para planeamento | Não localizada em PlantUML na Etapa2                                               | Planeamento        |
| 2.1           | Diagrama de Use Cases              | Especificação textual em `docs/Etapa1/03-use-cases/`                            | UML comportamental |
| 2.2           | Modelo de Domínio                 | `docs/Etapa1/04-domain-model/modelo_dominio.plantuml`                             | Modelo conceptual  |
| 3.1           | Diagrama de Componentes            | `docs/Etapa2/01-architecture/components.plantuml`                                 | UML estrutural     |
| 3.2           | Sequência UC-02                   | `docs/Etapa2/03-seq-diagrams/UC-02.puml`                                          | UML comportamental |
| 3.3           | Sequência UC-06                   | `docs/Etapa2/03-seq-diagrams/UC-06.puml`                                          | UML comportamental |
| 3.4           | Diagrama de Classes                | `docs/Etapa2/02-class-diagram/class-diagram-domain.puml` e `class-diagram.puml` | UML estrutural     |
| 3.5           | Diagrama de Deployment             | `docs/Etapa2/01-architecture/deployment-view.puml`                                | UML físico        |
| 3.6           | Modelo lógico-relacional          | `docs/Etapa2/02-class-diagram/logical-data-model.plantuml`                        | Modelo de dados    |
| 3.7           | Protótipos principais             | `docs/Etapa2/05-ui-interface-mockup/*.html`                                       | UI / wireframes    |
| 6.2 a 6.15    | Sequências UC-01 e UC-03 a UC-16  | `docs/Etapa2/03-seq-diagrams/*.puml`                                              | UML comportamental |
| 6.16          | Wireframes complementares          | `docs/Etapa2/05-ui-interface-mockup/*.html`                                       | UI / wireframes    |

Nota: a figura 6.1 do PDF corresponde ao inquérito aos clientes. É uma evidência de elicitação, não um diagrama de arquitetura, requisitos, domínio ou design, pelo que apenas é referida como artefacto contextual.

## 2. Leitura global dos diagramas

O conjunto de diagramas cobre uma cadeia de rastreabilidade bastante completa:

1. O Gantt enquadra o plano temporal.
2. O diagrama de casos de uso identifica quem usa o sistema e que objetivos pretende atingir.
3. O modelo de domínio define os conceitos de negócio.
4. O diagrama de componentes transforma o domínio e os casos de uso numa arquitetura por camadas.
5. Os diagramas de sequência mostram como os componentes colaboram em cada caso de uso.
6. O diagrama de classes detalha a estrutura estática de entidades, serviços, controladores, DTOs e repositórios.
7. O deployment mostra onde o sistema corre.
8. O modelo lógico-relacional traduz o domínio para persistência MySQL.
9. Os protótipos validam a experiência de utilização esperada.

Esta progressão é coerente com um projeto de LI4 organizado por etapas: requisitos, arquitetura/design, implementação e validação. O ponto mais forte do conjunto é a ligação entre casos de uso, arquitetura MVC em camadas e implementação Spring Boot. O ponto que exige maior cuidado é manter alinhadas as cardinalidades do modelo de domínio, do modelo relacional e do código, sobretudo em `Estadia`, `Pagamento`, `PlanoCuidados` e `Reserva`.

## 3. Figura 1.1 - Diagrama de Gantt

### 3.1 Finalidade

O Gantt apresenta o planeamento temporal das atividades do projeto. A sua função não é descrever o sistema, mas sim demonstrar organização do trabalho: etapas, dependências, duração aproximada e distribuição do esforço.

### 3.2 Interpretação

O diagrama deve ser lido como uma vista de gestão:

- As tarefas representam atividades de análise, documentação, design, implementação e validação.
- A sequência temporal mostra a natureza progressiva do projeto.
- A sobreposição de tarefas, se existir, indica trabalho paralelo entre membros da equipa.
- As fases finais devem concentrar validação, testes e consolidação documental.

### 3.3 Análise crítica

O Gantt é útil para justificar que a equipa não avançou diretamente para implementação sem consolidar requisitos e arquitetura. A sua utilidade aumenta quando está alinhado com os artefactos produzidos em cada etapa. A principal limitação é que o Gantt não prova cumprimento real do plano; apenas documenta uma intenção ou uma visão de execução. Para uma análise mais robusta, poderia ser complementado por marcos reais, datas de entrega e evidência de alterações ao plano.

## 4. Figura 2.1 - Diagrama de Casos de Uso

### 4.1 Finalidade

O diagrama de casos de uso define a fronteira funcional do sistema Patas&Bigodes. Mostra os atores que interagem com a aplicação e os objetivos que cada perfil pretende atingir.

### 4.2 Atores principais

O sistema modela cinco atores, que correspondem diretamente aos perfis de acesso usados na implementação:

| Ator                      | Responsabilidade principal                            | Exemplos de casos de uso                        |
| ------------------------- | ----------------------------------------------------- | ----------------------------------------------- |
| Funcionário de Receção | Operação de front-office e ciclo reserva-estadia    | UC-02, UC-03, UC-04, UC-05, UC-06, UC-07, UC-08 |
| Cuidador                  | Registo operacional durante estadias                  | UC-09, UC-10                                    |
| Médico Veterinário      | Acompanhamento clínico                               | UC-11 e alterações clínicas relacionadas     |
| Responsável pela Limpeza | Libertação operacional de alojamentos               | UC-12                                           |
| Diretor                   | Supervisão, administração, relatórios e auditoria | UC-13, UC-14, UC-15, UC-16                      |

### 4.3 Casos de uso e agrupamento funcional

Os casos de uso podem ser organizados em seis blocos:

| Bloco                           | Casos de uso               | Objetivo                                           |
| ------------------------------- | -------------------------- | -------------------------------------------------- |
| Segurança                      | UC-01                      | Autenticação e entrada no sistema                |
| Gestão de clientes e hóspedes | UC-03                      | Registo de tutor e animal                          |
| Reservas e disponibilidade      | UC-02, UC-04, UC-05        | Evitar overbooking e gerir intenção de estadia   |
| Estadia e faturação           | UC-06, UC-07, UC-08, UC-10 | Abrir, acompanhar, encerrar e cobrar estadias      |
| Operação diária e clínica   | UC-09, UC-11, UC-12        | Cuidados, histórico clínico e limpeza            |
| Gestão e controlo              | UC-13, UC-14, UC-15, UC-16 | Indicadores, colaboradores, catálogos e auditoria |

### 4.4 Relações `include`

O relatório refere dependências de reutilização com `include`. As mais relevantes são:

- Criar reserva inclui a verificação de disponibilidade, pois a reserva só é válida se houver alojamento compatível e livre.
- Check-in inclui pagamento, porque a entrada pressupõe cobrança do valor base.
- Check-out pode incluir pagamento, quando existem valores complementares.
- Relatórios e auditoria relacionam-se indiretamente, porque a geração de relatório é registada como evento auditável.

### 4.5 Análise crítica

O diagrama é adequado para delimitar responsabilidades por perfil. A vantagem é que antecipa a matriz de permissões e prepara a implementação com Spring Security. A principal cautela está em não interpretar atores como pessoas físicas únicas: um colaborador real pode mudar de função, mas o sistema deve autorizar operações pelo perfil, não pela identidade informal.

Também é importante distinguir casos de uso independentes de fluxos incluídos. `UC-08 - Processar Pagamento`, por exemplo, é reutilizável e aparece em check-in/check-out, mas a implementação também permite um `PagamentoController` próprio. Esta opção é aceitável, desde que as regras de pagamento permaneçam centralizadas no `IPagamentoService`.

## 5. Figura 2.2 - Modelo de Domínio

Fonte: `docs/Etapa1/04-domain-model/modelo_dominio.plantuml`.

### 5.1 Finalidade

O modelo de domínio descreve os conceitos de negócio do hotel sem se concentrar em classes técnicas. É uma ponte entre requisitos e design, permitindo validar se os elementos centrais do problema estão representados antes de decidir controllers, services ou tabelas.

### 5.2 Núcleo conceptual

O domínio organiza-se em quatro áreas:

| Área                           | Conceitos                                                                                                               | Papel no negócio                                                |
| ------------------------------- | ----------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------- |
| Clientes e hóspedes            | `Tutor`, `Animal`, `Especie`, `EstadoSaude`                                                                     | Identificar quem contrata o serviço e quem fica hospedado       |
| Alojamento e reserva            | `TipoAlojamentoTarifa`, `Alojamento`, `Reserva`, `EstadoReserva`                                                | Controlar capacidade, compatibilidade e intenção de ocupação |
| Estadia operacional             | `Estadia`, `PlanoCuidados`, `TarefaCuidado`, `RegistoCuidado`, `Nota`                                         | Registar o que acontece durante a permanência                   |
| Financeiro, clínico e controlo | `ServicoExtra`, `IntervencaoClinica`, `Pagamento`, `AlteracaoEstadoSaude`, `Colaborador`, `AuditoriaEvento` | Cobrança, saúde, autoria e rastreabilidade                     |

### 5.3 Relações fundamentais

As relações mais importantes são:

- Um `Tutor` é responsável por vários `Animal`.
- Um `Tutor` solicita várias `Reserva`.
- Um `Animal` pode ter várias `Reserva`.
- Um `Alojamento` pode ser reservado várias vezes, desde que não existam conflitos temporais.
- Uma `Reserva` origina no máximo uma `Estadia`.
- Uma `Estadia` concentra pagamentos, cuidados, serviços extra, intervenções clínicas e alterações de saúde.
- Um `PlanoCuidados` organiza várias `TarefaCuidado`.
- Um `Colaborador` é autor de registos operacionais e origina eventos de auditoria.

### 5.4 Regras de negócio expressas no diagrama

O modelo explicita três regras críticas:

- Um alojamento só está disponível se não tiver reserva sobreposta, não tiver estadia ativa, estiver limpo e for compatível com o animal.
- O pagamento de check-in cobre o valor base da estadia; o de check-out cobre valores complementares.
- A auditoria regista operações críticas como reservas, check-in, check-out, pagamentos, limpeza, serviços, intervenções e gestão de perfis.

### 5.5 Análise crítica

A separação entre `Reserva` e `Estadia` é uma das melhores decisões do modelo. Evita tratar uma intenção futura como se fosse uma ocupação real. Isto permite cancelamentos antes do check-in, validação do animal sem estadia ativa e encerramento explícito no check-out.

A separação entre `TipoServicoExtra` e `ServicoExtra` também é relevante. O primeiro é catálogo; o segundo é ocorrência real durante uma estadia. Esta distinção evita perda de histórico quando o catálogo muda.

O modelo conceptual usa `Alojamento -> EstadoLimpeza` e `Reserva -> EstadoReserva` como enumerações, o que reduz estados inválidos. A implementação deve continuar a respeitar esta disciplina, porque regras como disponibilidade e limpeza dependem fortemente desses estados.

## 6. Figura 3.1 - Diagrama de Componentes

Fonte: `docs/Etapa2/01-architecture/components.plantuml`.

### 6.1 Finalidade

O diagrama de componentes apresenta a arquitetura lógica da aplicação. Demonstra que o sistema segue um monólito Spring Boot organizado em camadas:

1. Controllers.
2. Services.
3. Repositories.
4. Base de dados MySQL.

### 6.2 Estrutura representada

O diagrama usa quatro blocos principais:

| Bloco            | Elementos                                                                                                               | Responsabilidade                                                  |
| ---------------- | ----------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------- |
| `Controllers`  | `AuthController`, `ReservaController`, `EstadiaController`, `ClinicaController`, `RelatorioController`, etc.  | Receber pedidos HTTP, validar entrada de UI, devolver vistas/DTOs |
| `Services`     | `ReservaService`, `EstadiaService`, `PagamentoService`, `AvailabilityDomainService`, `AuditoriaService`, etc. | Orquestrar casos de uso e aplicar regras de negócio              |
| `Repositories` | `ReservaRepository`, `AlojamentoRepository`, `EstadiaRepository`, etc.                                            | Encapsular acesso à persistência                                |
| `hotelanimais` | Base de dados                                                                                                           | Persistir entidades e suportar consultas                          |

### 6.3 Interpretação das dependências

As dependências fluem de cima para baixo:

`API -> Controllers -> Services -> Repositories -> DB`

Apresentacao -> Aplicacao/Negocio -> Dominio -> Dados

Esta orientação é importante. Controllers não devem aceder diretamente a repositories; repositories não devem conhecer services; entidades não devem depender da interface web. O diagrama torna visível essa fronteira.

### 6.4 Serviços de domínio e serviços transversais

Alguns serviços têm papel especialmente arquitetural:

- `AvailabilityDomainService`: protege a regra de disponibilidade, incluindo concorrência e lock pessimista.
- `RegraDominioService`: centraliza validações transversais.
- `AuditoriaOperacaoService` e `AuditoriaService`: separam a decisão de auditar da persistência dos eventos.
- `TipoAlojamentoTarifaService` e `TipoServicoExtraService`: isolam catálogos configuráveis.

### 6.5 Análise crítica

O diagrama valida a decisão de usar uma arquitetura em camadas. Isto é adequado para a escala do hotel e para a natureza CRUD/transacional da aplicação. A arquitetura favorece:

- manutenção localizada;
- testes unitários por serviço;
- controlo de permissões por controller/service;
- substituição futura da interface sem alterar o domínio;
- persistência relacional consistente.

O risco clássico deste estilo é a criação de services demasiado grandes. O próprio diagrama mostra que `EstadiaService`, `ReservaService`, `PagamentoService` e `RelatorioService` concentram fluxos de alto impacto. Estes serviços devem ser vigiados quanto a coesão, duplicação e regras espalhadas.

## 7. Diagramas de Sequência

Fontes: `docs/Etapa2/03-seq-diagrams/*.puml`.

### 7.1 Função conjunta

Os diagramas de sequência mostram o comportamento dinâmico do sistema. Enquanto o diagrama de componentes diz "quem existe", os diagramas de sequência dizem "quem fala com quem, por que ordem e com que alternativas".

A convenção comum é:

- ator inicia pedido;
- controller recebe rota HTTP;
- service aplica regras;
- repository consulta ou persiste;
- services de auditoria registam operações críticas;
- controller devolve confirmação, erro ou vista ao utilizador.

### 7.2 UC-01 - Autenticar no Sistema

Fonte: `UC-01.puml`.

Este diagrama descreve a autenticação por formulário. O `Colaborador` acede ao `AuthController` para obter o formulário e submete credenciais para o `SpringSecurity`. A autenticação é delegada ao `ColaboradorUserDetailsService`, que procura o utilizador no `ColaboradorRepository`.

Elementos críticos:

- validação de colaborador existente e ativo;
- transformação em `UserDetails` com roles;
- validação da password com BCrypt;
- redirecionamento para `/` em sucesso;
- redirecionamento para `/login?error` em falha.

Análise: o fluxo separa bem apresentação e segurança. O ponto mais importante é a verificação de conta ativa, porque permite desativar colaboradores sem apagar histórico. Isto suporta a rastreabilidade exigida pela auditoria.

### 7.3 UC-02 - Consultar Disponibilidade de Alojamentos

Fonte: `UC-02.puml`.

Este diagrama aparece na Figura 3.2 do relatório. O utilizador autorizado envia um pedido de procura de disponibilidade. O `ReservaController` obtém o animal através do `IAnimalService`, extrai a espécie e pede ao `IAlojamentoService` alojamentos disponíveis para datas e espécie. O `AlojamentoRepository` devolve alojamentos limpos, compatíveis e sem conflitos.

Regras aplicadas:

- datas de início/fim são usadas como intervalo de disponibilidade;
- espécie do animal restringe o tipo de alojamento;
- alojamento precisa estar limpo;
- reservas/estadias sobrepostas excluem o alojamento.

Análise: este fluxo é central para evitar overbooking. A consulta está corretamente posicionada em service/repository, mas a criação de reserva deve repetir a validação com lock, como acontece no UC-04. Consultar disponibilidade não basta para garantir reserva em cenário concorrente.

### 7.4 UC-03 - Registar Tutor e Animal

Fonte: `UC-03.puml`.

O diagrama divide o fluxo em dois momentos: criação do tutor e associação de animal. O `TutorAnimalController` chama `ITutorService.registar`, que valida duplicação de NIF no `TutorRepository`. Se o NIF estiver livre, o tutor é persistido. Depois, o mesmo controller regista o animal, obtendo o tutor e chamando `IAnimalService`.

Regras aplicadas:

- NIF único;
- animal sempre associado a tutor existente;
- confirmação independente de tutor e animal.

Análise: a separação em dois passos reduz o risco de criar animais sem tutor. A validação do NIF é essencial para evitar duplicação de clientes. Em termos de usabilidade, convém garantir que o segundo passo é claramente encadeado após a criação do tutor.

### 7.5 UC-04 - Criar Reserva

Fonte: `UC-04.puml`.

O `ReservaController` chama `IReservaService.criar`. O serviço obtém tutor e animal, faz uma primeira contagem de conflitos e, se não encontrar conflito, chama `AvailabilityDomainService.validarDisponivelParaReservaComLock`. Este serviço bloqueia o alojamento (`findByIdForUpdate`) e reconta conflitos antes de a reserva ser gravada.

Regras aplicadas:

- tutor e animal precisam existir;
- alojamento não pode ter reserva/estadia conflituante;
- validação com lock pessimista evita dupla reserva;
- criação bem sucedida gera evento de auditoria.

Análise: este é um dos fluxos tecnicamente mais fortes. A dupla validação, incluindo lock, responde diretamente ao risco de concorrência. A lógica de disponibilidade não fica dependente apenas da UI, o que é correto. Deve ser garantido que a validação com lock corre dentro de transação ativa.

### 7.6 UC-05 - Cancelar Reserva

Fonte: `UC-05.puml`.

O `ReservaController` recebe o pedido de cancelamento, chama `IReservaService.cancelar` e o serviço obtém a reserva com detalhes. Se a reserva não existir ou não for cancelável, devolve erro. Se estiver ativa, altera o estado para `CANCELADA`, persiste e audita.

Regras aplicadas:

- apenas reservas em estado adequado podem ser canceladas;
- cancelamento não apaga o registo;
- auditoria regista a operação.

Análise: a opção por transição de estado em vez de remoção física é consistente com histórico e auditoria. O diagrama não mostra notificação ao cliente, o que está fora do âmbito atual.

### 7.7 UC-06 - Registar Check-in

Fonte: `UC-06.puml`.

Este diagrama aparece na Figura 3.3 do relatório. O `EstadiaController` valida o método de pagamento e chama `IEstadiaService.abrirEstadiaPorReserva`. O serviço obtém a reserva, bloqueia o animal, verifica se já existe estadia em curso, confirma a reserva, cria a estadia, calcula o valor base e regista o pagamento de check-in.

Regras aplicadas:

- método de pagamento obrigatório;
- reserva tem de estar ativa;
- animal não pode ter outra estadia em curso;
- check-in cria `Estadia EM_CURSO`;
- pagamento base é calculado a partir da tarifa ativa;
- operação auditada.

Análise: o check-in agrega várias regras transacionais. O bloqueio do animal é uma boa decisão porque a exclusividade da estadia é por animal, não apenas por alojamento. O diagrama também mostra que `PlanoCuidados` é esperado no ciclo de check-in, embora a chamada explícita ao `IPlanoCuidadosService` apareça mais claramente no diagrama de classes e contratos do que neste fluxo.

### 7.8 UC-07 - Registar Check-out

Fonte: `UC-07.puml`.

O `EstadiaController` valida o método de pagamento e chama `IEstadiaService.checkOut`. O serviço obtém a estadia, valida que está em curso, chama `IPagamentoService.registrarPagamentoCheckOut`, soma custos de serviços extra e intervenções clínicas, termina a estadia, conclui a reserva e marca o alojamento como pendente de limpeza.

Regras aplicadas:

- só estadias em curso podem terminar;
- custos complementares são cobrados no check-out;
- reserva associada passa a concluída;
- alojamento fica indisponível até limpeza;
- operação auditada.

Análise: o fluxo liga corretamente finanças e logística. O ponto chave é `marcarPendenteLimpeza`, porque impede que o alojamento volte imediatamente à disponibilidade. Este UC fecha o ciclo iniciado no UC-06.

### 7.9 UC-08 - Processar Pagamento

Fonte: `UC-08.puml`.

O `PagamentoController` submete um `PagamentoDto` ao `IPagamentoService`. O serviço valida método, procura a estadia e grava o pagamento no `PagamentoRepository`, com auditoria em caso de sucesso.

Regras aplicadas:

- método de pagamento obrigatório;
- pagamento precisa estar associado a estadia existente;
- operação auditada.

Análise: é um bom caso de uso reutilizável, mas a sua relação com UC-06 e UC-07 deve permanecer clara. Se pagamentos forem criados diretamente e também por check-in/check-out, o serviço deve garantir idempotência ou impedir duplicados conforme a regra de negócio.

### 7.10 UC-09 - Registar Cuidados e Notas Operacionais

Fonte: `UC-09.puml`.

O fluxo começa com a consulta do plano de cuidados ativo. A partir daí o colaborador autorizado pode registar cuidado, concluir tarefa, adicionar nota ou registar alteração de saúde. O diagrama usa blocos `opt`, mostrando que estas ações são opcionais e independentes dentro do contexto da estadia.

Regras aplicadas:

- plano de cuidados é obtido por estadia;
- registos de cuidado associam autor e data/hora;
- tarefas do plano podem ser concluídas;
- notas ficam ligadas à reserva;
- alteração de saúde crítica pode promover prioridade do plano para `CRITICO`.

Análise: este diagrama é particularmente relevante para substituir processos em papel. A maior força é a rastreabilidade por autor. O ponto a vigiar é a coerência entre nota, alteração clínica e plano: a informação deve ficar facilmente consultável no histórico, sem duplicação confusa.

### 7.11 UC-10 - Registar Serviço Extra

Fonte: `UC-10.puml`.

O `ServicoExtraController` chama `IServicoExtraService.register`. O serviço verifica se a estadia existe e está em curso, valida o tipo de serviço extra no catálogo e grava a ocorrência. Em sucesso, regista auditoria.

Regras aplicadas:

- só estadias em curso aceitam serviços extra;
- tipo de serviço tem de existir e estar ativo;
- custo inválido é rejeitado;
- serviço extra entra na faturação complementar.

Análise: a separação catálogo/ocorrência protege o histórico financeiro. Mesmo que o catálogo mude, o serviço extra realizado deve preservar o custo praticado no momento.

### 7.12 UC-11 - Gerir Historial Clínico

Fonte: `UC-11.puml`.

O médico veterinário consulta a ficha clínica por animal. O `ClinicaController` chama `IClinicaService`, que agrega dados do animal, estadias, alterações de saúde e intervenções clínicas. Opcionalmente, o médico consulta histórico consolidado via `HistoricoController`. Para registar intervenção, o `IIntervencaoClinicaService` valida a estadia e o perfil antes de persistir e auditar.

Regras aplicadas:

- histórico clínico é agregado por animal;
- intervenção com custo exige estadia em curso;
- autoria médica é registada;
- operação clínica é auditada.

Análise: o diagrama separa bem consulta clínica e registo de intervenção. Isto é importante porque consultar histórico pode acontecer fora de uma estadia ativa, mas registar custos clínicos associados à estadia não.

### 7.13 UC-12 - Registar Limpeza de Alojamento

Fonte: `UC-12.puml`.

O responsável pela limpeza consulta `/limpeza`, obtendo alojamentos com estado pendente. Depois marca um alojamento como limpo. O `ILimpezaService` procura o alojamento, atualiza o estado para concluído e audita.

Regras aplicadas:

- apenas alojamentos pendentes aparecem na fila de trabalho;
- alojamento inexistente gera erro;
- alojamento limpo volta a poder entrar em disponibilidade;
- limpeza é auditada.

Análise: este fluxo é simples, mas crítico. Sem ele, a regra de disponibilidade ficaria incompleta. O diagrama mostra que limpeza não é apenas uma tarefa operacional; é uma transição de estado que desbloqueia inventário.

### 7.14 UC-13 - Consultar Dashboard e Gerar Relatórios

Fonte: `UC-13.puml`.

O diretor consulta o dashboard, que agrega métricas de reservas, estadias, ocupação, faturação e pagamentos. Para relatórios, o `RelatorioController` valida o limite de período. Se o período for válido, o `IRelatorioService` consulta repositories, gera resumo e regista auditoria. A exportação CSV/PDF reutiliza a agregação.

Regras aplicadas:

- acesso restrito ao diretor;
- relatórios síncronos limitados a 3 meses;
- agregação usa reservas, estadias, pagamentos e serviços extra;
- geração e exportação são auditadas.

Análise: o limite de 3 meses é uma decisão operacional sensata para evitar pedidos pesados em ambiente síncrono. A reutilização da agregação para web, CSV e PDF reduz divergência entre formatos. O relatório não precisa de tabela própria, porque deriva de dados operacionais.

### 7.15 UC-14 - Gerir Colaboradores e Perfis

Fonte: `UC-14.puml`.

O diretor lista colaboradores, cria novos utilizadores e pode editar ou desativar contas. O serviço valida duplicação de username/email, codifica password com `PasswordEncoder`, grava colaborador e audita a operação.

Regras aplicadas:

- gestão restrita ao diretor;
- username/email únicos;
- passwords codificadas;
- desativação preserva histórico;
- operações administrativas auditadas.

Análise: o fluxo reforça RBAC e rastreabilidade. A desativação é preferível à eliminação física porque evita quebrar autoria de eventos antigos.

### 7.16 UC-15 - Gerir Tarifas e Catálogos

Fonte: `UC-15.puml`.

O diretor gere tarifas por tipo de alojamento e tipos de serviço extra. O diagrama mostra listagem, criação, validação de duplicados/tarifa negativa e operações de ativar/desativar.

Regras aplicadas:

- tarifa diária não pode ser negativa;
- tipo de alojamento ou serviço não deve ser duplicado;
- itens podem ser desativados ou reativados;
- catálogo suporta faturação e registo operacional.

Análise: este UC reduz valores fixos no código. A gestão por catálogo torna o sistema mais adaptável a alterações comerciais. Deve existir cuidado para que alterações de tarifa não alterem retroativamente valores já faturados.

### 7.17 UC-16 - Consultar Auditoria

Fonte: `UC-16.puml`.

O diretor acede à auditoria com filtros. O `AuditoriaController` normaliza período e filtros, chama `IAuditoriaService`, que constrói uma `Specification` e consulta o `AuditoriaRepository`. A exportação CSV reutiliza a consulta filtrada.

Regras aplicadas:

- acesso restrito ao diretor;
- filtros por utilizador, operação, entidade e resultado;
- paginação na consulta;
- exportação CSV dos eventos filtrados.

Análise: o uso de `Specification` é adequado para filtros combináveis. A auditoria deve preservar imutabilidade dos eventos e limitar exportações demasiado grandes. A consulta paginada é essencial para RNF de desempenho.

## 8. Figura 3.4 - Diagrama de Classes

Fontes: `docs/Etapa2/02-class-diagram/class-diagram-domain.puml` e `docs/Etapa2/02-class-diagram/class-diagram.puml`.

### 8.1 Finalidade

O diagrama de classes detalha a estrutura estática da solução. Existem duas leituras complementares:

- `class-diagram-domain.puml`: foca entidades e enumerações de domínio.
- `class-diagram.puml`: acrescenta controllers, interfaces de serviço, serviços concretos, repositories, DTOs, configuração e componentes de auditoria.

### 8.2 Entidades principais

As entidades persistentes incluem:

`Colaborador`, `Tutor`, `Animal`, `Alojamento`, `Reserva`, `Estadia`, `Pagamento`, `RegistoCuidado`, `PlanoCuidados`, `TarefaCuidado`, `Nota`, `ServicoExtra`, `TipoServicoExtra`, `IntervencaoClinica`, `AlteracaoEstadoSaude`, `TipoAlojamentoTarifa` e `AuditoriaEvento`.

### 8.3 Enumerações

As enumerações controlam estados e classificações:

`TipoColaborador`, `Especie`, `EstadoSaude`, `EstadoLimpeza`, `EstadoReserva`, `EstadoEstadia`, `MetodoPagamento`, `MomentoPagamento`, `EstadoPagamento`, `PrioridadePlano`, `PeriodicidadeTarefa` e `ResultadoAuditoria`.

### 8.4 Relações de domínio

O diagrama destaca:

- agregação de `Animal` e `Reserva` por `Tutor`;
- associação de `Reserva` a `Animal`, `Tutor` e `Alojamento`;
- relação `Reserva 1 -> 0..1 Estadia`;
- `Estadia` como centro operacional para pagamentos, registos, serviços, clínica e saúde;
- composição entre `PlanoCuidados` e `TarefaCuidado`;
- `AuditoriaEvento` associado a `Colaborador`.

### 8.5 Camadas técnicas no diagrama completo

O diagrama completo mostra que:

- controllers dependem de interfaces de services;
- services concretos dependem de repositories e services internos;
- repositories dependem das entidades;
- DTOs isolam a camada web das entidades JPA;
- `SecurityConfig`, `ColaboradorUserDetailsService` e `PasswordEncoder` suportam autenticação;
- auditoria tem service, operação service e job de limpeza/retenção.

### 8.6 Análise crítica

O diagrama completo é valioso para manutenção, mas é denso. Para leitura pedagógica, o diagrama de domínio é melhor; para análise técnica, o completo é mais útil. A existência de DTOs é uma decisão positiva porque evita expor entidades JPA diretamente na interface.

Ponto de atenção: quando o diagrama completo inclui muitas dependências `..>`, pode tornar-se difícil perceber as relações mais importantes. Para futuras entregas, pode ser útil manter duas versões: uma de domínio e outra por módulo técnico.

## 9. Figura 3.5 - Diagrama de Deployment

Fonte: `docs/Etapa2/01-architecture/deployment-view.puml`.

### 9.1 Finalidade

O deployment mostra como a aplicação é distribuída fisicamente no ambiente do hotel.

### 9.2 Estrutura

O diagrama representa:

- rede local do hotel;
- postos de trabalho da receção, direção, cuidados, veterinário e limpeza;
- cada posto com apenas um browser;
- servidor/host Docker;
- contentor `hotelanimais-app` com `app.jar` e recursos web;
- contentor `hotelanimais-db` com base de dados `hotelanimais`;
- comunicação HTTP na porta 8080;
- comunicação interna para MySQL na porta 3306.

### 9.3 Decisões expressas

O sistema é centralizado. Os postos não instalam aplicação própria; apenas acedem via browser. A base de dados não fica exposta aos postos, apenas à aplicação.

### 9.4 Análise crítica

A solução é adequada para um hotel de pequena/média dimensão, porque simplifica instalação e manutenção. O principal risco é o ponto único de falha: se o servidor falhar, todos os postos deixam de aceder ao sistema.

Mitigações recomendadas:

- backups automáticos da base de dados;
- reinício automático da aplicação;
- UPS no servidor;
- monitorização de `/actuator/health`;
- procedimento de recuperação documentado;
- eventual réplica ou backup externo numa evolução futura.

## 10. Figura 3.6 - Modelo lógico-relacional

Fonte: `docs/Etapa2/02-class-diagram/logical-data-model.plantuml`.

### 10.1 Finalidade

O modelo lógico-relacional traduz o domínio para tabelas, chaves primárias, chaves estrangeiras, unicidades, enumerações e índices. É a ponte entre o modelo conceptual e a implementação MySQL/Flyway.

### 10.2 Módulos de dados

O relatório organiza o modelo em cinco módulos:

| Módulo                            | Tabelas principais                                                                                     | Função                                      |
| ---------------------------------- | ------------------------------------------------------------------------------------------------------ | --------------------------------------------- |
| Atores e perfis                    | `colaborador`, `tutor`                                                                             | Identidade, autenticação, RBAC e clientes   |
| Entidades biológicas e alojamento | `animal`, `alojamento`, `tipo_alojamento_tarifa`                                                 | Hóspedes e inventário físico               |
| Transação                        | `reserva`, `estadia`, `pagamento`, `nota`                                                      | Ciclo reserva-estadia-faturação             |
| Operações e cuidados             | `plano_cuidados`, `tarefa_cuidado`, `registo_cuidado`, `servico_extra`, `tipo_servico_extra` | Rotina diária e serviços                    |
| Clínica e auditoria               | `intervencao_clinica`, `alteracao_estado_saude`, `auditoria_evento`                              | Saúde, responsabilização e rastreabilidade |

### 10.3 Restrições importantes

O modelo inclui:

- `username` e `email` únicos em `colaborador`;
- `nif` único em `tutor`;
- `identificacao` única em `alojamento`;
- `animal.tutor_id`;
- `reserva` ligada a tutor, animal e alojamento;
- `plano_cuidados.estadia_id` único;
- custos e tarifas com valores não negativos;
- índices para consultas por estadia, animal, plano, reserva e auditoria.

### 10.4 Análise crítica

O modelo relacional é compatível com Spring Data JPA e com relatórios derivados por consulta. A decisão de não criar tabelas específicas para relatórios é correta: relatórios são vistas agregadas sobre dados existentes.

Pontos de atenção:

- O ficheiro PlantUML representa `reserva ||--|| estadia`, mas o texto do relatório e o modelo conceptual indicam `Reserva 1 -> 0..1 Estadia`. A interpretação correta para o negócio é: uma estadia exige reserva, mas uma reserva pode nunca originar estadia.
- O ficheiro também representa `estadia ||--|| pagamento`, enquanto o domínio admite múltiplos pagamentos por estadia. Deve ser confirmada a regra final: se existir pagamento de check-in e eventual check-out, a cardinalidade lógica deveria permitir mais do que um pagamento por estadia, ou então o pagamento deve agregar tipos/momentos num único registo.
- A relação `nota ||--o{ alteracao_estado_saude` no PlantUML parece mais interpretativa do que estrutural. Se não existir FK real, deve ser tratada como relação funcional/documental.

Estes pontos não invalidam o modelo, mas devem ser revistos para evitar divergência entre documentação e implementação.

## 11. Figuras 3.7 e 6.16 - Protótipos e wireframes

Fontes: `docs/Etapa2/05-ui-interface-mockup/*.html`.

### 11.1 Finalidade

Os protótipos materializam a navegação esperada e validam a correspondência entre casos de uso e ecrãs. Não são diagramas UML, mas são artefactos de design importantes.

### 11.2 Ecrãs principais

Os ficheiros HTML cobrem:

- login (`UC-01`);
- dashboard (`UC-13`);
- reservas e disponibilidade (`UC-02`, `UC-04`);
- plano de cuidados (`UC-09`);
- histórico clínico (`UC-11`);
- limpeza (`UC-12`);
- colaboradores (`UC-14`);
- disponibilidade (`UC-02`);
- ecrãs complementares mencionados no PDF: auditoria, check-in, check-out, clínica, animal, início, estadias, plano de cuidados do turno, relatórios e tutor.

### 11.3 Análise crítica

Os protótipos servem duas funções: validação com stakeholders e guia para implementação Thymeleaf. Como usam AdminLTE/Bootstrap, estão alinhados com a decisão de interface server-side.

O risco principal é os protótipos ficarem desatualizados face à implementação. Para mitigar isto, cada ecrã deveria manter uma correspondência clara com:

- caso de uso;
- controller;
- rota;
- perfil autorizado;
- DTO principal.

## 12. Análise transversal de rastreabilidade

### 12.1 Rastreabilidade entre requisitos e diagramas

| Requisito / preocupação     | Diagramas onde aparece                                |
| ----------------------------- | ----------------------------------------------------- |
| Autenticação e perfis       | UC-01, casos de uso, componentes, classes, deployment |
| Disponibilidade e overbooking | UC-02, UC-04, domínio, dados, classes                |
| Registo de tutores e animais  | UC-03, domínio, dados, classes, protótipos          |
| Ciclo de reserva              | UC-04, UC-05, domínio, classes, dados                |
| Check-in/check-out            | UC-06, UC-07, domínio, classes, dados                |
| Pagamentos                    | UC-08, UC-06, UC-07, domínio, dados, classes         |
| Cuidados e notas              | UC-09, domínio, classes, dados, protótipos          |
| Serviços extra               | UC-10, UC-07, domínio, dados                         |
| Clínica                      | UC-11, UC-09, domínio, dados, protótipos            |
| Limpeza                       | UC-12, UC-07, domínio, dados, deployment             |
| Relatórios                   | UC-13, componentes, classes, dados, protótipos       |
| Colaboradores                 | UC-14, classes, dados, casos de uso                   |
| Catálogos                    | UC-15, classes, dados                                 |
| Auditoria                     | UC-16, UC-04 a UC-14, classes, dados                  |

### 12.2 Coerência arquitetural

Os diagramas são coerentes com a decisão de monólito em camadas:

- casos de uso identificam funcionalidades;
- componentes identificam camadas;
- sequência confirma chamadas controller -> service -> repository;
- classes concretizam entidades, services e DTOs;
- deployment mantém tudo centralizado;
- modelo relacional suporta persistência transacional.

Esta coerência é um bom indicador de maturidade documental. Não há indícios de uma arquitetura descrita de uma forma e implementada noutra.

### 12.3 Pontos fortes

- Forte separação entre `Reserva` e `Estadia`.
- Validação de disponibilidade com lock pessimista no momento crítico de criação da reserva.
- Auditoria modelada como preocupação transversal.
- RBAC aparece desde casos de uso até implementação.
- Catálogos evitam valores fixos para tarifas e serviços extra.
- DTOs evitam exposição direta de entidades.
- Deployment simples e adequado à escala do problema.

### 12.4 Riscos e pontos de melhoria

1. Cardinalidades de pagamento e estadia devem ser uniformizadas entre domínio, modelo lógico e implementação.
2. A criação automática do plano de cuidados no check-in deve ficar explícita nos diagramas de sequência se for regra obrigatória.
3. Serviços centrais como `EstadiaService`, `ReservaService`, `PagamentoService` e `RelatorioService` devem ser monitorizados para evitar baixa coesão.
4. A auditoria deve ter política clara de retenção, exportação e limpeza para não degradar desempenho.
5. O deployment centralizado deve ser acompanhado por backups e recuperação operacional.
6. Os protótipos devem manter rastreabilidade com rotas e controllers reais.

## 13. Conclusão

Os diagramas do `LI4-G08.pdf` formam um conjunto consistente e progressivo de documentação técnica. O relatório começa por representar planeamento e requisitos, passa para domínio e arquitetura, detalha fluxos com diagramas de sequência, concretiza estrutura com classes e modelo relacional, e fecha com implantação e protótipos.

A análise dos ficheiros PlantUML da Etapa2 confirma que a arquitetura foi pensada de forma alinhada com a implementação Spring Boot: controladores recebem pedidos, serviços aplicam regras, repositórios persistem dados e a auditoria atravessa operações críticas. As principais melhorias recomendadas são de refinamento documental, não de reformulação: uniformizar cardinalidades, tornar algumas regras transversais mais explícitas nos diagramas de sequência e manter os protótipos sincronizados com a aplicação real.
