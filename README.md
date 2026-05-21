# Neon Trails

Juego tipo arena (inspiración *Tron*) en **Java Swing**: dos jugadores locales, discos y modo **moto 5 s**.

## Ejecutar (consola)

Requiere **JDK 17** y **Maven**. Desde la raíz:

```bash
make run        # mvn exec:java  (arranca el juego)
make test       # mvn test       (JUnit 5)
make verify     # mvn -B verify  (compila + tests + jar)
```

El `Makefile` es un wrapper sobre `mvn`; ver `make help` para todos los targets.

## Controles

| Jugador | Movimiento | Disco | Moto (5 s) |
|---------|------------|-------|------------|
| P1 | W A S D | E | Q |
| P2 | Flechas | Enter | U |

## Documentación

- Arquitectura: [docs/architecture.md](docs/architecture.md)
- Pruebas manuales: [docs/PRUEBAS.md](docs/PRUEBAS.md)
- Reglas de cátedra: [docs/rules/Proyecto Técnicas de Programación.pdf](docs/rules/Proyecto%20Técnicas%20de%20Programación.pdf)

## Web (stub)

Abre [web/index.html](web/index.html) en el navegador para ver el mock de ranking (`web/mock/leaderboard.json`).
