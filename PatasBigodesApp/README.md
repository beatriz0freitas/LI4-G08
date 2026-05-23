# PatasBigodesApp

Manual técnico de execução e manutenção da aplicação **PatasBigodesApp**, desenvolvida com Spring Boot e preparada para correr com MySQL via Docker Compose.

## 1. Visão geral

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

Este comando sobe primeiro o serviço MySQL definido em `docker-compose.yml` e só depois executa os testes Maven.

Em alternativa, se a base de dados já estiver ativa, podes executar diretamente:

```bash
mvn test
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
| `make test` | Sobe o MySQL e executa os testes Maven da aplicação. |
| `make package` | Gera o ficheiro JAR com `mvn clean package`. |
| `make run` | Arranca a aplicação localmente com o perfil e credenciais da base de dados configurados via variáveis de ambiente. |
| `make run-mysql` | Alias de `make run`. |
| `make verify` | Sobe o MySQL, corre os testes e valida a configuração do Docker Compose. |
| `make config` | Mostra a configuração final resolvida do Docker Compose. |
| `make db-up` | Sobe apenas o serviço de base de dados MySQL. |
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
- `DB_USERNAME=hoteluser`
- `DB_PASSWORD=hotelpass`

Se precisares de alterar a porta local da base de dados, podes fazê-lo assim:

```bash
HOST_DB_PORT=3308 make up
```

## 7. Estrutura do Docker Compose

O ficheiro `docker-compose.yml` define dois serviços:

- `db`: contentor MySQL 8.0 com volume persistente `mysql_data`.
- `app`: aplicação Spring Boot construída a partir do `Dockerfile` do projeto.

O serviço da aplicação aguarda que a base de dados esteja saudável antes de arrancar.

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
