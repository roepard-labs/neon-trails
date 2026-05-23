package view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifica que los 19 sprites SVG del handoff de Claude Design se cargan correctamente
 * con Batik y que la caché reutiliza la misma instancia para la misma tupla
 * {@code (spriteName, width, height)}.
 * <p>
 * NOTE: Tests headless; el transcoder de Batik usa renderer propio y no requiere display.
 */
class SpriteLoaderTest {

    /** Sprites entregados por el handoff (ver src/main/resources/assets/sprites/). */
    private static final String[] HANDOFF_SPRITES = {
            "arena-floor-vaporwave.svg",
            "bike-p1.svg", "bike-p2.svg",
            "disc-p1-active.svg", "disc-p1-stuck.svg",
            "disc-p2-active.svg", "disc-p2-stuck.svg",
            "p1-moto.svg", "p1-normal.svg",
            "p2-moto.svg", "p2-normal.svg",
            "player-p1-bike.svg", "player-p1-idle.svg",
            "player-p1-throw.svg", "player-p1-walk.svg",
            "player-p2-bike.svg", "player-p2-idle.svg",
            "player-p2-throw.svg", "player-p2-walk.svg",
    };

    @BeforeEach
    void clearCache() {
        SpriteLoader.clearCache();
    }

    @Test
    void allHandoffSpritesLoadAtCommonGameSize() {
        for (String name : HANDOFF_SPRITES) {
            BufferedImage img = SpriteLoader.load(name, 44);
            assertNotNull(img, "Sprite no cargó: " + name);
            assertEquals(44, img.getWidth(), "Ancho esperado en " + name);
            assertEquals(44, img.getHeight(), "Alto esperado en " + name);
        }
    }

    @Test
    void loadCachesByExactDimensions() {
        BufferedImage a = SpriteLoader.load("p1-normal.svg", 22);
        BufferedImage b = SpriteLoader.load("p1-normal.svg", 22);
        BufferedImage c = SpriteLoader.load("p1-normal.svg", 44);

        assertSame(a, b, "Mismo nombre y tamaño deben devolver la imagen cacheada");
        assertEquals(22, a.getWidth());
        assertEquals(44, c.getWidth(),
                "Tamaño distinto debe generar entrada de caché separada");
    }

    @Test
    void loadWithRectangularSizeRendersExactDimensions() {
        BufferedImage floor = SpriteLoader.load("arena-floor-vaporwave.svg", 960, 640);
        assertEquals(960, floor.getWidth());
        assertEquals(640, floor.getHeight());
    }

    @Test
    void missingSpriteThrowsClearError() {
        assertThrows(IllegalArgumentException.class,
                () -> SpriteLoader.load("no-existe.svg", 22),
                "Ruta inválida debe lanzar IllegalArgumentException con mensaje explicativo");
    }
}
