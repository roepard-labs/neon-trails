# Neon Trails — wrapper fino sobre Maven.
# Comandos pensados para uso local y paridad con GitHub Actions.

MVN ?= mvn

.PHONY: help compile test run package verify clean

help: ## Lista los targets disponibles
	@echo "Targets disponibles:"
	@echo "  make compile   - Compila las clases (mvn compile)"
	@echo "  make test      - Ejecuta los tests JUnit (mvn test)"
	@echo "  make run       - Arranca el juego (mvn exec:java)"
	@echo "  make package   - Genera el jar en target/ (mvn package)"
	@echo "  make verify    - Pipeline completo: compile + test + package (mvn -B verify)"
	@echo "  make clean     - Limpia target/ (mvn clean)"

compile:
	$(MVN) -q compile

test:
	$(MVN) -q test

run:
	$(MVN) -q exec:java

package:
	$(MVN) -q package

verify:
	$(MVN) -B verify

clean:
	$(MVN) -q clean
