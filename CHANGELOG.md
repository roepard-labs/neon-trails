# Changelog

Todas las modificaciones notables a este proyecto se documentan en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/lang/es/).

## [0.3.0] - Tal vez lo que emerge de lo desconocido no sea tan aterrador después de todo.
### Agregado
- Dockerizacion completa, un setup inigualable.
- Los assets de jaco implementados.
- Hacer que el jugador no se muera en moto al ir a su rumbo opuesto.
- API Laravel 13 , Base de datos MariaDB 11.
- Landing Page en Vue 3 con Shadcn-vue.

## [0.2.0] - Nanana
### Agregado
- Mejoras en los diseños de los jugadores.
- Incluir dockerizacion.
- Implementacion mejorada de la documentacion con Vue3-Radix.
- Linea de rastro en los jugadores.


## [0.1.0] — El Despertar de las Bestias.

### Agregado
- Arena de juego 2D con bordes colisionables (`view/GamePanel`)
- Sistema de dos jugadores locales con movimiento independiente
- Controles: P1 (WASD) y P2 (Flechas)
- Disparo de discos (P1: E, P2: Enter)
- Modo moto ~5 s como power-up temporal (P1: Q, P2: U)
- Pantallas: Welcome, NameInput, Game, GameOver mediante `ScreenManager`
- CI/CD con GitHub Actions: compila, testea y empaqueta en push y PR
- Pipeline Maven con `make verify` (compilación + tests JUnit 5 + jar)
- Persistencia de ranking en archivo
- Documentación de arquitectura en `docs/architecture.md`
- Pruebas manuales en `docs/PRUEBAS.md`
- Stub web en `web/` (HTML/CSS/JS + JSON mock de ranking)

### Pendiente (ver TODO.md)
- Sonidos SFX: `shoot.wav`, `hit.wav`, `bike.wav`, `gameover.wav`
- Íconos e imágenes del juego
- Música de fondo opcional (menú/juego)
