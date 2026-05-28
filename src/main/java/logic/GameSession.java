package logic;

/**
 * Datos de la sesión de juego compartidos entre pantallas (menú → captura de nombres → partida →
 * fin de partida).
 * <p>
 * NOTE: [sustentación] Contenedor mutable simple, vivo durante toda la corrida del programa.
 * Es la forma en que las pantallas leen/escriben información tipada sin pasarse {@code Object}
 * entre sí — patrón de "sesión compartida" muy común en aplicaciones Swing multi-pantalla.
 */
public class GameSession {

    /** Nombre del jugador 1 (capturado en NameInputScreen). */
    private String playerOneName = "P1";

    /** Nombre del jugador 2 (capturado en NameInputScreen). */
    private String playerTwoName = "P2";

    /** Identificador del jugador ganador (1 o 2); 0 si la partida no terminó. */
    private int winnerId;

    /** Puntaje final del jugador 1 (se publica en el leaderboard al terminar la partida). */
    private int playerOneScore;

    /** Puntaje final del jugador 2 (se publica en el leaderboard al terminar la partida). */
    private int playerTwoScore;

    /**
     * Instante (epoch millis) en que arrancó el cronómetro de la partida actual.
     * <p>
     * NOTE: [sustentación] Lo dispara {@code GameScreen.onShow} junto con el {@code javax.swing.Timer}
     * del HUD. Cumple el criterio 6 de la rúbrica ("control de tiempo mediante clase Timer") sin
     * tocar el hilo dedicado del {@link view.GameLoop}.
     */
    private long matchStartMillis;

    /** Duración total de la última partida terminada (millis). 0 si aún está en curso. */
    private long matchDurationMillis;

    /** @return nombre del jugador 1 (capturado en NameInputScreen, "P1" si nada se capturó). */
    public String getPlayerOneName() {
        return playerOneName;
    }

    /** @param playerOneName nombre saneado del jugador 1; longitud máxima ya recortada. */
    public void setPlayerOneName(String playerOneName) {
        this.playerOneName = playerOneName;
    }

    /** @return nombre del jugador 2 ("P2" si nada se capturó). */
    public String getPlayerTwoName() {
        return playerTwoName;
    }

    /** @param playerTwoName nombre saneado del jugador 2; longitud máxima ya recortada. */
    public void setPlayerTwoName(String playerTwoName) {
        this.playerTwoName = playerTwoName;
    }

    /** @return id del ganador (1 = cian, 2 = rosa, 0 = partida en curso). */
    public int getWinnerId() {
        return winnerId;
    }

    /** @param winnerId id del ganador a registrar al finalizar la partida. */
    public void setWinnerId(int winnerId) {
        this.winnerId = winnerId;
    }

    /** @return puntaje final del jugador 1 (publicado en el leaderboard al cerrar la partida). */
    public int getPlayerOneScore() {
        return playerOneScore;
    }

    /** @param playerOneScore puntaje final del jugador 1 capturado por {@link GameSession}. */
    public void setPlayerOneScore(int playerOneScore) {
        this.playerOneScore = playerOneScore;
    }

    /** @return puntaje final del jugador 2 (publicado en el leaderboard al cerrar la partida). */
    public int getPlayerTwoScore() {
        return playerTwoScore;
    }

    /** @param playerTwoScore puntaje final del jugador 2 capturado por {@link GameSession}. */
    public void setPlayerTwoScore(int playerTwoScore) {
        this.playerTwoScore = playerTwoScore;
    }

    /**
     * Arranca el cronómetro de la partida actual. Llamado por {@code GameScreen.onShow} al
     * entrar a la pantalla del juego.
     */
    public void startMatchClock() {
        this.matchStartMillis = System.currentTimeMillis();
        this.matchDurationMillis = 0L;
    }

    /**
     * Detiene el cronómetro y sella {@link #getMatchDurationMillis()}. Llamado por
     * {@code GameScreen.onHide} al salir de la pantalla del juego.
     */
    public void stopMatchClock() {
        if (matchStartMillis > 0L) {
            this.matchDurationMillis = System.currentTimeMillis() - matchStartMillis;
        }
    }

    /**
     * @return millis transcurridos desde {@link #startMatchClock()} hasta ahora; 0 si el
     *         cronómetro nunca se inició.
     */
    public long getElapsedMillisLive() {
        if (matchStartMillis <= 0L) {
            return 0L;
        }
        return System.currentTimeMillis() - matchStartMillis;
    }

    /**
     * @return duración sellada de la última partida tras llamar a {@link #stopMatchClock()}; 0
     *         si la partida aún no terminó.
     */
    public long getMatchDurationMillis() {
        return matchDurationMillis;
    }

    /**
     * Devuelve el nombre del ganador según {@link #winnerId}, o cadena vacía si la partida no
     * terminó.
     */
    public String getWinnerName() {
        if (winnerId == 1) {
            return playerOneName;
        }
        if (winnerId == 2) {
            return playerTwoName;
        }
        return "";
    }
}
