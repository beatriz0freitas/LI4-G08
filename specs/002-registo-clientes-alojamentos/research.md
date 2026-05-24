# Research: Registo Base de Clientes e Alojamentos

## 1. Alinhamento com Etapa 2

**Decision**: A implementação desta feature deve seguir a arquitetura e os modelos já documentados em Etapa 2, sem introduzir novas convenções de pacote, novas camadas ou fluxos alternativos.

**Rationale**: A [architecture.md](../../docs/Etapa2/01-architecture/architecture.md) define a aplicação como um monólito MVC em camadas com Thymeleaf e Spring Data JPA. A [class-diagram.md](../../docs/Etapa2/02-class-diagram/class-diagram.md) e os diagramas de sequência [UC-03.mmd](../../docs/Etapa2/03-seq-diagrams/UC-03.mmd) e [UC-04.mmd](../../docs/Etapa2/03-seq-diagrams/UC-04.mmd) detalham exatamente onde esta feature encaixa: `TutorAnimalController`, `ReservaController`, interfaces `ITutorService`, `IAnimalService`, `IReservaService`, `IAlojamentoService`, implementações concretas `TutorService`, `AnimalService`, `ReservaService`, `AlojamentoService` e respetivos repositórios.

**Alternatives considered**: Introduzir endpoints REST paralelos, uma camada de frontend separada ou fluxos de serviço não alinhados com os diagramas existentes. Essas opções foram rejeitadas por quebrarem a rastreabilidade e complicarem a implementação sem benefício funcional.

## 2. UI e experiência de receção

**Decision**: Usar os mockups da Etapa 2 como referência visual e de navegação, com prioridade para [wf03-reservas.html](../../docs/Etapa2/05-ui-interface-mockup/wf03-reservas.html) no fluxo de consulta de disponibilidade e criação de reserva.

**Rationale**: Esta feature é executada por funcionários de receção e deve manter consistência com o dashboard e o formulário de reservas já desenhados. O mockup de reservas mostra a organização esperada dos painéis, seleção de tutor/animal, lista de alojamentos e confirmação da reserva.

**Alternatives considered**: Desenhar novos ecrãs sem referência aos mockups existentes. Foi rejeitado porque reduziria consistência visual e aumentaria o risco de retrabalho em Etapa 3.

## 3. Stack técnica e arquitetura

**Decision**: Manter a stack já declarada no repositório: Java 21, Spring Boot 3.3.5, Spring MVC, Thymeleaf, Spring Security, Spring Data JPA e Flyway.

**Rationale**: O `pom.xml` confirma esta combinação, e a Etapa 2 já fixa uma arquitetura MVC monolítica com renderização server-side. Isto reduz risco de integração e mantém coerência com os artefactos existentes.

**Alternatives considered**: REST API separada com frontend SPA; versões mais recentes da stack; introdução de novo framework de interface. Foram rejeitadas por aumentarem complexidade sem benefício para esta fase.

## 4. Modelo de dados e integridade

**Decision**: Modelar `Tutor`, `Animal`, `Reserva` e `Alojamento` como entidades persistidas com integridade relacional, usando restrições de unicidade e chaves estrangeiras para garantir consistência.

**Rationale**: A UC-03 exige que o animal seja criado a partir de um tutor existente, e a UC-04 depende da disponibilidade de alojamento. A integridade precisa de ficar no servidor e na base de dados, não apenas na interface.

**Alternatives considered**: Guardar os dados apenas em memória; validar unicidade e associações só na camada de apresentação. Ambas as opções foram rejeitadas por permitirem estados inconsistentes.

## 5. Controlo de disponibilidade

**Decision**: A disponibilidade do alojamento será calculada no service layer a partir de três condições: ausência de reserva sobreposta, ausência de estadia ativa e estado de limpeza concluído.

**Rationale**: O RF-06 e o RD-03 impõem que a reserva só seja criada quando o alojamento é realmente elegível. Centralizar a regra no serviço evita discrepâncias entre tela, controller e persistência.

**Alternatives considered**: Filtragem apenas na interface; cálculo ad hoc em cada controller. Estas abordagens foram rejeitadas por serem frágeis e difíceis de testar.

## 6. Contratos de interação

**Decision**: Documentar contratos para os fluxos de receção como contratos de UI e de submissão de formulários, em vez de definir uma API pública separada.

**Rationale**: A arquitetura da Etapa 2 usa Thymeleaf e MVC server-side. Os contratos relevantes são os formulários, páginas e redirecionamentos usados pelo funcionário de receção.

**Alternatives considered**: Criar uma camada REST paralela apenas para esta fase. A alternativa foi rejeitada por duplicar responsabilidades e afastar-se do desenho aprovado.

## 7. Estratégia de testes

**Decision**: Usar `Spring Boot Test`, `MockMvc`, `Mockito` e `Spring Security Test` para cobrir fluxos de controller e serviço; usar Mockito quando não há persistência e MySQL nos testes que validam repositories, queries, transações ou migrations.

**Rationale**: Estes testes cobrem a lógica crítica da fase 2 sem simular um SGBD diferente do usado pela aplicação.

**Alternatives considered**: Base de dados em memória; apenas testes unitários. A base em memória foi rejeitada por poder divergir do MySQL, e testes unitários isolados seriam insuficientes para validar fluxos de reserva e segurança.



**Implementation note**: As alterações de implementação e o histórico de correcções devem ser registados em `refinements.md`. Não use este ficheiro como changelog; `research.md` mantém decisões, alternativas e rationale.
