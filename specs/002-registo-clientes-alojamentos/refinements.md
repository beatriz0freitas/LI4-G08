# Refinements / Changelog: Registo Base de Clientes e Alojamentos

## 2026-05-06 — Correções técnicas e de navegação (por agente LLM)

Resumo das correções aplicadas ao código e UI durante a implementação da feature:

- Fix: Corrigida a comparação de enums em JPQL para evitar runtime exceptions na resolução de queries. Arquivo alterado: `PatasBigodesApp/src/main/java/pt/hotel/animais/repository/AlojamentoRepository.java`.
- Fix: Removidos mapeamentos placeholders duplicados em `ModuloPlaceholderController.java` que causavam `Ambiguous handler methods`. Arquivos/Classes alteradas: `ModuloPlaceholderController`, `TutorAnimalController` (removido método redundante), novo `AnimalController` criado.
- Fix: Adicionada `animais/list.html` e ponto de entrada `GET /animais` no `AnimalController` para listagem geral de animais.
- Fix: Atualizada a fragment `fragments/sidebar.html` para apontar o link "Animais" a `/animais` e para corrigir a lógica de `menu-open`/`active` com base em `activePage`.
- Fix: `reservas/form.html` — corrigi o wizard de criação de reservas; normalizei as importações JS para jQuery 3.6 + Bootstrap 4.6 e adicionei a função `goToStep(stepId)` para navegar entre etapas via API de tabs do Bootstrap.

Arquivos modificados (resumo):

- PatasBigodesApp/src/main/java/pt/hotel/animais/repository/AlojamentoRepository.java
- PatasBigodesApp/src/main/java/pt/hotel/animais/controller/ModuloPlaceholderController.java
- PatasBigodesApp/src/main/java/pt/hotel/animais/controller/TutorAnimalController.java
- PatasBigodesApp/src/main/java/pt/hotel/animais/controller/AnimalController.java (novo)
- PatasBigodesApp/src/main/resources/templates/animais/list.html (novo)
- PatasBigodesApp/src/main/resources/templates/fragments/sidebar.html
- PatasBigodesApp/src/main/resources/templates/reservas/form.html

Notas e follow-ups:

- Verificação: testar o wizard "Criar Reserva" em ambiente local com browser; coletar console JS e server logs se o botão "Próximo" continuar a não funcionar.
- Tech debt: rever templates restantes para assegurar consistência de versões de Bootstrap/jQuery (algumas páginas podem ainda incluir Bootstrap 5 por erro).
- Tests: adicionar testes de integração para `AlojamentoRepository` (T034) e cobrir o fluxo de criação de reserva em `ReservaController`.

## 2026-05-06 — JPQL e Enums — detalhe e recomendações

Contexto: Queries JPQL que comparam campos de tipo `enum` com literais string (ex.: `a.estadoLimpeza = 'CONCLUIDO'`) geravam exceções de parsing/performance em runtime, provocando HTTP 500 nas páginas que executavam essas queries.

Correção aplicada:

- Substituídas comparações por literais por referências qualificadas às constantes de enum, p.ex. `a.estadoLimpeza = pt.hotel.animais.model.enums.EstadoLimpeza.CONCLUIDO`.
- Alternativa segura aplicada em alguns pontos: parametrizar o método do repositório para receber `EstadoLimpeza`/`EstadoReserva` como `@Param` e passar a enum diretamente do service/controller — isto melhora legibilidade e evita erros de string.

Recomendações operacionais:

- Preferir métodos do Spring Data JPA que aceitam enums como parâmetros (`findByEstadoLimpeza(EstadoLimpeza estado)`) quando possível.
- Adicionar validação unitária para as queries que envolvem enums (testes que persistem entidades com estados vários e executam a query de disponibilidade).
- Documentar no repositório a decisão de usar enums qualificados ou parâmetros (comentário e/ou JavaDoc curto) para evitar regressões.

Arquivos para acompanhar: `PatasBigodesApp/src/main/java/pt/hotel/animais/repository/AlojamentoRepository.java`, `PatasBigodesApp/src/main/java/pt/hotel/animais/service/AlojamentoService.java`.

## 2026-05-06 — Conflito de mapeamentos entre controllers — detalhe e recomendações

Contexto: `Ambiguous handler methods` causado por controllers genéricos (placeholders) a mapear as mesmas rotas que controllers implementados.

Correção aplicada:

- Removidos os mapeamentos duplicados do `ModuloPlaceholderController` para `/tutores`, `/reservas` e `/animais`.
- Criado `AnimalController` com `@RequestMapping("/animais")` para servir `GET /animais` e `GET /animais/{id}`.
- Removido o método redundante `detalheAnimal` em `TutorAnimalController` para concentrar as rotas raiz de `animais` no novo controller.

Recomendações operacionais:

- Evitar controllers "catch-all" que mapeiem rotas de módulos inteiros; preferir placeholders com prefixos claros que não colidam com implementações reais (p.ex. `/modulo/recepcao-placeholder`).
- Adicionar um teste de integração que carrega o contexto Spring e verifica que não existem `Ambiguous handler methods` (p.ex. `WebApplicationContext` start-up test com asserts sobre `RequestMappingHandlerMapping`).
- Registar esta decisão no ADR local se existirem convenções de routing partilhadas pela equipa.

Arquivos para acompanhar: `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/ModuloPlaceholderController.java`, `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/AnimalController.java`, `PatasBigodesApp/src/main/java/pt/hotel/animais/controller/TutorAnimalController.java`.

## Follow-ups técnicos e prioridades

- Prioridade imediata: validar o wizard de reservas no browser e coletar logs caso o problema persista (ver task T034 e item 4 da TODO list).
- Prioridade curta: adicionar testes de integração para `AlojamentoRepository` (T034) e criar um pequeno teste de arranque da aplicação para detectar mapeamentos ambíguos.
- Prioridade média: fazer varredura automatizada dos templates `src/main/resources/templates/**` para detectar inclusões de CDN conflitantes (`bootstrap`, `jquery`) e corrigir para a versão definida (Bootstrap 4.6 + jQuery 3.6). Recomenda-se criar script ou grep simples para encontrar ocorrências.


