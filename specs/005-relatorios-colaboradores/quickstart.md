# Quickstart — Relatórios e Colaboradores

## Acesso

1. Iniciar a aplicação.
2. Abrir `/login`.
3. Entrar com o utilizador inicial `diretor` e password `diretor123`.

## Relatórios

1. Abrir `/relatorios`.
2. Selecionar `dataInicio`, `dataFim`, `tipoAlojamento` opcional e agrupamento.
3. Submeter o formulário em `Gerar`.
4. Usar os botões `CSV` ou `PDF` para descarregar o relatório filtrado.

## Colaboradores

1. Abrir `/colaboradores`.
2. Clicar em `Novo colaborador`.
3. Preencher username, nome, email, password e `tipoColaborador`.
4. Guardar e confirmar que o colaborador aparece na lista.
5. Usar editar/desativar para gerir o acesso.

## Auditoria

1. Abrir `/auditoria` com sessão de `DIRETOR`.
2. Filtrar por `dataInicio`, `dataFim`, utilizador, operação, entidade ou resultado.
3. Confirmar que a tabela apresenta eventos com timestamp, utilizador, operação, entidade, ID afetado, ação e resultado.
4. Usar o botão `CSV` para descarregar a auditoria filtrada.

Eventos registados automaticamente:

- Colaboradores: criação, edição e desativação.
- Reservas/estadias/pagamentos: criação de reserva, cancelamento, check-in, check-out, criação e liquidação de pagamento.
- Cuidados/clínica/limpeza: registo de cuidado, intervenção clínica, serviço extra e limpeza realizada.

Nota: a operação `EDITAR_RESERVA` está documentada na LAC-13, mas o fluxo de edição de reserva ainda não existe na aplicação atual.

## Retenção de Auditoria

- A retenção configurada é de 12 meses.
- O job `AuditoriaSchedulerJob` executa diariamente às 03h00.
- Eventos anteriores ao período de retenção são apagados fisicamente por `AuditoriaService.limparzardosAntigos(1)`.

## Utilizadores Iniciais

- `diretor` / `diretor123`

Só o `DIRETOR` inicial é semeado por migration Flyway, com password armazenada em BCrypt. Os restantes colaboradores devem ser criados em `/colaboradores` pelo diretor.
