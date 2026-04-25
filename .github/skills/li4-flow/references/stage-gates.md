# Gates do Flow LI4

## Gate para entrar na Etapa 1
- O enunciado e o tema foram lidos e os limites do problema estao claros.
- A nomenclatura base do dominio esta estabilizada o suficiente para comecar a derivar `US`, `RF`, `RD`, `RNF` e `UC`.
- Os stakeholders, atores e contexto operacional do tema do hotel de animais estao identificados.

## Gate para entrar na Etapa 2
- User stories, requisitos funcionais, requisitos nao funcionais e casos de uso cobrem os fluxos mais importantes do sistema, em linha com a SRS (IEEE 830/29148).
- O modelo de dominio da Etapa 1 ja identifica os principais conceitos de negocio.
- Ambiguidades relevantes estao registadas como pressupostos, nao escondidas na arquitetura.

## Gate para entrar na Etapa 3
- A arquitetura e os diagramas UML da Etapa 2 (componentes, classes de design, sequencia) definem camadas, responsabilidades e principais interacoes.
- As decisoes arquiteturais com impacto tecnico ja estao explicitas em artefactos da Etapa 2.
- Os contratos de API e pontos de integracao necessarios para o caso de uso a implementar estao definidos.

## Gate para entrar na Etapa 4
- A Etapa 3 ja tem executaveis, testes tecnicos ou guias suficientes para recolher evidencia real.
- Existe uma ligacao clara entre comportamento implementado e `RF`, `RNF` ou `UC` a validar.
- As limitacoes conhecidas da implementacao estao identificadas para nao contaminar a avaliacao.

## Gate para a fase final (pos-Etapa 4)
- O sistema esta preparado para instalacao operacional e existe guia para a sua operacao e manutencao.
- O relatorio final do projeto pode ser sustentado por artefactos reais das quatro etapas.
- A apresentacao tecnica reflete o trabalho efetivamente realizado e o papel dos LLM ao longo do processo.

## Verificacao global
- Cada etapa reutiliza terminologia e IDs das etapas anteriores em vez de os reinventar.
- Se um artefacto novo alterar entendimento anterior, a mudanca fica visivel no artefacto certo e nao apenas implicita.
- O repositorio mostra um caminho continuo do problema ate a verificacao final.
- O contributo dos LLM esta usado de forma critica e governada, conforme esperado pelo enunciado de LI4.