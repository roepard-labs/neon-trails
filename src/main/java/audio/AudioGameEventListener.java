package audio;

import logic.GameEventListener;

/**
 * Implementación de {@link GameEventListener} que traduce cada evento de la simulación a una
 * reproducción del {@link SoundManager}.
 * <p>
 * NOTE: Esta clase es el puente entre {@code logic/} (puro) y la capa de audio. Mantiene a
 * {@code logic/} sin imports de {@link javax.sound.sampled} y permite probar la lógica con
 * un listener stub sin tocar el mixer.
 */
public class AudioGameEventListener implements GameEventListener {

    @Override
    public void onShoot(int playerId) {
        SoundManager.playRandom(Sfx.SHOOT_1, Sfx.SHOOT_2, Sfx.SHOOT_3);
    }

    @Override
    public void onBounce() {
        SoundManager.play(Sfx.BOUNCE);
    }

    @Override
    public void onDiscStopped() {
        SoundManager.play(Sfx.STOP);
    }

    @Override
    public void onPickup(int playerId) {
        SoundManager.play(Sfx.PICKUP);
    }

    @Override
    public void onHit(int victimId) {
        SoundManager.play(Sfx.HIT);
    }

    @Override
    public void onRespawn() {
        SoundManager.play(Sfx.RESPAWN);
    }

    @Override
    public void onBikeActivated(int playerId) {
        SoundManager.play(Sfx.BIKE_INIT);
        SoundManager.startBikeLoop();
    }

    @Override
    public void onBikeEnded(int playerId) {
        SoundManager.stopBikeLoop();
        SoundManager.play(Sfx.BIKE_END);
    }

    // NOTE: onGameOver no se traduce a audio aquí. La GameOverScreen toca el sting/jingle según
    // su propio ciclo de vida (onShow/onHide), para poder detenerlo al volver al menú.
}
