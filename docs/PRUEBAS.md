# Checklist de pruebas — base jugable

Marca cada ítem al validar manualmente en tu máquina.

## Arranque

- [ ] `javac` compila todos los `.java` sin errores.
- [ ] Al ejecutar `Main`, aparece la ventana con borde cian y dos jugadores.

## Controles simultáneos

- [ ] **P1** se mueve con **W/A/S/D** mientras **P2** se mueve con **flechas** al mismo tiempo.
- [ ] **P1** dispara con **Shift** (mantener o pulsar) respetando enfriamiento.
- [ ] **P2** dispara con **Enter** respetando enfriamiento.

## Moto (5 s)

- [ ] **P1** con **Q** entra en modo moto: HUD muestra `[MOTO]` y hay halo visual.
- [ ] **P2** con **U** entra en modo moto igual.
- [ ] Tras ~5 s el modo moto termina solo (sin reiniciar la partida).

## Discos y bordes

- [ ] Los discos se mueven y desaparecen al tocar el borde del panel.
- [ ] Un impacto válido suma punto al oponente y reposiciona jugadores; limpia discos.

## Web stub

- [ ] Abrir `web/index.html` en el navegador muestra el Top 3 del JSON mock.
- [ ] Los números de “Partida simulada” cambian (pulso demo en `app.js`).

## Regresiones conocidas / TODO

- [ ] Si el foco no está en la ventana, las teclas pueden no registrarse — hacer clic en el panel.
- [ ] FIXME futuro: pantallas del PDF (menú, game over, ranking persistente, sonidos).
