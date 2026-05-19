package events;

/**
 * Snapshot de entrada por tick: teclas mantenidas y solicitudes de borde (moto / disparo).
 * <p>
 * NOTE: El hilo de UI escribe flags; el hilo de juego lee y consume solicitudes puntuales.
 * Para esta base se acepta visibilidad sin {@code volatile} si ambos accesos están coordinados
 * por el mismo modelo de eventos; si se amplía, considerar {@code volatile} o {@code AtomicBoolean}.
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

    public boolean isP1Left() {
        return p1Left;
    }

    public void setP1Left(boolean p1Left) {
        this.p1Left = p1Left;
    }

    public boolean isP1Right() {
        return p1Right;
    }

    public void setP1Right(boolean p1Right) {
        this.p1Right = p1Right;
    }

    public boolean isP1Up() {
        return p1Up;
    }

    public void setP1Up(boolean p1Up) {
        this.p1Up = p1Up;
    }

    public boolean isP1Down() {
        return p1Down;
    }

    public void setP1Down(boolean p1Down) {
        this.p1Down = p1Down;
    }

    public boolean isP1Shoot() {
        return p1Shoot;
    }

    public void setP1Shoot(boolean p1Shoot) {
        this.p1Shoot = p1Shoot;
    }

    public boolean isP2Left() {
        return p2Left;
    }

    public void setP2Left(boolean p2Left) {
        this.p2Left = p2Left;
    }

    public boolean isP2Right() {
        return p2Right;
    }

    public void setP2Right(boolean p2Right) {
        this.p2Right = p2Right;
    }

    public boolean isP2Up() {
        return p2Up;
    }

    public void setP2Up(boolean p2Up) {
        this.p2Up = p2Up;
    }

    public boolean isP2Down() {
        return p2Down;
    }

    public void setP2Down(boolean p2Down) {
        this.p2Down = p2Down;
    }

    public boolean isP2Shoot() {
        return p2Shoot;
    }

    public void setP2Shoot(boolean p2Shoot) {
        this.p2Shoot = p2Shoot;
    }

    /** Solicitud de moto P1 (se consume en el tick de lógica). */
    public void requestP1Bike() {
        this.p1BikeRequest = true;
    }

    /** Solicitud de moto P2. */
    public void requestP2Bike() {
        this.p2BikeRequest = true;
    }

    public boolean consumeP1BikeRequest() {
        boolean v = p1BikeRequest;
        p1BikeRequest = false;
        return v;
    }

    public boolean consumeP2BikeRequest() {
        boolean v = p2BikeRequest;
        p2BikeRequest = false;
        return v;
    }
}
