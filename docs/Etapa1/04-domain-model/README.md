# Modelo de Domínio

Este diretório deve concentrar os artefactos conceptuais da Etapa 1 que descrevem o domínio do sistema.

## Ficheiros recomendados
- `domain-model.mmd`: diagrama principal em Mermaid.
- `domain-model.md`: explicação curta das entidades, relações, multiplicidades e pressupostos.
- `traceability.md` (opcional): mapeamento entre entidades do modelo e `US`, `RF`, `RD`, `RNF` ou `UC` quando o diagrama crescer.

## Critérios
- Modelar conceitos de negócio do hotel de animais, não classes técnicas de implementação.
- Manter os nomes alinhados com os documentos em `01-user-stories`, `02-requirements` e `03-use-cases`.
- Registar pressupostos sempre que os documentos de origem não forem suficientes para justificar uma entidade ou relação.
- Preferir um único diagrama Mermaid principal antes de dividir o domínio por subdiagramas.