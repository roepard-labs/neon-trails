# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project at a glance

**Neon Trails** is a two-player local arena game inspired by Tron, written in plain **Java Swing** with no external dependencies and no build tool. It is a deliverable for the *Técnicas de Programación* course; the binding spec is `docs/rules/Proyecto Técnicas de Programación.pdf`, and most structural decisions (the `logic` / `view` / `events` package split, the use of an explicit `Thread` for the game loop, key bindings instead of `KeyListener`) trace back to it. Re-read that PDF before adding screens, ranking, persistence, sounds, or new entities.

The `web/` directory is a **Vue 3 + TypeScript + Vite** single-page app (Tailwind v4 + shadcn-vue/reka-ui): the project's academic presentation, with a permanent **neon-Tron dark theme** and **11 sections**. The last three — *Stack & Código*, *Despliegue*, *Herramientas* — embed real repo code (Java/PHP/Vue/SQL/Docker/nginx/Makefile) highlighted with Shiki, so when you change those source files, update the extracts in `web/src/data/{stack,deploy,tools}.ts`. The *Leaderboard* section consumes `GET /api/scores` from the Laravel backend (same origin in production; in dev the Vite proxy forwards `/api` → `:8000`). Theme tokens live in `web/src/style.css`; animated sprites in `web/public/sprites/`.

## Build and run

Build system: **Maven** (`pom.xml` at repo root). Java 17 LTS. Entry point `Main` (default package). The `Makefile` is a thin wrapper over `mvn`.

```bash
make compile   # mvn compile
make run       # mvn exec:java  (arranca el juego)
make test      # mvn test
make package   # mvn package    (jar en target/)
make verify    # mvn -B verify  (paridad con CI)
make clean     # mvn clean
```

Web app (presentación): `cd web && pnpm install`, then `make web-dev` (Vite dev server; proxies `/api` → `:8000`) or `make web-build` / `make web-lint` / `make web-type-check`. CI runs in `.github/workflows/vue.yml` (Node 24.16.0, `pnpm lint` + `pnpm build`).

## Tests

JUnit 5 (Jupiter) corre vía Surefire. Tests viven en `src/test/java/` siguiendo el layout estándar Maven. La cobertura actual se limita a la capa pura `logic/` (`PlayerTest`, `DiscProjectileTest`, `GameConstantsTest`) — Swing/EDT/`view/` no se prueban con código aún; usar la checklist manual en `docs/PRUEBAS.md` para validar UI y controles.

GitHub Actions (`.github/workflows/java.yml`) ejecuta `mvn -B verify` en push y PR contra `develop` y `master`.

## Architecture

The three core packages are fixed by the assignment spec — do not collapse, rename, or merge them.

| Layer  | Path                          | Role |
|--------|-------------------------------|------|
| Entry  | `src/main/java/Main.java`     | `main()`, schedules `GameGUI.iniciar()` on the EDT |
| Facade | `src/main/java/modules/`      | `GameGUI` — single boot hook; future menu / instructions screens plug in here |
| Logic  | `src/main/java/logic/`        | `GameState`, `Player`, `DiscProjectile`, `GameConstants` — pure simulation, **no Swing imports** |
| View   | `src/main/java/view/`         | `GameWindow` (`JFrame`), `GamePanel` (`JPanel` + render), `GameLoop` (game thread) |
| Events | `src/main/java/events/`       | `InputController` (per-tick input snapshot), `KeyboardBindings` (Swing key bindings) |
| Tests  | `src/test/java/logic/`        | JUnit 5 tests for the pure simulation layer |

### Threading model (load-bearing)

- A single dedicated `Thread` named `neon-trails-game-loop` (`view/GameLoop.java`) drives `GameState.tick()` at ~60 Hz (`GameConstants.GAME_TICK_MS = 16`). The loop calls `panel.repaint()` from the game thread, which queues paint on the Swing EDT.
- `GamePanel` owns one `stateLock`. Both `stepSimulation()` and `paintComponent()` synchronize on it. Any new mutation of game state from outside the loop must take the **same** lock — do not add a second lock.
- `InputController` is written from the EDT (key actions) and read from the game thread. Today it relies on coordinated event flow rather than `volatile` / `AtomicBoolean`. If you add cross-thread fields, prefer the existing edge-event pattern (`requestPxBike` / `consumePxBikeRequest`) or upgrade synchronization deliberately — don't sprinkle ad-hoc reads.

### Input system

Use Swing **Key Bindings** (`InputMap` + `ActionMap`) with `WHEN_IN_FOCUSED_WINDOW`, installed by `KeyboardBindings.install`. Do **not** use `KeyListener` — focus loss between sub-components silently drops a player's keys, which is exactly the bug this project is designed to avoid. Per-key state lives in `InputController` as either:

- a held boolean (movement, fire) toggled by paired press/release bindings, or
- an edge-trigger request (`requestPxBike` / `consumePxBikeRequest`) consumed once per tick.

Two players share one keyboard: P1 = WASD + E (disc) + Q (bike); P2 = arrows + Enter (disc) + U (bike). Keep both reachable on a single layout when adding keys.

### Game rules (current baseline)

`GameState.tick` runs movement → cooldown → shoot → bike-request → disc update → hit resolution. Bike mode lasts 5 s (`BIKE_DURATION_SEC`, a hard requirement from the spec). Discs expire at the screen edge or after `DISC_MAX_TICKS`; a freshly fired disc has `friendlyTicks = 8` so it can't immediately hit its owner. A hit awards 1 point to the opponent, clears all discs, and respawns both players to opposite corners — there is no game-over screen yet.

## Conventions

- **Language**: comments, JavaDoc, and public method names are in **Spanish** (`iniciar`, `mostrar`, `consumeP1BikeRequest`-style mixed when the verb is a Java idiom). Match the surrounding file — do not translate existing Spanish code to English.
- **Comments**: keep `// NOTE:`, `// TODO:`, `// FIXME:` markers when they capture non-obvious context (threading, focus quirks, spec requirements). They are the project's main way of recording "why".
- **Scope**: changes stay minimal and task-focused. The assignment penalizes scope creep; do not refactor adjacent code "while you're there".
- **Tunables**: balance/dimension constants belong in `GameConstants`. Add new ones there rather than scattering literals across files.
- **Compiled output**: Maven escribe en `target/` y está ignorado en `.gitignore`. El antiguo `out/` (`javac` legacy) fue removido del repo y también está ignorado.

## Where to look first

- `AGENTS.md` — extended human/agent-facing guide; aligned with this file and goes deeper on conventions and the per-feature checklist.
- `docs/architecture.md` — the package model and tick/render flow.
- `docs/PRUEBAS.md` — manual QA checklist; the closest thing this project has to a test suite.
- `docs/rules/Proyecto Técnicas de Programación.pdf` — binding academic spec.
