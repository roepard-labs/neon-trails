# Checklist de pruebas — base jugable

Marca cada ítem al validar manualmente en tu máquina.

## Arranque

- [ ] `make verify` pasa (compila + tests JUnit + package) sin errores.
- [ ] `make run` levanta la ventana con borde cian y los dos jugadores.

## Controles simultáneos

- [ ] **P1** se mueve con **W/A/S/D** mientras **P2** se mueve con **flechas** al mismo tiempo.
- [ ] **P1** dispara con **Shift** (mantener o pulsar) respetando enfriamiento.
- [ ] **P2** dispara con **Enter** respetando enfriamiento.

## Moto (5 s)

- [ ] **P1** con **Q** entra en modo moto: HUD muestra `[MOTO]` y hay halo visual.
- [ ] **P2** con **U** entra en modo moto igual.
- [ ] Tras ~5 s el modo moto termina solo (sin reiniciar la partida).

## Discos: rebote, límite y recuperación

- [ ] Cada disparo rebota en bordes laterales (P1 apunta a una pared lateral con Shift; el disco vuelve sin desaparecer).
- [ ] Cada disparo rebota en bordes superior/inferior (mismo test, apuntando hacia arriba/abajo).
- [ ] Tras **3 rebotes** el disco queda quieto pegado a la pared y se tinta del color del dueño con anillo blanco.
- [ ] **1 disco por jugador**: con un disco en juego (moviéndose o quieto), volver a presionar `Shift`/`Enter` no genera un segundo disco.
- [ ] **Pickup del dueño**: P1 deja su disco quieto y camina sobre él → el disco desaparece y P1 puede disparar inmediatamente (sin esperar cooldown).
- [ ] **Enemigo atraviesa**: P1 deja su disco quieto y P2 camina sobre él → no pasa nada (sin daño, sin colisión sólida).
- [ ] **Hit con disco en movimiento**: sigue restando vida, suma punto al oponente, reposiciona jugadores y limpia todos los discos.

## Web stub

- [ ] Abrir `web/index.html` en el navegador muestra el Top 3 del JSON mock.
- [ ] Los números de “Partida simulada” cambian (pulso demo en `app.js`).

## Regresiones conocidas / TODO

- [ ] Si el foco no está en la ventana, las teclas pueden no registrarse — hacer clic en el panel.
- [ ] FIXME futuro: pantallas del PDF (menú, game over, ranking persistente, sonidos).
