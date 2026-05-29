package view.screens;

import audio.Sfx;
import audio.SoundManager;
import audio.UiSound;
import logic.GameSession;
import logic.RankingManager;
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
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Pantalla de fin de partida: anuncia al ganador, muestra el detalle del marcador y el Top-3
 * histórico, y permite volver al menú o reiniciar la partida.
 * <p>
 * Tiene tres responsabilidades secundarias:
 * <ol>
 *   <li><b>Publicar puntajes</b> en el leaderboard remoto (POST {@code /api/scores}) de forma
 *       asíncrona y tolerante a fallos vía
 *       {@link LeaderboardClient#submitAsync(String, int, String)}.</li>
 *   <li><b>Registrar puntajes localmente</b> en el {@link RankingManager} (ranking persistente
 *       en {@code ~/.neon-trails/ranking.json}) y mostrar el Top-3 resultante.</li>
 *   <li><b>Reproducir un "sting"</b> (jingle de fin de partida) elegido aleatoriamente entre dos
 *       variantes, que se detiene al salir de la pantalla.</li>
 * </ol>
 */
public class GameOverScreen extends BaseScreen {

    private static final Color BG = new Color(0x0a, 0x0a, 0x12);
    private static final Color CYAN = new Color(0x00, 0xff, 0xff);
    private static final Color PINK = new Color(0xff, 0x33, 0x99);
    private static final Color TEXT = new Color(0xee, 0xee, 0xff);
    private static final Color MUTED = new Color(0xb0, 0xb0, 0xc8);

    private final JLabel headline = new JLabel("", SwingConstants.CENTER);
    private final JLabel detail = new JLabel("", SwingConstants.CENTER);

    /** Panel con el Top-3 del ranking persistente; se recompone en cada {@link #onShow()}. */
    private final JPanel top3Panel = new JPanel();

    /** Último jingle de game over reproducido; se conserva para detenerlo en {@link #onHide()}. */
    private Sfx activeGameOverSfx;

    /**
     * Monta la jerarquía visual: título "GAME OVER", anuncio del ganador, detalle del marcador,
     * panel del Top-3 y dos botones ("Jugar de nuevo" y "Volver al menú").
     */
    public GameOverScreen() {
        setBackground(BG);
        setLayout(new BorderLayout());

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));

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

        top3Panel.setOpaque(false);
        top3Panel.setLayout(new BoxLayout(top3Panel, BoxLayout.Y_AXIS));
        top3Panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton back = SpriteButton.create("accionadores/btn-menu-principal.svg", "Volver al menú", 220, 56);
        UiSound.attachClick(back);
        back.addActionListener(e -> screens().mostrar("welcome"));

        JButton replay = SpriteButton.create("accionadores/btn-reintentar.svg", "Jugar de nuevo", 220, 56);
        UiSound.attachClick(replay);
        replay.addActionListener(e -> screens().mostrar("game"));

        center.add(title);
        center.add(Box.createVerticalStrut(20));
        center.add(headline);
        center.add(Box.createVerticalStrut(8));
        center.add(detail);
        center.add(Box.createVerticalStrut(20));
        center.add(top3Panel);
        center.add(Box.createVerticalStrut(28));
        center.add(replay);
        center.add(Box.createVerticalStrut(10));
        center.add(back);

        add(center, BorderLayout.CENTER);
    }

    /**
     * Al mostrar la pantalla: actualiza los textos del anuncio según el ganador registrado en la
     * sesión, registra ambos puntajes en el ranking local y los publica también en el backend
     * Laravel (asíncrono y tolerante a fallos), renderiza el Top-3 y lanza un jingle aleatorio.
     */
    @Override
    public void onShow() {
        GameSession s = screens().getSession();
        int w = s.getWinnerId();
        if (w == 1 || w == 2) {
            headline.setText("¡Gana " + s.getWinnerName() + "!");
            String quienPerdio = w == 1
                    ? s.getPlayerTwoName() + " se quedó sin vidas"
                    : s.getPlayerOneName() + " se quedó sin vidas";
            detail.setText(quienPerdio + "  ·  " + formatDuration(s.getMatchDurationMillis()));

            // Persistencia local: ranking ordenado con quicksort recursivo, guardado en JSON.
            RankingManager ranking = screens().getRanking();
            ranking.addEntry(s.getPlayerOneName(), s.getPlayerOneScore(), w == 1 ? "win" : "loss");
            ranking.addEntry(s.getPlayerTwoName(), s.getPlayerTwoScore(), w == 2 ? "win" : "loss");
            renderTop3(ranking.getTop(3));

            // Publicación remota (backend Laravel) — asíncrona, no bloquea ni interrumpe el juego.
            LeaderboardClient.submitAsync(s.getPlayerOneName(), s.getPlayerOneScore(), w == 1 ? "win" : "loss");
            LeaderboardClient.submitAsync(s.getPlayerTwoName(), s.getPlayerTwoScore(), w == 2 ? "win" : "loss");
        } else {
            headline.setText("Partida finalizada");
            detail.setText("");
            top3Panel.removeAll();
            top3Panel.revalidate();
            top3Panel.repaint();
        }
        // NOTE: Elegimos uniformemente entre las dos variantes para añadir variedad sin sobreingeniar
        // un mapeo por jugador. La pista se detiene en onHide() al volver al menú.
        activeGameOverSfx = ThreadLocalRandom.current().nextBoolean() ? Sfx.GAMEOVER_1 : Sfx.GAMEOVER_2;
        SoundManager.play(activeGameOverSfx);
    }

    /**
     * Al ocultar la pantalla: detiene el jingle que esté sonando para que no se solape con el
     * audio de la siguiente pantalla.
     */
    @Override
    public void onHide() {
        if (activeGameOverSfx != null) {
            SoundManager.stop(activeGameOverSfx);
            activeGameOverSfx = null;
        }
    }

    /**
     * Pinta el panel del Top-3 con tres líneas formateadas "1. Nombre  N  resultado". Si la lista
     * tiene menos de 3 entradas (típico en la primera partida), pinta sólo las que haya.
     */
    private void renderTop3(List<RankingManager.RankingEntry> top) {
        top3Panel.removeAll();

        JLabel header = new JLabel("RANKING TOP 3", SwingConstants.CENTER);
        header.setFont(FontLoader.bold(16f));
        header.setForeground(CYAN);
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        top3Panel.add(header);
        top3Panel.add(Box.createVerticalStrut(8));

        if (top.isEmpty()) {
            JLabel vacio = new JLabel("(aún no hay partidas registradas)", SwingConstants.CENTER);
            vacio.setFont(FontLoader.regular(12f));
            vacio.setForeground(MUTED);
            vacio.setAlignmentX(Component.CENTER_ALIGNMENT);
            top3Panel.add(vacio);
        } else {
            for (int i = 0; i < top.size(); i++) {
                RankingManager.RankingEntry e = top.get(i);
                String line = String.format("%d.  %-15s  %4d   %s",
                        i + 1, e.playerName, e.score, nullToDash(e.result));
                JLabel lbl = new JLabel(line, SwingConstants.CENTER);
                lbl.setFont(FontLoader.regular(14f));
                lbl.setForeground(i == 0 ? CYAN : TEXT);
                lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                top3Panel.add(lbl);
                top3Panel.add(Box.createVerticalStrut(2));
            }
        }
        top3Panel.revalidate();
        top3Panel.repaint();
    }

    /** Formatea {@code ms} como {@code "Tiempo total: MM:SS"} para mostrar en el {@code detail}. */
    private static String formatDuration(long ms) {
        long sec = ms / 1000L;
        return String.format("Tiempo total: %02d:%02d", sec / 60L, sec % 60L);
    }

    /** Devuelve {@code s} o un guión largo cuando la cadena es {@code null} o solo espacios. */
    private static String nullToDash(String s) {
        return (s == null || s.isBlank()) ? "—" : s;
    }
}
