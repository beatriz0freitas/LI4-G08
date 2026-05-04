.DEFAULT_GOAL := help

MVN := mvn
COMPOSE := docker compose

DB_HOST ?= localhost
HOST_DB_PORT ?= 3307
DB_PORT ?= $(HOST_DB_PORT)
DB_NAME ?= hotelanimais
DB_USERNAME ?= hoteluser
DB_PASSWORD ?= hotelpass

export HOST_DB_PORT

.PHONY: help test package run run-mysql verify config db-up db-stop db-shell up down restart destroy ps logs logs-app logs-db clean

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
	@echo "  make clean       - Limpar artefactos Maven"
	@echo ""
	@echo "Variáveis:"
	@echo "  HOST_DB_PORT     - Porta MySQL publicada no host (default: 3307)"
	@echo "  DB_PORT          - Porta usada pela app local (default: HOST_DB_PORT)"

test:
	$(MVN) test

package:
	$(MVN) clean package

run:
	DB_HOST=$(DB_HOST) DB_PORT=$(DB_PORT) DB_NAME=$(DB_NAME) DB_USERNAME=$(DB_USERNAME) DB_PASSWORD=$(DB_PASSWORD) $(MVN) spring-boot:run

run-mysql: run

verify:
	$(MVN) test
	$(COMPOSE) config

config:
	$(COMPOSE) config

db-up:
	$(COMPOSE) up -d db

db-stop:
	$(COMPOSE) stop db

db-shell:
	$(COMPOSE) exec db mysql -u$(DB_USERNAME) -p$(DB_PASSWORD) $(DB_NAME)

up:
	$(COMPOSE) up -d --build

down:
	$(COMPOSE) down

restart:
	$(COMPOSE) down
	$(COMPOSE) up -d --build

destroy:
	$(COMPOSE) down -v --remove-orphans

ps:
	$(COMPOSE) ps

logs:
	$(COMPOSE) logs -f

logs-app:
	$(COMPOSE) logs -f app

logs-db:
	$(COMPOSE) logs -f db

clean:
	$(MVN) clean
