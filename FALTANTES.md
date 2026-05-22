# Análisis de Faltantes — Neon Trails

> **Fecha del análisis:** 2026-05-21
> **Fecha de entrega del proyecto:** 2026-05-28 8:30 pm — **quedan 7 días**
> **Fuente de verdad:** `docs/rules/Proyecto Técnicas de Programación.pdf` (Docente: Leonardo Montes)
> **Rama auditada:** `main` (HEAD: `13ebd78 feat(sfx): integrar sonidos y actualizar documentacion.`)

Este documento mapea el estado actual del repositorio contra la **rúbrica de calificación del PDF** (sección 10, total 5.0 puntos). Cada item cita el archivo o ruta donde está la evidencia (o donde falta).

Convenciones:
- ✅ Cumplido
- ⚠️ Parcial / requiere refuerzo
- ❌ Falta

---

## 1. Resumen ejecutivo

| Bloque | Cumplido | Parcial | Faltante |
|--------|----------|---------|----------|
| **Técnica (POO, paquetes, hilos, recursividad, ordenamiento, Timer)** — 1.4 pts | 3 / 6 | 0 / 6 | 3 / 6 |
| **Multimedia (Resources, sonidos)** — 0.4 pts | 2 / 2 | 0 / 2 | 0 / 2 |
| **Interfaz (Bienvenida, Instrucciones, Juego, Game Over)** — 0.7 pts | 0 / 4 | 3 / 4 | 1 / 4 |
| **Funciones (Enemigos, Power-ups, Persistencia, Ciclo)** — 1.0 pts | 1 / 4 | 2 / 4 | 1 / 4 |
| **Código (documentación, compila)** — 0.5 pts | 1 / 2 | 1 / 2 | 0 / 2 |
| **Documento (PDF APA, Manual, Conclusiones)** — 0.6 pts | 0 / 3 | 0 / 3 | 3 / 3 |
| **Sustentación (40%)** — 1.0 pts | depende del equipo | — | — |

**Puntaje técnico estimado actual (sin sustentación, sin documento):** ~2.3 / 3.0 puntos (60% del proyecto).
**Puntos en riesgo concentrados en:** ranking persistente (0.3), recursividad (0.2), ordenamiento (0.2), Timer (0.2), documento PDF (0.6), manual de usuario (0.2), ventana de instrucciones (0.1), enemigos con interacción (0.3).

---

## 2. Rúbrica del PDF — estado actual línea por línea

### 2.1 Técnica (1.4 pts)

| # | Criterio | Puntos | Estado | Evidencia / Faltante |
|---|---|---|---|---|
| 1 | Aplicación correcta de los 4 pilares de la POO | 0.3 | ✅ | Encapsulación (`logic/Player.java`, campos privados + getters), herencia (`view/BaseScreen.java` → 4 pantallas concretas), polimorfismo (`logic/GameEventListener.java` con `audio/AudioGameEventListener.java` y stubs en tests), abstracción (`BaseScreen` abstract con hooks `onShow/onHide`). **Documentar explícitamente cada pilar en el PDF entregable.** |
| 2 | Organización en paquetes (Lógica, Vista, Eventos) | 0.2 | ✅ | `src/main/java/logic/`, `src/main/java/view/`, `src/main/java/events/`. **Extra:** `audio/` y `modules/` están bien justificados (audio aísla `javax.sound.sampled`; `modules/GameGUI.java` es la fachada de arranque). |
| 3 | Implementación funcional de Hilos (Threads) | 0.3 | ✅ | `view/GameLoop.java:24` (bucle ~60 Hz en hilo dedicado `neon-trails-game-loop`) lanzado desde `view/GamePanel.java:84` (`startLoop`). **Tip:** poder explicar en sustentación: por qué un `Thread` propio y no `Swing Timer` para el game loop (regularidad ~60 Hz vs latencia EDT). |
| 4 | Uso de **recursividad** en una funcionalidad lógica | 0.2 | ❌ | No existe ningún método recursivo en `src/main/java/`. Ver §3.1 para opciones de implementación. |
| 5 | Implementación de **algoritmos de ordenamiento** | 0.2 | ❌ | No existe `sort`, `Collections.sort`, `Arrays.sort` ni implementación propia. Ver §3.2 (vinculado al ranking Top 3). |
| 6 | Control de tiempo mediante la clase **Timer** | 0.2 | ❌ | El proyecto NO usa `javax.swing.Timer` ni `java.util.Timer`. El tick de juego se hace con `Thread.sleep` (`view/GameLoop.java:31`), válido como hilo pero **no cuenta como "Timer"** según rúbrica. Ver §3.3. |

### 2.2 Multimedia (0.4 pts)

| # | Criterio | Puntos | Estado | Evidencia / Faltante |
|---|---|---|---|---|
| 7 | Gestión de recursos (carpeta Resources, imágenes y **sprites**) | 0.2 | ⚠️ | `src/main/resources/` existe con `audio/sfx/` (15 WAV) y `assets/fonts/rajdhani/` (fuentes TTF/WOFF). **Faltan:** logo UAM, imagen de fondo de bienvenida, sprites del jugador (animaciones), no se carga ninguna imagen con `ImageIO`/`BufferedImage` (ver §3.4). El render del juego es geometría (`fillRoundRect`, `fillOval`) → "sprites (animaciones)" del PDF queda **no cumplido**. |
| 8 | Inclusión de 4 sonidos (Inicio, Juego, Colisión, Game Over) | 0.2 | ✅ | `src/main/resources/audio/sfx/` con 15 WAV (`shoot_1/2/3`, `hit`, `bike_init/loop/end`, `bounce`, `stop`, `pickup`, `respawn`, `gameover_1/2`, `ui_click`, `ui_hover`). Integrado vía `audio/SoundManager.java` y `audio/AudioGameEventListener.java`. **Tip:** justificar el formato WAV PCM en el PDF (regla "cero dependencias de runtime"). |

### 2.3 Interfaz (0.7 pts)

| # | Criterio | Puntos | Estado | Evidencia / Faltante |
|---|---|---|---|---|
| 9 | Ventana Bienvenida (Logo UAM, datos integrantes, navegación) | 0.2 | ⚠️ | Existe `view/screens/WelcomeScreen.java`, muestra título "NEON TRAILS", subtítulo, materia, docente. **Faltan obligatorios del PDF:** Logo UAM (imagen), imagen de fondo, **nombres de los 3 integrantes**, botón **Instrucciones**, botones de navegación (volver / regresar al inicio). Ver §3.5. |
| 10 | Ventana Instrucciones con reglas y botón volver | 0.1 | ❌ | **No existe** `InstructionsScreen.java`. No hay registro de la pantalla en `view/GameWindow.java:25-29`. Ver §3.6. |
| 11 | Pantalla Juego: visualización de puntaje, tiempo y vidas | 0.2 | ⚠️ | `view/GamePanel.java:212-213` (`drawHud`) muestra vidas (corazones) y `[MOTO]`. **Faltan:** **puntaje numérico** visible (existe `Player.getScore()` pero no se dibuja) y **tiempo de partida** (no hay cronómetro). Ver §3.7. |
| 12 | Pantalla Game Over: nombre del jugador, puntaje final y tiempo | 0.2 | ⚠️ | `view/screens/GameOverScreen.java:84-95` muestra "¡Gana X!" y quién perdió. **Faltan:** **puntaje final** explícito (numérico), **tiempo total jugado**, **Top 3** del ranking (criterio 15). Ver §3.8. |

### 2.4 Funciones (1.0 pts)

| # | Criterio | Puntos | Estado | Evidencia / Faltante |
|---|---|---|---|---|
| 13 | Mecánica de juego: enemigos e interacción funcional | 0.3 | ⚠️ | El "enemigo" hoy es el **otro jugador local** (P1 vs P2). El PDF describe enemigos como entidades del juego: aunque PvP local es válido, en la sustentación pesa más demostrar enemigos AI/NPC. Considerar añadir al menos 1 enemigo simple con IA básica (patrullaje + colisión) en `logic/`. Ver §3.9. |
| 14 | Sistema de Power-ups y transformación visual/funcional | 0.2 | ⚠️ | Existe **1 power-up**: modo moto 5 s (velocidad ×1.75, halo blanco — `view/GamePanel.java:175-178`). PDF dice "power-up**s**" (plural). Añadir al menos 1 más con transformación visual distinta (escudo, multidisco, etc.). Ver §3.10. |
| 15 | Persistencia: guardar historial y mostrar Ranking Top 3 | 0.3 | ❌ | **No existe** `RankingManager`, ni archivo de persistencia, ni Top 3 en pantalla. El stub `web/mock/leaderboard.json` **no se conecta** al juego Java. Ver §3.11. |
| 16 | Ciclo de juego: jugar múltiples veces sin cerrar la app | 0.2 | ✅ | `view/screens/GameOverScreen.java:65-68` ofrece "Jugar de nuevo" y "Volver al menú". `view/screens/GameScreen.java:30` llama `resetGame()` en cada entrada. Confirmado funcional. |

### 2.5 Código (0.5 pts)

| # | Criterio | Puntos | Estado | Evidencia / Faltante |
|---|---|---|---|---|
| 17 | Código documentado y sin errores/advertencias en IntelliJ | 0.2 | ⚠️ | JavaDoc en español presente en casi todas las clases públicas. **Verificar localmente en IntelliJ** que no haya advertencias (imports no usados, switch incompletos, etc.). El PDF exige "sin **advertencias**", no solo sin errores. Ejecutar `Code → Inspect Code` antes de empacar. |
| 18 | El proyecto compila y ejecuta correctamente | 0.3 | ✅ | `make verify` ejecuta `mvn -B verify` (compila + tests + jar). CI `.github/workflows/java.yml` corre en cada push. **Validar en máquina de cada integrante** (requisito explícito del PDF: "cada integrante debe tener el proyecto funcionando en su computador"). |

### 2.6 Documento (0.6 pts)

| # | Criterio | Puntos | Estado | Evidencia / Faltante |
|---|---|---|---|---|
| 19 | PDF con Normas APA séptima, Logo UAM y estructura solicitada | 0.2 | ❌ | **No existe** el PDF entregable. El PDF debe nombrarse `PROYECTO POO - NEON TRAILS.pdf` (MAYÚSCULAS) y contener: Índice, Portada integrantes, Normas APA 7ª, Logo UAM, Descripción del proyecto, Capturas de la interfaz, Explicación de clases, Librerías utilizadas y justificación. Ver §3.12. |
| 20 | Manual de usuario con capturas e instrucciones paso a paso | 0.2 | ❌ | Existe `README.md` con controles, pero no es manual de usuario completo con capturas. Necesita: imágenes de cada pantalla, descripción paso a paso del flujo, controles ilustrados. Ver §3.13. |
| 21 | Conclusiones individuales de aprendizaje por integrante | 0.2 | ❌ | No existen. Cada uno de los 3 integrantes debe escribir su sección. Ver §3.14. |

### 2.7 Sustentación (1.0 pts — individual)

| # | Criterio | Puntos | Estado |
|---|---|---|---|
| 22 | Explicación clara y dominio del código por el estudiante | 0.5 | Pendiente — depende de la preparación individual |
| 23 | Capacidad de realizar modificaciones en vivo exitosas | 0.5 | Pendiente — depende de la preparación individual |

> **Causal de anulación (nota 0.0):** "Código no comprendido por el estudiante durante la sustentación". Cada integrante debe poder modificar y mantener funcional. Ensayar antes.

> **Causal de anulación (nota 0.0):** "Uso de inteligencia artificial para generar el proyecto". Este punto es responsabilidad del equipo; este archivo de análisis no sustituye las normas del docente — declarar y justificar honestamente cualquier herramienta usada según las normas del curso.

---

## 3. Trabajo pendiente — propuestas de implementación

### 3.1 Recursividad (criterio 4 — 0.2 pts)

**Opción A (recomendada, encaja con el ranking):** Implementar **quicksort recursivo** en `logic/RankingManager.java` para ordenar entradas por puntaje. Cumple criterios 4, 5 y 15 a la vez.

**Opción B (alternativa de alcance acotado):** Algoritmo de "limpieza recursiva" de discos detenidos: dado un disco quieto, eliminar recursivamente todos los discos del dueño que estén dentro de un radio R. Más artificial; preferir A.

**Opción C:** Generar fondo neón animado en `GamePanel` con un fractal sencillo recursivo (líneas tipo "Tron-grid"). Sirve como recurso visual y como recursividad.

### 3.2 Ordenamiento (criterio 5 — 0.2 pts)

Vinculado al ranking Top 3 — ver §3.11. Implementar **manualmente** (no usar `Collections.sort`) un quicksort/mergesort recursivo en `RankingManager` para ordenar la lista de entradas. Documentar la complejidad en el PDF.

### 3.3 Timer (criterio 6 — 0.2 pts)

Añadir un `javax.swing.Timer` en `view/screens/GameScreen.java` que:
- Tick cada 1000 ms.
- Incremente el contador de tiempo de partida (segundos jugados).
- Repaint del HUD para que `view/GamePanel.java#drawHud` muestre `MM:SS`.
- Detener el Timer en `onHide()` (ya hay precedente: `panel.stopLoop()` en línea 37).

Esto cubre criterio 6 **y** parte del criterio 11 (tiempo de juego visible) y del 12 (tiempo total en Game Over — guardar `tiempoFinal` en `GameSession`).

### 3.4 Sprites e imágenes (criterio 7 — 0.2 pts)

Producir como mínimo:
- `logo_uam.png` para `WelcomeScreen`.
- `bg_arena.png` opcional para fondo de juego (ya existe paleta neón).
- **Sprite del jugador**: 4 frames de "encendido" (idle / movimiento) por jugador. Cargar con `ImageIO.read(getClass().getResourceAsStream("/assets/images/p1_sprite_0.png"))` en `GamePanel`, alternar frame cada N ticks. Sin esto, "sprites (animaciones)" sigue **no cumplido**.
- Imágenes de `docs/branding/images/` (ya existen `p1_wins.png`, `p2_wins.png`, `over.png`, etc.) — moverlas a `src/main/resources/assets/images/` y usarlas en `GameOverScreen` y `WelcomeScreen`.

> **NOTA:** según `docs/multimedia-libraries.md` y `TODO.md`, hay un debate de formatos pendiente (MP3 vs WAV, SVG vs PNG). Mantener PNG/WAV para no romper la regla "cero dependencias de runtime" del `pom.xml`.

### 3.5 Ventana de Bienvenida — refuerzo (criterio 9 — 0.2 pts)

Editar `view/screens/WelcomeScreen.java` para añadir:
- `JLabel` con `ImageIcon` del Logo UAM en la esquina superior izquierda o centrado arriba.
- `JLabel` con **nombres de los 3 integrantes** (datos del equipo — debe escribirse).
- Imagen de fondo (override de `paintComponent` dibujando `BufferedImage` escalado antes del `BoxLayout`).
- Botón **Instrucciones** → navega a `"instructions"` (a registrar en `view/GameWindow.java`).
- Botones de navegación (Salir de la aplicación, etc.).

### 3.6 Ventana de Instrucciones (criterio 10 — 0.1 pts)

Crear `view/screens/InstructionsScreen.java` que extienda `BaseScreen`:
- Texto/imagen con las reglas del juego (controles P1/P2, mecánica de discos con rebotes, modo moto 5 s, condición de victoria por agotamiento de vidas).
- Botón "Volver al menú" → `screens().mostrar("welcome")`.
- Tecla Escape (con `KeyboardBindings` o un `KeyStroke` local) que regrese al menú.

Registrar en `view/GameWindow.java:25-29`: `screens.register("instructions", new InstructionsScreen());` y vincular el botón desde `WelcomeScreen`.

### 3.7 HUD de juego — puntaje y tiempo (criterio 11 — 0.2 pts)

Modificar `view/GamePanel.java#drawHud` para añadir:
- Línea de puntaje: `"P1: " + p1.getScore() + "   P2: " + p2.getScore()` (los datos ya existen, solo falta dibujarlos).
- Línea de tiempo: `"Tiempo: " + formato("MM:SS", segundos)` alimentada desde el Timer de §3.3.

### 3.8 Game Over — datos completos (criterio 12 — 0.2 pts)

Editar `view/screens/GameOverScreen.java#onShow`:
- Mostrar puntaje final de ambos (lee de `GameSession` — necesitarás añadirle `p1Score`, `p2Score`, `tiempoSegundos`).
- Mostrar tiempo total (formato `MM:SS`).
- Renderizar Top 3 leyendo de `RankingManager`.
- Conservar el botón "Volver al menú" (criterio del PDF).

### 3.9 Enemigos no-jugadores (criterio 13 — 0.3 pts)

Opcional pero **recomendado** para asegurar el punto:
- Crear `logic/Enemy.java` y `logic/EnemySpawner.java`.
- IA mínima: enemigo que se mueve en línea recta o sigue al jugador más cercano.
- Colisión enemigo↔jugador → quita una vida o reduce barra (PDF dice "pierde una vida o resta 20%").
- Spawn periódico desde un Timer (refuerza criterio 6).
- Dibujar en `GamePanel` con sprite o forma geométrica.

Sin esto, la sustentación dependerá de defender que el PvP local cuenta como "enemigos con interacción". Es defendible pero el evaluador puede penalizar.

### 3.10 Segundo Power-up (criterio 14 — 0.2 pts)

Añadir al menos un power-up adicional. Sugerencias compatibles con el código actual:
- **Escudo** (3 s): bloquea el próximo disco enemigo. Variable `shieldUntilNanos` en `Player`, render con halo distinto al de moto.
- **Multidisco**: dispara 3 discos en abanico durante 5 s.
- **Curación** (también cumple "mecánica para recuperar vidas" del PDF — funcionalidad 4.3): ítem que aparece cada N segundos y restaura 1 vida al recogerlo.

Recomendado el **escudo** o **curación** porque la curación cubre también la "mecánica para recuperar vidas" del bloque 4.3.

### 3.11 Persistencia + Top 3 (criterio 15 — 0.3 pts)

Crear `logic/RankingManager.java`:
- Carga al inicio `ranking.dat` (serialización Java) o `ranking.csv` desde el directorio de usuario (no del classpath, para que sea escribible — `System.getProperty("user.home") + "/.neontrails/ranking.dat"`).
- Lista de `RankingEntry { String nombre; int puntaje; long tiempoMs; }`.
- Insertar nueva entrada tras cada partida (desde `GameOverScreen#onShow` o `GameScreen#onGameOver`).
- Ordenar con **quicksort recursivo propio** (§3.1, §3.2).
- Exponer `getTop3(): List<RankingEntry>`.
- Persistir al disco tras cada inserción.

Conectar:
- `GameOverScreen` lee `rankingManager.getTop3()` y dibuja la tabla.
- Decidir clave de ranking: por puntaje del ganador, o por puntaje individual de ambos jugadores. Lo más limpio: registrar 2 entradas por partida (una por jugador).

### 3.12 Documento PDF entregable (criterio 19 — 0.2 pts)

Crear documento **fuera del repo** (o en `docs/entregable/`) llamado `PROYECTO POO - NEON TRAILS.pdf` con:
1. **Portada** — Logo UAM, nombre del juego, materia, docente, integrantes (3), fecha.
2. **Índice** — generado.
3. **Descripción del proyecto** — qué es, género, mecánica principal.
4. **Capturas de la interfaz** — 1 por pantalla (Bienvenida, Instrucciones, NameInput, Juego, GameOver) y opcionalmente in-game (modo moto, disco rebotando).
5. **Explicación de clases** — diagrama de paquetes + tabla con cada clase importante: `Player`, `DiscProjectile`, `GameState`, `GameLoop`, `GamePanel`, `ScreenManager`, `SoundManager`, `RankingManager`, etc.
6. **Librerías utilizadas y justificación** — Java SE Swing (sin dependencias externas, salvo JUnit 5 en test). Justificar por qué no se usa motor de juegos / por qué cero dependencias de runtime (regla del PDF + portabilidad).
7. **Manual de usuario** — ver §3.13.
8. **Conclusiones individuales** — ver §3.14.
9. **Referencias APA séptima** — fuentes consultadas (Oracle Java docs, jsfxr para SFX, etc.).

### 3.13 Manual de usuario (criterio 20 — 0.2 pts)

Sección dentro del PDF (o anexo) con:
- Cómo instalar Java 17 + Maven en Windows/Linux/Mac.
- Cómo clonar el repo y ejecutar (`make run` o `mvn exec:java`).
- Captura de cada pantalla con flechas/etiquetas explicando elementos.
- Tabla de controles (la del README sirve de base).
- Resolución de problemas comunes (no se oyen sonidos → headless; teclas no responden → click en panel para enfocar).

### 3.14 Conclusiones por integrante (criterio 21 — 0.2 pts)

Cada uno de los 3 integrantes escribe 1 página con:
- Qué aprendió técnicamente (POO aplicada, hilos, Swing, etc.).
- Qué dificultades enfrentó.
- Qué haría diferente.
- Qué parte del código entiende mejor (preparación para sustentación).

---

## 4. Otros gaps no asignados a la rúbrica pero presentes en el cuerpo del PDF

| Elemento | Sección PDF | Estado | Notas |
|---|---|---|---|
| **Sistema de vidas mínimo 3** | 4.3 | ✅ | `logic/Player.java:11` (`INITIAL_LIVES = 3`). |
| **Sistema de recompensas** | 4.3 | ⚠️ | Solo "score por golpear" (1 punto por hit). Considerar añadir recompensas tras N hits, o por recoger ítems. Vinculado a §3.10 (power-up de curación) y/o §3.11 (mejor puntaje → ranking). |
| **Dispositivo adicional (BONUS)** | 8 (ABET-SO7) | ❌ | Solo teclado. Joystick / gamepad daría BONUS (no es obligatorio). Librerías candidatas (introducen dependencia): `jinput`, `libgdx-controllers`. Costo/beneficio bajo dado el tiempo restante. |
| **README integrantes** | 7 | ⚠️ | `README.md` actual no lista los 3 integrantes. Añadirlos en una sección "Integrantes" antes de entregar. |

---

## 5. Riesgos y problemas latentes detectados

### 5.1 Causales de anulación a vigilar

1. **"Código no comprendido por el estudiante en la sustentación"** → cada integrante debe poder explicar **al menos**: `GameState.tick`, `GameLoop`, `ScreenManager`, `SoundManager`, `KeyboardBindings`, y la sección que él/ella desarrolló del ranking.
2. **"Uso de inteligencia artificial para generar el proyecto"** → este punto es responsabilidad de los integrantes; revisar las normas exactas del docente. El historial de commits y trazabilidad del trabajo manual son clave.
3. **"Código no compila"** → ejecutar `make verify` antes de empacar el .zip y en la(s) computadora(s) destino. Tener copia de respaldo en computadores de la universidad como sugiere el PDF.

### 5.2 Riesgos técnicos

- **`web/` no se conecta al juego.** Si en sustentación pesa demostrar persistencia, el JSON web podría confundir al evaluador. Aclarar en README o eliminarlo si no aporta a la rúbrica.
- **Headless audio en CI.** Funciona bien (`SoundManager.preloadAll` cae a no-op silencioso). No es un riesgo, pero documentarlo.
- **`InputController` sin `volatile`** (`events/InputController.java:8` lo comenta). En un Mac M1 o JVM con visibilidad estricta podría aparecer race. Considerar `volatile` en los campos boolean (cambio trivial, alto retorno).
- **Sin pantalla de instrucciones** → si el evaluador presiona un botón "Instrucciones" inexistente, baja la nota visual.
- **Score no se muestra hoy** aunque existe en `Player`. Es el "no terminé de cablear" más fácil de cerrar — 5 líneas en `GamePanel#drawHud`.

### 5.3 Inconsistencias documentales

- `CHANGELOG.md` lista "Persistencia de ranking en archivo" en sección "Agregado" — **no existe en el código**. Actualizar o mover a "Pendiente".
- `docs/PRUEBAS.md` línea 68 todavía marca "FIXME futuro: pantallas del PDF (ranking persistente, música de fondo del menú)". Coherente.
- `docs/plan-pantallas.md` §7 lista exactamente los mismos gaps que este documento — referencia útil para implementación.

---

## 6. Plan de ataque sugerido (priorizado por costo/beneficio en 7 días)

> Días estimados son **persona-día**. Con 3 integrantes en paralelo, se puede comprimir.

| Prioridad | Tarea | Criterios cubiertos | Estimación |
|-----------|---|---|---|
| 🔴 **P0** | Crear `InstructionsScreen` + registrar en `GameWindow` + botón en `WelcomeScreen` | 10 | 0.5 d |
| 🔴 **P0** | Mostrar puntaje y tiempo en HUD de `GamePanel`. Añadir `Swing Timer` en `GameScreen` para cronómetro de partida | 6, 11 | 0.5 d |
| 🔴 **P0** | Completar Game Over con puntaje final + tiempo total + (en cuanto exista) Top 3 | 12 | 0.5 d |
| 🔴 **P0** | Crear `RankingManager` con **quicksort recursivo** propio + persistencia en archivo + integración con `GameOverScreen` | 4, 5, 15 | 1 d |
| 🟡 **P1** | Logo UAM + nombres de integrantes en `WelcomeScreen` + (idealmente) imagen de fondo | 9 | 0.5 d |
| 🟡 **P1** | Cargar e integrar al menos un sprite animado (2-4 frames) para los jugadores | 7 | 1 d |
| 🟡 **P1** | Añadir 2º power-up (escudo o curación). Si es curación, cubre "recuperar vidas" del bloque 4.3 | 14 | 1 d |
| 🟢 **P2** | (Opcional) Enemigo no-jugador con IA mínima | 13 (refuerzo) | 1.5 d |
| 🟢 **P2** | Verificación en IntelliJ: corregir advertencias detectadas por `Code → Inspect Code` | 17 | 0.5 d |
| 🔴 **P0** | Redactar PDF entregable (Portada, Índice, APA, Logo UAM, Descripción, Capturas, Explicación de clases, Librerías, Manual, Conclusiones) | 19, 20, 21 | 1.5 d (paralelo) |
| 🟡 **P1** | Actualizar `README.md` con integrantes y `CHANGELOG.md` con estado real | 17, 19 | 0.25 d |
| 🟢 **P2** | Ensayar sustentación individual: cada integrante explica una porción del código y prepara modificaciones de prueba | 22, 23 | 1 d (cada uno) |

**Camino crítico mínimo** (si el tiempo apura, completar al menos esto): P0 + Documento PDF. Esto recupera ~1.3 puntos perdidos sin tocar enemigos AI.

---

## 7. Referencias dentro del repo

- Rúbrica oficial: `docs/rules/Proyecto Técnicas de Programación.pdf` §10.
- Arquitectura actual: `docs/architecture.md`, `AGENTS.md`, `CLAUDE.md`.
- Plan de pantallas previo: `docs/plan-pantallas.md` (§7 ya listaba estos gaps con menor detalle).
- TODO de multimedia: `TODO.md` (sección Hito 4 / Hito 5).
- Pruebas manuales actuales: `docs/PRUEBAS.md`.
- CI: `.github/workflows/java.yml`.

---

_Documento generado a partir de auditoría de código y rúbrica del PDF — actualizar a medida que se cierren ítems._
