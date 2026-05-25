import type { Actor } from '@/types/domain'

export const actors: Actor[] = [
  {
    id: 'jugador',
    nombre: 'Jugador',
    descripcion:
      'Persona que controla una moto de luz. En cada partida local hay dos jugadores compartiendo el teclado (P1 = WASD + E + Q; P2 = flechas + Enter + U). Conduce, dispara discos que rebotan y activa el modo moto temporal para ganar velocidad.',
    responsabilidades: [
      'Iniciar la partida desde la pantalla de bienvenida e ingresar su nombre.',
      'Conducir la moto y disparar discos que rebotan en los bordes.',
      'Activar el modo moto (5 s) para acelerar de forma temporal.',
      'Consultar el ranking Top N al terminar la partida.',
    ],
    icon: 'Gamepad2',
  },
  {
    id: 'administrador',
    nombre: 'Administrador del leaderboard',
    descripcion:
      'Operador que entra al panel Filament en /admin con credenciales propias. Gestiona el CRUD de puntajes: revisa el ranking, corrige o depura entradas inválidas y modera el historial que se muestra en la web.',
    responsabilidades: [
      'Autenticarse en el panel Filament (/admin).',
      'Listar, crear, editar y eliminar puntajes del leaderboard.',
      'Ordenar y filtrar el ranking por puntaje y fecha.',
      'Mantener la coherencia del historial mostrado en la SPA.',
    ],
    icon: 'ShieldCheck',
  },
  {
    id: 'leaderboard',
    nombre: 'Servicio de leaderboard',
    descripcion:
      'Actor «sistema»: la API REST de Laravel sobre MariaDB. Recibe los puntajes que el juego Java envía al terminar cada partida (POST /api/scores) y los sirve ordenados a la SPA Vue y al panel Filament (GET /api/scores).',
    responsabilidades: [
      'Validar y persistir cada puntaje recibido (StoreScoreRequest + throttle).',
      'Servir el ranking ordenado por puntaje descendente (scope ranked).',
      'Exponer el historial al panel Filament y a la sección Leaderboard de Vue.',
      'Degradar con elegancia: si la API no responde, el juego no se rompe.',
    ],
    icon: 'Database',
  },
]

export const actorById = (id: string): Actor | undefined =>
  actors.find((a) => a.id === id)
