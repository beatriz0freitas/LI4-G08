# Checklist de Consistencia da Etapa 2

## Arquitetura
- A visao arquitetural mostra camadas, responsabilidades e dependencias coerentes com os requisitos.
- Os componentes principais conseguem ser explicados pelos casos de uso ou restricoes nao funcionais existentes.
- O deployment representa o runtime real esperado para a aplicacao e as suas dependencias.

## Diagramas UML de Design
- Os diagramas de classes mostram responsabilidades tecnicas claras e nao apenas conceitos de negocio repetidos.
- Relacoes e dependencias batem certo com a arquitetura escolhida.
- A terminologia continua alinhada com o modelo de dominio e os requisitos da Etapa 1.
- Os diagramas seguem convencoes UML reconhecidas.

## Diagramas de Sequencia
- O ator, o trigger e o resultado do cenario estao claros.
- O fluxo principal e as alternativas nao contradizem `UC` ou `RF` existentes.
- As interacoes mostram colaboracoes plausiveis entre os elementos arquiteturais definidos.

## Design de Interfaces e Contratos de API
- Cada contrato identifica endpoint ou operacao, inputs, outputs, validacoes e erros relevantes.
- Autenticacao, autorizacao e restricoes observaveis estao explicitas quando aplicavel.
- O design das interfaces respeita os requisitos nao funcionais relevantes (usabilidade, seguranca, desempenho).

## Decisoes Arquiteturais (ADRs)
- Cada ADR explica contexto, decisao, alternativas consideradas e consequencias de forma verificavel.
- ADRs cobrem escolhas com impacto material em implementacao, operacao ou qualidade.

## Coerencia Cruzada
- A Etapa 2 resolve a Etapa 1 em termos tecnicos sem reinventar o problema de negocio.
- Se um detalhe tecnico introduzir uma restricao nova, essa restricao esta visivel no artefacto certo.
- Sugestoes de design por LLM foram revistas criticamente quanto a viabilidade no contexto do projeto academico.