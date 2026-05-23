package logic;

/**
 * Constantes globales del núcleo jugable.
 * <p>
 * NOTE: Valores ajustables para balance; centralizar aquí facilita pruebas.
 */
public final class GameConstants {

    private GameConstants() {
    }

    /** Ancho por defecto del área de juego (px). */
    public static final int DEFAULT_WIDTH = 960;
    /** Alto por defecto del área de juego (px). */
    public static final int DEFAULT_HEIGHT = 640;

    /** Duración del modo moto en segundos (requisito: 5 s). */
    public static final double BIKE_DURATION_SEC = 5.0;

    /** Velocidad base de jugador (px por tick a ~60 Hz). */
    public static final double PLAYER_SPEED = 3.2;
    /** Multiplicador de velocidad en moto. */
    public static final double BIKE_SPEED_MULT = 1.75;

    /** Tamaño del jugador (cuadrado). */
    public static final int PLAYER_SIZE = 22;
    /** Radio aproximado del disco (círculo). */
    public static final int DISC_RADIUS = 7;
    /** Velocidad del disco (px por tick). */
    public static final double DISC_SPEED = 7.5;
    /** Tiempo de vida máximo del disco (ticks). */
    public static final int DISC_MAX_TICKS = 240;
    /** Número máximo de rebotes en bordes antes de que el disco se detenga. */
    public static final int DISC_MAX_BOUNCES = 3;
    /** Enfriamiento entre disparos (ticks). */
    public static final int FIRE_COOLDOWN_TICKS = 28;

    /** Período del bucle de juego (ms). */
    public static final int GAME_TICK_MS = 16;

    /** Tiempo que tarda en erosionarse el rastro de moto completo desde la cola (s). */
    public static final double TRAIL_EROSION_SEC = 3.0;
    /** Ventana de invulnerabilidad tras recibir daño del rastro de moto (s). */
    public static final double TRAIL_INVULN_SEC = 0.5;
    /**
     * Cantidad de puntos más recientes del propio rastro que no dañan al dueño.
     * <p>
     * NOTE: Gracia para evitar auto-daño instantáneo al activar la moto. Operar en índices
     * (no en distancia euclídea) simplifica la colisión y es estable ante velocidad variable.
     */
    public static final int TRAIL_GRACE_POINTS = 4;
    /** Grosor de la polilínea del rastro de moto (px). */
    public static final float TRAIL_LINE_WIDTH = 3.0f;
}
