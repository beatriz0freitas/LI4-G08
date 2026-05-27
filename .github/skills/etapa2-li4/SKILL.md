---
name: etapa2-li4
description: 'Workflow for LI4 Etapa 2 (Arquitetura e Design de Software utilizando LLM, LEI, Universidade do Minho): arquitetura global do sistema, diagramas UML de componentes, deployment, classes de design e sequencia, design de interfaces, contratos de API e decisoes arquiteturais (ADRs), sempre alinhados com a Etapa 1 e com o enunciado do hotel de animais.'
argument-hint: 'Indica o artefacto da Etapa 2 que queres criar, rever ou alinhar'
---

# Etapa 2 LI4

## Contexto
Etapa 2 do trabalho pratico de LI4 (LEI, Universidade do Minho), edicao 2026. Foco em definir a arquitetura global do sistema e os seus principais componentes, explorar o uso de LLM como assistentes de projeto e revisao arquitetural, e produzir a documentacao tecnica que descreve estrutura, comportamento e interfaces, incluindo diagramas UML, especificacao de API e decisoes arquiteturais.

## Quando usar
- Criar ou rever a arquitetura global do sistema.
- Produzir diagramas UML de componentes, deployment, classes de design ou sequencia.
- Especificar ou rever o design de interfaces e contratos de API.
- Registar decisoes arquiteturais com impacto tecnico (ADRs).
- Validar arquitetura ou design com apoio de LLM como agente de revisao.

## Procedimento
1. Ler as fontes por ordem: `docs/Etapa0/Enunciado.md`, `docs/Etapa0/Tema.md`, `docs/Etapa1/01-user-stories/`, `docs/Etapa1/02-requirements/`, `docs/Etapa1/03-use-cases/`, `docs/Etapa1/04-domain-model/` e depois o diretorio relevante de `docs/Etapa2/`.
2. Identificar os `RF`, `RD`, `RNF`, `UC` e conceitos do modelo de dominio que sustentam o artefacto pedido.
3. Produzir ou rever apenas a vista arquitetural ou de design alvo, sem misturar numa unica resposta todas as vistas da etapa.
4. Validar o resultado com a [checklist](./references/checklists.md).
5. Se a arquitetura introduzir uma restricao nova ou um trade-off relevante, registar isso num ADR ou no artefacto apropriado da Etapa 2.
6. Quando o LLM propuser padroes arquiteturais ou tecnologias, confirmar que sao compativeis com os requisitos nao funcionais e com a viabilidade tecnica do projeto academico.

## Regras de decisao
- Diagramas de componentes e deployment devem refletir executaveis, fronteiras e integracoes reais, nao uma wishlist abstrata.
- Diagramas de classes da Etapa 2 descrevem design e colaboracao tecnica em UML; nao devem copiar o modelo de dominio da Etapa 1 sem valor adicional.
- Cada diagrama de sequencia deve estar centrado num cenario observavel ligado a um caso de uso ou fluxo relevante.
- Contratos de API devem explicitar operacoes, dados, validacoes, autenticacao e erros observaveis coerentes com os requisitos.
- ADRs sao obrigatorios quando a escolha afetar implementacao, operacao ou qualidade de forma relevante.

## Template de ADR
Usa o template [adr-template.md](./references/adr-template.md) quando a decisao arquitetural precisar de ser registada.
