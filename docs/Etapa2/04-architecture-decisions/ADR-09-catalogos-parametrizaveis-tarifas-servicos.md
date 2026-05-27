# ADR-09 - Catálogos persistentes para tarifas e serviços extra

**Estado:** Aceite
**Data:** 2026-05-27
**Decisores:** Equipa LI4-G08
**Contexto:** Etapa 2 - Arquitetura e Design; speckit 003

## Contexto
As funcionalidades de faturação, estadias e serviços extra precisam de valores configuráveis sem alteração de código. Os speckits introduzem a necessidade de consultar tarifas ativas por tipo de alojamento e de associar cada serviço extra a um tipo gerido pela direção.

Sem catálogos persistentes, os preços e nomes ficariam dispersos em enumerações, texto livre ou constantes, dificultando alterações operacionais e histórico coerente.

## Decisão
Manter os tipos de alojamento/tarifas e os tipos de serviço extra como entidades persistentes:

- `TipoAlojamentoTarifa` e `TipoAlojamentoTarifaService` são a fonte de verdade para a tarifa diária ativa de cada tipo de alojamento.
- `TipoServicoExtra` e `TipoServicoExtraService` são a fonte de verdade para os tipos de serviço extra disponíveis.
- `ServicoExtra` referencia `TipoServicoExtra`, evitando texto livre como identificador principal do tipo.
- `PagamentoService` calcula valores de estadia a partir da tarifa ativa obtida pelo catálogo.

## Alternativas consideradas
- Manter tarifas e serviços extra em enumerações Java, rejeitado por obrigar a alterações de código para gestão operacional.
- Guardar apenas texto livre no serviço extra, rejeitado por dificultar filtros, relatórios e consistência de nomes.
- Configurar preços apenas em ficheiros externos, rejeitado por não dar autonomia funcional ao perfil Diretor dentro da aplicação.

## Consequências
### Positivas
- O Diretor consegue gerir catálogos funcionais sem alterar código.
- Relatórios, histórico e faturação usam identificadores persistentes e nomes consistentes.
- A aplicação passa a suportar alterações futuras de tarifas com menor impacto.

### Negativas
- A criação de estadias, pagamentos e serviços extra depende da existência de catálogos ativos.
- É necessário manter regras de unicidade e ativação/desativação nos serviços de catálogo.

## Impacto na arquitetura
- [architecture.md](../01-architecture/architecture.md)
- [components.mmd](../01-architecture/components.mmd)
- [class-diagram.mmd](../02-class-diagram/class-diagram.mmd)
- Implementação: `TipoAlojamentoTarifa`, `TipoServicoExtra`, `ServicoExtra`, `TipoAlojamentoTarifaService`, `TipoServicoExtraService`, `ServicoExtraService`, `PagamentoService`.

## Rastreabilidade
- Requisitos: RF-10, RF-17, RF-18, RD-04, RD-09.
- Speckits: `specs/003-reservas-estadias-pagamentos/`.
- Decisões dependentes: ADR-03, ADR-04, ADR-06, ADR-08.

## Conformidade com a implementação atual
- Existem modelos, repositórios, controladores e serviços específicos para `TipoAlojamentoTarifa` e `TipoServicoExtra`.
- `ServicoExtraService` resolve o tipo por catálogo antes de persistir o serviço prestado.
- `RelatorioService` agrega serviços extra por `TipoServicoExtra` quando o relatório é agrupado por tipo de serviço.
- As migrações de base de dados criam e povoam os catálogos necessários para funcionamento inicial.

## Status atual
- [ ] Proposta
- [x] Aprovada
- [x] Implementada
- [ ] Validada
