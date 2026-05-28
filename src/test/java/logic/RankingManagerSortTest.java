package logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del ordenamiento de {@link RankingManager}: validan que el quicksort recursivo deja la
 * lista correctamente ordenada por puntaje descendente en distintos escenarios.
 * <p>
 * Usa {@link TempDir} para que el archivo {@code ranking.json} viva fuera del HOME real del
 * usuario y no contamine ejecuciones reales del juego ni otros tests.
 */
class RankingManagerSortTest {

    @TempDir
    Path tempHome;

    private String originalHome;

    @BeforeEach
    void redirectHome() {
        originalHome = System.getProperty("user.home");
        System.setProperty("user.home", tempHome.toString());
    }

    @org.junit.jupiter.api.AfterEach
    void restoreHome() {
        if (originalHome != null) {
            System.setProperty("user.home", originalHome);
        }
    }

    @Test
    void quicksortDejaListaOrdenadaDescendentemente() {
        RankingManager manager = new RankingManager();
        manager.addEntry("Ana", 3, "loss");
        manager.addEntry("Luis", 9, "win");
        manager.addEntry("Mar", 5, "loss");
        manager.addEntry("Eva", 7, "win");

        List<RankingManager.RankingEntry> top = manager.getTop(4);

        assertEquals(4, top.size());
        assertEquals(9, top.get(0).score, "el mayor primero");
        assertEquals(7, top.get(1).score);
        assertEquals(5, top.get(2).score);
        assertEquals(3, top.get(3).score, "el menor último");
    }

    @Test
    void listaVaciaNoRompeAlPedirTop() {
        RankingManager manager = new RankingManager();
        assertTrue(manager.getTop(3).isEmpty());
        assertTrue(manager.getTop(0).isEmpty());
        assertTrue(manager.getTop(100).isEmpty());
    }

    @Test
    void unaSolaEntradaSiempreEsLaPrimera() {
        RankingManager manager = new RankingManager();
        manager.addEntry("Solo", 1, "win");

        List<RankingManager.RankingEntry> top = manager.getTop(3);
        assertEquals(1, top.size());
        assertEquals("Solo", top.get(0).playerName);
    }

    @Test
    void scoresIgualesNoRompenElOrden() {
        RankingManager manager = new RankingManager();
        manager.addEntry("A", 5, "win");
        manager.addEntry("B", 5, "loss");
        manager.addEntry("C", 5, "win");

        List<RankingManager.RankingEntry> top = manager.getTop(3);
        assertEquals(3, top.size());
        // Todos con score 5: lo importante es que la lista no se corrompa.
        for (RankingManager.RankingEntry e : top) {
            assertEquals(5, e.score);
        }
    }

    @Test
    void quicksortOrdena100EntradasAleatorias() {
        List<RankingManager.RankingEntry> list = new ArrayList<>();
        Random rnd = new Random(42);
        for (int i = 0; i < 100; i++) {
            list.add(new RankingManager.RankingEntry("P" + i, rnd.nextInt(1000), "win", "t"));
        }

        RankingManager.quicksortDescByScore(list, 0, list.size() - 1);

        for (int i = 0; i < list.size() - 1; i++) {
            assertTrue(list.get(i).score >= list.get(i + 1).score,
                    "fallo en el índice " + i + ": " + list.get(i).score + " < " + list.get(i + 1).score);
        }
    }

    @Test
    void recursionAceptaTamanosCero1y2() {
        // Caso base explícito: tramos triviales no deben fallar ni recursar infinito.
        List<RankingManager.RankingEntry> vacia = new ArrayList<>();
        RankingManager.quicksortDescByScore(vacia, 0, -1);
        assertTrue(vacia.isEmpty());

        List<RankingManager.RankingEntry> uno = new ArrayList<>();
        uno.add(new RankingManager.RankingEntry("a", 1, "win", "t"));
        RankingManager.quicksortDescByScore(uno, 0, 0);
        assertEquals(1, uno.size());

        List<RankingManager.RankingEntry> dos = new ArrayList<>();
        dos.add(new RankingManager.RankingEntry("a", 1, "win", "t"));
        dos.add(new RankingManager.RankingEntry("b", 2, "win", "t"));
        RankingManager.quicksortDescByScore(dos, 0, 1);
        assertEquals(2, dos.get(0).score);
        assertEquals(1, dos.get(1).score);
    }
}
