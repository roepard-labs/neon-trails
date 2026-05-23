package view;

import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Carga sprites SVG animados (handoff Claude Design) y los entrega como
 * {@link BufferedImage} cacheados, listos para dibujar con {@link java.awt.Graphics2D}.
 * <p>
 * NOTE: La transcodificación con Batik captura un frame estático del SVG (instante t=0
 * de las animaciones SMIL). El rasterizado del arena floor a 960×640 toma varios segundos:
 * por eso {@link #load(String, int, int)} NO debe llamarse desde la EDT durante un paint.
 * Usar {@link #preloadAsync(List)} en construcción y {@link #tryGetCached(String, int)} desde
 * paintComponent (con fallback geométrico si todavía no se cargó).
 */
public final class SpriteLoader {

    private static final ConcurrentHashMap<String, BufferedImage> CACHE = new ConcurrentHashMap<>();
    private static final String SPRITE_ROOT = "/assets/sprites/";

    private SpriteLoader() {
    }

    /**
     * Carga un sprite cuadrado del handoff (BLOQUEANTE — no llamar desde la EDT).
     *
     * @param spriteName nombre del archivo bajo {@code src/main/resources/assets/sprites/}.
     * @param size lado en píxeles del {@link BufferedImage} resultante.
     * @return imagen lista para dibujar; nunca null.
     */
    public static BufferedImage load(String spriteName, int size) {
        return load(spriteName, size, size);
    }

    /**
     * Carga un sprite con dimensiones independientes (BLOQUEANTE — útil para arena floor).
     *
     * @param spriteName nombre del archivo SVG.
     * @param width ancho objetivo en píxeles.
     * @param height alto objetivo en píxeles.
     * @return imagen cacheada por la tupla {@code (spriteName, width, height)}.
     */
    public static BufferedImage load(String spriteName, int width, int height) {
        String key = key(spriteName, width, height);
        BufferedImage cached = CACHE.get(key);
        if (cached != null) {
            return cached;
        }
        String path = SPRITE_ROOT + spriteName;
        try (InputStream in = SpriteLoader.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalArgumentException("Sprite no encontrado en classpath: " + path);
            }
            CapturingTranscoder transcoder = new CapturingTranscoder();
            transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, (float) width);
            transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, (float) height);
            transcoder.transcode(new TranscoderInput(in), null);
            BufferedImage img = transcoder.getImage();
            if (img == null) {
                throw new IllegalStateException("Batik no produjo imagen para " + path);
            }
            CACHE.put(key, img);
            return img;
        } catch (IOException | TranscoderException e) {
            throw new IllegalStateException("Error cargando sprite " + spriteName, e);
        }
    }

    /**
     * Lookup NO BLOQUEANTE en la caché. Apto para llamar desde paintComponent.
     *
     * @return imagen cacheada o {@code null} si todavía no se preloadeó.
     */
    public static BufferedImage tryGetCached(String spriteName, int size) {
        return tryGetCached(spriteName, size, size);
    }

    /**
     * Lookup NO BLOQUEANTE para sprites rectangulares.
     */
    public static BufferedImage tryGetCached(String spriteName, int width, int height) {
        return CACHE.get(key(spriteName, width, height));
    }

    /**
     * Pre-carga una lista de sprites en un hilo daemon. Returns inmediatamente. Errores en
     * sprites individuales no abortan la pre-carga del resto.
     * <p>
     * NOTE: Usar en GamePanel constructor para que cuando la pantalla de juego aparezca los
     * sprites estén en cache y paintComponent pueda obtenerlos vía {@link #tryGetCached}.
     *
     * @return el hilo daemon arrancado (útil en tests con {@code thread.join()}).
     */
    public static Thread preloadAsync(List<SpriteSpec> sprites) {
        Thread t = new Thread(() -> {
            for (SpriteSpec spec : sprites) {
                try {
                    load(spec.name, spec.width, spec.height);
                } catch (Throwable ignored) {
                    // NOTE: Atrapamos Throwable (no solo RuntimeException) porque
                    // FactoryConfigurationError de los parsers XML hereda de Error.
                    // Un sprite roto NO debe abortar el preload del resto ni matar el daemon.
                }
            }
        }, "sprite-preloader");
        t.setDaemon(true);
        t.start();
        return t;
    }

    /**
     * Vacía la caché de imágenes ya rasterizadas. Útil para tests o cambios de resolución.
     */
    public static void clearCache() {
        CACHE.clear();
    }

    private static String key(String spriteName, int width, int height) {
        return spriteName + "@" + width + "x" + height;
    }

    /**
     * Especificación de un sprite a pre-cargar: nombre y dimensiones objetivo.
     */
    public static final class SpriteSpec {
        final String name;
        final int width;
        final int height;

        public SpriteSpec(String name, int size) {
            this(name, size, size);
        }

        public SpriteSpec(String name, int width, int height) {
            this.name = name;
            this.width = width;
            this.height = height;
        }
    }

    /**
     * Variante de {@link ImageTranscoder} que captura el {@link BufferedImage} resultante en
     * lugar de escribirlo a un {@link java.io.OutputStream}.
     */
    private static final class CapturingTranscoder extends ImageTranscoder {
        private BufferedImage image;

        @Override
        public BufferedImage createImage(int w, int h) {
            return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        }

        @Override
        public void writeImage(BufferedImage img, TranscoderOutput output) {
            this.image = img;
        }

        BufferedImage getImage() {
            return image;
        }
    }
}
