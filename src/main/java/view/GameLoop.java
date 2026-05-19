package view;

import logic.GameConstants;

/**
 * Bucle de simulación en un hilo dedicado (requisito académico: uso de {@link Thread}).
 * <p>
 * NOTE: El repintado se solicita desde este hilo; Swing repinta en el EDT.
 */
public class GameLoop implements Runnable {

    private volatile boolean running = true;
    private final GamePanel panel;

    public GameLoop(GamePanel panel) {
        this.panel = panel;
    }

    public void stop() {
        running = false;
    }

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
