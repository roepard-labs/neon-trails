#!/usr/bin/env bash
# Lanza el escritorio mínimo y el juego dentro del display VNC :1.
# Lo arranca supervisord (programa `game`) una vez Xtigervnc está activo.
set -e
export DISPLAY=:1

# Esperar a que el servidor X (Xtigervnc) responda.
for i in $(seq 1 30); do
  if xdpyinfo -display :1 >/dev/null 2>&1; then
    break
  fi
  sleep 1
done

# Gestor de ventanas mínimo (idempotente: no apilar instancias en reinicios).
if ! pgrep -x openbox >/dev/null 2>&1; then
  openbox &
  sleep 1
fi

# El juego postea los puntajes a LEADERBOARD_API_URL (heredado del entorno;
# por defecto http://127.0.0.1/api → nginx → Laravel, mismo contenedor).
exec java -jar /opt/neon-trails/neon-trails.jar
