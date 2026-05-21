package audio;

/**
 * Catálogo lógico de efectos de sonido del juego.
 * <p>
 * Cada constante referencia un archivo WAV PCM 16-bit mono 44.1 kHz dentro de
 * {@code src/main/resources/audio/sfx/} y declara una ganancia de mezcla expresada en decibelios
 * (0 dB es la amplitud nativa del archivo; valores negativos atenúan).
 * <p>
 * NOTE: Mantener este enum como única fuente de verdad de nombres de archivo evita strings sueltos
 * en el resto del código y simplifica la precarga en {@link SoundManager}.
 */
public enum Sfx {

    SHOOT_1("shoot_1.wav", -3f),
    SHOOT_2("shoot_2.wav", -3f),
    SHOOT_3("shoot_3.wav", -3f),
    HIT("hit.wav", -2f),
    BIKE_INIT("bike_init.wav", -2f),
    BIKE("bike.wav", -8f),
    BIKE_END("bike_end.wav", -2f),
    BOUNCE("bounce.wav", -4f),
    STOP("stop.wav", -4f),
    PICKUP("pickup.wav", -2f),
    RESPAWN("respawn.wav", -2f),
    GAMEOVER_1("gameover_1.wav", -8f),
    GAMEOVER_2("gameover_2.wav", -8f),
    UI_CLICK("ui_click.wav", -3f),
    UI_HOVER("ui_hover.wav", -10f);

    private final String filename;
    private final float gainDb;

    Sfx(String filename, float gainDb) {
        this.filename = filename;
        this.gainDb = gainDb;
    }

    public String filename() {
        return filename;
    }

    public float gainDb() {
        return gainDb;
    }
}
