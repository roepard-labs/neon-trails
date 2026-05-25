import audio.SoundManager;
import modules.GameGUI;
import view.FontLoader;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada del juego Neon Trails.
 */
public class Main {

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
