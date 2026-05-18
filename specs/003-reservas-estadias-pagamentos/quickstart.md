# Quick Start: Reservas, Estadias e Pagamentos

## 1. Pré-requisitos

- Java 21
- Maven
- Docker e Docker Compose (opcional para stack completa)

## 2. Arranque local

1. Entrar em `PatasBigodesApp`.
2. Subir serviços com `make up`.
3. Confirmar aplicação em `http://localhost:8080`.

Alternativa (BD em Docker + app local):

1. `make db-up`
2. `make run`

## 3. Verificação automática

1. Executar `make test`.
2. Confirmar testes verdes em `controller`, `service` e `integration` para os fluxos desta fase.

## 4. Fluxos manuais de validação (P1)

1. Consultar disponibilidade de boxes para um período e validar ausência de sobreposição.
2. Criar reserva válida para tutor/animal existente.
3. Cancelar reserva e confirmar que não pode ser reativada.
4. Registar check-in com pagamento base.
5. Registar check-out com faturação complementar (quando existirem extras/intervenções).

## 5. Fluxos manuais de validação (P2)

1. Consultar histórico de estadias e pagamentos por animal/período.
2. Consultar dashboard de direção com indicadores e lista de pendentes.

## 6. Critérios de confirmação da feature

- Regras RD-01, RD-02, RD-03, RD-04, RD-06, RD-07 e RD-09 respeitadas em execução e testes.
- Pagamentos registam sempre valor, método e estado.
- Pelo menos 1 teste automatizado por funcionalidade P1.
- Pelo menos 1 teste de integração por UC principal (UC-04, UC-05, UC-06, UC-07, UC-08).
