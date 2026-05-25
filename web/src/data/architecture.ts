export interface ArchPillar {
  titulo: string
  bullets: string[]
}

export interface RfRnfRow {
  tipo: 'RF' | 'RNF'
  requerimiento: string
  ubicacion: string
}

export interface DocLink {
  ruta: string
  descripcion: string
}

export interface VueRoute {
  ruta: string
  nombre: string
  meta: string
}

export interface ApiEndpoint {
  metodo: string
  ruta: string
  descripcion: string
}

export const archPillars: ArchPillar[] = [
  {
    titulo: 'Backend del leaderboard (Laravel 13 + MariaDB 11)',
    bullets: [
      'API REST en /api/scores: GET sirve el Top N y POST registra puntajes validados.',
      'Modelo Score con scopeRanked() (orderByDesc score) y casts; validación con StoreScoreRequest.',
      'Panel Filament v5 en /admin para el CRUD del leaderboard, ordenado por puntaje.',
      'Rate-limit throttle:30,1 en el POST; ScoreResource fija el shape JSON (ISO8601).',
    ],
  },
  {
    titulo: 'Juego y presentación (Java Swing + Vue 3)',
    bullets: [
      'Paquetes logic / view / events; hilo neon-trails-game-loop conduce GameState.tick a ~60 Hz.',
      'Entrada con Swing Key Bindings (InputMap/ActionMap) para dos jugadores sin pérdida de foco.',
      'net/LeaderboardClient envía el puntaje con HttpClient nativo, async y tolerante a fallos.',
      'SPA Vue 3 (esta presentación + /leaderboard) servida como estáticos; juego jugable en /game vía noVNC.',
    ],
  },
]

export const rfRnfMapping: RfRnfRow[] = [
  {
    tipo: 'RF',
    requerimiento: 'Movimiento simultáneo de dos jugadores',
    ubicacion: 'logic/GameState.tick, events/InputController, view/GamePanel',
  },
  {
    tipo: 'RF',
    requerimiento: 'Disco con rebote y colisión',
    ubicacion: 'logic/DiscProjectile.java, GameConstants (DISC_MAX_TICKS)',
  },
  {
    tipo: 'RF',
    requerimiento: 'Modo moto temporal (5 s)',
    ubicacion: 'logic/GameConstants.BIKE_DURATION_SEC, GameState (request/consume)',
  },
  {
    tipo: 'RF',
    requerimiento: 'Enviar puntaje al terminar',
    ubicacion: 'net/LeaderboardClient.java → POST /api/scores',
  },
  {
    tipo: 'RF',
    requerimiento: 'Consultar el ranking',
    ubicacion: 'Api/ScoreController@index, web composables/useLeaderboard.ts',
  },
  {
    tipo: 'RNF',
    requerimiento: 'Entrada fiable para dos jugadores',
    ubicacion: 'events/KeyboardBindings (WHEN_IN_FOCUSED_WINDOW)',
  },
  {
    tipo: 'RNF',
    requerimiento: 'Animación estable ~60 Hz',
    ubicacion: 'view/GameLoop (Thread + sleep), GameConstants.GAME_TICK_MS',
  },
  {
    tipo: 'RNF',
    requerimiento: 'Tolerancia a fallos del envío',
    ubicacion: 'net/LeaderboardClient (sendAsync + try/catch + log)',
  },
  {
    tipo: 'RNF',
    requerimiento: 'Seguridad de transporte y abuso',
    ubicacion: 'routes/api.php (throttle:30,1), Traefik HTTPS en Dokploy',
  },
]

export const archFlowMermaid = `flowchart LR
  subgraph game [Juego Java Swing]
    P[2 Jugadores]
    L[GameLoop ~60 Hz]
    S[GameState]
    C[LeaderboardClient]
  end
  subgraph web [Plataforma web · monolito]
    A[Laravel API]
    D[(MariaDB 11)]
    F[Filament /admin]
    V[Vue SPA /leaderboard]
  end
  P -->|teclado| L
  L --> S
  S -->|game over| C
  C -->|POST /api/scores| A
  A --> D
  F --> D
  V -->|GET /api/scores| A
`

export const archSequenceMermaid = `sequenceDiagram
  participant J as Jugadores
  participant G as Juego (Swing)
  participant A as Laravel API
  participant D as MariaDB
  participant W as Vue /leaderboard
  J->>G: Juegan la partida
  G->>G: tick() ~60 Hz hasta game over
  G->>A: POST /api/scores (async)
  A->>D: persiste Score
  A-->>G: 201 Created
  W->>A: GET /api/scores
  A->>D: ranked() Top N
  A-->>W: ranking JSON
  W-->>J: Top puntajes
`

export const scrambleDocs: DocLink[] = [
  { ruta: '/', descripcion: 'Vue SPA — landing y presentación del proyecto (raíz del gateway nginx).' },
  { ruta: '/api', descripcion: 'Laravel (php-fpm) — API REST del leaderboard, mismo origen que la SPA.' },
  { ruta: '/admin', descripcion: 'Panel Filament — CRUD del leaderboard con credenciales de administrador.' },
  { ruta: '/game/', descripcion: 'noVNC — el juego Swing jugable en el navegador (WebSocket vía websockify).' },
]

export const vueRoutes: VueRoute[] = [
  {
    ruta: '/',
    nombre: 'portada',
    meta: 'Landing y presentación; punto de entrada de la SPA.',
  },
  {
    ruta: '/leaderboard',
    nombre: 'leaderboard',
    meta: 'Ranking en vivo; consume GET /api/scores con useLeaderboard.',
  },
  {
    ruta: '/arquitectura',
    nombre: 'arquitectura',
    meta: 'Esta sección: diagramas, mapeo RF/RNF y superficies del monolito.',
  },
]

export const apiEndpoints: ApiEndpoint[] = [
  {
    metodo: 'GET',
    ruta: '/api/scores',
    descripcion: 'Top N del ranking ordenado por puntaje; limit acotado a [1, 100].',
  },
  {
    metodo: 'POST',
    ruta: '/api/scores',
    descripcion: 'Registrar puntaje (StoreScoreRequest) con rate-limit throttle:30,1.',
  },
  {
    metodo: 'GET',
    ruta: '/admin',
    descripcion: 'Panel Filament del leaderboard (autenticado).',
  },
  {
    metodo: 'GET',
    ruta: '/game/',
    descripcion: 'Juego Neon Trails servido por noVNC dentro del contenedor.',
  },
]
