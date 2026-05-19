package logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de {@link DiscProjectile}: movimiento rectilíneo, ventana amistosa al dueño y expiración.
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
    void expiresAfterMaxTicks() {
        DiscProjectile disc = new DiscProjectile(2, 0, 0, 1, 0);

        for (int i = 0; i < GameConstants.DISC_MAX_TICKS - 1; i++) {
            disc.tick();
        }
        assertFalse(disc.isExpired(), "Justo antes del límite no debe estar expirado");

        disc.tick();
        assertTrue(disc.isExpired(), "Al alcanzar DISC_MAX_TICKS debe estar expirado");
    }

    @Test
    void ownerIdIsPreserved() {
        DiscProjectile disc = new DiscProjectile(2, 10, 10, 0, 1);
        assertEquals(2, disc.getOwnerId());
    }
}
