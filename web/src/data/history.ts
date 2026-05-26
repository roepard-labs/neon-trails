/**
 * Datos de la sección "Historia & ADN".
 *
 * Por qué existe Neon Trails y en qué nos basamos: la inspiración (Tron), el
 * mandato académico (demostrar POO), el ADN de diseño (neón) y cómo creció el
 * proyecto. Pensado para primer semestre: nada se da por sabido.
 */

export interface TronElemento {
  titulo: string
  descripcion: string
}

export interface PooPilar {
  pilar: string
  queEs: string
  dondeSeVe: string
}

export interface TimelineStep {
  titulo: string
  detalle?: string
}

// ── De dónde viene ───────────────────────────────────────────────────────────
export const origenSimple =
  'Neon Trails nace de las "motos de luz" de la película Tron: dos motos corren dejando un muro de luz detrás; si chocas contra un muro, pierdes. Nosotros tomamos esa idea y le sumamos un disco que rebota, como un frisbee de neón.'

export const origenDetalle =
  'No copiamos un juego existente: reconstruimos la mecánica desde cero en Java para que cada pieza (mover, disparar, chocar) fuera nuestra y se pudiera explicar y probar. La estética —cian, magenta y negro— viene directamente de esa imagen de arena digital.'

export const tronElementos: TronElemento[] = [
  { titulo: 'Moto de luz', descripcion: 'Cada jugador es una moto. Al activar el modo moto (5 s) deja una estela sólida.' },
  { titulo: 'Estela', descripcion: 'El muro de luz que queda detrás. Chocar contra una estela (propia o ajena) cuesta una vida.' },
  { titulo: 'Disco', descripcion: 'Un proyectil que se lanza y rebota en los bordes; si te toca, anota el rival.' },
  { titulo: 'Arena', descripcion: 'El campo cerrado donde todo ocurre, con su rejilla de neón.' },
]

// ── El reto académico (el ADN) ───────────────────────────────────────────────
export const retoSimple =
  'Esto es un proyecto de la materia Técnicas de Programación. La meta real no es "hacer un juego bonito", sino demostrar que entendimos la Programación Orientada a Objetos (POO): organizar el código en piezas con responsabilidades claras.'

export const retoDetalle =
  'El PDF de cátedra (docs/rules/) pide demostrar varias cosas a la vez: los 4 pilares de la POO, organización en paquetes, uso de hilos, multimedia (imágenes/sonido) y persistencia (guardar datos). Neon Trails cumple todo eso: el juego usa los pilares y los hilos, los sprites/sonidos son la multimedia, y el leaderboard web es la persistencia.'

export const pooPilares: PooPilar[] = [
  {
    pilar: 'Abstracción',
    queEs: 'Modelar el mundo con clases que dicen QUÉ hacen, no cómo.',
    dondeSeVe: 'GameState, Player, DiscProjectile (logic/)',
  },
  {
    pilar: 'Encapsulamiento',
    queEs: 'Los datos están protegidos; se tocan solo por métodos (getters).',
    dondeSeVe: 'Player: campos privados + getX() / getFireCooldownTicks()',
  },
  {
    pilar: 'Herencia',
    queEs: 'Una clase reutiliza y especializa a otra (es-un).',
    dondeSeVe: 'WelcomeScreen, GameScreen… extends BaseScreen',
  },
  {
    pilar: 'Polimorfismo',
    queEs: 'Una misma interfaz, varias implementaciones intercambiables.',
    dondeSeVe: 'AudioGameEventListener implements GameEventListener',
  },
]

// ── ADN de diseño ─────────────────────────────────────────────────────────────
export const adnDiseno =
  'El look neón no es casual: cian #00ffff para el Jugador 1 y magenta #ff3399 para el Jugador 2 nacen del propio código del juego (logic/GameState.java). Los sprites animados de esta presentación vienen de un handoff de diseño y se documentan en docs/multimedia-libraries.md.'

// ── Cómo creció el proyecto ──────────────────────────────────────────────────
export const evolucion: TimelineStep[] = [
  {
    titulo: '1) El juego (Java Swing)',
    detalle: 'Primero, la arena local de dos jugadores: lógica pura en logic/, dibujo en view/, teclado en events/.',
  },
  {
    titulo: '2) El leaderboard web (Laravel + MariaDB)',
    detalle: 'Para que los puntajes no se pierdan al cerrar, el juego los envía a una API que los guarda y un ranking que cualquiera puede ver.',
  },
  {
    titulo: '3) Jugable en el navegador (Docker + noVNC)',
    detalle: 'Para mostrarlo sin instalar Java en cada equipo, todo se empaqueta en una imagen Docker y el juego se transmite a una pestaña del navegador.',
  },
]
