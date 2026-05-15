# TODO Neon-trails
Que hay por hacer

# 14may2026
Indexar sonidos sfx/bg, iconos, imagenes.
Formatos adecuados .svg para animaciones.
- [ ] hola

## Sonidos requeridos (Hito 4)

PDF exige **4 sonidos mínimos**. Formato obligatorio: **`.wav` PCM 16-bit**, mono (estéreo opcional para los últimos dos), 44.1 o 22.05 kHz, <200 KB cada uno. Ubicación: `src/resources/sounds/`. Generar los 3 primeros en 30 s cada uno con [jsfxr](https://sfxr.me) (CC0, browser, sin cuenta).

- [ ] `shoot.wav` — disparo de disco (Shift / Enter). 80–150 ms. "Pew" / blip electrónico corto. jsfxr preset: **Laser/Shoot**.
- [ ] `hit.wav` — golpe confirmado, pérdida de vida. 200–400 ms. Impacto seco / glitch. jsfxr preset: **Hit/Hurt**.
- [ ] `bike.wav` — activación de modo moto (Q / U). 300–600 ms. Whoosh / power-up. jsfxr preset: **Powerup**.
- [ ] `gameover.wav` — al entrar a GameOverScreen. 1000–2000 ms. Jingle / stinger descendente. Buscar en [kenney.nl](https://kenney.nl) (CC0) u [opengameart.org](https://opengameart.org) filtrando por CC0.

**Restricciones técnicas**:
- Nada de `.mp3` (Java SE no lo soporta sin librería externa, rompería la regla "cero dependencias").
- Toda fuente externa debe ser CC0 o licencia compatible (registrar atribución en `CreditsScreen` cuando aplique).

**Hito 4 listo para implementar cuando los 4 archivos estén en `src/resources/sounds/`.** Música de fondo de menú/juego es opcional, no requerida por el PDF — iría a un Hito 5 separado si la pides.