import type { UseCase } from '@/types/domain'

export const useCases: UseCase[] = [
  {
    codigo: 'CU-PART-01',
    nombre: 'Iniciar partida',
    actores: ['jugador'],
    tipo: 'principal',
    precondiciones: [
      'El juego está abierto en la pantalla de bienvenida.',
      'Ambos jugadores comparten el teclado (P1 = WASD, P2 = flechas).',
    ],
    descripcion:
      'Dos jugadores inician una partida local. Tras ingresar sus nombres, GameState posiciona ambas motos en esquinas opuestas y el bucle de juego empieza a correr a ~60 Hz.',
    flujoNormal: [
      'El jugador pulsa “Jugar” en la pantalla de bienvenida.',
      'La pantalla de nombres captura el nombre de P1 y P2.',
      'GameScreen llama a resetGame() y arranca el GameLoop.',
      'GameState ubica a los jugadores y comienza la simulación.',
    ],
    flujosAlternos: [
      {
        titulo: 'FA-1. Reanudar tras una partida previa',
        pasos: [
          'Desde Game Over el jugador elige “Jugar de nuevo”.',
          'Se reinicia el estado sin volver al menú principal.',
        ],
      },
    ],
    excepciones: [
      {
        titulo: 'EX-01. Nombre vacío',
        descripcion:
          'Si un jugador no ingresa nombre, se usa un valor por defecto (P1 / P2) para no bloquear el inicio.',
      },
    ],
    postcondiciones: [
      'La partida está activa con dos jugadores en juego.',
      'El HUD muestra puntaje, vidas y tiempo desde el primer tick.',
    ],
  },
  {
    codigo: 'CU-MOV-02',
    nombre: 'Conducir la moto de luz',
    actores: ['jugador'],
    tipo: 'principal',
    precondiciones: [
      'La partida está activa.',
      'El controlador de entrada está instalado (Key Bindings con foco en la ventana).',
    ],
    descripcion:
      'El jugador conduce su moto en las cuatro direcciones. La entrada se toma como un snapshot por tick desde InputController y GameState actualiza la posición; cada movimiento incluye la detección de colisiones.',
    flujoNormal: [
      'El jugador mantiene presionada una tecla de dirección (WASD o flechas).',
      'InputController registra el estado de la tecla (press/release).',
      'GameState.tick lee el snapshot y mueve la moto.',
      'Se detectan colisiones con bordes, rivales y discos.',
    ],
    flujosAlternos: [
      {
        titulo: 'FA-1. Cambio brusco de dirección',
        pasos: [
          'El jugador invierte el sentido (ver BikeReversalTest).',
          'GameState aplica la regla de reversa sin permitir solaparse sobre sí mismo.',
        ],
      },
    ],
    excepciones: [
      {
        titulo: 'EX-01. Pérdida de foco',
        descripcion:
          'Con Key Bindings WHEN_IN_FOCUSED_WINDOW la entrada no se pierde aunque cambie el subcomponente enfocado.',
      },
    ],
    postcondiciones: [
      'La posición de la moto queda actualizada para el render.',
      'Las colisiones detectadas se resuelven en la fase de impacto del tick.',
    ],
  },
  {
    codigo: 'CU-DISC-03',
    nombre: 'Disparar y rebotar el disco',
    actores: ['jugador'],
    tipo: 'principal',
    precondiciones: [
      'La partida está activa y el cooldown de disparo está disponible.',
    ],
    descripcion:
      'El jugador dispara un disco (E para P1, Enter para P2). El disco viaja, rebota en los bordes y puede impactar al rival; un disco recién disparado tiene friendlyTicks para no golpear a su propio dueño.',
    flujoNormal: [
      'El jugador pulsa su tecla de disparo.',
      'GameState crea un DiscProjectile si el cooldown lo permite.',
      'El disco avanza y rebota en los bordes cada tick.',
      'Si impacta al rival, se resuelve el acierto y se otorga el punto.',
    ],
    flujosAlternos: [
      {
        titulo: 'FA-1. Disco expirado',
        pasos: [
          'El disco alcanza el borde de salida o supera DISC_MAX_TICKS.',
          'Se elimina sin otorgar puntaje.',
        ],
      },
    ],
    excepciones: [
      {
        titulo: 'EX-01. Disparo en cooldown',
        descripcion:
          'Si el cooldown no se ha cumplido, la pulsación se ignora y no se crea disco.',
      },
    ],
    postcondiciones: [
      'El disco queda registrado en la simulación o ya fue resuelto.',
      'En caso de impacto, se limpian los discos y se respawnean los jugadores.',
    ],
  },
  {
    codigo: 'CU-MOTO-04',
    nombre: 'Activar el modo moto (5 s)',
    actores: ['jugador'],
    tipo: 'principal',
    precondiciones: [
      'La partida está activa.',
      'El jugador no está ya en modo moto.',
    ],
    descripcion:
      'El jugador activa el modo moto (Q para P1, U para P2): durante BIKE_DURATION_SEC = 5 s la velocidad sube ×1.75 y la moto se resalta. Se solicita por evento de borde (request/consume) que se consume una vez por tick.',
    flujoNormal: [
      'El jugador pulsa su tecla de moto.',
      'InputController marca la solicitud (requestPxBike).',
      'GameState.tick consume la solicitud y activa el modo por 5 s.',
      'Al expirar el temporizador, la velocidad vuelve a la normal.',
    ],
    flujosAlternos: [
      {
        titulo: 'FA-1. Pulsación durante el modo activo',
        pasos: [
          'Si el modo ya está activo, la solicitud se ignora.',
          'No se reinicia ni se acumula la duración.',
        ],
      },
    ],
    excepciones: [],
    postcondiciones: [
      'El jugador queda en modo moto durante 5 s.',
      'El HUD refleja el estado de moto activo.',
    ],
  },
  {
    codigo: 'CU-SCORE-05',
    nombre: 'Registrar el puntaje en el leaderboard',
    actores: ['jugador', 'leaderboard'],
    tipo: 'secundario',
    precondiciones: [
      'La partida terminó y existe un resultado por jugador.',
      'LEADERBOARD_API_URL está configurado (si está vacío, se omite el envío).',
    ],
    descripcion:
      'Al terminar la partida, el juego envía el puntaje de cada jugador al leaderboard. LeaderboardClient hace POST /api/scores de forma asíncrona y tolerante a fallos, sin bloquear el EDT ni romper el juego si la API no responde.',
    flujoNormal: [
      'GameOverScreen captura nombres y puntajes desde GameSession.',
      'LeaderboardClient construye el JSON y hace POST /api/scores (async).',
      'Laravel valida con StoreScoreRequest y persiste el Score en MariaDB.',
      'La API responde 201 Created.',
    ],
    flujosAlternos: [
      {
        titulo: 'FA-1. Modo offline',
        pasos: [
          'Si la URL de la API está vacía, el envío se omite en silencio.',
          'El juego sigue mostrando el resultado local sin error.',
        ],
      },
    ],
    excepciones: [
      {
        titulo: 'EX-01. API no disponible',
        descripcion:
          'Si el POST falla, LeaderboardClient registra el error en log y no propaga la excepción al juego.',
      },
      {
        titulo: 'EX-02. Validación 422',
        descripcion:
          'Si el payload no cumple las reglas (p. ej. nombre > 40 caracteres), la API responde 422 y no persiste.',
      },
    ],
    postcondiciones: [
      'El puntaje queda persistido en MariaDB (o el envío se omitió sin afectar al juego).',
      'El nuevo registro queda disponible para el ranking y para Filament.',
    ],
  },
  {
    codigo: 'CU-RANK-06',
    nombre: 'Consultar el ranking',
    actores: ['jugador', 'leaderboard'],
    tipo: 'secundario',
    precondiciones: [
      'Existe al menos un puntaje registrado.',
    ],
    descripcion:
      'Un visitante consulta el Top N del ranking desde la sección Leaderboard de la SPA Vue. El backend devuelve los puntajes ordenados por score descendente mediante el scope ranked() del modelo Score.',
    flujoNormal: [
      'El visitante abre /leaderboard en la SPA.',
      'El composable useLeaderboard hace GET /api/scores?limit=n.',
      'ScoreController responde con ScoreResource::collection ordenada.',
      'La tabla muestra el ranking Top N.',
    ],
    flujosAlternos: [
      {
        titulo: 'FA-1. Ranking vacío',
        pasos: [
          'Si no hay puntajes, la API devuelve una colección vacía.',
          'La SPA muestra un estado “Aún no hay puntajes”.',
        ],
      },
    ],
    excepciones: [
      {
        titulo: 'EX-01. API inaccesible',
        descripcion:
          'Si el fetch falla, la SPA muestra un mensaje de error y permite reintentar.',
      },
    ],
    postcondiciones: [
      'El visitante ve el ranking ordenado por puntaje.',
      'El límite solicitado se acota entre 1 y 100 en el servidor.',
    ],
  },
  {
    codigo: 'CU-ADMIN-07',
    nombre: 'Administrar puntajes (Filament)',
    actores: ['administrador', 'leaderboard'],
    tipo: 'secundario',
    precondiciones: [
      'El administrador tiene credenciales válidas para el panel.',
    ],
    descripcion:
      'El administrador entra a /admin (Filament) y opera el CRUD del leaderboard: listar ordenado por score, crear, editar y eliminar puntajes para moderar el historial mostrado en la web.',
    flujoNormal: [
      'El administrador inicia sesión en /admin.',
      'Abre el recurso Leaderboard (ScoreResource).',
      'Lista, filtra y ordena los puntajes por score descendente.',
      'Crea, edita o elimina registros según necesite.',
    ],
    flujosAlternos: [
      {
        titulo: 'FA-1. Depurar entrada inválida',
        pasos: [
          'El administrador localiza un puntaje anómalo.',
          'Lo edita o elimina; el cambio se refleja en GET /api/scores.',
        ],
      },
    ],
    excepciones: [
      {
        titulo: 'EX-01. Acceso no autorizado',
        descripcion:
          'canAccessPanel controla el ingreso; sin permiso, Filament niega el acceso al panel.',
      },
    ],
    postcondiciones: [
      'El historial del leaderboard queda actualizado y coherente con la API.',
    ],
  },
  {
    codigo: 'CU-CICLO-08',
    nombre: 'Jugar de nuevo / volver al menú',
    actores: ['jugador'],
    tipo: 'secundario',
    precondiciones: [
      'La partida terminó y se muestra la pantalla de Game Over.',
    ],
    descripcion:
      'El jugador decide repetir la partida o volver al menú sin cerrar la aplicación, cumpliendo el ciclo de juego de “jugar múltiples veces”.',
    flujoNormal: [
      'Game Over muestra el resultado y dos acciones.',
      '“Jugar de nuevo” reinicia el estado y arranca otra partida.',
      '“Volver al menú” regresa a la pantalla de bienvenida.',
    ],
    flujosAlternos: [
      {
        titulo: 'FA-1. Cierre de la aplicación',
        pasos: [
          'El jugador cierra la ventana del juego.',
          'El GameLoop se detiene de forma ordenada (stopLoop).',
        ],
      },
    ],
    excepciones: [],
    postcondiciones: [
      'El juego queda listo para una nueva partida o en el menú principal.',
    ],
  },
]

export const useCaseByCodigo = (codigo: string): UseCase | undefined =>
  useCases.find((uc) => uc.codigo === codigo)
