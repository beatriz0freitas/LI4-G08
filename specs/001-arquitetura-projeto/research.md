# Research: arquitetura-projeto

**Date**: 2026-04-22  
**Spec**: `specs/001-arquitetura-projeto/spec.md`

## Objetivo

Definir e documentar a arquitetura global do sistema de gestao de hotel para animais (caes e gatos), produzindo os diagramas UML necessarios para suportar a Etapa 2 do projeto LI4-G08.

Os diagramas devem servir como base para:

- implementacao tecnica
- onboarding da equipa
- revisao arquitetural e rastreabilidade de decisoes

## Contexto do Sistema

- **Nome**: Sistema de Gestao de Hotel para Animais
- **Utilizadores**: Rececionista, Veterinario, Cuidador, Administrador, Responsavel de Limpeza
- **Escala operacional**: cerca de 32 alojamentos e ate 10 utilizadores simultaneos
- **Dominio principal**: reservas, alojamentos, estadias, cuidados, servicos extra, faturacao e pagamentos

## Entidades de Dominio Identificadas

- `Tutor`: responsavel pelo animal e ponto de contacto
- `Colaborador`: utilizador interno com perfil de acesso
- `Animal`: entidade central clinica e operacional
- `Alojamento`: unidade fisica com tipologia e estado de limpeza
- `Reserva`: compromisso futuro que pode originar no maximo uma estadia
- `Estadia`: execucao operacional da reserva
- `Cuidado`: acao diaria associada a estadia
- `Nota`: comunicacao entre turnos
- `HistorialClinico`: agregado clinico do animal
- `IntervencaoClinica`: ato clinico registado
- `ServicoExtra`: item com impacto financeiro
- `Fatura`: documento financeiro da estadia
- `Pagamento`: registo de liquidacao total ou parcial

## Fluxos Principais do Sistema

1. **Reserva**: verificar disponibilidade, criar e confirmar reserva
2. **Check-in**: validar reserva e iniciar estadia
3. **Check-out**: encerrar estadia, gerar fatura e marcar limpeza pendente
4. **Cuidados**: registar cuidados diarios na estadia
5. **Faturacao/Pagamento**: calcular fatura e registar pagamentos
6. **Limpeza**: atualizar estado do alojamento ate disponibilidade
7. **Intervencao veterinaria**: registar intervencao clinica e eventual impacto financeiro

## Regras de Negocio Criticas

- Alojamento disponivel apenas se limpeza concluida, sem estadia ativa e sem reserva confirmada para o periodo
- Alojamento deve respeitar compatibilidade especie-tipologia
- Reserva origina no maximo uma estadia
- Estadia tem exatamente uma fatura
- Fatura pode ter multiplos pagamentos
- Animal nao pode ter duas estadias ativas simultaneamente
- Reserva cancelada nao e reativada; cria-se nova reserva
- Custos de servico extra/intervencao tornam-se imutaveis apos check-out

## Decisao A - Services vs Subsystems

### Opcoes consideradas

1. Decomposicao em subsistemas autonomos
2. Decomposicao em services coesos

### Decisao

Adotar services coesos como abordagem principal no estado atual do projeto.

### Fundamentacao (Sommerville)

Para sistemas de media dimensao com dominio coeso, a decomposicao orientada a componentes de servico reduz complexidade acidental e preserva clareza de responsabilidades. A decomposicao em subsistemas independentes deve ser reservada para maior autonomia operacional, distribuicao ou contexto organizacional mais amplo.

### Impacto

- Menor sobrecarga arquitetural imediata
- Melhor rastreabilidade entre casos de uso e componentes
- Evolucao futura para subsistemas permanece possivel

## Decisao B - Services vs Facades

### Opcoes consideradas

1. Adotar facades genericas internamente
2. Adotar services como nucleo interno e facades apenas em fronteiras externas

### Decisao

Services sao a abstracao principal interna; facades ficam reservadas para fronteiras externas.

### Fundamentacao (Sommerville)

Facade e util para reduzir acoplamento de clientes externos com internals de um subsistema; porem, aplicar facade internamente sem necessidade adiciona camada extra sem ganho funcional. Em contexto de media escala, simplicidade e coesao devem prevalecer. Facade nao deve substituir responsabilidades de service interno quando nao ha ganho de coesao.

### Impacto

- Arquitetura interna mais direta
- Menor risco de over-engineering
- Facade continua disponivel para portal externo ou API agregadora futura

## Decisao C - Interfaces e Implementacoes

### Decisao

Todos os services e repositories sao definidos por interface de contrato + implementacao concreta.

### Fundamentacao (Sommerville)

Ocultacao de informacao e separacao interface/implementacao reduzem acoplamento e facilitam evolucao sem alteracao transversal.

## Decisao D - Consolidacao de Services (Modelo Final)

### Services alvo

- `UtilizadorService`
- `AnimalService`
- `ClinicaService`
- `AlojamentoService` (inclui limpeza)
- `ReservaService`
- `EstadiaService` (inclui cuidados, notas, servicos extra e plano de cuidado)
- `FaturacaoService`
- `PagamentoService`
- `RelatorioService`

### Racional (Sommervile)

- Separar por razao de mudanca
- Evitar fragmentacao artificial
- Preservar fronteiras funcionais entre reserva, estadia, clinica e faturacao

## Decisao E - Repositories com Metodos Explicitos

### Problema

Interfaces de repository vazias nao documentam acesso a dados e nao guiam implementacao.

### Decisao

Cada repository deve declarar consultas principais de negocio.

### Metodos minimos recomendados

- `AlojamentoRepository.findDisponiveis(inicio, fim, especie)`
- `AlojamentoRepository.findByEstadoLimpeza(estado)`
- `ReservaRepository.findByAlojamentoAndPeriodo(alojamento, inicio, fim)`
- `ReservaRepository.findByAnimal(animal)`
- `ReservaRepository.findByEstado(estado)`
- `EstadiaRepository.findEmCurso()`
- `EstadiaRepository.findByAnimal(animal)`
- `EstadiaRepository.findByPeriodo(inicio, fim)`
- `FaturaRepository.findPendentes()`
- `FaturaRepository.findByPeriodo(inicio, fim)`
- `PagamentoRepository.findByEstado(estado)`
- `PagamentoRepository.findByFatura(fatura)`

## Decisao F - Faturacao, Pagamento, Factory e Strategy

### Decisao

- `FaturacaoService`: ciclo de vida da fatura
- `PagamentoService`: registo e validacao de pagamentos
- `FaturaFactory`: construcao de fatura
- `EstrategiaCalculoFatura`: variacao de calculo sem alterar contrato principal

### Fundamentacao (Sommerville)

Separacao de criacao de objeto complexo, orquestracao e variacao de algoritmo melhora extensibilidade e manutencao.

## Decisao G - Observer para Mudanca de Estado de Alojamento

### Decisao

Usar mecanismo observavel para transicao de estado para `PendenteLimpeza`.

### Fundamentacao (Sommerville)

Quando multiplos componentes reagem a eventos de estado, observer reduz acoplamento e facilita evolucao.

## Decisao H - Limites da Logica de Dominio

### Decisao

- Regras com dependencia de consulta externa ficam em service, nao escondidas na entidade.
- Autorizacao sai de entidades de dominio e fica no modulo de acesso/seguranca.

## Decisao I - Melhorias de Entidades e Enumeracoes

### Melhorias aprovadas

1. `Estadia -> Tutor` associacao direta
2. `Fatura -> Tutor` associacao direta
3. `Reserva.criadaPor : Colaborador`
4. `Animal.estadoSaude : EstadoSaude`
5. `HistorialClinico` com operacoes clinicas (`getUltimaIntervencao`, `temPrescricaoAtiva`)
6. `ItemPlanoCuidado.tipo : TipoCuidado`
7. `EstadoReserva` inclui `Pendente`
8. `MetodoPagamento` inclui `MBReference`
9. Nota semantica obrigatoria na associacao opcional `IntervencaoClinica <-> ServicoExtra`

## Decisao J - Excecoes de Dominio e Invariantes

### Excecoes minimas

- `ReservaNaoConfirmadaException`
- `AlojamentoIndisponivelException`
- `PagamentoPendenteException`
- `AnimalEmEstadiaException`

### Decisao

Contratos de service devem declarar falhas de negocio, e o diagrama deve anotar invariantes RD-01 a RD-09.

## Decisao K - Cobertura de Diagramas UML (Etapa 2)

### Diagramas obrigatorios

- Diagrama de classes
- Diagramas de sequencia dos fluxos principais
- Diagrama de componentes

### Formato de output

- Mermaid (`.mmd`) para todos os diagramas
- PlantUML (`.txt`) adicional para sequencia, importavel no Visual Paradigm

### Regras de qualidade

- UML 2.5 estrita
- Sem cores/estilos nao standard
- Sem elementos HTTP/controller nos diagramas de sequencia de negocio
- Cabecalho obrigatorio em cada ficheiro de arquitetura

## Expansoes de Modelacao Aplicadas (a partir da analise)

### Hierarquia de utilizadores

- `Utilizador` (abstrata), `Tutor`, `Colaborador`
- Perfis de acesso tipados por enum

### Expansao de atributos operacionais

- `Animal`: dados de saude e necessidades
- `Reserva`: rastreabilidade de criacao e estado
- `Estadia`: auditoria de check-in/check-out
- `Fatura` e `Pagamento`: estado financeiro detalhado
- `ServicoExtra`, `Cuidado`, `Nota`: autoria, data/hora e contexto operacional

### Enumeracoes recomendadas

- `EstadoSaude`
- `EstadoReserva`
- `EstadoEstadia`
- `EstadoFatura`
- `EstadoPagamento`
- `MetodoPagamento`
- `TipoCuidado`
- `TipoServicoExtra`
- `EstadoLimpeza`

## Priorizacao de Implementacao da Documentacao

1. Completar contratos de repositories e services no diagrama de classes
2. Aplicar melhorias de entidades/enums/cardinalidades
3. Introduzir `FaturaFactory`, `EstrategiaCalculoFatura` e observer
4. Anotar RD-01 a RD-09 e excecoes de dominio
5. Produzir diagramas de sequencia obrigatorios (`.mmd` + `.txt`)
6. Produzir diagrama de componentes e validar consistencia entre artefactos

## Registo de Decisoes (obrigatorio)

Cada decisao critica deve ser registada em `docs/architecture/decisoes/` com:

- Contexto
- Problema
- Alternativas
- Decisao
- Consequencias
- Fundamentacao em Ian Sommerville

## Resultado Esperado

Arquitetura modular, simples e extensivel, com fronteiras de responsabilidade claras, contratos estaveis e diagramas UML completos para suportar implementacao, revisao tecnica e onboarding.