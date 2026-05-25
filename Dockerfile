# syntax=docker/dockerfile:1
# ============================================================================
#  Neon Trails — imagen monolítica (entrypoint de Dokploy)
#
#  Una sola imagen sirve, detrás de un gateway nginx, las tres capas:
#    /        → SPA Vue (landing + presentación + leaderboard)
#    /api     → API Laravel (php-fpm) sobre MariaDB
#    /admin   → panel Filament del leaderboard
#    /game/   → el juego Java Swing, jugable en el navegador vía noVNC
#
#  MariaDB vive FUERA de esta imagen (servicio Database de Dokploy en prod,
#  o el servicio `mariadb` de docker-compose.yml en local).
#  Los complementos (nginx, supervisord, entrypoint, etc.) viven en .docker/.
# ============================================================================

# ─────────────────────────────────────────────────────────────────────────
#  Stage 1 — web-builder: compila la SPA Vue a estáticos (Node 24.16.0)
# ─────────────────────────────────────────────────────────────────────────
FROM node:24.16.0-bookworm-slim AS web-builder
WORKDIR /web
# corepack activa el pnpm pineado en package.json (packageManager: pnpm@11.0.9)
RUN corepack enable
COPY web/package.json web/pnpm-lock.yaml web/pnpm-workspace.yaml ./
RUN pnpm install --frozen-lockfile
COPY web/ ./
RUN pnpm build   # → /web/dist

# ─────────────────────────────────────────────────────────────────────────
#  Stage 2 — php-deps: vendor de Laravel sin dev-deps (Composer 2)
# ─────────────────────────────────────────────────────────────────────────
FROM composer:2 AS php-deps
WORKDIR /app
# Cache de dependencias: primero solo los manifiestos.
# --ignore-platform-reqs: la imagen composer no trae todas las extensiones (intl, gd…),
# pero el runtime sí; con el lock ya resuelto, esto solo descarga las versiones fijadas.
COPY api/composer.json api/composer.lock ./
RUN composer install --no-dev --no-interaction --no-scripts --prefer-dist --optimize-autoloader --ignore-platform-reqs
# Luego el código de la app y se vuelve a optimizar el autoloader.
COPY api/ ./
RUN composer dump-autoload --optimize --no-dev --no-interaction --ignore-platform-reqs

# ─────────────────────────────────────────────────────────────────────────
#  Stage 3 — java-builder: fat jar del juego (Maven + Temurin 17)
# ─────────────────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS java-builder
WORKDIR /build
# Cache de dependencias Maven.
COPY pom.xml ./
RUN mvn -B -q dependency:go-offline || true
COPY src ./src
# El maven-shade-plugin produce un jar ejecutable con Batik/Xerces/jsoup dentro.
RUN mvn -B -DskipTests package \
 && cp target/neon-trails-*.jar target/app.jar

# ─────────────────────────────────────────────────────────────────────────
#  Stage 4 — runtime: Ubuntu 24.04 + PHP 8.5 + JRE 17 + nginx + VNC/noVNC
# ─────────────────────────────────────────────────────────────────────────
FROM ubuntu:24.04 AS runtime

ENV DEBIAN_FRONTEND=noninteractive \
    LANG=es_CO.UTF-8 \
    LANGUAGE=es_CO:es \
    LC_ALL=es_CO.UTF-8 \
    DISPLAY=:1 \
    VNC_RESOLUTION=1280x800 \
    LEADERBOARD_API_URL=http://127.0.0.1/api

# 1) Base del sistema + PHP 8.5 (PPA ondrej) + JRE 17 + nginx + escritorio VNC mínimo
RUN apt-get update \
 && apt-get install -y --no-install-recommends \
      ca-certificates curl gnupg software-properties-common locales \
 && add-apt-repository -y ppa:ondrej/php \
 && apt-get update \
 && apt-get install -y --no-install-recommends \
      php8.5-fpm php8.5-cli php8.5-mysql php8.5-mbstring php8.5-xml \
      php8.5-bcmath php8.5-intl php8.5-zip php8.5-gd php8.5-curl php8.5-sqlite3 \
      nginx supervisor \
      openjdk-17-jre \
      tigervnc-standalone-server tigervnc-common novnc websockify \
      openbox dbus-x11 x11-utils x11-xserver-utils \
      fonts-dejavu-core fontconfig \
 && locale-gen es_CO.UTF-8 \
 && mkdir -p /run/php /var/log/supervisor /root/.vnc /opt/neon-trails \
 && apt-get purge -y software-properties-common gnupg \
 && apt-get autoremove -y \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/*

# 2) Artefactos de las tres capas
COPY --from=web-builder /web/dist            /var/www/web
COPY --from=php-deps    /app                 /var/www/api
COPY --from=java-builder /build/target/app.jar /opt/neon-trails/neon-trails.jar

# 3) Configuración (complementos en .docker/)
COPY .docker/nginx.conf       /etc/nginx/conf.d/default.conf
COPY .docker/php-fpm.conf      /etc/php/8.5/fpm/pool.d/www.conf
COPY .docker/supervisord.conf  /etc/supervisor/supervisord.conf
COPY .docker/vnc-startup.sh    /usr/local/bin/start-game.sh
COPY .docker/entrypoint.sh     /usr/local/bin/entrypoint.sh

# 4) Permisos: quitar el site por defecto de nginx, hacer ejecutables los scripts,
#    y dar a www-data las carpetas escribibles de Laravel.
RUN rm -f /etc/nginx/sites-enabled/default \
 && chmod +x /usr/local/bin/start-game.sh /usr/local/bin/entrypoint.sh \
 && chown -R www-data:www-data /var/www/api/storage /var/www/api/bootstrap/cache \
 && chmod -R 775 /var/www/api/storage /var/www/api/bootstrap/cache

EXPOSE 80

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
