# Future Expansions: Spec 001

**Purpose**: Este ficheiro guarda apenas temas que não fazem parte da implementação da Fase 1, para evitar confusão durante o desenvolvimento do monolito MVC atual.

## Why this is separate from the contracts

Os contratos desta spec devem descrever apenas o comportamento implementável agora, com base em controllers, services, repositories e Thymeleaf. Se misturarem detalhes de fases futuras, o risco é duas vezes maior:
- a equipa pode implementar cedo comportamentos que ainda não têm entidades nem casos de uso completos;
- a leitura do contrato fica ambígua entre o que é obrigatório agora e o que é apenas evolução posterior.

Separar estas notas mantém os contratos como artefactos de implementação e deixa as futuras capacidades num ficheiro de consulta rápida.

## Potential future topics

- Exposição de dados por endpoints JSON, se houver necessidade de integrações externas.
- Estratégias de cache para indicadores do dashboard.
- Optimistic locking ou outro controlo de concorrência para fluxos de limpeza e reservas.
- Reposição da lógica de indicadores quando as fases de reservas, estadias e pagamentos estiverem completas.

## Rule

Nada nesta lista é requisito da Fase 1. Só deve ser considerado quando existir spec própria, use cases e requisitos já aprovados.
