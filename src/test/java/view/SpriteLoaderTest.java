package view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    void accionadorButtonsLoadAtNativeSize() {
        // NOTE: Regresión — estos SVG traían fill="rgba(255,255,255,0.15)", que Batik 1.17 rechaza
        // (la función rgba() no es válida en el atributo fill de SVG 1.1; lo correcto es fill +
        // fill-opacity). Rasterizarlos de verdad aquí evita que un re-export del handoff reintroduzca
        // el valor inválido sin que nadie lo note hasta el runtime.
        assertButtonLoads("accionadores/btn-iniciar-juego.svg", 300, 80);
        assertButtonLoads("accionadores/btn-jugar.svg", 280, 56);
        assertButtonLoads("accionadores/btn-reintentar.svg", 220, 56);
        assertButtonLoads("accionadores/btn-menu-principal.svg", 220, 56);
    }

    private static void assertButtonLoads(String name, int width, int height) {
        BufferedImage img = SpriteLoader.load(name, width, height);
        assertNotNull(img, "Botón no cargó: " + name);
        assertEquals(width, img.getWidth(), "Ancho esperado en " + name);
        assertEquals(height, img.getHeight(), "Alto esperado en " + name);
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

    @Test
    void tryGetCachedReturnsNullBeforeLoad() {
        assertNull(SpriteLoader.tryGetCached("p1-normal.svg", 22),
                "Sin carga previa, tryGetCached debe ser non-blocking y devolver null");
    }

    @Test
    void tryGetCachedReturnsCachedImageAfterLoad() {
        BufferedImage loaded = SpriteLoader.load("p2-moto.svg", 44);
        BufferedImage cached = SpriteLoader.tryGetCached("p2-moto.svg", 44);

        assertSame(loaded, cached,
                "tryGetCached debe devolver exactamente la imagen que ya está en cache");
    }

    @Test
    void preloadAsyncPopulatesCacheInBackground() throws InterruptedException {
        Thread worker = SpriteLoader.preloadAsync(List.of(
                new SpriteLoader.SpriteSpec("p1-normal.svg", 30),
                new SpriteLoader.SpriteSpec("disc-p1-active.svg", 28)));
        // El preload se ejecuta en thread daemon; esperamos a que termine para verificar.
        worker.join(10_000L);

        assertNotNull(SpriteLoader.tryGetCached("p1-normal.svg", 30),
                "Tras el preload, el sprite debe estar disponible en cache");
        assertNotNull(SpriteLoader.tryGetCached("disc-p1-active.svg", 28));
    }

    @Test
    void preloadAsyncSkipsMissingSpritesWithoutCrashing() throws InterruptedException {
        Thread worker = SpriteLoader.preloadAsync(List.of(
                new SpriteLoader.SpriteSpec("no-existe.svg", 22),
                new SpriteLoader.SpriteSpec("p1-moto.svg", 22)));
        worker.join(10_000L);

        assertNull(SpriteLoader.tryGetCached("no-existe.svg", 22));
        assertNotNull(SpriteLoader.tryGetCached("p1-moto.svg", 22),
                "Un sprite faltante no debe abortar el preload del resto");
    }
}
