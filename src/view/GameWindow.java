package view;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * Ventana principal del juego (capa vista).
 */
public class GameWindow {

    /**
     * Construye y muestra la ventana en el hilo de despacho de Swing.
     */
    public void mostrar() {
        JFrame frame = new JFrame("Neon Trails — base jugable");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        GamePanel panel = new GamePanel();
        frame.setContentPane(panel);
        frame.pack();
        frame.setMinimumSize(new java.awt.Dimension(640, 480));
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        panel.requestFocusInWindow();
    }
}
