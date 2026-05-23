# Librerías multimedia recomendadas — Neon Trails

Proyecto: Java 17 + Swing, Maven.

## Política de dependencias

**Se permite agregar dependencias de runtime cuando el beneficio lo justifique:**

- ✅ **Sí**: cuando Java SE no lo soporta nativamente (MP3, MP4) o la implementación manual es frágil/compleja (SVG, JSON).
- ✅ **Sí**: cuando la librería es ligera, madura y con pocas dependencias transitivas.
- ❌ **No**: "porque sí" o cuando se puede resolver con ≤20 líneas de código estándar.

Cada dependencia debe **justificarse en el PDF entregable** (sección "Librerías utilizadas y justificación") y, cuando aplique, anotar su licencia para incluirla en `CreditsScreen`.

Cada sección cubre **una** librería, la más adecuada para el formato.

---

## 🖼️ SVG — Apache Batik

**Maven:**
```xml
<dependency>
    <groupId>org.apache.xmlgraphics</groupId>
    <artifactId>batik-all</artifactId>
    <version>1.17</version>
</dependency>
```

**Por qué esta:** Es el estándar de facto en Java para SVG. Usado por Apache POI, JasperReports, Gephi. Renderiza a `BufferedImage` y lo dibujas con `Graphics2D` en cualquier `JPanel`.

**Ejemplo mínimo (cargar SVG y pintarlo):**
```java
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

// Cargar SVG como BufferedImage
PNGTranscoder transcoder = new PNGTranscoder();
try (InputStream svgStream = getClass().getResourceAsStream("/assets/icon.svg")) {
    TranscoderInput input = new TranscoderInput(svgStream);
    BufferedImageTranscoder output = new BufferedImageTranscoder();
    transcoder.transcode(input, null);
    BufferedImage image = output.getBufferedImage();
    // luego: g2d.drawImage(image, x, y, null);
}
```

**Alternativa ligera:** `com.formdev:svgSalamander:1.1.4` (~400 KB vs ~8 MB). API más simple pero menos features.

---

## 🔉 MP3 — JLayer

**Maven:**
```xml
<dependency>
    <groupId>javazoom</groupId>
    <artifactId>jlayer</artifactId>
    <version>1.0.1</version>
</dependency>
```

**Por qué esta:** Puro Java (~120 KB), sin dependencias nativas, probado por décadas. Ideal para efectos de sonido y música de fondo en un juego 2D.

**Ejemplo mínimo (reproducir MP3):**
```java
import javazoom.jl.player.Player;
import java.io.InputStream;

try (InputStream mp3Stream = getClass().getResourceAsStream("/assets/sound.mp3")) {
    Player player = new Player(mp3Stream);
    player.play(); // bloquea hasta que termine → ejecutar en un hilo aparte
}
```

**Para playback no bloqueante con control (pause/stop/loop):**
```java
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackListener;

// En un hilo separado:
AdvancedPlayer player = new AdvancedPlayer(inputStream);
player.setPlayBackListener(new PlaybackListener() {
    @Override
    public void playbackFinished(PlaybackEvent evt) { /* loop si quieres */ }
});
new Thread(() -> { try { player.play(); } catch (Exception e) { /* manejar */ } }).start();
```

**Alternativa con SPI de javax.sound:** `com.googlecode.soundlibs:mp3spi:1.9.5.4` agrega soporte MP3 a `AudioSystem`, pero requiere JLayer + Tritonus, más dependencias para lo mismo.

---

## 🎬 MP4 — VLCJ

**Maven:**
```xml
<dependency>
    <groupId>uk.co.caprica</groupId>
    <artifactId>vlcj</artifactId>
    <version>4.8.0</version>
</dependency>
```

**Por qué esta:** Bindings Java para LibVLC (el motor de VLC). Soporta **todos los formatos** (MP4, MP3, MKV, OGG, etc.) y se embebe directamente en un `Canvas` de Swing. Reproduce video, audio, streaming, y hasta captura frames.

**⚠️ Requisito:** VLC (o al menos `libvlc`) debe estar instalado en el sistema. En Linux: `sudo apt install vlc`. Para distribuir el juego, se empaquetan los `.so`/`.dll` en `src/main/resources/natives/`.

**Ejemplo mínimo (reproducir MP4 en un JPanel):**
```java
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import javax.swing.*;
import java.awt.*;

// Crear componente embebido
EmbeddedMediaPlayerComponent mediaPlayer = new EmbeddedMediaPlayerComponent();
JFrame frame = new JFrame("Video");
frame.setContentPane(mediaPlayer); // lo agrega al frame
frame.setSize(800, 600);
frame.setVisible(true);

// Reproducir archivo
mediaPlayer.mediaPlayer().media().play("/ruta/al/video.mp4");
```

**Notas importantes:**
- `vlcj-4.x` requiere VLC 4.x (en desarrollo). Para VLC 3.x estable, usar `vlcj:3.12.1`.
- El playback es asíncrono (no bloquea el EDT).
- También reproduce MP3, así que si usas VLCJ no necesitas JLayer.
- Pesa ~2 MB el JAR + lo que pesen las librerías nativas de VLC.

**Alternativa sin nativos:** `org.jcodec:jcodec:0.2.5` decodifica MP4 puro Java, pero su API es mucho más compleja y el rendimiento inferior a VLCJ.

---

## 📄 JSON — Gson

**Maven:**
```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.11.0</version>
</dependency>
```

**Por qué esta:** Serializar/deserializar JSON a mano es frágil (escapado de strings, Unicode, anidamiento de objetos, fechas). Gson (~300 KB, sin dependencias transitivas) es la opción más ligera y madura para Java SE. Mantenida por Google, API mínima (`new Gson().toJson(...)` y `.fromJson(...)`).

**Ejemplo mínimo (leer y escribir `leaderboard.json`):**
```java
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;

Gson gson = new Gson();
Path file = Path.of(System.getProperty("user.home"), ".neontrails", "leaderboard.json");

// Leer
Type listType = new TypeToken<List<RankingEntry>>(){}.getType();
List<RankingEntry> entries;
try (FileReader reader = new FileReader(file.toFile())) {
    entries = gson.fromJson(reader, listType);
}

// Escribir
try (FileWriter writer = new FileWriter(file.toFile())) {
    gson.toJson(entries, writer);
}
```

**Alternativa más potente:** `com.fasterxml.jackson.core:jackson-databind:2.17.x` (~1.5 MB con dependencias). Más rápido en payloads grandes pero overkill para un leaderboard de Top 3.

---

## Resumen

| Formato | Librería | Peso | ¿Requiere nativos? |
|---|---|---|---|
| SVG | Apache Batik 1.17 | ~8 MB | No |
| MP3 | JLayer 1.0.1 | ~120 KB | No |
| MP4 | VLCJ 4.x | ~2 MB + VLC | Sí (VLC instalado o empaquetado) |
| JSON | Gson 2.11.0 | ~300 KB | No |

Si se necesita video **sin dependencias nativas**, evaluar `jcodec` (MP4 puro Java) o `JavaFX` (`JFXPanel` + `MediaPlayer`).
