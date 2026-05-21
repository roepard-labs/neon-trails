package events;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;

/**
 * Registra atajos de teclado con {@link InputMap}/{@link ActionMap} en el componente raíz.
 * <p>
 * NOTE: {@code WHEN_IN_FOCUSED_WINDOW} permite que ambos jugadores reciban teclas aunque el foco
 * cambie entre controles internos.
 */
public final class KeyboardBindings {

    private KeyboardBindings() {
    }

    /**
     * Instala las acciones de teclado.
     *
     * @param root   panel o ventana contenedora
     * @param input  controlador a mutar
     */
    public static void install(JComponent root, InputController input) {
        int cond = JComponent.WHEN_IN_FOCUSED_WINDOW;
        InputMap im = root.getInputMap(cond);
        ActionMap am = root.getActionMap();

        bindHold(im, am, "P1_LEFT_PRESS", "P1_LEFT_RELEASE", KeyStroke.getKeyStroke("pressed A"),
                KeyStroke.getKeyStroke("released A"), () -> input.setP1Left(true), () -> input.setP1Left(false));
        bindHold(im, am, "P1_RIGHT_PRESS", "P1_RIGHT_RELEASE", KeyStroke.getKeyStroke("pressed D"),
                KeyStroke.getKeyStroke("released D"), () -> input.setP1Right(true), () -> input.setP1Right(false));
        bindHold(im, am, "P1_UP_PRESS", "P1_UP_RELEASE", KeyStroke.getKeyStroke("pressed W"),
                KeyStroke.getKeyStroke("released W"), () -> input.setP1Up(true), () -> input.setP1Up(false));
        bindHold(im, am, "P1_DOWN_PRESS", "P1_DOWN_RELEASE", KeyStroke.getKeyStroke("pressed S"),
                KeyStroke.getKeyStroke("released S"), () -> input.setP1Down(true), () -> input.setP1Down(false));

        bindHold(im, am, "P1_FIRE_PRESS", "P1_FIRE_RELEASE", KeyStroke.getKeyStroke("pressed E"),
                KeyStroke.getKeyStroke("released E"),
                () -> input.setP1Shoot(true), () -> input.setP1Shoot(false));

        bindEdge(im, am, "P1_BIKE", KeyStroke.getKeyStroke("pressed Q"), input::requestP1Bike);

        bindHold(im, am, "P2_LEFT_PRESS", "P2_LEFT_RELEASE", KeyStroke.getKeyStroke("pressed LEFT"),
                KeyStroke.getKeyStroke("released LEFT"), () -> input.setP2Left(true), () -> input.setP2Left(false));
        bindHold(im, am, "P2_RIGHT_PRESS", "P2_RIGHT_RELEASE", KeyStroke.getKeyStroke("pressed RIGHT"),
                KeyStroke.getKeyStroke("released RIGHT"), () -> input.setP2Right(true), () -> input.setP2Right(false));
        bindHold(im, am, "P2_UP_PRESS", "P2_UP_RELEASE", KeyStroke.getKeyStroke("pressed UP"),
                KeyStroke.getKeyStroke("released UP"), () -> input.setP2Up(true), () -> input.setP2Up(false));
        bindHold(im, am, "P2_DOWN_PRESS", "P2_DOWN_RELEASE", KeyStroke.getKeyStroke("pressed DOWN"),
                KeyStroke.getKeyStroke("released DOWN"), () -> input.setP2Down(true), () -> input.setP2Down(false));

        bindHold(im, am, "P2_FIRE_PRESS", "P2_FIRE_RELEASE", KeyStroke.getKeyStroke("pressed ENTER"),
                KeyStroke.getKeyStroke("released ENTER"), () -> input.setP2Shoot(true), () -> input.setP2Shoot(false));

        bindEdge(im, am, "P2_BIKE", KeyStroke.getKeyStroke("pressed U"), input::requestP2Bike);
    }

    private static void bindHold(InputMap im, ActionMap am, String pressKey, String releaseKey,
            KeyStroke pressStroke, KeyStroke releaseStroke, Runnable onPress, Runnable onRelease) {
        im.put(pressStroke, pressKey);
        im.put(releaseStroke, releaseKey);
        am.put(pressKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onPress.run();
            }
        });
        am.put(releaseKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRelease.run();
            }
        });
    }

    private static void bindEdge(InputMap im, ActionMap am, String key, KeyStroke stroke, Runnable onFire) {
        im.put(stroke, key);
        am.put(key, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onFire.run();
            }
        });
    }
}
