# Neon Trails — Makefile central (orquesta Java, Vue y Laravel).
# `make` sin argumentos muestra la ayuda. Los targets usan prefijos por capa:
#   java-*   el juego (Maven)      web-*   la SPA Vue (pnpm)
#   api-*    el backend Laravel    docker- / up / down / logs   el monolito
MVN  ?= mvn
PNPM ?= pnpm
PHP  ?= php
WEB_DIR ?= web
API_DIR ?= api
IMAGE   ?= neon-trails:local

.DEFAULT_GOAL := help
.PHONY: help \
	java-compile java-test java-run java-package java-verify java-clean \
	compile test run package verify clean \
	web-install web-dev web-build web-lint web-type-check \
	api-install api-key api-migrate api-seed api-serve api-test api-filament-user \
	docker-build up down logs deploy-check

help: ## Muestra esta ayuda
	@echo "Neon Trails — targets disponibles:"
	@grep -E '^[a-zA-Z0-9_-]+:.*?## ' $(MAKEFILE_LIST) \
		| awk 'BEGIN{FS=":.*?## "}{printf "  \033[36m%-22s\033[0m %s\n", $$1, $$2}'

## ── Java (juego) ──────────────────────────────────────────────
java-compile: ## Compila las clases del juego
	$(MVN) -q compile
java-test: ## Ejecuta los tests JUnit
	$(MVN) -q test
java-run: ## Arranca el juego (mvn exec:java)
	$(MVN) -q exec:java
java-package: ## Genera el fat jar en target/
	$(MVN) -q package
java-verify: ## Pipeline Maven completo (paridad con CI)
	$(MVN) -B verify
java-clean: ## Limpia target/
	$(MVN) -q clean

# Aliases retrocompatibles (CLAUDE.md / AGENTS.md)
compile: java-compile
test: java-test
run: java-run
package: java-package
verify: java-verify
clean: java-clean

## ── Web (Vue) ─────────────────────────────────────────────────
web-install: ## Instala dependencias del front
	cd $(WEB_DIR) && $(PNPM) install --frozen-lockfile
web-dev: ## Servidor de desarrollo Vite
	cd $(WEB_DIR) && $(PNPM) dev
web-build: ## Build de producción (web/dist)
	cd $(WEB_DIR) && $(PNPM) build
web-lint: ## Lint (oxlint + eslint)
	cd $(WEB_DIR) && $(PNPM) lint
web-type-check: ## Chequeo de tipos (vue-tsc)
	cd $(WEB_DIR) && $(PNPM) type-check

## ── API (Laravel) ─────────────────────────────────────────────
api-install: ## Instala dependencias de Composer
	cd $(API_DIR) && composer install
api-key: ## Genera APP_KEY
	cd $(API_DIR) && $(PHP) artisan key:generate
api-migrate: ## Ejecuta las migraciones
	cd $(API_DIR) && $(PHP) artisan migrate
api-seed: ## Siembra datos de ejemplo (admin + puntajes)
	cd $(API_DIR) && $(PHP) artisan db:seed
api-serve: ## Servidor local de Laravel (:8000)
	cd $(API_DIR) && $(PHP) artisan serve
api-test: ## Tests Pest del API
	cd $(API_DIR) && $(PHP) artisan test --compact
api-filament-user: ## Crea un usuario admin de Filament
	cd $(API_DIR) && $(PHP) artisan make:filament-user

## ── Docker / deploy (monolito + MariaDB local) ────────────────
docker-build: ## Construye la imagen monolítica
	docker build -t $(IMAGE) .
up: ## Levanta el monolito + MariaDB (compose)
	docker compose up -d --build
down: ## Detiene y elimina los contenedores
	docker compose down
logs: ## Sigue los logs del monolito
	docker compose logs -f app
deploy-check: ## Valida el Dockerfile sin construir (buildkit --check)
	docker build --check .
