/**
 * Datos de la sección "Librerías".
 *
 * Qué es una librería/dependencia y para qué sirve, con las dependencias REALES
 * del juego (pom.xml) y los plugins de Maven. Para primer semestre: se explica
 * desde cero qué es Maven y por qué no reinventamos la rueda.
 */
import type { CodeSnippet } from './stack'

export interface Dependencia {
  libreria: string
  version: string
  queEs: string
  paraQue: string
}

export interface MavenPlugin {
  plugin: string
  queHace: string
}

// ── ¿Qué es una librería? ¿Qué es Maven? ─────────────────────────────────────
export const libreriaSimple =
  'Una librería es una caja de piezas de Lego que alguien ya fabricó y probó. En vez de moldear tú el plástico (escribir todo desde cero), tomas la pieza que necesitas y la encajas. Una "dependencia" es justo eso: una librería de la que tu proyecto depende.'

export const mavenSimple =
  'Maven es el ayudante que va a buscar esas cajas de Lego por internet, las baja en la versión exacta que pediste y arma el proyecto por ti. Tú solo escribes una lista (el pom.xml) y Maven se encarga del resto: descargar, compilar, probar y empaquetar.'

export const mavenDetalle =
  'pom.xml es la lista de la compra del proyecto Java. Cada <dependency> dice grupo, nombre y versión; Maven las descarga a un repositorio local y las pone en el classpath. Fijar la versión hace que el build sea reproducible: a ti y al profe les baja exactamente lo mismo.'

// ── Dependencias del juego (DataTable) ───────────────────────────────────────
export const dependencias: Dependencia[] = [
  {
    libreria: 'batik-transcoder',
    version: '1.17',
    queEs: 'Conversor de SVG de Apache Batik',
    paraQue: 'Convierte los sprites SVG animados en imágenes que Swing puede dibujar (view/SpriteLoader).',
  },
  {
    libreria: 'batik-codec',
    version: '1.17',
    queEs: 'Códecs de imagen de Batik',
    paraQue: 'Da soporte a los formatos que Batik necesita al rasterizar.',
  },
  {
    libreria: 'xercesImpl',
    version: '2.12.2',
    queEs: 'Parser de XML de Apache',
    paraQue: 'Batik lo exige en tiempo de ejecución para leer el SVG; sin él, los sprites no cargan.',
  },
  {
    libreria: 'jsoup',
    version: '1.19.1',
    queEs: 'Parser de HTML',
    paraQue: 'Leer y limpiar HTML de forma segura.',
  },
  {
    libreria: 'junit-jupiter',
    version: '5.10.2',
    queEs: 'Framework de pruebas (JUnit 5)',
    paraQue: 'Escribir y correr las pruebas unitarias. Solo en pruebas (scope test), no viaja en el juego final.',
  },
]

// ── Plugins de Maven (qué pasa en cada fase) ─────────────────────────────────
export const plugins: MavenPlugin[] = [
  { plugin: 'maven-compiler-plugin', queHace: 'Compila el código fuente a Java 17.' },
  { plugin: 'maven-surefire-plugin', queHace: 'Corre las pruebas en `mvn test` (en modo headless, sin abrir ventanas).' },
  { plugin: 'maven-jar-plugin', queHace: 'Empaqueta un .jar con Main como punto de entrada.' },
  { plugin: 'exec-maven-plugin', queHace: 'Permite `mvn exec:java`: arranca el juego sin tener que empaquetar.' },
  { plugin: 'maven-shade-plugin', queHace: 'Crea el "fat jar": mete TODAS las librerías dentro de un único .jar ejecutable.' },
]

// ── Snippet real del pom.xml ──────────────────────────────────────────────────
export const pomSnippet: CodeSnippet = {
  filename: 'pom.xml — <dependencies>',
  lang: 'xml',
  player: 'p1',
  note: 'Cada bloque <dependency> es una pieza de Lego que Maven baja en esa versión exacta.',
  code: `<dependencies>
  <!-- Apache Batik: convierte los SVG animados a imagen para Swing -->
  <dependency>
    <groupId>org.apache.xmlgraphics</groupId>
    <artifactId>batik-transcoder</artifactId>
    <version>1.17</version>
  </dependency>
  <dependency>
    <groupId>org.apache.xmlgraphics</groupId>
    <artifactId>batik-codec</artifactId>
    <version>1.17</version>
  </dependency>

  <!-- Batik pide este parser XML en runtime; sin él, los sprites no cargan -->
  <dependency>
    <groupId>xerces</groupId>
    <artifactId>xercesImpl</artifactId>
    <version>2.12.2</version>
  </dependency>

  <!-- Parser de HTML -->
  <dependency>
    <groupId>org.jsoup</groupId>
    <artifactId>jsoup</artifactId>
    <version>1.19.1</version>
  </dependency>

  <!-- Pruebas unitarias (solo durante los tests) -->
  <dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.2</version>
    <scope>test</scope>
  </dependency>
</dependencies>`,
}
