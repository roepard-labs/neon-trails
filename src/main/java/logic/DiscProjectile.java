package logic;

/**
 * Disco lanzado por un jugador: movimiento rectilíneo con rebotes en bordes y dueño (para ignorar
 * colisión inicial breve). Tras agotar los rebotes queda quieto y solo el dueño puede recogerlo.
 * <p>
 * Invariantes:
 * <ul>
 *   <li>{@code friendlyTicks} decrece monotónicamente hasta cero; nunca rebota.</li>
 *   <li>{@code bouncesRemaining} decrece monotónicamente hasta cero, momento en que
 *       {@code stuck} pasa a true y la velocidad se anula.</li>
 *   <li>Mientras {@code stuck} es true, {@code vx == vy == 0} y {@link #tick()} es un no-op.</li>
 * </ul>
 * <p>
 * NOTE: [sustentación] Encapsulación clásica: campos privados, mutación únicamente a través de
 * {@link #tick()}, {@link #bounceX()}, {@link #bounceY()} y {@link #setPosition(double, double)}.
 * La regla "sólo el dueño recoge" la aplica {@link GameState}, no esta clase.
 */
public class DiscProjectile {

    private final int ownerId;
    private double x;
    private double y;
    private double vx;
    private double vy;
    private int ticksAlive;
    /** Ticks antes de poder golpear a su dueño (evita autogol instantáneo). */
    private int friendlyTicks;
    /** Rebotes que aún puede dar contra los bordes antes de detenerse. */
    private int bouncesRemaining;
    /** Si está quieto contra una pared: ya no se mueve ni hace daño; solo el dueño puede recogerlo. */
    private boolean stuck;

    /**
     * Crea un disco recién disparado con su gracia inicial y los rebotes máximos disponibles.
     *
     * @param ownerId jugador que disparó (1 o 2)
     * @param x       centro X inicial
     * @param y       centro Y inicial
     * @param vx      velocidad X por tick
     * @param vy      velocidad Y por tick
     */
    public DiscProjectile(int ownerId, double x, double y, double vx, double vy) {
        this.ownerId = ownerId;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.friendlyTicks = GameConstants.DISC_FRIENDLY_TICKS;
        this.bouncesRemaining = GameConstants.DISC_MAX_BOUNCES;
    }

    /** @return id del jugador que disparó este disco (1 = cian, 2 = rosa). */
    public int getOwnerId() {
        return ownerId;
    }

    /** @return centro X actual del disco en píxeles. */
    public double getX() {
        return x;
    }

    /** @return centro Y actual del disco en píxeles. */
    public double getY() {
        return y;
    }

    /** @return número de ticks vividos desde que se disparó (≥0). */
    public int getTicksAlive() {
        return ticksAlive;
    }

    /** @return true si agotó sus rebotes y quedó quieto contra una pared. */
    public boolean isStuck() {
        return stuck;
    }

    /**
     * Reposiciona el disco (usado por la simulación para fijar el centro contra un borde tras
     * rebotar y evitar penetración numérica).
     */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Avanza el disco un tick: actualiza posición según velocidad, incrementa contador de vida y
     * decrementa la gracia de auto-golpe. Si el disco ya está quieto, es un no-op.
     */
    public void tick() {
        if (stuck) {
            // NOTE: Un disco quieto no avanza; el dueño debe recogerlo.
            return;
        }
        x += vx;
        y += vy;
        ticksAlive++;
        if (friendlyTicks > 0) {
            friendlyTicks--;
        }
    }

    /**
     * Invierte la velocidad horizontal y consume un rebote. Si se agotan los rebotes, queda quieto.
     */
    public void bounceX() {
        if (stuck) {
            return;
        }
        vx = -vx;
        consumeBounce();
    }

    /**
     * Invierte la velocidad vertical y consume un rebote. Si se agotan los rebotes, queda quieto.
     */
    public void bounceY() {
        if (stuck) {
            return;
        }
        vy = -vy;
        consumeBounce();
    }

    private void consumeBounce() {
        bouncesRemaining--;
        if (bouncesRemaining <= 0) {
            stop();
        }
    }

    private void stop() {
        vx = 0;
        vy = 0;
        stuck = true;
    }

    /**
     * @return true si el disco aún está dentro de su ventana de gracia y por lo tanto no puede
     *         golpear a su propio dueño. La gracia dura {@link GameConstants#DISC_FRIENDLY_TICKS}
     *         ticks desde el disparo.
     */
    public boolean isFriendlyToOwner() {
        return friendlyTicks > 0;
    }
}
