import modules.GameGUI;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada del juego Neon Trails.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameGUI().iniciar());
    }
}
