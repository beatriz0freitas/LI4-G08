# Research: Arquitetura do Projeto

## Objetivo
Investigar e consolidar as melhores práticas para a criação de diagramas de arquitetura (classes, sequência, componentes) para um sistema de hotel para animais (cães e gatos), garantindo clareza, rastreabilidade e alinhamento com os requisitos do domínio.

## Decisões Tomadas
- Utilizar PlantUML e/ou Mermaid para todos os diagramas.
- Diagramas devem ser completos: incluir todas as classes, métodos, atributos e relações relevantes.
- Diagramas de sequência devem cobrir os principais fluxos do sistema (ex: reserva, check-in, check-out, registo de cuidados, faturação).
- Diagramas de componentes devem mostrar a separação lógica (ex: módulos de reservas, gestão de estadias, faturação, histórico clínico, etc.).
- Todos os diagramas e decisões ficam em docs/architecture/.

## Racional
- PlantUML e Mermaid são ferramentas amplamente aceites, com boa integração em Markdown e exportação para imagem/PDF.
- Detalhe completo nos diagramas facilita onboarding, manutenção e evolução futura.
- Centralizar diagramas e decisões em docs/architecture garante fácil acesso e versionamento.

## Alternativas Consideradas
- Ferramentas visuais (ex: Lucidchart, draw.io): rejeitadas para garantir versionamento e integração com o repositório.
- Diagramas simplificados: rejeitado para evitar ambiguidades e garantir rastreabilidade total.

## Referências
- Sommerville, I. (2016). Software Engineering (10th ed.).
- Constituição do projeto LI4-G08.
- Exemplos de diagramas UML/Mermaid em projetos open-source.

---

Este documento serve de base para a modelação e documentação da arquitetura do projeto.
