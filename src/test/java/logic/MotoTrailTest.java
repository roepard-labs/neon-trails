package logic;

import events.InputController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de integración para la mecánica de rastro del modo moto: grabación, colisión,
 * invulnerabilidad, erosión y limpieza en respawn.
 * <p>
 * NOTE: Headless, sin Swing ni Thread.sleep — se manipulan los timers de
 * {@link Player#setBikeUntilNanos(long)} y {@link Player#setTrailInvulnUntilNanos(long)}.
 */
class MotoTrailTest {

    private static final int W = GameConstants.DEFAULT_WIDTH;
    private static final int H = GameConstants.DEFAULT_HEIGHT;

    /** Listener mínimo para contar emisiones de eventos relevantes al trail. */
    private static final class TrailCounter implements GameEventListener {
        int trailHitCount;
        int lastTrailHitVictim;
        int respawnCount;
        int gameOverCount;
        int lastWinner;

        @Override public void onTrailHit(int victimId) {
            trailHitCount++;
            lastTrailHitVictim = victimId;
        }
        @Override public void onRespawn() {
            respawnCount++;
        }
        @Override public void onGameOver(int winnerId) {
            gameOverCount++;
            lastWinner = winnerId;
        }
    }

    @Test
    void trailRecordsWhileOnBike() {
        GameState state = new GameState(W, H);
        InputController input = new InputController();
        input.requestP1Bike();
        state.tick(input, W, H); // activa moto
        assertTrue(state.getPlayerOne().isOnBike());

        // Avanza varios ticks moviéndose para que addTrailPoint no salte por duplicado.
        input.setP1Right(true);
        for (int i = 0; i < 5; i++) {
            state.tick(input, W, H);
        }

        assertFalse(state.getPlayerOne().getMotoTrail().isEmpty(),
                "Con la moto activa y movimiento, el rastro debe acumular puntos");
    }

    @Test
    void trailDoesNotRecordWhileOffBike() {
        GameState state = new GameState(W, H);
        InputController input = new InputController();
        input.setP1Right(true);

        for (int i = 0; i < 10; i++) {
            state.tick(input, W, H);
        }

        assertTrue(state.getPlayerOne().getMotoTrail().isEmpty(),
                "Sin moto activa, el rastro debe permanecer vacío");
    }

    @Test
    void trailErosionStartsWhenBikeExpires() {
        GameState state = new GameState(W, H);
        InputController input = new InputController();
        input.requestP1Bike();
        state.tick(input, W, H);
        input.setP1Right(true);
        for (int i = 0; i < 5; i++) {
            state.tick(input, W, H);
        }
        assertFalse(state.getPlayerOne().getMotoTrail().isEmpty(),
                "Precondición: el trail debe tener puntos antes de forzar la expiración");

        // Forzar expiración del temporizador de moto y tickear para que se detecte la transición.
        state.getPlayerOne().setBikeUntilNanos(System.nanoTime() - 1L);
        state.tick(input, W, H);

        assertFalse(state.getPlayerOne().isOnBike());
        assertTrue(state.getPlayerOne().isTrailEroding(),
                "Al expirar la moto, el rastro debe entrar en erosión");
    }

    @Test
    void trailErosionEmptiesTrailEventually() {
        GameState state = new GameState(W, H);
        InputController input = new InputController();
        input.requestP1Bike();
        state.tick(input, W, H);
        input.setP1Right(true);
        for (int i = 0; i < 5; i++) {
            state.tick(input, W, H);
        }

        state.getPlayerOne().setBikeUntilNanos(System.nanoTime() - 1L);
        input.setP1Right(false); // suelta movimiento para no enturbiar la observación
        for (int i = 0; i < 300; i++) {
            state.tick(input, W, H);
        }

        assertTrue(state.getPlayerOne().getMotoTrail().isEmpty(),
                "Tras suficientes ticks la erosión debe vaciar el trail");
        assertFalse(state.getPlayerOne().isTrailEroding(),
                "Vacío el trail, la bandera de erosión debe apagarse");
    }

    @Test
    void trailHitDamagesEnemyAndEmitsOnTrailHit() {
        TrailCounter counter = new TrailCounter();
        GameState state = new GameState(W, H, counter);
        // Posicionar jugadores lejos para evitar colisiones espurias del propio trail.
        state.getPlayerOne().setPosition(300, 300);
        state.getPlayerTwo().setPosition(600, 300);
        int initialLives = state.getPlayerOne().getLives();

        // P2 deja una pista en (300, 300), justo encima de P1.
        Player p2 = state.getPlayerTwo();
        p2.addTrailPoint(300, 300);

        InputController input = new InputController();
        state.tick(input, W, H);

        assertEquals(initialLives - 1, state.getPlayerOne().getLives(),
                "Tocar el trail enemigo debe quitar una vida a la víctima");
        assertEquals(1, counter.trailHitCount);
        assertEquals(1, counter.lastTrailHitVictim);
        assertEquals(1, counter.respawnCount,
                "Quedan vidas: el hit no letal debe llamar onRespawn");
    }

    @Test
    void trailHitDamagesOwnerOutOfGracePoints() {
        TrailCounter counter = new TrailCounter();
        GameState state = new GameState(W, H, counter);
        state.getPlayerOne().setPosition(400, 400);
        // Posicionar P2 lejos para que el chequeo de P1 vs trail-de-P2 no interfiera.
        state.getPlayerTwo().setPosition(W - 100, H - 100);
        int initialLives = state.getPlayerOne().getLives();

        // 10 puntos: los primeros 6 están sobre (400, 400) y los últimos 4 (gracia) lejos. Como
        // addTrailPoint salta duplicados con la misma posición exacta, alternamos minúsculamente.
        Player p1 = state.getPlayerOne();
        p1.addTrailPoint(400, 400);
        p1.addTrailPoint(401, 400);
        p1.addTrailPoint(400, 400);
        p1.addTrailPoint(401, 400);
        p1.addTrailPoint(400, 400);
        p1.addTrailPoint(401, 400);
        // Los últimos TRAIL_GRACE_POINTS=4 puntos quedan en una zona alejada y NO dañarán a P1.
        p1.addTrailPoint(700, 50);
        p1.addTrailPoint(701, 50);
        p1.addTrailPoint(702, 50);
        p1.addTrailPoint(703, 50);

        InputController input = new InputController();
        state.tick(input, W, H);

        assertEquals(initialLives - 1, p1.getLives(),
                "Friendly fire: los puntos antiguos del trail propio (fuera de gracia) deben dañar al dueño");
        assertEquals(1, counter.trailHitCount);
        assertEquals(1, counter.lastTrailHitVictim);
    }

    @Test
    void trailInvulnBlocksDamageWithinWindow() {
        TrailCounter counter = new TrailCounter();
        GameState state = new GameState(W, H, counter);
        state.getPlayerOne().setPosition(300, 300);
        state.getPlayerTwo().setPosition(600, 300);
        int initialLives = state.getPlayerOne().getLives();

        // P1 entra al tick con invulnerabilidad ya activa.
        state.getPlayerOne().setTrailInvuln();
        // P2 ya dejó un punto encima de P1.
        state.getPlayerTwo().addTrailPoint(300, 300);

        InputController input = new InputController();
        state.tick(input, W, H);

        assertEquals(initialLives, state.getPlayerOne().getLives(),
                "Con invulnerabilidad activa, tocar trail no debe quitar vidas");
        assertEquals(0, counter.trailHitCount,
                "Tampoco debe emitirse onTrailHit mientras la invulnerabilidad esté activa");
    }

    @Test
    void trailClearsOnRespawn() {
        GameState state = new GameState(W, H);
        InputController input = new InputController();
        input.requestP1Bike();
        state.tick(input, W, H);
        input.setP1Right(true);
        for (int i = 0; i < 4; i++) {
            state.tick(input, W, H);
        }
        assertFalse(state.getPlayerOne().getMotoTrail().isEmpty(),
                "Precondición: el trail de P1 tiene puntos");

        // Forzar respawn vía un golpe de disco: P1 dispara y reubicamos al disco sobre P2.
        input.setP1Right(false);
        input.setP1Shoot(true);
        state.tick(input, W, H);
        input.setP1Shoot(false);
        DiscProjectile disc = state.getDiscs().get(0);
        state.getPlayerOne().setPosition(100, 100);
        state.getPlayerTwo().setPosition(200, 100);
        disc.setPosition(200, 100);

        state.tick(input, W, H);

        assertTrue(state.getPlayerOne().getMotoTrail().isEmpty(),
                "Tras respawnPlayersAfterHit, el trail de P1 debe quedar vacío");
        assertTrue(state.getPlayerTwo().getMotoTrail().isEmpty(),
                "Tras respawnPlayersAfterHit, el trail de P2 debe quedar vacío");
    }
}
