/**
 * Datos de la sección "Herramientas".
 *
 * mise (versiones), make (atajos de tareas) y CI (los 3 workflows). Las tablas
 * de versiones y de targets las pinta @tanstack/vue-table en la vista; aquí van
 * solo las filas (datos reales del repo) y los snippets para <CodeBlock>.
 */
import type { CodeSnippet } from './stack'

export interface ToolVersion {
  herramienta: string
  version: string
  /** Dónde queda fijada esa versión (fuente de verdad). */
  fijadaEn: string
  para: string
}

export interface MakeTarget {
  target: string
  grupo: 'Java' | 'Web' | 'API' | 'Docker'
  descripcion: string
}

export interface CiWorkflow {
  nombre: string
  archivo: string
  descripcion: string
}

// ── Analogías "para niños" ────────────────────────────────────────────────
export const miseSimple =
  'mise es un organizador de juguetes por proyecto: al entrar a esta carpeta le pone a Java la versión exacta (17) sin que tengas que recordarla ni pelear con otras versiones que tengas instaladas en la compu.'

export const makeSimple =
  'make es un botón mágico con nombres cortos: escribes una palabra (make run, make up) y él hace por ti toda la tarea larga —comandos enormes de Maven, pnpm o Docker— sin que tengas que memorizarlos.'

export const ciSimple =
  'CI es un robot revisor en la nube: cada vez que subes código a GitHub, vuelve a compilar y probar TODO desde cero. Si algo se rompe, te avisa con una ✗ roja antes de que llegue a la entrega.'

// ── Encuadre técnico ───────────────────────────────────────────────────────
export const miseDetalle =
  'El .mise.toml fija el JDK del juego para el desarrollo local. Node 24.16.0 y PHP 8.5 se fijan en el Dockerfile y en los workflows de CI, de modo que la versión es idéntica en tu compu, en CI y en producción.'

export const makeDetalle =
  'Un único Makefile en la raíz orquesta las tres capas con prefijos (java-*, web-*, api-*, docker-*). Es autodocumentado: `make` a secas lista cada target con su descripción.'

export const ciDetalle =
  'Tres workflows independientes (uno por capa) que solo se disparan cuando cambian sus archivos (filtro paths). Reproducen exactamente los comandos locales: mvn verify, pnpm build y php artisan test.'

// ── Tabla de versiones (TanStack) ───────────────────────────────────────────
export const toolVersions: ToolVersion[] = [
  {
    herramienta: 'Java (Temurin JDK)',
    version: '17 LTS',
    fijadaEn: '.mise.toml · pom.xml · CI java.yml',
    para: 'Compilar y correr el juego Swing',
  },
  {
    herramienta: 'Node.js',
    version: '24.16.0',
    fijadaEn: 'Dockerfile · CI vue.yml',
    para: 'Construir la SPA Vue',
  },
  {
    herramienta: 'pnpm',
    version: '11.0.9',
    fijadaEn: 'package.json (packageManager) + corepack',
    para: 'Gestor de paquetes del front',
  },
  {
    herramienta: 'PHP',
    version: '8.5',
    fijadaEn: 'Dockerfile (ppa:ondrej) · CI laravel.yml',
    para: 'Backend Laravel 13 + Filament v5',
  },
  {
    herramienta: 'Composer',
    version: '2',
    fijadaEn: 'Dockerfile (composer:2)',
    para: 'Dependencias de PHP',
  },
  {
    herramienta: 'Maven',
    version: '3.9',
    fijadaEn: 'Dockerfile (maven:3.9-eclipse-temurin-17)',
    para: 'Build del fat jar del juego',
  },
]

// ── Tabla de targets de make (TanStack) ─────────────────────────────────────
export const makeTargets: MakeTarget[] = [
  { target: 'compile', grupo: 'Java', descripcion: 'Compila las clases del juego' },
  { target: 'run', grupo: 'Java', descripcion: 'Arranca el juego (mvn exec:java)' },
  { target: 'test', grupo: 'Java', descripcion: 'Ejecuta los tests JUnit' },
  { target: 'package', grupo: 'Java', descripcion: 'Genera el fat jar en target/' },
  { target: 'verify', grupo: 'Java', descripcion: 'Pipeline Maven completo (paridad con CI)' },
  { target: 'web-dev', grupo: 'Web', descripcion: 'Servidor de desarrollo Vite' },
  { target: 'web-build', grupo: 'Web', descripcion: 'Build de producción (web/dist)' },
  { target: 'web-lint', grupo: 'Web', descripcion: 'Lint (oxlint + eslint)' },
  { target: 'web-type-check', grupo: 'Web', descripcion: 'Chequeo de tipos (vue-tsc)' },
  { target: 'api-migrate', grupo: 'API', descripcion: 'Ejecuta las migraciones' },
  { target: 'api-serve', grupo: 'API', descripcion: 'Servidor local de Laravel (:8000)' },
  { target: 'api-test', grupo: 'API', descripcion: 'Tests Pest del API' },
  { target: 'docker-build', grupo: 'Docker', descripcion: 'Construye la imagen monolítica' },
  { target: 'up', grupo: 'Docker', descripcion: 'Levanta el monolito + MariaDB (compose)' },
  { target: 'down', grupo: 'Docker', descripcion: 'Detiene y elimina los contenedores' },
  { target: 'logs', grupo: 'Docker', descripcion: 'Sigue los logs del monolito' },
]

// ── Workflows de CI ──────────────────────────────────────────────────────────
export const ciWorkflows: CiWorkflow[] = [
  {
    nombre: 'Java CI',
    archivo: '.github/workflows/java.yml',
    descripcion: 'Compila, testea y empaqueta el juego con mvn verify (JDK 17).',
  },
  {
    nombre: 'Laravel CI',
    archivo: '.github/workflows/laravel.yml',
    descripcion: 'Corre la suite Pest sobre SQLite con PHP 8.5.',
  },
  {
    nombre: 'Vue CI',
    archivo: '.github/workflows/vue.yml',
    descripcion: 'Lint + vue-tsc + vite build con Node 24.16.0; sube el artefacto dist.',
  },
]

// ── Snippets ────────────────────────────────────────────────────────────────
export const miseSnippet: CodeSnippet = {
  filename: '.mise.toml',
  lang: 'toml',
  player: 'p1',
  note: 'Mínimo a propósito: mise solo gestiona el JDK local; el resto de versiones vive en Docker/CI.',
  code: `[tools]
java = "17"`,
}

export const makefileSnippet: CodeSnippet = {
  filename: 'Makefile (extracto)',
  lang: 'make',
  player: 'p2',
  note: 'El target help se autogenera leyendo los comentarios "## " de cada target — por eso `make` solo ya documenta todo.',
  code: `.DEFAULT_GOAL := help

help: ## Muestra esta ayuda
	@grep -E '^[a-zA-Z0-9_-]+:.*?## ' $(MAKEFILE_LIST) \\
		| awk 'BEGIN{FS=":.*?## "}{printf "  \\033[36m%-22s\\033[0m %s\\n", $$1, $$2}'

run: java-run                 # alias retrocompatible
java-run: ## Arranca el juego (mvn exec:java)
	$(MVN) -q exec:java

web-build: ## Build de producción (web/dist)
	cd $(WEB_DIR) && $(PNPM) build

up: ## Levanta el monolito + MariaDB (compose)
	docker compose up -d --build`,
}

export const ciSnippet: CodeSnippet = {
  filename: '.github/workflows/vue.yml (condensado)',
  lang: 'yaml',
  player: 'p1',
  note: 'Mismos comandos que en local (pnpm lint, pnpm build): si pasa aquí, pasa en la entrega.',
  code: `name: Vue CI
on:
  push: { branches: [main], paths: ['web/**'] }
  pull_request: { paths: ['web/**'] }

jobs:
  build:
    runs-on: ubuntu-latest
    defaults: { run: { working-directory: web } }
    steps:
      - uses: actions/checkout@v4
      - uses: pnpm/action-setup@v4
        with: { version: 11.0.9 }
      - uses: actions/setup-node@v4
        with: { node-version: 24.16.0, cache: pnpm }
      - run: pnpm install --frozen-lockfile
      - run: pnpm lint        # oxlint + eslint
      - run: pnpm build       # vue-tsc + vite build`,
}
