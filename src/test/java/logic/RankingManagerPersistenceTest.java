package logic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de persistencia de {@link RankingManager}: garantizan que escribir/leer
 * {@code ~/.neon-trails/ranking.json} sobrevive a la creación de una nueva instancia y
 * degrada a vacío cuando el archivo está corrupto o ausente.
 * <p>
 * Redirige {@code user.home} a una carpeta temporal para no tocar el HOME real del usuario.
 */
class RankingManagerPersistenceTest {

    @TempDir
    Path tempHome;

    private String originalHome;
    private Path rankingFile;

    @BeforeEach
    void redirectHome() {
        originalHome = System.getProperty("user.home");
        System.setProperty("user.home", tempHome.toString());
        rankingFile = tempHome.resolve(".neon-trails").resolve("ranking.json");
    }

    @AfterEach
    void restoreHome() {
        if (originalHome != null) {
            System.setProperty("user.home", originalHome);
        }
    }

    @Test
    void sinArchivoArrancaVacio() {
        RankingManager manager = new RankingManager();
        assertTrue(manager.getAll().isEmpty());
    }

    @Test
    void addEntryPersisteAlArchivoYNuevaInstanciaLoRecupera() throws Exception {
        RankingManager primero = new RankingManager();
        primero.addEntry("Juan", 5, "win");
        primero.addEntry("Jacobo", 3, "loss");

        assertTrue(Files.exists(rankingFile), "addEntry debe crear el archivo");

        RankingManager segundo = new RankingManager();
        List<RankingManager.RankingEntry> top = segundo.getTop(2);
        assertEquals(2, top.size());
        assertEquals("Juan", top.get(0).playerName);
        assertEquals(5, top.get(0).score);
        assertEquals("Jacobo", top.get(1).playerName);
    }

    @Test
    void jsonCorruptoDegradaAVacioSinLanzar() throws Exception {
        Files.createDirectories(rankingFile.getParent());
        Files.writeString(rankingFile, "esto-no-es-json");

        RankingManager manager = new RankingManager();
        assertTrue(manager.getAll().isEmpty(),
                "JSON inválido debe degradar a lista vacía sin propagar excepción");

        // Y debe seguir funcionando: una nueva entrada se escribe limpio sobre el corrupto.
        manager.addEntry("Recovery", 1, "win");
        assertEquals(1, manager.getAll().size());
    }

    @Test
    void varisasEntradasMantienenOrdenTrasReabrir() {
        RankingManager primero = new RankingManager();
        primero.addEntry("A", 2, "win");
        primero.addEntry("B", 9, "win");
        primero.addEntry("C", 4, "loss");

        RankingManager segundo = new RankingManager();
        List<RankingManager.RankingEntry> top = segundo.getTop(3);
        assertEquals(3, top.size());
        assertEquals(9, top.get(0).score);
        assertEquals(4, top.get(1).score);
        assertEquals(2, top.get(2).score);
    }
}
