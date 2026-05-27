.DEFAULT_GOAL := help

# ─── Java 21 ──────────────────────────────────────────────────────────────────
LINUX_JAVA_HOME ?= /usr/lib/jvm/jdk-21.0.2+13
JAVA_HOME ?= $(shell \
	if [ -d "$(LINUX_JAVA_HOME)" ]; then \
		echo "$(LINUX_JAVA_HOME)"; \
	elif [ -x /usr/libexec/java_home ]; then \
		/usr/libexec/java_home -v 21 2>/dev/null || /usr/libexec/java_home 2>/dev/null; \
	fi)
export JAVA_HOME

# ─── Base de dados ────────────────────────────────────────────────────────────
DB_HOST      ?= localhost
DB_PORT      ?= 3307
DB_NAME      ?= hotelanimais
DB_USERNAME  ?= hoteluser
DB_PASSWORD  ?= hotelpass

TEST_DB_PORT ?= 3308
TEST_DB_NAME ?= hotelanimais_test

export DB_PORT TEST_DB_PORT

# ─── Variáveis internas ───────────────────────────────────────────────────────
TEST_ENV = DB_HOST=$(DB_HOST) DB_PORT=$(TEST_DB_PORT) DB_NAME=$(TEST_DB_NAME) \
           DB_USERNAME=$(DB_USERNAME) DB_PASSWORD=$(DB_PASSWORD)

# Testes que precisam de Docker (excluídos dos alvos *-unit)
UNIT_EXCLUDES := "!ReservaServiceTests,!TutorServiceTimingTests,!PagamentoCheckInCalculoTest,\
!CheckInServiceTest,!CheckOutSequenceServiceTest,!CheckInServiceSimpleTest,\
!LimpezaControllerTest,!HotelAnimaisApplicationTests,!DashboardServiceIntegrationTest,\
!CheckInIntegrationTest,!CheckOutIntegrationTest,!DisponibilidadeIntegrationTest,\
!PagamentoCheckInIntegrationTest,!ReservaCancelIntegrationTest,!ReservaConfirmIntegrationTest,\
!ReservaCreateIntegrationTest,!SecurityIntegrationTest,!AlojamentoRepositoryIntegrationTest,\
!HistoricoRepositoryIntegrationTest,!DashboardControllerTest,!ReservaRenderingControllerTest,\
!ReservaWizardControllerTest,!TutorAnimalControllerTemplateTest,!AlojamentoServiceTimingTests,\
!ConcurrentAccessTest,!PlaywrightE2ETest"

UNIT_FLAGS = -Dtest=$(UNIT_EXCLUDES) -Dsurefire.failIfNoSpecifiedTests=false

.PHONY: help \
        test test-unit test-integration \
        coverage coverage-unit open-coverage \
        mutation open-mutation \
        e2e playwright-install concurrent-test backup-test \
        run package \
        db-up db-reset-test db-stop db-shell \
        up down restart destroy ps logs \
        docs clean
help:
	@echo ""
	@echo "  PatasBigodesApp — comandos disponíveis"
	@echo ""
	@echo "  TESTES"
	@echo "  make test-unit         Unitários + WebMvcTest (sem Docker)"
	@echo "  make test-integration  Integração com MySQL (requer Docker)"
	@echo "  make test              Todos os testes (requer Docker)"
	@echo ""
	@echo "  COBERTURA & MUTAÇÃO"
	@echo "  make coverage-unit     JaCoCo — só unitários"
	@echo "  make coverage          JaCoCo — todos os testes (requer Docker)"
	@echo "  make open-coverage     Abrir relatório JaCoCo no browser"
	@echo "  make mutation          PITest mutation testing (sem Docker)"
	@echo "  make open-mutation     Abrir relatório PITest no browser"
	@echo ""
	@echo "  TESTES NÃO-FUNCIONAIS"
	@echo "  make concurrent-test   RNF-01: 10 utilizadores em simultâneo"
	@echo "  make e2e               E2E com Playwright (requer Docker)"
	@echo "  make playwright-install  Instalar browsers Playwright (uma vez)"
	@echo "  make backup-test       RNF-08: backup e recuperação"
	@echo ""
	@echo "  APLICAÇÃO"
	@echo "  make run               Arrancar a app (requer db-up)"
	@echo "  make package           Gerar jar Spring Boot"
	@echo ""
	@echo "  BASE DE DADOS"
	@echo "  make db-up             Subir MySQL principal"
	@echo "  make db-reset-test     Recriar BD de testes"
	@echo "  make db-stop           Parar MySQL principal"
	@echo "  make db-shell          Abrir cliente MySQL"
	@echo ""
	@echo "  INFRA"
	@echo "  make up                Subir stack completa (db + app)"
	@echo "  make down              Parar stack"
	@echo "  make restart           Rebuild e reiniciar stack"
	@echo "  make destroy           Parar stack e apagar volumes"
	@echo "  make ps                Estado dos contentores"
	@echo "  make logs              Logs da stack (seguir)"
	@echo ""
	@echo "  make docs              Gerar Javadoc"
	@echo "  make clean             Limpar artefactos Maven"
	@echo ""

test-unit:
	mvn test $(UNIT_FLAGS)

test-integration: db-reset-test
	$(TEST_ENV) mvn test

test: db-reset-test
	$(TEST_ENV) mvn test

coverage-unit:
	mvn test jacoco:report $(UNIT_FLAGS)
	@echo "→ target/site/jacoco/index.html"

coverage: db-reset-test
	$(TEST_ENV) mvn test jacoco:report
	@echo "→ target/site/jacoco/index.html"

open-coverage:
	@xdg-open target/site/jacoco/index.html 2>/dev/null || \
	 open     target/site/jacoco/index.html 2>/dev/null || \
	 echo "Abre manualmente: target/site/jacoco/index.html"

mutation:
	mvn org.pitest:pitest-maven:mutationCoverage $(UNIT_FLAGS)
	@echo "→ target/pit-reports/index.html"

open-mutation:
	@xdg-open target/pit-reports/index.html 2>/dev/null || \
	 open     target/pit-reports/index.html 2>/dev/null || \
	 echo "Abre manualmente: target/pit-reports/index.html"

concurrent-test: db-reset-test
	$(TEST_ENV) mvn test -Dtest="ConcurrentAccessTest" -Dsurefire.failIfNoSpecifiedTests=false

playwright-install:
	mvn exec:java -e \
	  -Dexec.mainClass=com.microsoft.playwright.CLI \
	  -Dexec.args="install chromium" \
	  -Dexec.classpathScope=test

e2e: db-reset-test
	$(TEST_ENV) mvn test -Dtest="PlaywrightE2ETest" -Dsurefire.failIfNoSpecifiedTests=false

backup-test: db-reset-test
	mvn flyway:migrate \
	  -Dflyway.url="jdbc:mysql://$(DB_HOST):$(TEST_DB_PORT)/$(TEST_DB_NAME)?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Europe/Lisbon" \
	  -Dflyway.user=$(DB_USERNAME) -Dflyway.password=$(DB_PASSWORD) \
	  -Dflyway.locations=classpath:db/migration -q
	TEST_DB_NAME=$(TEST_DB_NAME) DB_USERNAME=$(DB_USERNAME) DB_PASSWORD=$(DB_PASSWORD) \
	  bash scripts/test-backup-recovery.sh
run:
	DB_HOST=$(DB_HOST) DB_PORT=$(DB_PORT) DB_NAME=$(DB_NAME) \
	  DB_USERNAME=$(DB_USERNAME) DB_PASSWORD=$(DB_PASSWORD) \
	  mvn spring-boot:run

package:
	mvn clean package

db-up:
	docker compose up -d db

db-reset-test:
	docker compose up -d --wait --force-recreate db-tests

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

docs:
	mvn javadoc:javadoc -DskipTests
	@echo "→ target/generated-docs/index.html"

clean:
	mvn clean