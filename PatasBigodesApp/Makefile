.DEFAULT_GOAL := help

DB_HOST ?= localhost
HOST_DB_PORT ?= 3307
DB_PORT ?= $(HOST_DB_PORT)
DB_NAME ?= hotelanimais
TEST_HOST_DB_PORT ?= 3308
TEST_DB_PORT ?= $(TEST_HOST_DB_PORT)
TEST_DB_NAME ?= hotelanimais_test
DB_USERNAME ?= hoteluser
DB_PASSWORD ?= hotelpass

export HOST_DB_PORT
export TEST_HOST_DB_PORT

.PHONY: help test package run run-mysql verify config db-up db-reset-test db-stop db-shell up down restart destroy ps logs logs-app logs-db docs docs-code docs-clean clean

help:
	@echo "Alvos disponíveis:"
	@echo "  make help        - Mostrar esta ajuda"
	@echo "  make test        - Executar testes"
	@echo "  make package     - Gerar jar Spring Boot"
	@echo "  make run         - Arrancar a app localmente contra MySQL (Docker Compose)"
	@echo "  make run-mysql   - Alias de run"
	@echo "  make verify      - Validar testes e docker compose config"
	@echo "  make config      - Mostrar a configuração final do docker compose"
	@echo "  make db-up       - Subir apenas a base de dados MySQL em Docker"
	@echo "  make db-reset-test - Recriar a base de dados MySQL de testes"
	@echo "  make db-stop     - Parar apenas a base de dados MySQL"
	@echo "  make db-shell    - Abrir cliente MySQL dentro do contentor db"
	@echo "  make up          - Subir a stack completa (db + app)"
	@echo "  make down        - Parar e remover a stack completa"
	@echo "  make restart     - Reiniciar a stack completa"
	@echo "  make destroy     - Parar a stack e remover volumes"
	@echo "  make ps          - Mostrar estado dos contentores"
	@echo "  make logs        - Mostrar logs agregados da stack"
	@echo "  make logs-app    - Mostrar logs da app"
	@echo "  make logs-db     - Mostrar logs da base de dados"
	@echo "  make docs        - Gerar documentação Javadoc HTML"
	@echo "  make docs-clean  - Remover Javadoc HTML gerado"
	@echo "  make clean       - Limpar artefactos Maven"

test:
	make db-reset-test
	DB_HOST=$(DB_HOST) DB_PORT=$(TEST_DB_PORT) DB_NAME=$(TEST_DB_NAME) DB_USERNAME=$(DB_USERNAME) DB_PASSWORD=$(DB_PASSWORD) mvn test

package:
	mvn clean package

run:
	DB_HOST=$(DB_HOST) DB_PORT=$(DB_PORT) DB_NAME=$(DB_NAME) DB_USERNAME=$(DB_USERNAME) DB_PASSWORD=$(DB_PASSWORD) mvn spring-boot:run

run-mysql: run

verify:
	make db-reset-test
	DB_HOST=$(DB_HOST) DB_PORT=$(TEST_DB_PORT) DB_NAME=$(TEST_DB_NAME) DB_USERNAME=$(DB_USERNAME) DB_PASSWORD=$(DB_PASSWORD) mvn test
	docker compose config

config:
	docker compose config

db-up:
	docker compose up -d db

db-reset-test:
	docker compose rm -sf db-tests
	docker compose up -d --wait db-tests

db-stop:
	docker compose stop db

db-shell:
	docker compose exec db mysql -u$(DB_USERNAME) -p$(DB_PASSWORD) $(DB_NAME)

up:
	docker compose up -d --build

down:
	docker compose down

restart:
	docker compose down
	docker compose up -d --build

destroy:
	docker compose down -v --remove-orphans

ps:
	docker compose ps

logs:
	docker compose logs -f

logs-app:
	docker compose logs -f app

logs-db:
	docker compose logs -f db

docs: 
	mvn javadoc:javadoc -DskipTests
	@echo "Documentação gerada:"
	@echo "  Código: target/generated-docs/index.html"

docs-clean:
	rm -rf target/generated-docs

clean:
	mvn clean
