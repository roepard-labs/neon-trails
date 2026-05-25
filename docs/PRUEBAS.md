# Checklist de pruebas — base jugable

Marca cada ítem al validar manualmente en tu máquina.

## Arranque

- [ ] `make verify` pasa (compila + tests JUnit + package) sin errores.
- [ ] `make run` levanta la ventana con borde cian y los dos jugadores.

## Botones (interfaz SVG)

Los 4 botones de menú son sprites SVG (`assets/sprites/accionadores/`) rasterizados por
`view.SpriteButton` vía Batik; deben verse como la imagen neón (no como botón de texto plano).

- [ ] **WelcomeScreen**: el botón se ve como el SVG cian "INICIAR JUEGO" (con su glow), no como un botón de texto.
- [ ] **NameInputScreen**: el botón se ve como el SVG cian "JUGAR".
- [ ] **GameOverScreen**: se ven los SVG "REINTENTAR" (cian) y "MENÚ PRINCIPAL" (rosa).
- [ ] El cursor cambia a mano al pasar sobre cualquiera de ellos.
- [ ] Activación por teclado: **Tab** enfoca el botón y **Espacio/Enter** lo dispara igual que el click.
- [ ] Degradación: si faltara un SVG, el botón cae a texto neón legible en vez de romper el arranque.

## Controles simultáneos

- [ ] **P1** se mueve con **W/A/S/D** mientras **P2** se mueve con **flechas** al mismo tiempo.
- [ ] **P1** dispara con **E** (mantener o pulsar) respetando enfriamiento.
- [ ] **P2** dispara con **Enter** respetando enfriamiento.

## Moto (5 s)

- [ ] **P1** con **Q** entra en modo moto: HUD muestra `[MOTO]` y hay halo visual.
- [ ] **P2** con **U** entra en modo moto igual.
- [ ] Tras ~5 s el modo moto termina solo (sin reiniciar la partida).
- [ ] En moto, la reversa directa (ibas a la derecha y pulsas izquierda) **no** te invierte: sigues avanzando; para volver hay que girar (arriba/abajo o diagonal) primero — como la culebrita.
- [ ] Fuera de moto, el movimiento sigue siendo libre (puedes invertir la dirección al instante).

## Discos: rebote, límite y recuperación

- [ ] Cada disparo rebota en bordes laterales (P1 apunta a una pared lateral con E; el disco vuelve sin desaparecer).
- [ ] Cada disparo rebota en bordes superior/inferior (mismo test, apuntando hacia arriba/abajo).
- [ ] Tras **3 rebotes** el disco queda quieto pegado a la pared y se tinta del color del dueño con anillo blanco.
- [ ] **1 disco por jugador**: con un disco en juego (moviéndose o quieto), volver a presionar `E`/`Enter` no genera un segundo disco.
- [ ] **Pickup del dueño**: P1 deja su disco quieto y camina sobre él → el disco desaparece y P1 puede disparar inmediatamente (sin esperar cooldown).
- [ ] **Enemigo atraviesa**: P1 deja su disco quieto y P2 camina sobre él → no pasa nada (sin daño, sin colisión sólida).
- [ ] **Hit con disco en movimiento**: sigue restando vida, suma punto al oponente, reposiciona jugadores y limpia todos los discos.

## Audio (SFX)

Todos los archivos viven en `src/main/resources/audio/sfx/` y se cargan en arranque por `audio.SoundManager.preloadAll()`. Si no hay mixer (CI headless), el manager entra en modo silencioso y el juego sigue funcionando sin sonido.

### Menús

- [ ] Click en "Iniciar Juego" (WelcomeScreen) → suena `ui_click.wav`.
- [ ] Click en "Jugar" (NameInputScreen) → suena `ui_click.wav`.
- [ ] Enter dentro de un campo de nombre → también suena `ui_click.wav` antes de cambiar de pantalla.
- [ ] Click en "Volver al menú" o "Jugar de nuevo" (GameOverScreen) → suena `ui_click.wav` + se corta el jingle de gameover inmediatamente.

### Partida

- [ ] **E / Enter** dispara → suena una variante aleatoria entre `shoot_1.wav`, `shoot_2.wav`, `shoot_3.wav` (jugar varias veces para verificar variabilidad).
- [ ] Disco rebota contra borde → suena `bounce.wav` (hasta 2 veces; el último rebote suena distinto).
- [ ] Disco queda quieto tras el 3er rebote → suena `stop.wav` (no `bounce`).
- [ ] El dueño pasa sobre su disco quieto → suena `pickup.wav` y desaparece el disco.
- [ ] **Q / U** activa moto → suena `bike_init.wav` y comienza un loop de `bike.wav` mientras dura.
- [ ] Si ambos jugadores activan moto simultáneamente, el loop no se duplica (se escucha un único `bike.wav` de fondo).
- [ ] Al expirar la moto (~5 s sin reactivar) → suena `bike_end.wav` y el loop se detiene.
- [ ] Disco enemigo golpea a un jugador → suenan `hit.wav` y luego `respawn.wav`.

### Game Over

- [ ] Tras perder la última vida, en GameOverScreen suena `gameover_1.wav` o `gameover_2.wav` (alterna al azar entre partidas).
- [ ] Volver al menú detiene el jingle de inmediato (no continúa de fondo en WelcomeScreen).
- [ ] Jugar varias partidas seguidas no produce latencia creciente ni clip cortado: el preload evita jitter en el primer disparo de cada partida.

## Web stub

- [ ] Abrir `web/index.html` en el navegador muestra el Top 3 del JSON mock.
- [ ] Los números de “Partida simulada” cambian (pulso demo en `app.js`).

## Regresiones conocidas / TODO

- [ ] Si el foco no está en la ventana, las teclas pueden no registrarse — hacer clic en el panel.
- [ ] FIXME futuro: pantallas del PDF (ranking persistente, música de fondo del menú).
