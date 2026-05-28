# Perguntas de defesa e revisão do projeto Patas&Bigodes

## Objetivo

Este ficheiro reúne perguntas para preparar a defesa, revisão técnica e discussão crítica do projeto como um todo. As perguntas estão organizadas por etapa, com atenção equilibrada à conceção, requisitos, arquitetura, implementação e validação.

O foco principal é perceber:

- que decisões foram tomadas em cada fase;
- que impacto tiveram nas fases seguintes;
- que alternativas existiam;
- porque foram rejeitadas ou deixadas para trabalho futuro;
- como se mantém a rastreabilidade entre `US`, `RF`, `RD`, `RNF`, `UC`, diagramas, código e testes.

As perguntas não têm resposta única obrigatória. Devem ser usadas para treinar explicações fundamentadas, sempre com referência aos artefactos do projeto.

## Como usar

Para cada pergunta, tenta responder indicando:

1. A decisão tomada.
2. A evidência documental ou técnica que a suporta.
3. O impacto positivo e negativo dessa decisão.
4. As alternativas consideradas.
5. Porque essas alternativas foram rejeitadas, adiadas ou substituídas.
6. Que artefactos são afetados se a decisão mudar.

---

## Etapa 0 - Contextualização, tema e âmbito

1. Qual era o problema operacional inicial do Patas&Bigodes e porque justificava um sistema informático?
2. Que processos manuais existiam antes do sistema e que riscos introduziam na operação diária?
3. Porque foi escolhido um hotel de animais como domínio do projeto?
4. Que características do domínio tornam o problema mais complexo do que um simples CRUD?
5. Que objetivos de negócio foram identificados logo no início?
6. Como é que a centralização da informação responde aos problemas da Dra. Céu e da equipa?
7. Que funcionalidades foram consideradas fora de âmbito inicial e porquê?
8. Porque é que espécies para além de cão e gato foram excluídas do âmbito?
9. Que impacto teria incluir múltiplos estabelecimentos logo na primeira versão?
10. Que riscos teria implementar um portal do tutor nesta fase?
11. Que pressupostos foram assumidos sobre a dimensão do hotel e número de utilizadores?
12. Como é que esses pressupostos influenciaram as escolhas arquiteturais posteriores?
13. Que dados do enunciado serviram como fonte de verdade para as etapas seguintes?
14. Que partes do enunciado foram diretamente transformadas em requisitos?
15. Houve alguma ambiguidade no contexto do problema? Como foi tratada?
16. Que alternativas de âmbito poderiam ter sido escolhidas para reduzir ainda mais o projeto?
17. Que alternativas de âmbito poderiam ter tornado o projeto demasiado grande?
18. Que decisões iniciais tiveram maior impacto na implementação final?
19. Se o cliente pedisse suporte a espécies exóticas, que documentos teriam de ser atualizados?
20. Como justificarias que o sistema desenvolvido é adequado à escala atual do Patas&Bigodes?

---

## Etapa 1 - Conceção e engenharia de requisitos

1. Como foram identificados os stakeholders principais do sistema?
2. Porque foram definidos cinco perfis principais de utilizador?
3. Que diferenças existem entre Diretor, Funcionário de Receção, Cuidador, Médico Veterinário e Responsável pela Limpeza?
4. Como é que as entrevistas e o contexto do enunciado deram origem às user stories?
5. Que critérios foram usados para atribuir prioridades MoSCoW às user stories?
6. Que user stories são absolutamente críticas para o funcionamento mínimo do sistema?
7. Que user stories foram consideradas `Could Have` ou `Should Have` e porquê?
8. Que risco existiria se uma user story `Must Have` ficasse sem requisito associado?
9. Como distinguiste requisitos funcionais, requisitos não funcionais e requisitos de domínio?
10. Dá um exemplo de um `RF` que resulte diretamente de uma necessidade operacional.
11. Dá um exemplo de um `RD` que represente uma regra própria do negócio.
12. Dá um exemplo de um `RNF` que tenha impacto arquitetural.
13. Porque é importante preservar identificadores como `US-xx`, `RF-xx`, `RD-xx`, `RNF-xx` e `UC-xx`?
14. Como é que o diagrama de casos de uso ajuda a validar a fronteira do sistema?
15. Porque é que `UC-08 - Processar Pagamento` foi modelado como caso de uso separado?
16. Porque é que `Reserva` e `Estadia` aparecem como conceitos diferentes já na análise?
17. Que requisitos originaram `UC-02 - Consultar Disponibilidade de Alojamentos`?
18. Que requisitos originaram `UC-06 - Registar Check-in`?
19. Que requisitos originaram `UC-07 - Registar Check-out`?
20. Que requisitos originaram `UC-13 - Consultar Dashboard e Gerar Relatórios`?
21. Como foi garantido que cada caso de uso tinha origem em necessidades reais?
22. Que casos de uso são transversais a vários perfis?
23. Que casos de uso são restritos ao Diretor e porquê?
24. Como é que o modelo de domínio ajuda a evitar regras inventadas na implementação?
25. Que entidades do modelo de domínio são centrais para a rastreabilidade operacional?
26. Que entidades do modelo de domínio são centrais para a faturação?
27. Que entidades do modelo de domínio são centrais para auditoria?
28. Que alternativa existia a modelar plano de cuidados como entidade própria?
29. Porque seria problemático guardar cuidados e notas apenas como texto livre sem estrutura?
30. Se fosse necessário remover um requisito, como verificarias que UC, testes e código seriam afetados?

---

## Etapa 2 - Arquitetura e design

1. Porque foi escolhida uma arquitetura monolítica em camadas?
2. Que alternativas existiam à arquitetura monolítica?
3. Porque foram rejeitados ou adiados microserviços?
4. Porque foi escolhida uma aplicação web centralizada?
5. Que impacto teve a decisão de usar Spring MVC com Thymeleaf server-side?
6. Que alternativa existia a Thymeleaf e porque não foi usada?
7. Porque foi escolhido MySQL como SGBD relacional?
8. Que alternativa existia a uma base de dados relacional?
9. Porque é que uma base NoSQL seria menos adequada ao domínio?
10. Que vantagens trouxe o padrão repository com Spring Data JPA?
11. Que riscos trouxe a utilização de JPA?
12. Porque foi criada uma camada de DTOs entre controllers e domínio?
13. Que problema poderia ocorrer se os controllers expusessem entidades JPA diretamente?
14. Como é que o diagrama de componentes confirma a separação controller-service-repository?
15. Que responsabilidades pertencem aos controllers?
16. Que responsabilidades pertencem aos services?
17. Que responsabilidades pertencem aos repositories?
18. Que regras devem ficar no domínio ou nos services, e não nos templates?
19. Porque foi criado `AvailabilityDomainService`?
20. Que problema de concorrência a validação com lock pessimista pretende resolver?
21. Que alternativa existia ao lock pessimista?
22. Porque é que a auditoria persistente própria foi escolhida em vez de depender apenas de logs?
23. Que impacto tem a auditoria no modelo de dados e nos fluxos de serviço?
24. Porque foram definidos catálogos persistentes para tarifas e serviços extra?
25. Que risco existiria em usar valores fixos no código para tarifas?
26. Porque foi decidido exportar relatórios em PDF e CSV a partir de uma agregação comum?
27. Que alternativa existia para relatórios muito extensos?
28. Que limitações tem o deployment centralizado num único servidor?
29. Que medidas operacionais mitigam o ponto único de falha?
30. Se fosse necessário mudar para uma API REST com frontend separado, que ADRs e diagramas seriam afetados?

---

## Etapa 3 - Implementação e desenvolvimento

1. Como foi organizada a implementação incremental do sistema?
2. Porque era importante implementar por fases pequenas?
3. Que fase criou a fundação transversal da aplicação?
4. Que fase implementou tutores, animais e reservas?
5. Que fase implementou estadias e pagamentos?
6. Que fase implementou cuidados, clínica e limpeza?
7. Que fase consolidou relatórios, colaboradores e auditoria?
8. Que impacto teve a ordem das fases na redução de risco?
9. Que funcionalidades dependiam obrigatoriamente de outras já implementadas?
10. Porque é que check-in e check-out não podiam ser implementados antes de reservas?
11. Porque é que pagamentos dependem de estadias?
12. Porque é que limpeza depende do check-out?
13. Como foi garantido que a implementação respeitava a Etapa 1 e a Etapa 2?
14. Que papel tiveram os contratos Speckit ou specs na implementação?
15. Como é que os DTOs implementados refletem decisões da Etapa 2?
16. Como é que os controllers implementados refletem os UC definidos?
17. Que services são mais críticos para regras de negócio?
18. Que repositories suportam diretamente regras de disponibilidade?
19. Que validações foram implementadas para evitar overbooking?
20. Que validações foram implementadas para evitar estadias duplicadas para o mesmo animal?
21. Que impacto teve a utilização de Flyway nas migrações da base de dados?
22. Que alternativa existia a Flyway e porque foi menos interessante?
23. Como foi tratada a gestão de perfis na implementação?
24. Porque é que desativar colaboradores é preferível a eliminá-los fisicamente?
25. Como é que a auditoria foi integrada nos fluxos críticos?
26. Que fluxos ficaram mais difíceis de implementar do que o previsto?
27. Que requisitos ou user stories tiveram implementação parcial?
28. Que decisões técnicas tomadas na implementação deveriam voltar a ser registadas como ADR?
29. Que partes do código seriam mais afetadas se o modelo de domínio mudasse?
30. Se tivesses mais tempo de implementação, que melhoria técnica farias primeiro e porquê?

---

## Etapa 4 - Verificação, validação e avaliação da qualidade

1. Qual foi a estratégia geral de testes?
2. Porque foram usados testes unitários para services?
3. Porque foram usados testes WebMvc/MockMvc para controllers?
4. Porque foram usados testes de integração com MySQL?
5. Que tipos de erro cada nível de teste consegue detetar melhor?
6. Que limitações têm os testes unitários neste projeto?
7. Que limitações têm os testes de integração?
8. Que requisitos foram mais fáceis de validar automaticamente?
9. Que requisitos foram mais difíceis de validar automaticamente?
10. Porque é que alguns RNF ficaram apenas parcialmente verificados?
11. Que evidência existe para afirmar que os RF principais foram cobertos?
12. Como é que os testes de aceitação foram derivados dos UC?
13. Que testes validam autenticação e autorização?
14. Que testes validam disponibilidade e prevenção de overbooking?
15. Que testes validam check-in e check-out?
16. Que testes validam pagamento e faturação complementar?
17. Que testes validam cuidados, notas e serviços extra?
18. Que testes validam histórico clínico?
19. Que testes validam relatórios e auditoria?
20. Que papel teve JaCoCo na avaliação da qualidade?
21. Que limitações tem medir qualidade apenas por cobertura de código?
22. Que papel teve PITest ou mutation testing?
23. Que tipos de defeitos o mutation testing ajuda a revelar?
24. Como foram usadas métricas ISO/IEC 25010?
25. Que qualidade do produto ficou melhor demonstrada pelos testes?
26. Que qualidade ficou menos demonstrada e exigiria avaliação em ambiente real?
27. Que testes faltariam para validar usabilidade com utilizadores reais?
28. Que testes faltariam para validar disponibilidade contínua em produção?
29. Que testes faltariam para validar desempenho com histórico muito grande?
30. Se um teste falhar após alterar um requisito, como identificarias que documentos precisam de ser atualizados?

---

## Rastreabilidade entre US, requisitos, UC, design, código e testes

### Perguntas gerais de rastreabilidade

1. Como explicas a diferença entre rastreabilidade para trás e rastreabilidade para a frente?
2. Como demonstras que uma user story deu origem a requisitos concretos?
3. Como demonstras que um requisito deu origem a um ou mais casos de uso?
4. Como demonstras que um caso de uso foi considerado nos diagramas de sequência?
5. Como demonstras que um caso de uso foi implementado em controllers e services?
6. Como demonstras que um caso de uso foi testado?
7. Que artefacto usas para saber que requisitos originaram cada UC?
8. Que artefacto usas para saber que serviços implementam cada UC?
9. Que artefacto usas para saber que testes validam cada UC?
10. Como verificas se existe algum requisito sem cobertura por UC?
11. Como verificas se existe algum UC sem origem em requisitos?
12. Como verificas se existe algum teste que valida comportamento não especificado?
13. Como verificas se algum requisito foi implementado sem estar documentado?
14. Que risco existe quando a implementação evolui mas a matriz de rastreabilidade não é atualizada?
15. Como manterias a rastreabilidade se fosse adicionada uma nova user story?

### Perguntas por caso de uso

| UC | Perguntas de rastreabilidade |
|---|---|
| UC-01 | Que requisitos justificam a autenticação? Que `RNF` obriga a controlo de acesso? Que classes implementam a autenticação? Que testes demonstram que perfis não autorizados são bloqueados? |
| UC-02 | Que `US`, `RF`, `RD` e `RNF` originam a consulta de disponibilidade? Porque é que a disponibilidade depende de datas, espécie, reservas, estadias e limpeza? Que repository suporta a consulta? |
| UC-03 | Que requisito exige registo de tutor e animal? Porque é que `RD-05` e `RD-08` são relevantes? Que validações impedem dados inconsistentes? |
| UC-04 | Que requisitos originam a criação de reserva? Porque `RF-06` aparece juntamente com `RF-07`? Que papel tem `AvailabilityDomainService`? Que testes provam prevenção de overbooking? |
| UC-05 | Que requisito permite cancelar reserva? Que regra de domínio limita o cancelamento? Porque cancelar não deve apagar fisicamente a reserva? |
| UC-06 | Que requisitos originam check-in? Porque inclui pagamento e plano de cuidados? Que regra impede duas estadias ativas para o mesmo animal? |
| UC-07 | Que requisitos originam check-out? Porque o check-out altera reserva, estadia, pagamento e limpeza? Que testes demonstram a sequência completa? |
| UC-08 | Porque pagamento foi separado como UC próprio? Que UC incluem ou reutilizam pagamento? Que regra obriga método de pagamento válido? |
| UC-09 | Que user stories originam cuidados, notas e alterações de saúde? Como se garante autoria e data/hora? Que relação existe entre alteração crítica de saúde e prioridade do plano? |
| UC-10 | Que requisito originou serviços extra? Como se liga serviço extra à faturação de check-out? Porque é necessário catálogo ativo? |
| UC-11 | Que requisitos originam histórico clínico? Porque o médico veterinário tem permissões específicas? Que informação clínica pode afetar faturação? |
| UC-12 | Que requisitos originam limpeza? Como o check-out cria trabalho para este UC? Porque a limpeza afeta disponibilidade? |
| UC-13 | Que user stories do Diretor originam dashboard e relatórios? Que requisitos de desempenho e auditoria são relevantes? Porque a geração de relatórios é auditada? |
| UC-14 | Que requisito originou gestão de colaboradores? Que RNF exige autorização por perfil? Porque desativar é melhor do que eliminar? |
| UC-15 | Que requisito originou catálogos e tarifas? Que alternativa seria usar valores fixos? Porque essa alternativa foi rejeitada? |
| UC-16 | Que requisito originou auditoria? Que RNF exige rastreabilidade? Que filtros são necessários para consulta? Que risco existe se os eventos forem editáveis? |

### Perguntas por requisito funcional

1. Que UC cobrem `RF-01 - Dashboard`?
2. Que UC cobrem `RF-02 - Gestão de colaboradores e perfis`?
3. Que UC cobrem `RF-03 - Relatórios operacionais e financeiros`?
4. Que UC cobrem `RF-04 - Registo de tutores e animais`?
5. Que UC cobrem `RF-05 - Histórico de reservas, estadias e eventos`?
6. Que UC cobrem `RF-06 - Consulta de disponibilidade`?
7. Que UC cobrem `RF-07 - Gestão de reservas`?
8. Que UC cobrem `RF-08 - Check-in`?
9. Que UC cobrem `RF-09 - Check-out`?
10. Que UC cobrem `RF-10 - Pagamentos`?
11. Que UC cobrem `RF-11 - Plano de cuidados`?
12. Que UC cobrem `RF-12 - Registo de cuidados`?
13. Que UC cobrem `RF-13 - Alterações de estado de saúde`?
14. Que UC cobrem `RF-14 - Historial clínico`?
15. Que UC cobrem `RF-15 - Limpeza de alojamentos`?
16. Que UC cobrem `RF-16 - Notas operacionais`?
17. Que UC cobrem `RF-17 - Serviços extra`?
18. Que UC cobrem `RF-18 - Tarifas e catálogos`?
19. Que UC cobrem `RF-19 - Auditoria`?
20. Existem RF cobertos por mais do que um UC? Porque isso acontece?

### Perguntas por requisito de domínio

1. Que UC aplicam `RD-01 - Disponibilidade de alojamento`?
2. Que UC aplicam `RD-02 - Reserva válida para check-in`?
3. Que UC aplicam `RD-03 - Sequência de check-out`?
4. Que UC aplicam `RD-04 - Regras de pagamento`?
5. Que UC aplicam `RD-05 - Associação tutor-animal`?
6. Que UC aplicam `RD-06 - Cancelamento de reserva`?
7. Que UC aplicam `RD-07 - Exclusividade de estadia ativa por animal`?
8. Que UC aplicam `RD-08 - Dados clínicos ou de saúde do animal`?
9. Que UC aplicam `RD-09 - Custos associados a serviços/intervenções`?
10. Que UC aplicam `RD-10 - Plano de cuidados e prioridade`?
11. Que UC aplicam `RD-11 - Catálogos parametrizáveis`?
12. Que regra de domínio teve maior impacto no modelo de dados?
13. Que regra de domínio teve maior impacto nos testes?
14. Que regra de domínio teve maior impacto na arquitetura?
15. Que regra de domínio seria mais difícil alterar depois da implementação?

### Perguntas por requisito não funcional

1. Que decisões arquiteturais respondem a `RNF-01`?
2. Como `RNF-02` influenciou os protótipos e a interface?
3. Como `RNF-03` influenciou o deployment?
4. Como `RNF-04` influenciou autenticação e autorização?
5. Como `RNF-05` influenciou proteção e separação de dados clínicos?
6. Como `RNF-06` influenciou disponibilidade, concorrência e desempenho?
7. Como `RNF-07` influenciou relatórios e limite de período?
8. Como `RNF-08` influenciou auditoria e retenção?
9. Como `RNF-09` influenciou autoria, data/hora e registos críticos?
10. Que RNF não podem ser completamente provados por testes automatizados?

---

## Perguntas sobre alternativas rejeitadas ou adiadas

1. Porque não foi escolhido um sistema distribuído por microserviços?
2. Porque não foi escolhido um frontend SPA separado do backend?
3. Porque não foi escolhida uma base de dados NoSQL?
4. Porque não foi usada apenas uma folha de cálculo melhorada?
5. Porque não foi usado armazenamento em ficheiros para reservas e pagamentos?
6. Porque não foi criada uma app móvel nativa para cada perfil?
7. Porque não foi incluído portal do tutor na primeira versão?
8. Porque não foi incluído suporte multi-estabelecimento?
9. Porque não foi incluído suporte a espécies para além de cão e gato?
10. Porque não foi incluída receção 24 horas ou escalas avançadas?
11. Porque não foram modelados relatórios como tabelas persistentes próprias?
12. Porque não foi usada auditoria baseada apenas em logs da aplicação?
13. Porque não foram eliminados fisicamente colaboradores desativados?
14. Porque não foram usados valores fixos para tarifas e serviços extra?
15. Porque não foi implementado processamento assíncrono de relatórios extensos?
16. Porque não foram feitos testes formais de usabilidade com utilizadores reais?
17. Porque não foram feitos testes de carga extensos com histórico massivo?
18. Porque não foi implementada réplica de base de dados na primeira versão?
19. Porque não foi implementado backup automático completo como funcionalidade aplicacional?
20. Que alternativa rejeitada seria a primeira candidata a ser recuperada numa evolução futura?

---

## Perguntas de impacto entre etapas

1. Que decisão da Etapa 0 teve maior impacto na Etapa 1?
2. Que requisito da Etapa 1 teve maior impacto na arquitetura da Etapa 2?
3. Que ADR da Etapa 2 teve maior impacto no código da Etapa 3?
4. Que decisão da Etapa 3 teve maior impacto nos testes da Etapa 4?
5. Que resultado da Etapa 4 obrigaria a rever requisitos da Etapa 1?
6. Se `RD-01` mudasse, que UC, diagrams, services, repositories e testes seriam afetados?
7. Se `RNF-06` exigisse 100 utilizadores simultâneos, que arquitetura poderia deixar de ser adequada?
8. Se fosse introduzido portal do tutor, que perfis, permissões e UCs teriam de ser revistos?
9. Se fossem aceites novas espécies, que entidades, enumerações, regras e testes mudariam?
10. Se fosse necessário integrar pagamentos reais, que UC e ADR teriam de ser criados ou alterados?
11. Que parte da documentação ajuda mais a impedir scope creep?
12. Que parte da documentação ajuda mais a explicar o sistema a um novo programador?
13. Que parte da documentação ajuda mais a validar o sistema com o cliente?
14. Que parte da documentação ajuda mais a manter testes alinhados com requisitos?
15. Que inconsistência documental teria maior risco de gerar erro de implementação?

---

## Perguntas rápidas para simulação de defesa oral

1. Explica o projeto em dois minutos, do problema à solução.
2. Qual foi a decisão técnica mais importante do projeto?
3. Qual foi a decisão de requisitos mais importante do projeto?
4. Qual foi a decisão que mais reduziu risco?
5. Qual foi a decisão que mais criou limitações futuras?
6. Qual requisito foi mais difícil de implementar?
7. Qual requisito foi mais difícil de testar?
8. Qual UC melhor demonstra a coerência entre requisitos, arquitetura, código e testes?
9. Qual diagrama melhor explica a solução a um avaliador técnico?
10. Qual diagrama melhor explica a solução a um stakeholder não técnico?
11. Se tivesses de remover uma funcionalidade, qual removerias com menor impacto?
12. Se tivesses de acrescentar uma funcionalidade, qual seria a mais coerente com o trabalho futuro?
13. Onde é que a utilização de LLM ajudou mais?
14. Onde é que a utilização de LLM exigiu maior controlo humano?
15. Como provas que o sistema não inventou regras fora do enunciado?
16. Como provas que os testes não validam apenas casos felizes?
17. Como explicas a diferença entre reserva ativa, reserva confirmada e estadia em curso?
18. Como explicas a diferença entre serviço extra e tipo de serviço extra?
19. Como explicas a diferença entre plano de cuidados, tarefa de cuidado e registo de cuidado?
20. Como explicas a diferença entre histórico clínico e auditoria?

