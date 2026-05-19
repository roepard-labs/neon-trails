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
