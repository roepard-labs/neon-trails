import audio.SoundManager;
import modules.GameGUI;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada del juego Neon Trails.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // NOTE: precarga de SFX antes de mostrar la ventana para evitar jitter en el primer
            // disparo. Si no hay mixer (headless / CI), el manager entra en modo no-op silencioso.
            SoundManager.preloadAll();
            new GameGUI().iniciar();
        });
    }
}
