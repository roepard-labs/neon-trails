# AGENTS.md — web-pf-ts

Vue 3 + Vite 8 SPA with TypeScript, Pinia, Vue Router, Tailwind v4, and shadcn-vue. Student/final project.

## Commands

| Command | What it does |
|---------|-------------|
| `pnpm dev` | Vite dev server (HMR) |
| `pnpm build` | `vue-tsc --build` → `vite build` (type errors block build) |
| `pnpm type-check` | `vue-tsc --build` only (incremental, uses `tsBuildInfoFile`) |
| `pnpm lint` | `oxlint . --fix` then `eslint . --fix --cache` (in order) |
| `pnpm format` | `prettier --write --experimental-cli src/` |

- **pnpm only.** No npm/yarn. Lockfile is `pnpm-lock.yaml`. Version locked to `pnpm@11.0.9` (`packageManager` field).
- **No test command exists.** No vitest, jest, or other test runner. The `CLAUDE.md` references `npm test` but it doesn't exist.
- **Node.js**: `>=22` (engines field).

## Toolchain quirks

- **Prettier requires `--experimental-cli` flag.** Standard `prettier --write` won't work; use `pnpm format`.
- **Type-check uses build mode** (`vue-tsc --build`), not `--noEmit`. Uses project references from root `tsconfig.json`. Incremental cache in `node_modules/.tmp/`.
- **Three tsconfig files**: root (`tsconfig.json`) with references to `tsconfig.app.json` (app code) and `tsconfig.node.json` (build/config code like `vite.config.ts`, `eslint.config.ts`).
- **Lint order matters**: oxlint runs first (`--fix`), then eslint. ESLint config pulls oxlint rules via `buildFromOxlintConfigFile('.oxlintrc.json')`.
- **ESLint uses flat config** (`eslint.config.ts` with `jiti` for TypeScript config loading).
- **ESLint cache** is at `.eslintcache` (gitignored).
- **VSCode**: format on save via Prettier. Fix-all on save. Extensions: Volar, ESLint, EditorConfig, oxc, Prettier.

## Architecture

```
src/
├── main.ts              # createApp → Pinia → Router → mount('#app')
├── App.vue              # Root: <RouterView /> only (no layout/header)
├── style.css            # Tailwind v4 + CSS design tokens + Google Fonts (JetBrains Mono)
├── router/index.ts      # createRouter with createWebHistory, eagerly loads HomeView
├── stores/counter.ts    # Pinia stores (Composition API: defineStore('name', () => {…}))
├── views/HomeView.vue   # Route-level component at '/'
├── components/ui/       # shadcn-vue UI primitives (barrel export pattern)
│   └── button/          #   index.ts (exports Button + buttonVariants) + Button.vue
└── lib/utils.ts         # cn() utility (clsx + tailwind-merge)
```

- **Path alias**: `@/` → `./src/` (configured in `vite.config.ts` and `tsconfig.app.json`).
- **shadcn-vue aliases** (`components.json`): `@/components`, `@/components/ui`, `@/lib/utils`, `@/lib`, `@/composables`.
- **Pinia stores follow Composition API pattern**: `export const useXStore = defineStore('x', () => { … return { state, getters, actions } })`.
- **Component pattern**: shadcn-vue uses barrel exports (`index.ts` re-exports component + variants). Import with `import { Button } from '@/components/ui/button'`.
- **CSS**: Tailwind v4 via `@tailwindcss/vite` plugin. No `tailwind.config` file — all config is CSS-first in `src/style.css`. Design tokens use OKLCH colors, dark mode via `.dark` class.
- **Animations**: GSAP, `@vueuse/motion`, and `tw-animate-css`.

## Key dependencies

| Dependency | Purpose |
|-----------|---------|
| `reka-ui` | Headless UI primitives (shadcn-vue foundation) |
| `class-variance-authority` | Variant-based component styling (`cva`) |
| `clsx` + `tailwind-merge` | Class merging utility (`cn()` in `lib/utils.ts`) |
| `lucide-vue-next` | Icon library (configured in `components.json`) |
| `@vueuse/core` | Vue composable utilities |
| `@vueuse/motion` | Declarative Vue animations |
| `gsap` | Animation library |
| `unplugin-icons` | Icon auto-import (dev) |
| `rollup-plugin-visualizer` | Bundle size visualization (dev) |

## TypeScript

- **Strict indexed access**: `noUncheckedIndexedAccess: true` in `tsconfig.app.json`. Array/object indexing returns `T | undefined`. Always handle the undefined case.
- **Vue SFC types**: handled by `vue-tsc` + Volar extension. Standard `tsc` can't parse `.vue` files.
- **env.d.ts**: at project root (not `src/`). Only `/// <reference types="vite/client" />` — enables Vite env type hints.

## Style conventions

- 2-space indent, LF line endings, UTF-8, trailing whitespace trimmed (EditorConfig)
- No semicolons, single quotes, 100 char print width (Prettier)
- Vue SFCs use `<script setup lang="ts">` with Composition API
- Max file length: 500 lines (from CLAUDE.md rule)
- Tailwind classes for all styling — no custom CSS in components unless unavoidable


## CI/CD

- GitHub Actions workflow at `.github/workflows/ci.yml`
- Runs on push/PR to `main`, manual dispatch supported
- Installs with `pnpm install --frozen-lockfile` (pnpm 11, Node 22)
- Runs `pnpm build` (vue-tsc + vite build) then uploads `dist/` artifact for 7 days
- No test job (no test framework exists)

## Anti-patterns

- **NEVER** use `npm`/`yarn` — `pnpm` only (lockfile + `packageManager` field)
- **NEVER** use bare `prettier --write` — always `pnpm format` (needs `--experimental-cli`)
- **NEVER** run eslint before oxlint — lint order is oxlint → eslint
- **NEVER** use `tsc` or `vue-tsc` without `--build` for type-checking
- **NEVER** create files unless editing existing ones won't suffice
- **NEVER** add files to repo root — use `/src`, `/tests`, `/docs`, `/config`, `/scripts`
- **NEVER** exceed 500 lines per file
