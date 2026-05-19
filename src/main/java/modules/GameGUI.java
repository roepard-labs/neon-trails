package modules;

import view.GameWindow;

/**
 * Fachada de arranque entre el {@code main} y la vista Swing.
 * <p>
 * NOTE: Aquí se puede enchufar más adelante el flujo de pantallas (menú, instrucciones, etc.).
 */
public class GameGUI {

    /**
     * Inicia la interfaz del juego.
     */
    public void iniciar() {
        new GameWindow().mostrar();
    }
}
