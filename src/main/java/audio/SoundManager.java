package audio;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.GraphicsEnvironment;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Gestor de efectos de sonido del juego: precarga los WAV al arranque y los reproduce con baja
 * latencia usando {@link javax.sound.sampled.Clip} (sin librerías externas, regla "cero dependencias").
 * <p>
 * NOTE: Sólo se cargan archivos cuando hay {@code Mixer} disponible. En modo headless o si el sistema
 * de audio no se puede abrir, el manager entra en modo no-op silencioso y todas las llamadas siguen
 * siendo seguras (necesario para CI Surefire con {@code java.awt.headless=true}).
 * <p>
 * NOTE: Para efectos potencialmente solapables (rebote, golpe, disparo) se mantiene un pool pequeño
 * de clips por sample con round-robin; el resto reusa un único clip que se rebobina antes de cada
 * reproducción. {@link Sfx#BIKE} es un loop continuo controlado por refcount entre los dos jugadores.
 */
public final class SoundManager {

    /** Samples que se reproducen en pool para permitir solapamiento real. */
    private static final Set<Sfx> POOLED = EnumSet.of(
            Sfx.BOUNCE, Sfx.HIT, Sfx.SHOOT_1, Sfx.SHOOT_2, Sfx.SHOOT_3);
    /** Tamaño del pool por sample solapable. */
    private static final int POOL_SIZE = 2;

    private static boolean initialized;
    private static boolean available;
    private static final Map<Sfx, Clip[]> CLIPS = new EnumMap<>(Sfx.class);
    private static final Map<Sfx, Integer> POOL_INDEX = new EnumMap<>(Sfx.class);
    /** Cuántos jugadores están actualmente en moto; el loop BIKE arranca/para según pase 0↔1. */
    private static int bikeLoopRefCount;

    /** Clase de utilidades estáticas; no se instancia. */
    private SoundManager() {
    }

    /**
     * Carga todos los samples definidos en {@link Sfx}. Idempotente: invocaciones posteriores son
     * no-op. Llamar una sola vez en el arranque (antes de mostrar la ventana).
     */
    public static synchronized void preloadAll() {
        if (initialized) {
            return;
        }
        initialized = true;
        if (GraphicsEnvironment.isHeadless()) {
            // NOTE: en headless (CI) no hay mixer estable; no intentamos abrir clips.
            System.err.println("[audio] Modo headless: SFX deshabilitado");
            return;
        }
        try {
            for (Sfx s : Sfx.values()) {
                int count = POOLED.contains(s) ? POOL_SIZE : 1;
                Clip[] arr = new Clip[count];
                for (int i = 0; i < count; i++) {
                    arr[i] = loadClip(s);
                }
                CLIPS.put(s, arr);
            }
            available = true;
        } catch (RuntimeException | LineUnavailableException | UnsupportedAudioFileException
                | IOException e) {
            System.err.println("[audio] Inicialización fallida (" + e.getClass().getSimpleName()
                    + "): " + e.getMessage());
            shutdownInternal();
            available = false;
        }
    }

    /**
     * Abre un {@link Clip} con el WAV de {@code s} desde el classpath y le aplica la ganancia
     * declarada en el enum. Lanza si el recurso no existe o el mixer no concede una línea.
     */
    private static Clip loadClip(Sfx s)
            throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        String path = "/audio/sfx/" + s.filename();
        InputStream raw = SoundManager.class.getResourceAsStream(path);
        if (raw == null) {
            throw new IOException("Recurso no encontrado en classpath: " + path);
        }
        try (AudioInputStream in = AudioSystem.getAudioInputStream(new BufferedInputStream(raw))) {
            Clip clip = AudioSystem.getClip();
            clip.open(in);
            applyGain(clip, s.gainDb());
            return clip;
        }
    }

    /** Aplica {@code dB} al {@code MASTER_GAIN} del clip si el mixer lo soporta; clampa al rango válido. */
    private static void applyGain(Clip clip, float dB) {
        if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            return;
        }
        FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float clamped = Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), dB));
        gain.setValue(clamped);
    }

    /**
     * Reproduce un sample puntual. Si el sample tiene pool, rota round-robin; si no, rebobina el
     * clip único. No bloquea (la reproducción ocurre en el hilo interno del {@code AudioSystem}).
     */
    public static void play(Sfx s) {
        if (!available) {
            return;
        }
        Clip[] pool = CLIPS.get(s);
        if (pool == null || pool.length == 0) {
            return;
        }
        Clip clip;
        if (pool.length == 1) {
            clip = pool[0];
        } else {
            int idx = POOL_INDEX.getOrDefault(s, 0);
            clip = pool[idx];
            POOL_INDEX.put(s, (idx + 1) % pool.length);
        }
        synchronized (clip) {
            clip.stop();
            clip.setFramePosition(0);
            clip.start();
        }
    }

    /**
     * Reproduce uno de los samples al azar (distribución uniforme). Útil para variantes
     * (disparo, gameover) y así evitar fatiga auditiva.
     */
    public static void playRandom(Sfx... samples) {
        if (samples == null || samples.length == 0) {
            return;
        }
        play(samples[ThreadLocalRandom.current().nextInt(samples.length)]);
    }

    /**
     * Inicia el loop {@link Sfx#BIKE} si no estaba activo. Cuenta cuántos jugadores lo están
     * solicitando: el loop arranca al pasar de 0 a 1 jugador en moto.
     */
    public static synchronized void startBikeLoop() {
        bikeLoopRefCount++;
        if (!available) {
            return;
        }
        if (bikeLoopRefCount == 1) {
            Clip clip = singleClip(Sfx.BIKE);
            if (clip == null) {
                return;
            }
            synchronized (clip) {
                clip.stop();
                clip.setFramePosition(0);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
    }

    /**
     * Detiene el loop {@link Sfx#BIKE} cuando el último jugador en moto lo libera.
     */
    public static synchronized void stopBikeLoop() {
        if (bikeLoopRefCount <= 0) {
            // NOTE: defensivo; no debería bajar de cero, pero evitamos estados negativos persistentes.
            bikeLoopRefCount = 0;
            return;
        }
        bikeLoopRefCount--;
        if (!available) {
            return;
        }
        if (bikeLoopRefCount == 0) {
            Clip clip = singleClip(Sfx.BIKE);
            if (clip == null) {
                return;
            }
            synchronized (clip) {
                clip.stop();
            }
        }
    }

    /**
     * Detiene un sample puntual. Útil para cortar el clip de game over al volver al menú.
     */
    public static void stop(Sfx s) {
        if (!available) {
            return;
        }
        Clip[] pool = CLIPS.get(s);
        if (pool == null) {
            return;
        }
        for (Clip c : pool) {
            synchronized (c) {
                c.stop();
            }
        }
    }

    /** @return true si {@link #preloadAll()} consiguió abrir los clips y el audio está operativo. */
    public static boolean isAvailable() {
        return available;
    }

    /**
     * Cierra todos los clips. Llamar al apagar la aplicación si se quiere liberar el mixer; los
     * clips se reciclan si no se invoca (la JVM los recoge al terminar).
     */
    public static synchronized void shutdown() {
        shutdownInternal();
        available = false;
        initialized = false;
    }

    /** Devuelve el primer (o único) clip asociado a {@code s}, o {@code null} si no se precargó. */
    private static Clip singleClip(Sfx s) {
        Clip[] pool = CLIPS.get(s);
        if (pool == null || pool.length == 0) {
            return null;
        }
        return pool[0];
    }

    /** Cierra todos los clips abiertos y limpia caches; best-effort, no propaga excepciones. */
    private static void shutdownInternal() {
        for (Clip[] pool : CLIPS.values()) {
            for (Clip c : pool) {
                if (c == null) {
                    continue;
                }
                try {
                    c.stop();
                    c.close();
                } catch (RuntimeException ignored) {
                    // NOTE: cierre best-effort; estamos terminando.
                }
            }
        }
        CLIPS.clear();
        POOL_INDEX.clear();
        bikeLoopRefCount = 0;
    }
}
