import audio.SoundManager;
import modules.GameGUI;
import view.FontLoader;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada del juego Neon Trails.
 * <p>
 * Responsabilidad mínima: encolar el arranque de Swing en el Event Dispatch Thread (EDT) y
 * delegar la construcción de la ventana a {@link GameGUI}. La inicialización de los recursos
 * pesados (fuentes TTF y SFX) se hace antes de mostrar la ventana para evitar jitter en el
 * primer render y en el primer disparo.
 */
public class Main {

    /**
     * Arranca el juego.
     * <p>
     * NOTE: [sustentación] {@link SwingUtilities#invokeLater(Runnable)} garantiza que todos los
     * componentes Swing se crean en el EDT — requisito documentado de Swing para evitar
     * condiciones de carrera al construir {@code JFrame}, {@code JPanel} y sus hijos.
     * <p>
     * NOTE: La precarga de {@link FontLoader} y {@link SoundManager} en este hilo es síncrona,
     * pero ocurre antes de mostrar la ventana, así que el usuario no percibe la espera. En
     * entornos sin display (CI / headless) ambos sistemas degradan a modo no-op silencioso.
     *
     * @param args argumentos de línea de comandos (no se usan)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // NOTE: precarga de fuentes y SFX antes de mostrar la ventana para evitar jitter en el
            // primer render/disparo. En headless / CI ambos degradan a modo no-op silencioso.
            FontLoader.preloadAll();
            SoundManager.preloadAll();
            new GameGUI().iniciar();
        });
    }
}
