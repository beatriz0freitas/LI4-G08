# ADR-06 - Isolamento da camada de apresentação através de DTOs

**Estado:** Aceite
**Data:** 2026-04-25
**Decisores:** Equipa LI4-G08
**Contexto:** Etapa 2 - Arquitetura e Design

## Contexto
A exposição direta das entidades de domínio à camada de apresentação aumentaria o acoplamento entre a interface e o modelo interno da aplicação. Além disso, poderia facilitar a exposição indevida de dados pessoais ou clínicos em páginas onde essa informação não fosse necessária.

## Decisão
Utilizar objetos de transferência de dados, DTOs, entre a camada de apresentação e a camada de aplicação. A conversão entre entidades de domínio e DTOs é realizada na camada de aplicação, permitindo que a interface trabalhe apenas com os dados necessários para cada caso de uso.

## Alternativas consideradas
- Utilização direta das entidades na interface, rejeitada por aumentar o acoplamento, dificultar a evolução independente das camadas e elevar o risco de exposição de dados não necessários.

## Consequências
### Positivas
- Maior isolamento entre apresentação e domínio.
- Melhor controlo sobre os dados expostos.
- Facilidade em adaptar formulários e vistas sem alterar diretamente o modelo de domínio.

### Negativas
- Necessidade de criar e manter classes adicionais de DTO e respetiva lógica de conversão.

## Impacto na arquitetura
- [architecture.md](../01-architecture/architecture.md)
- [class-diagram.mmd](../02-class-diagram/class-diagram.mmd)
- [README.md](../03-seq-diagrams/README.md)

## Rastreabilidade
- RF/RD/RNF/UC relacionados: RNF-02, RNF-05, UC-01..UC-13.
- Decisões dependentes: ADR-01, ADR-02.

## Status atual
- [ ] Proposta
- [x] Aprovada
- [ ] Implementada
- [ ] Validada