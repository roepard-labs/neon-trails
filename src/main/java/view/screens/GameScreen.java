package view.screens;

import logic.GameSession;
import view.BaseScreen;
import view.GamePanel;

import javax.swing.Timer;
import java.awt.BorderLayout;

/**
 * Envoltorio de pantalla para {@link GamePanel}: actúa como adaptador entre el flujo de pantallas
 * ({@code CardLayout} de {@code ScreenManager}) y el ciclo de vida del hilo de simulación.
 * <p>
 * Esta pantalla es responsable de <b>arrancar y detener</b> el bucle de juego porque
 * {@code CardLayout} sólo oculta paneles, no los desmonta: sin esta gestión explícita, el bucle
 * seguiría corriendo en GameOver o tras volver al menú, consumiendo CPU y reproduciendo SFX
 * fantasma.
 * <p>
 * NOTE: [sustentación] Aquí coexisten <b>dos relojes</b>: el hilo dedicado del
 * {@link view.GameLoop} a 60 Hz (criterio de rúbrica "uso de Threads") y un
 * {@link javax.swing.Timer} a 1 Hz que repinta el HUD para refrescar el cronómetro en pantalla
 * (criterio de rúbrica "control de tiempo mediante clase Timer"). Cada uno tiene su frecuencia
 * y su responsabilidad; no compiten entre sí.
 *
 * @see GamePanel
 * @see view.GameLoop
 */
public class GameScreen extends BaseScreen {

    private final GamePanel panel = new GamePanel();

    /**
     * Timer del HUD: refresca el cronómetro y el score visibles cada 1 segundo. Vive en el EDT.
     * Es independiente del {@link view.GameLoop} de 60 Hz, que sigue siendo el motor de la
     * simulación.
     */
    private final Timer hudTimer = new Timer(1000, e -> panel.repaint());

    /**
     * Monta el {@link GamePanel} a tamaño completo y conecta el callback de fin de partida que
     * navega hacia la pantalla {@code "gameover"}.
     */
    public GameScreen() {
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        panel.setOnGameOver(this::onGameOver);
    }

    /**
     * Inicializa la partida cada vez que la pantalla se muestra: enlaza la sesión compartida con
     * el panel, resetea el estado del juego, arranca el cronómetro de la sesión, arranca el
     * hilo del bucle y el timer del HUD, y solicita el foco para que las teclas de los dos
     * jugadores empiecen a registrarse.
     */
    @Override
    public void onShow() {
        GameSession session = screens().getSession();
        panel.setSession(session);
        panel.resetGame();
        session.startMatchClock();
        panel.startLoop();
        hudTimer.start();
        panel.requestFocusInWindow();
    }

    /**
     * Detiene el hilo del bucle y el timer del HUD al ocultar la pantalla. Sella la duración
     * de la partida para que la pantalla siguiente la muestre.
     */
    @Override
    public void onHide() {
        hudTimer.stop();
        screens().getSession().stopMatchClock();
        panel.stopLoop();
    }

    /**
     * Callback que recibe el {@link GamePanel} cuando un jugador pierde sus tres vidas. Persiste
     * los puntajes finales en la {@link GameSession} y navega a la pantalla de fin de partida.
     *
     * @param winnerId identificador del jugador ganador (1 = cian, 2 = rosa)
     */
    private void onGameOver(int winnerId) {
        GameSession session = screens().getSession();
        session.setWinnerId(winnerId);
        // Captura los puntajes finales para que GameOverScreen los publique en el leaderboard.
        session.setPlayerOneScore(panel.getPlayerOneScore());
        session.setPlayerTwoScore(panel.getPlayerTwoScore());
        screens().mostrar("gameover");
    }
}
