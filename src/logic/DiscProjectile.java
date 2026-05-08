package logic;

/**
 * Disco lanzado por un jugador: movimiento rectilíneo, TTL y dueño (para ignorar colisión inicial breve).
 */
public class DiscProjectile {

    private final int ownerId;
    private double x;
    private double y;
    private final double vx;
    private final double vy;
    private int ticksAlive;
    /** Ticks antes de poder golpear a su dueño (evita autogol instantáneo). */
    private int friendlyTicks;

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

    public void tick() {
        x += vx;
        y += vy;
        ticksAlive++;
        if (friendlyTicks > 0) {
            friendlyTicks--;
        }
    }

    public boolean isFriendlyToOwner() {
        return friendlyTicks > 0;
    }

    public boolean isExpired() {
        return ticksAlive >= GameConstants.DISC_MAX_TICKS;
    }
}
