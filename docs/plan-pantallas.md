# Plan: Pantallas (Intro, Menú, Outro + Gaps PDF)

> Estado: **Plan para revisión previa a implementación.**
> Fecha: 14/05/2026

---

## 0. Situación Actual

Actualmente `GameGUI.iniciar()` crea un `GameWindow` que mete `GamePanel` directamente — no hay navegación entre pantallas. El juego arranca directo a la arena.

**Lo que NO existe aún:**
- Sistema de navegación entre pantallas
- Pantalla de intro / splash animada
- Menú principal
- Pantalla de instrucciones
- Ingreso de nombre de jugador
- Sistema de vidas / timer de partida
- Enemigos / power-ups
- Ranking Top 3 con persistencia
- Game Over con resultados
- Créditos / outro
- Sonidos (0 de 4 requeridos)
- Carpeta `resources/` (PDF la exige)

**Lo que SÍ funciona:**
- Arena 2 jugadores (WASD / flechas)
- Discos, modo moto 5s
- Colisiones con bordes y entre jugadores
- Bucle de juego en hilo dedicado (~60 Hz)

---

## 1. Visión General del Flujo de Pantallas

```
                    ┌─────────────┐
                    │  IntroScreen │  (animación ~3-5s, logo, título)
                    └──────┬──────┘
                           │ auto-avanza o skip con tecla
                           ▼
                 ┌──────────────────┐
                 │  WelcomeScreen   │  (menú principal)
                 │  - Nombre juego  │
                 │  - Logo UAM      │
                 │  - Integrantes   │
                 │  - Materia       │
                 │  [Iniciar]       │
                 │  [Instrucciones] │
                 │  [Créditos]      │
                 └──┬───────┬───────┘
                    │       │
          ┌─────────┘       └─────────┐
          ▼                           ▼
  ┌──────────────┐           ┌──────────────────┐
  │ NameInput    │           │ Instructions     │
  │ (nombre P1)  │           │ (reglas, teclas) │
  └──────┬───────┘           └────────┬─────────┘
         │                            │ "Volver"
         ▼                            │
  ┌──────────────┐                    │
  │  GameScreen  │◄───────────────────┘
  │  (arena)     │
  │  HUD: score  │
  │  timer, vidas│
  └──────┬───────┘
         │ gana alguien / sin vidas
         ▼
  ┌──────────────┐
  │ GameOver     │
  │ - ganador    │
  │ - score final│
  │ - tiempo     │
  │ - Top 3      │
  │ [Volver]     │
  └──────┬───────┘
         │
         ▼
  ┌──────────────┐
  │ CreditsScreen │  (outro con créditos)
  │ - integrantes│
  │ - materia    │
  │ - docente    │
  │ - assets     │
  │ [Volver menú]│
  └──────────────┘
```

---

## 2. Arquitectura de Navegación

### 2.1 ScreenManager (orquestador central)

**Ubicación:** `src/view/ScreenManager.java`

Responsabilidad:
- Mantener un `JPanel` con `CardLayout` como contenedor raíz
- Registrar cada pantalla con un nombre (`String`)
- Exponer `mostrar(String screenName)` y `mostrar(String screenName, Object data)` para pasar datos entre pantallas (ej: nombre del jugador, puntaje final)
- Cada pantalla recibe referencia al `ScreenManager` para poder navegar

```java
// Esqueleto conceptual
public class ScreenManager {
    private final JPanel container;
    private final CardLayout cards;
    private final Map<String, Screen> screens;

    public void register(String name, Screen screen);
    public void mostrar(String name);
    public void mostrar(String name, Object data);  // data via Screen.onEnter(data)
}
```

### 2.2 Interfaz Screen

**Ubicación:** `src/view/screens/Screen.java`

Cada pantalla implementa esta interfaz:

```java
public interface Screen {
    /** El JPanel que se agrega al CardLayout */
    JPanel getPanel();
    /** Llamado cuando esta pantalla se vuelve visible */
    void onEnter(Object data);
    /** Llamado al salir de esta pantalla (limpiar estado) */
    void onExit();
    /** Iniciar/detener animaciones o timers propios */
    void startAnimations();
    void stopAnimations();
}
```

### 2.3 Refactor de GameWindow

`GameWindow` deja de instanciar `GamePanel` directamente. Ahora:
1. Crea el `JFrame`
2. Instancia `ScreenManager`
3. Registra todas las pantallas
4. Muestra `IntroScreen` primero
5. Sigue siendo el dueño del `JFrame` y del hilo de juego

### 2.4 Ventaja: extensibilidad

Añadir una pantalla nueva = crear clase que implemente `Screen` + registrarla en `GameWindow`. El resto no se toca.

---

## 3. Diseño Detallado por Pantalla

### 3.1 IntroScreen (nueva)

| Aspecto | Detalle |
|---------|---------|
| **Duración** | ~4s (fade-in título + logo, sin bloquear EDT) |
| **Skip** | Cualquier tecla o click → salta al WelcomeScreen |
| **Visual** | Fondo oscuro (`#0a0a12`), título "NEON TRAILS" con fuente grande en cian neón. Subtítulo "arena 2 jugadores". Efecto fade-in con `javax.swing.Timer`. |
| **Técnica** | `Swing Timer` (requisito PDF) para animar opacidad. No usa hilo de juego. |
| **Transición** | Auto-avanza al terminar el timer O al presionar tecla → `screenManager.mostrar("welcome")` |

**Requisitos PDF cubiertos:** Timer (parcial), animación/sprites.

### 3.2 WelcomeScreen (nueva)

| Aspecto | Detalle |
|---------|---------|
| **Fondo** | Imagen de fondo (`assets/images/`) o gradiente neón oscuro |
| **Logo UAM** | Esquina superior — imagen estática (necesitas el PNG del logo) |
| **Texto** | Nombre juego, nombres de los 3 integrantes, "Técnicas de Programación", docente |
| **Botones** | [Iniciar Juego] → NameInputScreen / [Instrucciones] → InstructionsScreen / [Créditos] → CreditsScreen |
| **Botones como imágenes** | Los PNG existentes (`play_before.png`, `instructions_before.png`, `quit_before.png`) pueden reusarse como botones gráficos con rollover |
| **Controles** | Mouse (click en botones) + teclado (Enter = iniciar, I = instrucciones, C = créditos) |

**Requisitos PDF cubiertos:** Ventana de Bienvenida (completa).

### 3.3 InstructionsScreen (nueva)

| Aspecto | Detalle |
|---------|---------|
| **Contenido** | Reglas: 2 jugadores, movimiento con WASD/flechas, discos con Shift/Enter, moto 5s con Q/U. Objetivo: golpear al oponente con discos. |
| **Fondo** | `instructions_page.png` (500×480) como referencia visual, o recreado con texto neón |
| **Botón** | [Volver al menú] |
| **Tecla** | Escape → volver |

### 3.4 NameInputScreen (nueva)

| Aspecto | Detalle |
|---------|---------|
| **Campo** | `JTextField` con placeholder "Nombre del jugador" |
| **Botón** | [Jugar] → inicia GameScreen |
| **Validación** | Nombre no vacío, máximo ~15 chars |
| **Dato** | Pasa el nombre al `GameScreen` vía `screenManager.mostrar("game", playerName)` |

**Requisitos PDF cubiertos:** Ingreso de Usuario.

### 3.5 GameScreen (refactor de GamePanel actual)

| Aspecto | Detalle |
|---------|---------|
| **Base** | Reutiliza `GamePanel` actual, lo envuelve en un `Screen` |
| **HUD nuevo** | Score P1/P2, timer de partida (mm:ss), vidas (corazones o [♥♥♥]) |
| **Timer partida** | `Swing Timer` para el contador de tiempo visible (no el game loop) |
| **Fin de partida** | Cuando un jugador llega a 3 puntos (o se acaban vidas) → `screenManager.mostrar("gameover", resultado)` |
| **Pausa** | Tecla Escape pausa el game loop (no requerido por PDF pero útil) |

> **NOTA:** El PDF exige enemigos, power-ups, y sistema de vidas. Eso es trabajo FUTURO sobre `GameState` y `GamePanel`, no parte de este plan de pantallas. Ver sección 7.

### 3.6 GameOverScreen (nueva)

| Aspecto | Detalle |
|---------|---------|
| **Título** | "GAME OVER" o "¡P1 GANA!" / "¡P2 GANA!" |
| **Datos** | Nombre jugador, puntaje final, tiempo total |
| **Ranking Top 3** | Leído de `RankingManager` — ordenado por puntaje (descendente). Muestra: puesto, nombre, puntaje |
| **Persistencia** | `RankingManager` guarda en archivo (`.dat` serializado o `.csv` en `resources/`) |
| **Botón** | [Volver al menú] |
| **Imágenes** | `p1_wins.png`, `p2_wins.png`, `tie.png` como banners de resultado |

**Requisitos PDF cubiertos:** Pantalla Game Over, Ranking Top 3, ordenamiento, persistencia.

### 3.7 CreditsScreen (nueva) — el outro

| Aspecto | Detalle |
|---------|---------|
| **Estilo** | Scroll vertical automático (tipo créditos de película) con `Swing Timer` |
| **Contenido** | "NEON TRAILS", integrantes (nombres + roles), materia, docente "Leonardo Montes", universidad, año 2026 |
| **Assets** | Mencionar: imágenes de dominio público / creadas por el equipo, sonidos de [fuente] |
| **Música** | Si se implementa sonido, reproducir tema de cierre aquí |
| **Botón** | [Volver al menú] (o auto-regresa al terminar el scroll) |
| **Tecla** | Escape → volver |

---

## 4. RankingManager (nuevo — lógica)

**Ubicación:** `src/logic/RankingManager.java`

```java
// Responsabilidades:
// - Cargar/guardar ranking desde archivo (resources/ranking.dat)
// - Mantener lista ordenada (descendente por puntaje) → requisito "ordenamiento"
// - Insertar nueva entrada y mantener solo Top 3
// - Exponer getTop3(): List<RankingEntry>
```

**Ordenamiento:** `Collections.sort()` con `Comparator` por puntaje descendente. Si el PDF pide implementar algoritmo manual, usar quicksort o mergesort (recursividad).

**Persistencia:** Serialización Java (`ObjectOutputStream`) o CSV simple. Archivo en `src/resources/ranking.dat`.

---

## 5. Mapa de Archivos (nuevos y modificados)

### Archivos nuevos

```
src/
  view/
    ScreenManager.java              ← orquestador de pantallas
    screens/
      Screen.java                   ← interfaz
      IntroScreen.java              ← animación inicial
      WelcomeScreen.java            ← menú principal
      InstructionsScreen.java       ← reglas
      NameInputScreen.java          ← captura nombre
      GameScreen.java               ← wrapper del GamePanel existente
      GameOverScreen.java           ← resultados + ranking
      CreditsScreen.java            ← outro
  logic/
    RankingManager.java             ← Top 3 + persistencia
  resources/                        ← NUEVA carpeta (PDF la exige)
    images/                         ← mover assets/images aquí
    sounds/                         ← nueva
      start.wav / .mp3              ← sonido inicio
      gameplay.wav                  ← sonido durante juego
      collision.wav                 ← sonido colisión
      gameover.wav                  ← sonido game over
```

### Archivos modificados

```
src/
  Main.java                         ← sin cambios (invoca GameGUI)
  modules/GameGUI.java              ← ahora registra pantallas en ScreenManager
  view/GameWindow.java              ← refactor: delega a ScreenManager
  view/GamePanel.java               ← se envuelve en GameScreen (misma lógica)
```

---

## 6. Orden de Implementación (recomendado)

| Fase | Qué | Depende de |
|------|-----|------------|
| **Fase 1** | `ScreenManager` + interfaz `Screen` | Nada |
| **Fase 2** | `WelcomeScreen` estático (sin botones funcionales) | Fase 1 |
| **Fase 3** | Refactor `GameWindow` para usar ScreenManager | Fase 1, 2 |
| **Fase 4** | `GameScreen` (wrapper de GamePanel actual) | Fase 1 |
| **Fase 5** | Navegación Welcome → Game → Welcome (loop básico) | Fase 2, 3, 4 |
| **Fase 6** | `IntroScreen` con animación y skip | Fase 1 |
| **Fase 7** | `InstructionsScreen` | Fase 1 |
| **Fase 8** | `NameInputScreen` | Fase 1 |
| **Fase 9** | Lógica de fin de partida en GameState (vidas, puntaje límite) | Fase 4 |
| **Fase 10** | `RankingManager` (ordenamiento + persistencia) | Nada |
| **Fase 11** | `GameOverScreen` con ranking | Fase 9, 10 |
| **Fase 12** | `CreditsScreen` con scroll | Fase 1 |
| **Fase 13** | Integración completa del flujo | Todas |
| **Fase 14** | Sonidos (4 mínimos) | Assets de audio |
| **Fase 15** | Mover assets a `resources/` y actualizar rutas | Fase 13 |

---

## 7. Gaps con el PDF (lo que falta DESPUÉS de este plan)

Este plan cubre toda la navegación y pantallas. Lo que **NO cubre** y necesitarás implementar aparte:

| Requisito PDF | Estado actual | Trabajo pendiente |
|---------------|---------------|-------------------|
| **Enemigos** | No existen | Nueva clase `Enemy`, IA simple, spawn, colisiones |
| **Sistema de vidas** | No existe (solo score) | Añadir `vidas` a `Player`, HUD, game over por 0 vidas |
| **Power-ups / superpoderes** | Solo modo moto (ya existe) | Añadir 1-2 power-ups más: escudo, multi-disco, etc. |
| **Recuperar vidas** | No existe | Mecánica: cada N puntos o spawn de ítem |
| **Transformación visual** (al obtener superpoder) | Motocicleta tiene halo | Añadir efecto visual distinto por power-up |
| **Timer de partida** | No existe | Contador regresivo o progresivo en HUD |
| **Sonidos (4)** | 0 de 4 | Cargar/reproducir `.wav` con `javax.sound.sampled` |
| **Sprites / animaciones** | Solo dibujo estático | Secuencias de imágenes, animación por frames |
| **Recursividad** | No usada | RankingManager (quicksort recursivo) u otra aplicación |
| **Dispositivo adicional** (bonus) | Solo teclado | Joystick / gamepad (opcional, bonus ABET) |
| **Carpeta resources/** | `src/assets/` actual | Renombrar/mover a `src/resources/` |
| **Documentación JavaDoc** | Parcial | Completar en todas las clases públicas |

---

## 8. Notas Técnicas

### 8.1 Evitar bloqueos Swing
- Toda actualización de UI va en el EDT (`SwingUtilities.invokeLater`)
- Los `Screen.onEnter` / `onExit` se llaman desde el EDT
- `GameLoop` sigue en su propio hilo, disparando `repaint()`

### 8.2 Transiciones entre pantallas
- `CardLayout.show()` es instantáneo — suficiente para este proyecto
- Si se quiere fade entre pantallas: `Swing Timer` + `AlphaComposite` en un panel overlay

### 8.3 Sonidos
- Usar `javax.sound.sampled.Clip` para efectos cortos (colisión, game over)
- Usar `javax.sound.sampled.SourceDataLine` para música de fondo (streaming)
- Cargar desde `getClass().getResourceAsStream("/sounds/archivo.wav")`

### 8.4 Imagen de fondo en pantallas
- `WelcomeScreen`: paintComponent dibuja la imagen escalada, luego los botones encima
- Las imágenes existentes (`main_menu.png`, `instructions_page.png`) son pequeñas (150x60, 500x480). Se escalan con `Image.getScaledInstance()` o se rediseñan a 960×640.

### 8.5 Rendimiento
- Solo `GameScreen` corre el game loop. El resto de pantallas son estáticas o con `Swing Timer`.
- `stopAnimations()` se llama al salir de cada pantalla para liberar timers.
