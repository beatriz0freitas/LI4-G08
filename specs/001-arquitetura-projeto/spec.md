# Feature Specification: arquitetura-projeto

**Feature Branch**: `[001-arquitetura-projeto]`
**Created**: 2026-04-21
**Status**: Draft
**Input**: User description: "quero modelar a arquitetura do projeto, para ir de encontro á fase 2 de #file:20252026-UM-LEI-LI4-Enunciado-Trabalho-Pratico.pdf, os diagramas é para criar em plantuml ou mermaid, tem que ter diagramas  de  classes,  sequência  e  componentes e outros que aches necessários, mantém tudo muito claro e simples"

## Contexto
Sistema de gestão de hotel para animais (cães e gatos) — web app em 3 camadas.
Objetivo: produzir toda a documentação de arquitetura da Etapa 2 do projeto LI4-G08,
seguindo Sommerville (Cap. 5 e 7) e as normas UML 2.5.

## Functional Requirements

1. Devem ser criados diagramas de classes, sequência e componentes, utilizando PlantUML ou Mermaid.
2. Todos os diagramas devem seguir estritamente os standards UML, respeitando as regras da linguagem (ex: notação, símbolos, relações, agrupamentos) tal como implementado em ferramentas como Visual Paradigm.
3. Não devem ser inventadas cores, símbolos ou estilos não standard; usar apenas o que é definido pela especificação UML.
4. Os diagramas devem ser claros, simples e facilmente compreendidos por todos os membros da equipa.
5. A documentação deve permitir a atualização fácil dos diagramas sempre que houver alterações na arquitetura.
6. Os diagramas devem poder ser exportados em formatos standard (imagem, PDF).
7. Devem ser incluídos outros diagramas relevantes (ex: casos de uso, domínio) se necessário para clarificar a arquitetura.
8. Toda a documentação deve estar organizada e acessível num local centralizado do projeto.
9. Devem ser incluídos diagramas de sequência para os principais fluxos do sistema: reserva, check-in, check-out, cuidados, faturação, limpeza e intervenção do veterinário.


## Success Criteria

- Todos os diagramas obrigatórios (classes, sequência, componentes) estão presentes, claros e compreendidos pela equipa.
- Alterações na arquitetura são refletidas rapidamente nos diagramas/documentação.
- Diagramas podem ser exportados e partilhados sem perda de informação.
- Novos membros conseguem compreender a arquitetura apenas com a documentação criada.
- Stakeholders externos conseguem interpretar os diagramas exportados.

## Key Entities

- Diagrama de Classes
- Diagrama de Sequência
- Diagrama de Componentes
- Documentação centralizada

## Assumptions

- A equipa tem conhecimentos básicos de leitura de diagramas UML/Mermaid.
- Ferramentas para edição e exportação de diagramas estão disponíveis.
- A arquitetura inicial pode evoluir ao longo do projeto.

## Dependencies

- Ferramentas de edição de diagramas (PlantUML, Mermaid, etc.)
- Acesso à documentação do projeto

## Out of Scope

- Implementação do código-fonte dos componentes
- Detalhes técnicos de baixo nível (ex: queries SQL, endpoints REST)

## Clarifications

### Session 2026-04-21
- Q: Para garantir que os diagramas de arquitetura são realmente úteis para todos, qual o nível de detalhe desejado nos diagramas de classes e componentes? → A: Detalhe completo (todas as classes, métodos, atributos e relações detalhadas)

### Atualização dos requisitos
- Os diagramas de classes e componentes devem apresentar todas as classes, métodos, atributos e relações detalhadas, garantindo o máximo de detalhe possível para apoiar análise e implementação.

### Diagramas Obrigatórios

1. **Diagrama de Classes** em Mermaid (`.mmd`):
   - Entidades obrigatórias com atributos tipados e métodos completos:
     - `Tutor`, `Colaborador` (com papel: Rececionista | Veterinário | Cuidador | Responsável Limpeza )
     - `Animal` (espécie: Cão | Gato), `HistorialClínico`, `IntervençãoClínica`
     - `Alojamento` (tipologia: Canino | Felino; estadoLimpeza: enumerado),
       `Reserva`, `Estadia`, `Cuidado`, `Nota`
     - `ServiçoExtra`, `Fatura`, `Pagamento`
   - Incluir classes de Service e Repository 
   - Representar todas as relações com cardinalidades corretas:
     - Tutor 1 → * Animal
     - Animal 1 → 1 HistorialClínico
     - HistorialClínico 1 → * IntervençãoClínica
     - Reserva * → 1 Tutor, * → 1 Animal, * → 1 Alojamento
     - Reserva 1 → 0..1 Estadia
     - Estadia 1 → * Cuidado, 1 → * Nota, 1 → * ServiçoExtra, 1 → 1 Fatura
     - Fatura 1 → * Pagamento
   - Visibilidade UML (`+`, `-`, `#`) em todos os atributos e métodos

2. **Diagramas de Sequência** — um ficheiro `.mmd` e um `.txt` por fluxo:
   - Formato `.txt` deve ser PlantUML válido, importável no Visual Paradigm sem erros
   - Participantes: objetos de domínio e serviços (ex: `:ReservaService`, `:Estadia`,
     `:AlojamentoRepository`) — **sem HTTP, sem Controllers, sem endpoints REST**
   - Fluxos obrigatórios:
     - `seq-reserva` — verificação de disponibilidade do Alojamento, criação e confirmação
       de Reserva, associação Tutor + Animal + Alojamento
     - `seq-checkin` — validação da Reserva, criação da Estadia, atualização do estado
       do Alojamento para ocupado
     - `seq-checkout` — encerramento da Estadia, agregação de ServiçosExtra, cálculo e
       emissão de Fatura, atualização do estado do Alojamento para pendente de limpeza
     - `seq-cuidado` — registo de Cuidado diário (alimentação, medicação, higiene, atividade)
       por Cuidador, associado à Estadia
     - `seq-nota` — registo de Nota de turno associada à Estadia por Colaborador
     - `seq-servico-extra` — registo de ServiçoExtra por Cuidador ou Veterinário,
       associado à Estadia com impacto na Fatura
     - `seq-veterinario` — registo de IntervençãoClínica no HistorialClínico do Animal
       e, se aplicável, criação de ServiçoExtra na Estadia
     - `seq-limpeza` — atualização do estado de limpeza do Alojamento por Colaborador;
       quando concluída, Alojamento fica disponível
     - `seq-pagamento` — registo de Pagamento associado à Fatura; suporte a múltiplos
       Pagamentos por Fatura

3. **Diagrama de Componentes** em Mermaid (`.mmd`):
   - Módulos lógicos: Reservas, Estadias, Cuidados, ServiçosExtra, Faturação,
     Limpeza, GestãoAnimais, GestãoUtilizadores
   - Camadas: Frontend, API/Backend, Base de Dados
   - Notação UML (`<<component>>`, `<<interface>>`)

4. **Diagrama de Casos de Uso** em Mermaid (`.mmd`):
   - Atores: Tutor, Rececionista, Veterinário, Cuidador, Administrador
   - Casos de uso cobrindo todos os fluxos de sequência definidos acima

### Requisitos de Qualidade
5. Notação UML 2.5 estrita — sem cores ou estilos custom.
6. Ficheiros `.txt` PlantUML importáveis no Visual Paradigm via File > Import > PlantUML.
7. Cada ficheiro em `docs/architecture/` com cabeçalho: nome, tipo, data, descrição breve.
8. Regras de negócio refletidas nos diagramas:
   - Alojamento disponível apenas se limpeza concluída E sem Estadia ativa
   - Alojamento só aceita a espécie correspondente à sua tipologia
   - Reserva origina no máximo uma Estadia
   - Estadia tem exatamente uma Fatura

## Estrutura de Ficheiros Esperada

docs/
└── architecture/
    ├── class-domain.mmd
    ├── component.mmd
    ├── seq-reserva.mmd
    ├── seq-reserva.txt
    ├── seq-checkin.mmd
    ├── seq-checkin.txt
    ├── seq-checkout.mmd
    ├── seq-checkout.txt
    ├── seq-cuidado.mmd
    ├── seq-cuidado.txt
    ├── seq-nota.mmd
    ├── seq-nota.txt
    ├── seq-servico-extra.mmd
    ├── seq-servico-extra.txt
    ├── seq-veterinario.mmd
    ├── seq-veterinario.txt
    ├── seq-limpeza.mmd
    ├── seq-limpeza.txt
    ├── seq-pagamento.mmd
    ├── seq-pagamento.txt
    └── decisions/
    └── ADR-001-arquitetura-camadas.md

## Success Criteria
- Todos os diagramas existem, renderizam sem erros e seguem UML 2.5.
- Ficheiros `.txt` importam sem erros no Visual Paradigm.
- Diagrama de classes tem os quatro packages com todas as entidades, relações e cardinalidades.
- Diagramas de sequência mostram apenas lógica de negócio (sem HTTP/endpoints).
- Regras de negócio críticas estão explicitamente modeladas.

## Out of Scope
- Endpoints REST, payloads, status codes HTTP → ficam em `docs/api/`
- Implementação de código-fonte
- Schema SQL ou detalhes de base de dados

## Assumptions
- Diagramas de sequência seguem design-level (Sommerville Cap. 7), não system-level
- Visual Paradigm suporta importação via File > Import > PlantUML
- A stack tecnológica não altera o modelo de domínio