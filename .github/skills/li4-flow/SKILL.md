---
name: li4-flow
description: 'Workflow for LI4 (Laboratorios de Informatica IV, LEI, Universidade do Minho) cross-stage flow: planear transicoes entre Etapa 1, Etapa 2, Etapa 3, Etapa 4 e fase final de entrega, validar dependencias e manter rastreabilidade desde o enunciado ate aos testes e relatorio final, com apoio sistematico de LLM como agentes assistentes.'
argument-hint: 'Indica a etapa atual, a transicao ou o artefacto que queres preparar'
---

# LI4 Flow

## Contexto
Trabalho pratico da unidade curricular LI4 (Laboratorios de Informatica IV) da licenciatura em Engenharia Informatica da Universidade do Minho, edicao 2026, tema do hotel de animais. O ciclo de vida e sequencial e incremental, inspirado em Waterfall e V-Model, enriquecido com praticas de DevOps e desenvolvimento assistido por LLM (AI-assisted SE). Os LLM sao usados como agentes assistentes em todas as etapas, mas nunca substituem a decisao tecnica e a governacao do grupo.

## Quando usar
- Decidir qual o proximo artefacto ou etapa a trabalhar.
- Verificar se uma etapa tem base suficiente na etapa anterior.
- Preparar a transicao entre requisitos, arquitetura, implementacao e validacao.
- Preparar a fase final pos-Etapa 4 (guia de operacao, relatorio final, apresentacao tecnica).
- Rever coerencia global do projeto antes de entrega.

## Procedimento
1. Ler por ordem `docs/Etapa0/Enunciado.md`, `docs/Etapa0/tema.md`, os artefactos da etapa atual e os artefactos imediatamente anteriores que a suportam.
2. Identificar o artefacto alvo, a etapa de origem e os IDs, decisoes ou diagramas que transitam para a etapa seguinte.
3. Confirmar a gate da etapa na [checklist](./references/stage-gates.md) antes de criar novo material.
4. Produzir ou rever apenas o artefacto pedido, preservando nomenclatura, estrutura e terminologia ja estabilizadas no repositorio.
5. Registar pressupostos quando a etapa seguinte exigir detalhe que ainda nao exista na etapa anterior.
6. Quando o LLM gerar conteudo significativo, validar a sua coerencia com o enunciado, com os artefactos existentes e com as normas aplicaveis (IEEE 830/29148, ISO/IEC/IEEE 12207, ISO/IEC 25010).

## Regras de decisao
- Uma etapa nao deve introduzir conceitos que contrariem a etapa anterior sem registo explicito da decisao.
- Se faltar suporte documental, consolidar primeiro o artefacto anterior em vez de compensar com invencao na etapa seguinte.
- O detalhe tecnico aumenta de etapa para etapa, mas o vocabulario de negocio deve permanecer reconhecivel.
- Em caso de duvida, priorizar rastreabilidade e coerencia em vez de aparente completude.
- Output assistido por LLM e tratado como proposta sujeita a revisao humana, nao como facto definitivo.