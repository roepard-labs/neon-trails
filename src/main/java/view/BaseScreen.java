package view;

import javax.swing.JPanel;

/**
 * Pantalla base del flujo Swing: extiende {@link JPanel} y expone hooks de ciclo de vida.
 * <p>
 * NOTE: {@link #onShow()} y {@link #onHide()} se invocan desde el EDT por {@link ScreenManager};
 * cada pantalla concreta es libre de iniciar/detener sus propios {@code Swing Timer} dentro de ellos.
 * <p>
 * NOTE: [sustentación POO: Herencia] Cima de la jerarquía de pantallas: {@code WelcomeScreen},
 * {@code InstructionsScreen}, {@code NameInputScreen}, {@code GameScreen} y {@code GameOverScreen}
 * heredan de esta clase abstracta y reutilizan {@link #attachManager(ScreenManager)},
 * {@link #screens()} y los hooks {@link #onShow()}/{@link #onHide()} sin redeclarar el contrato.
 */
public abstract class BaseScreen extends JPanel {

    private ScreenManager screenManager;

    /**
     * Inyecta el orquestador para permitir navegación desde la pantalla.
     * Lo llama {@link ScreenManager#register(String, BaseScreen)}.
     */
    final void attachManager(ScreenManager screenManager) {
        this.screenManager = screenManager;
    }

    /**
     * Acceso al orquestador de pantallas para navegar.
     */
    protected final ScreenManager screens() {
        return screenManager;
    }

    /**
     * Llamado cuando esta pantalla se vuelve visible. Hook por defecto vacío.
     * <p>
     * NOTE: [sustentación POO: Herencia] Hook que las subclases concretas sobreescriben para
     * refrescar UI o arrancar {@code Swing Timer} al entrar a la pantalla.
     */
    public void onShow() {
        // NOTE: las subclases sobreescriben para refrescar UI o arrancar timers.
    }

    /**
     * Llamado cuando esta pantalla se oculta. Hook por defecto vacío.
     * <p>
     * NOTE: [sustentación POO: Herencia] Hook que las subclases concretas sobreescriben para
     * liberar {@code Swing Timer} o detener el bucle de juego al salir de la pantalla.
     */
    public void onHide() {
        // NOTE: las subclases sobreescriben para liberar timers o pausar el bucle.
    }
}
