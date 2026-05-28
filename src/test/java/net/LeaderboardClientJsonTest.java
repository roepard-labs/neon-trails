package net;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del serializador JSON de {@link LeaderboardClient}. Validan que el cambio de escapado
 * manual a Gson preserva el contrato con el backend Laravel: claves {@code player_name},
 * {@code score} y, opcionalmente, {@code result}; y que caracteres especiales en el nombre del
 * jugador se serializan de forma round-trip correcta.
 * <p>
 * No realizan ninguna llamada de red — sólo invocan al builder estático {@code toJson}.
 */
class LeaderboardClientJsonTest {

    @Test
    void jsonBasicoTieneLasClavesEsperadas() {
        String json = LeaderboardClient.toJson("Juan", 7, "win");
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        assertEquals("Juan", obj.get("player_name").getAsString());
        assertEquals(7, obj.get("score").getAsInt());
        assertEquals("win", obj.get("result").getAsString());
    }

    @Test
    void resultNuloOmiteLaClave() {
        String json = LeaderboardClient.toJson("Solo", 3, null);
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        assertEquals("Solo", obj.get("player_name").getAsString());
        assertFalse(obj.has("result"), "result null no debe aparecer en el payload");
    }

    @Test
    void resultBlancoOmiteLaClave() {
        String json = LeaderboardClient.toJson("Otro", 1, "   ");
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        assertFalse(obj.has("result"));
    }

    @Test
    void caracteresEspecialesRoundTrip() {
        String original = "Ana \"la rápida\"\nñoño \\Patiño 😀";
        String json = LeaderboardClient.toJson(original, 5, "loss");
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        assertEquals(original, obj.get("player_name").getAsString(),
                "Gson debe escapar y re-parsear sin perder caracteres");
    }

    @Test
    void scoreCeroEsValido() {
        String json = LeaderboardClient.toJson("Zero", 0, "loss");
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        assertEquals(0, obj.get("score").getAsInt());
    }

    @Test
    void nombreVacioNoRompe() {
        String json = LeaderboardClient.toJson("", 0, null);
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        assertTrue(obj.has("player_name"));
        assertEquals("", obj.get("player_name").getAsString());
    }
}
