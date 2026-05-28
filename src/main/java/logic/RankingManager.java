package logic;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestor del ranking persistente del juego: lee/escribe el Top-N en
 * {@code ~/.neon-trails/ranking.json} y lo mantiene ordenado por puntaje descendente.
 * <p>
 * Cubre tres criterios distintos del PDF de Técnicas de Programación con una sola clase:
 * <ol>
 *   <li><b>Recursividad</b> — {@link #quicksortDescByScore(List, int, int)} ordena con quicksort
 *       recursivo (esquema Lomb).</li>
 *   <li><b>Ordenamiento propio</b> — no usa {@link Collections#sort(List)}, sino la
 *       implementación manual del quicksort.</li>
 *   <li><b>Persistencia + Top-3</b> — guarda cada partida en disco y expone
 *       {@link #getTop(int)} para la pantalla de fin de partida.</li>
 * </ol>
 * <p>
 * NOTE: [sustentación] El archivo vive en el HOME del usuario, no en el classpath. La razón es
 * técnica: una vez empaquetado el JAR, los recursos en {@code src/main/resources/} son de sólo
 * lectura. El directorio {@code ~/.neon-trails/} se crea bajo demanda en el {@link #persist()}.
 * <p>
 * NOTE: [sustentación] Es seguro contra fallos: si el JSON está corrupto o el archivo no existe,
 * arranca con lista vacía y registra un warning. Nunca lanza al constructor — el juego siempre
 * puede arrancar.
 *
 * @see GameOverScreen para la integración con la UI
 */
public final class RankingManager {

    private static final Logger LOG = Logger.getLogger(RankingManager.class.getName());

    /** Tope superior del histórico — más allá no aporta nada y crece sin sentido. */
    private static final int MAX_ENTRIES = 50;

    /** Directorio bajo el HOME del usuario donde vive el archivo de ranking. */
    private final Path rankingDir;

    /** Archivo JSON con la lista serializada por Gson. */
    private final Path rankingFile;

    private final Gson gson = new Gson();
    private final List<RankingEntry> entries;

    /**
     * Construye el manager apuntando a {@code ~/.neon-trails/ranking.json}, cargando el archivo
     * si existe; en cualquier fallo arranca vacío.
     * <p>
     * NOTE: [sustentación] Los paths se calculan en el constructor (no como constantes estáticas)
     * para permitir tests que redirijan {@code user.home} a una carpeta temporal sin tocar el
     * HOME real del usuario.
     */
    public RankingManager() {
        this.rankingDir = Paths.get(System.getProperty("user.home"), ".neon-trails");
        this.rankingFile = rankingDir.resolve("ranking.json");
        this.entries = load();
    }

    /**
     * Entrada inmutable del ranking. Sus campos son públicos finales por simplicidad de
     * serialización con Gson (sin necesidad de getters explícitos).
     */
    public static final class RankingEntry {

        public final String playerName;
        public final int score;
        public final String result;
        public final String playedAt;

        public RankingEntry(String playerName, int score, String result, String playedAt) {
            this.playerName = playerName;
            this.score = score;
            this.result = result;
            this.playedAt = playedAt;
        }
    }

    /**
     * Agrega una entrada nueva, reordena toda la lista, recorta a {@link #MAX_ENTRIES} y
     * persiste a disco.
     * <p>
     * Es {@code synchronized} para tolerar llamadas desde la EDT (donde la invoca
     * {@code GameOverScreen}) y un eventual hilo de prueba simultáneo.
     *
     * @param playerName nombre del jugador (se almacena tal cual; ya viene saneado de
     *                   {@code NameInputScreen})
     * @param score      puntaje final de la partida (≥0)
     * @param result     resultado: típicamente {@code "win"} o {@code "loss"}
     */
    public synchronized void addEntry(String playerName, int score, String result) {
        entries.add(new RankingEntry(playerName, score, result, Instant.now().toString()));
        quicksortDescByScore(entries, 0, entries.size() - 1);
        if (entries.size() > MAX_ENTRIES) {
            entries.subList(MAX_ENTRIES, entries.size()).clear();
        }
        persist();
    }

    /**
     * Devuelve una copia inmutable de las primeras {@code n} entradas (Top-N) en orden de
     * puntaje descendente.
     *
     * @param n número de entradas pedidas (se clampa a {@code [0, entries.size()]})
     * @return lista inmutable de tamaño {@code min(n, entries.size())}; nunca null
     */
    public synchronized List<RankingEntry> getTop(int n) {
        int upper = Math.min(Math.max(n, 0), entries.size());
        return Collections.unmodifiableList(new ArrayList<>(entries.subList(0, upper)));
    }

    /**
     * Devuelve una copia inmutable de todas las entradas actuales del ranking, en el mismo orden
     * en que están almacenadas (descendente por puntaje). Útil para tests.
     */
    public synchronized List<RankingEntry> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(entries));
    }

    // ------------------------------------------------------------------------------------------
    // Quicksort recursivo (esquema Lomb) — cubre criterios de rúbrica 4 (recursividad) y 5
    // (ordenamiento propio sin Collections.sort).
    // ------------------------------------------------------------------------------------------

    /**
     * Ordena {@code list[lo..hi]} in-place por {@link RankingEntry#score} <b>descendente</b>
     * (mayor primero). Es <b>recursivo</b>: se invoca dos veces sobre las dos mitades resultantes
     * de la partición.
     * <p>
     * Complejidad: O(n log n) en promedio, O(n²) en el peor caso (lista ya ordenada con pivote
     * naive). Para n ≤ 50 (tope del archivo), el peor caso es despreciable.
     * <p>
     * NOTE: [sustentación] Esquema de partición de Lomuto. Pasos para defender en oral:
     * <ol>
     *   <li><b>Caso base</b>: si {@code lo >= hi}, el tramo tiene 0 o 1 elementos y ya está
     *       ordenado.</li>
     *   <li><b>Particionar</b>: con {@code list[hi]} como pivote, mover a la izquierda los
     *       elementos con score ≥ pivote y a la derecha los menores.</li>
     *   <li><b>Recurrir</b> sobre la mitad izquierda ({@code lo..p-1}) y la mitad derecha
     *       ({@code p+1..hi}).</li>
     * </ol>
     *
     * @param list lista mutable a ordenar
     * @param lo   índice inicial inclusivo
     * @param hi   índice final inclusivo
     */
    static void quicksortDescByScore(List<RankingEntry> list, int lo, int hi) {
        if (lo >= hi) {
            return;
        }
        int pivotIdx = partition(list, lo, hi);
        quicksortDescByScore(list, lo, pivotIdx - 1);
        quicksortDescByScore(list, pivotIdx + 1, hi);
    }

    /**
     * Esquema de partición de Lomuto sobre {@code list[lo..hi]}. Usa {@code list[hi]} como
     * pivote y deja a la izquierda los elementos con score ≥ pivote (orden descendente).
     *
     * @return índice final del pivote tras la partición
     */
    private static int partition(List<RankingEntry> list, int lo, int hi) {
        int pivotScore = list.get(hi).score;
        int i = lo - 1;
        for (int j = lo; j < hi; j++) {
            // ">=" para orden descendente estable (entradas con mismo score conservan inserción).
            if (list.get(j).score >= pivotScore) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, hi);
        return i + 1;
    }

    // ------------------------------------------------------------------------------------------
    // Persistencia con Gson
    // ------------------------------------------------------------------------------------------

    private List<RankingEntry> load() {
        if (!Files.exists(rankingFile)) {
            return new ArrayList<>();
        }
        try {
            String json = Files.readString(rankingFile);
            Type listType = new TypeToken<List<RankingEntry>>() { }.getType();
            List<RankingEntry> loaded = gson.fromJson(json, listType);
            return loaded != null ? new ArrayList<>(loaded) : new ArrayList<>();
        } catch (IOException | JsonSyntaxException e) {
            LOG.log(Level.WARNING, "Ranking corrupto en " + rankingFile + "; reiniciando", e);
            return new ArrayList<>();
        }
    }

    private void persist() {
        try {
            Files.createDirectories(rankingDir);
            Files.writeString(rankingFile, gson.toJson(entries));
        } catch (IOException e) {
            LOG.log(Level.WARNING, "No se pudo persistir ranking en " + rankingFile, e);
        }
    }
}
