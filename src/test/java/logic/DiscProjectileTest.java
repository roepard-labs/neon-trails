package logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de {@link DiscProjectile}: movimiento rectilíneo, ventana amistosa al dueño,
 * rebotes en bordes y estado quieto tras agotar rebotes.
 */
class DiscProjectileTest {

    @Test
    void tickAdvancesByVelocity() {
        DiscProjectile disc = new DiscProjectile(1, 100.0, 100.0, 2.5, -1.5);

        disc.tick();

        assertEquals(102.5, disc.getX(), 1e-9);
        assertEquals(98.5, disc.getY(), 1e-9);
        assertEquals(1, disc.getTicksAlive());
    }

    @Test
    void friendlyWindowProtectsOwnerForFirstEightTicks() {
        DiscProjectile disc = new DiscProjectile(1, 0, 0, 1, 0);
        assertTrue(disc.isFriendlyToOwner(), "Recién creado debe ser amistoso para evitar autogol");

        for (int i = 0; i < 7; i++) {
            disc.tick();
            assertTrue(disc.isFriendlyToOwner(), "Sigue amistoso en el tick " + (i + 1));
        }

        disc.tick(); // tick #8
        assertFalse(disc.isFriendlyToOwner(), "Tras 8 ticks el disco ya puede impactar al dueño");
    }

    @Test
    void ownerIdIsPreserved() {
        DiscProjectile disc = new DiscProjectile(2, 10, 10, 0, 1);
        assertEquals(2, disc.getOwnerId());
    }

    @Test
    void bounceXInvertsHorizontalVelocity() {
        DiscProjectile disc = new DiscProjectile(1, 100.0, 100.0, 3.0, 2.0);

        disc.bounceX();
        disc.tick();

        assertEquals(97.0, disc.getX(), 1e-9, "vx debe haberse invertido");
        assertEquals(102.0, disc.getY(), 1e-9, "vy no debe cambiar");
        assertFalse(disc.isStuck(), "Aún quedan rebotes; no debe quedar quieto");
    }

    @Test
    void bounceYInvertsVerticalVelocity() {
        DiscProjectile disc = new DiscProjectile(1, 100.0, 100.0, 3.0, 2.0);

        disc.bounceY();
        disc.tick();

        assertEquals(103.0, disc.getX(), 1e-9, "vx no debe cambiar");
        assertEquals(98.0, disc.getY(), 1e-9, "vy debe haberse invertido");
        assertFalse(disc.isStuck());
    }

    @Test
    void becomesStuckAfterMaxBounces() {
        DiscProjectile disc = new DiscProjectile(1, 100.0, 100.0, 3.0, 2.0);

        for (int i = 0; i < GameConstants.DISC_MAX_BOUNCES; i++) {
            assertFalse(disc.isStuck(), "Antes del rebote " + (i + 1) + " no debe estar quieto");
            disc.bounceX();
        }

        assertTrue(disc.isStuck(), "Tras agotar DISC_MAX_BOUNCES el disco queda quieto");
    }

    @Test
    void stuckDiscDoesNotMoveOnTick() {
        DiscProjectile disc = new DiscProjectile(1, 50.0, 75.0, 4.0, 1.0);
        for (int i = 0; i < GameConstants.DISC_MAX_BOUNCES; i++) {
            disc.bounceX();
        }
        assertTrue(disc.isStuck());
        double xBefore = disc.getX();
        double yBefore = disc.getY();

        for (int i = 0; i < 30; i++) {
            disc.tick();
        }

        assertEquals(xBefore, disc.getX(), 1e-9, "Disco quieto no debe moverse en X");
        assertEquals(yBefore, disc.getY(), 1e-9, "Disco quieto no debe moverse en Y");
    }

    @Test
    void bounceOnStuckDiscIsNoop() {
        DiscProjectile disc = new DiscProjectile(1, 50.0, 75.0, 4.0, 1.0);
        for (int i = 0; i < GameConstants.DISC_MAX_BOUNCES; i++) {
            disc.bounceX();
        }
        assertTrue(disc.isStuck());

        disc.bounceX();
        disc.bounceY();
        disc.tick();

        assertEquals(50.0, disc.getX(), 1e-9);
        assertEquals(75.0, disc.getY(), 1e-9);
    }
}
