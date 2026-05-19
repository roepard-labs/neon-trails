# Arquitectura base — Neon Trails

Este documento describe la organización mínima del proyecto según el PDF de *Técnicas de Programación* (paquetes **lógica**, **vista**, **eventos**).

## Paquetes

| Paquete | Carpeta | Responsabilidad |
|---------|---------|-----------------|
| `logic` | `src/main/java/logic/` | Estado del juego, entidades (`Player`, `DiscProjectile`), reglas de colisión y actualización por tick. |
| `view` | `src/main/java/view/` | Ventana Swing (`JFrame`), panel de dibujo (`JPanel`), bucle que dispara repintado en el hilo de UI. |
| `events` | `src/main/java/events/` | Entrada de teclado con *Key Bindings* (evita problemas de foco frente a `KeyListener`). |

## Flujo de ejecución

1. `Main` crea la fachada `GameGUI` (módulo de arranque).
2. `GameWindow` muestra un `GamePanel` y registra teclas en `KeyboardBindings`.
3. Un `Thread` (`GameLoop`) ejecuta ticks fijos (~60 Hz): actualiza `GameState` y pide `repaint()` en el hilo de Swing.
4. `GamePanel.paintComponent` lee una **instantánea** del estado para dibujar sin bloquear el hilo de juego de forma prolongada.

## Hilos y temporizadores

- **Hilo de juego**: avanza simulación y colisiones.
- **Swing Timer** (opcional en fases siguientes): HUD, efectos; en esta base el HUD se dibuja en cada frame.

## Extensión futura (PDF)

- Pantallas (bienvenida, instrucciones, game over), ranking Top 3, sonidos y más enemigos encajan añadiendo vistas y servicios en `logic` sin romper el núcleo de tick + render.
