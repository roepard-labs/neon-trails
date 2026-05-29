package logic;

/**
 * Observador de eventos discretos emitidos por {@link GameState} durante la simulación.
 * <p>
 * NOTE: Interface pura, sin imports externos: permite que la capa de audio (paquete
 * {@code audio/}) o cualquier futura capa (partículas, vibración) reaccione sin que {@code logic/}
 * deje de ser independiente de Swing / AudioSystem. Todos los métodos tienen implementación
 * {@code default} vacía para que las pruebas headless puedan usar un listener mínimo o
 * {@code new GameEventListener(){}} como no-op.
 * <p>
 * NOTE: [sustentación POO: Abstracción] Contrato puro sin estado ni dependencias de
 * Swing/AudioSystem; los métodos {@code default} vacíos permiten composición y no-op en pruebas
 * headless. Quien lo implementa decide qué eventos atender sin ver detalles de la simulación.
 */
public interface GameEventListener {

    /** Un jugador disparó un disco. */
    default void onShoot(int playerId) {
    }

    /** Un disco rebotó contra un borde sin quedar quieto. */
    default void onBounce() {
    }

    /** Un disco agotó sus rebotes y quedó quieto contra una pared. */
    default void onDiscStopped() {
    }

    /** El dueño tocó su propio disco quieto y lo recuperó. */
    default void onPickup(int playerId) {
    }

    /** Un disco enemigo golpeó a un jugador (no se incluye el caso friendly-fire bloqueado). */
    default void onHit(int victimId) {
    }

    /** Un jugador recibió daño por tocar un rastro de moto (propio fuera de la gracia o enemigo). */
    default void onTrailHit(int victimId) {
    }

    /** Tras un golpe no letal: jugadores reposicionados en esquinas opuestas. */
    default void onRespawn() {
    }

    /** Modo moto activado para un jugador. */
    default void onBikeActivated(int playerId) {
    }

    /** Modo moto terminado (expiró el temporizador) para un jugador. */
    default void onBikeEnded(int playerId) {
    }

    /** Partida finalizada porque un jugador llegó a 0 vidas. */
    default void onGameOver(int winnerId) {
    }
}
