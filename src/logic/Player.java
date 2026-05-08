package logic;

import java.awt.Color;

/**
 * Jugador controlable: posición, dirección de movimiento, disparo y modo moto temporal.
 */
public class Player {

    private final int id;
    private final Color color;
    private double x;
    private double y;
    /** Dirección de movimiento actual (-1, 0, 1). */
    private int moveX;
    private int moveY;
    /** Última dirección de avance usada para orientar el disco. */
    private int facingX = 1;
    private int facingY = 0;
    private int fireCooldownTicks;
    /** Fin del modo moto en nanosegundos desde {@link System#nanoTime()}. */
    private long bikeUntilNanos;
    private int score;

    /**
     * @param id identificador 1 o 2
     * @param color color de render
     * @param spawnX posición inicial X
     * @param spawnY posición inicial Y
     */
    public Player(int id, Color color, double spawnX, double spawnY) {
        this.id = id;
        this.color = color;
        this.x = spawnX;
        this.y = spawnY;
    }

    public int getId() {
        return id;
    }

    public Color getColor() {
        return color;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public int getMoveX() {
        return moveX;
    }

    public int getMoveY() {
        return moveY;
    }

    public void setMove(int mx, int my) {
        this.moveX = mx;
        this.moveY = my;
        if (mx != 0 || my != 0) {
            facingX = mx;
            facingY = my;
        }
    }

    public int getFacingX() {
        return facingX;
    }

    public int getFacingY() {
        return facingY;
    }

    public int getFireCooldownTicks() {
        return fireCooldownTicks;
    }

    public void setFireCooldownTicks(int fireCooldownTicks) {
        this.fireCooldownTicks = fireCooldownTicks;
    }

    public void tickCooldown() {
        if (fireCooldownTicks > 0) {
            fireCooldownTicks--;
        }
    }

    /**
     * Activa la moto durante {@link GameConstants#BIKE_DURATION_SEC} segundos.
     */
    public void activateBike() {
        long now = System.nanoTime();
        bikeUntilNanos = now + (long) (GameConstants.BIKE_DURATION_SEC * 1_000_000_000L);
    }

    /**
     * @return true si el jugador está en modo moto (velocidad aumentada).
     */
    public boolean isOnBike() {
        return System.nanoTime() < bikeUntilNanos;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int delta) {
        this.score += delta;
    }

    /**
     * Avanza la posición respetando límites del rectángulo de juego.
     *
     * @param width  ancho del mundo
     * @param height alto del mundo
     */
    public void moveWithinBounds(int width, int height) {
        double speed = GameConstants.PLAYER_SPEED * (isOnBike() ? GameConstants.BIKE_SPEED_MULT : 1.0);
        double nx = x + moveX * speed;
        double ny = y + moveY * speed;
        int half = GameConstants.PLAYER_SIZE / 2;
        nx = clamp(nx, half, width - half);
        ny = clamp(ny, half, height - half);
        x = nx;
        y = ny;
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
