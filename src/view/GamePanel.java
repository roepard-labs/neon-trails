package view;

import events.InputController;
import events.KeyboardBindings;
import logic.DiscProjectile;
import logic.GameConstants;
import logic.GameState;
import logic.Player;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

/**
 * Panel principal: entrada, simulación y renderizado.
 */
public class GamePanel extends JPanel {

    private final Object stateLock = new Object();
    private final InputController input = new InputController();
    private final GameState gameState;
    private GameLoop gameLoop;
    private Thread loopThread;

    public GamePanel() {
        setBackground(new Color(0x0a, 0x0a, 0x12));
        setPreferredSize(new Dimension(GameConstants.DEFAULT_WIDTH, GameConstants.DEFAULT_HEIGHT));
        setFocusable(true);
        this.gameState = new GameState(GameConstants.DEFAULT_WIDTH, GameConstants.DEFAULT_HEIGHT);
        KeyboardBindings.install(this, input);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
        if (loopThread == null || !loopThread.isAlive()) {
            this.gameLoop = new GameLoop(this);
            loopThread = new Thread(gameLoop, "neon-trails-game-loop");
            loopThread.setDaemon(true);
            loopThread.start();
        }
    }

    @Override
    public void removeNotify() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        super.removeNotify();
    }

    /**
     * Un tick de simulación; invocado desde el hilo de juego.
     */
    public void stepSimulation() {
        int w = Math.max(getWidth(), GameConstants.DEFAULT_WIDTH / 4);
        int h = Math.max(getHeight(), GameConstants.DEFAULT_HEIGHT / 4);
        synchronized (stateLock) {
            gameState.tick(input, w, h);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        drawBorder(g2, w, h);
        synchronized (stateLock) {
            drawDiscs(g2, gameState.getDiscs());
            drawPlayer(g2, gameState.getPlayerOne(), "P1");
            drawPlayer(g2, gameState.getPlayerTwo(), "P2");
            drawHud(g2, w, h);
        }
        g2.dispose();
    }

    private static void drawBorder(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(0x33, 0xff, 0xff, 80));
        g2.setStroke(new BasicStroke(3f));
        g2.drawRect(2, 2, w - 4, h - 4);
    }

    private void drawPlayer(Graphics2D g2, Player p, String label) {
        int half = GameConstants.PLAYER_SIZE / 2;
        int px = (int) Math.round(p.getX()) - half;
        int py = (int) Math.round(p.getY()) - half;
        if (p.isOnBike()) {
            g2.setColor(new Color(255, 255, 255, 60));
            g2.fillOval(px - 6, py - 6, GameConstants.PLAYER_SIZE + 12, GameConstants.PLAYER_SIZE + 12);
        }
        g2.setColor(p.getColor());
        g2.fillRoundRect(px, py, GameConstants.PLAYER_SIZE, GameConstants.PLAYER_SIZE, 8, 8);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 11));
        g2.drawString(label, px + 3, py + 15);
    }

    private void drawDiscs(Graphics2D g2, List<DiscProjectile> discs) {
        for (DiscProjectile d : discs) {
            int r = GameConstants.DISC_RADIUS;
            int cx = (int) Math.round(d.getX()) - r;
            int cy = (int) Math.round(d.getY()) - r;
            g2.setColor(new Color(0xaa, 0xff, 0xff));
            g2.fillOval(cx, cy, r * 2, r * 2);
        }
    }

    private void drawHud(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(0xee, 0xee, 0xff));
        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        Player p1 = gameState.getPlayerOne();
        Player p2 = gameState.getPlayerTwo();
        g2.drawString("P1 puntos: " + p1.getScore() + (p1.isOnBike() ? "  [MOTO]" : ""), 12, 22);
        g2.drawString("P2 puntos: " + p2.getScore() + (p2.isOnBike() ? "  [MOTO]" : ""), 12, 40);
        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        String help = "P1: WASD | disco: Shift | moto 5s: Q   —   P2: flechas | disco: Enter | moto 5s: U";
        g2.drawString(help, 12, h - 12);
    }
}
