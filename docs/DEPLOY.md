# Despliegue — Neon Trails (monolito en Dokploy)

Neon Trails se despliega como **una sola imagen Docker monolítica** que, detrás de un
gateway nginx, sirve las tres capas del proyecto. **MariaDB vive fuera** de la imagen
(servicio Database de Dokploy en producción; servicio `mariadb` de Compose en local).

## Arquitectura

```
Dokploy (Application) → Dockerfile (multi-stage) → imagen Ubuntu 24.04
  └─ supervisord
       ├─ nginx :80  (gateway, único puerto expuesto)
       │    /        → /var/www/web         (SPA Vue, fallback SPA)
       │    /api     → php-fpm Laravel       (API del leaderboard)
       │    /admin   → php-fpm Laravel       (panel Filament)
       │    /game/   → 127.0.0.1:6080        (noVNC → el juego Swing)
       ├─ php-fpm 8.5   (Laravel 13 + Filament v5)
       └─ Xtigervnc :1 + websockify/noVNC :6080 + openbox + java -jar (el juego)

Servicio aparte:  MariaDB 11   (Dokploy Database / servicio `mariadb` en compose)
```

| Capa | Tecnología | Ruta servida |
|------|------------|--------------|
| Presentación | Vue 3 (estáticos) | `/`, `/leaderboard`, … |
| Backend leaderboard | Laravel 13 + Filament v5 | `/api`, `/admin` |
| Juego | Java 17 Swing vía noVNC | `/game/` |
| Datos | MariaDB 11 | (servicio externo) |

El juego envía cada puntaje a `LEADERBOARD_API_URL` (`http://127.0.0.1/api` dentro del
contenedor → nginx → Laravel), y la SPA + Filament lo leen. Mismo origen ⇒ sin CORS.

## Stages del Dockerfile

1. **web-builder** (`node:24.16.0-bookworm-slim`) → `pnpm build` → `web/dist`.
2. **php-deps** (`composer:2`) → `composer install --no-dev` → `vendor/` de Laravel.
3. **java-builder** (`maven:3.9-eclipse-temurin-17`) → `mvn -DskipTests package` → fat jar (shade plugin con Batik/Xerces/jsoup dentro).
4. **runtime** (`ubuntu:24.04`) → PHP 8.5 (PPA ondrej) + JRE 17 + nginx + supervisor + TigerVNC/noVNC; copia los tres artefactos y los configs de `.docker/`.

## Desarrollo local

Requiere Docker. Todo se orquesta desde el `Makefile` central (`make` muestra la ayuda).

```bash
cp .docker/env.reference .env     # opcional: ajustar credenciales/dominio
make up                           # build + monolito + MariaDB 11 (compose)
make logs                         # seguir los logs del contenedor
make down                         # detener
```

Luego abrir:

- `http://localhost/` — landing y presentación (Vue).
- `http://localhost/leaderboard` — ranking en vivo.
- `http://localhost/admin` — panel Filament (credenciales `FILAMENT_ADMIN_*`).
- `http://localhost/game/vnc.html?path=game/websockify&autoconnect=true&resize=remote` — el juego (noVNC abierto, sin contraseña).

Para iterar cada capa por separado, sin Docker:

```bash
make api-install && make api-migrate && make api-serve   # Laravel en :8000
make web-install && make web-dev                          # Vite (proxy /api → :8000)
make java-run                                             # el juego en el escritorio
```

## Despliegue en Dokploy

1. Crear un **Project** en Dokploy.
2. Añadir **Database → MariaDB 11**. Anotar host interno, base, usuario y contraseña.
3. Añadir **Application** apuntando al repo y rama, tipo **Dockerfile** (`Dockerfile` de la raíz).
4. Configurar las **env vars** (ver `.docker/env.reference`):
   - `APP_KEY` (genera con `cd api && php artisan key:generate --show`), `APP_URL=https://<dominio>`.
   - `DB_CONNECTION=mariadb`, `DB_HOST=<host MariaDB de Dokploy>`, `DB_DATABASE`, `DB_USERNAME`, `DB_PASSWORD`.
   - `LEADERBOARD_API_URL=http://127.0.0.1/api`, `VNC_RESOLUTION` (opcional).
   - `FILAMENT_ADMIN_NAME`, `FILAMENT_ADMIN_EMAIL`, `FILAMENT_ADMIN_PASSWORD`.
5. Asignar el **dominio** a la app (puerto **80**). Traefik inyecta el TLS (Let's Encrypt).
6. **Deploy.** El `entrypoint.sh` genera APP_KEY si falta, espera la BD, migra, siembra el
   admin de Filament, cachea config/rutas/vistas y arranca supervisord.

## Verificación end-to-end

1. `https://<dominio>/` carga la landing Neon Trails.
2. `/admin` permite iniciar sesión y el recurso *Leaderboard* lista/crea/edita puntajes.
3. `/game/…` muestra el juego jugable (noVNC).
4. Jugar hasta *game over* → el puntaje aparece en `GET /api/scores`, en `/leaderboard` y en Filament.

## Notas / puntos a verificar en producción

- **PHP 8.5** se instala desde `ppa:ondrej/php`; si una patch no estuviera disponible, fijar la última `php8.5.*`.
- **noVNC bajo `/game/`**: el WebSocket usa `?path=game/websockify`; si el cliente no conecta, revisar ese parámetro y los headers `Upgrade`/`Connection` del `location /game/`.
- **Tamaño de imagen** ~1.3–1.6 GB (JRE + PHP + VNC/escritorio): esperado por la opción de juego-en-navegador.
- **Audio**: bajo VNC no hay tarjeta de sonido; `SoundManager` degrada a no-op silencioso (no rompe el juego).
```
