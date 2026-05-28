package view;

import logic.GameSession;
import logic.RankingManager;

import javax.swing.JPanel;
import java.awt.CardLayout;
import java.util.HashMap;
import java.util.Map;

/**
 * Orquestador de pantallas basado en {@link CardLayout}.
 * <p>
 * NOTE: Mantiene la sesión compartida ({@link GameSession}) accesible a todas las pantallas
 * para evitar pasar datos sin tipo entre ellas.
 */
public class ScreenManager {

    private final JPanel container;
    private final CardLayout cards = new CardLayout();
    private final Map<String, BaseScreen> screens = new HashMap<>();
    private final GameSession session = new GameSession();
    /** Ranking persistente compartido entre pantallas (lectura desde GameOver, escritura en partidas). */
    private final RankingManager ranking = new RankingManager();
    private BaseScreen current;

    public ScreenManager() {
        this.container = new JPanel(cards);
    }

    /**
     * Panel raíz a colocar en el {@code JFrame}.
     */
    public JPanel getContainer() {
        return container;
    }

    /**
     * Registra una pantalla bajo un nombre. La inyecta con la referencia al manager.
     */
    public void register(String name, BaseScreen screen) {
        screen.attachManager(this);
        screens.put(name, screen);
        container.add(screen, name);
    }

    /**
     * Muestra la pantalla por nombre, disparando los hooks de ciclo de vida.
     *
     * @param name nombre registrado previamente con {@link #register(String, BaseScreen)}
     */
    public void mostrar(String name) {
        BaseScreen next = screens.get(name);
        if (next == null) {
            throw new IllegalArgumentException("Pantalla no registrada: " + name);
        }
        if (current != null) {
            current.onHide();
        }
        cards.show(container, name);
        current = next;
        current.onShow();
        // NOTE: pedir foco para que las pantallas con teclado lo reciban tras el cambio.
        current.requestFocusInWindow();
    }

    /**
     * Sesión de juego compartida entre pantallas.
     */
    public GameSession getSession() {
        return session;
    }

    /**
     * Ranking persistente compartido entre pantallas: lo escribe {@code GameOverScreen} al
     * terminar la partida y lo lee la misma pantalla para mostrar el Top-3.
     */
    public RankingManager getRanking() {
        return ranking;
    }
}
