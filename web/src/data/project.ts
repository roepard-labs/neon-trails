import type { ProjectInfo } from '@/types/domain'

export const projectInfo: ProjectInfo = {
  titulo: 'Neon Trails',
  subtitulo:
    'Arena local para dos jugadores inspirada en Tron: motos de luz, discos que rebotan y un modo moto temporal. Hecha en Java Swing puro, con un leaderboard persistente en Laravel + MariaDB administrado desde Filament y jugable en el navegador vía noVNC.',
  asignatura: 'Técnicas de Programación',
  carrera: 'Ingeniería de Sistemas',
  universidad: 'Universidad Autónoma de Manizales',
  ciudad: 'Manizales',
  ano: 2026,
  integrantes: [
    { nombre: 'Juan Esteban Manrique Giraldo', identificacion: '1054865411' },
    { nombre: 'Jacobo Lopez Patiño', identificacion: '1002653890' },
  ],
  docente: 'Leonardo Montes',
}

export const problemaContexto = {
  resumen:
    'El proyecto debe demostrar el dominio de la Programación Orientada a Objetos en un juego funcional: cuatro pilares de la POO, organización en paquetes, hilos, multimedia y persistencia. Neon Trails resuelve ese reto con una arena local de dos jugadores tipo Tron, separando la simulación pura de la vista y la entrada, y extendiéndola con un leaderboard web que persiste cada partida.',
  contexto:
    'La lógica vive en logic/ sin dependencias de Swing; la vista (view/) dibuja sobre el EDT mientras un hilo dedicado conduce el bucle de juego a ~60 Hz; la entrada (events/) usa Swing Key Bindings para dos jugadores sin pérdida de foco. Sobre esa base, un backend Laravel 13 + MariaDB 11 expone el ranking por API REST y lo administra con Filament, y el juego se sirve jugable en el navegador mediante TigerVNC + noVNC.',
  actualmente: [
    'Un juego de arena en tiempo real mezcla con facilidad la simulación con el dibujado, volviéndolo difícil de mantener y de sustentar.',
    'Sin un hilo dedicado, el bucle de juego compite con el EDT de Swing y la animación se entrecorta.',
    'Los KeyListener pierden el foco entre subcomponentes y un jugador deja de responder.',
    'Las puntuaciones viven solo en memoria: al cerrar el juego se pierde el historial.',
  ],
  problemas: [
    'Acoplamiento entre simulación, render y entrada que rompe los pilares de la POO.',
    'Animación irregular y entrada poco fiable para dos jugadores en un mismo teclado.',
    'Ausencia de persistencia y de un ranking consultable fuera del juego.',
    'No se puede mostrar el juego en una presentación web sin instalar Java en cada equipo.',
  ],
  seRequiere: [
    'Separar la lógica pura (logic/) de la vista (view/) y la entrada (events/) según la guía del curso.',
    'Conducir el bucle a ~60 Hz en un Thread dedicado y dibujar vía repaint sobre el EDT.',
    'Usar Swing Key Bindings (InputMap/ActionMap) para dos jugadores sin pérdida de foco.',
    'Persistir cada partida en un leaderboard (Laravel + MariaDB), administrable con Filament y publicado en la web.',
  ],
}
