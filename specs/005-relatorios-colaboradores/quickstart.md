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

## Utilizadores Iniciais

- `diretor` / `diretor123`

Só o `DIRETOR` inicial é semeado por migration Flyway, com password armazenada em BCrypt. Os restantes colaboradores devem ser criados em `/colaboradores` pelo diretor.
