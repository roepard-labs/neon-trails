/**
 * Datos de la sección "Documentación & GitHub".
 *
 * Dónde vive el código (GitHub) y cómo se documenta (carpeta docs/ + README).
 * Para primer semestre: se explica qué es un repositorio y por qué documentar.
 */
import type { CodeSnippet } from './stack'

export interface DocFile {
  archivo: string
  queContiene: string
}

export const repoUrl = 'https://github.com/roepard-labs/neon-trails'

// ── GitHub ────────────────────────────────────────────────────────────────────
export const githubSimple =
  'GitHub es como una carpeta en la nube compartida con "máquina del tiempo": guarda todo el código, quién cambió qué y cuándo, y permite volver atrás si algo se rompe. Los dos integrantes trabajan sobre el mismo repositorio sin pisarse.'

export const githubDetalle =
  'El proyecto vive en el repositorio roepard-labs/neon-trails. Cada cambio se sube como un "commit" (una foto del proyecto con un mensaje). Al subir, el robot de integración continua (CI) recompila y prueba todo automáticamente, así que un error se detecta enseguida.'

// ── Documentación ─────────────────────────────────────────────────────────────
export const docsSimple =
  'Documentar es dejar notas claras para que cualquiera —el profe, un compañero, o tú mismo en seis meses— entienda cómo funciona y pueda continuarlo sin adivinar.'

export const docsDetalle =
  'La carpeta docs/ guarda las explicaciones largas (arquitectura, despliegue, pruebas), el README es la puerta de entrada, y los archivos CLAUDE.md/AGENTS.md fijan las convenciones del repo. El enunciado oficial de la cátedra (PDF) es la fuente de verdad de los requisitos.'

// ── Índice de documentación (DataTable) ──────────────────────────────────────
export const docFiles: DocFile[] = [
  { archivo: 'README.md', queContiene: 'Puerta de entrada: cómo ejecutar, controles y enlaces rápidos.' },
  { archivo: 'docs/architecture.md', queContiene: 'El modelo de paquetes y el flujo tick → render del juego.' },
  { archivo: 'docs/DEPLOY.md', queContiene: 'Cómo desplegar el monolito en Dokploy (Docker + noVNC).' },
  { archivo: 'docs/PRUEBAS.md', queContiene: 'Lista de pruebas manuales para validar la interfaz.' },
  { archivo: 'docs/multimedia-libraries.md', queContiene: 'Qué librerías multimedia se usan (SVG, sonido) y por qué.' },
  { archivo: 'docs/rules/…PDF', queContiene: 'El enunciado oficial de la cátedra: la fuente de verdad.' },
  { archivo: 'CLAUDE.md · AGENTS.md', queContiene: 'Convenciones del repo y guía para colaboradores/asistentes.' },
  { archivo: 'CHANGELOG.md', queContiene: 'Qué cambió en cada versión del proyecto.' },
]

// ── Árbol del repositorio ─────────────────────────────────────────────────────
export const treeSnippet: CodeSnippet = {
  filename: 'Estructura del repositorio',
  lang: 'bash',
  player: 'p2',
  note: 'Tres proyectos en uno: el juego (src/), el backend (api/) y esta presentación (web/).',
  code: `neon-trails/
├─ src/                  # el juego (Java)
│  ├─ main/java/
│  │  ├─ Main.java       # punto de entrada
│  │  ├─ logic/          # reglas puras (sin Swing)
│  │  ├─ view/           # ventanas y dibujo (Swing)
│  │  ├─ events/         # teclado (Key Bindings)
│  │  ├─ audio/          # sonido
│  │  └─ modules/        # GameGUI (arranque)
│  └─ test/java/logic/   # pruebas JUnit
├─ api/                  # backend del leaderboard (Laravel)
├─ web/                  # esta presentación (Vue)
├─ .docker/              # nginx, supervisord, VNC…
├─ docs/                 # documentación
├─ Dockerfile            # imagen del monolito
├─ Makefile              # atajos (make run, make up…)
└─ pom.xml               # dependencias del juego (Maven)`,
}
