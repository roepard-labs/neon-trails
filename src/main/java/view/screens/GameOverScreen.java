package view.screens;

import audio.Sfx;
import audio.SoundManager;
import audio.UiSound;
import logic.GameSession;
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
import java.util.concurrent.ThreadLocalRandom;

/**
 * Pantalla de fin de partida: anuncia ganador y permite volver al menú.
 */
public class GameOverScreen extends BaseScreen {

    private static final Color BG = new Color(0x0a, 0x0a, 0x12);
    private static final Color CYAN = new Color(0x00, 0xff, 0xff);
    private static final Color PINK = new Color(0xff, 0x33, 0x99);
    private static final Color TEXT = new Color(0xee, 0xee, 0xff);

    private final JLabel headline = new JLabel("", SwingConstants.CENTER);
    private final JLabel detail = new JLabel("", SwingConstants.CENTER);
    /** Último jingle de game over reproducido; se conserva para detenerlo en {@link #onHide()}. */
    private Sfx activeGameOverSfx;

    public GameOverScreen() {
        setBackground(BG);
        setLayout(new BorderLayout());

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(60, 20, 60, 20));

        JLabel title = new JLabel("GAME OVER", SwingConstants.CENTER);
        title.setFont(new Font(Font.MONOSPACED, Font.BOLD, 48));
        title.setForeground(PINK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        headline.setFont(new Font(Font.MONOSPACED, Font.BOLD, 28));
        headline.setForeground(CYAN);
        headline.setAlignmentX(Component.CENTER_ALIGNMENT);

        detail.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        detail.setForeground(TEXT);
        detail.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton back = neonButton("Volver al menú");
        back.setAlignmentX(Component.CENTER_ALIGNMENT);
        UiSound.attachClick(back);
        back.addActionListener(e -> screens().mostrar("welcome"));

        JButton replay = neonButton("Jugar de nuevo");
        replay.setAlignmentX(Component.CENTER_ALIGNMENT);
        UiSound.attachClick(replay);
        replay.addActionListener(e -> screens().mostrar("game"));

        center.add(title);
        center.add(Box.createVerticalStrut(24));
        center.add(headline);
        center.add(Box.createVerticalStrut(12));
        center.add(detail);
        center.add(Box.createVerticalStrut(40));
        center.add(replay);
        center.add(Box.createVerticalStrut(12));
        center.add(back);

        add(center, BorderLayout.CENTER);
    }

    @Override
    public void onShow() {
        GameSession s = screens().getSession();
        int w = s.getWinnerId();
        if (w == 1 || w == 2) {
            headline.setText("¡Gana " + s.getWinnerName() + "!");
            detail.setText(w == 1
                    ? s.getPlayerTwoName() + " se quedó sin vidas"
                    : s.getPlayerOneName() + " se quedó sin vidas");
        } else {
            headline.setText("Partida finalizada");
            detail.setText("");
        }
        // NOTE: Elegimos uniformemente entre las dos variantes para añadir variedad sin sobreingeniar
        // un mapeo por jugador. La pista se detiene en onHide() al volver al menú.
        activeGameOverSfx = ThreadLocalRandom.current().nextBoolean() ? Sfx.GAMEOVER_1 : Sfx.GAMEOVER_2;
        SoundManager.play(activeGameOverSfx);
    }

    @Override
    public void onHide() {
        if (activeGameOverSfx != null) {
            SoundManager.stop(activeGameOverSfx);
            activeGameOverSfx = null;
        }
    }

    private static JButton neonButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
        b.setForeground(BG);
        b.setBackground(CYAN);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        b.setMaximumSize(new Dimension(260, 48));
        return b;
    }
}
