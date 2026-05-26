# Plan — Reestructurar la presentación: quitar 2 slides, añadir 4 y navegación por flechas

## Contexto

La presentación web (`web/`, SPA Vue de 11 secciones) debe alinearse con el **reparto real de la exposición** entre los dos integrantes y con el público objetivo: estudiantes de **primer semestre** que no conocen Laravel, Vue, Docker, make ni Maven. Hay que **quitar dos secciones** que no aportan a ese reparto (`03 Requerimientos`, `06 Descripciones de casos de uso`) y **añadir cuatro** que hoy faltan (Historia/ADN, Librerías, Pruebas unitarias, Documentación+GitHub), explicadas de cero con el patrón de doble nivel (`ExplainCard`: 🧒 analogía + ⚙️ detalle real). Además se piden **dos botones flotantes (atrás/adelante)** abajo a la derecha para navegar las slides.

Decisiones confirmadas con el usuario: **+4 → 13 secciones**, **orden por tema** (sin agrupar por expositor).

## Estructura final (13 secciones, orden por tema)

| # | Ruta | Vista | Estado |
|---|------|-------|--------|
| 1 | `/` | PortadaView | existe |
| 2 | `/historia` | **HistoriaView** | NUEVA |
| 3 | `/problema` | ProblemaView | renum. 2→3 |
| 4 | `/arquitectura` | ArquitecturaView | existe + ampliar (package/import) |
| 5 | `/casos-de-uso` | CasosUsoView | existe |
| 6 | `/mockups` | MockupsView | renum. 7→6 |
| 7 | `/stack` | StackView | renum. 9→7 |
| 8 | `/librerias` | **LibreriasView** | NUEVA |
| 9 | `/pruebas` | **PruebasView** | NUEVA |
| 10 | `/despliegue` | DespliegueView | sin cambio (10) |
| 11 | `/herramientas` | HerramientasView | sin cambio (11) |
| 12 | `/documentacion` | **DocumentacionView** | NUEVA |
| 13 | `/leaderboard` | LeaderboardView | renum. 8→13 |

El **orden del array `sectionRoutes`** (en `web/src/router/index.ts`) es la fuente de verdad: de él derivan sidebar, command palette, saltos `1‑9`, breadcrumb y los botones nuevos. Hay que **reordenar el array** a esta secuencia, poner `meta.seccion.numero` 1..13, y actualizar `<PageHeader :numero>` en cada vista que cambie.

## Parte A — Quitar las 2 secciones y limpiar referencias

- **`web/src/router/index.ts`**: eliminar las rutas `requerimientos` y `descripciones`.
- Borrar vistas `web/src/views/RequerimientosView.vue` y `DescripcionesView.vue`.
- Borrar lo que queda huérfano **tras verificar con grep que nadie más los importa**: `web/src/data/requirements.ts`, `web/src/components/content/RequirementCategory.vue`, `web/src/components/content/UseCaseDescriptionCard.vue`.
- **`web/src/components/layout/CommandPalette.vue`**: `goUseCase()` empuja a `/descripciones#${codigo}` (ruta que desaparece). Repuntar el grupo "Casos de uso" a `/casos-de-uso` (sin hash).
- **Se conserva**: `data/useCases.ts` y `data/useCaseDiagram.ts` (los usa `CasosUsoView`), y `rfRnfMapping` en `data/architecture.ts` (lo usa `ArquitecturaView`, así que los requerimientos siguen visibles ahí en forma resumida).

## Parte B — Las 4 secciones nuevas

Patrón de cada vista nueva (copiar de `StackView`/`DespliegueView`): `PageHeader`, `useSectionAnimation()`, `class="snap-slide"`, bloques `data-anim`, y mucho `ExplainCard` (doble nivel) + `CodeBlock` + `DataTable` con **datos reales del repo** en `web/src/data/*.ts` (reusar el tipo `CodeSnippet` de `stack.ts`). Reusar `NeonSprite` como adorno donde encaje.

### B1. `HistoriaView` (`/historia`, #2) · icono `Dna` — *Jaco*
`data/history.ts`. Cubre **por qué Tron y en qué nos basamos**:
- **Inspiración**: las motos de luz de *Tron* (estela, arena, disco) → analogía simple.
- **Mandato académico**: el PDF de cátedra (`docs/rules/…`) pide demostrar **POO**: 4 pilares, organización en **paquetes**, **hilos**, **multimedia** y **persistencia** (texto base en `data/project.ts` → `problemaContexto.resumen`).
- **ADN de diseño**: estética neón + sprites del *handoff* de Claude Design (ver `docs/multimedia-libraries.md`).
- **Evolución** (timeline corto con `StepList`): juego Java Swing → + leaderboard web (Laravel/MariaDB) → + jugable en navegador (Docker/noVNC).

### B2. `LibreriasView` (`/librerias`, #8) · icono `Library` — *Jemg*
`data/libraries.ts`. **Qué es una librería/dependencia y para qué sirve** (analogía: piezas de Lego ya hechas vs. fabricarlas tú). Contenido real de `pom.xml`:
- `ExplainCard` "¿Qué es Maven?" (gestor de dependencias + constructor).
- `DataTable` de dependencias: `batik-transcoder`/`batik-codec` (render de los SVG animados → `view/SpriteLoader.java`), `xercesImpl` (parser XML que Batik necesita en runtime), `jsoup` (parser HTML), `junit-jupiter` (pruebas). Columnas: librería · versión · qué es · para qué.
- `CodeBlock` con el bloque `<dependencies>` real del `pom.xml`.
- `ExplainCard` de plugins Maven (compiler, surefire, jar, exec, **shade** = "mete todo en un solo .jar ejecutable").
- Nota breve: la web tiene sus propias librerías (Vue, Vite, Tailwind, Shiki…) en `web/package.json` (enlaza mentalmente con la sección Stack).
- **Requiere** añadir `xml` a `SHIKI_LANGS` en `web/src/lib/shiki.ts` (verificar que el lenguaje exista en el bundle de shiki; si no, usar `html`).

### B3. `PruebasView` (`/pruebas`, #9) · icono `FlaskConical` — *Jaco*
`data/tests.ts`. **Qué es una prueba unitaria y para qué** (analogía: probar cada pieza por separado antes de armar el juguete). Material real (`src/test/java/logic/`):
- `ExplainCard` con el patrón **Arrange‑Act‑Assert** y `make test` / `mvn test` (JUnit 5).
- `CodeBlock` con ejemplos reales: `GameConstantsTest.bikeDurationMatchesSpec()` (assert de que la moto dura 5 s, atado al PDF) y `DiscProjectileTest.tickAdvancesByVelocity()` (arrange‑act‑assert claro).
- `DataTable` de los 6 tests (`PlayerTest`, `DiscProjectileTest`, `GameConstantsTest`, `BikeReversalTest`, `GameStateEventsTest`, `MotoTrailTest`) → qué invariante verifica cada uno.
- `ExplainCard` "por qué solo se prueba `logic/`": es lógica pura sin Swing/EDT; la UI se valida a mano con `docs/PRUEBAS.md`. El CI (`java.yml`) corre `mvn -B verify` en cada push.

### B4. `DocumentacionView` (`/documentacion`, #12) · icono `Github` — *Jemg*
`data/docs.ts`. **Dónde vive el código y cómo se documenta**:
- `ExplainCard` GitHub: el repo `roepard-labs/neon-trails` (origin real), commits, y que el CI revisa cada push (enlaza con Herramientas sin duplicar el detalle de workflows).
- `CodeBlock` con un árbol del repo (`src/`, `api/`, `web/`, `.docker/`, `docs/`) para ubicar las piezas.
- `DataTable`/lista de la documentación: `README.md`, `docs/architecture.md`, `docs/DEPLOY.md`, `docs/PRUEBAS.md`, `docs/multimedia-libraries.md`, `docs/rules/…PDF`, `CLAUDE.md`/`AGENTS.md`, `CHANGELOG.md` → qué contiene cada uno.
- `ExplainCard` "¿por qué documentar?" (que cualquiera entienda y continúe el proyecto).

## Parte C — Ampliar `ArquitecturaView` (package/import, "lo visto en clase")

Añadir a `ArquitecturaView` un bloque nuevo (`ExplainCard` + `CodeBlock`) que explique **carpetas = paquetes** e **import = traer una clase de otra carpeta**, con código real de `logic/GameState.java` (`package logic;` + `import events.InputController;`). Mantener intactos los diagramas Mermaid y las cards existentes.

## Parte D — Botones flotantes atrás/adelante

- Nuevo componente `web/src/components/layout/SlideNavButtons.vue`: dos botones redondos (`ChevronLeft`/`ChevronRight`) fijos abajo a la derecha (`fixed bottom-5 right-5 z-30`, respetando `safe-area`, ocultos en `print`, con acento neón). Deshabilitados en los extremos.
- Lógica: nuevo composable `web/src/composables/useSectionNav.ts` que deriva `{ prev, next, hasPrev, hasNext, index, total }` de `sectionRoutes` + `useRoute`/`useRouter`. Reutilizarlo también en `useAppShortcuts.ts` (que hoy duplica `nextSection/prevSection`) para no tener dos fuentes.
- Montar `<SlideNavButtons />` en `web/src/components/layout/AppShell.vue` (junto a `CommandPalette`/`Toaster`).

## Parte E — Integración y renumeración

- **`router/index.ts`**: reordenar `sectionRoutes` al orden de la tabla; `numero` 1..13; añadir 4 rutas lazy con `meta.seccion` (titulo/subtitulo/icon).
- **`PageHeader :numero`** en las vistas que cambian de número: Problema (→3), Mockups (→6), Stack (→7), Leaderboard (→13). Las nuevas usan 2/8/9/12.
- **iconMap** en `SidebarNavContent.vue` y `CommandPalette.vue`: añadir `Dna`, `Library`, `FlaskConical`, `Github`.
- **`PortadaView.vue`**: "— 11 secciones" → "— 13 secciones".

## Inventario de archivos

**Crear:** `web/src/views/{HistoriaView,LibreriasView,PruebasView,DocumentacionView}.vue`; `web/src/data/{history,libraries,tests,docs}.ts`; `web/src/components/layout/SlideNavButtons.vue`; `web/src/composables/useSectionNav.ts`.

**Modificar:** `web/src/router/index.ts`; `web/src/components/layout/{AppShell,SidebarNavContent,CommandPalette}.vue`; `web/src/composables/useAppShortcuts.ts`; `web/src/views/{ProblemaView,MockupsView,StackView,LeaderboardView,ArquitecturaView,PortadaView}.vue`; `web/src/lib/shiki.ts` (+`xml` si aplica).

**Borrar (tras verificar imports):** `web/src/views/{RequerimientosView,DescripcionesView}.vue`; `web/src/data/requirements.ts`; `web/src/components/content/{RequirementCategory,UseCaseDescriptionCard}.vue`.

## Verificación

1. `cd web && pnpm type-check` y `pnpm lint` limpios (sin imports rotos tras borrar/renumerar).
2. `pnpm build` y `make web-build` exitosos.
3. `pnpm dev` y revisar manualmente:
   - **13 secciones** en sidebar, command palette (⌘K) y saltos `1‑9`; sin `/requerimientos` ni `/descripciones`.
   - Numeración correcta en breadcrumb y `PageHeader` (1..13) y barra de progreso.
   - Las 4 nuevas con `ExplainCard`/`CodeBlock`/`DataTable` y resaltado correcto (incl. el `pom.xml`).
   - **Botones atrás/adelante** abajo a la derecha: navegan en orden y se deshabilitan en portada (atrás) y leaderboard (adelante); coherentes con flechas ←/→.
   - `ArquitecturaView` muestra el bloque package/import.
