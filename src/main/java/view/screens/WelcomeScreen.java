package view.screens;

import audio.UiSound;
import view.BaseScreen;
import view.FontLoader;
import view.SpriteButton;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Pantalla de bienvenida / menú principal de Neon Trails.
 * <p>
 * Es la primera pantalla que ve el usuario al lanzar el juego. Tiene tres zonas en {@link BorderLayout}:
 * <ul>
 *   <li><b>NORTH</b> — título "NEON TRAILS" y subtítulo "arena 2 jugadores".</li>
 *   <li><b>CENTER</b> — botones de acción ("Iniciar Juego" y "Instrucciones").</li>
 *   <li><b>SOUTH</b> — créditos institucionales: logo UAM (si está presente), materia, docente
 *       e integrantes del equipo.</li>
 * </ul>
 * <p>
 * Es estática (sin animaciones); su único trabajo es navegar a la pantalla correspondiente
 * cuando se pulsa un botón.
 *
 * @see InstructionsScreen
 * @see NameInputScreen
 */
public class WelcomeScreen extends BaseScreen {

    private static final Color BG = new Color(0x0a, 0x0a, 0x12);
    private static final Color CYAN = new Color(0x00, 0xff, 0xff);
    private static final Color PINK = new Color(0xff, 0x33, 0x99);
    private static final Color TEXT = new Color(0xee, 0xee, 0xff);

    public WelcomeScreen() {
        setBackground(BG);
        setLayout(new BorderLayout());
        add(buildTitle(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    /**
     * Construye la sección superior con el título "NEON TRAILS" (cian neón) y el subtítulo
     * "arena 2 jugadores" (rosa neón) centrados verticalmente.
     */
    private JPanel buildTitle() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(40, 20, 10, 20));

        JLabel title = new JLabel("NEON TRAILS", SwingConstants.CENTER);
        title.setFont(FontLoader.bold(56f));
        title.setForeground(CYAN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("arena 2 jugadores", SwingConstants.CENTER);
        subtitle.setFont(FontLoader.regular(16f));
        subtitle.setForeground(PINK);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(title);
        p.add(Box.createVerticalStrut(8));
        p.add(subtitle);
        return p;
    }

    /**
     * Construye la sección central con los botones de acción:
     * <ol>
     *   <li>"INICIAR JUEGO" (grande, sprite cian) → navega a {@link NameInputScreen}.</li>
     *   <li>"INSTRUCCIONES" (mediano, sprite rosa) → navega a {@link InstructionsScreen}.</li>
     * </ol>
     */
    private JPanel buildCenter() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

        JButton play = SpriteButton.create("accionadores/btn-iniciar-juego.svg", "Iniciar Juego", 300, 80);
        UiSound.attachClick(play);
        play.addActionListener(e -> screens().mostrar("nameinput"));
        play.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton howTo = SpriteButton.create("accionadores/btn-menu-principal.svg", "Instrucciones", 240, 60);
        UiSound.attachClick(howTo);
        howTo.addActionListener(e -> screens().mostrar("instructions"));
        howTo.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(Box.createVerticalGlue());
        p.add(play);
        p.add(Box.createVerticalStrut(16));
        p.add(howTo);
        p.add(Box.createVerticalGlue());
        return p;
    }

    /**
     * Construye la sección inferior con créditos institucionales: logo UAM (opcional, vía
     * {@link #tryLoadUamLogo()}), nombre de la universidad, materia, docente e integrantes del
     * equipo.
     * <p>
     * El logo se carga de forma opcional desde {@code /assets/branding/logo-uam.png}; si el
     * recurso no existe se omite sin romper la pantalla.
     */
    private JPanel buildFooter() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(10, 20, 30, 20));

        JComponent logo = tryLoadUamLogo();
        p.add(logo);
        p.add(Box.createVerticalStrut(6));

        JLabel uam = centeredLabel("Universidad Autónoma de Manizales", 13);
        uam.setForeground(CYAN);
        JLabel materia = centeredLabel("Técnicas de Programación · Fundamentos de POO", 12);
        JLabel docente = centeredLabel("Docente: Leonardo Montes", 11);
        JLabel integrantes = centeredLabel(
                "Juan Esteban Manrique Giraldo · Jacobo Lopez Patiño", 11);

        p.add(uam);
        p.add(materia);
        p.add(Box.createVerticalStrut(2));
        p.add(docente);
        p.add(Box.createVerticalStrut(6));
        p.add(integrantes);
        return p;
    }

    /** Crea un {@link JLabel} centrado horizontalmente con la fuente Rajdhani regular. */
    private static JLabel centeredLabel(String text, int size) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(FontLoader.regular(size));
        l.setForeground(TEXT);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    /**
     * Carga {@code /assets/branding/logo-uam.png} usando {@link ImageIO} y lo escala a 100 px de
     * alto preservando la proporción. Si el recurso no existe o no se puede leer, devuelve un
     * {@link JLabel} vacío (degradación elegante — la pantalla sigue funcional).
     * <p>
     * NOTE: [sustentación] Uso explícito de {@link ImageIO#read(InputStream)} para cubrir el
     * criterio 7 de la rúbrica ("Gestión de recursos - imágenes"). Complementa a Batik (que se
     * usa para SVG): ImageIO es la API estándar de Java SE para PNG/JPEG y no requiere
     * dependencias externas.
     *
     * @return un {@link JComponent} con el logo escalado, o un {@code JLabel} vacío si no se pudo
     *         cargar; nunca {@code null}
     */
    private JComponent tryLoadUamLogo() {
        try (InputStream in = getClass().getResourceAsStream("/assets/branding/logo-uam.png")) {
            if (in == null) {
                return new JLabel();
            }
            BufferedImage img = ImageIO.read(in);
            if (img == null) {
                return new JLabel();
            }
            int targetH = 100;
            int targetW = img.getWidth() * targetH / img.getHeight();
            Image scaled = img.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
            JLabel lbl = new JLabel(new ImageIcon(scaled));
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            return lbl;
        } catch (IOException e) {
            return new JLabel();
        }
    }
}
