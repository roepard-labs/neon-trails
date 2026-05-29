package audio;

import logic.GameEventListener;

/**
 * Puente entre la lógica pura ({@code logic/}) y la capa de audio: traduce cada evento de la
 * simulación a una reproducción del {@link SoundManager}.
 * <p>
 * Mantiene a {@code logic/} sin imports de {@link javax.sound.sampled} y permite probar la
 * simulación con un listener stub sin tocar el mixer.
 * <p>
 * NOTE: [sustentación POO: Polimorfismo] Patrón Observer en estado puro: {@link logic.GameState}
 * invoca métodos de {@link GameEventListener} y, por enlace dinámico, aquí resuelven a la
 * implementación de audio sin que la simulación conozca esta clase. Un test puede sustituir el
 * listener por un stub no-op y la lógica del juego sigue idéntica.
 */
public class AudioGameEventListener implements GameEventListener {

    /**
     * Reproduce una variante aleatoria de {@code shoot_1/2/3.wav} para evitar fatiga auditiva
     * cuando los disparos son frecuentes.
     * <p>
     * NOTE: [sustentación POO: Polimorfismo] Override que reemplaza el {@code default} no-op de
     * {@link GameEventListener#onShoot(int)} por una reproducción real.
     */
    @Override
    public void onShoot(int playerId) {
        SoundManager.playRandom(Sfx.SHOOT_1, Sfx.SHOOT_2, Sfx.SHOOT_3);
    }

    /** Reproduce {@code bounce.wav} cuando un disco choca con un borde. */
    @Override
    public void onBounce() {
        SoundManager.play(Sfx.BOUNCE);
    }

    /** Reproduce {@code stop.wav} cuando un disco agota sus rebotes y queda quieto. */
    @Override
    public void onDiscStopped() {
        SoundManager.play(Sfx.STOP);
    }

    /** Reproduce {@code pickup.wav} cuando un jugador recoge su propio disco quieto. */
    @Override
    public void onPickup(int playerId) {
        SoundManager.play(Sfx.PICKUP);
    }

    /** Reproduce {@code hit.wav} cuando un jugador recibe daño (disco enemigo o rastro hostil). */
    @Override
    public void onHit(int victimId) {
        SoundManager.play(Sfx.HIT);
    }

    /** Reproduce {@code respawn.wav} cuando ambos jugadores reaparecen tras un impacto. */
    @Override
    public void onRespawn() {
        SoundManager.play(Sfx.RESPAWN);
    }

    /**
     * Reproduce {@code bike_init.wav} y arranca el loop {@code bike.wav} mientras dure el modo
     * moto. El loop es idempotente: si ya estaba sonando (segundo jugador activa moto), no se
     * duplica.
     * <p>
     * NOTE: [sustentación POO: Polimorfismo] Override que reemplaza el {@code default} no-op de
     * {@link GameEventListener#onBikeActivated(int)} y dispara el loop de audio del modo moto.
     */
    @Override
    public void onBikeActivated(int playerId) {
        SoundManager.play(Sfx.BIKE_INIT);
        SoundManager.startBikeLoop();
    }

    /**
     * Detiene el loop {@code bike.wav} y reproduce {@code bike_end.wav} cuando el modo moto
     * expira.
     */
    @Override
    public void onBikeEnded(int playerId) {
        SoundManager.stopBikeLoop();
        SoundManager.play(Sfx.BIKE_END);
    }

    // NOTE: onGameOver no se traduce a audio aquí. La GameOverScreen toca el sting/jingle según
    // su propio ciclo de vida (onShow/onHide), para poder detenerlo al volver al menú.
}
