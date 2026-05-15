# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project at a glance

**Neon Trails** is a two-player local arena game inspired by Tron, written in plain **Java Swing** with no external dependencies and no build tool. It is a deliverable for the *Técnicas de Programación* course; the binding spec is `docs/rules/Proyecto Técnicas de Programación.pdf`, and most structural decisions (the `logic` / `view` / `events` package split, the use of an explicit `Thread` for the game loop, key bindings instead of `KeyListener`) trace back to it. Re-read that PDF before adding screens, ranking, persistence, sounds, or new entities.

The `web/` directory is a static HTML/CSS/JS leaderboard mock. It does **not** talk to the Java game and is intentionally a stub.

## Build and run

No Maven/Gradle/npm. Plain `javac` from the repo root, classpath `out/`, entry point `Main` (default package).

```bash
# Compile every .java in src into out/
mkdir -p out
javac -d out $(find src -name '*.java')

# Run the game
java -cp out Main
```

Web stub: open `web/index.html` directly in a browser. `app.js` falls back to embedded mock data if `fetch` is blocked under `file://`.

## Tests

No automated test framework is configured. `tests/unit/` exists but is empty. Validation is manual — work through the checklist in `docs/PRUEBAS.md` before declaring a feature done. If you add JUnit, put sources under `tests/unit/` and document the runner here; do not drop ad-hoc test files into `src/`.

## Architecture

The three core packages are fixed by the assignment spec — do not collapse, rename, or merge them.

| Layer  | Path                  | Role |
|--------|-----------------------|------|
| Entry  | `src/Main.java`       | `main()`, schedules `GameGUI.iniciar()` on the EDT |
| Facade | `src/modules/`        | `GameGUI` — single boot hook; future menu / instructions screens plug in here |
| Logic  | `src/logic/`          | `GameState`, `Player`, `DiscProjectile`, `GameConstants` — pure simulation, **no Swing imports** |
| View   | `src/view/`           | `GameWindow` (`JFrame`), `GamePanel` (`JPanel` + render), `GameLoop` (game thread) |
| Events | `src/events/`         | `InputController` (per-tick input snapshot), `KeyboardBindings` (Swing key bindings) |

### Threading model (load-bearing)

- A single dedicated `Thread` named `neon-trails-game-loop` (`view/GameLoop.java`) drives `GameState.tick()` at ~60 Hz (`GameConstants.GAME_TICK_MS = 16`). The loop calls `panel.repaint()` from the game thread, which queues paint on the Swing EDT.
- `GamePanel` owns one `stateLock`. Both `stepSimulation()` and `paintComponent()` synchronize on it. Any new mutation of game state from outside the loop must take the **same** lock — do not add a second lock.
- `InputController` is written from the EDT (key actions) and read from the game thread. Today it relies on coordinated event flow rather than `volatile` / `AtomicBoolean`. If you add cross-thread fields, prefer the existing edge-event pattern (`requestPxBike` / `consumePxBikeRequest`) or upgrade synchronization deliberately — don't sprinkle ad-hoc reads.

### Input system

Use Swing **Key Bindings** (`InputMap` + `ActionMap`) with `WHEN_IN_FOCUSED_WINDOW`, installed by `KeyboardBindings.install`. Do **not** use `KeyListener` — focus loss between sub-components silently drops a player's keys, which is exactly the bug this project is designed to avoid. Per-key state lives in `InputController` as either:

- a held boolean (movement, fire) toggled by paired press/release bindings, or
- an edge-trigger request (`requestPxBike` / `consumePxBikeRequest`) consumed once per tick.

Two players share one keyboard: P1 = WASD + Shift (disc) + Q (bike); P2 = arrows + Enter (disc) + U (bike). Keep both reachable on a single layout when adding keys.

### Game rules (current baseline)

`GameState.tick` runs movement → cooldown → shoot → bike-request → disc update → hit resolution. Bike mode lasts 5 s (`BIKE_DURATION_SEC`, a hard requirement from the spec). Discs expire at the screen edge or after `DISC_MAX_TICKS`; a freshly fired disc has `friendlyTicks = 8` so it can't immediately hit its owner. A hit awards 1 point to the opponent, clears all discs, and respawns both players to opposite corners — there is no game-over screen yet.

## Conventions

- **Language**: comments, JavaDoc, and public method names are in **Spanish** (`iniciar`, `mostrar`, `consumeP1BikeRequest`-style mixed when the verb is a Java idiom). Match the surrounding file — do not translate existing Spanish code to English.
- **Comments**: keep `// NOTE:`, `// TODO:`, `// FIXME:` markers when they capture non-obvious context (threading, focus quirks, spec requirements). They are the project's main way of recording "why".
- **Scope**: changes stay minimal and task-focused. The assignment penalizes scope creep; do not refactor adjacent code "while you're there".
- **Tunables**: balance/dimension constants belong in `GameConstants`. Add new ones there rather than scattering literals across files.
- **Compiled output**: `out/` is currently committed (`.gitignore` only ignores `.cursor/`). If you do a clean build, recompile before committing so the checked-in classes match the source.

## Where to look first

- `AGENTS.md` — extended human/agent-facing guide; aligned with this file and goes deeper on conventions and the per-feature checklist.
- `docs/architecture.md` — the package model and tick/render flow.
- `docs/PRUEBAS.md` — manual QA checklist; the closest thing this project has to a test suite.
- `docs/rules/Proyecto Técnicas de Programación.pdf` — binding academic spec.
