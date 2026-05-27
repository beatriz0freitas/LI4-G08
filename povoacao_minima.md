# Povoação Mínima do Software

Este ficheiro descreve a povoação mínima criada para testar os módulos principais do sistema Patas & Bigodes.

## Ficheiro executável

- `PatasBigodesApp/scripts/povoacao-minima.sql`

O script é idempotente: pode ser executado novamente sem duplicar os dados principais.

## Credenciais de teste

Todos os utilizadores abaixo usam a palavra-passe `diretor123`.

| Username | Perfil |
|---|---|
| `diretor` | `DIRETOR` |
| `rececao` | `FUNCIONARIO_RECEPCAO` |
| `cuidador` | `CUIDADOR` |
| `veterinario` | `MEDICO_VETERINARIO` |
| `limpeza` | `RESPONSAVEL_LIMPEZA` |

## Dados incluídos

- Colaboradores para todos os perfis de acesso.
- Tipos de alojamento e tarifas ativas.
- Tipos de serviços extra.
- Alojamentos caninos e felinos em estados `CONCLUIDO` e `PENDENTE`.
- Tutores e animais com estados de saúde `NORMAL`, `ALTERADO` e `CRITICO`.
- Reservas nos estados `ATIVA`, `CONFIRMADA` e `CANCELADA`.
- Uma estadia em curso.
- Pagamento de check-in.
- Serviços extra.
- Nota operacional.
- Registo de cuidado.
- Plano de cuidados com tarefas concluídas e pendentes.
- Intervenção clínica.
- Alteração de estado de saúde.
- Evento de auditoria.

## Como aplicar manualmente

Com a base de dados em execução:

```bash
cd PatasBigodesApp
docker compose exec -T db mysql -uhoteluser -photelpass hotelanimais < scripts/povoacao-minima.sql
```
