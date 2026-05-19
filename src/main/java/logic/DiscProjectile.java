package logic;

/**
 * Disco lanzado por un jugador: movimiento rectilíneo con rebotes en bordes y dueño (para ignorar
 * colisión inicial breve). Tras agotar los rebotes queda quieto y solo el dueño puede recogerlo.
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
     * @param ownerId jugador que disparó
     * @param x         centro X inicial
     * @param y         centro Y inicial
     * @param vx        velocidad X por tick
     * @param vy        velocidad Y por tick
     */
    public DiscProjectile(int ownerId, double x, double y, double vx, double vy) {
        this.ownerId = ownerId;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.friendlyTicks = 8;
        this.bouncesRemaining = GameConstants.DISC_MAX_BOUNCES;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getTicksAlive() {
        return ticksAlive;
    }

    public boolean isStuck() {
        return stuck;
    }

    /**
     * Reposiciona el disco (usado por la simulación para fijar el centro contra un borde tras rebotar).
     */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

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

    public boolean isFriendlyToOwner() {
        return friendlyTicks > 0;
    }
}
