---
name: etapa1-li4
description: 'Workflow for LI4 Etapa 1 (Concecao e Engenharia de Requisitos Assistida por LLM, LEI, Universidade do Minho): identificacao de stakeholders, eliciacao de requisitos, user stories, requisitos funcionais, requisitos nao funcionais, requisitos de dominio, casos de uso UML, rastreabilidade, modelo de dominio Mermaid e especificacao alinhada com IEEE 830/29148 para o tema do hotel de animais.'
argument-hint: 'Indica o artefacto da Etapa 1 que queres criar, rever ou alinhar'
---

# Etapa 1 LI4

## Contexto
Etapa 1 do trabalho pratico de LI4 (Laboratorios de Informatica IV) da licenciatura em Engenharia Informatica da Universidade do Minho, edicao 2026. Foco em definir o problema e o dominio do sistema, introduzir os LLM como agentes de apoio cognitivo a Engenharia de Requisitos e produzir documentacao formal alinhada com normas reconhecidas (IEEE 830/29148, ISO/IEC/IEEE 12207) que sirva de base solida as etapas seguintes.

## Quando usar
- Identificar stakeholders, atores e contexto operacional do sistema.
- Eliciar e refinar requisitos, incluindo simulacao de entrevistas com stakeholders apoiada por LLM.
- Criar ou rever user stories.
- Derivar ou consolidar requisitos `RF`, `RD` e `RNF`.
- Escrever ou rever casos de uso e diagramas UML associados.
- Verificar rastreabilidade entre artefactos.
- Criar ou rever o modelo de dominio em Mermaid.

## Procedimento
1. Ler as fontes por ordem: `docs/Etapa0/Enunciado.md`, `docs/Etapa0/Tema.md`, `docs/Etapa1/01-user-stories/`, `docs/Etapa1/02-requirements/`, `docs/Etapa1/03-use-cases/` e, se existir, `docs/Etapa1/04-domain-model/`.
2. Identificar os IDs fonte que suportam o artefacto pedido.
3. Produzir ou rever apenas o artefacto alvo, mantendo a nomenclatura ja usada no projeto e o vocabulario do dominio do hotel de animais.
4. Validar o resultado com a [checklist](./references/checklists.md).
5. Se o pedido for sobre modelo de dominio, comecar pelo [template Mermaid](./assets/domain-model-template.mmd) e depois adaptar ao que os documentos realmente suportam.
6. Quando o LLM gerar requisitos, user stories ou casos de uso, validar a sua consistencia com o enunciado e registar pressupostos em vez de aceitar afirmacoes nao suportadas.

## Regras de decisao
- Se houver conflito entre artefactos, dar prioridade ao enunciado e aos requisitos mais especificos.
- Se uma regra de negocio nao estiver suportada, registar a duvida como pressuposto em vez de a apresentar como facto.
- Se uma entidade aparecer apenas como detalhe operacional e nao como conceito de negocio, nao a promover automaticamente para o modelo de dominio.
- A especificacao de requisitos (SRS) deve permanecer alinhada com IEEE 830/29148: requisitos atomicos, verificaveis, identificados e rastreaveis.
