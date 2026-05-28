package modules;

import view.GameWindow;

/**
 * Fachada de arranque entre el {@code main} y la vista Swing.
 * <p>
 * Es la capa intermedia que permite que {@link Main} no conozca directamente las clases del
 * paquete {@code view/}. Aquí se pueden enchufar futuras decisiones de boot (selección de
 * idioma, splash screen, comprobaciones de assets) sin tocar el punto de entrada.
 * <p>
 * NOTE: [sustentación] Esta clase cubre el criterio de "organización en módulos" del PDF: el
 * paquete {@code modules/} contiene las fachadas, separadas tanto de la lógica como de la vista.
 *
 * @see view.GameWindow
 */
public class GameGUI {

    /**
     * Inicia la interfaz del juego: construye y muestra la {@link GameWindow}.
     * <p>
     * Debe invocarse desde el EDT (lo hace {@link Main#main(String[])}).
     */
    public void iniciar() {
        new GameWindow().mostrar();
    }
}
