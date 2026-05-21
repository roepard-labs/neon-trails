package audio;

import javax.swing.AbstractButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Helpers estáticos para asociar feedback auditivo a controles Swing.
 * <p>
 * NOTE: Mantenido como utilidad para no duplicar el {@code addActionListener}/{@code MouseAdapter}
 * en cada pantalla.
 */
public final class UiSound {

    private UiSound() {
    }

    /**
     * Añade un listener al botón que reproduce {@link Sfx#UI_CLICK} cuando se activa.
     * El sonido se dispara antes del cambio de pantalla porque {@code AudioSystem} no bloquea.
     */
    public static void attachClick(AbstractButton button) {
        button.addActionListener(e -> SoundManager.play(Sfx.UI_CLICK));
    }

    /**
     * Añade un listener al botón que reproduce {@link Sfx#UI_HOVER} cuando el cursor entra.
     * <p>
     * NOTE: No se invoca por defecto en las pantallas (puede resultar molesto en exploración de
     * menús). Activar manualmente desde la pantalla si se desea.
     */
    public static void attachHover(AbstractButton button) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                SoundManager.play(Sfx.UI_HOVER);
            }
        });
    }
}
