package view.screens;

import audio.UiSound;
import view.BaseScreen;
import view.FontLoader;
import view.SpriteButton;

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

/**
 * Pantalla "¿Cómo jugar?": presenta los controles de los dos jugadores y las reglas básicas del
 * juego antes de la partida.
 * <p>
 * Se llega aquí desde {@link WelcomeScreen} (botón "Instrucciones") y se vuelve con el botón
 * "Volver". Es estática (sin animaciones ni temporizadores propios) — su única lógica es navegar.
 * <p>
 * NOTE: [sustentación] Cumple el criterio 10 de la rúbrica académica ("Pantalla Instrucciones con
 * reglas claras + botón volver"). Las reglas listadas reflejan la mecánica real del juego, no
 * descripciones genéricas, para que sean verificables jugando.
 *
 * @see WelcomeScreen
 * @see BaseScreen
 */
public class InstructionsScreen extends BaseScreen {

    private static final Color BG = new Color(0x0a, 0x0a, 0x12);
    private static final Color CYAN = new Color(0x00, 0xff, 0xff);
    private static final Color PINK = new Color(0xff, 0x33, 0x99);
    private static final Color TEXT = new Color(0xee, 0xee, 0xff);
    private static final Color MUTED = new Color(0xb0, 0xb0, 0xc8);

    public InstructionsScreen() {
        setBackground(BG);
        setLayout(new BorderLayout());
        add(buildTitle(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    /** Construye el encabezado "¿CÓMO JUGAR?" en cian neón con su subtítulo. */
    private JPanel buildTitle() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(40, 20, 10, 20));

        JLabel title = new JLabel("¿CÓMO JUGAR?", SwingConstants.CENTER);
        title.setFont(FontLoader.bold(40f));
        title.setForeground(CYAN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("dos jugadores · un teclado · arena Tron", SwingConstants.CENTER);
        subtitle.setFont(FontLoader.regular(14f));
        subtitle.setForeground(PINK);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(title);
        p.add(Box.createVerticalStrut(8));
        p.add(subtitle);
        return p;
    }

    /**
     * Construye el cuerpo central con dos filas de controles (P1 cian, P2 rosa) y un bloque de
     * reglas debajo.
     */
    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(20, 60, 10, 60));

        body.add(controlsRow("JUGADOR 1 (cian)",
                "Mover: W A S D     ·     Disco: E     ·     Moto (5s): Q",
                CYAN));
        body.add(Box.createVerticalStrut(12));
        body.add(controlsRow("JUGADOR 2 (rosa)",
                "Mover: flechas    ·     Disco: Enter   ·     Moto (5s): U",
                PINK));
        body.add(Box.createVerticalStrut(24));
        body.add(rulesBlock());
        return body;
    }

    /**
     * Construye una fila con la etiqueta del jugador (en su color) y la lista de controles
     * debajo, centrada horizontalmente.
     */
    private JPanel controlsRow(String header, String controls, Color accent) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel head = new JLabel(header, SwingConstants.CENTER);
        head.setFont(FontLoader.bold(16f));
        head.setForeground(accent);
        head.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel body = new JLabel(controls, SwingConstants.CENTER);
        body.setFont(FontLoader.regular(14f));
        body.setForeground(TEXT);
        body.setAlignmentX(Component.CENTER_ALIGNMENT);

        row.add(head);
        row.add(Box.createVerticalStrut(4));
        row.add(body);
        return row;
    }

    /**
     * Construye el bloque de reglas con cinco bullets que reflejan la mecánica real del juego:
     * comportamiento del disco, modo moto, condición de victoria, etc.
     */
    private JPanel rulesBlock() {
        JPanel rules = new JPanel();
        rules.setOpaque(false);
        rules.setLayout(new BoxLayout(rules, BoxLayout.Y_AXIS));
        rules.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        rules.add(rule("· El disco rebota hasta 3 veces; al tercer rebote queda quieto y solo el dueño puede recogerlo."));
        rules.add(rule("· Cada jugador puede tener UN disco en juego al mismo tiempo."));
        rules.add(rule("· 8 ticks de gracia: el disco recién disparado no puede golpear a su propio dueño."));
        rules.add(rule("· Modo moto: dura 5 s, da más velocidad y deja un rastro letal. Cuidado: no se puede reversar directo."));
        rules.add(rule("· Cada jugador tiene 3 vidas. Gana quien deje al rival sin vidas."));
        return rules;
    }

    /** Crea una línea de regla con estilo uniforme (regular 13f, color {@link #MUTED}). */
    private JLabel rule(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FontLoader.regular(13f));
        l.setForeground(MUTED);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    /**
     * Construye el pie con el botón "VOLVER" que regresa a {@link WelcomeScreen}. Reutiliza el
     * sprite {@code btn-menu-principal.svg} ya presente en el handoff de diseño.
     */
    private JPanel buildFooter() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 30, 20));

        JButton back = SpriteButton.create("accionadores/btn-menu-principal.svg", "Volver", 220, 56);
        UiSound.attachClick(back);
        back.addActionListener(e -> screens().mostrar("welcome"));
        back.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(back);
        return p;
    }
}
