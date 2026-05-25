package net;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cliente HTTP mínimo que publica puntajes en la API del leaderboard (backend Laravel).
 * <p>
 * NOTE: Usa {@link java.net.http.HttpClient} nativo (Java 11+) para no añadir dependencias al POM.
 * Es <b>tolerante a fallos</b>: si la API no responde o falla, se registra y se descarta — nunca
 * interrumpe ni bloquea el juego. El envío es asíncrono ({@code sendAsync}) para no congelar la EDT
 * ni el hilo de simulación.
 * <p>
 * La URL base se toma de la variable de entorno {@code LEADERBOARD_API_URL}; si no está definida,
 * usa {@code http://127.0.0.1/api} (el gateway nginx dentro del contenedor monolítico).
 */
public final class LeaderboardClient {

    private static final Logger LOG = Logger.getLogger(LeaderboardClient.class.getName());

    /** Base de la API, normalizada sin barra final. */
    private static final String API_BASE = resolveApiBase();

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    private LeaderboardClient() {
    }

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

    private static void logIfRejected(HttpResponse<String> response) {
        if (response.statusCode() >= 300) {
            LOG.log(Level.WARNING, "El leaderboard rechazó el puntaje (HTTP {0})",
                    response.statusCode());
        }
    }

    /** Serializa el puntaje a JSON a mano (payload mínimo; evita depender de una librería JSON). */
    private static String toJson(String playerName, int score, String result) {
        StringBuilder sb = new StringBuilder(64);
        sb.append('{')
                .append("\"player_name\":\"").append(escape(playerName)).append("\",")
                .append("\"score\":").append(score);
        if (result != null && !result.isBlank()) {
            sb.append(",\"result\":\"").append(escape(result)).append('"');
        }
        return sb.append('}').toString();
    }

    /** Escapa los caracteres especiales de JSON en una cadena. */
    private static String escape(String s) {
        StringBuilder sb = new StringBuilder(s.length() + 8);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }
}
