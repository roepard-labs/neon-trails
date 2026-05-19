# Changelog

Todas las modificaciones notables a este proyecto se documentan en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/lang/es/).

## [0.1.0] — En desarrollo

### Agregado
- Arena de juego 2D con bordes colisionables (`view/GamePanel`)
- Sistema de dos jugadores locales con movimiento independiente
- Controles: P1 (WASD) y P2 (Flechas)
- Disparo de discos (P1: Shift, P2: Enter)
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
