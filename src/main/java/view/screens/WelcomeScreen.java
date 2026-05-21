package view.screens;

import audio.UiSound;
import view.BaseScreen;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

/**
 * Pantalla de bienvenida / menú principal.
 * <p>
 * NOTE: Estática (sin animaciones); navegación con botones que llevan a la captura de nombres.
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

    private JPanel buildTitle() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(40, 20, 10, 20));

        JLabel title = new JLabel("NEON TRAILS", SwingConstants.CENTER);
        title.setFont(new Font(Font.MONOSPACED, Font.BOLD, 56));
        title.setForeground(CYAN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("arena 2 jugadores", SwingConstants.CENTER);
        subtitle.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        subtitle.setForeground(PINK);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(title);
        p.add(Box.createVerticalStrut(8));
        p.add(subtitle);
        return p;
    }

    private JPanel buildCenter() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

        JButton play = neonButton("Iniciar Juego");
        UiSound.attachClick(play);
        play.addActionListener(e -> screens().mostrar("nameinput"));
        play.setAlignmentX(Component.CENTER_ALIGNMENT);
        // UiSound.attachHover(play); // opcional: hover suave si se quiere más vivo

        p.add(Box.createVerticalGlue());
        p.add(play);
        p.add(Box.createVerticalGlue());
        return p;
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(10, 20, 30, 20));

        JLabel materia = centeredLabel("Técnicas de Programación", 13);
        JLabel docente = centeredLabel("Docente: Leonardo Montes", 12);
        JLabel hint = centeredLabel("Pulsa Enter para iniciar", 11);
        hint.setForeground(CYAN);

        p.add(materia);
        p.add(Box.createVerticalStrut(4));
        p.add(docente);
        p.add(Box.createVerticalStrut(12));
        p.add(hint);
        return p;
    }

    private static JLabel centeredLabel(String text, int size) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font(Font.MONOSPACED, Font.PLAIN, size));
        l.setForeground(TEXT);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private static JButton neonButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        b.setForeground(BG);
        b.setBackground(CYAN);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(12, 28, 12, 28));
        b.setMaximumSize(new Dimension(280, 56));
        return b;
    }
}
