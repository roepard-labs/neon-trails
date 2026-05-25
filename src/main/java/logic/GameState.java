package logic;

import events.InputController;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Estado mutable del juego: jugadores, discos y reglas de colisión por tick.
 * <p>
 * NOTE: Los métodos {@link #tick(events.InputController, int, int)} están pensados para llamarse
 * desde un único hilo (el del {@link view.GameLoop}).
 */
public class GameState {

    /** Listener no-op compartido cuando el constructor sin listener se usa (compat / tests). */
    private static final GameEventListener NOOP_LISTENER = new GameEventListener() {
    };

    private final Player playerOne;
    private final Player playerTwo;
    private final List<DiscProjectile> discs = new ArrayList<>();
    private final GameEventListener listener;
    /** Último tamaño conocido del mundo (para respawns tras un golpe). */
    private int lastWorldWidth = GameConstants.DEFAULT_WIDTH;
    private int lastWorldHeight = GameConstants.DEFAULT_HEIGHT;
    /** Bandera de fin de partida: cierra la simulación cuando un jugador llega a 0 vidas. */
    private boolean finished;
    /** Identificador del ganador (1 o 2); 0 mientras la partida sigue. */
    private int winnerId;
    /**
     * Cache persistente entre ticks del estado de moto por jugador, para detectar la transición
     * true→false (expiración del temporizador) sin importar en qué tick ocurra.
     */
    private boolean prevOnBikeP1;
    private boolean prevOnBikeP2;

    /**
     * Crea estado inicial con posiciones de spawn en esquinas opuestas y sin listener (no-op).
     */
    public GameState(int width, int height) {
        this(width, height, NOOP_LISTENER);
    }

    /**
     * Crea estado inicial con un {@link GameEventListener} que recibe eventos discretos
     * (disparo, rebote, golpe, etc.) durante la simulación.
     */
    public GameState(int width, int height, GameEventListener listener) {
        int margin = GameConstants.PLAYER_SIZE * 2;
        this.playerOne = new Player(1, new Color(0x00, 0xff, 0xff), margin, margin);
        this.playerTwo = new Player(2, new Color(0xff, 0x33, 0x99), width - margin, height - margin);
        this.listener = listener != null ? listener : NOOP_LISTENER;
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }

    public List<DiscProjectile> getDiscs() {
        return discs;
    }

    public boolean isFinished() {
        return finished;
    }

    public int getWinnerId() {
        return winnerId;
    }

    /**
     * Restablece el estado para una nueva partida: vidas, posiciones y discos.
     */
    public void reset() {
        playerOne.resetForNewGame();
        playerTwo.resetForNewGame();
        discs.clear();
        finished = false;
        winnerId = 0;
        respawnPlayersAfterHit();
    }

    /**
     * Avanza un tick de simulación.
     *
     * @param input  snapshot de teclas
     * @param width  ancho del mundo
     * @param height alto del mundo
     */
    public void tick(InputController input, int width, int height) {
        this.lastWorldWidth = width;
        this.lastWorldHeight = height;
        if (finished) {
            // NOTE: La pantalla decide cuándo limpiar; aquí solo no avanzamos más simulación.
            return;
        }
        applyMovementInput(input);
        playerOne.moveWithinBounds(width, height);
        playerTwo.moveWithinBounds(width, height);

        // NOTE: Graba rastro tras el movimiento del tick para que cada punto refleje la posición
        // ya validada contra los bordes. addTrailPoint salta duplicados (jugador quieto).
        if (playerOne.isOnBike()) {
            playerOne.addTrailPoint(playerOne.getX(), playerOne.getY());
        }
        if (playerTwo.isOnBike()) {
            playerTwo.addTrailPoint(playerTwo.getX(), playerTwo.getY());
        }

        playerOne.tickCooldown();
        playerTwo.tickCooldown();

        tryShoot(playerOne, input.isP1Shoot(), width, height);
        tryShoot(playerTwo, input.isP2Shoot(), width, height);

        if (input.consumeP1BikeRequest()) {
            playerOne.activateBike();
            listener.onBikeActivated(1);
        }
        if (input.consumeP2BikeRequest()) {
            playerTwo.activateBike();
            listener.onBikeActivated(2);
        }

        updateDiscs(width, height);
        resolveDiscHits();
        resolveTrailHits();

        // NOTE: Compara el estado de moto cacheado del tick anterior con el actual al cierre del
        // tick. Detecta la expiración del temporizador (true→false) aunque ocurra entre ticks.
        boolean nowOnBikeP1 = playerOne.isOnBike();
        boolean nowOnBikeP2 = playerTwo.isOnBike();
        if (prevOnBikeP1 && !nowOnBikeP1) {
            listener.onBikeEnded(1);
            playerOne.startTrailErosion();
        }
        if (prevOnBikeP2 && !nowOnBikeP2) {
            listener.onBikeEnded(2);
            playerTwo.startTrailErosion();
        }
        prevOnBikeP1 = nowOnBikeP1;
        prevOnBikeP2 = nowOnBikeP2;

        if (playerOne.isTrailEroding()) {
            playerOne.erodeTrail();
        }
        if (playerTwo.isTrailEroding()) {
            playerTwo.erodeTrail();
        }
    }

    private void applyMovementInput(events.InputController input) {
        int mx1 = 0;
        int my1 = 0;
        if (input.isP1Left()) {
            mx1--;
        }
        if (input.isP1Right()) {
            mx1++;
        }
        if (input.isP1Up()) {
            my1--;
        }
        if (input.isP1Down()) {
            my1++;
        }
        normalizeMovement(mx1, my1, playerOne);

        int mx2 = 0;
        int my2 = 0;
        if (input.isP2Left()) {
            mx2--;
        }
        if (input.isP2Right()) {
            mx2++;
        }
        if (input.isP2Up()) {
            my2--;
        }
        if (input.isP2Down()) {
            my2++;
        }
        normalizeMovement(mx2, my2, playerTwo);
    }

    /**
     * Traduce el input del tick a la dirección de movimiento. Evita que la diagonal sea más rápida
     * que un solo eje (signum) y, en modo moto, bloquea la reversa directa (180°) al estilo culebrita:
     * el opuesto exacto del rumbo actual se ignora para que el jugador no se invierta sobre su propia
     * estela; para volver hay que girar primero (perpendicular o diagonal). Fuera de moto: movimiento
     * libre, incluida la inversión instantánea.
     */
    private static void normalizeMovement(int mx, int my, Player p) {
        int dx = (mx != 0 && my != 0) ? Integer.signum(mx) : mx;
        int dy = (mx != 0 && my != 0) ? Integer.signum(my) : my;

        if (p.isOnBike()
                && (p.getMoveX() != 0 || p.getMoveY() != 0)
                && dx == -p.getMoveX() && dy == -p.getMoveY()) {
            // NOTE: Reversa directa en moto → se ignora; conserva el rumbo (hay que girar para volver).
            // Detenerse (0,0) nunca es el opuesto de un rumbo no nulo, así que soltar teclas sí frena.
            return;
        }
        p.setMove(dx, dy);
    }

    private void tryShoot(Player p, boolean wantsShoot, int width, int height) {
        if (!wantsShoot) {
            return;
        }
        if (p.getFireCooldownTicks() > 0) {
            return;
        }
        if (hasActiveDisc(p.getId())) {
            // NOTE: Cada jugador solo puede tener un disco en juego; debe recogerlo para volver a disparar.
            return;
        }
        int fx = p.getFacingX();
        int fy = p.getFacingY();
        if (fx == 0 && fy == 0) {
            fx = 1;
        }
        double len = Math.hypot(fx, fy);
        double vx = (fx / len) * GameConstants.DISC_SPEED;
        double vy = (fy / len) * GameConstants.DISC_SPEED;
        double cx = clamp(p.getX() + fx * (GameConstants.PLAYER_SIZE * 0.6), GameConstants.DISC_RADIUS,
                width - GameConstants.DISC_RADIUS);
        double cy = clamp(p.getY() + fy * (GameConstants.PLAYER_SIZE * 0.6), GameConstants.DISC_RADIUS,
                height - GameConstants.DISC_RADIUS);
        discs.add(new DiscProjectile(p.getId(), cx, cy, vx, vy));
        p.setFireCooldownTicks(GameConstants.FIRE_COOLDOWN_TICKS);
        listener.onShoot(p.getId());
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private boolean hasActiveDisc(int playerId) {
        for (DiscProjectile d : discs) {
            if (d.getOwnerId() == playerId) {
                return true;
            }
        }
        return false;
    }

    private void updateDiscs(int width, int height) {
        for (DiscProjectile d : discs) {
            d.tick();
            if (d.isStuck()) {
                continue;
            }
            // NOTE: Rebote eje a eje con clamp al borde para no incrustar el disco en la pared
            // cuando se queda quieto tras el último rebote.
            double minX = GameConstants.DISC_RADIUS;
            double maxX = width - GameConstants.DISC_RADIUS;
            double minY = GameConstants.DISC_RADIUS;
            double maxY = height - GameConstants.DISC_RADIUS;
            boolean bouncedAny = false;
            if (d.getX() <= minX) {
                d.setPosition(minX, d.getY());
                d.bounceX();
                bouncedAny = true;
            } else if (d.getX() >= maxX) {
                d.setPosition(maxX, d.getY());
                d.bounceX();
                bouncedAny = true;
            }
            if (d.getY() <= minY) {
                d.setPosition(d.getX(), minY);
                d.bounceY();
                bouncedAny = true;
            } else if (d.getY() >= maxY) {
                d.setPosition(d.getX(), maxY);
                d.bounceY();
                bouncedAny = true;
            }
            if (bouncedAny) {
                // NOTE: si el rebote consumió el último, emitimos sólo onDiscStopped; evita
                // bounce + stop sonando juntos en la misma transición.
                if (d.isStuck()) {
                    listener.onDiscStopped();
                } else {
                    listener.onBounce();
                }
            }
        }
    }

    private void resolveDiscHits() {
        Iterator<DiscProjectile> it = discs.iterator();
        while (it.hasNext()) {
            DiscProjectile d = it.next();
            if (d.isStuck()) {
                // NOTE: Un disco quieto no daña; solo el dueño puede recogerlo y eso resetea su cooldown.
                Player owner = (d.getOwnerId() == 1) ? playerOne : playerTwo;
                if (hitsPlayer(d, owner)) {
                    owner.setFireCooldownTicks(0);
                    it.remove();
                    listener.onPickup(owner.getId());
                }
                continue;
            }
            if (hitsPlayer(d, playerOne)) {
                if (!(d.getOwnerId() == 1 && d.isFriendlyToOwner())) {
                    playerTwo.addScore(1);
                    playerOne.loseLife();
                    it.remove();
                    listener.onHit(1);
                    if (playerOne.isDead()) {
                        finished = true;
                        winnerId = 2;
                        listener.onGameOver(2);
                    } else {
                        respawnPlayersAfterHit();
                        listener.onRespawn();
                    }
                    return;
                }
            }
            if (hitsPlayer(d, playerTwo)) {
                if (!(d.getOwnerId() == 2 && d.isFriendlyToOwner())) {
                    playerOne.addScore(1);
                    playerTwo.loseLife();
                    it.remove();
                    listener.onHit(2);
                    if (playerTwo.isDead()) {
                        finished = true;
                        winnerId = 1;
                        listener.onGameOver(1);
                    } else {
                        respawnPlayersAfterHit();
                        listener.onRespawn();
                    }
                    return;
                }
            }
        }
    }

    private static boolean hitsPlayer(DiscProjectile d, Player p) {
        double px = p.getX();
        double py = p.getY();
        double half = GameConstants.PLAYER_SIZE / 2.0;
        double closestX = clamp(d.getX(), px - half, px + half);
        double closestY = clamp(d.getY(), py - half, py + half);
        double dx = d.getX() - closestX;
        double dy = d.getY() - closestY;
        return (dx * dx + dy * dy) <= (double) GameConstants.DISC_RADIUS * GameConstants.DISC_RADIUS;
    }

    private void respawnPlayersAfterHit() {
        discs.clear();
        int w = lastWorldWidth;
        int h = lastWorldHeight;
        int margin = GameConstants.PLAYER_SIZE * 2;
        playerOne.setPosition(margin, margin);
        playerTwo.setPosition(w - margin, h - margin);
        playerOne.clearTrail();
        playerTwo.clearTrail();
    }

    /**
     * Resuelve colisiones de los jugadores contra rastros de moto (propios o enemigos).
     * <p>
     * NOTE: Se ejecuta tras {@link #resolveDiscHits()} para que un golpe de disco letal corte la
     * cadena. La invulnerabilidad temporal evita doble daño en ticks consecutivos sobre el mismo
     * trail; los últimos {@link GameConstants#TRAIL_GRACE_POINTS} puntos del trail propio no dañan
     * al dueño (gracia para que activar la moto no se autodañe).
     */
    private void resolveTrailHits() {
        if (finished) {
            return;
        }
        // Cada víctima se chequea contra ambos trails. Orden: trail propio primero (más probable
        // en juegos cerrados) y luego trail enemigo. checkTrailHit aplica daño y respawn si toca.
        checkTrailHit(playerOne, playerOne, 1);
        if (finished) {
            return;
        }
        checkTrailHit(playerOne, playerTwo, 1);
        if (finished) {
            return;
        }
        checkTrailHit(playerTwo, playerTwo, 2);
        if (finished) {
            return;
        }
        checkTrailHit(playerTwo, playerOne, 2);
    }

    private void checkTrailHit(Player victim, Player trailOwner, int victimId) {
        if (victim.isTrailInvuln()) {
            return;
        }
        List<Point2D.Double> trail = trailOwner.getMotoTrail();
        if (trail.isEmpty()) {
            return;
        }
        int endIdx = trail.size();
        if (victim == trailOwner) {
            // Gracia: ignorar los puntos más recientes del propio rastro (cabeza de la lista).
            endIdx = Math.max(0, trail.size() - GameConstants.TRAIL_GRACE_POINTS);
        }
        for (int i = 0; i < endIdx; i++) {
            Point2D.Double pt = trail.get(i);
            if (pointHitsPlayer(pt.x, pt.y, victim)) {
                applyTrailHit(victim, victimId);
                return;
            }
        }
    }

    private void applyTrailHit(Player victim, int victimId) {
        victim.loseLife();
        victim.setTrailInvuln();
        listener.onTrailHit(victimId);
        if (victim.isDead()) {
            finished = true;
            winnerId = (victimId == 1) ? 2 : 1;
            listener.onGameOver(winnerId);
        } else {
            respawnPlayersAfterHit();
            listener.onRespawn();
        }
    }

    private static boolean pointHitsPlayer(double px, double py, Player p) {
        double cx = p.getX();
        double cy = p.getY();
        double half = GameConstants.PLAYER_SIZE / 2.0;
        return px >= cx - half && px <= cx + half && py >= cy - half && py <= cy + half;
    }
}
