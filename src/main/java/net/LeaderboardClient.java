package net;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cliente HTTP del leaderboard: envía puntajes y consulta el Top-N desde el backend Laravel.
 * <p>
 * NOTE: [sustentación] Usa {@link java.net.http.HttpClient} (Java 11+) para la red — sin
 * dependencias adicionales — y {@code Gson} sólo para serializar/parsear JSON. Gson reemplazó
 * al escapado manual previo porque éste era frágil con caracteres Unicode, comillas dobles y
 * saltos de línea en nombres de jugador.
 * <p>
 * Es <b>tolerante a fallos</b>: cualquier fallo se registra y se descarta; el juego nunca se
 * interrumpe ni se bloquea. Tanto {@link #submitAsync(String, int, String)} como
 * {@link #fetchTopAsync(int)} ejecutan en hilos de {@link HttpClient}, fuera de la EDT y del
 * hilo de simulación.
 * <p>
 * La URL base se toma de la variable de entorno {@code LEADERBOARD_API_URL}; si no está
 * definida, usa {@code http://127.0.0.1/api} (el gateway nginx del contenedor monolítico).
 */
public final class LeaderboardClient {

    private static final Logger LOG = Logger.getLogger(LeaderboardClient.class.getName());

    /** Base de la API, normalizada sin barra final. */
    private static final String API_BASE = resolveApiBase();

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    /** Instancia única de Gson reutilizada (thread-safe). */
    private static final Gson GSON = new Gson();

    /** Clase de utilidades estáticas; no se instancia. */
    private LeaderboardClient() {
    }

    /**
     * Resuelve la URL base del backend leyendo {@code LEADERBOARD_API_URL}; si no está definida,
     * usa {@code http://127.0.0.1/api}. Quita la barra final para normalizar concatenaciones.
     */
    private static String resolveApiBase() {
        String env = System.getenv("LEADERBOARD_API_URL");
        if (env == null || env.isBlank()) {
            return "http://127.0.0.1/api";
        }
        return env.endsWith("/") ? env.substring(0, env.length() - 1) : env;
    }

    /**
     * Publica un puntaje de forma asíncrona. No lanza excepciones: cualquier fallo se registra.
     *
     * @param playerName nombre del jugador
     * @param score      puntaje obtenido en la partida
     * @param result     resultado de la partida ({@code "win"}, {@code "loss"} o {@code "draw"})
     */
    public static void submitAsync(String playerName, int score, String result) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/scores"))
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(toJson(playerName, score, result)))
                    .build();

            HTTP.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(LeaderboardClient::logIfRejected)
                    .exceptionally(ex -> {
                        LOG.log(Level.FINE, "No se pudo enviar el puntaje al leaderboard", ex);
                        return null;
                    });
        } catch (RuntimeException ex) {
            // URI inválida u otro fallo de construcción: el juego continúa sin leaderboard.
            LOG.log(Level.FINE, "Envío al leaderboard descartado", ex);
        }
    }

    /**
     * Consulta los Top-N puntajes del backend (asíncrono). Tolerante a fallos: si la API no
     * responde, completa con lista vacía y registra en nivel FINE.
     * <p>
     * Útil para mostrar un ranking remoto en la pantalla de fin de partida cuando el juego
     * tiene acceso al backend; complementa al {@link logic.RankingManager} local.
     *
     * @param limit cantidad pedida (el backend clampa internamente entre 1 y 100)
     * @return future con la lista de puntajes ordenados descendente por score; nunca lanza
     */
    public static CompletableFuture<List<ScoreEntry>> fetchTopAsync(int limit) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/scores?limit=" + limit))
                    .timeout(Duration.ofSeconds(3))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            return HTTP.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(LeaderboardClient::parseScores)
                    .exceptionally(ex -> {
                        LOG.log(Level.FINE, "GET /scores falló; usando ranking local", ex);
                        return List.of();
                    });
        } catch (RuntimeException ex) {
            LOG.log(Level.FINE, "Construcción de GET /scores descartada", ex);
            return CompletableFuture.completedFuture(List.of());
        }
    }

    /** Registra un warning si la respuesta HTTP indica error (códigos ≥ 300). */
    private static void logIfRejected(HttpResponse<String> response) {
        if (response.statusCode() >= 300) {
            LOG.log(Level.WARNING, "El leaderboard rechazó el puntaje (HTTP {0})",
                    response.statusCode());
        }
    }

    /**
     * Construye el cuerpo JSON del POST {@code /api/scores} usando Gson.
     * <p>
     * NOTE: [sustentación] {@link LinkedHashMap} preserva el orden de inserción de las claves
     * para que el JSON resultante sea idéntico al de la implementación previa (manual). El
     * backend Laravel ignora el orden, pero esto facilita inspeccionar diferencias en logs.
     */
    static String toJson(String playerName, int score, String result) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("player_name", playerName);
        payload.put("score", score);
        if (result != null && !result.isBlank()) {
            payload.put("result", result);
        }
        return GSON.toJson(payload);
    }

    /**
     * Parsea la respuesta de {@code GET /api/scores}. El backend Laravel envuelve los datos en
     * {@code {"data": [...]}} (recurso de colección), pero se tolera también un array plano.
     */
    private static List<ScoreEntry> parseScores(HttpResponse<String> resp) {
        if (resp.statusCode() >= 300) {
            return List.of();
        }
        try {
            JsonElement root = JsonParser.parseString(resp.body());
            JsonArray arr;
            if (root.isJsonObject() && root.getAsJsonObject().has("data")) {
                arr = root.getAsJsonObject().getAsJsonArray("data");
            } else if (root.isJsonArray()) {
                arr = root.getAsJsonArray();
            } else {
                return List.of();
            }
            Type listType = new TypeToken<List<ScoreEntry>>() { }.getType();
            List<ScoreEntry> parsed = GSON.fromJson(arr, listType);
            return parsed != null ? parsed : List.of();
        } catch (RuntimeException ex) {
            LOG.log(Level.FINE, "Respuesta del leaderboard ilegible", ex);
            return List.of();
        }
    }

    /**
     * DTO inmutable de una entrada del ranking devuelta por el backend.
     * <p>
     * Los nombres de campo en JSON usan {@code snake_case}; las anotaciones {@link SerializedName}
     * los mapean al estilo Java {@code camelCase}.
     */
    public static final class ScoreEntry {

        @SerializedName("player_name")
        public final String playerName;

        public final int score;

        public final String result;

        public ScoreEntry(String playerName, int score, String result) {
            this.playerName = playerName;
            this.score = score;
            this.result = result;
        }
    }
}
