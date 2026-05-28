package view.screens;

import audio.SoundManager;
import audio.Sfx;
import audio.UiSound;
import logic.GameSession;
import view.BaseScreen;
import view.FontLoader;
import view.SpriteButton;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

/**
 * Captura los nombres de los dos jugadores antes de empezar la partida.
 * <p>
 * Tiene dos campos de texto (P1 cian, P2 rosa) y un botón "Jugar" que valida los nombres,
 * los guarda en la {@link GameSession} compartida y navega a la pantalla del juego. Como
 * atajo, presionar Enter en cualquiera de los campos también empieza la partida.
 * <p>
 * NOTE: [sustentación] La validación es mínima ({@link #sanitize(String, String)}): recorta
 * espacios, limita a 15 chars y si queda vacío usa los valores por defecto "P1"/"P2". Es
 * deliberadamente simple — el énfasis del proyecto está en POO y threading, no en formularios.
 */
public class NameInputScreen extends BaseScreen {

    private static final Color BG = new Color(0x0a, 0x0a, 0x12);
    private static final Color CYAN = new Color(0x00, 0xff, 0xff);
    private static final Color PINK = new Color(0xff, 0x33, 0x99);
    private static final Color TEXT = new Color(0xee, 0xee, 0xff);
    private static final int MAX_NAME = 15;

    private final JTextField p1Field = new JTextField(12);
    private final JTextField p2Field = new JTextField(12);

    public NameInputScreen() {
        setBackground(BG);
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    /** Construye el encabezado "¿Quiénes juegan?" en cian neón. */
    private JLabel buildHeader() {
        JLabel l = new JLabel("¿Quiénes juegan?", SwingConstants.CENTER);
        l.setFont(FontLoader.bold(36f));
        l.setForeground(CYAN);
        l.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));
        return l;
    }

    /**
     * Construye el formulario central con dos filas (P1 + P2) y el botón "JUGAR" que dispara
     * {@link #empezar()}.
     */
    private JPanel buildForm() {
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));

        form.add(buildRow("Jugador 1 (cian, WASD)", p1Field, CYAN));
        form.add(Box.createVerticalStrut(20));
        form.add(buildRow("Jugador 2 (rosa, flechas)", p2Field, PINK));

        form.add(Box.createVerticalStrut(36));

        JButton play = SpriteButton.create("accionadores/btn-jugar.svg", "Jugar", 280, 56);
        UiSound.attachClick(play);
        play.addActionListener(e -> empezar());
        // UiSound.attachHover(play); // opcional
        form.add(play);

        return form;
    }

    /**
     * Construye una fila del formulario: etiqueta + {@link JTextField} con el color de acento del
     * jugador. Engancha también el atajo Enter para empezar la partida.
     */
    private JPanel buildRow(String label, JTextField field, Color accent) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel l = new JLabel(label);
        l.setFont(FontLoader.regular(14f));
        l.setForeground(accent);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);

        field.setMaximumSize(new Dimension(280, 32));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setFont(FontLoader.regular(16f));
        field.setBackground(new Color(0x14, 0x14, 0x22));
        field.setForeground(TEXT);
        field.setCaretColor(accent);
        field.setBorder(BorderFactory.createLineBorder(accent, 2));
        // NOTE: Enter en un campo dispara empezar() — reproducimos el click manualmente porque
        // attachClick sólo aplica a JButton.
        field.addActionListener(e -> {
            SoundManager.play(Sfx.UI_CLICK);
            empezar();
        });

        row.add(l);
        row.add(Box.createVerticalStrut(6));
        row.add(field);
        return row;
    }

    /** Construye el pie de la pantalla con la ayuda "Enter inicia la partida". */
    private JLabel buildFooter() {
        JLabel l = new JLabel("Enter en cualquier campo también inicia la partida", SwingConstants.CENTER);
        l.setFont(FontLoader.regular(11f));
        l.setForeground(TEXT);
        l.setBorder(BorderFactory.createEmptyBorder(0, 20, 24, 20));
        return l;
    }

    /**
     * Al mostrar la pantalla: refresca los campos con los nombres guardados en la sesión (útil
     * al volver desde GameOver para reusar los mismos nombres) y pide el foco al campo de P1.
     */
    @Override
    public void onShow() {
        // NOTE: Reset visual al volver desde GameOver.
        GameSession s = screens().getSession();
        p1Field.setText(s.getPlayerOneName());
        p2Field.setText(s.getPlayerTwoName());
        p1Field.requestFocusInWindow();
    }

    /**
     * Sanea los nombres ingresados, los guarda en la {@link GameSession}, marca la partida como
     * en curso (winnerId = 0) y navega a la pantalla del juego.
     */
    private void empezar() {
        GameSession s = screens().getSession();
        s.setPlayerOneName(sanitize(p1Field.getText(), "P1"));
        s.setPlayerTwoName(sanitize(p2Field.getText(), "P2"));
        s.setWinnerId(0);
        screens().mostrar("game");
    }

    /**
     * Sanea un nombre: recorta espacios y limita la longitud a {@link #MAX_NAME}. Si tras el
     * trim queda vacío, devuelve el valor por defecto.
     *
     * @param raw      texto bruto del campo
     * @param fallback valor a usar si el input es nulo o vacío
     * @return nombre listo para mostrar y persistir
     */
    private static String sanitize(String raw, String fallback) {
        if (raw == null) {
            return fallback;
        }
        String t = raw.trim();
        if (t.isEmpty()) {
            return fallback;
        }
        if (t.length() > MAX_NAME) {
            t = t.substring(0, MAX_NAME);
        }
        return t;
    }
}
