package logic;

import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;

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

    @Test
    void addTrailPointStoresPosition() {
        Player p = newPlayer(100, 100);
        p.addTrailPoint(150, 200);

        List<Point2D.Double> trail = p.getMotoTrail();
        assertEquals(1, trail.size());
        assertEquals(150, trail.get(0).x, 1e-9);
        assertEquals(200, trail.get(0).y, 1e-9);
    }

    @Test
    void addTrailPointSkipsDuplicatePosition() {
        Player p = newPlayer(0, 0);
        p.addTrailPoint(50, 50);
        p.addTrailPoint(50, 50);
        p.addTrailPoint(50, 50);

        assertEquals(1, p.getMotoTrail().size(),
                "Mismas coordenadas consecutivas no deben inflar la lista");

        p.addTrailPoint(51, 50);
        assertEquals(2, p.getMotoTrail().size(),
                "Coordenadas distintas si avanzan la lista");
    }

    @Test
    void clearTrailRemovesAllPointsAndStopsErosion() {
        Player p = newPlayer(0, 0);
        p.addTrailPoint(1, 1);
        p.addTrailPoint(2, 2);
        p.startTrailErosion();
        assertTrue(p.isTrailEroding());

        p.clearTrail();

        assertTrue(p.getMotoTrail().isEmpty());
        assertFalse(p.isTrailEroding());
    }

    @Test
    void trailInvulnActiveWithinWindow() {
        Player p = newPlayer(0, 0);
        assertFalse(p.isTrailInvuln(), "Sin setTrailInvuln, no debe haber invulnerabilidad");

        p.setTrailInvuln();

        assertTrue(p.isTrailInvuln(),
                "Tras setTrailInvuln la ventana de 0.5 s debe estar activa");
    }

    @Test
    void trailInvulnExpiresOnTimerInThePast() {
        Player p = newPlayer(0, 0);
        // NOTE: Usar setter package-private para forzar el final de la ventana en el pasado y
        // evitar Thread.sleep en tests.
        p.setTrailInvulnUntilNanos(System.nanoTime() - 1_000_000L);

        assertFalse(p.isTrailInvuln());
    }

    @Test
    void erodeTrailEmptiesProgressively() {
        Player p = newPlayer(0, 0);
        // 60 puntos distintos. A 60 Hz y EROSION_SEC=3 → 180 ticks ideales para vaciar; con
        // size<totalTicks la fórmula colapsa a 1 punto por tick, así que 60 ticks bastan.
        for (int i = 0; i < 60; i++) {
            p.addTrailPoint(i, 0);
        }
        p.startTrailErosion();
        int initialSize = p.getMotoTrail().size();

        // Un solo tick debe reducir al menos un punto.
        p.erodeTrail();
        assertTrue(p.getMotoTrail().size() < initialSize,
                "erodeTrail debe quitar al menos un punto por llamada");

        // Suficientes ticks para vaciar por completo.
        for (int i = 0; i < 200; i++) {
            p.erodeTrail();
        }
        assertTrue(p.getMotoTrail().isEmpty(),
                "Tras suficientes ticks la cola debe quedar vacía");
        assertFalse(p.isTrailEroding(),
                "Al vaciarse, erodeTrail debe apagar la bandera de erosión");
    }

    @Test
    void resetForNewGameClearsTrailAndInvuln() {
        Player p = newPlayer(0, 0);
        p.addTrailPoint(10, 10);
        p.addTrailPoint(20, 20);
        p.startTrailErosion();
        p.setTrailInvuln();

        p.resetForNewGame();

        assertTrue(p.getMotoTrail().isEmpty(), "resetForNewGame debe vaciar el trail");
        assertFalse(p.isTrailEroding(), "resetForNewGame debe apagar erosión");
        assertFalse(p.isTrailInvuln(), "resetForNewGame debe limpiar la ventana de invulnerabilidad");
    }
}
