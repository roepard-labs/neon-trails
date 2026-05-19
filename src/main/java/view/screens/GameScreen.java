package view.screens;

import logic.GameSession;
import view.BaseScreen;
import view.GamePanel;

import java.awt.BorderLayout;

/**
 * Envoltorio de pantalla para {@link GamePanel}.
 * <p>
 * NOTE: Esta pantalla controla el ciclo de vida del hilo de simulación (arranca en {@link #onShow()},
 * detiene en {@link #onHide()}) porque {@code CardLayout} sólo oculta paneles, no los desmonta —
 * así evitamos que el bucle siga corriendo en GameOver o tras volver al menú.
 */
public class GameScreen extends BaseScreen {

    private final GamePanel panel = new GamePanel();

    public GameScreen() {
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        panel.setOnGameOver(this::onGameOver);
    }

    @Override
    public void onShow() {
        GameSession session = screens().getSession();
        panel.setSession(session);
        panel.resetGame();
        panel.startLoop();
        panel.requestFocusInWindow();
    }

    @Override
    public void onHide() {
        panel.stopLoop();
    }

    private void onGameOver(int winnerId) {
        GameSession session = screens().getSession();
        session.setWinnerId(winnerId);
        screens().mostrar("gameover");
    }
}
