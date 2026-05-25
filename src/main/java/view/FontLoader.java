package view;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Carga la fuente Rajdhani (TTF empaquetado en {@code src/main/resources/assets/fonts/rajdhani/ttf/})
 * y entrega instancias {@link Font} cacheadas por la tupla (peso, tamaño).
 * <p>
 * Espejo del patrón de {@link SpriteLoader} / {@code audio.SoundManager}: sin dependencias externas
 * ({@link Font#createFont} es Java SE puro), con caché thread-safe y degradación elegante — si el TTF
 * falta o está corrupto, {@link #get} devuelve una fuente lógica en vez de romper la UI.
 * <p>
 * NOTE: se usa el subset {@code -latin-} porque cubre todo el español (á é í ó ú ñ ü ¿ ¡ viven en
 * Latin-1). Rajdhani NO incluye símbolos como ♥/♡ (U+2665/U+2661); para esos glifos el HUD usa una
 * fuente de respaldo explícita (ver {@code GamePanel.drawHud}).
 */
public final class FontLoader {

    /** Pesos de Rajdhani disponibles bajo {@code /assets/fonts/rajdhani/ttf/}. */
    public enum Weight {
        LIGHT(300), REGULAR(400), MEDIUM(500), SEMIBOLD(600), BOLD(700);
        final int value;
        Weight(int value) {
            this.value = value;
        }
    }

    private static final String FONT_ROOT = "/assets/fonts/rajdhani/ttf/";
    private static final ConcurrentHashMap<Weight, Font> BASE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Font> DERIVED = new ConcurrentHashMap<>();

    private FontLoader() {
    }

    /**
     * Carga la fuente base de un peso (BLOQUEANTE la primera vez) y la registra en el
     * {@link GraphicsEnvironment} para que también sea usable por nombre.
     */
    private static Font base(Weight w) {
        return BASE.computeIfAbsent(w, weight -> {
            String path = FONT_ROOT + "rajdhani-latin-" + weight.value + "-normal.ttf";
            try (InputStream in = FontLoader.class.getResourceAsStream(path)) {
                if (in == null) {
                    throw new IllegalArgumentException("Fuente no encontrada en classpath: " + path);
                }
                Font f = Font.createFont(Font.TRUETYPE_FONT, in);
                if (!GraphicsEnvironment.isHeadless()) {
                    GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
                }
                return f;
            } catch (IOException | FontFormatException e) {
                throw new IllegalStateException("Error cargando fuente " + path, e);
            }
        });
    }

    /**
     * Rajdhani del peso pedido al tamaño dado (cacheada).
     *
     * @param w peso Rajdhani.
     * @param size tamaño en puntos.
     * @return la fuente lista para {@code setFont}; si el TTF falla, una fuente lógica equivalente
     *     para no romper la UI.
     */
    public static Font get(Weight w, float size) {
        return DERIVED.computeIfAbsent(w + "@" + size, k -> {
            try {
                // NOTE: derivar SOLO el tamaño; el peso ya viene del propio TTF (no aplicar Font.BOLD
                // encima del 700, eso produciría faux-bold).
                return base(w).deriveFont(size);
            } catch (RuntimeException e) {
                int style = (w.value >= Weight.SEMIBOLD.value) ? Font.BOLD : Font.PLAIN;
                return new Font(Font.MONOSPACED, style, Math.round(size));
            }
        });
    }

    /** Atajo para el mapeo faithful de {@code Font.BOLD} → Rajdhani 700. */
    public static Font bold(float size) {
        return get(Weight.BOLD, size);
    }

    /** Atajo para el mapeo faithful de {@code Font.PLAIN} → Rajdhani 400. */
    public static Font regular(float size) {
        return get(Weight.REGULAR, size);
    }

    /**
     * Pre-carga sincrónica de los pesos usados, antes de mostrar la ventana, para evitar jitter en el
     * primer render. Si está headless (CI) no hace nada; {@link #get} igual degradaría a fuente lógica.
     */
    public static void preloadAll() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        try {
            base(Weight.REGULAR);
            base(Weight.BOLD);
        } catch (RuntimeException ignored) {
            // Una fuente rota no debe impedir arrancar el juego.
        }
    }
}
