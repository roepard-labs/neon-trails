/**
 * Datos de la sección "Despliegue".
 *
 * El proyecto se entrega como UNA imagen Docker monolítica que, tras un gateway
 * nginx, sirve las tres capas + el juego por noVNC, orquestadas por supervisord.
 * Snippets reales (condensados) de Dockerfile, docker-compose, nginx, supervisord
 * y env.reference, más los pasos de Dokploy de docs/DEPLOY.md.
 */
import type { CodeSnippet } from './stack'

export interface DeployStep {
  titulo: string
  detalle?: string
}

// ── Analogías "para niños" ────────────────────────────────────────────────
export const dockerSimple =
  'Docker es una caja de mudanza que ya trae TODO adentro: el juego, la web, el guardia de la libreta y hasta el sistema operativo. La armas una vez y funciona igual en tu compu, en la del profe o en la nube — se acabó el clásico “en mi máquina sí servía”.'

export const vncSimple =
  'El juego es de escritorio (una ventana de Java), no una página web. noVNC es como una cámara que graba esa ventana dentro del servidor y te manda el video al navegador; tus clics y teclas viajan de vuelta. Así juegas algo de escritorio… desde una pestaña.'

export const dokploySimple =
  'Dokploy es el ayudante que recibe la caja de mudanza, la abre en un servidor de internet y le pone una dirección web (un dominio) para que cualquiera pueda entrar.'

// ── Diagramas Mermaid ──────────────────────────────────────────────────────
/** Build multi-stage: 3 stages que aportan artefactos → 1 runtime. */
export const buildMermaid = `flowchart LR
  A["web-builder<br/>node 24.16.0<br/>pnpm build"] --> R
  B["php-deps<br/>composer 2<br/>vendor --no-dev"] --> R
  C["java-builder<br/>maven · temurin 17<br/>fat jar (shade)"] --> R
  R["runtime<br/>ubuntu 24.04<br/>PHP 8.5 · JRE 17 · nginx · VNC"] --> IMG[("imagen final<br/>~1.3–1.6 GB")]`

/** Runtime: supervisord arranca y vigila los 5 procesos; nginx enruta por path. */
export const runtimeMermaid = `flowchart TD
  S["supervisord<br/>(PID 1)"] --> N["nginx :80<br/>gateway"]
  S --> P["php-fpm 8.5<br/>Laravel + Filament"]
  S --> X["Xtigervnc :1"]
  S --> W["websockify / noVNC :6080"]
  S --> G["java -jar<br/>el juego Swing"]
  N -->|"/"| WEB["SPA Vue"]
  N -->|"/api · /admin"| P
  N -->|"/game/"| W
  W --> X
  X --> G`

// ── Snippets ────────────────────────────────────────────────────────────────
export const dockerfileSnippet: CodeSnippet = {
  filename: 'Dockerfile (condensado)',
  lang: 'docker',
  player: 'p1',
  note: 'Cada stage prepara una pieza; el último (runtime) las copia todas. Así la imagen final no carga Maven, Composer ni pnpm.',
  code: `# 1) SPA Vue → estáticos
FROM node:24.16.0-bookworm-slim AS web-builder
WORKDIR /web
RUN corepack enable
COPY web/package.json web/pnpm-lock.yaml web/pnpm-workspace.yaml ./
RUN pnpm install --frozen-lockfile
COPY web/ ./
RUN pnpm build                       # → /web/dist

# 2) Dependencias de Laravel (sin dev)
FROM composer:2 AS php-deps
WORKDIR /app
COPY api/composer.json api/composer.lock ./
RUN composer install --no-dev --optimize-autoloader --ignore-platform-reqs --no-scripts
COPY api/ ./
RUN composer dump-autoload --optimize --no-dev --ignore-platform-reqs

# 3) Fat jar del juego (shade: Batik/Xerces dentro)
FROM maven:3.9-eclipse-temurin-17 AS java-builder
WORKDIR /build
COPY pom.xml ./
RUN mvn -B -q dependency:go-offline || true
COPY src ./src
RUN mvn -B -DskipTests package && cp target/neon-trails-*.jar target/app.jar

# 4) Runtime: una imagen que junta las tres capas
FROM ubuntu:24.04 AS runtime
# … PHP 8.5 (ppa:ondrej) + JRE 17 + nginx + supervisor + TigerVNC/noVNC …
COPY --from=web-builder  /web/dist              /var/www/web
COPY --from=php-deps     /app                   /var/www/api
COPY --from=java-builder /build/target/app.jar  /opt/neon-trails/neon-trails.jar
EXPOSE 80
ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]`,
}

export const composeSnippet: CodeSnippet = {
  filename: 'docker-compose.yml (local, condensado)',
  lang: 'yaml',
  player: 'p1',
  note: 'Reproduce Dokploy en tu máquina: la app + una MariaDB 11. En prod, la base es un servicio aparte de Dokploy.',
  code: `services:
  app:
    build: { context: ., dockerfile: Dockerfile }
    environment:
      APP_KEY: \${APP_KEY:-}
      APP_URL: \${APP_URL:-http://localhost}
      DB_CONNECTION: mariadb
      DB_HOST: mariadb
      DB_DATABASE: \${DB_DATABASE:-neon_trails}
      DB_USERNAME: \${DB_USERNAME:-neon}
      DB_PASSWORD: \${DB_PASSWORD:-neon}
    ports:
      - "\${APP_PORT:-80}:80"
    depends_on:
      mariadb: { condition: service_healthy }

  mariadb:
    image: mariadb:11
    environment:
      MARIADB_DATABASE: \${DB_DATABASE:-neon_trails}
      MARIADB_USER: \${DB_USERNAME:-neon}
      MARIADB_PASSWORD: \${DB_PASSWORD:-neon}
    volumes:
      - mariadb-data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "healthcheck.sh", "--connect", "--innodb_initialized"]
      interval: 10s

volumes:
  mariadb-data:`,
}

export const nginxSnippet: CodeSnippet = {
  filename: '.docker/nginx.conf (condensado)',
  lang: 'nginx',
  player: 'p2',
  note: 'Un solo puerto 80 reparte por ruta: / (Vue), /api y /admin (Laravel), /game/ (noVNC). Mismo origen ⇒ sin CORS.',
  code: `server {
    listen 80 default_server;
    root /var/www/web;            # la SPA Vue compilada
    index index.html;

    # Laravel: API, panel Filament, Livewire, health
    location ~ ^/(api|admin|livewire|up)([/-]|$) {
        root /var/www/api/public;
        try_files $uri /index.php?$query_string;
    }

    # WebSocket de noVNC (debe ir ANTES del fallback de la SPA)
    location /websockify {
        proxy_pass http://127.0.0.1:6080/websockify;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    # El juego Swing servido por noVNC
    location /game/ {
        proxy_pass http://127.0.0.1:6080/;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_read_timeout 3600s;        # sesiones VNC largas
    }

    # Fallback de la SPA (history mode)
    location / {
        try_files $uri $uri/ /index.html;
    }
}`,
}

export const supervisordSnippet: CodeSnippet = {
  filename: '.docker/supervisord.conf (condensado)',
  lang: 'ini',
  player: 'p2',
  note: 'supervisord es el director de orquesta: arranca 5 procesos por prioridad y reinicia el que se caiga.',
  code: `[supervisord]
nodaemon=true

[program:php-fpm]
command=/usr/sbin/php-fpm8.5 -F
priority=10

[program:nginx]
command=/usr/sbin/nginx -g 'daemon off;'
priority=20

[program:xvnc]
command=/usr/bin/Xtigervnc :1 -geometry %(ENV_VNC_RESOLUTION)s -depth 24 -SecurityTypes None -localhost yes
priority=30

[program:novnc]
command=/usr/bin/websockify --web=/usr/share/novnc 6080 localhost:5901
priority=40

[program:game]
command=/usr/local/bin/start-game.sh
environment=DISPLAY=":1"
priority=50`,
}

export const envSnippet: CodeSnippet = {
  filename: '.docker/env.reference (extracto)',
  lang: 'ini',
  player: 'p1',
  note: 'La misma lista de variables sirve para `make up` local y para la Application de Dokploy.',
  code: `APP_ENV=production
APP_KEY=                      # cd api && php artisan key:generate --show
APP_URL=https://tu-dominio

DB_CONNECTION=mariadb
DB_HOST=<host MariaDB de Dokploy>
DB_DATABASE=neon_trails
DB_USERNAME=neon
DB_PASSWORD=••••••

LEADERBOARD_API_URL=http://127.0.0.1/api
VNC_RESOLUTION=1280x800

FILAMENT_ADMIN_EMAIL=admin@neon-trails.local
FILAMENT_ADMIN_PASSWORD=••••••`,
}

export const dokploySteps: DeployStep[] = [
  {
    titulo: 'Crear un Project en Dokploy',
    detalle: 'El contenedor lógico que agrupa la base de datos y la aplicación.',
  },
  {
    titulo: 'Añadir Database → MariaDB 11',
    detalle: 'Anota host interno, base, usuario y contraseña: irán en las env vars de la app.',
  },
  {
    titulo: 'Añadir Application tipo Dockerfile',
    detalle: 'Apuntando al repo y rama; usa el Dockerfile de la raíz (build multi-stage).',
  },
  {
    titulo: 'Configurar las variables de entorno',
    detalle: 'APP_KEY, APP_URL, DB_*, LEADERBOARD_API_URL y FILAMENT_ADMIN_* (ver env.reference).',
  },
  {
    titulo: 'Asignar el dominio al puerto 80',
    detalle: 'Traefik (incluido en Dokploy) inyecta el TLS de Let’s Encrypt automáticamente.',
  },
  {
    titulo: 'Deploy',
    detalle:
      'El entrypoint genera APP_KEY si falta, espera la BD, migra, siembra el admin de Filament, cachea config/rutas/vistas y arranca supervisord.',
  },
]
