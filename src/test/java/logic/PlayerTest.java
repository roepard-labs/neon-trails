package logic;

import org.junit.jupiter.api.Test;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de la capa pura {@link Player}. Sin Swing ni System.nanoTime().
 */
class PlayerTest {

    private Player newPlayer(double x, double y) {
        return new Player(1, Color.CYAN, x, y);
    }

    @Test
    void moveWithinBoundsClampsAtRightEdge() {
        int width = GameConstants.DEFAULT_WIDTH;
        int height = GameConstants.DEFAULT_HEIGHT;
        int half = GameConstants.PLAYER_SIZE / 2;
        Player p = newPlayer(width - half, height / 2.0);
        p.setMove(1, 0);

        p.moveWithinBounds(width, height);

        assertEquals(width - half, p.getX(), 1e-9);
    }

    @Test
    void moveWithinBoundsAdvancesInCenter() {
        int width = GameConstants.DEFAULT_WIDTH;
        int height = GameConstants.DEFAULT_HEIGHT;
        Player p = newPlayer(width / 2.0, height / 2.0);
        double startX = p.getX();
        p.setMove(1, 0);

        p.moveWithinBounds(width, height);

        // En el centro la posición avanza una vez la PLAYER_SPEED.
        assertEquals(startX + GameConstants.PLAYER_SPEED, p.getX(), 1e-9);
    }

    @Test
    void setMoveKeepsFacingWhenStopping() {
        Player p = newPlayer(100, 100);
        p.setMove(0, -1);
        p.setMove(0, 0);

        // Al soltar las teclas, el facing debe mantener la última dirección
        // para que el disco se dispare en sentido coherente.
        assertEquals(0, p.getFacingX());
        assertEquals(-1, p.getFacingY());
    }

    @Test
    void loseLifeBottomsAtZero() {
        Player p = newPlayer(0, 0);
        for (int i = 0; i < Player.INITIAL_LIVES + 2; i++) {
            p.loseLife();
        }

        assertEquals(0, p.getLives());
        assertTrue(p.isDead());
    }

    @Test
    void resetForNewGameRestoresInitialState() {
        Player p = newPlayer(0, 0);
        p.loseLife();
        p.addScore(7);
        p.setMove(1, 1);
        p.setFireCooldownTicks(20);

        p.resetForNewGame();

        assertEquals(Player.INITIAL_LIVES, p.getLives());
        assertEquals(0, p.getScore());
        assertEquals(0, p.getMoveX());
        assertEquals(0, p.getMoveY());
        assertEquals(0, p.getFireCooldownTicks());
        assertFalse(p.isDead());
    }

    @Test
    void tickCooldownDoesNotGoNegative() {
        Player p = newPlayer(0, 0);
        p.setFireCooldownTicks(1);
        p.tickCooldown();
        p.tickCooldown();

        assertEquals(0, p.getFireCooldownTicks());
    }
}
