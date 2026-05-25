package view.screens;

import audio.Sfx;
import audio.SoundManager;
import audio.UiSound;
import logic.GameSession;
import net.LeaderboardClient;
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
        title.setFont(FontLoader.bold(48f));
        title.setForeground(PINK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        headline.setFont(FontLoader.bold(28f));
        headline.setForeground(CYAN);
        headline.setAlignmentX(Component.CENTER_ALIGNMENT);

        detail.setFont(FontLoader.regular(14f));
        detail.setForeground(TEXT);
        detail.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton back = SpriteButton.create("accionadores/btn-menu-principal.svg", "Volver al menú", 220, 56);
        UiSound.attachClick(back);
        back.addActionListener(e -> screens().mostrar("welcome"));

        JButton replay = SpriteButton.create("accionadores/btn-reintentar.svg", "Jugar de nuevo", 220, 56);
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
            // Publica ambos puntajes en el leaderboard (asíncrono y tolerante a fallos).
            LeaderboardClient.submitAsync(s.getPlayerOneName(), s.getPlayerOneScore(), w == 1 ? "win" : "loss");
            LeaderboardClient.submitAsync(s.getPlayerTwoName(), s.getPlayerTwoScore(), w == 2 ? "win" : "loss");
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
}
