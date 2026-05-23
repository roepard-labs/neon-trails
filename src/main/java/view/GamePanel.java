package view;

import audio.AudioGameEventListener;
import events.InputController;
import events.KeyboardBindings;
import logic.DiscProjectile;
import logic.GameConstants;
import logic.GameEventListener;
import logic.GameSession;
import logic.GameState;
import logic.Player;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * Panel principal: entrada, simulación y renderizado.
 * <p>
 * NOTE: A diferencia de versiones anteriores, este panel NO arranca el hilo de juego al adjuntarse.
 * La pantalla contenedora ({@code GameScreen}) decide cuándo iniciar y detener el bucle, para que el
 * ciclo de vida del hilo encaje con el {@link java.awt.CardLayout} (que oculta paneles en vez de
 * desmontarlos).
 */
public class GamePanel extends JPanel {

    private final Object stateLock = new Object();
    private final InputController input = new InputController();
    private final GameEventListener gameEventListener = new AudioGameEventListener();
    private final GameState gameState;
    private GameSession session;
    private GameLoop gameLoop;
    private Thread loopThread;
    private IntConsumer onGameOver;
    private boolean gameOverFired;

    public GamePanel() {
        setBackground(new Color(0x0a, 0x0a, 0x12));
        setPreferredSize(new Dimension(GameConstants.DEFAULT_WIDTH, GameConstants.DEFAULT_HEIGHT));
        setFocusable(true);
        this.gameState = new GameState(GameConstants.DEFAULT_WIDTH, GameConstants.DEFAULT_HEIGHT,
                gameEventListener);
        KeyboardBindings.install(this, input);
    }

    /**
     * Asocia la sesión activa para que el HUD muestre los nombres correctos.
     */
    public void setSession(GameSession session) {
        this.session = session;
    }

    /**
     * Registra el callback que se dispara una vez cuando un jugador llega a 0 vidas.
     *
     * @param onGameOver recibe el id (1 o 2) del jugador ganador
     */
    public void setOnGameOver(IntConsumer onGameOver) {
        this.onGameOver = onGameOver;
    }

    /**
     * Reinicia el estado del juego para una nueva partida.
     */
    public void resetGame() {
        synchronized (stateLock) {
            gameState.reset();
        }
        gameOverFired = false;
        repaint();
    }

    /**
     * Arranca el bucle de simulación en un hilo dedicado. Idempotente.
     */
    public void startLoop() {
        if (loopThread != null && loopThread.isAlive()) {
            return;
        }
        this.gameLoop = new GameLoop(this);
        loopThread = new Thread(gameLoop, "neon-trails-game-loop");
        loopThread.setDaemon(true);
        loopThread.start();
    }

    /**
     * Solicita la detención del bucle. No bloquea.
     */
    public void stopLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    @Override
    public void removeNotify() {
        stopLoop();
        super.removeNotify();
    }

    /**
     * Un tick de simulación; invocado desde el hilo de juego.
     */
    public void stepSimulation() {
        int w = Math.max(getWidth(), GameConstants.DEFAULT_WIDTH / 4);
        int h = Math.max(getHeight(), GameConstants.DEFAULT_HEIGHT / 4);
        boolean justFinished;
        int winnerId;
        synchronized (stateLock) {
            gameState.tick(input, w, h);
            justFinished = gameState.isFinished() && !gameOverFired;
            winnerId = gameState.getWinnerId();
            if (justFinished) {
                gameOverFired = true;
            }
        }
        repaint();
        if (justFinished && onGameOver != null) {
            // NOTE: notificar en EDT; la pantalla cambiará de card y debe correr en Swing.
            IntConsumer cb = onGameOver;
            SwingUtilities.invokeLater(() -> cb.accept(winnerId));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        drawArenaFloor(g2, w, h);
        drawBorder(g2, w, h);
        synchronized (stateLock) {
            drawTrails(g2, gameState.getPlayerOne(), gameState.getPlayerTwo());
            drawDiscs(g2, gameState.getDiscs());
            drawPlayer(g2, gameState.getPlayerOne(), labelFor(1));
            drawPlayer(g2, gameState.getPlayerTwo(), labelFor(2));
            drawHud(g2, w, h);
        }
        g2.dispose();
    }

    private static void drawArenaFloor(Graphics2D g2, int w, int h) {
        if (w <= 0 || h <= 0) {
            return;
        }
        BufferedImage floor = SpriteLoader.load("arena-floor-vaporwave.svg", w, h);
        g2.drawImage(floor, 0, 0, null);
    }

    private static void drawTrails(Graphics2D g2, Player p1, Player p2) {
        drawPlayerTrail(g2, p1);
        drawPlayerTrail(g2, p2);
    }

    private static void drawPlayerTrail(Graphics2D g2, Player p) {
        List<Point2D.Double> trail = p.getMotoTrail();
        if (trail.size() < 2) {
            return;
        }
        g2.setColor(p.getColor());
        g2.setStroke(new BasicStroke(GameConstants.TRAIL_LINE_WIDTH,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D.Double path = new Path2D.Double();
        Point2D.Double first = trail.get(0);
        path.moveTo(first.x, first.y);
        for (int i = 1; i < trail.size(); i++) {
            Point2D.Double pt = trail.get(i);
            path.lineTo(pt.x, pt.y);
        }
        g2.draw(path);
    }

    private String labelFor(int id) {
        if (session == null) {
            return id == 1 ? "P1" : "P2";
        }
        return id == 1 ? session.getPlayerOneName() : session.getPlayerTwoName();
    }

    private static void drawBorder(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(0x33, 0xff, 0xff, 80));
        g2.setStroke(new BasicStroke(3f));
        g2.drawRect(2, 2, w - 4, h - 4);
    }

    private void drawPlayer(Graphics2D g2, Player p, String label) {
        // NOTE: Sprite del handoff a 2x el lado lógico (PLAYER_SIZE=22 → 44 px) para que el glow
        // SMIL no quede recortado. El hitbox lógico sigue siendo 22, solo el render se infla.
        int spriteSize = GameConstants.PLAYER_SIZE * 2;
        int half = spriteSize / 2;
        int cx = (int) Math.round(p.getX());
        int cy = (int) Math.round(p.getY());

        if (p.isOnBike()) {
            // Aura translúcida bajo el sprite para reforzar visualmente el modo moto.
            g2.setColor(new Color(255, 255, 255, 60));
            g2.fillOval(cx - half - 4, cy - half - 4, spriteSize + 8, spriteSize + 8);
        }

        String spriteName = (p.getId() == 1 ? "p1" : "p2") + (p.isOnBike() ? "-moto" : "-normal") + ".svg";
        BufferedImage sprite = SpriteLoader.load(spriteName, spriteSize);
        // Rotación: el SVG apunta al norte (facing 0,-1 = ángulo 0).
        double angle = Math.atan2(p.getFacingX(), -p.getFacingY());
        AffineTransform old = g2.getTransform();
        g2.rotate(angle, cx, cy);
        g2.drawImage(sprite, cx - half, cy - half, null);
        g2.setTransform(old);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 11));
        g2.drawString(label, cx - half + 3, cy - half - 4);
    }

    private static void drawDiscs(Graphics2D g2, List<DiscProjectile> discs) {
        // Tamaño visible del disco: 4x el radio lógico para que el glow del sprite no se recorte.
        int spriteSize = GameConstants.DISC_RADIUS * 4;
        int half = spriteSize / 2;
        for (DiscProjectile d : discs) {
            int cx = (int) Math.round(d.getX());
            int cy = (int) Math.round(d.getY());
            String state = d.isStuck() ? "stuck" : "active";
            String spriteName = "disc-p" + d.getOwnerId() + "-" + state + ".svg";
            BufferedImage sprite = SpriteLoader.load(spriteName, spriteSize);
            g2.drawImage(sprite, cx - half, cy - half, null);
        }
    }

    private void drawHud(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(0xee, 0xee, 0xff));
        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        Player p1 = gameState.getPlayerOne();
        Player p2 = gameState.getPlayerTwo();
        g2.drawString(labelFor(1) + "  " + hearts(p1.getLives()) + (p1.isOnBike() ? "  [MOTO]" : ""), 12, 22);
        g2.drawString(labelFor(2) + "  " + hearts(p2.getLives()) + (p2.isOnBike() ? "  [MOTO]" : ""), 12, 40);
        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        String help = "P1: WASD | disco: E | moto 5s: Q   —   P2: flechas | disco: Enter | moto 5s: U";
        g2.drawString(help, 12, h - 12);
    }

    /** Representación visual de vidas como serie de corazones llenos / vacíos. */
    private static String hearts(int lives) {
        int total = Player.INITIAL_LIVES;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < total; i++) {
            sb.append(i < lives ? '♥' : '♡');
        }
        return sb.toString();
    }
}
