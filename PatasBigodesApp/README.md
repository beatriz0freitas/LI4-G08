# PatasBigodesApp

Manual técnico de execução e manutenção da aplicação **PatasBigodesApp**, desenvolvida com Spring Boot e preparada para correr com MySQL via Docker Compose.## 1. Visão geral

A aplicação expõe-se na porta `8080` e, por defeito, trabalha com o perfil `mysql`. Isso significa que a base de dados deve estar disponível antes de arrancar a aplicação em modo local.

O ambiente de desenvolvimento e execução foi pensado para dois cenários principais:

1. Subir tudo de uma vez com Docker Compose.
2. Subir apenas a base de dados e arrancar a aplicação localmente com Maven.

## 2. Pré-requisitos

Antes de executar a aplicação, confirma que tens instalados os seguintes componentes:

- Java 21
- Maven 3.9.x ou compatível
- Docker e Docker Compose

## 3. Execução da aplicação

### 3.1. Opção recomendada: subir a stack completa com Docker Compose

Esta opção levanta a base de dados MySQL e a aplicação Java no mesmo comando.

```bash
make up
```

Depois de iniciar, a aplicação fica disponível em:

- `http://localhost:8080`

A base de dados fica exposta localmente na porta `3307` por omissão.

### 3.2. Opção manual: subir primeiro a base de dados e depois correr a aplicação


1. Subir apenas o MySQL:

```bash
make db-up
```

2. Arrancar a aplicação localmente:

```bash
make run
```

O comando `make run` injeta as variáveis necessárias para a ligação à base de dados e executa:

```bash
mvn spring-boot:run
```

### 3.3. Arranque direto com Docker Compose

Se preferires não usar o `Makefile`, podes executar diretamente:

```bash
docker compose up -d --build
```

Isto sobe os serviços definidos em `docker-compose.yml`.

## 4. Execução dos testes

Para correr a suite de testes, usa:

```bash
make test
```

Este comando recria o serviço MySQL `db-tests`, definido em `docker-compose.yml`, e só depois executa os testes Maven contra a base `hotelanimais_test`.

Em alternativa, se a base de dados já estiver ativa, podes executar diretamente:

```bash
mvn test
```

Neste caso, garante primeiro que o serviço `db-tests` está ativo e limpo, por exemplo com:

```bash
make db-reset-test
```

Se quiseres validar simultaneamente os testes e a configuração do Docker Compose, usa:

```bash
make verify
```

## 5. Comandos do Makefile

O `Makefile` funciona como camada de atalho para tarefas comuns do projeto.

| Comando | Descrição |
| --- | --- |
| `make help` | Mostra a lista de alvos disponíveis e a respetiva função. |
| `make test` | Recria o serviço `db-tests` e executa os testes Maven da aplicação. |
| `make package` | Gera o ficheiro JAR com `mvn clean package`. |
| `make run` | Arranca a aplicação localmente com o perfil e credenciais da base de dados configurados via variáveis de ambiente. |
| `make run-mysql` | Alias de `make run`. |
| `make verify` | Recria o serviço `db-tests`, corre os testes e valida a configuração do Docker Compose. |
| `make config` | Mostra a configuração final resolvida do Docker Compose. |
| `make db-up` | Sobe apenas o serviço de base de dados MySQL. |
| `make db-reset-test` | Recria o serviço MySQL de testes `db-tests`. |
| `make db-stop` | Pára apenas o serviço de base de dados MySQL. |
| `make db-shell` | Abre um cliente MySQL dentro do contentor da base de dados. |
| `make up` | Sobe a stack completa com rebuild das imagens. |
| `make down` | Para e remove a stack completa. |
| `make restart` | Reinicia a stack completa. |
| `make destroy` | Para a stack e remove também os volumes persistentes. |
| `make ps` | Mostra o estado atual dos contentores. |
| `make logs` | Mostra os logs agregados da stack. |
| `make logs-app` | Mostra apenas os logs da aplicação. |
| `make logs-db` | Mostra apenas os logs da base de dados. |
| `make clean` | Limpa artefactos gerados pelo Maven. |

## 6. Variáveis de configuração relevantes

O `Makefile` usa estas variáveis por omissão:

- `DB_HOST=localhost`
- `HOST_DB_PORT=3307`
- `DB_PORT=3307`
- `DB_NAME=hotelanimais`
- `TEST_HOST_DB_PORT=3308`
- `TEST_DB_PORT=3308`
- `TEST_DB_NAME=hotelanimais_test`
- `DB_USERNAME=hoteluser`
- `DB_PASSWORD=hotelpass`

Se precisares de alterar a porta local da base de dados, podes fazê-lo assim:

```bash
HOST_DB_PORT=3309 make up
```

## 7. Estrutura do Docker Compose

O ficheiro `docker-compose.yml` define dois serviços:

- `db`: contentor MySQL 8.0 com volume persistente `mysql_data`.
- `db-tests`: contentor MySQL 8.0 exclusivo para testes, sem volume persistente.
- `app`: aplicação Spring Boot construída a partir do `Dockerfile` do projeto.

O serviço da aplicação aguarda que a base de dados esteja saudável antes de arrancar.
O serviço `db` disponibiliza a base principal `hotelanimais`; o serviço `db-tests` disponibiliza `hotelanimais_test` e é recriado pelo `Makefile` antes da suite.

## 8. Notas operacionais

- A aplicação arranca com `spring.profiles.active=mysql` por defeito.
- O perfil MySQL espera uma base de dados acessível pelas variáveis `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME` e `DB_PASSWORD`.
- O volume `mysql_data` preserva os dados mesmo quando os contentores são recriados.
- O alvo `make destroy` remove também esse volume, pelo que deve ser usado com cuidado.

## 9. Encerramento do ambiente

Para parar a stack completa:

```bash
make down
```

Se também quiseres eliminar os dados persistidos:

```bash
make destroy
```

## 10. Sistema de interface

A aplicação usa uma base de estilos partilhada e simples:

- `src/main/resources/static/css/_tokens.scss` — fonte de tokens Sass com as cores, raios e sombras do sistema.
- `src/main/resources/static/css/ui-tokens.css` — stylesheet final consumido pelos templates.

As páginas principais usam classes semânticas para estados e contexto visual:

- `.status-success`, `.status-warning`, `.status-info`, `.status-danger`
- `.section-tag`
- `.filter-panel`
- `.empty-state`

Isto mantém a linguagem visual coerente sem duplicar estilos dentro dos templates.


Alunos:

- A106804 — Alice Isabel Faria Soares
- A106853 — Ana Beatriz Ribeiro Freitas
- A107365 — Beatriz Martins Miranda
- A107367 — João Paulo Batista Azevedo