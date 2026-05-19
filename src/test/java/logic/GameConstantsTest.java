package logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas mínimas que fijan invariantes documentados en el PDF de cátedra
 * y constantes que otras pruebas asumen.
 */
class GameConstantsTest {

    @Test
    void bikeDurationMatchesSpec() {
        // El PDF exige modo moto de 5 s; cualquier cambio aquí debe ser deliberado.
        assertEquals(5.0, GameConstants.BIKE_DURATION_SEC, 0.0);
    }

    @Test
    void tickPeriodIsSixtyHz() {
        assertEquals(16, GameConstants.GAME_TICK_MS);
    }

    @Test
    void physicsConstantsAreSane() {
        assertTrue(GameConstants.PLAYER_SPEED > 0);
        assertTrue(GameConstants.DISC_SPEED > 0);
        assertTrue(GameConstants.DISC_MAX_TICKS > 0);
        assertTrue(GameConstants.FIRE_COOLDOWN_TICKS > 0);
        assertTrue(GameConstants.BIKE_SPEED_MULT > 1.0);
    }
}
