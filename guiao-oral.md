Usei apenas o relatório `LI4-G08.pdf`. Aqui vai um guião oral de cerca de 5 minutos, focado no **processo de trabalho desenvolvido**.

**Estrutura Dos 5 Minutos**

**0:00-0:40 | Introdução - Katara**
Começa por enquadrar o projeto:

> O nosso trabalho consistiu no desenvolvimento do Patas&Bigodes, um sistema de gestão para um hotel de animais em Braga. O problema inicial era a existência de processos manuais, informação dispersa e falta de rastreabilidade nas operações do dia a dia. Por isso, o objetivo principal foi criar uma aplicação web centralizada, capaz de apoiar reservas, estadias, cuidados, pagamentos, relatórios e gestão de acessos.

**0:40-1:20 | Metodologia**
Explica a forma como organizaram o trabalho:

> O processo seguiu uma metodologia sequencial e incremental, dividida em quatro etapas: primeiro, conceção e engenharia de requisitos; depois, arquitetura e design; em seguida, implementação e desenvolvimento; e, por fim, verificação, validação e avaliação da qualidade. Esta organização permitiu avançar por fases, validar os artefactos produzidos em cada etapa e garantir que a implementação continuava alinhada com os requisitos definidos.

**1:20-2:10 | Etapa 1: Requisitos - Kefron**
Fala do levantamento e validação:

> Na primeira etapa, começámos por compreender o domínio do hotel de animais, identificar stakeholders e analisar restrições operacionais, organizacionais e técnicas. A partir daí foram produzidas user stories, requisitos funcionais, não funcionais e de domínio, casos de uso e modelo de domínio. Os LLM foram usados como apoio à estruturação da informação, reformulação técnica, deteção de ambiguidades e geração inicial de artefactos, mas sempre com validação humana. Uma regra importante foi não aceitar regras de negócio inventadas: tudo tinha de estar ancorado no contexto ou nos requisitos aprovados.

**2:10-3:00 | Etapa 2: Arquitetura e Design**
Mostra como passaram dos requisitos para uma solução técnica:

> Na segunda etapa, os requisitos foram transformados numa arquitetura concreta. A equipa adotou uma aplicação web centralizada com arquitetura em camadas, separando apresentação, aplicação, domínio e dados. Esta opção foi documentada através de ADRs, juntamente com outras decisões relevantes. Também foram produzidos diagramas UML, modelo lógico da base de dados, contratos de serviço, especificação de API, matriz de permissões e protótipos de interface. Os LLM ajudaram na geração e revisão destes artefactos, especialmente em PlantUML, contratos e coerência entre vistas arquiteturais, mas as decisões arquiteturais permaneceram da responsabilidade da equipa.

**3:00-4:00 | Etapa 3: Implementação - Fergie**
Explica o desenvolvimento assistido por LLM:

> Na implementação, o trabalho foi incremental e modular. Primeiro criou-se uma base funcional da aplicação, com configuração, autenticação, controlo de acessos, navegação e dashboard. Depois foram sendo implementados os módulos principais: tutores, animais, alojamentos, reservas, estadias, pagamentos, limpeza, cuidados, clínica, relatórios e colaboradores.
> Para controlar melhor o desenvolvimento assistido por LLM, foi usado o Speckit. Antes de gerar código, cada funcionalidade era descrita em especificações, planos, modelos de dados, contratos, quickstarts e tarefas pequenas. Isto ajudou a manter rastreabilidade entre requisitos, decisões e implementação. O código gerado ou sugerido por LLM foi sempre revisto, testado e comparado com a arquitetura e as regras de domínio.

**4:00-4:45 | Verificação e Qualidade - Twist**
Apresenta os resultados de validação:

> A última etapa foi dedicada à verificação e validação. Foram usados testes unitários, testes de controllers com MockMvc e testes de integração com contexto Spring e MySQL. Também existiu um teste end-to-end que percorre o fluxo operacional completo: reserva, check-in, cuidados, serviços extra, check-out, limpeza e relatório.

**4:45-5:00 | Conclusão**
Fecha com uma síntese forte:

> Em síntese, o processo permitiu transformar um contexto operacional manual e disperso numa aplicação web centralizada, testada e rastreável. O principal resultado não foi apenas o sistema final, mas também a forma controlada como os LLM foram integrados no ciclo de desenvolvimento: como ferramentas de apoio à análise, escrita, implementação e testes, mas sempre com supervisão humana. As limitações identificadas apontam trabalho futuro, sobretudo em validação com utilizadores reais, testes de carga mais extensivos e evolução funcional do sistema.
