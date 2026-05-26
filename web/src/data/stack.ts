/**
 * Datos de la sección "Stack & Código en vivo".
 *
 * Cada capa del monolito (juego Java · backend Laravel · frontend Vue · datos
 * SQL) trae una analogía sencilla y fragmentos de código REALES extraídos del
 * repositorio, listos para resaltar con Shiki en <CodeBlock>. Si tocas el código
 * fuente, actualiza también estos extractos para que la presentación no mienta.
 */

export interface CodeSnippet {
  filename: string
  lang: string
  code: string
  /** Acento del marco: cyan (P1) o magenta (P2). */
  player?: 'p1' | 'p2'
  /** Nota corta bajo el bloque (qué mirar). */
  note?: string
}

export interface StackLayer {
  id: 'juego' | 'backend' | 'frontend' | 'datos'
  label: string
  /** Nombre de icono lucide (lo resuelve la vista). */
  icon: string
  tagline: string
  /** Analogía "para niños". */
  simple: string
  /** Encuadre técnico. */
  detalle: string
  snippets: CodeSnippet[]
}

export const stackLayers: StackLayer[] = [
  {
    id: 'juego',
    label: 'Juego (Java)',
    icon: 'Gamepad2',
    tagline: 'Java 17 + Swing · un hilo a ~60 Hz',
    simple:
      'El juego es como un cuaderno de dibujos animados (flipbook): pasa 60 hojas por segundo y en cada hoja los muñecos se mueven un poquito. Un ayudante (el "hilo") es quien pasa las hojas a ese ritmo, y en cada hoja revisa: ¿se movieron?, ¿lanzaron el disco?, ¿alguien chocó?',
    detalle:
      'El núcleo jugable vive en el paquete logic/ y no importa nada de Swing (lógica pura, fácil de testear). Un único Thread dedicado (view/GameLoop) llama a GameState.tick() cada 16 ms y luego pide repaint(). El orden del tick es fijo: movimiento → enfriamiento → disparo → moto → discos → colisiones.',
    snippets: [
      {
        filename: 'logic/GameConstants.java',
        lang: 'java',
        player: 'p1',
        note: 'Todos los números que balancean el juego viven en un solo sitio (requisito del enunciado).',
        code: `public final class GameConstants {
    /** Área de juego por defecto (px). */
    public static final int DEFAULT_WIDTH  = 960;
    public static final int DEFAULT_HEIGHT = 640;

    /** Duración del modo moto en segundos (requisito: 5 s). */
    public static final double BIKE_DURATION_SEC = 5.0;

    /** Período del bucle de juego (ms) → ~60 fotogramas por segundo. */
    public static final int GAME_TICK_MS = 16;

    public static final double PLAYER_SPEED        = 3.2;
    public static final double BIKE_SPEED_MULT     = 1.75;
    public static final double DISC_SPEED          = 7.5;
    public static final int    DISC_MAX_TICKS      = 240;
    public static final int    FIRE_COOLDOWN_TICKS = 28;
}`,
      },
      {
        filename: 'logic/GameState.java — tick() (condensado)',
        lang: 'java',
        player: 'p1',
        note: 'Una "hoja" del flipbook: el orden de estos pasos es parte del diseño y no se altera a la ligera.',
        code: `public void tick(InputController input, int width, int height) {
    if (finished) return;                       // partida terminada: no avanzar

    applyMovementInput(input);                  // 1) teclas → dirección normalizada
    playerOne.moveWithinBounds(width, height);
    playerTwo.moveWithinBounds(width, height);

    playerOne.tickCooldown();                   // 2) enfría el disparo
    playerTwo.tickCooldown();

    tryShoot(playerOne, input.isP1Shoot(), width, height);  // 3) disparar disco
    tryShoot(playerTwo, input.isP2Shoot(), width, height);

    if (input.consumeP1BikeRequest()) playerOne.activateBike();  // 4) moto (5 s)
    if (input.consumeP2BikeRequest()) playerTwo.activateBike();

    updateDiscs(width, height);                 // 5) mover discos + rebotes
    resolveDiscHits();                          // 6) ¿un disco golpeó a alguien?
    resolveTrailHits();                         // 7) ¿alguien chocó contra un rastro?
}`,
      },
      {
        filename: 'view/GameLoop.java — run()',
        lang: 'java',
        player: 'p1',
        note: 'El "ayudante" que pasa las hojas: avanza un tick, calcula cuánto sobró de los 16 ms y duerme ese resto.',
        code: `@Override
public void run() {
    while (running) {
        long t0 = System.currentTimeMillis();
        panel.stepSimulation();                          // un tick + repaint()
        long elapsed = System.currentTimeMillis() - t0;
        long sleep = GameConstants.GAME_TICK_MS - elapsed;   // 16 ms ≈ 60 Hz
        if (sleep > 0) {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }
}`,
      },
    ],
  },
  {
    id: 'backend',
    label: 'Backend (Laravel + Filament)',
    icon: 'Server',
    tagline: 'Laravel 13 + Filament v5 · PHP 8.5',
    simple:
      'La API es una libreta de récords con un guardia en la puerta. El juego le entrega un papelito: "¡saqué 7 puntos!". El guardia revisa que no sea trampa (que el nombre no sea larguísimo, que el puntaje sea un número de verdad) y recién entonces lo apunta. Cuando alguien pregunta por el ranking, el guardia lee la libreta de mayor a menor.',
    detalle:
      'Dos rutas en routes/api.php: GET para leer el ranking y POST (con throttle) para registrar. El FormRequest valida la entrada antes de tocar la base; el Eloquent Resource decide exactamente qué campos salen en el JSON; y el panel Filament reutiliza el mismo modelo para administrar los puntajes a mano.',
    snippets: [
      {
        filename: 'api/routes/api.php',
        lang: 'php',
        player: 'p2',
        note: 'throttle:30,1 = máximo 30 envíos por minuto, para que nadie inunde la libreta.',
        code: `Route::get('scores', [ScoreController::class, 'index']);

Route::post('scores', [ScoreController::class, 'store'])
    ->middleware('throttle:30,1');`,
      },
      {
        filename: 'app/Http/Controllers/Api/ScoreController.php',
        lang: 'php',
        player: 'p2',
        code: `class ScoreController extends Controller
{
    // GET /api/scores → ranking (mayores puntajes primero)
    public function index(Request $request): AnonymousResourceCollection
    {
        $limit = max(1, min($request->integer('limit', 10), 100));

        return ScoreResource::collection(
            Score::ranked()->limit($limit)->get()
        );
    }

    // POST /api/scores → el juego registra el puntaje al terminar la partida
    public function store(StoreScoreRequest $request): JsonResponse
    {
        $score = Score::create($request->validated());

        return ScoreResource::make($score)
            ->response()
            ->setStatusCode(Response::HTTP_CREATED);
    }
}`,
      },
      {
        filename: 'app/Http/Requests/StoreScoreRequest.php',
        lang: 'php',
        player: 'p2',
        note: 'El "guardia": si algo no cumple estas reglas, Laravel responde 422 y NO se guarda nada.',
        code: `public function rules(): array
{
    return [
        'player_name'     => ['required', 'string', 'max:40'],
        'score'           => ['required', 'integer', 'min:0', 'max:100000'],
        'result'          => ['nullable', Rule::enum(MatchResult::class)],
        'match_played_at' => ['nullable', 'date'],
    ];
}`,
      },
      {
        filename: 'app/Models/Score.php',
        lang: 'php',
        player: 'p2',
        note: 'scopeRanked() encapsula el orden del ranking; los casts convierten texto ↔ enum/fecha automáticamente.',
        code: `class Score extends Model
{
    protected $fillable = ['player_name', 'score', 'result', 'match_played_at'];

    // Ranking: mayor puntaje primero, desempate por antigüedad.
    public function scopeRanked(Builder $query): void
    {
        $query->orderByDesc('score')->orderBy('created_at');
    }

    protected function casts(): array
    {
        return [
            'score'           => 'integer',
            'result'          => MatchResult::class,
            'match_played_at' => 'datetime',
        ];
    }
}`,
      },
      {
        filename: 'app/Http/Resources/ScoreResource.php',
        lang: 'php',
        player: 'p2',
        note: 'Define la forma EXACTA del JSON que recibe la web (la interfaz LeaderboardEntry de Vue es su espejo).',
        code: `public function toArray(Request $request): array
{
    return [
        'id'          => $this->id,
        'player_name' => $this->player_name,
        'score'       => $this->score,
        'result'      => $this->result?->value,
        'played_at'   => $this->match_played_at?->toIso8601String(),
        'created_at'  => $this->created_at?->toIso8601String(),
    ];
}`,
      },
      {
        filename: 'app/Filament/Resources/Scores/Tables/ScoresTable.php',
        lang: 'php',
        player: 'p2',
        note: 'El mismo modelo Score, ahora como tabla administrable en /admin (buscar, ordenar, editar, borrar).',
        code: `public static function configure(Table $table): Table
{
    return $table
        ->columns([
            TextColumn::make('player_name')->searchable(),
            TextColumn::make('score')->numeric()->sortable(),
            TextColumn::make('result')->badge()->searchable(),
            TextColumn::make('match_played_at')->dateTime()->sortable(),
        ])
        ->defaultSort('score', 'desc')
        ->recordActions([EditAction::make()])
        ->toolbarActions([
            BulkActionGroup::make([DeleteBulkAction::make()]),
        ]);
}`,
      },
    ],
  },
  {
    id: 'frontend',
    label: 'Frontend (Vue + TS)',
    icon: 'Code2',
    tagline: 'Vue 3 + TypeScript + Vite + Tailwind v4',
    simple:
      'La web es una ventanita curiosa: le pregunta a la libreta "¿cómo va el ranking?" y lo pinta bonito en la pantalla. Si la libreta está dormida y no contesta, la ventanita no se rompe: muestra la lista vacía y avisa con calma.',
    detalle:
      'Un composable (useLeaderboard) hace fetch a /api/scores y expone entries/loading/error de forma reactiva, tolerante a fallos. En desarrollo no hay servidor unificado, así que el proxy de Vite reenvía /api al Laravel de :8000; en producción todo es el mismo origen (sin CORS).',
    snippets: [
      {
        filename: 'src/composables/useLeaderboard.ts',
        lang: 'ts',
        player: 'p1',
        note: 'try/catch/finally: pase lo que pase, loading vuelve a false y la UI nunca se queda colgada.',
        code: `export function useLeaderboard() {
  const entries = ref<LeaderboardEntry[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function load(limit = 20) {
    loading.value = true
    error.value = null
    try {
      const res = await fetch(\`\${API_BASE}/api/scores?limit=\${limit}\`, {
        headers: { Accept: 'application/json' },
      })
      if (!res.ok) throw new Error(\`HTTP \${res.status}\`)
      const json = await res.json()
      entries.value = Array.isArray(json) ? json : (json.data ?? [])
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'No se pudo cargar el ranking.'
      entries.value = []
    } finally {
      loading.value = false
    }
  }

  return { entries, loading, error, load }
}`,
      },
      {
        filename: 'src/router/index.ts (extracto)',
        lang: 'ts',
        player: 'p1',
        note: 'SPA con history mode: cada sección es una ruta; lo desconocido redirige a la portada.',
        code: `const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    ...sectionRoutes,                       // las 11 secciones de la presentación
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
  scrollBehavior(to) {
    if (to.hash) return { el: to.hash, behavior: 'smooth' }
    return { top: 0, behavior: 'smooth' }
  },
})`,
      },
      {
        filename: 'vite.config.ts (extracto)',
        lang: 'ts',
        player: 'p1',
        note: 'Solo afecta a dev. Permite codear la web sin Docker hablando con el Laravel local.',
        code: `export default defineConfig({
  // …plugins, alias '@' → ./src …
  server: {
    // En dev, la SPA llama a /api en el mismo origen; el proxy lo reenvía a Laravel.
    proxy: {
      '/api': {
        target: 'http://localhost:8000',
        changeOrigin: true,
      },
    },
  },
})`,
      },
    ],
  },
  {
    id: 'datos',
    label: 'Datos (MariaDB / SQL)',
    icon: 'Database',
    tagline: 'MariaDB 11 · una tabla, un índice',
    simple:
      'La base de datos es el archivador donde vive la libreta. Cada récord es una fila. Un "índice" es como pegar pestañas de colores: deja encontrar al mejor jugador sin tener que leer todas las hojas una por una.',
    detalle:
      'Una sola tabla scores. El índice compuesto (score, created_at) hace que el ranking (orden por puntaje desc, desempate por antigüedad) sea rápido y determinista. La migración es la definición versionada de esa tabla.',
    snippets: [
      {
        filename: 'database/migrations/..._create_scores_table.php',
        lang: 'php',
        player: 'p2',
        note: 'El índice (score, created_at) es exactamente el orden que pide scopeRanked().',
        code: `Schema::create('scores', function (Blueprint $table) {
    $table->id();
    $table->string('player_name', 40);
    $table->unsignedInteger('score')->default(0);
    $table->string('result')->nullable();
    $table->timestamp('match_played_at')->nullable();
    $table->timestamps();

    // Ranking: mayores puntajes primero, desempate determinista por antigüedad.
    $table->index(['score', 'created_at']);
});`,
      },
      {
        filename: 'Consulta del ranking (la que genera Eloquent)',
        lang: 'sql',
        player: 'p2',
        note: 'Esto es lo que Score::ranked()->limit(10)->get() le pide a MariaDB por debajo.',
        code: `select * from scores
order by score desc, created_at asc
limit 10;`,
      },
    ],
  },
]
