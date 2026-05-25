import type { RequirementGroup } from '@/types/domain'

export const requirementGroups: RequirementGroup[] = [
  {
    categoria: 'negocio',
    titulo: 'Requerimientos de negocio',
    subtitulo: '¿Por qué existe Neon Trails?',
    items: [
      {
        codigo: 'RN-01',
        nombre: 'Cumplir la rúbrica de POO',
        descripcion:
          'Evidenciar los cuatro pilares de la POO, organización en paquetes, hilos, recursividad, ordenamiento y control de tiempo dentro de un juego funcional.',
      },
      {
        codigo: 'RN-02',
        nombre: 'Juego jugable y sustentable',
        descripcion:
          'Entregar un juego que compila y corre en el equipo de cada integrante, con código documentado y comprensible para la sustentación individual.',
      },
      {
        codigo: 'RN-03',
        nombre: 'Ranking persistente',
        descripcion:
          'Guardar el historial de partidas y exponer un Top N consultable, de modo que las puntuaciones sobrevivan al cierre del juego.',
      },
      {
        codigo: 'RN-04',
        nombre: 'Presentación en la web',
        descripcion:
          'Publicar una landing de presentación y el juego jugable en el navegador, sin obligar al evaluador a instalar Java.',
      },
      {
        codigo: 'RN-05',
        nombre: 'Despliegue reproducible',
        descripcion:
          'Operar bajo un esquema de despliegue reproducible y documentado (Docker monolítico + Dokploy) con HTTPS.',
      },
    ],
  },
  {
    categoria: 'usuario',
    titulo: 'Requerimientos del usuario',
    subtitulo: '¿Qué debe poder hacer cada actor?',
    items: [
      {
        codigo: 'RU-01',
        nombre: 'Jugador — iniciar partida',
        descripcion:
          'El jugador quiere iniciar una partida local de dos jugadores e ingresar su nombre antes de competir.',
      },
      {
        codigo: 'RU-02',
        nombre: 'Jugador — conducir y disparar',
        descripcion:
          'El jugador quiere mover su moto de luz y disparar discos que rebotan en los bordes para impactar al rival.',
      },
      {
        codigo: 'RU-03',
        nombre: 'Jugador — activar modo moto',
        descripcion:
          'El jugador quiere activar un modo moto temporal (5 s) que aumenta su velocidad para escapar o perseguir.',
      },
      {
        codigo: 'RU-04',
        nombre: 'Jugador — ver puntaje, vidas y tiempo',
        descripcion:
          'El jugador quiere ver en el HUD su puntaje, las vidas restantes y el tiempo de partida mientras juega.',
      },
      {
        codigo: 'RU-05',
        nombre: 'Jugador — consultar ranking',
        descripcion:
          'Al terminar, el jugador quiere ver su puntaje final y el ranking Top N tanto en el juego como en la web.',
      },
      {
        codigo: 'RU-06',
        nombre: 'Administrador — gestionar puntajes',
        descripcion:
          'El administrador quiere revisar, editar y depurar los puntajes del leaderboard desde el panel Filament.',
      },
    ],
  },
  {
    categoria: 'sistema',
    titulo: 'Requerimientos del sistema',
    subtitulo: '¿Cómo lo cumple técnicamente?',
    items: [
      {
        codigo: 'RS-01',
        nombre: 'Organización en paquetes',
        descripcion:
          'Separación estricta logic/ (simulación, sin Swing), view/ (render y ventanas) y events/ (entrada por Key Bindings).',
      },
      {
        codigo: 'RS-02',
        nombre: 'Bucle de juego en hilo dedicado',
        descripcion:
          'Un Thread neon-trails-game-loop conduce GameState.tick() a ~60 Hz (GAME_TICK_MS = 16) y solicita repaint al EDT.',
      },
      {
        codigo: 'RS-03',
        nombre: 'Entrada con Swing Key Bindings',
        descripcion:
          'InputMap + ActionMap con WHEN_IN_FOCUSED_WINDOW para dos jugadores; nada de KeyListener para evitar pérdida de foco.',
      },
      {
        codigo: 'RS-04',
        nombre: 'API REST sobre MariaDB',
        descripcion:
          'Laravel 13 expone GET/POST /api/scores sobre MariaDB 11; el modelo Score ordena con scopeRanked() por puntaje descendente.',
      },
      {
        codigo: 'RS-05',
        nombre: 'Panel Filament',
        descripcion:
          'Filament v5 publica el CRUD del leaderboard en /admin con acceso por credenciales y ordenamiento por score.',
      },
      {
        codigo: 'RS-06',
        nombre: 'Juego en el navegador',
        descripcion:
          'El juego Swing corre dentro del contenedor con TigerVNC + noVNC y se accede por /game/ a través del gateway nginx.',
      },
    ],
  },
  {
    categoria: 'funcional',
    titulo: 'Requerimientos funcionales (RF)',
    subtitulo: 'Funciones del sistema modeladas por casos de uso',
    items: [
      {
        codigo: 'RF-01',
        nombre: 'Movimiento de dos jugadores',
        descripcion:
          'Movimiento simultáneo de P1 (WASD) y P2 (flechas) resuelto cada tick en GameState.tick → GamePanel.',
      },
      {
        codigo: 'RF-02',
        nombre: 'Disco con rebote y colisión',
        descripcion:
          'DiscProjectile rebota en los bordes, expira en el borde o tras DISC_MAX_TICKS y respeta friendlyTicks para no golpear a su dueño al instante.',
      },
      {
        codigo: 'RF-03',
        nombre: 'Modo moto temporal',
        descripcion:
          'Modo moto de 5 s (BIKE_DURATION_SEC) con velocidad ×1.75 por jugador, solicitado por evento de borde (request/consume).',
      },
      {
        codigo: 'RF-04',
        nombre: 'Vidas, colisión y puntaje',
        descripcion:
          'Sistema de vidas, resolución de impactos, respawn a esquinas opuestas y +1 punto al rival por acierto.',
      },
      {
        codigo: 'RF-05',
        nombre: 'Enviar puntaje al leaderboard',
        descripcion:
          'Al terminar la partida, LeaderboardClient hace POST /api/scores de forma asíncrona y tolerante a fallos.',
      },
      {
        codigo: 'RF-06',
        nombre: 'Consultar el ranking',
        descripcion:
          'GET /api/scores devuelve el Top N ordenado por puntaje; lo consumen la sección Leaderboard de Vue y el panel Filament.',
      },
    ],
  },
  {
    categoria: 'no-funcional',
    titulo: 'Requerimientos no funcionales (RNF)',
    subtitulo: 'Restricciones de calidad, seguridad y operación',
    items: [
      {
        codigo: 'RNF-01',
        nombre: 'Rendimiento en tiempo real',
        descripcion:
          'Animación estable a ~60 fps; la simulación corre fuera del EDT y solo el repaint toca Swing.',
      },
      {
        codigo: 'RNF-02',
        nombre: 'Política estricta de dependencias',
        descripcion:
          'Java SE + librerías justificadas; el envío de puntajes usa java.net.http.HttpClient nativo, sin dependencias nuevas.',
      },
      {
        codigo: 'RNF-03',
        nombre: 'Tolerancia a fallos del leaderboard',
        descripcion:
          'Si la API no responde, el envío falla en silencio (try/catch + log) y el juego sigue siendo jugable offline.',
      },
      {
        codigo: 'RNF-04',
        nombre: 'Jugabilidad en navegador',
        descripcion:
          'El juego se puede jugar desde el navegador vía noVNC sin instalar Java en el equipo del evaluador.',
      },
      {
        codigo: 'RNF-05',
        nombre: 'Seguridad de transporte y abuso',
        descripcion:
          'HTTPS en producción (Traefik + Let’s Encrypt) y rate-limit throttle:30,1 sobre POST /api/scores.',
      },
      {
        codigo: 'RNF-06',
        nombre: 'Idioma',
        descripcion:
          'Interfaz, comentarios y JavaDoc en español, según la convención del proyecto.',
      },
    ],
  },
]
