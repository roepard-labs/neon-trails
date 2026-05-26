/**
 * Datos de la sección "Pruebas unitarias".
 *
 * Qué es una prueba unitaria y para qué sirve, con ejemplos REALES de
 * src/test/java/logic/. Para primer semestre: se explica el patrón
 * Arrange-Act-Assert desde cero.
 */
import type { CodeSnippet } from './stack'

export interface TestFile {
  archivo: string
  queVerifica: string
}

// ── ¿Qué es una prueba unitaria? ─────────────────────────────────────────────
export const pruebaSimple =
  'Una prueba unitaria es revisar UNA pieza pequeña por separado antes de armar todo el juguete: "si empujo el carrito, ¿avanza?". Aquí, en vez de abrir el juego y probar a mano cada vez, le pedimos a la computadora que verifique sola que cada pieza hace lo que debe.'

export const pruebaDetalle =
  'Usamos JUnit 5. Cada prueba sigue el patrón Arrange-Act-Assert: (1) preparas el escenario, (2) ejecutas la acción, (3) afirmas el resultado esperado con assertEquals/assertTrue. Si la realidad no coincide, la prueba falla en rojo. Se corren con `make test` (o `mvn test`).'

export const porQueLogicaSimple =
  'Solo probamos la carpeta logic/ porque es lógica pura (números y reglas), fácil de revisar sola. La parte visual (ventanas, botones) se prueba a mano siguiendo una lista en docs/PRUEBAS.md, porque automatizar clics es mucho más difícil.'

export const porQueLogicaDetalle =
  'logic/ no importa nada de Swing, así que las pruebas corren "headless" (sin abrir ventanas) y son rápidas y deterministas. La integración continua (.github/workflows/java.yml) ejecuta `mvn -B verify` en cada push: si una prueba falla, GitHub lo marca antes de que llegue a la entrega.'

// ── Los 6 archivos de prueba (DataTable) ─────────────────────────────────────
export const testFiles: TestFile[] = [
  { archivo: 'PlayerTest', queVerifica: 'El jugador: movimiento, vidas y estado (capa pura, sin Swing).' },
  { archivo: 'DiscProjectileTest', queVerifica: 'El disco: avanza por su velocidad, ventana amistosa al dueño, rebotes y quedar quieto.' },
  { archivo: 'GameConstantsTest', queVerifica: 'Invariantes del PDF (moto 5 s, ~60 Hz) y que las constantes de física sean sanas.' },
  { archivo: 'BikeReversalTest', queVerifica: 'Regla "culebrita" en moto: no se puede invertir 180° de golpe.' },
  { archivo: 'GameStateEventsTest', queVerifica: 'Que los eventos (disparo, golpe, rebote…) se emitan cuando deben.' },
  { archivo: 'MotoTrailTest', queVerifica: 'El rastro de la moto: se graba, colisiona, da invulnerabilidad, se erosiona y se limpia al respawn.' },
]

// ── Ejemplos reales ───────────────────────────────────────────────────────────
export const assertSnippet: CodeSnippet = {
  filename: 'logic/GameConstantsTest.java',
  lang: 'java',
  player: 'p1',
  note: 'La prueba más simple posible: afirmar UN hecho. Si alguien cambia la moto a 4 s, esta prueba falla y avisa.',
  code: `class GameConstantsTest {

    @Test
    void bikeDurationMatchesSpec() {
        // El PDF exige modo moto de 5 s; cualquier cambio aquí debe ser deliberado.
        assertEquals(5.0, GameConstants.BIKE_DURATION_SEC, 0.0);
    }

    @Test
    void tickPeriodIsSixtyHz() {
        assertEquals(16, GameConstants.GAME_TICK_MS);
    }
}`,
}

export const aaaSnippet: CodeSnippet = {
  filename: 'logic/DiscProjectileTest.java',
  lang: 'java',
  player: 'p2',
  note: 'El patrón Arrange-Act-Assert: preparar → actuar → comprobar. El disco debe moverse EXACTAMENTE su velocidad.',
  code: `@Test
void tickAdvancesByVelocity() {
    // Arrange: un disco en (100,100) con velocidad (2.5, -1.5)
    DiscProjectile disc = new DiscProjectile(1, 100.0, 100.0, 2.5, -1.5);

    // Act: avanza un tick
    disc.tick();

    // Assert: se movió exactamente esa velocidad
    assertEquals(102.5, disc.getX(), 1e-9);
    assertEquals(98.5, disc.getY(), 1e-9);
}`,
}
