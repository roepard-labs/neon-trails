# TODO Neon-trails
Que hay por hacer

# 14may2026
Indexar sonidos sfx/bg, iconos, imagenes.
Formatos adecuados .svg para animaciones.
- [ ] hola

## Sonidos requeridos (Hito 4)

PDF exige **4 sonidos mínimos**. Formato obligatorio: **`.wav` PCM 16-bit**, mono (estéreo opcional para los últimos dos), 44.1 o 22.05 kHz, <200 KB cada uno. Ubicación: `src/resources/sounds/`. Generar los 3 primeros en 30 s cada uno con [jsfxr](https://sfxr.me) (CC0, browser, sin cuenta).

- [x] `shoot.wav` — disparo de disco (E / Enter). Integrado como 3 variantes (`shoot_1/2/3.wav`) seleccionadas aleatoriamente vía `audio.SoundManager.playRandom`.
- [x] `hit.wav` — golpe confirmado, pérdida de vida. Integrado: emitido por `GameState.resolveDiscHits` al disparar el listener.
- [x] `bike.wav` — activación de modo moto. Integrado: `bike_init.wav` al activar + loop `bike.wav` mientras al menos un jugador esté en moto + `bike_end.wav` al expirar.
- [x] `gameover.wav` — al entrar a GameOverScreen. Integrado: random entre `gameover_1.wav` y `gameover_2.wav` en `GameOverScreen.onShow()`; detenido en `onHide()`.

**Restricciones técnicas**:
- Los SFX siguen en `.wav` por latencia (cargados con `javax.sound.sampled` sin dependencias). Para MP3 (música larga) usar JLayer — ver [`docs/multimedia-libraries.md`](docs/multimedia-libraries.md) y [`AGENTS.md`](AGENTS.md#política-de-dependencias).
- Toda fuente externa debe ser CC0 o licencia compatible (registrar atribución en `CreditsScreen` cuando aplique).

**Hito 4 listo para implementar cuando los 4 archivos estén en `src/resources/sounds/`.** Música de fondo de menú/juego es opcional, no requerida por el PDF — iría a un Hito 5 separado si la pides.

---

# Multimedia para feel "Tron película" (brief para Jaco)

Esta sección extiende el Hito 4 con TODA la multimedia (música + SFX + imágenes) que el juego necesita para acercarse a la atmósfera de Tron. Cada entrada apunta al evento exacto del código donde se conectaría.

## Música de fondo (estilo Tron — Hito 5 opcional)

`src/main/resources/audio/init/JourneySeparateWays.mp3` **YA EXISTE** y va como intro / menú principal.

> ⚠️ **Decisión de formato** (ver [`docs/multimedia-libraries.md`](docs/multimedia-libraries.md)):
> - **Opción A — WAV**: convertir el MP3 a WAV. Java SE solo lee WAV/AU/AIFF nativos. Un WAV PCM de 5 min pesa ~30 MB; recortado a 60–90 s para el menú baja a ~10 MB.
> - **Opción B — JLayer** (recomendada): `com.googlecode.soundlibs:jlayer:1.0.1.4` (~100 KB, LGPL). Permite reproducir MP3 directo. La política de dependencias del proyecto permite agregar librerías cuando Java SE no soporta el formato nativamente.
>
> **Recomendación:** usar JLayer y mantener los MP3 originales (justificar la dependencia en el PDF). Jaco puede preparar las pistas directamente en MP3.

| ID | Pantalla | Archivo sugerido | Mood | Hook donde se carga |
|----|----------|------------------|------|---------------------|
| `bg_menu` | `WelcomeScreen` + `NameInputScreen` | `audio/init/JourneySeparateWays.mp3` (existe) | Synth-rock 80s tipo Journey / Tron 1982 (Wendy Carlos). | `WelcomeScreen.onShow()` |
| `bg_game` | `GameScreen` | `audio/game/arena_loop.wav` (loopeable, 2–3 min) | Sintes oscuros estilo Daft Punk – *Tron Legacy* ("Derezzed", "The Grid"). Bajo presente, percusión electrónica, 110–130 BPM. | `GameScreen.onShow()` (`src/main/java/view/screens/GameScreen.java:27`) |
| `bg_gameover_win` | `GameOverScreen` (gana P1 o P2) | `audio/over/win.wav` (5–10 s, sin loop) | Fanfare sintético ascendente; sensación de "lo lograste". | `GameOverScreen.onShow()` (`src/main/java/view/screens/GameOverScreen.java:76`) |
| `bg_gameover_lose` | `GameOverScreen` (perspectiva del derrotado) | `audio/over/lose.wav` (5–10 s, sin loop) | Stinger descendente glitchy / desresolución digital. | Mismo hook que el anterior; el código elegirá uno según `winnerId`. |

## SFX adicionales para feel Tron (más allá del Hito 4)

Todos `.wav` PCM 16-bit mono, 44.1 kHz, <200 KB. Ubicación: `src/main/resources/sounds/`.

| Archivo | Duración | Mood / Preset jsfxr | Evento (código) |
|---------|----------|---------------------|-----------------|
| `bounce.wav` | 80–120 ms | Tink electrónico agudo, glitch corto. Preset **Hit/Hurt** con pitch alto. | `logic/GameState.java:205` (`d.bounceX()`) y `:212` (`d.bounceY()`) |
| `stop.wav` | 100–200 ms | Plop suave / "settle" eléctrico, descarga corta. | `logic/DiscProjectile.stop()` — cuando se agotan los rebotes y queda quieto. |
| `pickup.wav` | 150 ms | Chime / "recharge" corto, ascendente. Preset **Pickup/Coin**. | `logic/GameState.java:228` (dueño recoge disco quieto). |
| `respawn.wav` | 200–300 ms | Reset / teletransporte digital, "boot up" rápido. | `logic/GameState.respawnPlayersAfterHit()` (líneas 242, 256). |
| `ui_click.wav` | 60–100 ms | Beep confirmación seco. | Cualquier `JButton.addActionListener` en `WelcomeScreen` / `NameInputScreen` / `GameOverScreen`. |
| `ui_hover.wav` *(opcional)* | 40–70 ms | Tick muy bajo, casi inaudible. | Hover de botones (requiere `MouseListener` extra). |
| `bike_end.wav` *(opcional)* | 150–250 ms | Descarga / fade-down sintético. | Cuando `Player.isOnBike()` pasa a `false` tras los 5 s del modo moto. |

## Imágenes recomendadas

> Las imágenes en `docs/branding/images/` **no aplican directo** a Neon Trails (son de otro juego con modos Story/Survival/Two-Player y controles diferentes — `instructions_page.png` habla de "lightcycle/jump/boost" que aquí no existen). Pero el **estilo tipográfico** (fuente geometrica tipo Tron, blanco sobre negro, glow leve) sí es el target.

### Sirven como referencia visual de estilo
- `main_menu.png`, `play_before.png`, `quit_before.png`, `restart.png`, `instructions_before.png` → títulos blancos en fuente Tron sobre fondo negro. Usar este *look* para todos los títulos de Neon Trails.
- `over.png` → "GAME OVER" pulido; basar nuestro propio "GAME OVER" en este.
- `p1_wins.png` / `p2_wins.png` / `tie.png` / `win.png` → casi reutilizables tal cual; verificar que la paleta combine con **cian (P1)** / **rosa (P2)** que ya usa `GameState.java:34-35`.

### NO sirven (descartar)
- `story.png`, `survival.png`, `two_player.png` — Neon Trails es solo 2P local, no tiene modos seleccionables.
- `instructions_page.png` — los controles que documenta son de otro juego (jump, boost). Hay que rehacer las instrucciones.
- `high_scores.png` — el PDF de Técnicas no exige ranking todavía; queda para un Hito futuro.

### Imágenes nuevas a producir (mismo estilo neón blanco/cian sobre negro)
| Imagen | Dimensiones | Pantalla | Contenido |
|--------|-------------|----------|-----------|
| `neon_trails_logo.png` | ~600×200 px | `WelcomeScreen` (título) | Texto "NEON TRAILS" en fuente Tron, blanco con glow cian. |
| `instructions_neontrails.png` | ~600×400 px | Pantalla de instrucciones (futura) | Controles reales: **P1** = WASD + E (disco) + Q (moto). **P2** = flechas + Enter (disco) + U (moto). Mecánica nueva: disco rebota 3 veces y queda quieto; solo el dueño puede recogerlo. |
| `hud_background.png` *(opcional)* | ancho HUD × 60 px | Tira inferior del HUD en `GamePanel.drawHud()` | Sin texto, solo gradiente neón sutil. |
| `over_neontrails.png` | similar a `over.png` (~460×60 px) | `GameOverScreen` | Texto "GAME OVER" estilo Tron, mismo *look* que `branding/over.png` pero con paleta del proyecto. |

## Resumen para Jaco

**Total a producir / conseguir:**
- **4 SFX obligatorios** (Hito 4 PDF) — ver sección "Sonidos requeridos (Hito 4)" arriba.
- **3 archivos de música** (1 ya existe: `JourneySeparateWays.mp3`; faltan `bg_game`, `bg_gameover_win`, `bg_gameover_lose`).
- **7 SFX opcionales** para feel Tron completo (`bounce`, `stop`, `pickup`, `respawn`, `ui_click`, `ui_hover`, `bike_end`).
- **4 imágenes nuevas** en estilo neón blanco/cian sobre negro.

**Prioridad sugerida:**
1. 🔴 Los 4 SFX del Hito 4 (`shoot`, `hit`, `bike`, `gameover`) — requeridos para la entrega académica.
2. 🟡 `bg_game.wav` (música in-game loopeable estilo Daft Punk – Tron Legacy).
3. 🟡 Decidir formato de la intro `JourneySeparateWays.mp3` (Opción A: convertir a WAV; Opción B: agregar JLayer al `pom.xml`).
4. 🟢 SFX opcionales (`bounce`, `stop`, `pickup`, `respawn`, `ui_click`).
5. 🟢 Imágenes nuevas (`neon_trails_logo`, `instructions_neontrails`, `over_neontrails`).

**Licencia:** todo CC0 o equivalente. Fuentes recomendadas:
- SFX: [jsfxr.me](https://sfxr.me) (CC0, genera al instante en el navegador).
- Música / jingles: [kenney.nl](https://kenney.nl) y [opengameart.org](https://opengameart.org) filtrando por CC0.
- Fuentes tipográficas tipo Tron: buscar en [dafont.com](https://dafont.com) categoría "Sci-Fi" con licencia libre (`Tr2n`, `TRON.TTF`, etc. — verificar licencia).
- Atribuciones a registrar en una futura `CreditsScreen`.

**Reglas de formato:**
- Con JLayer (recomendado) → MP3 válido para música larga (intro / loops de fondo); los SFX siguen en WAV por latencia.
- Sin JLayer → convertir música a WAV PCM 16-bit. Ver política de dependencias en [`AGENTS.md`](AGENTS.md#política-de-dependencias) y detalles en [`docs/multimedia-libraries.md`](docs/multimedia-libraries.md).