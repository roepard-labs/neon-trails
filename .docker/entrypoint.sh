#!/usr/bin/env bash
# Entrypoint del monolito: prepara Laravel y arranca supervisord.
set -euo pipefail
cd /var/www/api

echo "[entrypoint] Neon Trails — preparando backend Laravel..."

# APP_KEY: usar el del entorno; si falta, generar uno efímero válido.
if [ -z "${APP_KEY:-}" ]; then
  export APP_KEY="base64:$(head -c 32 /dev/urandom | base64)"
  echo "[entrypoint] APP_KEY no provisto; generado uno efímero (define APP_KEY en producción)."
fi

# Esperar a la base de datos y aplicar migraciones.
echo "[entrypoint] Aplicando migraciones..."
for i in $(seq 1 30); do
  if php artisan migrate --force 2>/dev/null; then
    echo "[entrypoint] Migraciones aplicadas."
    break
  fi
  echo "[entrypoint] BD no disponible aún (intento $i/30)..."
  sleep 2
done

# Usuario admin de Filament (idempotente: updateOrCreate por email).
php artisan db:seed --class=AdminUserSeeder --force 2>/dev/null || true

# Enlace de storage y assets de Filament.
php artisan storage:link 2>/dev/null || true
php artisan filament:assets 2>/dev/null || true

# Descubrir paquetes y cachear para producción (lee el entorno real ya disponible).
php artisan package:discover --ansi 2>/dev/null || true
php artisan config:cache 2>/dev/null || true
php artisan route:cache 2>/dev/null || true
php artisan view:cache 2>/dev/null || true

# Asegurar que www-data sea dueño de lo que php-fpm reescribe en runtime.
chown -R www-data:www-data storage bootstrap/cache 2>/dev/null || true

# El VNC corre sin autenticación (SecurityTypes None) atado a localhost: solo es
# accesible por websockify y, desde fuera, por nginx /game/. Para exigir auth en
# producción, añade basic-auth al location /game/ (nginx) o en Traefik/Dokploy.

echo "[entrypoint] Arrancando supervisord (nginx + php-fpm + VNC + juego)..."
exec /usr/bin/supervisord -n -c /etc/supervisor/supervisord.conf
