package view;

import logic.GameConstants;

/**
 * Bucle de simulación en un hilo dedicado (requisito académico: uso explícito de {@link Thread}).
 * <p>
 * Implementa el patrón "fixed-step game loop": cada iteración mide cuánto tomó el tick, duerme
 * la diferencia hasta completar {@link GameConstants#GAME_TICK_MS} ms y entonces vuelve a
 * empezar. Esto fija la frecuencia objetivo (~60 Hz) independientemente de la carga del repaint.
 * <p>
 * NOTE: [sustentación] El repintado se solicita desde este hilo (mediante
 * {@link javax.swing.JComponent#repaint()}) pero Swing siempre repinta en el EDT. El bucle no
 * dibuja; sólo avanza la simulación y pide al EDT que actualice la pantalla cuando pueda.
 *
 * @see GamePanel#stepSimulation()
 */
public class GameLoop implements Runnable {

    /** Bandera de parada {@code volatile} para que {@link #stop()} se vea desde el EDT al instante. */
    private volatile boolean running = true;
    private final GamePanel panel;

    public GameLoop(GamePanel panel) {
        this.panel = panel;
    }

    /**
     * Solicita al bucle que termine. Es seguro llamar desde otros hilos (EDT incluido) porque
     * {@code running} es {@code volatile}.
     */
    public void stop() {
        running = false;
    }

    /**
     * Cuerpo del bucle: avanza un tick, duerme lo necesario y repite.
     * <p>
     * NOTE: [sustentación] Si el tick es más rápido que {@link GameConstants#GAME_TICK_MS}, se
     * duerme la diferencia para no saturar la CPU. Si fuese más lento (frame jank), no se duerme
     * — preferimos perder framerate momentáneamente antes que acumular un backlog visual.
     */
    @Override
    public void run() {
        while (running) {
            long t0 = System.currentTimeMillis();
            panel.stepSimulation();
            long elapsed = System.currentTimeMillis() - t0;
            long sleep = GameConstants.GAME_TICK_MS - elapsed;
            if (sleep > 0) {
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    running = false;
                }
            }
        }
    }
}
