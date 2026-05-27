# LI4 Workspace Guide

## Objetivo
Manter um fluxo de trabalho consistente entre equipa e LLM ao longo das quatro etapas do projeto.

## Índice do Workspace
- `AGENTS.md`: regras globais e fluxo base do workspace.
- `docs/Etapa0/Tema.md`: resumo curto do tema selecionado.
- `docs/Etapa0/Enunciado.md`: enunciado formal e âmbito do trabalho.
- `docs/Etapa1/`: artefactos de engenharia de requisitos.
- `docs/Etapa2/`: artefactos de arquitetura e design.
- `docs/Etapa3/`: implementação, testes técnicos e guias operacionais.
- `docs/Etapa4/`: verificação, validação e métricas de qualidade.
- `.github/agents/`: agentes especializados reutilizáveis.
- `.github/skills/`: skills e workflows repetidos.
- `docs/estrutura-projeto.md`: visão da estrutura recomendada.

## Fontes de Verdade
1. `docs/Etapa0/Enunciado.md`
2. `docs/Etapa0/Tema.md`
3. `docs/Etapa1/01-user-stories/`
4. `docs/Etapa1/02-requirements/`
5. `docs/Etapa1/03-use-cases/`
6. `docs/Etapa1/04-domain-model/`
7. `docs/`

## Regras Globais
- Escrever os artefactos do projeto em português europeu, salvo indicação explícita em contrário.
- Preservar os identificadores existentes: `US-xx`, `RD-xx`, `RF-xx`, `RNF-xx`, `UC-xx`.
- Não inventar regras de negócio sem suporte em enunciado, tema, requisitos ou casos de uso.
- Quando houver ambiguidade, registar pressupostos no documento produzido.
- Preferir um conceito principal por ficheiro quando isso melhorar a rastreabilidade.
- Não mover nem renomear pastas de entrega sem necessidade explícita.

## Regras para Diagramas e Modelos
- Guardar diagramas Mermaid em ficheiros `.mmd`.
- No modelo de domínio, representar conceitos de negócio e evitar classes técnicas.
- Acrescentar um `.md` de apoio apenas quando for preciso justificar decisões, relações ou pressupostos.
- Sempre que um diagrama introduzir um conceito relevante, ele deve ser explicável por pelo menos uma `US`, `RF`, `RD` ou `UC`.

## Fluxo Recomendado
1. Ler primeiro os artefactos da etapa em causa e os ficheiros vizinhos já existentes.
2. Cruzar sempre o pedido atual com as fontes de verdade acima.
3. Fazer a menor alteração possível para manter coerência documental.
4. Validar impacto em rastreabilidade, nomenclatura e estrutura.
5. Se o pedido criar uma nova decisão de projeto, registar essa decisão no diretório da etapa correspondente.

## Uso com LLM
- Regras sempre ativas vivem neste ficheiro `AGENTS.md`.
- Agentes especializados vivem em `.github/agents/`.
- Skills e workflows repetidos vivem em `.github/skills/`.
- Ficheiros de entrega da disciplina devem viver dentro da etapa correspondente, não em `.github/`.
