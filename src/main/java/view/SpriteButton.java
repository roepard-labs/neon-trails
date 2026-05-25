package view;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

/**
 * Fábrica de botones cuya cara es un sprite SVG del handoff de diseño
 * (carpeta {@code src/main/resources/assets/sprites/accionadores/}).
 * <p>
 * Reutiliza {@link SpriteLoader} para rasterizar el SVG a {@link BufferedImage} con Batik y lo coloca
 * como icono de un {@link JButton} sin chrome (sin borde, relleno ni foco pintado), de modo que el
 * vector — que ya trae el texto horneado — sea todo lo que se ve. El componente sigue siendo un
 * {@link JButton}: el click de ratón, la activación por teclado y el sonido de UI
 * ({@code audio.UiSound#attachClick}) funcionan igual que con un botón de texto.
 * <p>
 * NOTE: Espejo del patrón de {@link FontLoader}/{@link SpriteLoader} — API estática y degradación
 * elegante: si el SVG no carga, {@link #create} devuelve un botón de texto con estilo neón en vez de
 * romper el menú.
 */
public final class SpriteButton {

    private static final Color BG = new Color(0x0a, 0x0a, 0x12);
    private static final Color CYAN = new Color(0x00, 0xff, 0xff);

    private SpriteButton() {
    }

    /**
     * Crea un botón cuya cara es el sprite SVG indicado, dimensionado a su tamaño nativo.
     *
     * @param spriteName ruta del SVG bajo {@code /assets/sprites/}
     *     (p. ej. {@code "accionadores/btn-jugar.svg"}).
     * @param label texto accesible / tooltip y respaldo si el SVG falla (el texto visible vive
     *     horneado dentro del propio SVG).
     * @param width ancho objetivo en píxeles (el {@code viewBox} nativo del SVG).
     * @param height alto objetivo en píxeles.
     * @return un {@link JButton} listo para {@code addActionListener}; nunca null.
     */
    public static JButton create(String spriteName, String label, int width, int height) {
        JButton button;
        try {
            // NOTE: load() es bloqueante y SpriteLoader desaconseja llamarlo desde la EDT, pero esa
            // advertencia apunta al arena floor 960×640 (~2 s). Estos botones son ≤300×80: su
            // rasterizado es de pocos ms y ocurre una sola vez al construir las pantallas, antes de
            // que la ventana sea visible. No amerita el preload asíncrono que usa GamePanel.
            BufferedImage img = SpriteLoader.load(spriteName, width, height);
            button = new JButton(new ImageIcon(img));
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setOpaque(false);
        } catch (Throwable t) {
            // NOTE: Throwable (no solo RuntimeException) porque un FactoryConfigurationError de los
            // parsers XML hereda de Error (ver NOTE en SpriteLoader.preloadAsync). Un SVG roto no
            // debe tumbar el menú: degradamos a un botón de texto con el estilo neón de siempre.
            button = textFallback(label, height);
        }
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setToolTipText(label);
        button.getAccessibleContext().setAccessibleName(label);
        return button;
    }

    /**
     * Botón de respaldo con el estilo neón que tenían las pantallas, usado solo si el sprite no carga.
     */
    private static JButton textFallback(String label, int height) {
        JButton b = new JButton(label);
        // Fuente proporcional a la altura para que el texto entre holgado, con tope para etiquetas
        // largas ("INICIAR JUEGO", "MENÚ PRINCIPAL").
        b.setFont(FontLoader.bold(Math.max(14f, Math.min(20f, height * 0.34f))));
        b.setForeground(BG);
        b.setBackground(CYAN);
        b.setOpaque(true);
        return b;
    }
}
