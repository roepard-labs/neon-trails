package logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del cronómetro de partida en {@link GameSession}. Verifican que el reloj arranca y
 * sella correctamente, y que sin {@code startMatchClock} la cuenta es 0.
 */
class GameSessionTest {

    @Test
    void sinStartElapsedEs0() {
        GameSession s = new GameSession();
        assertEquals(0L, s.getElapsedMillisLive());
        assertEquals(0L, s.getMatchDurationMillis());
    }

    @Test
    void startMatchClockHaceElapsedCrecer() throws InterruptedException {
        GameSession s = new GameSession();
        s.startMatchClock();
        Thread.sleep(15);
        long elapsed = s.getElapsedMillisLive();
        assertTrue(elapsed > 0L, "el reloj vivo debe avanzar tras startMatchClock");
    }

    @Test
    void stopMatchClockSellaLaDuracion() throws InterruptedException {
        GameSession s = new GameSession();
        s.startMatchClock();
        Thread.sleep(20);
        s.stopMatchClock();
        long dur = s.getMatchDurationMillis();
        assertTrue(dur >= 20L, "la duración sellada debe reflejar al menos los millis dormidos");
    }

    @Test
    void resetCronoEntreDosStarts() throws InterruptedException {
        GameSession s = new GameSession();
        s.startMatchClock();
        Thread.sleep(15);
        s.stopMatchClock();
        long primera = s.getMatchDurationMillis();
        assertTrue(primera > 0L);

        s.startMatchClock();
        // matchDurationMillis vuelve a 0 al re-arrancar; el sello previo se pierde, que es lo esperado.
        assertEquals(0L, s.getMatchDurationMillis(),
                "startMatchClock debe limpiar la duración sellada de la partida previa");
    }

    @Test
    void nombresPorDefectoSiNadaSeCaptura() {
        GameSession s = new GameSession();
        assertEquals("P1", s.getPlayerOneName());
        assertEquals("P2", s.getPlayerTwoName());
        assertEquals("", s.getWinnerName(), "sin ganador, nombre del ganador es cadena vacía");
    }

    @Test
    void getWinnerNameRefleja(){
        GameSession s = new GameSession();
        s.setPlayerOneName("Juan");
        s.setPlayerTwoName("Jacobo");
        s.setWinnerId(1);
        assertEquals("Juan", s.getWinnerName());
        s.setWinnerId(2);
        assertEquals("Jacobo", s.getWinnerName());
    }
}
