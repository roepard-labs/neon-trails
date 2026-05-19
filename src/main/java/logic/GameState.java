package logic;

import events.InputController;

import java.awt.Color;
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

    private final Player playerOne;
    private final Player playerTwo;
    private final List<DiscProjectile> discs = new ArrayList<>();
    /** Último tamaño conocido del mundo (para respawns tras un golpe). */
    private int lastWorldWidth = GameConstants.DEFAULT_WIDTH;
    private int lastWorldHeight = GameConstants.DEFAULT_HEIGHT;
    /** Bandera de fin de partida: cierra la simulación cuando un jugador llega a 0 vidas. */
    private boolean finished;
    /** Identificador del ganador (1 o 2); 0 mientras la partida sigue. */
    private int winnerId;

    /**
     * Crea estado inicial con posiciones de spawn en esquinas opuestas.
     */
    public GameState(int width, int height) {
        int margin = GameConstants.PLAYER_SIZE * 2;
        this.playerOne = new Player(1, new Color(0x00, 0xff, 0xff), margin, margin);
        this.playerTwo = new Player(2, new Color(0xff, 0x33, 0x99), width - margin, height - margin);
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

        playerOne.tickCooldown();
        playerTwo.tickCooldown();

        tryShoot(playerOne, input.isP1Shoot(), width, height);
        tryShoot(playerTwo, input.isP2Shoot(), width, height);

        if (input.consumeP1BikeRequest()) {
            playerOne.activateBike();
        }
        if (input.consumeP2BikeRequest()) {
            playerTwo.activateBike();
        }

        updateDiscs(width, height);
        resolveDiscHits();
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
     * Evita movimiento diagonal más rápido que en un solo eje.
     */
    private static void normalizeMovement(int mx, int my, Player p) {
        if (mx != 0 && my != 0) {
            // NOTE: Movimiento en diagonal: mantener dirección pero no duplicar rapidez.
            p.setMove(Integer.signum(mx), Integer.signum(my));
        } else {
            p.setMove(mx, my);
        }
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
            if (d.getX() <= minX) {
                d.setPosition(minX, d.getY());
                d.bounceX();
            } else if (d.getX() >= maxX) {
                d.setPosition(maxX, d.getY());
                d.bounceX();
            }
            if (d.getY() <= minY) {
                d.setPosition(d.getX(), minY);
                d.bounceY();
            } else if (d.getY() >= maxY) {
                d.setPosition(d.getX(), maxY);
                d.bounceY();
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
                }
                continue;
            }
            if (hitsPlayer(d, playerOne)) {
                if (!(d.getOwnerId() == 1 && d.isFriendlyToOwner())) {
                    playerTwo.addScore(1);
                    playerOne.loseLife();
                    it.remove();
                    if (playerOne.isDead()) {
                        finished = true;
                        winnerId = 2;
                    } else {
                        respawnPlayersAfterHit();
                    }
                    return;
                }
            }
            if (hitsPlayer(d, playerTwo)) {
                if (!(d.getOwnerId() == 2 && d.isFriendlyToOwner())) {
                    playerOne.addScore(1);
                    playerTwo.loseLife();
                    it.remove();
                    if (playerTwo.isDead()) {
                        finished = true;
                        winnerId = 1;
                    } else {
                        respawnPlayersAfterHit();
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
    }
}
