# ADR-07 - Utilização de Docker no ambiente de desenvolvimento e testes

**Estado:** Aceite
**Data:** 2026-04-25
**Decisores:** Equipa LI4-G08
**Contexto:** Etapa 2 - Arquitetura e Design

## Contexto
O sistema utiliza MySQL como base de dados principal e também nos testes de integração. Para evitar diferenças de configuração entre ambientes locais e reduzir o esforço de preparação do ambiente, tornou-se necessário garantir uma forma consistente de disponibilizar os serviços de suporte ao desenvolvimento e aos testes.

## Decisão
Utilizar Docker para suportar a execução dos serviços necessários ao sistema, nomeadamente a base de dados MySQL. Esta opção permite criar ambientes reproduzíveis para desenvolvimento e testes, garantindo que a base de dados pode ser inicializada de forma controlada e independente da configuração local de cada máquina.

## Alternativas consideradas
- Instalação local manual do MySQL, rejeitada por aumentar o risco de diferenças de configuração entre máquinas e exigir maior esforço de preparação do ambiente.
- Base de dados em memória para testes, rejeitada por não reproduzir exatamente o comportamento do SGBD utilizado pela aplicação.

## Consequências
### Positivas
- Maior consistência entre ambientes.
- Preparação mais simples da base de dados.
- Redução de problemas causados por diferenças de configuração local.
- Melhor suporte à execução de testes de integração.

### Negativas
- Introduz dependência do Docker no ambiente de desenvolvimento e exige que os colaboradores tenham a ferramenta instalada e configurada.

## Impacto na arquitetura
- [architecture.md](../01-architecture/architecture.md)
- [deployment.mmd](../01-architecture/deployment.mmd)
- [README.md](../03-seq-diagrams/README.md)

## Rastreabilidade
- RF/RD/RNF/UC relacionados: RNF-01, RNF-06, RNF-07, UC-01..UC-13.
- Decisões dependentes: ADR-03, ADR-04.

## Status atual
- [ ] Proposta
- [x] Aprovada
- [ ] Implementada
- [ ] Validada