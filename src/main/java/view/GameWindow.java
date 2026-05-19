package view;

import view.screens.GameOverScreen;
import view.screens.GameScreen;
import view.screens.NameInputScreen;
import view.screens.WelcomeScreen;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Dimension;

/**
 * Ventana principal del juego (capa vista). Aloja el {@link ScreenManager}
 * y registra las pantallas del flujo: bienvenida → captura de nombres → partida → game over.
 */
public class GameWindow {

    /**
     * Construye y muestra la ventana en el hilo de despacho de Swing.
     */
    public void mostrar() {
        JFrame frame = new JFrame("Neon Trails");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        ScreenManager screens = new ScreenManager();
        screens.register("welcome", new WelcomeScreen());
        screens.register("nameinput", new NameInputScreen());
        screens.register("game", new GameScreen());
        screens.register("gameover", new GameOverScreen());

        frame.setContentPane(screens.getContainer());
        frame.setMinimumSize(new Dimension(640, 480));
        frame.setPreferredSize(new Dimension(960, 640));
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        screens.mostrar("welcome");
    }
}
