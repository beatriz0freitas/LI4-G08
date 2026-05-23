# ADR-02 - Interface de utilizador com renderização no servidor

**Estado:** Aceite
**Data:** 2026-04-25
**Decisores:** Equipa LI4-G08
**Contexto:** Etapa 2 - Arquitetura e Design

## Contexto
O sistema deve ser utilizado por colaboradores com diferentes perfis e níveis de familiaridade tecnológica. A compatibilidade com os equipamentos existentes exige uma solução acessível através de browser, sem instalação de software cliente dedicado em cada posto de trabalho.

## Decisão
Implementar a interface de utilizador através de páginas geradas no servidor, recorrendo a server-side rendering, usando Thymeleaf. Esta abordagem permite disponibilizar a aplicação através de browser e adaptar a visualização das funcionalidades ao perfil autenticado de cada utilizador, ocultando funcionalidades não autorizadas.

## Alternativas consideradas
- REST API + SPA, rejeitada por introduzir maior complexidade no lado do cliente, exigir gestão adicional de estado e aumentar o esforço de desenvolvimento com a necessidade de competências adicionais no desenvolvimento da interface.
- Aplicação desktop instalada nos postos, rejeitada por exigir instalação e atualização individual em cada estação de trabalho, contrariando o requisito de compatibilidade com os equipamentos existentes.

## Consequencias
### Positivas
- Acesso simples através de browser.
- Ausência de software específico a instalar nos postos.
- Integração direta com o controlo de autenticação e permissões.
- Menor complexidade de desenvolvimento face a uma SPA.

### Negativas
- Cada interação relevante com a interface requer comunicação com o servidor, considerada aceitável no contexto de rede local e da escala prevista.

## Impacto na arquitetura
- [architecture.md](../01-architecture/architecture.md)
- [components.mmd](../01-architecture/components.mmd)
- [wf01-login.html](../05-ui-interface-mockup/wf01-login.html)
- [README.md](../03-seq-diagrams/README.md)

## Rastreabilidade
- RF/RD/RNF/UC relacionados: RNF-02, RNF-04, UC-01..UC-13.
- Decisões dependentes: ADR-01.

## Status atual
- [ ] Proposta
- [x] Aprovada
- [ ] Implementada
- [ ] Validada
