# Convenções de Código Java/Spring Boot

Este documento centraliza as convenções adotadas na implementação da aplicação Patas & Bigodes. Deve ser usado como referência em todas as features para manter a estrutura, nomenclatura e estilo de código homogéneos.

## Princípios Gerais

- Usar Spring Boot com arquitetura em camadas: controller, service, repository e model.
- Controllers devem tratar navegação MVC, validação de entrada e preparação do `Model`; regras de negócio ficam nos services.
- Services devem concentrar regras de negócio e transações.
- Repositories devem limitar-se ao acesso a dados com Spring Data JPA.
- Evitar lógica de negócio em templates Thymeleaf, entidades JPA ou controllers.
- Preferir código simples, explícito e alinhado com os padrões já existentes no projeto.

## Entidades JPA

- Usar `@Entity` em todas as entidades persistentes.
- Usar `@Table` quando o nome da tabela deve ser explícito ou diferente do nome da classe.
- Usar Lombok `@Getter`, `@Setter` e `@NoArgsConstructor`.
- Não usar `@Data` em entidades JPA, para evitar `equals`, `hashCode` e `toString` automáticos sobre relações JPA.
- Não implementar getters e setters manualmente, salvo quando existir validação ou lógica específica.
- Implementar `toString()` apenas quando for útil para depuração e evitar incluir relações bidirecionais.
- Usar `@Enumerated(EnumType.STRING)` para enums persistidos.
- Usar `GenerationType.IDENTITY` para identificadores gerados pela base de dados, salvo decisão explícita em contrário.

Exemplo:

```java
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "animais")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
}
```

## DTOs e Formulários

- Usar DTOs para dados recebidos de formulários ou expostos à camada de apresentação.
- Não usar entidades JPA diretamente como contrato de formulário quando isso expõe campos internos ou relações desnecessárias.
- Nomear DTOs de formulário com o sufixo `FormDto` quando representam submissões HTML.
- Aplicar validações declarativas com Jakarta Validation sempre que possível.

Exemplo:

```java
public class ReservaFormDto {

    @NotNull
    private Long animalId;

    @NotNull
    private Long alojamentoId;
}
```

## Services

- Interfaces de service usam o prefixo `I`.
- Classes concretas de service não usam o sufixo `Impl`.
- Controllers devem injetar interfaces de service, não classes concretas.
- Métodos de service devem ter nomes orientados ao domínio, não à persistência.
- Usar `@Transactional` nos métodos que alteram estado ou coordenam várias operações de escrita.

Exemplo:

```java
public interface IReservaService {
    Reserva criarReserva(ReservaFormDto dto);
}

@Service
public class ReservaService implements IReservaService {
    // implementação
}
```

## Controllers MVC

- Usar `@Controller` para rotas que devolvem páginas HTML.
- Evitar `@RestController` salvo necessidade explicitamente aprovada na spec.
- Controllers devem devolver nomes de templates ou redirects, não JSON.
- Usar `@RequestMapping` ao nível da classe para o prefixo funcional.
- Usar `@GetMapping` para páginas e `@PostMapping` para submissões de formulário.
- Após operações de escrita bem-sucedidas, preferir redirect para evitar reenvio de formulários.
- Usar `RedirectAttributes` para mensagens flash.
- Controllers não devem aceder diretamente a repositories.

Exemplo:

```java
@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final IReservaService reservaService;

    public ReservaController(IReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("reserva", new ReservaFormDto());
        return "reservas/form";
    }
}
```

## Repositories

- Repositories devem estender `JpaRepository`.
- Queries customizadas devem ser usadas apenas quando os métodos derivados não forem suficientemente claros.
- Preferir parâmetros tipados, incluindo enums, em vez de literais string em JPQL.
- Queries críticas de disponibilidade, faturação e histórico devem ter testes dedicados.

Exemplo:

```java
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByEstado(EstadoReserva estado);
}
```

## Segurança

- Autorização por rotas deve ficar centralizada em `SecurityConfig` quando a regra depende apenas do caminho.
- Usar `@PreAuthorize` apenas quando a regra é específica do método ou depende dos argumentos da operação.
- Rotas MVC devem respeitar a matriz de permissões definida em `docs/Etapa2/06-role-permissions/permissoes.md`.
- Palavras-passe devem ser armazenadas apenas como hash BCrypt.

## Templates Thymeleaf

- Templates devem devolver páginas HTML renderizadas no servidor.
- Usar fragments partilhados para head, navbar, sidebar e footer.
- Evitar duplicação de layouts quando já existir fragment ou página equivalente.
- Formulários devem submeter para controllers MVC e tratar erros com mensagens visíveis.
- Não criar páginas placeholder para módulos já implementados; cada rota funcional deve pertencer ao controller real do módulo.

## Testes

- Regras de negócio devem ter testes unitários na camada de service.
- Fluxos MVC críticos devem ter testes de controller ou integração.
- Repositories com queries customizadas devem ter testes de persistência.
- Testes com base de dados devem usar o ambiente de teste documentado e não a base principal.

## Documentação do Código

- Usar Javadoc em controllers, services, DTOs e exceptions públicas ou relevantes para fluxos de negócio.
- Javadoc deve explicar responsabilidade, parâmetros e retorno quando isso ajuda a perceber o contrato.
- Não documentar código óbvio com comentários redundantes.
- A documentação HTML do código deve ser gerada com o Maven Javadoc Plugin.
