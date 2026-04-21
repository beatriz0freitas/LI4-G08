# Research: Arquitetura do Projeto

## Objetivo
Definir e documentar a arquitetura global de um sistema de gestão de hotel para animais (cães e gatos),
produzindo todos os diagramas UML necessários para suportar a Etapa 2 do projeto LI4-G08.
Os diagramas servem de base para implementação, onboarding e revisão arquitetural.

## Contexto do Sistema
- **Nome**: Sistema de Gestão de Hotel para Animais (cães e gatos)
- **Tipo**: Web App com arquitetura em 3 camadas (Presentation → Business Logic → Data Access)
- **Utilizadores**: Rececionista, Veterinário, Cuidador, Administrador, Responsável de Limpeza
- **Domínio principal**: Gestão de reservas e alojamentos, estadias, cuidados diários,
  serviços extra, faturação e pagamentos

### Entidades de Domínio Identificadas

- `Tutor` — responsável pelo animal; efetua reservas e é o ponto de contacto durante a estadia
- `Colaborador` — utilizador do sistema; pode assumir o papel de Rececionista, Veterinário
  ou Cuidador conforme as permissões atribuídas

- `Alojamento` — espaço físico do hotel; tem tipologia (canino ou felino) e estado de limpeza
  (enumerado); só está disponível quando limpeza concluída e sem estadia ativa
- `Reserva` — compromisso formal futuro; associa Tutor + Animal + Alojamento com datas definidas;
  pode originar no máximo uma Estadia
- `Estadia` — concretização de uma Reserva; associa Animal + Alojamento num intervalo temporal
  (check-in / check-out); entidade agregadora de toda a informação dinâmica da permanência
- `Cuidado` — ação executada por um Cuidador durante uma Estadia (alimentação, medicação,
  higiene, atividade); tem data, hora e colaborador responsável
- `Nota` — registo de comunicação entre turnos, associado a uma Estadia

- `Animal` — entidade central; espécie limitada a cão ou gato; tem raça, idade, estado de saúde
  e necessidades específicas; um Tutor pode ter um ou mais Animais
- `HistorialClínico` — agrega todas as IntervençõesClínicas de um Animal ao longo do tempo
- `IntervençãoClínica` — intervenção médica registada no historial do Animal

- `ServiçoExtra` — serviço ou intervenção com impacto financeiro (banho, tosquia, passeio,
  medicação prescrita, etc.); registado por Cuidador ou Veterinário; associado a uma Estadia
- `Fatura` — agrega o custo base do alojamento e os ServiçosExtra de uma Estadia;
  cada Estadia tem exatamente uma Fatura
- `Pagamento` — registo financeiro de liquidação; uma Fatura pode ter múltiplos Pagamentos
  (diferentes métodos/estados)

### Fluxos Principais do Sistema
1. **Reserva** — Tutor solicita reserva; sistema verifica disponibilidade do Alojamento;
   Reserva é criada e confirmada
2. **Check-in** — Rececionista regista chegada do Animal; Estadia é iniciada;
   Alojamento fica ocupado
3. **Check-out** — Rececionista encerra Estadia; Fatura é gerada com custo base +
   ServiçosExtra; Alojamento fica pendente de limpeza
4. **Registo de Cuidado** — Cuidador regista ação diária (Cuidado) associada à Estadia
5. **Registo de Nota** — Colaborador regista nota de turno associada à Estadia
6. **Serviço Extra** — Cuidador ou Veterinário regista ServiçoExtra com impacto na Fatura
7. **Intervenção Veterinária** — Veterinário regista IntervençãoClínica no HistorialClínico
   do Animal e, se aplicável, um ServiçoExtra na Estadia
8. **Limpeza** — Colaborador atualiza estado de limpeza do Alojamento após check-out;
   quando concluída, Alojamento fica disponível
9. **Faturação/Pagamento** — Fatura é calculada no check-out; Pagamento(s) são registados

### Regras de Negócio Críticas
- Um Alojamento só está disponível se: limpeza concluída **E** sem Estadia ativa **E** sem Reserva confirmada para o período
- Alojamentos só acolhem a espécie para a qual estão tipificados (canino ou felino)
- Uma Reserva origina no máximo uma Estadia
- Cada Estadia tem exatamente uma Fatura
- Uma Fatura pode ter múltiplos Pagamentos (suporta métodos diferentes e liquidação parcial)
- Cada Animal deve estar associado a pelo menos um Tutor
- Um Animal não pode ter duas Estadias ativas simultaneamente
- Uma Reserva cancelada não pode ser reativada; deve ser criada uma nova
- Custo de ServiçoExtra e IntervençãoClínica registados no momento da ocorrência; imutáveis após check-out
- Pagamento no check-in cobre só a estadia; ServiçosExtra cobrados no check-out
- Check-out só pode ser realizado após check-in ter sido registado


### Classes e Estruturas a Adicionar

#### 1. Camada de Serviços (Services)
Conforme obrigatoriedade da spec — classes responsáveis pela orquestração da lógica de negócio:

- `ReservaService`: Operações CRUD de Reservas, verificação de disponibilidade, cálculo de datas
- `EstadiaService`: Operações CRUD de Estadias, sincronização com Reserva, gestão de ciclo de vida
- `CuidadoService`: Registo e consulta de Cuidados associados a uma Estadia
- `AlojamentoService`: Verificação de disponibilidade (3 condições cumulativas), atualização de estado
- `FaturacaoService`: Cálculo de Fatura, agregação de ServiçosExtra e IntervencionesClinicas, gestão de Pagamentos
- `AnimalService`: Operações CRUD de Animais, consulta de HistorialClínico
- `UtilizadorService`: Gestão de Utilizadores, Tutores, autenticação, perfis de acesso
- `RelatorioService`: Geração de Relatórios, consulta de indicadores (taxa ocupação, faturação)

#### 2. Camada de Repositórios (Data Access)
Classes que encapsulam acesso à base de dados:

- `ReservaRepository`: CRUD de Reservas, query por período/estado/tutor
- `EstadiaRepository`: CRUD de Estadias, query por período/animal/alojamento
- `AlojamentoRepository`: CRUD de Alojamentos, query por disponibilidade/tipo
- `AnimalRepository`: CRUD de Animais, query por tutor/espécie
- `UtilizadorRepository`: CRUD de Utilizadores, query por perfil/username
- `FaturaRepository`: CRUD de Faturas, query por período/estado
- `CuidadoRepository`: CRUD de Cuidados, query por estadia/data
- `TutorRepository`: CRUD de Tutores, query por NIF/email

#### 3. Hierarquia de Utilizadores (Clarificação)
Distinção explícita entre "cliente" (Tutor) e "staff" (Colaborador):

**Utilizador** (classe abstrata/raiz):
- `id : Long`
- `ativo : Boolean`
- `criadoEm : DateTime`
- `atualizadoEm : DateTime`

**Tutor** (estende Utilizador):
- `nome : String`
- `nif : String` (identificador único)
- `email : String` (obrigatório para contacto — RF-04)
- `contacto : String` (telefone)
- `endereco : String`
- `possuiAnimais : Collection<Animal>`

**Colaborador** (estende Utilizador):
- `nome : String`
- `email : String`
- `contacto : String`
- `perfilAcesso : PerfilAcesso` (enum: Diretor, Rececionista, Cuidador, Veterinario, ResponsavelLimpeza)
- Métodos: `temPermissao(operacao : String) : Boolean`

#### 4. Expansão de Atributos — Animal
Conforme RF-04 (Registo de tutores e animais):

**Animal**:
- `id : Long` (existente)
- `nome : String` (existente)
- `especie : Especie` (enum: Cao, Gato — não String)
- `raca : String` (existente)
- **`dataNascimento : Date`** (novo — obrigatório)
- **`peso : Double`** (novo — obrigatório)
- **`estadoSaude : String`** (novo — obrigatório; ex: "Saudável", "Com alergias", "Medicação contínua")
- **`necessidadesAlimentares : String`** (novo — obrigatório)
- **`medicacaoEmCurso : String`** (novo — obrigatório; field descriptivo ou relação a Prescricao)
- `necessidadesEspeciais : String` (existente; renomear para clareza)
- `tutores : Collection<Tutor>` (1 tutor mínimo)
- `historicalClinico : HistorialClinico` (1-para-1)

#### 5. Expansão de Atributos — Tutor
Conforme RF-04:

**Tutor**:
- `id : Long`
- `nome : String` (existente)
- `nif : String` (existente)
- **`email : String`** (novo — obrigatório para contacto)
- `contacto : String` (existente)
- **`endereco : String`** (novo — recomendado)
- `animais : Collection<Animal>`

#### 6. Expansão de Atributos — Reserva
Ligação direta a Tutor e clareza de estado:

**Reserva**:
- `id : Long`
- `codigo : String` (único)
- **`tutor : Tutor`** (novo — relação direta para consultas rápidas)
- `animal : Animal`
- `alojamento : Alojamento`
- `dataInicio : Date`
- `dataFim : Date`
- `estado : EstadoReserva` (enum: Confirmada, Cancelada)
- `criadaEm : DateTime`
- `atualizadaEm : DateTime`
- `estadia : Estadia` (0..1 — linkagem bidirecional para rastreabilidade)

#### 7. Expansão de Atributos — Estadia
Auditoria e rastreabilidade:

**Estadia**:
- `id : Long`
- `codigo : String` (único)
- `reserva : Reserva` (1-para-1)
- `animal : Animal`
- `alojamento : Alojamento`
- `dataCheckIn : DateTime` (new — preciso vs Date)
- `dataCheckOut : DateTime` (new — preciso vs Date)
- `estado : EstadoEstadia` (enum: EmCurso, Encerrada)
- `fatura : Fatura` (1-para-1)
- **`criadoPor : Colaborador`** (novo — auditoria: quem fez check-in)
- **`atualizadoPor : Colaborador`** (novo — auditoria: quem fez check-out)
- **`cuidadosPlaneados : Collection<ItemPlanoCuidado>`** (novo — referência ao plano ativo)
- `cuidados : Collection<Cuidado>` (0..*)
- `notas : Collection<Nota>` (0..*)
- `servicosExtra : Collection<ServicoExtra>` (0..*)
- `criadaEm : DateTime`
- `encerradaEm : DateTime`

#### 8. Expansão de Atributos — Fatura
Estado financeiro completo:

**Fatura**:
- `id : Long`
- `numero : String` (único, sequencial)
- `estadia : Estadia` (1-para-1)
- `dataEmissao : DateTime`
- `valorBase : Double` (custo da estadia = dias × (preco/dia do alojamento))
- **`desconto : Double`** (novo — suporta descontos/promoções; default 0)
- `total : Double` (calculado = valorBase + servicosExtra - desconto)
- **`estado : EstadoFatura`** (novo — enum: Emitida, Paga, ParcialmPendente; rastreia status financeiro real)
- **`observacoes : String`** (novo — notas sobre faturação ou impagos)
- `pagamentos : Collection<Pagamento>` (0..*)
- `criadaEm : DateTime`

#### 9. Expansão de Atributos — Pagamento
Rastreabilidade de liquidação:

**Pagamento**:
- `id : Long`
- `fatura : Fatura`
- `valor : Double`
- `metodo : MetodoPagamento` (enum: Numerario, Cartao, MBWay, Transferencia)
- `estado : EstadoPagamento` (enum: Liquidado, Pendente)
- **`dataVencimento : Date`** (novo — para pagamentos pendentes)
- **`dataPagamento : DateTime`** (novo — quando foi pago, null se pendente)
- **`motivo : String`** (novo — opcional; se pendente, explicar por quê)
- `criadoEm : DateTime`

#### 10. Expansão de ServicoExtra
Tipificação e rastreabilidade:

**ServicoExtra**:
- `id : Long`
- `estadia : Estadia` (0..*)
- **`tipo : TipoServicoExtra`** (novo — enum: Banho, Passeio, Tosquia, MedicacaoPrescrita, Outro — em vez de String)
- `descricao : String` (novo — detalhe da execução)
- `custo : Double`
- **`registadoPor : Colaborador`** (novo — quem registou; pode ser Cuidador ou Veterinario)
- **`data : DateTime`** (novo — quando foi realizado)
- `criadoEm : DateTime`
- Métodos: `podeSerAlterado() : Boolean` (validar regra RD-09 — imutável após check-out)

#### 11. Classe EventoAuditoria (Novo)
Para rastreabilidade completa conforme RF-05:

**EventoAuditoria**:
- `id : Long`
- `tipoEvento : TipoEvento` (enum: CRIACAO, ATUALIZACAO, DELECAO, CHECK_IN, CHECK_OUT, PAGAMENTO_REGISTADO, etc.)
- `entidadeAfetada : String` (nome da classe: "Reserva", "Estadia", etc.)
- `idEntidade : Long` (ID da instância modificada)
- `utilizador : Colaborador`
- `alteracoes : String` (JSON com campo → {anterior, novo})
- `dataHora : DateTime`
- `ipOrigem : String` (opcional, para rastreamento)
- `detalhes : String` (campo livre para contexto adicional)

#### 12. Classe/Enum para Cuidado Expandida
Ligação clara a responsável:

**Cuidado**:
- `id : Long`
- `estadia : Estadia`
- `tipo : TipoCuidado` (enum: Alimentacao, Medicacao, Higiene, Atividade, Observacao)
- `descricao : String`
- `dataHora : DateTime`
- **`realizadoPor : Colaborador`** (novo — apenas Cuidador; validar em Service)
- `criadoEm : DateTime`
- Métodos: `pertenceAoMesmoTurno(outroCuidado : Cuidado) : Boolean`

#### 13. Classe Nota Expandida
Contexto de turno:

**Nota**:
- `id : Long`
- `estadia : Estadia`
- `descricao : String`
- `dataHora : DateTime`
- **`turno : Turno`** (novo — enum ou relação: Matutino, Vespertino, Noturno)
- **`criadaPor : Colaborador`** (novo — rastreabilidade)
- `criadaEm : DateTime`

#### 14. Classe Prescrição Expandida (Vinculada a Cuidados)
Ligação entre prescricao e aplicação:

**Prescricao**:
- `id : Long`
- `intervencaoClinica : IntervencaoClinica`
- `descricao : String`
- `dataInicio : Date`
- `dataFim : Date` (opcional — if null, indefinida)
- **`aplicacoes : Collection<AplicacaoPrescricao>`** (novo — rastrear cada dose/aplicação)

**AplicacaoPrescricao** (novo):
- `id : Long`
- `prescricao : Prescricao`
- `dataHora : DateTime`
- `aplicadoPor : Colaborador` (Cuidador ou Veterinário)
- `observacoes : String`

#### 15. Classe Alojamento com Método de Disponibilidade
Encapsular lógica de RF-06:

**Alojamento**:
- `id : Long`
- `tipo : TipoAlojamento` (enum: Canino, Felino)
- `especieSuportada : Especie` (enum: Cao, Gato — não String)
- `estadoLimpeza : EstadoLimpeza` (enum: Pendente, EmLimpeza, Concluido)
- `numero : String` (identificador legível: "Box-01", "Canil-A", etc.)
- `capacidade : int` (futura expansão para multi-ocupação)
- `precoDiario : Double` (novo — para cálculo de Fatura; pode variar por tipo)
- **`estaDisponivel(dataInicio : Date, dataFim : Date) : Boolean`** (novo — método que valida as 3 condições: limpeza + sem estadia + sem reserva)
- **`estaDisponivel() : Boolean`** (novo — sobrecarga para verificação instantânea)
- `registosLimpeza : Collection<RegistoLimpeza>`

#### 16. Classe IntervencaoClinica Expandida
Auditoria e linkagem a ServicoExtra:

**IntervencaoClinica**:
- `id : Long`
- `historicalClinico : HistorialClinico`
- `dataHora : DateTime`
- `descricao : String`
- **`realizadoPor : Colaborador`** (novo — identificar veterinário)
- `custo : Double`
- `prescricoes : Collection<Prescricao>`
- **`servicoExtraAssociado : ServicoExtra`** (novo — 0..1 linkagem; se há custo, pode gerar ServicoExtra na Estadia ativa)
- `criadoEm : DateTime`

#### 17. Classe/Enum para Turno (Opcional mas Recomendado)
Agregação de atuações por período:

**Turno** (enum ou classe):
- Se enum: `{Matutino, Vespertino, Noturno}`
- Se classe agregadora:
  - `id : Long`
  - `data : Date`
  - `tipo : TipoTurno`
  - `cuidadosRegistados : Collection<Cuidado>`
  - `notasRegistadas : Collection<Nota>`
  - `responsavel : Colaborador` (chefe de turno, opcional)

#### 18. Enumerações a Corrigir/Expandir

**Especie** (novo — corrigir Animal.especie):
- `Cao`, `Gato`

**TipoAlojamento** (novo):
- `Canino`, `Felino`

**EstadoLimpeza** (expandir de 2 para 3 estados):
- `Pendente`, `EmLimpeza`, `Concluido`

**EstadoReserva** (existente):
- `Confirmada`, `Cancelada`

**EstadoEstadia** (existente):
- `EmCurso`, `Encerrada`

**EstadoFatura** (novo):
- `Emitida`, `Paga`, `ParcialmPendente`

**MetodoPagamento** (existente):
- `Numerario`, `Cartao`, `MBWay`, `Transferencia`

**EstadoPagamento** (existente):
- `Liquidado`, `Pendente`

**PerfilAcesso** (existente):
- `Diretor`, `Rececionista`, `Cuidador`, `Veterinario`, `ResponsavelLimpeza`

**TipoCuidado** (novo):
- `Alimentacao`, `Medicacao`, `Higiene`, `Atividade`, `Observacao`

**TipoServicoExtra** (novo):
- `Banho`, `Passeio`, `Tosquia`, `MedicacaoPrescrita`, `Outro`

**TipoEvento** (novo — para auditoria):
- `CRIACAO`, `ATUALIZACAO`, `DELECAO`, `CHECK_IN`, `CHECK_OUT`, `PAGAMENTO_REGISTADO`, `SERVICO_EXTRA_REGISTADO`, `LIMPEZA_CONCLUIDA`, etc.

**TipoTurno** (novo — se Turno for classe):
- `Matutino`, `Vespertino`, `Noturno`

### Métodos de Negócio a Adicionar (com visibilidade UML)

#### Services
```
ReservaService:
+ criarReserva(tutor, animal, alojamento, dataInicio, dataFim) : Reserva
+ confirmarReserva(reserva) : void
+ cancelarReserva(reserva) : void
+ consultarDisponibilidade(alojamento, dataInicio, dataFim, especie) : Boolean
- validarDatas(dataInicio, dataFim) : Boolean
- validarEspecie(animal, alojamento) : Boolean

EstadiaService:
+ iniciarEstadia(reserva, colaborador) : Estadia
+ encerrarEstadia(estadia, colaborador) : void
+ consultarEstAtivasPor(animal) : Collection<Estadia>
- calcularTotalFatura(estadia) : Double

AlojamentoService:
+ verificarDisponibilidade(alojamento, dataInicio, dataFim) : Boolean
+ atualizarEstadoLimpeza(alojamento, novoEstado) : void
+ consultarAlojamentosDisp(especie) : Collection<Alojamento>

FaturacaoService:
+ gerarFatura(estadia) : Fatura
+ adicionarServicoExtra(estadia, servicoExtra) : void
+ registarPagamento(fatura, pagamento) : void
+ calcularTotal(fatura) : Double

AnimalService:
+ criarAnimal(tutor, dados) : Animal
+ consultarHistorialClinico(animal) : HistorialClinico
+ validarEspecie(especie) : Boolean

UtilizadorService:
+ autenticar(username, password) : Utilizador
+ criarColaborador(dados, perfil) : Colaborador
+ validarPermissao(utilizador, operacao) : Boolean
```

#### Classes de Domínio
```
Alojamento:
+ estaDisponivel(dataInicio, dataFim) : Boolean
+ estaDisponivel() : Boolean
+ suportaEspecie(especie) : Boolean
+ podeSerReservado() : Boolean

Reserva:
+ podeIniciarEstadia() : Boolean
+ estaCancelada() : Boolean
+ estáVencida() : Boolean

Estadia:
+ foiEncerrada() : Boolean
+ temPagamentoPendente() : Boolean

Fatura:
+ estaQuitada() : Boolean
+ temSaldoPendente() : Double
+ podeSerAlterada() : Boolean

Colaborador:
+ temPermissao(operacao) : Boolean
+ ehVeterinario() : Boolean
+ ehCuidador() : Boolean
+ ehResponsavelLimpeza() : Boolean
```

## Decisões Tomadas
- Utilizar PlantUML e/ou Mermaid para todos os diagramas.
- Diagramas devem ser completos: incluir todas as classes, métodos, atributos e relações relevantes.
- Diagramas de sequência devem cobrir os principais fluxos do sistema (ex: reserva, check-in, check-out, registo de cuidados, faturação).
- Diagramas de componentes devem mostrar a separação lógica (ex: módulos de reservas, gestão de estadias, faturação, histórico clínico, etc.).
- Todos os diagramas e decisões ficam em docs/architecture/.

### Sobre os Diagramas
- **Diagrama de Classes**: representa o modelo de domínio completo com todas as entidades identificadas,
  atributos tipados, métodos com visibilidade UML, enumerações e todas as relações com cardinalidades.
  Inclui as camadas Service (lógica de negócio) e Repository (acesso a dados).
  Segue todas as melhorias identificadas neste documento.
- **Diagramas de Sequência**: modelam a lógica de negócio interna (design-level, Sommerville Cap. 7)
  — participantes são objetos de domínio e serviços. **Sem HTTP, sem Controllers.**
  Os endpoints REST ficam na especificação da API (`docs/api/`).
- **Diagrama de Componentes**: mostra os módulos lógicos e as camadas da aplicação.
- **Diagrama de Casos de Uso**: clarifica atores e funcionalidades a alto nível.

### Sobre os Formatos de Output
- Todos os diagramas em **Mermaid** (`.mmd`) para integração em Markdown/GitHub.
- Diagramas de sequência também em **PlantUML** (`.txt`) compatível com importação
  no Visual Paradigm (File > Import > PlantUML).
- Ficheiros em `docs/architecture/`, organizados por tipo.

### Sobre a Arquitetura
- Padrão em 3 camadas: Presentation (Controllers) → Business Logic (Services) → Data Access (Repositories) → Database
- Serviços encapsulam toda a orquestração de regras de negócio
- Repositórios fornecem abstração de persistência
- EventoAuditoria regista todas as operações críticas
- Especificação de API (endpoints REST, payloads, status codes) em `docs/api/` — separado

## Racional
- Separar Reserva de Estadia permite modelar o ciclo de vida completo desde intenção até execução.
- HistorialClínico como entidade própria garante rastreabilidade médica independente da Estadia.
- ServiçoExtra como entidade separada de Cuidado permite distinguir ações operacionais de
  impacto financeiro.
- Fatura com múltiplos Pagamentos suporta diferentes métodos e liquidação parcial.
- Camadas Service e Repository promovem separação de responsabilidades e modularidade.
- EventoAuditoria garante rastreabilidade completa de todas as operações críticas.
- Métodos de negócio com visibilidade UML facilitam validação de regras em tempo de design.

## Alternativas Consideradas
- Fundir Reserva e Estadia numa só entidade: rejeitado — perde o ciclo de vida pré/pós check-in.
- Incluir HTTP nos diagramas de sequência: rejeitado — mistura níveis de abstração.
- Diagramas simplificados: rejeitado — detalhe total necessário para implementação.

## Referências
- Sommerville, I. (2016). *Software Engineering* (10th ed.), Cap. 5 e Cap. 7.
- Enunciado LI4-G08, Etapa 2 — Arquitetura e Design do Software.
- Especificação UML 2.5 (OMG).
- Constituição do projeto LI4-G08.
- Exemplos de diagramas UML/Mermaid em projetos open-source.
- Documentação oficial PlantUML: https://plantuml.com
- Visual Paradigm PlantUML import: https://www.visual-paradigm.com

---
Este documento serve de base para a modelação e documentação da arquitetura do projeto.