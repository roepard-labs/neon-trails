/// <reference types="vite/client" />

interface ImportMetaEnv {
  /** Base URL de la API del leaderboard. Vacío = mismo origen (monolito); en dev se enruta por el proxy de Vite. */
  readonly VITE_API_URL?: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
