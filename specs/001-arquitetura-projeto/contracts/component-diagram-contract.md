# Contract: Component Diagram

## Escopo

Define o contrato minimo do diagrama de componentes.

## Componentes logicos minimos

- Reservas
- Estadias
- Cuidados
- Faturacao
- Limpeza
- Gestao de Animais
- Gestao de Utilizadores

## Camadas obrigatorias

- Frontend
- Backend/API
- Base de Dados

## Regras UML

- Uso de `<<component>>` e `<<interface>>`
- Dependencias explicitas entre componentes
- Interfaces expostas claramente marcadas

## Coerencia com outros artefactos

- Cada componente deve ter correspondencia com services do diagrama de classes
- Fluxos de sequencia devem atravessar componentes coerentes com este diagrama
