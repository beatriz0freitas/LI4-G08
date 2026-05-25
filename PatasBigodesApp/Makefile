.DEFAULT_GOAL := help

# Java 21 (necessário para Spring Boot 3.x; ajusta o caminho se instalado noutra localização)
JAVA_HOME ?= /usr/lib/jvm/jdk-21.0.2+13
export JAVA_HOME

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

.PHONY: help test test-unit test-integration coverage coverage-unit mutation open-coverage open-mutation package run run-mysql verify config db-up db-reset-test db-stop db-shell up down restart destroy ps logs logs-app logs-db docs docs-code docs-clean clean

help:
	@echo ""
	@echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
	@echo "  PatasBigodesApp — Comandos disponíveis"
	@echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
	@echo ""
	@echo "  TESTES"
	@echo "  make test-unit         Testes unitários + WebMvcTest (sem Docker)"
	@echo "  make test-integration  Testes de integração com MySQL (requer Docker)"
	@echo "  make test              Todos os testes (requer Docker)"
	@echo ""
	@echo "  COBERTURA"
	@echo "  make coverage-unit     JaCoCo para testes unitários (sem Docker)"
	@echo "  make coverage          JaCoCo para todos os testes (requer Docker)"
	@echo "  make open-coverage     Abrir relatório JaCoCo no browser"
	@echo ""
	@echo "  MUTATION TESTING"
	@echo "  make mutation          PITest mutation coverage (sem Docker)"
	@echo "  make open-mutation     Abrir relatório PITest no browser"
	@echo ""
	@echo "  APLICAÇÃO"
	@echo "  make run               Arrancar a app (requer Docker para MySQL)"
	@echo "  make up                Subir stack completa (db + app)"
	@echo "  make down              Parar e remover a stack completa"
	@echo "  make restart           Reiniciar a stack"
	@echo "  make package           Gerar jar Spring Boot"
	@echo ""
	@echo "  BASE DE DADOS"
	@echo "  make db-up             Subir apenas MySQL"
	@echo "  make db-reset-test     Recriar BD de testes"
	@echo "  make db-stop           Parar MySQL"
	@echo "  make db-shell          Abrir cliente MySQL"
	@echo ""
	@echo "  OUTROS"
	@echo "  make ps                Estado dos contentores"
	@echo "  make logs              Logs da stack"
	@echo "  make docs              Gerar Javadoc"
	@echo "  make clean             Limpar artefactos Maven"
	@echo "  make verify            Validar testes + docker compose config"
	@echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
	@echo ""

# Testes unitários e WebMvcTest — não precisam de Docker
UNIT_EXCLUDES := "!ReservaServiceTests,!TutorServiceTimingTests,!PagamentoCheckInCalculoTest,\
!CheckInServiceTest,!CheckOutSequenceServiceTest,!CheckInServiceSimpleTest,\
!LimpezaControllerTest,!HotelAnimaisApplicationTests,!DashboardServiceIntegrationTest,\
!CheckInIntegrationTest,!CheckOutIntegrationTest,!DisponibilidadeIntegrationTest,\
!PagamentoCheckInIntegrationTest,!ReservaCancelIntegrationTest,!ReservaConfirmIntegrationTest,\
!ReservaCreateIntegrationTest,!SecurityIntegrationTest,!AlojamentoRepositoryIntegrationTest,\
!HistoricoRepositoryIntegrationTest,!DashboardControllerTest,!ReservaRenderingControllerTest,\
!ReservaWizardControllerTest,!TutorAnimalControllerTemplateTest,!AlojamentoServiceTimingTests"

test-unit:
	@echo "→ A correr testes unitários (sem Docker)..."
	mvn test -Dtest=$(UNIT_EXCLUDES) -Dsurefire.failIfNoSpecifiedTests=false
	@echo "→ Concluído."

test-integration:
	@echo "→ A iniciar MySQL de testes..."
	$(MAKE) db-reset-test
	@echo "→ A correr testes de integração..."
	DB_HOST=$(DB_HOST) DB_PORT=$(TEST_DB_PORT) DB_NAME=$(TEST_DB_NAME) \
	  DB_USERNAME=$(DB_USERNAME) DB_PASSWORD=$(DB_PASSWORD) \
	  mvn test
	@echo "→ Concluído."

test:
	$(MAKE) db-reset-test
	DB_HOST=$(DB_HOST) DB_PORT=$(TEST_DB_PORT) DB_NAME=$(TEST_DB_NAME) \
	  DB_USERNAME=$(DB_USERNAME) DB_PASSWORD=$(DB_PASSWORD) \
	  mvn test

coverage-unit:
	@echo "→ A correr testes unitários com JaCoCo (sem Docker)..."
	mvn test jacoco:report -Dtest=$(UNIT_EXCLUDES) -Dsurefire.failIfNoSpecifiedTests=false
	@echo ""
	@echo "→ Relatório gerado em: target/site/jacoco/index.html"
	@echo "   Usa 'make open-coverage' para abrir no browser."

coverage:
	$(MAKE) db-reset-test
	DB_HOST=$(DB_HOST) DB_PORT=$(TEST_DB_PORT) DB_NAME=$(TEST_DB_NAME) \
	  DB_USERNAME=$(DB_USERNAME) DB_PASSWORD=$(DB_PASSWORD) \
	  mvn test jacoco:report
	@echo "→ Relatório JaCoCo em: target/site/jacoco/index.html"

open-coverage:
	@xdg-open target/site/jacoco/index.html 2>/dev/null || open target/site/jacoco/index.html 2>/dev/null || \
	  echo "Abre manualmente: target/site/jacoco/index.html"

mutation:
	@echo "→ A correr PITest mutation testing (pode demorar alguns minutos)..."
	mvn org.pitest:pitest-maven:mutationCoverage \
	  -Dtest=$(UNIT_EXCLUDES) -Dsurefire.failIfNoSpecifiedTests=false
	@echo ""
	@echo "→ Relatório PITest em: target/pit-reports/index.html"
	@echo "   Usa 'make open-mutation' para abrir no browser."

open-mutation:
	@xdg-open target/pit-reports/index.html 2>/dev/null || open target/pit-reports/index.html 2>/dev/null || \
	  echo "Abre manualmente: target/pit-reports/index.html"

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
