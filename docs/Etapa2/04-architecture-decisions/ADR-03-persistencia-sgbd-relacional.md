# ADR-03 - Persistência de dados em SGBD relacional

**Estado:** Aceite
**Data:** 2026-04-25
**Decisores:** Equipa LI4-G08
**Contexto:** Etapa 2 - Arquitetura e Design

## Contexto
O domínio do sistema apresenta entidades com relações complexas e bem definidas, como Animal, Tutor, Reserva, Estadia, Cuidado, Fatura e Pagamento. Existem ainda múltiplas regras de integridade e necessidade de transações atómicas para garantir consistência em operações críticas, como o controlo de disponibilidade de alojamentos.

## Decisão
Adotar um sistema de gestão de base de dados relacional para suportar a persistência da informação. Esta decisão permite representar relações entre entidades, aplicar restrições de integridade, suportar transações e garantir maior consistência em operações concorrentes. A escolha da tecnologia concreta é documentada na ADR-04.

## Alternativas consideradas
- SGBD NoSQL orientado a documentos, rejeitado por ser menos adequado à estrutura relacional do domínio e aos requisitos de integridade referencial.
- Ficheiros planos ou folhas de cálculo, rejeitados por não suportarem adequadamente acesso concorrente, integridade dos dados, segurança ou cópias de segurança fiáveis.

## Consequencias
### Positivas
- Suporte a integridade referencial.
- Consistência em acessos concorrentes.
- Representação adequada das relações do domínio.
- Facilidade na realização de consultas estruturadas e de testes de integração.

### Negativas
- Maior rigidez do esquema de dados, exigindo alterações explícitas quando o modelo evolui.

## Impacto na arquitetura
- [architecture.md](../01-architecture/architecture.md)
- [deployment.mmd](../01-architecture/deployment.mmd)
- [class-diagram.mmd](../02-class-diagram/class-diagram.mmd)

## Rastreabilidade
- RF/RD/RNF/UC relacionados: RNF-01, RNF-06, RNF-07, UC-01..UC-13.
- Decisões dependentes: ADR-04, ADR-07.

## Status atual
- [ ] Proposta
- [x] Aprovada
- [ ] Implementada
- [ ] Validada
