# Estrutura Recomendada do Projeto

## Objetivo
Organizar a pasta `docs/` por etapa e por tipo de artefacto, mantendo o projeto fácil de navegar por humanos e fácil de reutilizar por LLM ao longo do semestre.

## Princípios
- Manter os entregáveis separados por etapa.
- Guardar um artefacto principal por ficheiro sempre que isso melhorar a rastreabilidade.
- Usar Mermaid em ficheiros `.mmd` para diagramas.
- Preservar IDs rastreáveis entre user stories, requisitos, casos de uso, diagramas e testes.

## Estrutura recomendada
```text
docs/
├── Etapa0/
│   ├── Enunciado.md
│   └── Tema.md
├── Etapa1/
│   ├── AGENT.MD
│   ├── 01-user-stories/
│   │   └── user-stories.md
│   ├── 02-requirements/
│   │   ├── domain/
│   │   ├── functional/
│   │   └── non-functional/
│   ├── 03-use-cases/
│   │   ├── README.md
│   │   ├── traceability.md
│   │   └── UC-*.md
│   └── 04-domain-model/
│       ├── domain-model.md
│       ├── domain-model.mmd
│       ├── modelo_dominio.plantuml
│       └── README.md
├── Etapa2/
│   ├── 01-architecture/
│   ├── 02-class-diagram/
│   ├── 03-seq-diagrams/
│   ├── 04-architecture-decisions/
│   └── 05-ui-mockup/
├── Etapa3/
│   └── 01-operation-guides/
└── Etapa4/
    ├── 01-test-plan/
    ├── 02-test-cases/
    ├── 03-test-results/
    └── 04-quality-metrics/
```

## O que guardar em cada etapa

### Etapa 1
- User stories, requisitos, casos de uso, matriz de rastreabilidade e modelo de domínio.
- Diagramas em Mermaid e explicações curtas em Markdown.

### Etapa 2
- Arquitetura global, diagramas de classes, diagramas de sequência, contratos de API e ADRs.
- Um diagrama por ficheiro quando o domínio começar a crescer.

### Estrutura real já presente
- `docs/Etapa0/`
- `docs/Etapa1/`
- `docs/Etapa2/`

### Etapa 3
- Código-fonte, testes automatizados, scripts de apoio e guias operacionais.
- Se houver frontend e backend, dividir `01-source/` por módulos ou aplicações.

### Etapa 4
- Plano de testes, casos de teste, evidências de execução e métricas de qualidade.
- Ligar sempre os testes aos requisitos ou casos de uso que validam.

## Uso com LLM no VS Code
- As instruções sempre ativas devem ficar em `AGENTS.md` na raiz.
- Agentes reutilizáveis devem ficar em `.github/agents/`.
- Workflows repetidos devem ficar em `.github/skills/`.
- O ficheiro `Etapa1/AGENT.MD` pode continuar como nota documental, mas nao e um formato reconhecido automaticamente pelo Copilot para customizacao do workspace.

## Regra pratica
Se um ficheiro for preciso para a entrega da disciplina, ele deve viver dentro da etapa correspondente. Se um ficheiro existir para orientar o LLM ou a equipa em varias etapas, ele deve viver em `.github/`.
