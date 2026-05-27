# LI4 — Projeto "Patas & Bigodes"

Este repositório contém o projeto LI4 (Laboratórios de Informática IV) sobre com a aplicação PatasBigodesApp — um protótipo de um "hotel de animais" que suporta registo de tutores, gestão de alojamentos, reservas, estadias, cuidados clínicos, serviços extra e faturação. Além do código, o repositório inclui especificações, casos de uso, modelos de domínio e diagramas de suporte para rastreabilidade.

Alunos:

- A106804 — Alice Isabel Faria Soares
- A106853 — Ana Beatriz Ribeiro Freitas
- A107365 — Beatriz Martins Miranda
- A107367 — João Paulo Batista Azevedo

Conteúdo principal:

- `PatasBigodesApp/` — código fonte, Dockerfile, Makefile e artefactos de execução.
- `docs/` — diagramas, design e decisões arquiteturais por etapa.
- `specs/` — especificações detalhadas, tarefas e checklists por funcionalidade.

Uso do Spec Kit / Specify CLI (opcional):

Se estiveres a usar o Spec Kit para automatizar especificações e geração de tarefas, ver o subdiretório `.github/` e a documentação do Spec Kit. Estas ferramentas são opcionais e não são necessárias para compilar ou executar a aplicação.

---

## Comandos `make` (PatasBigodesApp)

Os comandos abaixo são os alvos mais usados no `Makefile` localizado em `PatasBigodesApp/Makefile`. Executa-os a partir da raiz do repositório com `make -C PatasBigodesApp <target>` ou muda para a pasta `PatasBigodesApp/` antes de executar `make`.

Nota: os valores de portas e credenciais podem ser parametrizados através de variáveis de ambiente.

Comandos usuais:

```bash
# Subir a stack completa (app + bd) com rebuild das imagens
make -C PatasBigodesApp up

# Subir apenas o serviço de base de dados (MySQL)
make -C PatasBigodesApp db-up

# Parar a stack completa
make -C PatasBigodesApp down

# Remover a stack e volumes persistentes
make -C PatasBigodesApp destroy

# Arrancar a aplicação localmente (usa variáveis de ambiente para a BD)
make -C PatasBigodesApp run

# Empacotar a aplicação (mvn clean package)
make -C PatasBigodesApp package

# Executar a suite de testes (recria db-tests e corre mvn test)
make -C PatasBigodesApp test

# Recriar o serviço de testes MySQL
make -C PatasBigodesApp db-reset-test

# Mostrar logs agregados da stack
make -C PatasBigodesApp logs

# Entrar no shell da BD
make -C PatasBigodesApp db-shell
```

Se preferires executar os comandos sem `-C`, muda primeiro para a pasta da aplicação:

```bash
cd PatasBigodesApp
make up
```

