package events;

/**
 * Snapshot por tick del estado de entrada compartido entre los dos jugadores.
 * <p>
 * Modela dos patrones distintos según la naturaleza de la tecla:
 * <ul>
 *   <li><b>Hold</b> — la tecla refleja un estado <em>mantenido</em> mientras está presionada.
 *       Son las direcciones de movimiento y el disparo: el setter es invocado por las acciones
 *       de press y release de Swing y el getter es consultado cada tick del juego.
 *       Métodos: {@code isPxLeft/Right/Up/Down/Shoot} + {@code setPxLeft/...}.</li>
 *   <li><b>Edge-trigger</b> — la solicitud se activa con un <em>flanco</em> (una sola pulsación)
 *       y se consume de forma destructiva al leerla, garantizando exactamente una activación por
 *       cada tecla presionada aunque el tick lea varias veces el flag. Es la activación del modo
 *       moto: {@link #requestP1Bike()}/{@link #consumeP1BikeRequest()} (y su equivalente P2).</li>
 * </ul>
 * <p>
 * NOTE: [sustentación] El hilo de UI (EDT) escribe los flags al recibir eventos de teclado, y el
 * hilo de juego ({@code neon-trails-game-loop}) los lee al inicio de cada tick. Para esta base
 * se acepta la visibilidad sin {@code volatile} porque ambos accesos están coordinados por el
 * mismo flujo de eventos Swing y el ciclo de 16 ms tolera ver un valor con un tick de retraso.
 * Si se amplía la entrada (eventos cruzados, multi-hilo) considerar promover los campos a
 * {@code volatile} o {@code AtomicBoolean}.
 */
public class InputController {

    private boolean p1Left;
    private boolean p1Right;
    private boolean p1Up;
    private boolean p1Down;
    private boolean p1Shoot;
    private boolean p1BikeRequest;

    private boolean p2Left;
    private boolean p2Right;
    private boolean p2Up;
    private boolean p2Down;
    private boolean p2Shoot;
    private boolean p2BikeRequest;

    /** @return true si P1 mantiene la tecla de movimiento izquierda (A). */
    public boolean isP1Left() {
        return p1Left;
    }

    /** @param p1Left true al presionar A (P1), false al soltarla. */
    public void setP1Left(boolean p1Left) {
        this.p1Left = p1Left;
    }

    /** @return true si P1 mantiene la tecla de movimiento derecha (D). */
    public boolean isP1Right() {
        return p1Right;
    }

    /** @param p1Right true al presionar D (P1), false al soltarla. */
    public void setP1Right(boolean p1Right) {
        this.p1Right = p1Right;
    }

    /** @return true si P1 mantiene la tecla de movimiento arriba (W). */
    public boolean isP1Up() {
        return p1Up;
    }

    /** @param p1Up true al presionar W (P1), false al soltarla. */
    public void setP1Up(boolean p1Up) {
        this.p1Up = p1Up;
    }

    /** @return true si P1 mantiene la tecla de movimiento abajo (S). */
    public boolean isP1Down() {
        return p1Down;
    }

    /** @param p1Down true al presionar S (P1), false al soltarla. */
    public void setP1Down(boolean p1Down) {
        this.p1Down = p1Down;
    }

    /** @return true si P1 mantiene la tecla de disparo (E); el tick respeta el enfriamiento. */
    public boolean isP1Shoot() {
        return p1Shoot;
    }

    /** @param p1Shoot true al presionar E (P1), false al soltarla. */
    public void setP1Shoot(boolean p1Shoot) {
        this.p1Shoot = p1Shoot;
    }

    /** @return true si P2 mantiene la tecla de movimiento izquierda (flecha ←). */
    public boolean isP2Left() {
        return p2Left;
    }

    /** @param p2Left true al presionar flecha izquierda (P2), false al soltarla. */
    public void setP2Left(boolean p2Left) {
        this.p2Left = p2Left;
    }

    /** @return true si P2 mantiene la tecla de movimiento derecha (flecha →). */
    public boolean isP2Right() {
        return p2Right;
    }

    /** @param p2Right true al presionar flecha derecha (P2), false al soltarla. */
    public void setP2Right(boolean p2Right) {
        this.p2Right = p2Right;
    }

    /** @return true si P2 mantiene la tecla de movimiento arriba (flecha ↑). */
    public boolean isP2Up() {
        return p2Up;
    }

    /** @param p2Up true al presionar flecha arriba (P2), false al soltarla. */
    public void setP2Up(boolean p2Up) {
        this.p2Up = p2Up;
    }

    /** @return true si P2 mantiene la tecla de movimiento abajo (flecha ↓). */
    public boolean isP2Down() {
        return p2Down;
    }

    /** @param p2Down true al presionar flecha abajo (P2), false al soltarla. */
    public void setP2Down(boolean p2Down) {
        this.p2Down = p2Down;
    }

    /** @return true si P2 mantiene la tecla de disparo (Enter); el tick respeta el enfriamiento. */
    public boolean isP2Shoot() {
        return p2Shoot;
    }

    /** @param p2Shoot true al presionar Enter (P2), false al soltarla. */
    public void setP2Shoot(boolean p2Shoot) {
        this.p2Shoot = p2Shoot;
    }

    /**
     * Solicita activar el modo moto de P1 (tecla Q). Edge-trigger: la solicitud queda pendiente
     * hasta que el tick la consuma con {@link #consumeP1BikeRequest()}.
     */
    public void requestP1Bike() {
        this.p1BikeRequest = true;
    }

    /**
     * Solicita activar el modo moto de P2 (tecla U). Edge-trigger: la solicitud queda pendiente
     * hasta que el tick la consuma con {@link #consumeP2BikeRequest()}.
     */
    public void requestP2Bike() {
        this.p2BikeRequest = true;
    }

    /**
     * Consume la solicitud de moto de P1 (lee-y-limpia atómicamente desde la perspectiva del tick).
     * <p>
     * NOTE: [sustentación] Edge-trigger pattern: el set lo hace la tecla, el consume lo hace el
     * tick. Garantiza exactamente <em>una</em> activación por pulsación aunque el tick consulte
     * varias veces o la tecla quede presionada por varios frames.
     *
     * @return true exactamente la primera vez que se llama después de {@link #requestP1Bike()};
     *         false después y entre solicitudes
     */
    public boolean consumeP1BikeRequest() {
        boolean v = p1BikeRequest;
        p1BikeRequest = false;
        return v;
    }

    /**
     * Consume la solicitud de moto de P2 (lee-y-limpia). Mismo contrato que
     * {@link #consumeP1BikeRequest()}.
     *
     * @return true exactamente la primera vez tras {@link #requestP2Bike()}; false después
     */
    public boolean consumeP2BikeRequest() {
        boolean v = p2BikeRequest;
        p2BikeRequest = false;
        return v;
    }
}
