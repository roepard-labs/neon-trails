# TaskManager — Sitio de exposición (estático)

Contenido en HTML/CSS/JS sin paso de build. Sirve para una presentación tipo diapositivas (5 páginas).

El contenido pedagógico está alineado con la guía **«Requerimientos y casos de uso»** (niveles de requerimiento, RF/RNF, elementos UML del diagrama de casos de uso, relaciones «include», plantilla de descripción con flujo actor–sistema y excepciones).

## Archivos

| Página            | Archivo             |
|-------------------|---------------------|
| Portada           | `index.html`        |
| Arquitectura      | `arquitectura.html` |
| Casos de uso      | `casos-uso.html`    |
| Mockups           | `mockups.html`      |
| Descripciones     | `descripciones.html`|

Recursos: `assets/styles.css`, `assets/app.js`, `assets/mermaid-init.js`.

## Dokploy (mismo contenedor que el front Vue + nginx)

Tras el último build, la imagen copia `web/` a `/usr/share/nginx/html/web/` y nginx sirve ese prefijo.

**Dominio en Dokploy (recomendado, coherente con tu captura):**

| Campo | Valor |
|--------|--------|
| **Host** | `taskmanager.jemg.dev` (o el tuyo) |
| **Path** | `/web` |
| **Internal Path** | `/web` |
| **Strip Path** | **OFF** (el contenedor debe recibir `/web/...` tal cual) |
| **Container Port** | `80` |

Entrada útil: `https://taskmanager.jemg.dev/web/` o `.../web/index.html`.

Si usaras **Strip Path ON**, el proxy enviaría al contenedor rutas sin el prefijo `/web` y habría que cambiar nginx (no es el caso con la config actual).

## Dokploy / solo sitio estático

1. Alternativa: publicar solo esta carpeta como raíz del sitio.
2. Las rutas relativas (`assets/...`) funcionan bajo el mismo prefijo público.
3. Los diagramas (**Mermaid**) se cargan en `arquitectura.html` y `casos-uso.html` vía CDN (`cdn.jsdelivr.net`).

## Atajos

- Flechas **←** **→** (o RePág / AvPág): diapositiva anterior / siguiente.
- Botón sol/luna: tema claro u oscuro (persistente en `localStorage`).
