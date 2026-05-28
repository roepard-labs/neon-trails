package logic;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Jugador controlable: posición, dirección de movimiento, disparo y modo moto temporal.
 */
public class Player {

    /** Vidas iniciales por partida (requisito: condición de victoria por agotamiento de vidas). */
    public static final int INITIAL_LIVES = 3;

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
    private int lives = INITIAL_LIVES;
    /** Puntos del rastro del modo moto en orden de grabación (cola = más viejo, cabeza = más nuevo). */
    private final List<Point2D.Double> motoTrail = new ArrayList<>();
    /** Fin de la ventana de invulnerabilidad post-hit del rastro, en escala {@link System#nanoTime()}. */
    private long trailInvulnUntilNanos;
    /** True mientras el rastro se está erosionando (la moto expiró y los puntos van desapareciendo). */
    private boolean trailEroding;

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

    /** @return identificador del jugador (1 = cian, 2 = rosa). */
    public int getId() {
        return id;
    }

    /** @return color base del jugador (cian para P1, rosa para P2). */
    public Color getColor() {
        return color;
    }

    /** @return coordenada X del centro del jugador en píxeles. */
    public double getX() {
        return x;
    }

    /** @return coordenada Y del centro del jugador en píxeles. */
    public double getY() {
        return y;
    }

    /** Reposiciona el jugador (usado en respawn tras recibir daño). */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /** @return componente horizontal de la dirección de movimiento (-1, 0 o 1). */
    public int getMoveX() {
        return moveX;
    }

    /** @return componente vertical de la dirección de movimiento (-1, 0 o 1). */
    public int getMoveY() {
        return moveY;
    }

    /**
     * Fija la dirección de movimiento del próximo tick y actualiza la orientación
     * ({@code facing}) si la nueva dirección no es nula. La orientación se usa para
     * decidir hacia dónde sale disparado el disco.
     *
     * @param mx -1 (izq), 0 (sin movimiento horizontal) o 1 (der)
     * @param my -1 (arriba), 0 (sin movimiento vertical) o 1 (abajo)
     */
    public void setMove(int mx, int my) {
        this.moveX = mx;
        this.moveY = my;
        if (mx != 0 || my != 0) {
            facingX = mx;
            facingY = my;
        }
    }

    /** @return componente X de la última dirección de avance no nula (orientación del disparo). */
    public int getFacingX() {
        return facingX;
    }

    /** @return componente Y de la última dirección de avance no nula (orientación del disparo). */
    public int getFacingY() {
        return facingY;
    }

    /** @return ticks restantes de enfriamiento de disparo (≥0); 0 significa "puede disparar ya". */
    public int getFireCooldownTicks() {
        return fireCooldownTicks;
    }

    /** Fija el enfriamiento de disparo (en ticks). Llamado por {@link GameState} al disparar. */
    public void setFireCooldownTicks(int fireCooldownTicks) {
        this.fireCooldownTicks = fireCooldownTicks;
    }

    /** Decrementa el enfriamiento de disparo en un tick (nunca por debajo de 0). */
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
        bikeUntilNanos = now + (long) (GameConstants.BIKE_DURATION_SEC * GameConstants.NANOS_PER_SECOND);
    }

    /**
     * Ajusta directamente el instante en que termina el modo moto.
     * <p>
     * NOTE: Visibilidad de paquete a propósito: sólo para pruebas dentro de {@code logic/}
     * (forzar expiración inmediata sin esperar 5 s reales). No usar desde {@code view/}, {@code audio/}
     * ni {@code events/}.
     *
     * @param nanos valor absoluto en escala de {@link System#nanoTime()}
     */
    void setBikeUntilNanos(long nanos) {
        this.bikeUntilNanos = nanos;
    }

    /**
     * @return true si el jugador está en modo moto (velocidad aumentada).
     */
    public boolean isOnBike() {
        return System.nanoTime() < bikeUntilNanos;
    }

    /** @return puntaje acumulado del jugador en la partida actual. */
    public int getScore() {
        return score;
    }

    /** Suma puntos al marcador. Usado por {@link GameState} al golpear al rival. */
    public void addScore(int delta) {
        this.score += delta;
    }

    /** @return vidas restantes del jugador (0..{@link #INITIAL_LIVES}). */
    public int getLives() {
        return lives;
    }

    /** Resta una vida; nunca baja de cero. */
    public void loseLife() {
        if (lives > 0) {
            lives--;
        }
    }

    /** @return true si el jugador agotó todas sus vidas (la partida debe terminar). */
    public boolean isDead() {
        return lives <= 0;
    }

    /**
     * Restablece vidas, score, enfriamiento y rastro para iniciar una nueva partida.
     */
    public void resetForNewGame() {
        this.lives = INITIAL_LIVES;
        this.score = 0;
        this.fireCooldownTicks = 0;
        this.bikeUntilNanos = 0L;
        this.moveX = 0;
        this.moveY = 0;
        this.trailInvulnUntilNanos = 0L;
        clearTrail();
    }

    /**
     * Añade un punto al rastro del modo moto. Salta si la posición coincide con la del último
     * punto (evita inflar la lista cuando el jugador se queda quieto sobre el mismo pixel).
     *
     * @param x coordenada absoluta en píxeles
     * @param y coordenada absoluta en píxeles
     */
    public void addTrailPoint(double x, double y) {
        if (!motoTrail.isEmpty()) {
            Point2D.Double last = motoTrail.get(motoTrail.size() - 1);
            if (last.x == x && last.y == y) {
                return;
            }
        }
        motoTrail.add(new Point2D.Double(x, y));
    }

    /**
     * @return vista inmutable de los puntos del rastro, en orden de grabación (cola = más viejo).
     */
    public List<Point2D.Double> getMotoTrail() {
        return Collections.unmodifiableList(motoTrail);
    }

    /**
     * Vacía el rastro y cancela cualquier erosión en curso. Usado en respawn y reset.
     */
    public void clearTrail() {
        motoTrail.clear();
        trailEroding = false;
    }

    /**
     * @return true si el jugador todavía está dentro de la ventana de invulnerabilidad al trail
     *         tras un golpe reciente.
     */
    public boolean isTrailInvuln() {
        return System.nanoTime() < trailInvulnUntilNanos;
    }

    /**
     * Activa la ventana de invulnerabilidad al trail por
     * {@link GameConstants#TRAIL_INVULN_SEC} segundos a partir de ahora.
     */
    public void setTrailInvuln() {
        trailInvulnUntilNanos = System.nanoTime()
                + (long) (GameConstants.TRAIL_INVULN_SEC * GameConstants.NANOS_PER_SECOND);
    }

    /**
     * Marca el rastro para comenzar a erosionarse en el próximo tick. Idempotente; si el rastro
     * está vacío no hace nada.
     */
    public void startTrailErosion() {
        if (!motoTrail.isEmpty()) {
            trailEroding = true;
        }
    }

    /**
     * @return true si el rastro está erosionándose actualmente.
     */
    public boolean isTrailEroding() {
        return trailEroding;
    }

    /**
     * Elimina puntos desde la cola (los más viejos) a un ritmo que vacía la lista en
     * {@link GameConstants#TRAIL_EROSION_SEC} segundos a ~60 Hz.
     * <p>
     * NOTE: Pensado para llamarse una vez por tick mientras {@link #isTrailEroding()} sea true.
     */
    public void erodeTrail() {
        if (!trailEroding) {
            return;
        }
        if (motoTrail.isEmpty()) {
            trailEroding = false;
            return;
        }
        int totalTicks = Math.max(1, (int) Math.round(GameConstants.TRAIL_EROSION_SEC * 60.0));
        int pointsToRemove = Math.max(1, motoTrail.size() / totalTicks);
        for (int i = 0; i < pointsToRemove && !motoTrail.isEmpty(); i++) {
            motoTrail.remove(0);
        }
        if (motoTrail.isEmpty()) {
            trailEroding = false;
        }
    }

    /**
     * Fija directamente el instante en que termina la invulnerabilidad al trail.
     * <p>
     * NOTE: Visibilidad de paquete a propósito: sólo para pruebas dentro de {@code logic/}
     * (forzar expiración inmediata sin esperar 0.5 s reales).
     */
    void setTrailInvulnUntilNanos(long nanos) {
        this.trailInvulnUntilNanos = nanos;
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
