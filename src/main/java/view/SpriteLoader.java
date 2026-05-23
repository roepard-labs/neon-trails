package view;

import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Carga sprites SVG animados (handoff Claude Design) y los entrega como
 * {@link BufferedImage} cacheados, listos para dibujar con {@link java.awt.Graphics2D}.
 * <p>
 * NOTE: La transcodificación con Batik captura un frame estático del SVG (instante t=0
 * de las animaciones SMIL). Es suficiente para los sprites pequeños (22×22) de la arena
 * top-down. Si en el futuro se necesita animación viva en el panel, evaluar
 * {@code org.apache.batik.swing.JSVGCanvas} (más pesado) o pre-renderizar varios frames
 * variando {@code KEY_SNAPSHOT_TIME}.
 */
public final class SpriteLoader {

    private static final ConcurrentHashMap<String, BufferedImage> CACHE = new ConcurrentHashMap<>();
    private static final String SPRITE_ROOT = "/assets/sprites/";

    private SpriteLoader() {
    }

    /**
     * Carga un sprite cuadrado del handoff.
     *
     * @param spriteName nombre del archivo bajo {@code src/main/resources/assets/sprites/}
     *                   (ej: {@code "p1-normal.svg"}).
     * @param size lado en píxeles del {@link BufferedImage} resultante.
     * @return imagen lista para dibujar; nunca null.
     */
    public static BufferedImage load(String spriteName, int size) {
        return load(spriteName, size, size);
    }

    /**
     * Carga un sprite con dimensiones independientes (útil para arena floor estirada).
     *
     * @param spriteName nombre del archivo SVG.
     * @param width ancho objetivo en píxeles.
     * @param height alto objetivo en píxeles.
     * @return imagen cacheada por la tupla {@code (spriteName, width, height)}.
     */
    public static BufferedImage load(String spriteName, int width, int height) {
        String key = spriteName + "@" + width + "x" + height;
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
     * Vacía la caché de imágenes ya rasterizadas. Útil para tests o cambios de resolución.
     */
    public static void clearCache() {
        CACHE.clear();
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
