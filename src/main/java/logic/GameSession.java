package logic;

/**
 * Datos de la sesión de juego compartidos entre pantallas.
 * <p>
 * NOTE: Contenedor mutable simple; vive durante una corrida del programa.
 * Las pantallas leen/escriben campos tipados en lugar de pasar {@code Object}.
 */
public class GameSession {

    /** Nombre del jugador 1 (capturado en NameInputScreen). */
    private String playerOneName = "P1";
    /** Nombre del jugador 2. */
    private String playerTwoName = "P2";
    /** Identificador del jugador ganador (1 o 2); 0 si la partida no terminó. */
    private int winnerId;
    /** Puntaje final del jugador 1 (se publica en el leaderboard al terminar la partida). */
    private int playerOneScore;
    /** Puntaje final del jugador 2 (se publica en el leaderboard al terminar la partida). */
    private int playerTwoScore;

    public String getPlayerOneName() {
        return playerOneName;
    }

    public void setPlayerOneName(String playerOneName) {
        this.playerOneName = playerOneName;
    }

    public String getPlayerTwoName() {
        return playerTwoName;
    }

    public void setPlayerTwoName(String playerTwoName) {
        this.playerTwoName = playerTwoName;
    }

    public int getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(int winnerId) {
        this.winnerId = winnerId;
    }

    public int getPlayerOneScore() {
        return playerOneScore;
    }

    public void setPlayerOneScore(int playerOneScore) {
        this.playerOneScore = playerOneScore;
    }

    public int getPlayerTwoScore() {
        return playerTwoScore;
    }

    public void setPlayerTwoScore(int playerTwoScore) {
        this.playerTwoScore = playerTwoScore;
    }

    /** Devuelve el nombre del ganador según {@link #winnerId}, o cadena vacía si no aplica. */
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
