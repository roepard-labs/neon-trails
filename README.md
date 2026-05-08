# Neon Trails

Juego tipo arena (inspiración *Tron*) en **Java Swing**: dos jugadores locales, discos y modo **moto 5 s**.

## Ejecutar (consola)

```bash
mkdir -p out
javac -d out $(find src -name '*.java')
java -cp out Main
```

## Controles

| Jugador | Movimiento | Disco | Moto (5 s) |
|---------|------------|-------|------------|
| P1 | W A S D | Shift | Q |
| P2 | Flechas | Enter | U |

## Documentación

- Arquitectura: [docs/architecture.md](docs/architecture.md)
- Pruebas manuales: [docs/PRUEBAS.md](docs/PRUEBAS.md)
- Reglas de cátedra: [docs/rules/Proyecto Técnicas de Programación.pdf](docs/rules/Proyecto%20Técnicas%20de%20Programación.pdf)

## Web (stub)

Abre [web/index.html](web/index.html) en el navegador para ver el mock de ranking (`web/mock/leaderboard.json`).
