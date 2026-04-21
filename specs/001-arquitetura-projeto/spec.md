# Feature Specification: arquitetura-projeto

**Feature Branch**: `[001-arquitetura-projeto]`  
**Created**: 2026-04-21  
**Status**: Draft  
**Input**: User description: "quero criar a arquitetura do projeto, para ir de encontro á fase 2 de #file:20252026-UM-LEI-LI4-Enunciado-Trabalho-Pratico.pdf, os diagramas é para criar em plantuml ou mermaid, tem que ter diagramas  de  classes,  sequência  e  componentes e outros que aches necessários, mantém tudo muito claro e simples"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Visualizar Arquitetura (Priority: P1)

Como membro da equipa, quero visualizar a arquitetura do projeto de forma clara e simples, para compreender rapidamente a estrutura e os principais componentes do sistema.

**Why this priority**: Permite alinhar toda a equipa e facilitar a comunicação e planeamento das próximas fases.

**Independent Test**: Pode ser testado ao apresentar os diagramas e verificar se todos os elementos essenciais estão representados e compreendidos pelos membros da equipa.

**Acceptance Scenarios**:

1. **Given** a equipa acede à documentação, **When** consulta os diagramas, **Then** compreende a estrutura geral do sistema.
2. **Given** um novo membro entra na equipa, **When** lê a documentação, **Then** entende rapidamente os principais componentes e fluxos.

---

### User Story 2 - Atualizar Diagramas (Priority: P2)

Como responsável pela documentação, quero poder atualizar facilmente os diagramas (classes, sequência, componentes), para garantir que a arquitetura está sempre alinhada com o desenvolvimento.

**Why this priority**: Mantém a documentação útil e relevante ao longo do projeto.

**Independent Test**: Pode ser testado ao modificar um componente e atualizar o diagrama correspondente, verificando se a alteração é refletida de forma clara.

**Acceptance Scenarios**:

1. **Given** uma alteração na arquitetura, **When** o diagrama é atualizado, **Then** a documentação reflete corretamente a nova estrutura.
2. **Given** um diagrama desatualizado, **When** é revisto, **Then** passa a estar em conformidade com o sistema atual.

---

### User Story 3 - Exportar Diagramas (Priority: P3)

Como utilizador, quero exportar os diagramas em formatos standard (imagem, PDF), para partilhar facilmente com stakeholders externos.

**Why this priority**: Facilita a comunicação com partes interessadas fora da equipa técnica.

**Independent Test**: Pode ser testado ao exportar um diagrama e verificar a sua legibilidade e integridade.

**Acceptance Scenarios**:

1. **Given** um diagrama criado, **When** é exportado, **Then** mantém a clareza e todos os elementos essenciais.
2. **Given** a necessidade de partilha externa, **When** o diagrama é enviado, **Then** o destinatário compreende a arquitetura apresentada.

---

## Functional Requirements

1. Devem ser criados diagramas de classes, sequência e componentes, utilizando PlantUML ou Mermaid.
2. Os diagramas devem ser claros, simples e facilmente compreendidos por todos os membros da equipa.
3. A documentação deve permitir a atualização fácil dos diagramas sempre que houver alterações na arquitetura.
4. Os diagramas devem poder ser exportados em formatos standard (imagem, PDF).
5. Devem ser incluídos outros diagramas relevantes (ex: casos de uso, domínio) se necessário para clarificar a arquitetura.
6. Toda a documentação deve estar organizada e acessível num local centralizado do projeto.

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
- Outros diagramas relevantes (casos de uso, domínio)
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