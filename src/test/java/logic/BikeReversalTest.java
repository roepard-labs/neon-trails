package logic;

import events.InputController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pruebas de la regla de movimiento estilo culebrita en modo moto: no se puede invertir la dirección
 * 180° de golpe; para volver al lado opuesto hay que girar primero. La regla está acotada a la moto;
 * fuera de ella el movimiento sigue siendo libre.
 * <p>
 * NOTE: Headless, sin Swing ni Thread.sleep. La moto se activa con {@link InputController#requestP1Bike()}
 * + un tick (queda activa 5 s reales, de sobra para la duración del test). El rumbo se lee en
 * {@link Player#getMoveX()}/{@link Player#getMoveY()} y no lo altera un eventual respawn.
 */
class BikeReversalTest {

    private static final int W = GameConstants.DEFAULT_WIDTH;
    private static final int H = GameConstants.DEFAULT_HEIGHT;

    /**
     * Estado con P1 al centro, P2 lejos y la moto de P1 ya activa (un tick sin teclas de movimiento).
     */
    private GameState bikeReadyState(InputController input) {
        GameState state = new GameState(W, H);
        state.getPlayerOne().setPosition(W / 2.0, H / 2.0);
        state.getPlayerTwo().setPosition(W - 50, H - 50);
        input.requestP1Bike();
        state.tick(input, W, H); // activa moto; sin movimiento, rumbo queda en (0,0)
        return state;
    }

    @Test
    void reversalBlockedWhileOnBike() {
        InputController input = new InputController();
        GameState state = bikeReadyState(input);
        Player p1 = state.getPlayerOne();

        // Fijar rumbo a la derecha.
        input.setP1Right(true);
        state.tick(input, W, H);
        assertEquals(1, p1.getMoveX());
        assertEquals(0, p1.getMoveY());

        // Intentar reversa: soltar derecha y mantener izquierda → debe ignorarse.
        input.setP1Right(false);
        input.setP1Left(true);
        state.tick(input, W, H);

        assertEquals(1, p1.getMoveX(), "En moto, la reversa directa se ignora: sigue a la derecha");
        assertEquals(0, p1.getMoveY());
    }

    @Test
    void perpendicularTurnAllowedWhileOnBike() {
        InputController input = new InputController();
        GameState state = bikeReadyState(input);
        Player p1 = state.getPlayerOne();

        input.setP1Right(true);
        state.tick(input, W, H);

        // Giro perpendicular (arriba): permitido aunque sea en moto.
        input.setP1Right(false);
        input.setP1Up(true);
        state.tick(input, W, H);

        assertEquals(0, p1.getMoveX(), "El giro perpendicular debe cambiar el rumbo");
        assertEquals(-1, p1.getMoveY());
    }

    @Test
    void diagonalReversalBlockedWhileOnBike() {
        InputController input = new InputController();
        GameState state = bikeReadyState(input);
        Player p1 = state.getPlayerOne();

        // Rumbo diagonal abajo-derecha (1, 1).
        input.setP1Right(true);
        input.setP1Down(true);
        state.tick(input, W, H);
        assertEquals(1, p1.getMoveX());
        assertEquals(1, p1.getMoveY());

        // Reversa diagonal exacta arriba-izquierda (-1, -1) → debe ignorarse (8 direcciones).
        input.setP1Right(false);
        input.setP1Down(false);
        input.setP1Left(true);
        input.setP1Up(true);
        state.tick(input, W, H);

        assertEquals(1, p1.getMoveX(), "La reversa diagonal exacta también se bloquea en moto");
        assertEquals(1, p1.getMoveY());
    }

    @Test
    void stopAllowedWhileOnBike() {
        InputController input = new InputController();
        GameState state = bikeReadyState(input);
        Player p1 = state.getPlayerOne();

        input.setP1Right(true);
        state.tick(input, W, H);

        // Soltar todo: detenerse no cuenta como reversa.
        input.setP1Right(false);
        state.tick(input, W, H);

        assertEquals(0, p1.getMoveX(), "Soltar las teclas detiene al jugador aun en moto");
        assertEquals(0, p1.getMoveY());
    }

    @Test
    void reversalAllowedWhenNotOnBike() {
        GameState state = new GameState(W, H);
        Player p1 = state.getPlayerOne();
        p1.setPosition(W / 2.0, H / 2.0);
        InputController input = new InputController();

        input.setP1Right(true);
        state.tick(input, W, H);
        assertEquals(1, p1.getMoveX());

        // Sin moto, invertir la dirección al instante sigue funcionando (comportamiento original).
        input.setP1Right(false);
        input.setP1Left(true);
        state.tick(input, W, H);

        assertEquals(-1, p1.getMoveX(), "Sin moto la reversa es libre");
        assertEquals(0, p1.getMoveY());
    }
}
