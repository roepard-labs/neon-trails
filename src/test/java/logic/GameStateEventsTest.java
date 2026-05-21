package logic;

import events.InputController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas headless de las emisiones de {@link GameEventListener} desde {@link GameState}.
 * <p>
 * NOTE: Estas pruebas no instancian audio.SoundManager — comprueban únicamente la lógica de
 * eventos en {@code logic/}, así que corren con {@code java.awt.headless=true} sin tocar el mixer.
 */
class GameStateEventsTest {

    /** Contador simple de eventos para verificar la cantidad y los IDs de jugador. */
    private static final class CountingListener implements GameEventListener {
        int shootCount;
        int lastShootPlayer;
        int bounceCount;
        int stoppedCount;
        int pickupCount;
        int lastPickupPlayer;
        int hitCount;
        int lastHitVictim;
        int respawnCount;
        int bikeActivatedCount;
        int lastBikeActivatedPlayer;
        int bikeEndedCount;
        int lastBikeEndedPlayer;
        int gameOverCount;
        int lastWinner;

        @Override public void onShoot(int playerId) {
            shootCount++;
            lastShootPlayer = playerId;
        }
        @Override public void onBounce() {
            bounceCount++;
        }
        @Override public void onDiscStopped() {
            stoppedCount++;
        }
        @Override public void onPickup(int playerId) {
            pickupCount++;
            lastPickupPlayer = playerId;
        }
        @Override public void onHit(int victimId) {
            hitCount++;
            lastHitVictim = victimId;
        }
        @Override public void onRespawn() {
            respawnCount++;
        }
        @Override public void onBikeActivated(int playerId) {
            bikeActivatedCount++;
            lastBikeActivatedPlayer = playerId;
        }
        @Override public void onBikeEnded(int playerId) {
            bikeEndedCount++;
            lastBikeEndedPlayer = playerId;
        }
        @Override public void onGameOver(int winnerId) {
            gameOverCount++;
            lastWinner = winnerId;
        }
    }

    private static final int W = GameConstants.DEFAULT_WIDTH;
    private static final int H = GameConstants.DEFAULT_HEIGHT;

    @Test
    void shootEmitsOnShoot() {
        CountingListener counter = new CountingListener();
        GameState state = new GameState(W, H, counter);
        InputController input = new InputController();
        input.setP1Shoot(true);

        state.tick(input, W, H);

        assertEquals(1, counter.shootCount, "Debe emitir un solo onShoot por tick con cooldown a 0");
        assertEquals(1, counter.lastShootPlayer);
        assertEquals(1, state.getDiscs().size(), "Debe existir el disco recién lanzado");
    }

    @Test
    void discBounceAgainstWallEmitsOnBounce() {
        CountingListener counter = new CountingListener();
        GameState state = new GameState(W, H, counter);
        InputController input = new InputController();
        input.setP1Shoot(true);
        state.tick(input, W, H);
        input.setP1Shoot(false);

        // Forzar al disco contra el borde derecho para que el siguiente tick produzca rebote.
        DiscProjectile d = state.getDiscs().get(0);
        d.setPosition(W - GameConstants.DISC_RADIUS, 44);

        state.tick(input, W, H);

        assertTrue(counter.bounceCount >= 1, "Debe emitir al menos un onBounce");
        assertEquals(0, counter.stoppedCount, "Aún quedan rebotes; no debe emitir onDiscStopped");
        assertFalse(d.isStuck());
    }

    @Test
    void finalBounceEmitsOnlyStoppedNotBounce() {
        CountingListener counter = new CountingListener();
        GameState state = new GameState(W, H, counter);
        InputController input = new InputController();
        input.setP1Shoot(true);
        state.tick(input, W, H);
        input.setP1Shoot(false);
        DiscProjectile d = state.getDiscs().get(0);

        // Rebote 1: borde derecho.
        d.setPosition(W - GameConstants.DISC_RADIUS, 44);
        state.tick(input, W, H);
        assertEquals(1, counter.bounceCount);

        // Rebote 2: borde izquierdo.
        d.setPosition(GameConstants.DISC_RADIUS, 44);
        state.tick(input, W, H);
        assertEquals(2, counter.bounceCount);

        // Rebote 3 (último): borde derecho otra vez → queda stuck.
        d.setPosition(W - GameConstants.DISC_RADIUS, 44);
        state.tick(input, W, H);

        assertTrue(d.isStuck(), "Tras DISC_MAX_BOUNCES el disco debe quedar quieto");
        assertEquals(2, counter.bounceCount, "El rebote final no debe contarse como onBounce");
        assertEquals(1, counter.stoppedCount, "El rebote final debe emitir onDiscStopped");
    }

    @Test
    void hitEmitsHitAndRespawn() {
        CountingListener counter = new CountingListener();
        GameState state = new GameState(W, H, counter);
        // Colocar P2 cerca para que el disco lo golpee al forzar su posición.
        state.getPlayerOne().setPosition(100, 100);
        state.getPlayerTwo().setPosition(200, 100);
        InputController input = new InputController();
        input.setP1Shoot(true);
        state.tick(input, W, H);
        input.setP1Shoot(false);

        // Forzar al disco encima de P2 — friendlyTicks solo bloquea autogol del dueño.
        DiscProjectile d = state.getDiscs().get(0);
        d.setPosition(200, 100);

        state.tick(input, W, H);

        assertEquals(1, counter.hitCount);
        assertEquals(2, counter.lastHitVictim);
        assertEquals(1, counter.respawnCount, "Quedan vidas: debe haber onRespawn");
        assertEquals(0, counter.gameOverCount);
    }

    @Test
    void lastHitEmitsGameOverWithoutRespawn() {
        CountingListener counter = new CountingListener();
        GameState state = new GameState(W, H, counter);
        // Bajar P2 a una sola vida para que el siguiente impacto sea letal.
        Player p2 = state.getPlayerTwo();
        while (p2.getLives() > 1) {
            p2.loseLife();
        }
        state.getPlayerOne().setPosition(100, 100);
        p2.setPosition(200, 100);

        InputController input = new InputController();
        input.setP1Shoot(true);
        state.tick(input, W, H);
        input.setP1Shoot(false);
        DiscProjectile d = state.getDiscs().get(0);
        d.setPosition(200, 100);

        state.tick(input, W, H);

        assertEquals(1, counter.hitCount);
        assertEquals(0, counter.respawnCount, "Sin vidas restantes no debe respawn");
        assertEquals(1, counter.gameOverCount);
        assertEquals(1, counter.lastWinner);
        assertTrue(state.isFinished());
    }

    @Test
    void ownerPickingStuckDiscEmitsOnPickup() {
        CountingListener counter = new CountingListener();
        GameState state = new GameState(W, H, counter);
        InputController input = new InputController();
        input.setP1Shoot(true);
        state.tick(input, W, H);
        input.setP1Shoot(false);
        DiscProjectile d = state.getDiscs().get(0);

        // Tres rebotes contra paredes alternadas → stuck.
        d.setPosition(W - GameConstants.DISC_RADIUS, 44);
        state.tick(input, W, H);
        d.setPosition(GameConstants.DISC_RADIUS, 44);
        state.tick(input, W, H);
        d.setPosition(W - GameConstants.DISC_RADIUS, 44);
        state.tick(input, W, H);
        assertTrue(d.isStuck());

        // Mover P1 encima del disco quieto. El movimiento se clampa a [half, W-half] dentro de tick;
        // por eso usamos coordenadas válidas (945 ≈ W - half = 949).
        int half = GameConstants.PLAYER_SIZE / 2;
        double pickupX = W - half;
        double pickupY = 44;
        state.getPlayerOne().setPosition(pickupX, pickupY);
        d.setPosition(pickupX, pickupY);

        state.tick(input, W, H);

        assertEquals(1, counter.pickupCount);
        assertEquals(1, counter.lastPickupPlayer);
        assertEquals(0, counter.hitCount, "Un pickup no debe contarse como hit");
    }

    @Test
    void bikeActivationAndExpirationEmitEvents() {
        CountingListener counter = new CountingListener();
        GameState state = new GameState(W, H, counter);
        InputController input = new InputController();
        input.requestP1Bike();

        state.tick(input, W, H);

        assertEquals(1, counter.bikeActivatedCount);
        assertEquals(1, counter.lastBikeActivatedPlayer);
        assertTrue(state.getPlayerOne().isOnBike());

        // Forzar expiración del temporizador y emitir onBikeEnded en el siguiente tick.
        state.getPlayerOne().setBikeUntilNanos(System.nanoTime() - 1L);

        state.tick(input, W, H);

        assertFalse(state.getPlayerOne().isOnBike());
        assertEquals(1, counter.bikeEndedCount);
        assertEquals(1, counter.lastBikeEndedPlayer);
    }

    @Test
    void noopListenerConstructorStillWorks() {
        // Constructor compat sin listener: emisiones simplemente se descartan.
        GameState state = new GameState(W, H);
        InputController input = new InputController();
        input.setP1Shoot(true);

        state.tick(input, W, H);

        assertEquals(1, state.getDiscs().size());
    }
}
