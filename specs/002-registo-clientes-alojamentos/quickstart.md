# Quick Start: Registo Base de Clientes e Alojamentos

## 1. Pré-requisitos

- Java 21
- Maven
- Docker e Docker Compose, se for usado o ambiente completo local

## 2. Arranque local

1. Abrir o diretório `PatasBigodesApp`.
2. Subir a stack com `make up`.
3. Confirmar que a aplicação responde em `http://localhost:8080`.

Se for preferido executar apenas a base de dados em Docker e a app localmente:

1. Executar `make db-up`.
2. Executar `make run`.

## 3. Verificação automática

1. Executar `make test`.
2. Validar que os testes cobrem os fluxos de registo e reserva desta fase.

## 4. Fluxos manuais a validar

1. Autenticar com um utilizador de receção existente.
2. Abrir o formulário de registo de tutor e criar um tutor novo com NIF único.
3. Adicionar um animal associado ao tutor recém-criado.
4. Consultar disponibilidade para um intervalo de datas.
5. Criar uma reserva com uma box disponível.
6. Reconsultar a disponibilidade para confirmar que a box ficou indisponível no período.

## 5. Critérios de confirmação

- O tutor criado fica pesquisável pelo NIF ou nome.
- O animal fica associado ao tutor correto.
- A reserva fica registada com estado inicial `ATIVA`.
- O alojamento não volta a aparecer como disponível para o mesmo período.